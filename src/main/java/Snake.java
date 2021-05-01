import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.concurrent.TimeUnit;

public class Snake extends JFrame {

    public Snake() throws InterruptedException {

        File f = new File("ciphertext");
        if (!f.exists()) {
            Highscore.write(0);

            TimeUnit.SECONDS.sleep(3);

            initUI();
        } else { initUI(); }
    }

    private void initUI() {

        try {
            add(new Game());
        } catch (Exception e) { e.printStackTrace(); }

        setResizable(false);
        pack();

        setTitle("Snake");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ImageIcon imageIcon = new ImageIcon("src/resources/images/snakegameicon.png");
        setIconImage(imageIcon.getImage());
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            JFrame ex = null;
            try { ex = new Snake(); }
            catch (InterruptedException e) { e.printStackTrace(); }

            ex.setVisible(true);
        });
    }
}