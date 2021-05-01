import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

public class Game extends JPanel implements ActionListener {

    private final int B_WIDTH = 600;
    private final int B_HEIGHT = 600;
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

        ImageIcon imageDot = new ImageIcon("src/resources/images/body.png");
        body = imageDot.getImage();

        ImageIcon imagePoint = new ImageIcon("src/resources/images/point.png");
        point = imagePoint.getImage();

        ImageIcon imageHead = new ImageIcon("src/resources/images/head.png");
        head = imageHead.getImage();
    }

    private void initGame() {

        bodyParts = 1;
        level = 1;
        highscore = Highscore.read();

        for (int i = 0; i < bodyParts; i++) {
            x[i] = 200 - i * DOT_SIZE;
            y[i] = 200;
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
        String msgScore = "HI " + highscore +  "   " + "Score " + score + "   " + "Level" + level;

        if (inGame) {

            int ypos;

            if (y[0] <= 40) { ypos = 570; }
            else { ypos = 30; }

            drawUI(g, (B_WIDTH - fontMetrics(g, 14f, Color.WHITE).stringWidth(msgScore)) / 2, ypos);
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

        } else {

            String msgRestart = "Press SPACE to restart";
            String msgHighscore = "New Highscore!";

            gameOver(g, (B_WIDTH - fontMetrics(g, 60f, new Color(0xff5e5e)).stringWidth("Game Over")) / 2, 100);

            if (Integer.parseInt(highscore) <= score) {
                g.drawString(msgHighscore, (B_WIDTH - fontMetrics(g, 30f, Color.WHITE).stringWidth(msgHighscore)) / 2, B_HEIGHT / 2);
            }

            drawUI(g, (B_WIDTH - fontMetrics(g, 20f, Color.WHITE).stringWidth(msgScore)) / 2, 140);
            g.drawString(msgRestart, (B_WIDTH - fontMetrics(g, 20f, Color.WHITE).stringWidth(msgRestart)) / 2, 550);
        }
    }

    private void gameOver(Graphics g, int x, int y) {

        g.drawString("Game Over", x, y);
        playSound("explosion.wav");

        if (Integer.parseInt(highscore) >= score) {
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

            if (leftDirection) {
                x[0] -= DOT_SIZE;
            }

            if (rightDirection) {
                x[0] += DOT_SIZE;
            }

            if (upDirection) {
                y[0] -= DOT_SIZE;
            }

            if (downDirection) {
                y[0] += DOT_SIZE;
            }
        }
    }

    private void checkCollision() {

        for (int i = bodyParts; i > 0; i--) {

            if ((i > 5) && (x[0] == x[i] && (y[0] == y[i]))) {
                inGame = false;
            }
        }

        if (y[0] > B_HEIGHT) {
            System.out.println("down");
            y[0] = -40;
        }

        if (y[0] < -40) {
            System.out.println("up");
            y[0] = B_HEIGHT;
        }

        if (x[0] > B_WIDTH) {
            System.out.println("right");
            x[0] = -40;
        }

        if (x[0] < -40) {
            System.out.println("left");
            x[0] = B_WIDTH;
        }

        if (!inGame) {
            timer.stop();
        }
    }

    private void spawnPoint() {

        pointX = genRandomX();
        pointY = genRandomY();

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
        String msg = "PAUSE";
        centeredDropShadow(g, msg, 40f, new Color(0xff5e5e), Color.WHITE, 3, B_HEIGHT / 2);
    }

    private void renderBackground(Graphics g) {

        int row;
        int col;
        int xCB;
        int yCB;

        for (row = 0; row < 15; row++) {
            for (col = 0; col < 15; col++) {
                xCB = col * 40;
                yCB = row * 40;
                if ((row % 2) == (col % 2))
                    g.setColor(new Color(0x060606));
                else
                    g.setColor(new Color(0x080808));

                g.fillRect(xCB, yCB, B_WIDTH, B_HEIGHT);
            }

        }
    }

    private void drawUI(Graphics g, int x, int y) {

            String msg = "HI " + highscore +  "   " + "Score " + score + "   " + "Level " + level;
            g.drawString(msg, x, y);
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
        System.out.println("X:" + x[0] + ", Y:" + y[0] + ", Delay: " + DELAY + " InGame: " + inGame);
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

        try {
            File f = new File("src/resources/sounds/" + sound);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(f.toURI().toURL());
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FontMetrics fontMetrics (Graphics g, float size, Color color) {

        Font font = null;
        try { font = Font.createFont(Font.TRUETYPE_FONT, new File("src/resources/fonts/Early_GameBoy.ttf")).deriveFont(size); }
        catch (Exception e) { e.printStackTrace(); }

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(font);
        g.setFont(font);
        g.setColor(color);

        return getFontMetrics(font);
    }

    private void centeredDropShadow(Graphics g, String s, float size, Color primaryColor, Color secondaryColor, int offset, int y) {

        g.drawString(s, (B_WIDTH - fontMetrics(g, size, primaryColor).stringWidth(s)) / 2, y);
        g.drawString(s, (B_WIDTH - fontMetrics(g, size, secondaryColor).stringWidth(s)) / 2 + offset, y + offset);
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
