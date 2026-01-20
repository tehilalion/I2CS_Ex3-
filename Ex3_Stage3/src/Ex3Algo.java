import exe.ex3.game.PacManAlgo;
import exe.ex3.game.PacmanGame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Ex3Algo implements PacManAlgo {
    private final Random rnd = new Random();

    @Override
    public String getInfo() {
        return "Simple wall-avoiding random algo";
    }

    @Override
    public int move(PacmanGame game) {
        int[][] b = game.getGame(0);
        int w = b.length;
        int h = b[0].length;

        String[] pp = game.getPos(0).split(",");
        int x = Integer.parseInt(pp[0].trim());
        int y = Integer.parseInt(pp[1].trim());

        // collect legal moves (not into walls)
        List<Integer> legal = new ArrayList<>(4);
        if (isFree(b, w, h, x, y + 1)) legal.add(PacmanGame.UP);
        if (isFree(b, w, h, x, y - 1)) legal.add(PacmanGame.DOWN);
        if (isFree(b, w, h, x - 1, y)) legal.add(PacmanGame.LEFT);
        if (isFree(b, w, h, x + 1, y)) legal.add(PacmanGame.RIGHT);

        if (legal.isEmpty()) return PacmanGame.STAY;
        return legal.get(rnd.nextInt(legal.size()));
    }

    private boolean isFree(int[][] b, int w, int h, int x, int y) {
        if (x < 0 || y < 0 || x >= w || y >= h) return false;
        return b[x][y] != 1; // 1 = wall
    }
}
