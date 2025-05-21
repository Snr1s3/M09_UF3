import java.io.*;
import java.net.*;
import java.util.Hashtable;

public class ServidorXat {
    public static final int PORT = 9999;
    public static final String HOST = "localhost";
    public static final String MSG_SORTIR = "sortir";
    private Hashtable<String, GestorClients> clients = new Hashtable<>();
    private boolean sortir = false;
    private ServerSocket serverSocket;

    public void servidorAEscoltar() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor iniciat a " + HOST + ":" + PORT);
            while (!sortir) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connectat: " + clientSocket.getInetAddress());
                GestorClients gestor = new GestorClients(clientSocket, this);
                new Thread(gestor).start();
            }
        } catch (IOException e) {
            System.out.println("Error al escoltar: " + e.getMessage());
        }
    }

    public void pararServidor() {
        try {
            sortir = true;
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            System.out.println("Servidor aturat.");
        } catch (IOException e) {
            System.out.println("Error al parar el servidor: " + e.getMessage());
        }
    }

    public void finalitzarXat() {
        System.out.println("Tancant tots els clients.");
        enviarMissatgeGrup(MSG_SORTIR);
        System.out.println("DEBUG: multicast sortir");
        clients.clear();
        System.exit(0);
    }

    public synchronized void afegirClient(GestorClients gestor) {
        String nom = gestor.getNom();
        if (nom != null && !nom.isBlank()) {
            clients.put(nom, gestor);
            System.out.println(nom + " connectat.");
            System.out.println("DEBUG: multicast Entra: " + nom);
        }
    }

    public synchronized void eliminarClient(GestorClients gestor) {
        String nom = gestor.getNom();
        if (nom != null && clients.containsKey(nom)) {
            clients.remove(nom);
            enviarMissatgeGrup(Missatge.getMissatgeGrup(nom + " ha sortit del xat."));
        }
    }

    public synchronized void enviarMissatgeGrup(String missatge) {
        for (GestorClients gestor : clients.values()) {
            gestor.enviarMissatge("Servidor", missatge);
        }
    }

    public synchronized void enviarMissatgePersonal(String destinatari, String remitent, String missatge) {
        System.out.println("Missatge personal per (" + destinatari + ") de (" + remitent + "): " + missatge);
        GestorClients gestor = clients.get(destinatari);
        if (gestor != null) {
            gestor.enviarMissatge(remitent, Missatge.getMissatgePersonal(destinatari, missatge));
        }
    }

    public static void main(String[] args) {
        ServidorXat servidor = new ServidorXat();
        servidor.servidorAEscoltar();
        servidor.pararServidor();
    }
}