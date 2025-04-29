import java.io.BufferedReader;
import java.io.IOException;

public class FilLectorCX extends Thread {
    private BufferedReader in;

    public FilLectorCX(BufferedReader in) {
        this.in = in;
    }

    @Override
    public void run() {
        try {
            String missatge;
            while ((missatge = in.readLine()) != null) {
                System.out.println("Rebut: " + missatge);
            }
        } catch (IOException e) {
            System.out.println("Error al llegir del servidor: " + e.getMessage());
        } finally {
            try {
                if (in != null) in.close();
            } catch (IOException e) {
                System.out.println("Error al tancar el flux d'entrada: " + e.getMessage());
            }
        }
    }
}