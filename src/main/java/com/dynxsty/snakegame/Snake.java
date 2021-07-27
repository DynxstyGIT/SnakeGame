package com.dynxsty.snakegame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.concurrent.TimeUnit;


public class Snake extends JFrame {

    public static boolean sound = true;
    public static JCheckBoxMenuItem i1;

    public Snake() throws InterruptedException {

        File f = new File("ciphertext");
        if (!f.exists()) {
            Highscore.write(0);

            TimeUnit.SECONDS.sleep(3);

            initUI();
        } else { initUI(); }
    }

    private void initUI() {

        try { add(new Game()); }
        catch (Exception e) { e.printStackTrace(); }

        setResizable(false);
        pack();

        setTitle("Snake");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            setIconImage(ImageIO.read(getClass().getClassLoader().getResourceAsStream("images/snakegameicon.png")));
        } catch (Exception ignored) {}

            JMenuBar mb = new JMenuBar();
            JMenu menu = new JMenu("Options");
            i1 = new JCheckBoxMenuItem("Sound", sound);
            menu.add(i1);
            mb.add(menu);
            setJMenuBar(mb);
            setVisible(true);
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