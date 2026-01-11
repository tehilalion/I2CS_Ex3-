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
			//int up = Game.UP, left = Game.LEFT, down = Game.DOWN, right = Game.RIGHT;
		}
		_count++;
        // יצירת לוח
        int[][] board = game.getGame(0);
        _map= new Map(board);

        Index2D pacmanPos = parsePosition(game.getPos(0));
        GhostCL[] ghosts = game.getGhosts(0);
      int blackColour = Game.getIntColor(Color.BLACK,0);

// run from closest ghost
      GhostCL dangerGhost = findCloseDangerG (pacmanPos,ghosts);
      // found danger, run
      if(dangerGhost!=null) {
          return escapeFromGhost (pacmanPos,dangerGhost);
      }
      // no danger? cont rando
		int dir = randomDir();
		return dir;
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
	private static int randomDir() {
		int[] dirs = {Game.UP, Game.LEFT, Game.DOWN, Game.RIGHT};
		int ind = (int)(Math.random()*dirs.length);
		return dirs[ind];
	}

    private Index2D parsePosition(String pos) {
        String[] parts = pos.split(",");
        int x = Integer.parseInt(parts[0].trim());
        int y = Integer.parseInt(parts[1].trim());
        return new Index2D(x,y);
    }
    // finds danger ghost do not eat, find closest danger ghost
    private GhostCL findCloseDangerG (Index2D pacmanPos, GhostCL[] ghosts) {
        GhostCL closest = null;
        double minDist = 4.0; // only if its 4 spaces close

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
    private int escapeFromGhost (Index2D pacmanPos, GhostCL dangerGhost) {
        Index2D ghostPos = parsePosition(dangerGhost.getPos(0));
        int[] directions= {Game.UP, Game.DOWN, Game.LEFT, Game.RIGHT};
        int bestDir= Game.STAY;
        double maxDist = -1.0;
        for(int i=0;i<directions.length;i++){
            int dir = directions[i];
            Index2D nextPos = getNextPosition(pacmanPos, dir);
            double distance = nextPos.distance2D(ghostPos);
            if (distance > maxDist) {
                maxDist = distance;
                bestDir = dir;
            }
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





}