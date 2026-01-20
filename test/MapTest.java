import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class MapTest {

    private int[][] _map_3_3;
    private Map2D _m0, _m1, _m3_3;

    @BeforeEach
    public void setup() {
        _map_3_3 = new int[][]{
                {0, 1, 0},
                {1, 0, 1},
                {0, 1, 0}
        };
        _m3_3 = new Map(_map_3_3);
        _m0 = new Map(_map_3_3);
        _m1 = new Map(20, 25, 16);
    }


    /**
     * Tests the init method with a large array.
     * Verifies that width/height are updated correctly and that fill works after init.
     */
    @Test
    @Timeout(value = 1, unit = SECONDS)
    void initLarge() {
        int[][] bigarr = new int[500][500];
        _m1.init(bigarr);

        // In this project: map[x][y] => width = arr.length, height = arr[0].length
        assertEquals(bigarr.length, _m1.getWidth());
        assertEquals(bigarr[0].length, _m1.getHeight());

        Pixel2D p1 = new Index2D(3, 2);
        _m1.fill(p1, 1); // your Map.fill has no cyclic parameter
    }


    /**
     * Tests the isInside method with points inside, on the edge, and outside.
     */
    @Test
    void testIsInside() {
        Map m = new Map(10, 10, 0);

        assertTrue(m.isInside(new Index2D(5, 5)));
        assertTrue(m.isInside(new Index2D(0, 0)));
        assertTrue(m.isInside(new Index2D(9, 9)));

        assertFalse(m.isInside(new Index2D(10, 5)));
        assertFalse(m.isInside(new Index2D(5, -1)));
        assertFalse(m.isInside(new Index2D(-1, 0)));
    }

    /**
     * Tests that getMap returns a deep copy and not a reference to internal array.
     */
    @Test
    void getMapDeepCopy() {
        Map m = new Map(2, 2, 5);
        int[][] copy = m.getMap();

        assertEquals(2, copy.length);
        assertEquals(2, copy[0].length);
        assertEquals(5, copy[0][0]);

        copy[0][0] = 9;
        assertEquals(5, m.getPixel(0, 0)); // original should not change
    }

    @Test
    void getWidth() {
        Map m = new Map(4, 7, 0);
        assertEquals(4, m.getWidth());
    }

    @Test
    void getHeight() {
        Map m = new Map(4, 7, 0);
        assertEquals(7, m.getHeight());
    }

    @Test
    void getPixelAfterSetPixelXY() {
        Map m = new Map(3, 3, 0);
        m.setPixel(1, 1, 9);
        assertEquals(9, m.getPixel(1, 1));
    }

    @Test
    void setPixelXY() {
        Map m = new Map(3, 3, 0);
        m.setPixel(1, 2, 7);
        assertEquals(7, m.getPixel(1, 2));
    }

    @Test
    void setPixelByPixel2D() {
        Map m = new Map(3, 3, 0);
        Pixel2D p = new Index2D(0, 1);
        m.setPixel(p, 4);
        assertEquals(4, m.getPixel(p));
    }

    /**
     * Tests the fill (flood fill) method.
     * Your Map.fill fills the connected component of the *same old value* starting at p.
     */
    @Test
    void fillFloodFillConnectedComponent() {
        Map m = new Map(4, 4, 1);
        m.setPixel(2, 2, 9); // isolate one cell with different value

        int filled = m.fill(new Index2D(0, 0), 3);

        assertEquals(15, filled);          // 16 cells total - 1 protected (value 9)
        assertEquals(9, m.getPixel(2, 2)); // unchanged
        assertEquals(3, m.getPixel(0, 0)); // changed
    }

    /**
     * Tests shortestPath with no obstacles in a 3x3 grid (cyclic=false for predictable path length).
     */
    @Test
    void shortestPathLengthNonCyclic() {
        Map m = new Map(3, 3, 0);
        m.setCyclic(false);

        int obs = 9; // none of the cells are 9, so no obstacles
        Pixel2D[] path = m.shortestPath(new Index2D(0, 0), new Index2D(2, 2), obs);

        assertNotNull(path);
        assertEquals(new Index2D(0, 0), path[0]);
        assertEquals(new Index2D(2, 2), path[path.length - 1]);
        assertEquals(5, path.length); // 4 moves -> 5 nodes
    }

    /**
     * Tests allDistance in non-cyclic mode: dist(1,1) from (0,0) should be 2.
     */
    @Test
    void allDistanceNonCyclic() {
        Map m = new Map(3, 3, 0);
        m.setCyclic(false);

        int obs = 9;
        Map2D dist = m.allDistance(new Index2D(0, 0), obs);

        assertEquals(0, dist.getPixel(0, 0));
        assertEquals(2, dist.getPixel(1, 1));
    }

    /**
     * Tests cyclic wrap: from (0,0) to (2,0) should be 1 step when cyclic=true.
     */
    @Test
    void allDistanceCyclicWrap() {
        Map m = new Map(3, 3, 0);
        m.setCyclic(true);

        int obs = 9;
        Map2D dist = m.allDistance(new Index2D(0, 0), obs);

        assertEquals(1, dist.getPixel(2, 0));
    }
}
