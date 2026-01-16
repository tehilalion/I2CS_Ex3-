package mygame.server;

public class Ghost {
    private int x;
    private int y;
    private int edibleTime; // ticks remaining

    public Ghost(int x, int y) {
        this.x = x;
        this.y = y;
        this.edibleTime = 0;
    }

    public void setEdible(int time) {
        edibleTime = time;
    }

    public boolean isEdible() {
        return edibleTime > 0;
    }

    public void tick() {
        if (edibleTime > 0) edibleTime--;
    }

    public void moveRandom(GameBoard board) {
        int dir = (int)(Math.random() * 4);
        // same movement logic as PacMan (reuse code later)
    }

    public int getX() { return x; }
    public int getY() { return y; }
}
