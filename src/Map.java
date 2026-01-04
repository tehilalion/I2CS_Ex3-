import java.util.ArrayList;

/**
 * This class represents a 2D map as a "screen" or a raster matrix or maze over integers.
 * @author boaz.benmoshe
 *
 */
public class Map implements Map2D {
	private int[][] map;
	private boolean _cyclicFlag = true;
	
	/**
	 * Constructs a w*h 2D raster map with an init value v.
	 * @param w
	 * @param h
	 * @param v
	 */
	public Map(int w, int h, int v) {init(w,h, v);}
	/**
	 * Constructs a square map (size*size).
	 * @param size
	 */
	public Map(int size) {this(size,size, 0);}
	
	/**
	 * Constructs a map from a given 2D array.
	 * @param data
	 */
	public Map(int[][] data) {
		init(data);
	}
	@Override
	public void init(int w, int h, int v) {
        if (w<=0  || h<=0)
            throw new RuntimeException("Invalid width or height");
        map= new int [h][w];
        for (int i=0; i<h; i=i+1) {
            for (int j=0; j<w; j=j+1) {
                map[i][j]=v;
            }
        }
	}
	@Override
	public void init(int[][] arr) {
        if   (arr==null || arr.length==0)
            throw new RuntimeException("Invalid input array");
        int h = arr.length;
        int c= arr[0].length;
        if (c==0)
            throw new RuntimeException("Invalid Row Input");

        for (int i=1; i<h; i=i+1) {
            if (arr[i].length != c)
                throw new RuntimeException("Ragged array");
        }
        map = new int[h][c];
        for (int y = 0; y < h; y++){
            for (int x = 0; x < c; x++) {
                map[y][x] = arr[y][x];
            }
        }
	}
	@Override
	public int[][] getMap() {
        if (map==null) return null;
        int h= map.length;
        int w= map[0].length;
        int [][]copy= new int [h][w];
        for (int i=0; i<h; i=i+1) {
            for (int j=0; j<w; j=j+1) {
                copy [i][j]= map [i][j];
            }
        }
        return copy;
	}
	@Override
	public int getWidth() {return  map[0].length;}

	@Override
	public int getHeight() { return map.length;}

	@Override
	public int getPixel(int x, int y) {
        int ans = map[y][x];
        return ans;}

	@Override
	public int getPixel(Pixel2D p) {
        int ans = map[p.getY()][p.getX()];
        return ans;
	}

    @Override
	public void setPixel(int x, int y, int v) { map[y][x] = v;}

	@Override
	public void setPixel(Pixel2D p, int v) {
        map[p.getY()][p.getX()] = v;
	}

	@Override
	/** 
	 * Fills this map with the new color (new_v) starting from p.
	 * https://en.wikipedia.org/wiki/Flood_fill
	 */
	public int fill(Pixel2D xy, int new_v) {
        int old_v = getPixel(xy);
        if (old_v == new_v){
            return 0;}
        ArrayList<Pixel2D> q= new ArrayList<>();
        q.add(xy);
        setPixel(xy, new_v);
        int count= 0;
        int [][] dir= {{1,0},{-1,0},{0,1},{0,-1}};
        while (!q.isEmpty()){
            Pixel2D c=q.remove(0);
            setPixel(c,new_v);
            count++;
            for (int []step:dir){
                int nx = c.getX() + step[0];
                int ny = c.getY() + step[1];

                if (isInside(nx,ny) && getPixel(nx, ny) == old_v) {
                    setPixel(nx, ny, new_v);
                    q.add(new Index2D(nx, ny));
                }

            }
        }
        return count;
	}

	@Override
	/**
	 * BFS like shortest the computation based on iterative raster implementation of BFS, see:
	 * https://en.wikipedia.org/wiki/Breadth-first_search
	 */
	public Pixel2D[] shortestPath(Pixel2D p1, Pixel2D p2, int obsColor) {
        Map2D distMap= allDistance(p1,obsColor);
        int dist = distMap.getPixel(p2.getX(), p2.getY());
        if (dist == -1) return null;
        Pixel2D[] shortest = new Pixel2D[dist+1];
        Pixel2D temp=p2;
        int[] dx={1,-1,0,0};
        int[] dy={0,0,1,-1};
        for (int i=dist; i>=0; i--){
            shortest[i]= temp;
            if (i>0) {
                for (int j = 0; j < dx.length; j++) {
                    int nX = temp.getX() + dx[j];
                    int nY = temp.getY() + dy[j];

                    if (isInside(nX,nY) && distMap.getPixel(nX, nY) == i - 1) {
                        temp = new Index2D(nX, nY);
                        break;
                    }
                }

            }
        }
        return shortest;

    }
	@Override
	public boolean isInside(Pixel2D p) {
        boolean ans = false;
        if (p.getY()>=0 && p.getX()>=0
                && p.getY()<map.length && p.getX()<map[0].length ){
            ans = true;
        }
        return ans;
	}

	@Override
	/////// add your code below ///////
	public boolean isCyclic() {
		return false;
	}
	@Override
	/////// add your code below ///////
	public void setCyclic(boolean cy) {;}
	@Override
	/////// add your code below ///////
	public Map2D allDistance(Pixel2D start, int obsColor) {
		Map2D ans = null;  // the result.
		/////// add your code below ///////

		///////////////////////////////////
		return ans;
	}
}
