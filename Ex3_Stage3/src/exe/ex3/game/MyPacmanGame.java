package exe.ex3.game;

import java.util.Random;

public class MyPacmanGame implements PacmanGame {
    private int status = INIT;
    private boolean cyclic = true;

    private int[][] board;
    private int w = 20, h = 20;

    private int pacX = 1, pacY = 1;
    private int pacDir = STAY;

    private GhostCL[] ghosts;
    private int score = 0;
    private int steps = 0;

    private Random rnd = new Random(1);

    // optional: GUI can ignore this
    private Character lastKey = null;

    @Override
    public String init(int scenario, String mapFile, boolean cyclic, long seed, double dt, int a, int b) {
        this.cyclic = cyclic;
        this.status = INIT;
        this.score = 0;
        this.steps = 0;
        this.rnd = new Random(seed);

        // you can vary size by scenario if you want, but keep it simple:
        this.w = 20;
        this.h = 20;

        buildBoard();
        spawnEntities();
        return "OK";
    }

    private void buildBoard() {
        board = new int[w][h];

        int wall  = 1;
        int food  = 2;
        int power = 3;

        // border walls
        for (int x = 0; x < w; x++) {
            board[x][0] = wall;
            board[x][h - 1] = wall;
        }
        for (int y = 0; y < h; y++) {
            board[0][y] = wall;
            board[w - 1][y] = wall;
        }

        // fill food
        for (int x = 1; x < w - 1; x++) {
            for (int y = 1; y < h - 1; y++) {
                board[x][y] = food;
            }
        }

        // add a few simple internal walls (a "maze-ish" look)
        for (int x = 3; x < w - 3; x++) board[x][5] = wall;
        for (int x = 3; x < w - 3; x++) board[x][h - 6] = wall;
        for (int y = 3; y < h - 3; y++) board[7][y] = wall;
        for (int y = 3; y < h - 3; y++) board[w - 8][y] = wall;

        // open a few gaps so it’s playable
        board[7][5] = food;
        board[w - 8][h - 6] = food;
        board[10][5] = food;
        board[10][h - 6] = food;

        // power pellets (corners inside border)
        board[1][1] = power;
        board[1][h - 2] = power;
        board[w - 2][1] = power;
        board[w - 2][h - 2] = power;
    }

    private void spawnEntities() {
        // pacman start
        pacX = 1;
        pacY = 1;
        pacDir = STAY;

        // clear start cell so pacman doesn't instantly "eat" on tick 0 (optional)
        board[pacX][pacY] = 0;

        // ghosts (simple)
        ghosts = new GhostCL[3];
        ghosts[0] = new GhostCL(0, w - 2, h - 2);
        ghosts[1] = new GhostCL(1, w - 2, 1);
        ghosts[2] = new GhostCL(2, 1, h - 2);
    }

    @Override
    public void play() {
        if (board == null) init(0, "", true, 1, 0.1, 0, 0);
        status = PLAY;
    }

    /**
     * This is not in the interface, but your GUI already calls it.
     */
    public void applyPacMove(int dir) {
        this.pacDir = dir;
    }

    @Override
    public String move(int code) {
        if (status != PLAY) return "NOT_PLAYING";

        steps++;

        // 1) Move pacman
        movePacmanOneStep();

        // 2) Move ghosts
        if (steps % 3 == 0) moveGhostsOneStep();

        // 3) Tick edible timers
        for (GhostCL g : ghosts) g.tick();

        // 4) Check win condition (no food/power left)
        if (countRemainingFoodAndPower() == 0) {
            status = DONE;
            return "WIN";
        }

        return "OK";
    }

    private void movePacmanOneStep() {
        int nx = pacX, ny = pacY;
        if (pacDir == UP) ny++;
        else if (pacDir == DOWN) ny--;
        else if (pacDir == LEFT) nx--;
        else if (pacDir == RIGHT) nx++;

        // cyclic wrap if enabled
        if (cyclic) {
            if (nx < 0) nx = w - 1;
            if (nx >= w) nx = 0;
            if (ny < 0) ny = h - 1;
            if (ny >= h) ny = 0;
        }

        // bounds if not cyclic
        if (!cyclic) {
            if (nx < 0 || nx >= w || ny < 0 || ny >= h) return;
        }

        // wall check
        if (board[nx][ny] == 1) return;

        pacX = nx;
        pacY = ny;

        // eat
        if (board[pacX][pacY] == 2) {          // food
            score += 1;
            board[pacX][pacY] = 0;
        } else if (board[pacX][pacY] == 3) {   // power
            score += 5;
            board[pacX][pacY] = 0;
            for (GhostCL g : ghosts) g.makeEdible(70);
        }

        // collision after pacman moves
        handleCollisions();
    }

    private void moveGhostsOneStep() {
        for (GhostCL g : ghosts) {
            int gx = g.getX();
            int gy = g.getY();

            // try up to 6 random directions to find a legal move
            int[] dirs = {UP, DOWN, LEFT, RIGHT};
            boolean moved = false;

            for (int tries = 0; tries < 6 && !moved; tries++) {
                int dir = dirs[rnd.nextInt(dirs.length)];
                int nx = gx, ny = gy;

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
                    if (nx < 0 || nx >= w || ny < 0 || ny >= h) continue;
                }

                if (board[nx][ny] == 1) continue; // wall
                g.setPos(nx, ny);
                moved = true;
            }
        }

        // collision after ghosts move
        handleCollisions();
    }

    private void handleCollisions() {
        for (GhostCL g : ghosts) {
            if (g.getX() == pacX && g.getY() == pacY) {
                if (g.isEdible()) {
                    score += 10;
                    // reset ghost to a corner
                    g.setPos(w - 2, h - 2);
                    g.makeEdible(0);
                } else {
                    status = DONE;
                }
            }
        }
    }

    private int countRemainingFoodAndPower() {
        int cnt = 0;
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (board[x][y] == 2 || board[x][y] == 3) cnt++;
            }
        }
        return cnt;
    }

    @Override
    public String end(int code) {
        status = DONE;
        return "DONE";
    }

    @Override
    public int[][] getGame(int code) {
        return board;
    }

    @Override
    public String getPos(int code) {
        return pacX + "," + pacY;
    }

    @Override
    public GhostCL[] getGhosts(int code) {
        return ghosts;
    }

    @Override
    public String getData(int code) {
        return "score=" + score + ",steps=" + steps;
    }

    public int getScore() {
        return score;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public boolean isCyclic() {
        return cyclic;
    }

    @Override
    public Character getKeyChar() {
        return lastKey;
    }

    // Optional helper if you ever want to feed key presses into the server
    public void setKeyChar(Character c) {
        lastKey = c;
    }
}
