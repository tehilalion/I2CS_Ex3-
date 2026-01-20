

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
            if (emergencyPower != null) {
                int d = bfsDistance(pacmanPos, emergencyPower, map, blue);
                if (d != -1 && d <= 5) {
                    System.out.println("EMERGENCY: Going for PowerUP to survive!");
                    return moveTowardTarget(pacmanPos, emergencyPower, map, blue);
                }
            }
            return escapeDir(pacmanPos, danger, map, blue);
        }

        GhostCL edibleGhost = findNearestEdibleGhost(pacmanPos, ghosts, map, blue, 20.0);
        if (edibleGhost != null) {
            return chaseEdibleGhost(pacmanPos, edibleGhost, map, blue);
        }

        Index2D power = findNearPowerUP(pacmanPos, map, blue, green);
        if (power != null) {
            GhostCL approachingGhost = findNearDangerGhost(pacmanPos, ghosts, 15.0);

            int distToPower = bfsDistance(pacmanPos, power, map, blue); // BFS distance
            if (distToPower != -1 && (approachingGhost != null || distToPower <= 3)) {
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

    private int bfsDistance(Index2D from, Index2D to, Map2D map, int blue) {
        Map2D dist = map.allDistance(from, blue);
        return dist.getPixel(to.getX(), to.getY());
    }

    private boolean inGhostHouse(Index2D p) {
        int x = p.getX(), y = p.getY();
        return (x >= 10 && x <= 15 && y >= 10 && y <= 15);
    }

    private GhostCL findNearestEdibleGhost(Index2D pos, GhostCL[] ghosts, Map2D map, int blue, double maxDistance) {
        GhostCL best = null;
        double bestDist = maxDistance;

        for (GhostCL ghost : ghosts) {
            double t = ghost.remainTimeAsEatable(2);
            if (t <= 0) continue;

            Index2D gPos = parsePosition(ghost.getPos(0));
            if (inGhostHouse(gPos)) continue;

            int d = bfsDistance(pos, gPos, map, blue);
            if (d == -1) continue;

            int stepsLeft = (int)Math.floor(t);


            if (d <= stepsLeft && d < bestDist) {
                bestDist = d;
                best = ghost;
            }
        }

        return best;
    }


    private int escapeDir(Index2D pacmanPos, GhostCL dangerG, Map2D map, int blue) {
        Index2D ghostPos = parsePosition(dangerG.getPos(0));

        Map2D distFromGhost = map.allDistance(ghostPos, blue);

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

        if (safePoint != null) {
            Pixel2D[] path = map.shortestPath(pacmanPos, safePoint, blue);
            if (path != null && path.length > 1) {
                System.out.println("Escaping toward safe point: " + safePoint.getX() + "," + safePoint.getY());
                return computeDir(pacmanPos, path[1], map.getHeight(), map.getWidth());
            }
        }

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


    private GhostCL findNearDangerGhost(Index2D pos, GhostCL[] ghosts, double maxDistance) {
        GhostCL nearest = null;
        double minDist = maxDistance;

        for (GhostCL g : ghosts) {
            if (g.remainTimeAsEatable(0) <= 0) { // not edible
                Index2D gPos = parsePosition(g.getPos(0));

                if (inGhostHouse(gPos)) {
                    continue;
                }


                double d = pos.distance2D(gPos);
                if (d < minDist) {
                    minDist = d;
                    nearest = g;
                }
            }
        }
        return nearest;
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
        if (path != null && path.length > 1) {
            Pixel2D p = path[1];
            return computeDir(pacmanPos, p, map.getHeight(), map.getWidth());
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
        pos = pos.replaceAll("[^0-9,\\-]", "");
        String[] parts = pos.split(",");
        int x = Integer.parseInt(parts[0].trim());
        int y = Integer.parseInt(parts[1].trim());
        return new Index2D(x, y);
    }



}