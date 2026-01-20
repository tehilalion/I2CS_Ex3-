package exe.ex3.game;

import java.util.Random;

public class MyPacmanGame implements PacmanGame {
    private int status = INIT;
    private boolean cyclic = true;

    private int[][] board;
    private int w = 20, h = 20;

    private int pacX = 1, pacY = 1;
    private GhostCL[] ghosts;

    private final Random rnd = new Random();

    @Override public Character getKeyChar() { return null; }

    @Override public String getPos(int code) { return pacX + "," + pacY; }

    @Override public GhostCL[] getGhosts(int code) { return ghosts; }

    @Override public int[][] getGame(int code) { return board; }

    @Override public String move(int code) {
        if (status != PLAY) return "ERR";

        // tick ghosts edible timers
        for (GhostCL g : ghosts) g.tick();

        return "OK";
    }

    @Override public void play() { status = PLAY; }

    @Override public String end(int code) { status = DONE; return "DONE"; }

    @Override public String getData(int code) { return "pos=" + getPos(code) + " status=" + status; }

    @Override public int getStatus() { return status; }

    @Override public boolean isCyclic() { return cyclic; }

    @Override
    public String init(int scenario, String mapFile, boolean cyclic, long seed, double dt, int a, int b) {
        this.cyclic = cyclic;
        rnd.setSeed(seed);

        board = new int[w][h];

        int wall = Game.getIntColor(java.awt.Color.BLUE, 0);
        int food = Game.getIntColor(java.awt.Color.PINK, 0);
        int power = Game.getIntColor(java.awt.Color.GREEN, 0);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (x == 0 || y == 0 || x == w - 1 || y == h - 1) board[x][y] = wall;
                else board[x][y] = food;
            }
        }

        board[2][2] = power;

        pacX = 1; pacY = 1;
        board[pacX][pacY] = 0;

        ghosts = new GhostCL[] { new GhostCL(0, w - 2, h - 2) };

        status = INIT;
        return "OK";
    }

    // client helper: apply direction from Ex3Algo
    public void applyPacMove(int dir) {
        int nx = pacX, ny = pacY;

        if (dir == UP) ny++;
        else if (dir == DOWN) ny--;
        else if (dir == LEFT) nx--;
        else if (dir == RIGHT) nx++;

        if (cyclic) {
            if (nx < 0) nx = w - 1;
            if (nx >= w) nx = 0;
            if (ny < 0) ny = h - 1;
            if (ny >= h) ny = 0;
        } else {
            if (nx < 0 || nx >= w || ny < 0 || ny >= h) return;
        }

        int wall = Game.getIntColor(java.awt.Color.BLUE, 0);
        if (board[nx][ny] == wall) return;

        pacX = nx; pacY = ny;

        // eat food/power
        int power = Game.getIntColor(java.awt.Color.GREEN, 0);
        if (board[pacX][pacY] == power) {
            board[pacX][pacY] = 0;
            for (GhostCL g : ghosts) g.makeEdible(60);
        } else {
            board[pacX][pacY] = 0;
        }
    }
}
