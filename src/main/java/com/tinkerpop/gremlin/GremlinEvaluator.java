package com.tinkerpop.gremlin;

import com.tinkerpop.gremlin.statements.*;

import java.io.*;
import java.util.List;


/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class GremlinEvaluator {

    private Statement currentStatement;
    private XPathEvaluator xPathEvaluator;
    public static final String GREMLIN_RC_FILE = ".gremlinrc";

    public GremlinEvaluator() {
        this.xPathEvaluator = new XPathEvaluator();
        this.currentStatement = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(GREMLIN_RC_FILE);
            this.evaluate(fileInputStream);
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            throw new EvaluationException("Evaluation error: " + e.getMessage());
        }

    }

    public List evaluate(String line) throws SyntaxException, EvaluationException {
        line = line.trim();
        if (line.length() > 0) {
            try {
                if (null == this.currentStatement) {
                    this.currentStatement = StatementGenerator.generateStatement(line, xPathEvaluator);
                    if (this.currentStatement instanceof ReflectiveStatement) {
                        ((ReflectiveStatement) this.currentStatement).setGremlinEvaluator(this);
                    }
                    this.currentStatement.compileTokens(line);
                } else {
                    this.currentStatement.compileTokens(line);
                }

                if (this.currentStatement.isComplete()) {
                    Statement temp = this.currentStatement;
                    this.currentStatement = null;
                    return temp.evaluate();
                } else {
                    return null;
                }
            } catch (EvaluationException e) {
                this.currentStatement = null;
                throw e;
            } catch (SyntaxException e) {
                this.currentStatement = null;
                throw e;
            } catch (Exception e) {
                this.currentStatement = null;
                if (null != e.getMessage())
                    throw new EvaluationException(e.getMessage());
                else
                    throw new EvaluationException("An evaluation error has occurred");
            }
        }
        return null;
    }

    public List evaluate(final InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line;
        List result = null;

        try {
            while ((line = reader.readLine()) != null) {
                try {
                    this.xPathEvaluator.incrLineNumber();
                    result = this.evaluate(line);
                } catch (SyntaxException e) {
                    throw new SyntaxException(e.getMessage() + Tokens.AT_LINE + this.xPathEvaluator.getLastStatementLineNumber());
                } catch (EvaluationException e) {
                    throw new EvaluationException(e.getMessage() + Tokens.AT_LINE + this.xPathEvaluator.getLastStatementLineNumber());
                }
            }
        } catch (IOException e) {
            throw new EvaluationException(e.getMessage() + Tokens.AT_LINE + this.xPathEvaluator.getLastStatementLineNumber());
        }
        return result;
    }

    public VariableLibrary getVariables() {
        return this.xPathEvaluator.getVariables();
    }

    protected boolean inStatement() {
        return null != this.currentStatement;
    }

    protected int getDepth() {
        return this.xPathEvaluator.getDepth();
    }

    public int getLastStatementLineNumber() {
        return this.xPathEvaluator.getLastStatementLineNumber();
    }
}
