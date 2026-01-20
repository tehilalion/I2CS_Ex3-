package exe.ex3.game;

public interface PacmanGame {
    int INIT = 0;
    int PLAY = 1;
    int PAUSE = 2;
    int DONE = 3;
    int ERR = -1;
    int STAY = 0;
    int LEFT = 2;
    int RIGHT = 4;
    int UP = 1;
    int DOWN = 3;

    Character getKeyChar();
    String getPos(int code);
    GhostCL[] getGhosts(int code);
    int[][] getGame(int code);

    String move(int code);
    void play();
    String end(int code);

    String getData(int code);
    int getStatus();
    boolean isCyclic();

    String init(int scenario, String mapFile, boolean cyclic, long seed, double dt, int a, int b);
}
