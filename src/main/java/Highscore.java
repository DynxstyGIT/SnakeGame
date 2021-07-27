<<<<<<< HEAD
import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.SecureRandom;

public class Highscore {

    public static void write(int i){
        try {

            KeyGenerator kg = KeyGenerator.getInstance("DES");
            kg.init(new SecureRandom());
            SecretKey key = kg.generateKey();
            SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
            Class spec = Class.forName("javax.crypto.spec.DESKeySpec");
            DESKeySpec ks = (DESKeySpec) skf.getKeySpec(key, spec);
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("keyfile"));
            oos.writeObject(ks.getKey());

            Cipher c = Cipher.getInstance("DES/CFB8/NoPadding");
            c.init(Cipher.ENCRYPT_MODE, key);
            CipherOutputStream cos = new CipherOutputStream(new FileOutputStream("ciphertext"), c);
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(cos));
            pw.println(i);
            pw.flush();
            pw.close();
            oos.writeObject(c.getIV());
            oos.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static String read(){
        try {

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("keyfile"));
        DESKeySpec ks = new DESKeySpec((byte[]) ois.readObject());
        SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
        SecretKey key = skf.generateSecret(ks);

        Cipher c = Cipher.getInstance("DES/CFB8/NoPadding");
        c.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec((byte[]) ois.readObject()));
        CipherInputStream cis = new CipherInputStream(new FileInputStream("ciphertext"), c);
        BufferedReader br = new BufferedReader(new InputStreamReader(cis));
        return br.readLine();
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }
}
=======
import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.SecureRandom;

public class Highscore {

    public static void write(int i){
        try {

            KeyGenerator kg = KeyGenerator.getInstance("DES");
            kg.init(new SecureRandom());
            SecretKey key = kg.generateKey();
            SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
            Class spec = Class.forName("javax.crypto.spec.DESKeySpec");
            DESKeySpec ks = (DESKeySpec) skf.getKeySpec(key, spec);
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("keyfile"));
            oos.writeObject(ks.getKey());

            Cipher c = Cipher.getInstance("DES/CFB8/NoPadding");
            c.init(Cipher.ENCRYPT_MODE, key);
            CipherOutputStream cos = new CipherOutputStream(new FileOutputStream("ciphertext"), c);
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(cos));
            pw.println(i);
            pw.flush();
            pw.close();
            oos.writeObject(c.getIV());
            oos.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static String read(){
        try {

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("keyfile"));
        DESKeySpec ks = new DESKeySpec((byte[]) ois.readObject());
        SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
        SecretKey key = skf.generateSecret(ks);

        Cipher c = Cipher.getInstance("DES/CFB8/NoPadding");
        c.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec((byte[]) ois.readObject()));
        CipherInputStream cis = new CipherInputStream(new FileInputStream("ciphertext"), c);
        BufferedReader br = new BufferedReader(new InputStreamReader(cis));
        return br.readLine();
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }
}
>>>>>>> aad3a18 (initial commit)
