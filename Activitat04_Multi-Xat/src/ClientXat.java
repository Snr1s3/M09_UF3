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
            System.out.println("Client connectat a " +"localhost:" +8888);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            System.out.println("Error al crear el socket del servidor: " + e.getMessage());
        }
    }

    public void enviarMissatge(Object missatge) {
    }

    public void tanca() {
        try {
            if (socket != null) socket.close();
            if (out != null) out.close();
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
                if (missatgeCru instanceof Missatge) {
                    Missatge missatge = (Missatge) missatgeCru;
                    String missatgeRaw = missatge.toString(); 
                    String codi = Missatge.getCodiMissatge(missatgeRaw); 
                    String[] parts = Missatge.getPartsMissatge(missatgeRaw); 

                    switch (codi) {
                        case Missatge.CODI_SORTIR_TOTS:
                            sortir = true;
                            System.out.println("El servidor ha tancat el xat per a tots.");
                            break;
                        case Missatge.CODI_MSG_PERSONAL:
                            System.out.println("[Privat] " + parts[0] + ": " + parts[1]);
                            break;
                        case Missatge.CODI_MSG_GRUP:
                            System.out.println("[Grup] " + parts[0]);
                            break;
                        default:
                            System.out.println("Codi de missatge desconegut: " + codi);
                    }
                } else {
                    System.out.println("Error: Objecte rebut no és un Missatge.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error en la recepció de missatges: " + e.getMessage());
        } finally {
            tanca();
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
        System.out.println("help - Mostrar aquesta ajuda");
    }

    public String getLinea(Scanner scanner, String missatge, boolean obligatori) {
        return "";
    }

    public static void main(String[] args) {
        ClientXat client = new ClientXat();
        client.connecta();
    }
}
