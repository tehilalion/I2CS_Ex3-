

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
        GhostCL[] ghosts = game.getGhosts(code);
        _count++;



        GhostCL danger = findNearDangerGhost(pacmanPos, ghosts, 7.0);
        if (danger != null) {
            Index2D emergencyPower = findNearPowerUP(pacmanPos, map, blue, green);
            if (emergencyPower != null && pacmanPos.distance2D(emergencyPower) < 5.0) {
                System.out.println("EMERGENCY: Going for PowerUP to survive!");
                return moveTowardTarget(pacmanPos, emergencyPower, map, blue);
            }
            return escapeDir(pacmanPos, danger, map, blue);
        }

        GhostCL edibleGhost = findNearestEdibleGhost(pacmanPos, ghosts, 20.0);
        if (edibleGhost != null) {
            return chaseEdibleGhost(pacmanPos, edibleGhost, map, blue);
        }

        Index2D power =findNearPowerUP (pacmanPos, map, blue, green);
        if (power != null){
                double distToPower = pacmanPos.distance2D(power);
                GhostCL approachingGhost = findNearDangerGhost(pacmanPos, ghosts, 15.0);
                if (approachingGhost != null || distToPower < 3.0) {
                    return moveTowardTarget(pacmanPos, power, map, blue);
                }
            }

        Index2D food = findNearFood(pacmanPos, map, blue, pink);
        if (food != null) {
            return moveTowardTarget(pacmanPos, food, map, blue);
        }

        int dir = randomDir();
        return dir;
    }

    private int chaseEdibleGhost(Index2D pacmanPos, GhostCL edibleGhost, Map2D map, int blue) {
            Index2D ghostPos = parsePosition(edibleGhost.getPos(0));

            System.out.println("Chasing edible ghost at: " + ghostPos.getX() + "," + ghostPos.getY()
                    + " (time left: " + edibleGhost.remainTimeAsEatable(2) + ")");

            return moveTowardTarget(pacmanPos, ghostPos, map, blue);
        }


    // Add this function to find the nearest edible ghost
    private GhostCL findNearestEdibleGhost(Index2D pos, GhostCL[] ghosts, double maxDistance) {
        GhostCL nearest = null;
        double minDist = maxDistance;
        for (int i = 0; i < ghosts.length; i++) {
            GhostCL ghost = ghosts[i];
            if (ghost.remainTimeAsEatable(2) > 0) {
                Index2D ghostPos = parsePosition(ghost.getPos(0));

                int gx = ghostPos.getX();
                int gy = ghostPos.getY();
                if (gx >= 10 && gx <= 15 && gy >= 10 && gy <= 15) {
                    continue;
                }
                double dist = pos.distance2D(ghostPos);
                if (dist < minDist) {
                    minDist = dist;
                    nearest = ghost;
                }
            }
        }

        return nearest;
    }

    private int escapeDir(Index2D pacmanPos, GhostCL dangerG, Map2D map, int blue) {
        Index2D ghostPos = parsePosition(dangerG.getPos(0));

        // Find the safest point that's FAR from the ghost using allDistance
        Map2D distFromGhost = map.allDistance(ghostPos, blue);

        // Find the farthest reachable point
        Index2D safePoint = null;
        int maxDist = 0;

        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                int d = distFromGhost.getPixel(x, y);
                if (d > maxDist && d < 1000) { // 1000+ means unreachable/wall
                    maxDist = d;
                    safePoint = new Index2D(x, y);
                }
            }
        }

        // Path to the safest point
        if (safePoint != null) {
            Pixel2D[] path = map.shortestPath(pacmanPos, safePoint, blue);
            if (path != null && path.length > 1) {
                System.out.println("Escaping toward safe point: " + safePoint.getX() + "," + safePoint.getY());
                return computeDir(pacmanPos, path[1], map.getHeight(), map.getWidth());
            }
        }

        // Fallback: just move away
        int[] dirs = {Game.UP, Game.DOWN, Game.LEFT, Game.RIGHT};
        int bestDir = -1;
        double maxDist2 = -1;

        for (int dir : dirs) {
            Index2D nextPos = getNextPosition(pacmanPos, dir, map.getWidth(), map.getHeight());
            if (map.getPixel(nextPos.getX(), nextPos.getY()) !=blue) {
                double dist = nextPos.distance2D(ghostPos);
                if (dist > maxDist2) {
                    maxDist2 = dist;
                    bestDir = dir;
                }
            }
        }

        return bestDir != -1 ? bestDir : randomDir();
    }

    private Index2D getNextPosition(Index2D p, int dir, int w, int h) {
            int x = p.getX();
            int y = p.getY();
            if (dir == Game.UP) y++;
            if (dir == Game.DOWN) y--;
            if (dir == Game.RIGHT) x++;
            if (dir == Game.LEFT) x--;

            if (x < 0)
                x = w - 1;
            if (x >= w)
                x = 0;
            if (y < 0)
                y = h - 1;
            if (y >= h)
                y = 0;
            return new Index2D(x, y);
        }

    private GhostCL findNearDangerGhost(Index2D pos, GhostCL[] ghosts, double distance) {
        for (GhostCL g: ghosts) {
            if (g.remainTimeAsEatable(0)<=0){
                Index2D gPos= parsePosition(g.getPos(0));

                if (gPos.getX() >= 10 && gPos.getX() <= 15 && gPos.getY() >= 10 && gPos.getY() <= 12) {
                    continue;
                }

                if (pos.distance2D(gPos)<distance) {
                    return g;
                }
            }
        }
        return null;
    }



    private Index2D findNearPowerUP(Index2D pos, Map2D map, int blue, int green) {
        Map2D mapDist = map.allDistance(pos, blue);
        Index2D powerUp = null;
        int minDist = Integer.MAX_VALUE;
        for (int x=0; x< map.getWidth(); x++){
            for (int y=0; y< map.getHeight(); y++){
                if (map.getPixel(x,y)== green){
                        int d = mapDist.getPixel(x, y);
                        if (d != -1 && d < minDist) {
                            minDist = d;
                            powerUp = new Index2D(x, y);
                    }
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
                        int d = mapDist.getPixel(x, y);
                        if (d != -1 && d < minDist) {
                            minDist = d;
                            food = new Index2D(x, y);

                   }
                }
            }
        }
        return food;
    }

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