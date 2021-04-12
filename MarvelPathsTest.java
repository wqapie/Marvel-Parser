package marvel.junitTests;

import graph.CampusGraph;
import marvel.MarvelParser;
import marvel.MarvelPaths;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Test the behaviors of MarvelPaths in special cases.
 */
public class MarvelPathsTest {

    private CampusGraph<String, String> cg;

    @Before
    public void setUp() {
        cg = MarvelParser.buildGraph("buildMultiGraphs1.tsv");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNullGraph() {
        MarvelPaths.findPath(null, "Robin", "Robin");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNullStart() {
        MarvelPaths.findPath(cg, null, "Robin");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNullEnd() {
        MarvelPaths.findPath(cg, "Robin", null);
    }

    @Test
    public void testValidInput() {
        List<CampusGraph.Edges<String, String>> empty = new LinkedList<>();
        assertTrue(empty.equals(MarvelPaths.findPath(cg, "Robin", "Robin")));
    }
}
