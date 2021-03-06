package com.tinkerpop.gremlin.functions.g.var;

import com.tinkerpop.blueprints.pgm.Element;
import com.tinkerpop.gremlin.functions.Function;
import com.tinkerpop.gremlin.functions.FunctionHelper;
import com.tinkerpop.gremlin.functions.g.GremlinFunctionLibrary;
import com.tinkerpop.gremlin.statements.EvaluationException;
import org.apache.commons.jxpath.ExpressionContext;

import java.util.List;
import java.util.Map;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class AssignFunction implements Function {

    public static final String FUNCTION_NAME = "assign";

    public Object invoke(final ExpressionContext context, final Object[] parameters) {

        Object[] objects = FunctionHelper.nodeSetConversion(parameters);

        if (null != objects) {
            if (objects.length == 1 && objects[0] instanceof String) {
                // ../..[g:assign('$i')]
                if (FunctionHelper.isLastInContext(context))
                    FunctionHelper.getGremlin(context).getVariables().declareVariable((String) objects[0], FunctionHelper.asObject(context.getContextNodeList()));
                return Boolean.TRUE;
            } else if (objects.length == 2) {
                if (objects[0] instanceof String) {
                    // g:assign('$i', value)
                    FunctionHelper.getGremlin(context).getVariables().declareVariable((String) objects[0], objects[1]);
                    return objects[1];
                }
            } else if (objects.length == 3) {
                if (objects[0] instanceof Map && !(objects[1] instanceof List || objects[1] instanceof Map) && !(objects[2] instanceof List || objects[2] instanceof Map)) {
                    // g:assign(map,key,value)
                    return setMapKey((Map) objects[0], objects[1], objects[2]);
                } else if (objects[0] instanceof Element && objects[1] instanceof String) {
                    // g:assign(element,key,value)
                    return setElementKey((Element) objects[0], (String) objects[1], objects[2]);

                } else if (objects[0] instanceof List && objects[1] instanceof Number && !(objects[2] instanceof List)) {
                    // g:assign(list,index,value)
                    return setListIndex((List) objects[0], ((Number) objects[1]).intValue() - 1, objects[2]);
                }
            }
        }

        throw EvaluationException.createException(FunctionHelper.makeFunctionName(GremlinFunctionLibrary.NAMESPACE_PREFIX, FUNCTION_NAME), EvaluationException.EvaluationErrorType.UNSUPPORTED_PARAMETERS);
    }

    public String getName() {
        return FUNCTION_NAME;
    }


    private static Object setMapKey(final Map map, final Object key, final Object value) {
        if (value instanceof List || value instanceof Map)
            throw EvaluationException.createException(FunctionHelper.makeFunctionName(GremlinFunctionLibrary.NAMESPACE_PREFIX, FUNCTION_NAME), EvaluationException.EvaluationErrorType.EMBEDDED_COLLECTIONS);

        map.put(key, value);
        return value;
    }

    private static Object setElementKey(final Element element, final String key, final Object value) {
        if (value instanceof List || value instanceof Map)
            throw EvaluationException.createException(FunctionHelper.makeFunctionName(GremlinFunctionLibrary.NAMESPACE_PREFIX, FUNCTION_NAME), EvaluationException.EvaluationErrorType.EMBEDDED_COLLECTIONS);

        element.setProperty(key, value);
        return value;
    }


    private static Object setListIndex(final List list, final Integer index, final Object value) {
        if (list.size() < index + 1)
            throw EvaluationException.createException(FunctionHelper.makeFunctionName(GremlinFunctionLibrary.NAMESPACE_PREFIX, FUNCTION_NAME), EvaluationException.EvaluationErrorType.INDEX_BOUNDS);

        if (value instanceof List || value instanceof Map)
            throw EvaluationException.createException(FunctionHelper.makeFunctionName(GremlinFunctionLibrary.NAMESPACE_PREFIX, FUNCTION_NAME), EvaluationException.EvaluationErrorType.EMBEDDED_COLLECTIONS);

        list.set(index, value);
        return value;
    }

}