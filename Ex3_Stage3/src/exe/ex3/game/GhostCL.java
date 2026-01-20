package exe.ex3.game;

public class GhostCL {
    private int x, y;
    private int edibleTicks;
    private final int type;

    public GhostCL(int type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.edibleTicks = 0;
    }

    public int getType() { return type; }

    public double remainTimeAsEatable(int code) { return edibleTicks; }

    public void setPos(int x, int y) { this.x = x; this.y = y; }

    public void makeEdible(int ticks) { edibleTicks = Math.max(0, ticks); }

    public void tick() { if (edibleTicks > 0) edibleTicks--; }

    public boolean isEdible() { return edibleTicks > 0; }

    public int getX() { return x; }
    public int getY() { return y; }
}
