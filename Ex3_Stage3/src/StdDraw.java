import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 * Minimal StdDraw-compatible implementation for this assignment.
 * Put this file in the DEFAULT package (no package line).
 */
public final class StdDraw {
    // Common colors (as in Princeton StdDraw)
    public static final Color BLACK  = Color.BLACK;
    public static final Color BLUE   = Color.BLUE;
    public static final Color CYAN   = Color.CYAN;
    public static final Color RED    = Color.RED;
    public static final Color GREEN  = Color.GREEN;
    public static final Color PINK   = Color.PINK;
    public static final Color WHITE  = Color.WHITE;
    public static final Color YELLOW = Color.YELLOW;
    public static final Color ORANGE = Color.ORANGE;
    public static final Color GRAY   = Color.GRAY;

    private static JFrame frame;
    private static DrawPanel panel;

    private static int width = 512, height = 512;
    private static double xmin = 0.0, xmax = 1.0, ymin = 0.0, ymax = 1.0;

    private static Color penColor = Color.BLACK;

    // double buffer
    private static BufferedImage offscreen;
    private static Graphics2D g2;

    // keyboard
    private static volatile char lastKeyTyped = 0;

    static {
        init();
    }

    private static void init() {
        if (frame != null) return;

        frame = new JFrame("StdDraw");
        panel = new DrawPanel();

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setContentPane(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                lastKeyTyped = e.getKeyChar();
            }
        });

        setupBuffer();
        frame.setVisible(true);
    }

    private static void setupBuffer() {
        offscreen = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2 = offscreen.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        clear(WHITE);
        panel.setPreferredSize(new Dimension(width, height));
        panel.revalidate();
        frame.pack();
    }

    public static void setCanvasSize(int w, int h) {
        width = Math.max(1, w);
        height = Math.max(1, h);
        setupBuffer();
        show();
    }

    public static void setXscale(double min, double max) {
        xmin = min; xmax = max;
    }

    public static void setYscale(double min, double max) {
        ymin = min; ymax = max;
    }

    public static void clear() {
        clear(WHITE);
    }

    public static void clear(Color c) {
        Color old = g2.getColor();
        g2.setColor(c);
        g2.fillRect(0, 0, width, height);
        g2.setColor(old);
    }

    public static void setPenColor(Color c) {
        penColor = c;
        g2.setColor(penColor);
    }

    public static void filledSquare(double x, double y, double half) {
        double x0 = x - half;
        double y0 = y - half;
        double x1 = x + half;
        double y1 = y + half;

        int px0 = scaleX(x0);
        int py1 = scaleY(y1); // top
        int px1 = scaleX(x1);
        int py0 = scaleY(y0); // bottom

        int rw = Math.abs(px1 - px0);
        int rh = Math.abs(py0 - py1);
        g2.setColor(penColor);
        g2.fillRect(Math.min(px0, px1), Math.min(py1, py0), rw, rh);
    }

    public static void filledCircle(double x, double y, double r) {
        int cx = scaleX(x);
        int cy = scaleY(y);
        int rr = Math.max(1, (int)Math.round(r * width / (xmax - xmin)));
        g2.setColor(penColor);
        g2.fillOval(cx - rr, cy - rr, 2 * rr, 2 * rr);
    }

    public static void text(double x, double y, String s) {
        int px = scaleX(x);
        int py = scaleY(y);
        g2.setColor(penColor);
        g2.drawString(s, px, py);
    }

    public static void show() {
        panel.repaint();
        Toolkit.getDefaultToolkit().sync();
    }

    public static void pause(int t) {
        try { Thread.sleep(Math.max(0, t)); } catch (InterruptedException ignored) {}
    }

    // Optional compatibility methods (not required but harmless)
    public static void enableDoubleBuffering() {}
    public static void disableDoubleBuffering() {}

    public static boolean hasNextKeyTyped() {
        return lastKeyTyped != 0;
    }

    public static char nextKeyTyped() {
        char c = lastKeyTyped;
        lastKeyTyped = 0;
        return c;
    }

    // coordinate transforms
    private static int scaleX(double x) {
        double f = (x - xmin) / (xmax - xmin);
        return (int)Math.round(f * (width - 1));
    }

    private static int scaleY(double y) {
        // invert Y: StdDraw origin is bottom-left; Swing is top-left
        double f = (y - ymin) / (ymax - ymin);
        return (int)Math.round((1.0 - f) * (height - 1));
    }

    private static class DrawPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (offscreen != null) g.drawImage(offscreen, 0, 0, null);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(width, height);
        }
    }

    private StdDraw() {}
}
