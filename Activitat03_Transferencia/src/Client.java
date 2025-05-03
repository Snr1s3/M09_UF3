import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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

    public void envia(String missatge) {
        try {
            out.write((missatge + "\n").getBytes());
            out.flush();
            System.out.println("Enviant missatge: " + missatge);
        } catch (IOException e) {
            System.out.println("Error al enviar el missatge: " + e.getMessage());
        }
    }
     public boolean rebreFitxers() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Introdueix la ruta completa del fitxer a rebre: ");
            String nomFitxer = scanner.nextLine();
            out.write((nomFitxer+"\n").getBytes());
            out.flush();
            System.out.println("Enviant missatge: " + nomFitxer);
            File file = new File(nomFitxer);
            if(nomFitxer.equalsIgnoreCase("sortir") || nomFitxer.isBlank() || !file.exists()){
                return false;
            }
            System.out.println("Enviant missatge: " + nomFitxer);
            InputStream is = socket.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesLlegits;
            while ((bytesLlegits = is.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesLlegits);
            }
            byte[] contingutFitxer = baos.toByteArray();
            FileOutputStream fos = new FileOutputStream(DIR_ARRIBADA+"/fitxer_rebut");
            System.out.println("Fitxer rebut i guardat com: "+DIR_ARRIBADA+"/fitxer_rebut");
            fos.write(contingutFitxer);
            fos.close();

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
