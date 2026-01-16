package mygame.server;

public class GameState {
    private GameBoard board;
    private PacMan pacman;
    private Ghost[] ghosts;
    private int score;
    private boolean gameOver;

    public GameState(GameBoard board, PacMan pacman, Ghost[] ghosts) {
        this.board = board;
        this.pacman = pacman;
        this.ghosts = ghosts;
        this.score = 0;
        this.gameOver = false;
    }

    public void tick(int pacmanDir) {
        pacman.move(pacmanDir, board);

        for (Ghost g : ghosts) {
            g.moveRandom(board);
            g.tick();

            // collision
            if (g.getX() == pacman.getX() && g.getY() == pacman.getY()) {
                if (g.isEdible()) {
                    score += 200;
                } else {
                    gameOver = true;
                }
            }
        }
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getScore() {
        return score;
    }
}
