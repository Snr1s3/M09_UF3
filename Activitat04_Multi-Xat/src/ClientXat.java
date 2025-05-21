import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientXat {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private volatile boolean sortir = false;

    public void connecta() {
        try {
            socket = new Socket("localhost", 8888);
            System.out.println("Client connectat a localhost:8888");
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            System.out.println("Error al crear el socket del servidor: " + e.getMessage());
        }
    }

    public void enviarMissatge(String missatge) {
        try {
            out.writeObject(missatge);
            out.flush();
        } catch (Exception e) {
            System.out.println("Error enviant missatge: " + e.getMessage());
        }
    }

    public void tancarClient() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
            System.out.println("Sortint...");
            System.out.println("\nConexio tancada");
        } catch (Exception e) {
            System.out.println("Error al tancar la connexió: " + e.getMessage());
        }
    }

    public void execucio() {
        try {
            while (!sortir) {
                Object missatgeCru = in.readObject();
                if (missatgeCru instanceof String) {
                    String missatge = (String) missatgeCru;
                    String codi = Missatge.getCodiMissatge(missatge);
                    String[] parts = Missatge.getPartsMissatge(missatge);
                    
                    if (codi == null) {
                        System.out.println("Missatge rebut amb format incorrecte: " + missatge);
                        continue;
                    }

                    switch (codi) {
                        case Missatge.CODI_SORTIR_TOTS:
                            sortir = true;
                            System.out.println("El servidor ha tancat el xat per a tots.");
                            break;
                        case Missatge.CODI_MSG_PERSONAL:
                            if (parts.length <= 3)
                                System.out.println("[Privat] " + parts[1] + ": " + parts[2]);
                            else
                                System.out.println("Missatge personal mal format: " + missatge);
                            break;
                        case Missatge.CODI_MSG_GRUP:
                            if (parts.length >= 1)
                                System.out.println("[Grup] " + parts[1]);
                            else
                                System.out.println("Missatge de grup mal format: " + missatge);
                            break;
                        default:
                            System.out.println("Codi de missatge desconegut: " + codi);
                    }
                } else {
                    System.out.println("Error: Objecte rebut no és un String.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error en la recepció de missatges: " + e.getMessage());
        } finally {
            tancarClient();
        }
    }

    public void ajuda() {
        System.out.println("Comandes disponibles:");
        System.out.println("1 - Enviar nom d'usuari");
        System.out.println("2 - Missatge personal (necessita destinatari i missatge)");
        System.out.println("3 - Missatge al grup (necessita missatge)");
        System.out.println("4 - Sortir del xat (només aquest client)");
        System.out.println("5 - Sortir del xat per a tots els clients");
        System.out.println("Enter buit - Sortir del client");
    }

    public String getLinea(Scanner scanner, String missatge, boolean obligatori) {
        String linia;
        do {
            System.out.print(missatge);
            linia = scanner.nextLine();
            if (!obligatori) break;
        } while (linia.isBlank());
        return linia;
    }

    public static void main(String[] args) {
        ClientXat client = new ClientXat();
        client.connecta();
        new Thread(client::execucio).start();
        client.ajuda();
        Scanner scanner = new Scanner(System.in);

        while (!client.sortir) {
            String opcio = client.getLinea(scanner, "Opció: ", false);
            if (opcio.isBlank()) {
                client.sortir = true;
                break;
            }
            switch (opcio) {
                case "1":
                    String nom = client.getLinea(scanner, "Introdueix el teu nom: ", true);
                    if (!nom.isBlank()) {
                        client.enviarMissatge(Missatge.getMissatgeConectar(nom));
                    } else {
                        System.out.println("El nom no pot estar buit.");
                    }
                    break;
                case "2":
                    String destinatari = client.getLinea(scanner, "Introdueix el destinatari: ", true);
                    String missatgePersonal = client.getLinea(scanner, "Introdueix el missatge: ", true);
                    if (!destinatari.isBlank() && !missatgePersonal.isBlank()) {
                        client.enviarMissatge(Missatge.getMissatgePersonal(destinatari, missatgePersonal));
                    } else {
                        System.out.println("El destinatari i el missatge no poden estar buits.");
                    }
                    break;
                case "3":
                    String missatgeGrup = client.getLinea(scanner, "Introdueix el missatge al grup: ", true);
                    if (!missatgeGrup.isBlank()) {
                        client.enviarMissatge(Missatge.getMissatgeGrup(missatgeGrup));
                    } else {
                        System.out.println("El missatge no pot estar buit.");
                    }
                    break;
                case "4":
                    client.enviarMissatge(Missatge.getMissatgeSortirClient("sortir"));
                    client.sortir = true;
                    break;
                case "5":
                    client.enviarMissatge(Missatge.getMissatgeSortirTots("sortir"));
                    client.sortir = true;
                    break;
                case "help":
                    client.ajuda();
                    break;
                default:
                    System.out.println("Opció no vàlida.");
            }
        }
        client.tancarClient();
    }
}