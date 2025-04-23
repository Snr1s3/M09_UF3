

import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServidorXat{
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    private static ServerSocket serverSocket;
    private static Socket socket;
    private static String MSG_SORTIR = "sortir";
    private static FilServidorXat fsx;

    public static void iniciarServidor(){
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor en marxa a " + HOST + ":" + PORT);
            System.out.println("Esperant connexions a " + HOST + ":" + PORT);
            socket = serverSocket.accept();
            System.out.println("Client connectat: /127.0.0.1");
        } catch (IOException e) {
            System.out.println("Error al crear el socket del servidor: " + e.getMessage());
        }
    }
    public static void getNom() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            String nomClient =  in.readLine();
            System.out.println("Nom del client: " + nomClient);

            FilServidorXat fil = new FilServidorXat(in, nomClient);
            fil.start();
        } catch (IOException e) {
            System.out.println("Error al acceptar la connexió: " + e.getMessage());
        }
    }
    public static void  pararServidor(){
        try{
            serverSocket.close();
            socket.close();
            System.out.println("Servidor tancat.");
        }
        catch(IOException e){
            System.out.println("Error al tancar la connexió: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        iniciarServidor();
                getNom();
            pararServidor(); 
    }
}