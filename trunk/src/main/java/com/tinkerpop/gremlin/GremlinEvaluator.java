package com.tinkerpop.gremlin;

import com.tinkerpop.gremlin.lang.*;
import org.apache.commons.jxpath.JXPathInvalidAccessException;
import org.apache.commons.jxpath.JXPathInvalidSyntaxException;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 * @version 0.1
 */
public class GremlinEvaluator {

    private Statement currentStatement;
    private XPathEvaluator xPathEvaluator;

    public GremlinEvaluator() {
        this.xPathEvaluator = new XPathEvaluator();
    }

    public List evaluate(String line) throws JXPathInvalidSyntaxException, JXPathInvalidAccessException {
        line = line.trim();

        try {
            if (null == this.currentStatement) {
                if (ForeachStatement.isStatement(line)) {
                    this.currentStatement = new ForeachStatement(this.xPathEvaluator);
                    this.currentStatement.compileTokens(line);
                } else if (AssignmentStatement.isStatement(line)) {
                    AssignmentStatement assignmentStatement = new AssignmentStatement(this.xPathEvaluator);
                    assignmentStatement.compileTokens(line);
                    return assignmentStatement.evaluate();
                } else {
                    XPathStatement xPathStatement = new XPathStatement(this.xPathEvaluator);
                    xPathStatement.compileTokens(line);
                    return xPathStatement.evaluate();
                }
            } else {
                if (this.currentStatement.compileTokens(line)) {
                    List results = this.currentStatement.evaluate();
                    this.currentStatement = null;
                    return results;
                }
            }
        } catch (SyntaxErrorException e) {
            this.currentStatement = null;
        }
        return new ArrayList();
    }
}