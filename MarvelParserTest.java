package marvel.junitTests;

import marvel.MarvelParser;
import org.junit.*;

/**
 * Test the behaviors of MarvelParser in special cases.
 */
public class MarvelParserTest {

    /**
     * Test when the input file does not exist in the right place, then
     * throw new IllegalArgumentException.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testFileNotFound() {
        MarvelParser.buildGraph("nonExist.tsv");
    }
}
