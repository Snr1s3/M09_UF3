import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.PrintWriter;
public class Client{
    private static final int PORT = 7777;
    private static final String HOST = "localhost";
    private static Socket socket;
    private static PrintWriter out;

    public void connecta(){
        try{
            socket = new Socket(HOST, PORT);
            System.out.println("Connectat a servidor en" + HOST + ":" + PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
        }
        catch(IOException e){
            System.out.println("Error al crear el socket del servidor: " + e.getMessage());
        }
    }
    public void envia(String missatge){
        out.println(missatge);
        System.out.println("Enviant al servidor: " + missatge);
    }
    public void tanca(){
        try{
            out.close();
            socket.close();
            System.out.println("\n\nClient tancat");
        }
        catch(IOException e){
            System.out.println("Error al tancar la connexió: " + e.getMessage());
        }
    }
    public static void main(String[] args) {
        Client client = new Client();
        client.connecta();
        client.envia("Prova d'enviament 1");
        client.envia("Prova d'enviament 2");
        client.envia("Adéu!");
        System.out.println("Prem enter per tancar el client...");
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
            bf.readLine();
            bf.close();
        } catch (IOException e) {
            System.out.println("Error al llegir la resposta del servidor: " + e.getMessage());
        }
        client.tanca();
    }
}