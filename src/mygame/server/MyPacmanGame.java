package mygame.server;

import exe.ex3.game.*;

public class MyPacmanGame implements PacmanGame {

    private int status = INIT;
    private boolean cyclic;
    private int px = 1, py = 1;   // Pac-Man position
    private int[][] board;
    private GhostCL[] ghosts;

    @Override
    public String init(int id, String boardData, boolean cyclic,
                       long time, double speed, int ghostsCount, int level) {

        this.cyclic = cyclic;
        this.status = INIT;

        // VERY simple board (10x10 empty)
        board = new int[10][10];

        // Create dummy ghosts
        ghosts = new GhostCL[ghostsCount];
        for (int i = 0; i < ghostsCount; i++) {
            ghosts[i] = new GhostCL(i, 5 + i, 5, false);
        }

        return "OK";
    }

    @Override
    public void play() {
        status = PLAY;
    }

    @Override
    public String move(int dir) {
        if (status != PLAY) return "ERR";

        // Simple movement
        if (dir == UP) py--;
        if (dir == DOWN) py++;
        if (dir == LEFT) px--;
        if (dir == RIGHT) px++;

        // Bounds
        px = Math.max(0, Math.min(px, board.length - 1));
        py = Math.max(0, Math.min(py, board[0].length - 1));

        return "OK";
    }

    @Override
    public String getPos(int id) {
        return px + "," + py;
    }

    @Override
    public GhostCL[] getGhosts(int id) {
        return ghosts;
    }

    @Override
    public int[][] getGame(int id) {
        return board;
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
    public String end(int id) {
        status = DONE;
        return "Game Over";
    }

    @Override
    public String getData(int id) {
        return "Score=0";
    }

    @Override
    public Character getKeyChar() {
        return null;
    }
}
