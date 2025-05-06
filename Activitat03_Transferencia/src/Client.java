import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    private static Socket socket;
    private static String DIR_ARRIBADA = "./tmp";
    private static OutputStream out;
    private BufferedReader in;

    public void connecta() {
        try {
            socket = new Socket(HOST, PORT);
            System.out.println("Client connectat a " + HOST + ":" + PORT);
            out = socket.getOutputStream();
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e) {
            System.out.println("Error al crear el socket del servidor: " + e.getMessage());
        }
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

    public boolean rebreFitxers() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Introdueix la ruta completa del fitxer a rebre: ");
            String nomFitxer = scanner.nextLine();
            out.write((nomFitxer + "\n").getBytes());
            out.flush();
            if (nomFitxer.equalsIgnoreCase("sortir") || nomFitxer.isBlank()) {
                return false;
            }
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            int midaFitxer = dis.readInt();
            if (midaFitxer <= 0) {
                System.out.println("No s'ha rebut cap fitxer o la mida no és vàlida.");
                return false;
            }
            byte[] contingut = new byte[midaFitxer];
            int bytesLlegits = 0;
            while (bytesLlegits < midaFitxer) {
                int llegits = dis.read(contingut, bytesLlegits, midaFitxer - bytesLlegits);
                if (llegits == -1) break; 
                bytesLlegits += llegits;
            }
            FileOutputStream fos = new FileOutputStream(DIR_ARRIBADA + "/fitxer.rebut");
            fos.write(contingut);
            fos.close();
            System.out.println("Fitxer rebut i desat com: " + DIR_ARRIBADA + "/fitxer.rebut");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public static void main(String[] args) {
        Client client = new Client();
        client.connecta();
        try {
            boolean enviar = true;
            while(enviar){
                enviar = client.rebreFitxers();
            }
        } catch (Exception e) {
            System.out.println("Error al llegir l'entrada de l'usuari: " + e.getMessage());
        }
        client.tanca();
    }
}
