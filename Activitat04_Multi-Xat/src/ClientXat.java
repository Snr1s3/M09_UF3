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
            socket = new Socket("localhost", 9999);
            System.out.println("Client connectat a localhost:9999");
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
        System.out.println("Tancant client...");
        try {
            if (in != null) {
                in.close();
                System.out.println("Flux d'entrada tancat.");
            }
            if (out != null) {
                out.close();
                System.out.println("Flux de sortida tancat.");
            }
            if (socket != null) socket.close();
        } catch (Exception e) {
            System.out.println("Error tancant recursos: " + e.getMessage());
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
                            if (parts.length <= 3){
                                System.err.println(missatge);
                                System.out.println("[Privat] " + parts[1] + ": " + parts[2]);
                            }
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
        System.out.println("1.- Connectar al servidor (primer pas obligatori)");
        System.out.println("2.- Enviar missatge personal");
        System.out.println("3.- Enviar missatge al grup");
        System.out.println("4.- (o línia en blanc)-> Sortir del client");
        System.out.println("5.- Finalitzar tothom");
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
        System.out.println("Flux d'entrada i sortida creat.");
        System.out.println("-------------------------------");
        System.out.println("DEBUG: Iniciant rebuda de missatges...");
        new Thread(client::execucio).start();

        Scanner scanner = new Scanner(System.in);

        while (!client.sortir) {
             client.ajuda();
            System.out.println("-------------------------------");
            String opcio = client.getLinea(scanner, "", false);
            if (opcio.isBlank()) {
                client.sortir = true;
                break;
            }
            switch (opcio) {
                case "1":
                    String nom = client.getLinea(scanner, "Introdueix el nom: ", true);
                    String missatgeConnexio = Missatge.getMissatgeConectar(nom);
                    System.out.println("Enviant missatge: " + missatgeConnexio);
                    client.enviarMissatge(missatgeConnexio);
                    break;
                case "2":
                    String destinatari = client.getLinea(scanner, "Destinatari: ", true);
                    String missatgePersonal = client.getLinea(scanner, "Missatge a enviar: ", true);
                    String missatgePers = Missatge.getMissatgePersonal(destinatari, missatgePersonal);
                    System.out.println("Enviant missatge: " + missatgePers);
                    client.enviarMissatge(missatgePers);
                    break;
                case "3":
                    String missatgeGrup = client.getLinea(scanner, "Missatge a enviar al grup: ", true);
                    String missatgeGrupRaw = Missatge.getMissatgeGrup(missatgeGrup);
                    System.out.println("Enviant missatge: " + missatgeGrupRaw);
                    client.enviarMissatge(missatgeGrupRaw);
                    break;
                case "4":
                    String missatgeRaw = Missatge.getMissatgeSortirClient("Adéu");
                    System.out.println("Enviant missatge: " + missatgeRaw);
                    client.enviarMissatge(missatgeRaw);
                    client.sortir = true;
                    break;
                case "5":
                    String missatgeRawTots = Missatge.getMissatgeSortirTots("Adéu");
                    System.out.println("Enviant missatge: " + missatgeRawTots);
                    client.enviarMissatge(missatgeRawTots);
                    client.sortir = true;
                    break;
                default:
                    System.out.println("Opció no vàlida.");
            }
        }
        client.tancarClient();
    }
}