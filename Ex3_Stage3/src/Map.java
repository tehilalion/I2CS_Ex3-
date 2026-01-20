import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * This class represents a 2D map as a "screen" or a raster matrix or maze over integers.
 * @author boaz.benmoshe
 */
public class Map implements Map2D {
    private int[][] _map;
    private boolean _cyclicFlag = true;

    public Map(int w, int h, int v) { init(w, h, v); }
    public Map(int size) { this(size, size, 0); }
    public Map(int[][] data) { init(data); }

    @Override
    public void init(int w, int h, int v) {
        if (w <= 0 || h <= 0) throw new RuntimeException("Illegal map size");
        _map = new int[w][h];
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                _map[x][y] = v;
            }
        }
    }

    @Override
    public void init(int[][] arr) {
        if (arr == null || arr.length == 0 || arr[0] == null || arr[0].length == 0) {
            throw new RuntimeException("Illegal array");
        }
        int w = arr.length;
        int h = arr[0].length;
        for (int x = 0; x < w; x++) {
            if (arr[x] == null || arr[x].length != h) {
                throw new RuntimeException("Ragged array / illegal array");
            }
        }
        _map = deepCopy(arr);
    }

    @Override
    public int[][] getMap() {
        return deepCopy(_map);
    }

    @Override
    public int getWidth() {
        return _map.length;
    }

    @Override
    public int getHeight() {
        return _map.length == 0 ? 0 : _map[0].length;
    }

    @Override
    public int getPixel(int x, int y) {
        return _map[x][y];
    }

    @Override
    public int getPixel(Pixel2D p) {
        if (p == null) throw new RuntimeException("p is null");
        return getPixel(p.getX(), p.getY());
    }

    @Override
    public void setPixel(int x, int y, int v) {
        _map[x][y] = v;
    }

    @Override
    public void setPixel(Pixel2D p, int v) {
        if (p == null) throw new RuntimeException("p is null");
        setPixel(p.getX(), p.getY(), v);
    }

    @Override
    public boolean isInside(Pixel2D p) {
        if (p == null) return false;
        int x = p.getX(), y = p.getY();
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
    }

    @Override
    public boolean isCyclic() {
        return _cyclicFlag;
    }

    @Override
    public void setCyclic(boolean cy) {
        _cyclicFlag = cy;
    }

    @Override
    public int fill(Pixel2D xy, int new_v) {
        if (xy == null) throw new RuntimeException("xy is null");
        if (!isInside(xy)) return 0;

        int old = getPixel(xy);
        if (old == new_v) return 0;

        boolean[][] vis = new boolean[getWidth()][getHeight()];
        ArrayDeque<Index2D> q = new ArrayDeque<>();
        q.add(new Index2D(xy));
        vis[xy.getX()][xy.getY()] = true;

        int count = 0;

        while (!q.isEmpty()) {
            Index2D cur = q.poll();
            int x = cur.getX(), y = cur.getY();

            if (_map[x][y] != old) continue;
            _map[x][y] = new_v;
            count++;

            for (Index2D nb : neighbors(x, y)) {
                int nx = nb.getX(), ny = nb.getY();
                if (!vis[nx][ny] && _map[nx][ny] == old) {
                    vis[nx][ny] = true;
                    q.add(nb);
                }
            }
        }
        return count;
    }

    @Override
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
                    if (_cyclicFlag) {
                        nX = (nX + getWidth()) % getWidth();
                        nY = (nY + getHeight()) % getHeight();
                    }
                    Pixel2D po = new Index2D(nX, nY);
                    if (isInside(po) && distMap.getPixel(nX, nY) == i - 1) {
                        temp = new Index2D(nX, nY);
                        break;
                    }
                }

            }
        }
        return shortest;
    }

    @Override
    public Map2D allDistance(Pixel2D start, int obsColor) {
        Map2D distMap = new Map(this.getWidth(),this.getHeight(),-1);
        if (start == null) {
            throw new RuntimeException("invalid start");
        }
        distMap.setPixel(start.getX(), start.getY(), 0);
        ArrayList<Pixel2D> q= new ArrayList<Pixel2D>();
        q.add(start);
        int [] dx={1,-1,0,0}; //changes in x
        int [] dy= {0,0,1,-1};// changes in y

        while (!q.isEmpty()){
            Pixel2D c =q.remove(0);
            int currentDis= distMap.getPixel(c.getX(), c.getY());

            for (int i=0;i< dx.length;i=i+1) {
                int nX = c.getX() + dx[i];
                int nY = c.getY() + dy[i];
                if (_cyclicFlag) {
                    nX = (nX + getWidth()) % getWidth();
                    nY = (nY + getHeight()) % getHeight();
                }
                Pixel2D po = new Index2D(nX, nY);
                if (isInside(po)) {
                    if (this.getPixel(nX, nY) != obsColor && distMap.getPixel(nX, nY) == -1) {
                        distMap.setPixel(nX, nY, currentDis+1);
                        q.add(new Index2D(nX, nY));

                    }
                }
            }

        }
        return distMap;
    }

    /* =================== helpers =================== */

    private Pixel2D[] buildPath(Index2D[][] parent, int sx, int sy, int tx, int ty) {
        ArrayList<Pixel2D> rev = new ArrayList<>();
        int cx = tx, cy = ty;
        rev.add(new Index2D(cx, cy));

        while (!(cx == sx && cy == sy)) {
            Index2D p = parent[cx][cy];
            if (p == null) return null;
            cx = p.getX();
            cy = p.getY();
            rev.add(new Index2D(cx, cy));
        }

        Pixel2D[] path = new Pixel2D[rev.size()];
        for (int i = 0; i < rev.size(); i++) {
            path[i] = rev.get(rev.size() - 1 - i);
        }
        return path;
    }

    private ArrayList<Index2D> neighbors(int x, int y) {
        ArrayList<Index2D> ans = new ArrayList<>(4);

        int lx = x - 1, rx = x + 1, uy = y - 1, dy = y + 1;

        if (_cyclicFlag) {
            lx = wrap(lx, getWidth());
            rx = wrap(rx, getWidth());
            uy = wrap(uy, getHeight());
            dy = wrap(dy, getHeight());

            ans.add(new Index2D(lx, y));
            ans.add(new Index2D(rx, y));
            ans.add(new Index2D(x, uy));
            ans.add(new Index2D(x, dy));
        } else {
            if (lx >= 0) ans.add(new Index2D(lx, y));
            if (rx < getWidth()) ans.add(new Index2D(rx, y));
            if (uy >= 0) ans.add(new Index2D(x, uy));
            if (dy < getHeight()) ans.add(new Index2D(x, dy));
        }

        return ans;
    }

    private static int wrap(int v, int mod) {
        int r = v % mod;
        if (r < 0) r += mod;
        return r;
    }

    private static int[][] deepCopy(int[][] a) {
        if (a == null) return null;
        int[][] c = new int[a.length][];
        for (int i = 0; i < a.length; i++) {
            c[i] = new int[a[i].length];
            System.arraycopy(a[i], 0, c[i], 0, a[i].length);
        }
        return c;
    }
}