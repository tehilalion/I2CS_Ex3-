

import exe.ex3.game.Game;
import exe.ex3.game.GhostCL;
import exe.ex3.game.PacManAlgo;
import exe.ex3.game.PacmanGame;

import java.awt.*;

/**
 * This is the major algorithmic class for Ex3 - the PacMan game:
 * <p>
 * This code is a very simple example (random-walk algorithm).
 * Your task is to implement (here) your PacMan algorithm.
 */
public class Ex3Algo implements PacManAlgo {
    private int _count;

    public Ex3Algo() {
        _count = 0;
    }

    private Map _map;

    @Override
    /**
     *  Add a short description for the algorithm as a String.
     */
    public String getInfo() {
        return null;
    }

    @Override
    /**
     * This ia the main method - that you should design, implement and test.
     */
    public int move(PacmanGame game) {
        int code = 0;
        int[][] board = game.getGame(0);
        Map2D map = new Map(board);
        Index2D pacmanPos = parsePosition(game.getPos(0));
        int blue = Game.getIntColor(Color.BLUE, code);
        int pink = Game.getIntColor(Color.PINK, code);
        int black = Game.getIntColor(Color.BLACK, code);
        int green = Game.getIntColor(Color.GREEN, code);
        if (_count == 0 || _count == 300) {
            printBoard(board);
            System.out.println("Blue=" + blue + ", Pink=" + pink + ", Black=" + black + ", Green=" + green);
            String pos = game.getPos(code).toString();
            System.out.println("Pacman coordinate: " + pos);
            GhostCL[] ghosts = game.getGhosts(code);
            printGhosts(ghosts);
            int up = Game.UP, left = Game.LEFT, down = Game.DOWN, right = Game.RIGHT;
        }
        _count++;




        Index2D power =findNearPowerUP (pacmanPos, map, blue, green);
        if (power != null){
            return moveTowardTarget (pacmanPos,power,map,blue);
        }

        Index2D food = findNearFood(pacmanPos, map, blue, pink);
        if (food != null) {
            return moveTowardTarget(pacmanPos, food, map, blue);
        }

        int dir = randomDir();
        return dir;
    }

    private Index2D findNearPowerUP(Index2D pos, Map2D map, int blue, int green) {
        Map2D mapDist = map.allDistance(pos, blue);
        Index2D powerUp = null;
        int minDist = Integer.MAX_VALUE;
        for (int x=0; x< map.getWidth(); x++){
            for (int y=0; y< map.getHeight(); y++){
                if (map.getPixel(x,y)== green){
                    //if (x != pos.getX() && y != pos.getY()) {
                        int d = mapDist.getPixel(x, y);
                       // if (d != -1 || d < minDist) {
                            minDist = d;
                            powerUp = new Index2D(x, y);
                      //  }
                   // }
                }
            }
        }

        return powerUp;
    }

    private Index2D findNearFood(Index2D pos, Map2D map, int blue, int pink) {
        Map2D mapDist = map.allDistance(pos, blue);
        Index2D food = null;
        int minDist = Integer.MAX_VALUE;
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                if (map.getPixel(x, y) == pink) {
                    //  if (x != pos.getX() && y != pos.getY()) {}
                        int d = mapDist.getPixel(x, y);
                      //  if (d != -1 || d < minDist) {
                            minDist = d;
                            food = new Index2D(x, y);

                   // }
                }
            }
        }
        return food;
    }


   /* private static int moveTowardTarget(Index2D pacmanPos, Index2D target, Map2D map, int blue) {
        Pixel2D[] path = map.shortestPath(pacmanPos, target, blue);
        if (path != null && path.length >= 2) {
            Pixel2D nextStep = path[1];
            return computeDir(pacmanPos, nextStep, map.getHeight(), map.getWidth());
        }
        return randomDir();
    }

    */




    private static int moveTowardTarget(Index2D pacmanPos, Index2D target, Map2D map, int blue) {
        Pixel2D[] path = map.shortestPath(pacmanPos, target, blue);
        if (path != null) {
            Pixel2D p = path[1];
            int dir = computeDir(pacmanPos, p,map.getHeight(), map.getWidth());
            return dir;
        }
        return randomDir();
    }



    private static int computeDir(Index2D pacmanPos, Pixel2D p, int h, int w) {
        int x1 = pacmanPos.getX();
        int x2 = p.getX();
        int y1 = pacmanPos.getY();
        int y2 = p.getY();
        if (x2 != x1) {
            if (x1 == x2 + 1 || (x2 == w - 1 && x1 == 0)) {
                return Game.LEFT;
            }
            if (x2 == x1 + 1 || (x1 == w - 1 && x2 == 0)) {
                return Game.RIGHT;
            }
        }
        if (y2 != y1) {
            if (y2 == y1 + 1 || (y1 == h - 1 && y2 == 0)) {
                return Game.UP;
            }
            if (y1 == y2 + 1 || (y2 == h - 1 && y1 == 0)) {
                return Game.DOWN;
            }
        }
        return randomDir();
    }


    private static void printBoard(int[][] b) {
        for (int y = 0; y < b[0].length; y++) {
            for (int x = 0; x < b.length; x++) {
                int v = b[x][y];
                System.out.print(v + "\t");
            }
            System.out.println();
        }
    }

    private static void printGhosts(GhostCL[] gs) {
        for (int i = 0; i < gs.length; i++) {
            GhostCL g = gs[i];
            System.out.println(i + ") status: " + g.getStatus() + ",  type: " + g.getType() + ",  pos: " + g.getPos(0) + ",  time: " + g.remainTimeAsEatable(0));
        }
    }

    private static int randomDir() {
        int[] dirs = {Game.UP, Game.LEFT, Game.DOWN, Game.RIGHT};
        int ind = (int) (Math.random() * dirs.length);
        return dirs[ind];
    }

    private Index2D parsePosition(String pos) {
        String[] parts = pos.split(",");
        int x = Integer.parseInt(parts[0].trim());
        int y = Integer.parseInt(parts[1].trim());
        return new Index2D(x, y);
    }



}