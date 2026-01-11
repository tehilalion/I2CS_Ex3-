import exe.ex3.game.Game;
import exe.ex3.game.GhostCL;
import exe.ex3.game.PacManAlgo;
import exe.ex3.game.PacmanGame;

import java.awt.*;

/**
 * This is the major algorithmic class for Ex3 - the PacMan game:
 *
 * This code is a very simple example (random-walk algorithm).
 * Your task is to implement (here) your PacMan algorithm.
 */
public class Ex3Algo implements PacManAlgo{
	private int _count;
    private Map _map;
	public Ex3Algo() {_count=0;}
    private static final int[] DIRECTIONS = {Game.UP, Game.DOWN, Game.LEFT, Game.RIGHT};

    @Override
	/**
	 *  Add a short description for the algorithm as a String.
	 */
	public String getInfo() {
		return "Smart PacMan Algo- esacpes ghosts, chases blue ghost, collects power up and pink dots";
	}
	@Override
	/**
	 * This is the main method - that you should design, implement and test.
	 */
	public int move(PacmanGame game) {
		if(_count==0 || _count==300) {
			int code = 0;
			int[][] board = game.getGame(0);
			printBoard(board);
			int blue = Game.getIntColor(Color.BLUE, code);
			int pink = Game.getIntColor(Color.PINK, code);
			int black = Game.getIntColor(Color.BLACK, code);
			int green = Game.getIntColor(Color.GREEN, code);
			System.out.println("Blue=" + blue + ", Pink=" + pink + ", Black=" + black + ", Green=" + green);
			String pos = game.getPos(code).toString();
			System.out.println("Pacman coordinate: "+pos);
			GhostCL[] ghosts = game.getGhosts(code);
			printGhosts(ghosts);

        }
		_count++;
        int[][] board = game.getGame(0);
        _map= new Map(board);
        Index2D pacmanPos = parsePosition(game.getPos(0));
        GhostCL[] ghosts = game.getGhosts(0);
      int blackColour = Game.getIntColor(Color.BLACK,0);

      GhostCL dangerGhost = findCloseDangerG (pacmanPos,ghosts);
        GhostCL eatableGhost = findCloseEatG (pacmanPos, ghosts);

        if(dangerGhost!=null && eatableGhost==null) {
          return escapeFromGhost (pacmanPos,dangerGhost,blackColour);
      }

        if (eatableGhost != null){
            return chaseGhost (pacmanPos, eatableGhost);
        }
        Index2D power =findNearPowerUP (pacmanPos,board);
        if (power != null){
            return moveTowardTarget (pacmanPos,power);
        }

        Index2D food= findNearFood (pacmanPos,board);
        if (food != null){
            return moveTowardTarget (pacmanPos,food);
        }

        return randomDir(pacmanPos);
	}

    private static void printBoard(int[][] b) {
		for(int y =0;y<b[0].length;y++){
			for(int x =0;x<b.length;x++){
				int v = b[x][y];
				System.out.print(v+"\t");
			}
			System.out.println();
		}
	}
	private static void printGhosts(GhostCL[] gs) {
		for(int i=0;i<gs.length;i++){
			GhostCL g = gs[i];
			System.out.println(i+") status: "+g.getStatus()+",  type: "+g.getType()+",  pos: "+g.getPos(0)+",  time: "+g.remainTimeAsEatable(0));
		}
	}

    private int randomDir(Index2D pacmanPos) {
        int black = Game.getIntColor(Color.BLACK, 0);
        int[] validDirs = new int[4];
        int count = 0;
        for (int dir : DIRECTIONS) {
            if (isValidMove(getNextPosition(pacmanPos, dir), black)) {
                validDirs[count++] = dir;
            }
        }
        if (count == 0) return Game.STAY;
        return validDirs[(int)(Math.random() * count)];
    }


    private Index2D parsePosition(String pos) {
        String[] parts = pos.split(",");
        int x = Integer.parseInt(parts[0].trim());
        int y = Integer.parseInt(parts[1].trim());
        return new Index2D(x,y);
    }

    // finds danger ghost do not eat, find the closest danger ghost
    private GhostCL findCloseDangerG (Index2D pacmanPos, GhostCL[] ghosts) {
        GhostCL closest = null;
        double minDist = 2.0; // only if its 2 spaces close
        for(int i=0;i<ghosts.length;i++){
            GhostCL g = ghosts[i];
            if (g.remainTimeAsEatable(0)<= 0){
                Index2D ghostPos = parsePosition(g.getPos(0));
                double distance = pacmanPos.distance2D(ghostPos);
                if (distance < minDist) {
                    minDist = distance;
                    closest = g;
                }
            }
        }
        return closest;
    }

