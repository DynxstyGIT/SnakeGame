import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;

public class Game extends JPanel implements ActionListener {

    private final int B_WIDTH = 600;
    private final int B_HEIGHT = 650;
    private final int DOT_SIZE = 40;
    private final int ALL_DOTS = 225;
    private final int RAND_POS = 10;
    private int DELAY = 320;

    private final int x[] = new int[ALL_DOTS];
    private final int y[] = new int[ALL_DOTS];

    private int bodyParts, pointX, pointY, score, level;
    private String highscore;

    private boolean rightDirection = true;
    private boolean leftDirection = false;
    private boolean upDirection = false;
    private boolean downDirection = false;
    private boolean inGame = true;
    private boolean paused = false;

    private Timer timer;
    private Image body, point, head;

    public Game() { initBoard(); }

    private void initBoard() {

        addKeyListener(new TAdapter());
        setBackground(Color.black);
        setFocusable(true);

        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        loadSprites();
        initGame();
    }

    private void loadSprites() {

        try {

            body = ImageIO.read(getClass().getClassLoader().getResourceAsStream("images/body.png"));
            point = ImageIO.read(getClass().getClassLoader().getResourceAsStream("images/point.png"));
            head = ImageIO.read(getClass().getClassLoader().getResourceAsStream("images/head.png"));

        } catch (Exception ignored) {}

    }

    private void initGame() {

        bodyParts = 1;
        level = 1;
        highscore = Highscore.read();

        for (int i = 0; i < bodyParts; i++) {
            x[i] = 200 - i * DOT_SIZE;
            y[i] = 250;
        }

        spawnPoint();

        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);

    }

    private void draw(Graphics g) {

        renderBackground(g);
        String msgScore = "HI " + highscore +  "   " + "Score " + score + "   " + "Level " + level;

        g.drawString(msgScore, (B_WIDTH - fontMetrics(g, 14f, Color.WHITE).stringWidth(msgScore)) / 2, 30);

        if (inGame) {
            g.drawImage(point, pointX, pointY, this);

            for (int z = 0; z < bodyParts; z++) {
                if (z == 0) {
                    g.drawImage(head, x[z], y[z], this);
                } else {
                    g.drawImage(body, x[z], y[z], this);
                }
            }

        if (paused) pauseGame(g);

            Toolkit.getDefaultToolkit().sync();

        } else { gameOver(g); }
    }

    private void gameOver(Graphics g) {

        String msgRestart = "Press SPACE to restart";
        String msgHighscore = "New Highscore!";
        String msgGameOver = "Game Over";

        g.drawString(msgGameOver, (B_WIDTH - fontMetrics(g, 60f, new Color(0xff5e5e)).stringWidth(msgGameOver)) / 2, 120);
        g.drawString(msgRestart, (B_WIDTH - fontMetrics(g, 20f, Color.WHITE).stringWidth(msgRestart)) / 2, 550);
        playSound("explosion.wav");

        if (Integer.parseInt(highscore) <= score) {
            g.drawString(msgHighscore, (B_WIDTH - fontMetrics(g, 30f, Color.WHITE).stringWidth(msgHighscore)) / 2, B_HEIGHT / 2);
            Highscore.write(score);
        }
    }

    private void checkPoint() {
        if ((y[0] == pointY) && (x[0] == pointX)) {

            score++;
            bodyParts++;

            if ((level * 5) / score == 0 ) {

                level++;
                DELAY -= 20;
                playSound("levelup.wav");

            } else { playSound("collect.wav"); }

            timer.stop();

            timer = new Timer(DELAY, this);
            timer.start();
            spawnPoint();
        }
    }

    private void move() {

        if (!paused) {
            for (int z = bodyParts; z > 0; z--) {
                x[z] = x[(z - 1)];
                y[z] = y[(z - 1)];
            }

            if (leftDirection) { x[0] -= DOT_SIZE; }
            if (rightDirection) { x[0] += DOT_SIZE; }
            if (upDirection) { y[0] -= DOT_SIZE; }
            if (downDirection) { y[0] += DOT_SIZE; }
        }
    }

    private void checkCollision() {

        for (int i = bodyParts; i > 0; i--) {
            if ((i > 5) && (x[0] == x[i] && (y[0] == y[i]))) { inGame = false; }
        }

        if (y[0] > B_HEIGHT) { y[0] = 50; }

        if (y[0] < 50) { y[0] = B_HEIGHT; }

        if (x[0] > B_WIDTH) { x[0] = -40; }

        if (x[0] < -40) { x[0] = B_WIDTH; }

        if (!inGame) { timer.stop(); }
    }

    private void spawnPoint() {

        pointX = genRandomX();
        pointY = genRandomY() + 50;

        /*for (int i = bodyParts; i > 0; i--) {
            if (!(posX == x[i]) && !(posY == y[i])) {

                pointX = posX;
                pointY = posY;

            } else {
                spawnPoint();
            }
        }*/
    }

    private void pauseGame(Graphics g) {

        paused = true;
        String msgPause = "Game paused";
        g.drawString(msgPause, (B_WIDTH - fontMetrics(g, 40f, new Color(0xff5e5e)).stringWidth(msgPause)) / 2, B_HEIGHT / 2);
    }

    private void renderBackground(Graphics g) {

        int row;
        int col;
        int xCB;
        int yCB;

        for (row = 0; row < 15; row++) {
            for (col = 0; col < 15; col++) {
                xCB = col * 40;
                yCB = row * 40 + 50;
                if ((row % 2) == (col % 2))
                    g.setColor(new Color(0x060606));
                else
                    g.setColor(new Color(0x080808));

                g.fillRect(xCB, yCB, B_WIDTH, B_HEIGHT);
            }

        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (inGame) {
            if (score >= Integer.parseInt(highscore)) highscore = String.valueOf(score);

            checkPoint();
            checkCollision();
            move();
        }

        repaint();
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if ((key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_UP || key == KeyEvent.VK_W) && (!downDirection)) {
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            if ((key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) && (!upDirection)) {
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            if ((key == KeyEvent.VK_ESCAPE) && (inGame)) {

                if (paused) {
                    paused = false;
                } else {
                    paused = true;
                }
            }

            if ((key == KeyEvent.VK_SPACE) && (!inGame)) {

                DELAY = 320;
                score = 0;
                inGame = true;

                timer.stop();
                initGame();

                removeAll();
                revalidate();
                repaint();
            }
        }
    }

    private static void playSound(String sound) {


        if (Snake.i1.getState()) {

            try {

                InputStream audioSrc = Game.class.getClassLoader().getResourceAsStream("sounds/" + sound);
                InputStream bufferedIn = new BufferedInputStream(audioSrc);

                AudioInputStream audioIn = AudioSystem.getAudioInputStream(bufferedIn);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public FontMetrics fontMetrics(Graphics g, float size, Color color) {

        Font font = null;
        try { font = Font.createFont(Font.TRUETYPE_FONT, getClass().getClassLoader().getResourceAsStream("fonts/Early_GameBoy.ttf")).deriveFont(size); }
        catch (Exception e) { e.printStackTrace(); }

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(font);
        g.setFont(font);
        g.setColor(color);

        return getFontMetrics(font);
    }

    private int genRandomY() {
        int posY;

        int rand = (int) (Math.random() * RAND_POS);
        posY = ((rand * DOT_SIZE));

        return posY;
    }

    private int genRandomX() {
        int posX;

        int rand = (int) (Math.random() * RAND_POS);
        posX = ((rand * DOT_SIZE));

        return posX;
    }
}
