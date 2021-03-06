package com.tinkerpop.gremlin.functions.g.lme;

import com.tinkerpop.gremlin.functions.Function;
import com.tinkerpop.gremlin.functions.FunctionHelper;
import com.tinkerpop.gremlin.functions.g.GremlinFunctionLibrary;
import com.tinkerpop.gremlin.statements.EvaluationException;
import org.apache.commons.jxpath.ExpressionContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class ExceptFunction implements Function {

    public static final String FUNCTION_NAME = "except";

    public Boolean invoke(final ExpressionContext context, final Object[] parameters) {

        if (null != parameters) {
            Object[] objects = FunctionHelper.nodeSetConversion(parameters);
            Set setA = new HashSet();
            for (Object object : objects) {
                if (object instanceof List)
                    setA.addAll((List) object);
                else
                    setA.add(object);

            }
            /*Set setB = new HashSet();
            setB.add(context.getContextNodePointer().getValue());
            setB.removeAll(setA);
            return setB.size() > 0;*/
            return !setA.contains(context.getContextNodePointer().getValue());
        }
        throw EvaluationException.createException(FunctionHelper.makeFunctionName(GremlinFunctionLibrary.NAMESPACE_PREFIX, FUNCTION_NAME), EvaluationException.EvaluationErrorType.UNSUPPORTED_PARAMETERS);

    }

    public String getName() {
        return FUNCTION_NAME;
    }
}