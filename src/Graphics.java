import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 * This is a class
 * Created 2020-03-25
 *
 * @author Magnus Silverdal
 */
public class Graphics<ball2> extends Canvas implements Runnable {
    private String title = "Graphics";
    private int width;
    private int height;

    private JFrame frame;
    private BufferedImage image;
    private int[] pixels;
    private int scale;

    private Thread thread;
    private boolean running = false;
    private int fps = 60;
    private int ups = 70;

    private Ball b,ball2;
    private Paddle paddle1;
    private Paddle paddle2;
    private Paddle paddle3;

    public Graphics(int w, int h, int scale) {
        this.width = w;
        this.height = h;
        this.scale = scale;
        image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        Dimension size = new Dimension(scale*width, scale*height);
        setPreferredSize(size);
        frame = new JFrame();
        frame.setTitle(title);
        frame.add(this);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        this.addKeyListener(new MyKeyListener());
        this.addMouseListener(new MyMouseListener());
        this.requestFocus();
   /* paddle1 och paddle2 gör att röda rektangel  byter platsen

    */
        b = new Ball(200,150);
        ball2 = new Ball(100,50);
        paddle1 = new Paddle(300,100,0xFFFF0000);
        paddle2 = new Paddle(10,80,0xFF00FF0);
        paddle3 = new Paddle(150,150,0xFF0f00);
    }

    private void draw() {
        for (int i = 0 ; i < pixels.length ; i++) {
            pixels[i] = 0xFF000000;
        }
        b.draw(pixels, width);
        ball2.draw(pixels, width);
        paddle1.draw(pixels,width);
        paddle2.draw(pixels,width);
        paddle3.draw(pixels,width);

        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }

        java.awt.Graphics g = bs.getDrawGraphics();
        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        g.dispose();
        bs.show();
    }

    private void update() {

        b.update(paddle1.getBoundingBox());
        b.update(paddle2.getBoundingBox());
        b.update(paddle3.getBoundingBox());
        ball2.update(paddle1.getBoundingBox());
        ball2.update(paddle2.getBoundingBox());
        ball2.update(paddle3.getBoundingBox());
        paddle1.update();
        paddle2.update();
        paddle3.update();

    }

    public synchronized void start() {
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public synchronized void stop() {
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        double frameUpdateinteval = 1000000000.0 / fps;
        double stateUpdateinteval = 1000000000.0 / ups;
        double deltaFrame = 0;
        double deltaUpdate = 0;
        long lastTime = System.nanoTime();

        while (running) {
            long now = System.nanoTime();
            deltaFrame += (now - lastTime) / frameUpdateinteval;
            deltaUpdate += (now - lastTime) / stateUpdateinteval;
            lastTime = now;

            while (deltaUpdate >= 1) {
                update();
                deltaUpdate--;
            }

            while (deltaFrame >= 1) {
                draw();
                deltaFrame--;
            }
        }
        stop();
    }

    private class MyKeyListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent keyEvent) {

        }

        @Override
        public void keyPressed(KeyEvent keyEvent) {
            paddle1.keyPressed(keyEvent);
        }

        @Override
        public void keyReleased(KeyEvent keyEvent) { paddle2.keyReleased(keyEvent);
        }

    }

    private class MyMouseListener implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {
        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {
        }
    }
}