    // checks all directions, check the distance between all ghosts, choose the direction with max dist
    private int escapeFromGhost (Index2D pacmanPos, GhostCL dangerGhost, int blackColour) {
        Index2D ghostPos = parsePosition(dangerGhost.getPos(0));

        int bestDir= Game.STAY;
        double maxDist = -1.0;
        for(int i=0;i<DIRECTIONS.length;i++){
            int dir = DIRECTIONS[i];
            Index2D nextPos = getNextPosition(pacmanPos, dir);
            if (isValidMove(nextPos,blackColour)){
            double distance = nextPos.distance2D(ghostPos);
            if (distance > maxDist) {
                maxDist = distance;
                bestDir = dir;
            }
            }
        }
        if (bestDir == Game.STAY) {
            return randomDir(pacmanPos);
        }
        return bestDir;
    }

    // checks where we will be after we move
    private Index2D getNextPosition(Index2D pacmanPos, int dir) {
        int x = pacmanPos.getX();
        int y = pacmanPos.getY();
        if (dir==Game.UP) {
            y= y-1;
        }
      else if (dir==Game.DOWN) {
            y= y+1;
        }
        else if (dir==Game.LEFT) {
            x= x-1;
        }
        else if (dir==Game.RIGHT) {
            x= x+1;
        }
        return new Index2D(x,y);

    }


  private GhostCL findCloseEatG (Index2D pacmanPos, GhostCL[] ghosts) {
        GhostCL closest = null;
        double minDist = Double.MAX_VALUE;
        for(int i=0;i<ghosts.length;i++){
            GhostCL g = ghosts[i];
            double eatableTime = g.remainTimeAsEatable(0);
            if  (eatableTime> 20) {
                Index2D ghostPos = parsePosition(g.getPos(0));
                double distance = pacmanPos.distance2D(ghostPos);
                if (distance < minDist) {
                    minDist = distance;
                    closest = g;
                }
            }
        }
        return closest;
  }

private int chaseGhost (Index2D pacmanPos, GhostCL eatableGhost) {
        Index2D ghostPos = parsePosition(eatableGhost.getPos(0));
        int bestDir= Game.STAY;
        double minDist = Double.MAX_VALUE;
    int black = Game.getIntColor(Color.BLACK, 0);
        for(int i=0;i<DIRECTIONS.length;i++){
            int dir = DIRECTIONS[i];
            Index2D nextPos = getNextPosition(pacmanPos, dir);
            if (!isValidMove(nextPos, black)) continue;
            double distance = nextPos.distance2D(ghostPos);
            if (distance < minDist) {
                minDist = distance;
                bestDir = dir;
            }
        }
    if (bestDir == Game.STAY) {
        return randomDir(pacmanPos);
    }
        return bestDir;
}

private Index2D findNearPowerUP (Index2D pacmanPos, int[][] board) {
        int greenColour = Game.getIntColor(Color.GREEN,0);
        Index2D nearest = null;
        double minDist = Double.MAX_VALUE;
        for(int x=0;x<board.length;x++){
            for(int y=0;y<board[0].length;y++){
                if (board[x][y]== greenColour) {
                    Index2D powerPos = new Index2D (x,y);
                    double distance = pacmanPos.distance2D(powerPos);
                    if (distance < minDist) {
                        minDist = distance;
                        nearest = powerPos;
                    }
                }
            }
        }
    return nearest;

    }

private int moveTowardTarget (Index2D pacmanPos, Index2D target) {
        int bestDir= Game.STAY;
        double minDist = Double.MAX_VALUE;
    int black = Game.getIntColor(Color.BLACK, 0);
        for(int i=0;i<DIRECTIONS.length;i++){
            int dir = DIRECTIONS[i];
            Index2D nextPos = getNextPosition(pacmanPos, dir);
            System.out.println("Trying dir " + dir + " -> " + nextPos);
            if (!isValidMove(nextPos, black)) continue;
            double distance = nextPos.distance2D(target);
            if (distance < minDist) {
                minDist = distance;
                bestDir = dir;
            }
        }
    if (bestDir == Game.STAY) {
        return randomDir(pacmanPos);
    }
        return bestDir;
}



private Index2D findNearFood  (Index2D pacmanPos, int[][] board) {
        int pinkColour = Game.getIntColor(Color.PINK,0);
        Index2D nearest = null;
        double minDist = Double.MAX_VALUE;
        for (int x=0;x<board.length;x++){
            for (int y=0;y<board[0].length;y++){
                if (board[x][y]== pinkColour) {
                    Index2D foodPos = new Index2D (x,y);
                    double distance = pacmanPos.distance2D(foodPos);
                    if (distance < minDist) {
                        minDist = distance;
                        nearest = foodPos;
                    }
                }
            }
        }
        return nearest;

}
    private boolean isValidMove(Index2D pos, int blackColour) {
        if (!_map.isInside(pos)) {
            return false;
        }
        if (_map.getPixel(pos) == blackColour) {
            return false;
        }
        return true;
    }

}