import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class Ex3AlgoTest {

    @Test
    public void testParsePosition() {
        Ex3Algo algo = new Ex3Algo();
        Index2D p = algoTestParse(algo, "  (3,4) ");

        assertEquals(3, p.getX());
        assertEquals(4, p.getY());
    }

    // helper because parsePosition is private
    private Index2D algoTestParse(Ex3Algo a, String s) {
        try {
            var m = Ex3Algo.class.getDeclaredMethod("parsePosition", String.class);
            m.setAccessible(true);
            return (Index2D) m.invoke(a, s);
        } catch (Exception e) {
            fail("Reflection failed");
            return null;
        }
    }
}
