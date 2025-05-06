import java.io.*;

public class Fitxer{
    private String nom;
    private byte[] contingut;

    public Fitxer(String nom) {
        this.nom = nom;
        System.out.println("Nom del fitxer rebut: "+nom);
    }
    
    public byte[] getContingut() throws IOException {
        File fitxer = new File(nom);
        if (!fitxer.exists()) {
            throw new FileNotFoundException("El fitxer no existeix: " + nom);
        }
        FileInputStream fis = new FileInputStream(fitxer);
        contingut = fis.readAllBytes();
        fis.close();
        return contingut;
    }
}