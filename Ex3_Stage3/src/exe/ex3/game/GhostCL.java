package exe.ex3.game;

public class GhostCL {
    private int x, y;
    private int edibleTicks;
    private int type;

    public GhostCL(int type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.edibleTicks = 0;
    }

    public int getType() { return type; }

    public int getStatus() { return edibleTicks > 0 ? 1 : 0; }

    public String getPos(int code) { return x + "," + y; }

    public double remainTimeAsEatable(int code) { return edibleTicks; }

    // helpers for your server
    public void setPos(int x, int y) { this.x = x; this.y = y; }

    public void makeEdible(int ticks) { edibleTicks = ticks; }

    public void tick() { if (edibleTicks > 0) edibleTicks--; }

    public boolean isEdible() { return edibleTicks > 0; }

    public int getX() { return x; }
    public int getY() { return y; }
}
