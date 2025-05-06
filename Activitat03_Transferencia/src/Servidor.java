import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor{
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    private static ServerSocket serverSocket;
    private static Socket socket;

    public  void iniciarServidor(){
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Acceptant connexions en -> " + HOST + ":" + PORT);
            System.out.println("Esperant connexio...");
            socket = serverSocket.accept();
            
        } catch (IOException e) {
            System.out.println("Error al crear el socket del servidor: " + e.getMessage());
        }
    }
    public  void  pararServidor(){
        try{
            serverSocket.close();
            socket.close();
            System.out.println("Servidor aturat.");
        }
        catch(IOException e){
            System.out.println("Error al tancar la connexió: " + e.getMessage());
        }
    }

    public boolean enviarFitxers(){
        try {
            System.out.println("Esperant el nom del fitxer del client...");
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String nom = in.readLine();
            if (nom.equalsIgnoreCase("sortir") || nom.isBlank()) {
                System.out.println("Sortint");
                return false;
            }
            Fitxer fitxer = new Fitxer(nom);
            byte[] contingut = fitxer.getContingut();
            System.out.println("Enviant fitxer: " + nom);
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeInt(contingut.length);    
            dos.write(contingut);              
            dos.flush();
            System.out.println("Fitxer enviat correctament.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
        return true;
    }
    public static void main(String[] args) {
        Servidor srv = new Servidor();
        srv.iniciarServidor();
        try {
            System.out.println("Client connectat: /127.0.0.1");
            boolean enviar = true;
            while(enviar){
                enviar = srv.enviarFitxers();
            }
        } catch (Exception e) {
            System.out.println("Error al servidor: " + e.getMessage());
        } finally {
            srv.pararServidor();
        }
    }
}