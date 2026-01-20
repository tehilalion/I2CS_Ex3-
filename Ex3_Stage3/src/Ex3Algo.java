import exe.ex3.game.Game;
import exe.ex3.game.GhostCL;
import exe.ex3.game.PacManAlgo;
import exe.ex3.game.PacmanGame;

import java.util.ArrayList;
import java.util.List;

public class Ex3Algo implements PacManAlgo {
    private int _count = 0;

    @Override
    public String getInfo() {
        return "BFS-based: avoid danger ghosts, chase edible ghosts, otherwise go to nearest power/food";
    }

    @Override
    public int move(PacmanGame game) {
        int code = 0;

        int[][] board = game.getGame(code);
        Map2D map = new Map(board);

        // Server tile codes (OUR server):
        final int WALL = 1;
        final int FOOD = 2;
        final int POWER = 3;

        Index2D pacmanPos = parsePosition(game.getPos(code));
        GhostCL[] ghosts = game.getGhosts(code);
        _count++;

        // 1) If danger ghost near: escape OR go to close power
        GhostCL danger = findNearDangerGhost(pacmanPos, ghosts, 6.0);
        if (danger != null) {
            Index2D emergencyPower = findNearPowerUP(pacmanPos, map, WALL, POWER);
            if (emergencyPower != null) {
                int d = bfsDistance(pacmanPos, emergencyPower, map, WALL);
                if (d != -1 && d <= 6) {
                    return moveTowardTarget(pacmanPos, emergencyPower, map, WALL);
                }
            }
            return escapeDir(pacmanPos, danger, map, WALL);
        }

        // 2) If edible ghost reachable: chase
        GhostCL edible = findNearestEdibleGhost(pacmanPos, ghosts, map, WALL, 20.0);
        if (edible != null) {
            Index2D gpos = new Index2D(edible.getX(), edible.getY());
            return moveTowardTarget(pacmanPos, gpos, map, WALL);
        }

        // 3) Prefer power if near (or ghost somewhat close)
        Index2D power = findNearPowerUP(pacmanPos, map, WALL, POWER);
        if (power != null) {
            GhostCL approaching = findNearDangerGhost(pacmanPos, ghosts, 12.0);
            int distToPower = bfsDistance(pacmanPos, power, map, WALL);
            if (distToPower != -1 && (approaching != null || distToPower <= 4)) {
                return moveTowardTarget(pacmanPos, power, map, WALL);
            }
        }

        // 4) Otherwise go to nearest food
        Index2D food = findNearFood(pacmanPos, map, WALL, FOOD);
        if (food != null) {
            return moveTowardTarget(pacmanPos, food, map, WALL);
        }

        // 5) Fallback: random legal direction
        return randomLegalDir(pacmanPos, map, WALL);
    }

    // ---------- helpers ----------

    private int bfsDistance(Index2D from, Index2D to, Map2D map, int wallValue) {
        Map2D dist = map.allDistance(from, wallValue);
        return dist.getPixel(to.getX(), to.getY());
    }

    private static int moveTowardTarget(Index2D pacmanPos, Index2D target, Map2D map, int wallValue) {
        Pixel2D[] path = map.shortestPath(pacmanPos, target, wallValue);
        if (path != null && path.length > 1) {
            Pixel2D step = path[1];
            return computeDir(pacmanPos, step, map.getHeight(), map.getWidth());
        }
        return randomDir();
    }

