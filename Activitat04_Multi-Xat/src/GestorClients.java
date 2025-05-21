import java.io.*;
import java.net.Socket;

public class GestorClients implements Runnable {
    private Socket client;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ServidorXat servidor;
    private String nom;
    private boolean sortir = false;

    public GestorClients(Socket client, ServidorXat servidor) {
        try {
            this.client = client;
            this.servidor = servidor;
            this.out = new ObjectOutputStream(client.getOutputStream());
            this.in = new ObjectInputStream(client.getInputStream());
        } catch (IOException e) {
            System.out.println("Error inicialitzant GestorClients: " + e.getMessage());
        }
    }

    public String getNom() {
        return nom;
    }

    @Override
    public void run() {
        try {
            while (!sortir) {
                Object missatgeCru = in.readObject();
                if (missatgeCru instanceof String) {
                    processaMissatge((String) missatgeCru);
                } else {
                    System.out.println("Error: Missatge rebut no és un String.");
                }
            }
        } catch (EOFException eof) {
            System.out.println("El client " + nom + " s'ha desconnectat.");
        } catch (Exception e) {
            System.out.println("Error en el gestor de client: " + e.getMessage());
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                System.out.println("Error tancant el socket: " + e.getMessage());
            }
        }
    }

    public void enviarMissatge(String remitent, String missatge) {
        try {
            out.writeObject(missatge); 
            out.flush();
        } catch (IOException e) {
            System.out.println("Error enviant missatge a " + remitent + ": " + e.getMessage());
        }
    }

    public void processaMissatge(String missatgeRaw) {
        System.out.println("Missatge rebut pel servidor: [" + missatgeRaw + "]");
        if (missatgeRaw == null || missatgeRaw.isBlank()) {
            System.out.println("Missatge rebut és null o buit!");
            return;
        }
        String codi = Missatge.getCodiMissatge(missatgeRaw);
        if (codi == null) {
            System.out.println("Missatge rebut amb format incorrecte: " + missatgeRaw);
            return;
        }
    
        switch (codi) {
            case Missatge.CODI_CONECTAR:
                String[] partsConnectar = Missatge.getPartsMissatge(missatgeRaw);
                if (partsConnectar.length <= 0) {
                    System.out.println("Missatge de connexió mal format: " + missatgeRaw);
                    break;
                }
                this.nom = partsConnectar[1];
                servidor.afegirClient(this);
                break;
            case Missatge.CODI_SORTIR_CLIENT:
                sortir = true;
                servidor.eliminarClient(this);
                break;
            case Missatge.CODI_SORTIR_TOTS:
                sortir = true;
                servidor.finalitzarXat();
                break;
            case Missatge.CODI_MSG_GRUP:
                servidor.enviarMissatgeGrup(missatgeRaw);
                break;
            case Missatge.CODI_MSG_PERSONAL:
                String[] parts = Missatge.getPartsMissatge(missatgeRaw);
                if (parts.length < 3) {
                    System.out.println("Missatge personal mal format: " + missatgeRaw);
                    break;
                }
                String destinatari = parts[1];
                String text = parts[2];
                servidor.enviarMissatgePersonal(destinatari, this.nom, text);
                break;
            default:
                System.out.println("Codi de missatge desconegut: " + codi + " per missatge: " + missatgeRaw);
        }
    }
}