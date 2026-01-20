package exe.ex3.game;

import java.awt.Color;

public class Game {
    public static final int STAY = 0;
    public static final int UP = 1;
    public static final int LEFT = 2;
    public static final int DOWN = 3;
    public static final int RIGHT = 4;

    // Keep these tile codes consistent in your server board:
    // 0 empty, 1 wall, 2 food, 3 power
    public static int getIntColor(Color c, int code) {
        if (Color.BLACK.equals(c)) return 0;
        if (Color.BLUE.equals(c)) return 1;
        if (Color.PINK.equals(c)) return 2;
        if (Color.GREEN.equals(c)) return 3;
        return 0;
    }
}
