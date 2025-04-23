

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
            while (!(missatge = (String) in.readObject()).equals(MSG_SORTIR)) {
                System.out.println( ": " + missatge);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error al fil del servidor: " + e.getMessage());
        }
    }
}
