package com.tinkerpop.gremlin.functions.g.lme;

import com.tinkerpop.gremlin.XPathEvaluator;
import junit.framework.TestCase;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class DifferenceFunctionTest extends TestCase {

    public void testDifferenceFunction() {
        XPathEvaluator xe = new XPathEvaluator();
        assertEquals(xe.evaluateList("g:difference(g:list(1,2,3,4),3,4)").size(), 2);
        assertTrue(xe.evaluateList("g:difference(g:list(1,2,3,4),3,4)").contains(1.0));
        assertTrue(xe.evaluateList("g:difference(g:list(1,2,3,4),3,4)").contains(2.0));
        assertEquals(xe.evaluateList("g:difference(g:list(1,2,3,4),g:list(1,2))").size(), 2);
        assertTrue(xe.evaluateList("g:difference(g:list(1,2,3,4),g:list(1,2))").contains(3.0));
        assertTrue(xe.evaluateList("g:difference(g:list(1,2,3,4),g:list(1,2))").contains(4.0));
        assertEquals(xe.evaluateList("g:difference(1,2)").size(), 1);
        assertEquals(xe.evaluateList("g:difference(1,1)").size(), 0);
    }
}