    private int escapeDir(Index2D pacmanPos, GhostCL dangerG, Map2D map, int wallValue) {
        Index2D ghostPos = new Index2D(dangerG.getX(), dangerG.getY());

        // Find a "far" point from ghost using distance map
        Map2D distFromGhost = map.allDistance(ghostPos, wallValue);

        Index2D best = null;
        int bestD = -1;

        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                int d = distFromGhost.getPixel(x, y);
                if (d > bestD && d < 1000) { // ignore unreachable
                    bestD = d;
                    best = new Index2D(x, y);
                }
            }
        }

        if (best != null) {
            Pixel2D[] path = map.shortestPath(pacmanPos, best, wallValue);
            if (path != null && path.length > 1) {
                return computeDir(pacmanPos, path[1], map.getHeight(), map.getWidth());
            }
        }

        // fallback: move away by euclidean distance
        int[] dirs = {Game.UP, Game.DOWN, Game.LEFT, Game.RIGHT};
        int bestDir = Game.STAY;
        double maxDist = -1;

        for (int dir : dirs) {
            Index2D next = getNextPosition(pacmanPos, dir, map.getWidth(), map.getHeight());
            if (map.getPixel(next.getX(), next.getY()) == wallValue) continue;
            double d = next.distance2D(ghostPos);
            if (d > maxDist) {
                maxDist = d;
                bestDir = dir;
            }
        }
        return bestDir;
    }

    private GhostCL findNearDangerGhost(Index2D pos, GhostCL[] ghosts, double maxDistance) {
        if (ghosts == null) return null;
        GhostCL nearest = null;
        double minDist = maxDistance;

        for (GhostCL g : ghosts) {
            // danger if NOT edible
            if (g.remainTimeAsEatable(0) <= 0) {
                Index2D gPos = new Index2D(g.getX(), g.getY());
                double d = pos.distance2D(gPos);
                if (d < minDist) {
                    minDist = d;
                    nearest = g;
                }
            }
        }
        return nearest;
    }

    private GhostCL findNearestEdibleGhost(Index2D pos, GhostCL[] ghosts, Map2D map, int wallValue, double maxDistance) {
        if (ghosts == null) return null;
        GhostCL best = null;
        double bestDist = maxDistance;

        for (GhostCL g : ghosts) {
            double t = g.remainTimeAsEatable(0);
            if (t <= 0) continue;

            Index2D gPos = new Index2D(g.getX(), g.getY());
            int d = bfsDistance(pos, gPos, map, wallValue);
            if (d == -1) continue;

            // must be reachable before edible time ends
            if (d <= (int)Math.floor(t) && d < bestDist) {
                bestDist = d;
                best = g;
            }
        }
        return best;
    }

    private Index2D findNearPowerUP(Index2D pos, Map2D map, int wallValue, int powerValue) {
        Map2D dist = map.allDistance(pos, wallValue);
        Index2D best = null;
        int bestD = Integer.MAX_VALUE;

        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                if (map.getPixel(x, y) == powerValue) {
                    int d = dist.getPixel(x, y);
                    if (d != -1 && d < bestD) {
                        bestD = d;
                        best = new Index2D(x, y);
                    }
                }
            }
        }
        return best;
    }

    private Index2D findNearFood(Index2D pos, Map2D map, int wallValue, int foodValue) {
        Map2D dist = map.allDistance(pos, wallValue);
        Index2D best = null;
        int bestD = Integer.MAX_VALUE;

        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                if (map.getPixel(x, y) == foodValue) {
                    int d = dist.getPixel(x, y);
                    if (d != -1 && d < bestD) {
                        bestD = d;
                        best = new Index2D(x, y);
                    }
                }
            }
        }
        return best;
    }

    private static int computeDir(Index2D pacmanPos, Pixel2D p, int h, int w) {
        int x1 = pacmanPos.getX(), y1 = pacmanPos.getY();
        int x2 = p.getX(), y2 = p.getY();

        if (x2 != x1) {
            if (x1 == x2 + 1 || (x2 == w - 1 && x1 == 0)) return Game.LEFT;
            if (x2 == x1 + 1 || (x1 == w - 1 && x2 == 0)) return Game.RIGHT;
        }
        if (y2 != y1) {
            if (y2 == y1 + 1 || (y1 == h - 1 && y2 == 0)) return Game.UP;
            if (y1 == y2 + 1 || (y2 == h - 1 && y1 == 0)) return Game.DOWN;
        }
        return randomDir();
    }

    private static int randomDir() {
        int[] dirs = {Game.UP, Game.LEFT, Game.DOWN, Game.RIGHT};
        int ind = (int) (Math.random() * dirs.length);
        return dirs[ind];
    }

    private int randomLegalDir(Index2D pacmanPos, Map2D map, int wallValue) {
        int[] dirs = {Game.UP, Game.LEFT, Game.DOWN, Game.RIGHT};
        List<Integer> legal = new ArrayList<>();
        for (int d : dirs) {
            Index2D n = getNextPosition(pacmanPos, d, map.getWidth(), map.getHeight());
            if (map.getPixel(n.getX(), n.getY()) != wallValue) legal.add(d);
        }
        if (legal.isEmpty()) return Game.STAY;
        return legal.get((int)(Math.random() * legal.size()));
    }

    private Index2D getNextPosition(Index2D p, int dir, int w, int h) {
        int x = p.getX();
        int y = p.getY();
        if (dir == Game.UP) y++;
        if (dir == Game.DOWN) y--;
        if (dir == Game.RIGHT) x++;
        if (dir == Game.LEFT) x--;

        // cyclic wrap (your server is cyclic by default)
        if (x < 0) x = w - 1;
        if (x >= w) x = 0;
        if (y < 0) y = h - 1;
        if (y >= h) y = 0;

        return new Index2D(x, y);
    }

    private Index2D parsePosition(String pos) {
        pos = pos.replaceAll("[^0-9,\\-]", "");
        String[] parts = pos.split(",");
        int x = Integer.parseInt(parts[0].trim());
        int y = Integer.parseInt(parts[1].trim());
        return new Index2D(x, y);
    }
}
