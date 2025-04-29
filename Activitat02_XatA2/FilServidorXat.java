

import java.io.BufferedReader;
import java.io.IOException;

public class FilServidorXat extends Thread {
    private BufferedReader in;
    private String MSG_SORTIR;
    public FilServidorXat(BufferedReader in, String msg) {
        this.MSG_SORTIR = msg;
        this.in = in;
    }
    
    @Override
    public void run() {
        try {
            String missatge;
            while ((missatge = in.readLine()) != null) {
                if (missatge.equalsIgnoreCase(MSG_SORTIR)) {
                    System.out.println("Fil xat finalitzat.");
                    break;
                }
                System.out.println("Missatge (\'sortir\' per tancar): Rebut: " + missatge);
            }
        } catch (IOException e) {
            System.out.println("Error al fil del servidor: " + e.getMessage());
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                System.out.println("Error al tancar el flux d'entrada: " + e.getMessage());
            }
        }
    }
}
