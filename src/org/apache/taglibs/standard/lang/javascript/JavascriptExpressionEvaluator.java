/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */ 

package org.apache.taglibs.standard.lang.javascript;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*; 
import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.Boolean;

import org.apache.taglibs.standard.lang.javascript.adapter.*;
import org.apache.taglibs.standard.lang.spel.Evaluator;
import org.mozilla.javascript.*;



/**
 * Implementation of an ExpressionLanguageEvaluator for Ecma-Script, based
 * on the open-source Java implementation of Ecma-Script, Rhino.
 *
 * @author <a href="mailto:horwat@apache.org">Justyna Horwat</a>
 * @author <a href="mailto:bayern@essentially.net">Shawn Bayern</a>
 * @author <a href="mailto:cmlenz@apache.org">Christopher Lenz</a>
 * @version $Revision$
 * @see <a href="http://www.mozilla.org/rhino/">Mozilla Rhino</a>
 */
public class JavascriptExpressionEvaluator
    extends Evaluator
    implements WrapHandler {

    // -------------------------------------------------------------- Constants

    /**
     * The name under which the global Rhino scope will be stored in the
     * application scope attributes.
     */
    private static final String JS_GLOBAL_SCOPE =
        "org.apache.taglibs.standard.lang.javascript.globalScope";

    /**
     * The name under which the scope representing the PageContext will be
     * stored in the page scope attributes.
     */
    private static final String JS_PAGE_SCOPE =
        "org.apache.taglibs.standard.lang.javascript.pageScope";

    // -------------------------------------------------------- Class Variables

    /**
     * Cache for compiled scripts.
     */
    private static Map cachedScripts =
        Collections.synchronizedMap(new HashMap());

    // ------------------------------------- ExpressionEvaluator Implementation


    /** 
     * Tries to compile the expression, and returns an appriopriate error
     * message if a syntax error is encountered. The compilation result will
     * be stored in the script-cache, so that it can be used when the page is
     * actually executed.
     *
     * @see org.apache.taglibs.standard.lang.support.ExpressionEvaluator#validate
     */
    public String validate(String attributeName, 
                           String expression) {
        String msg = null;

        if (expression.startsWith("$")) {
            expression = expression.substring(1);
            Context cx = Context.enter();
            try {
                getOrCompileScript(cx, expression);
            } catch (EvaluatorException ee) {
                msg = ee.getMessage();
            } finally {
                // exit the Rhino context (essential to let Rhino do some
                // cleanup)
                cx.exit();
            }
        }

        return msg;
    }

    /** 
     * Evaluates the expression using either Rhino (for expression starting
     * with '$', or the SPEL Evaluator for literals.
     *
     * @see org.apache.taglibs.standard.lang.support.ExpressionEvaluator#evaluate
     */
    public Object evaluate(String attributeName, 
                           String expression, 
                           Class expectedType, 
                           Tag tag, 
                           PageContext pageContext) 
                           throws JspException {

        Object result = null;

        // Creates and enters a Context. Context stores information
        // about the execution environment of a script
        Context cx = Context.enter();
        cx.setWrapHandler(this);

        try {
            if (expression.startsWith("$")) {
                expression = expression.substring(1);

                // use the Rhino interpreter to evaluate the expression
                Script script = getOrCompileScript(cx, expression);
                Scriptable scope = getOrCreateScope(cx, pageContext);
                result = script.exec(cx, scope);

                // Unwrap scoped object
                if (result instanceof Wrapper) {
                    result = ((Wrapper) result).unwrap();
                }

                if (result instanceof NativeString) {
                    result = result.toString();
                } else if (result instanceof NativeBoolean) {
                    result = new Boolean(result.toString());
                } else if (result instanceof Undefined) {
                    result = null;
                }
            }
            else {
                // use the SPEL evaluate literal method 'cuz why reinvent the
                // wheel?
                result = super.evaluateLiteral(expression, pageContext,
                                               expectedType);
            }

        } catch (NotAFunctionException nafe) {
            throw new JspException(nafe.getMessage(), nafe);
        } catch (PropertyException pe) {
            throw new JspException(pe.getMessage(), pe);
        } catch (JavaScriptException jse) {
            throw new JspException(jse.getMessage(), jse);
        } catch (EvaluatorException ee) {
            throw new JspException(ee.getMessage(), ee);
        } finally {
            // exit the Rhino context (essential to let Rhino do some cleanup)
            cx.exit();
        }

        //System.out.println("RHINO result: " + result + ":");

        if (result != null && !expectedType.isInstance(result)) {
            throw new JspException("The tag expected an object of type ["
                    + expectedType.getName() + "] for the " + attributeName
                    + " attribute.  However, it received an "
                    + "object of type [" + result.getClass().getName() + "]");
        }

        return result;
    }

    // --------------------------------------------- WrapHandler Implementation


    /**
     * Implementation of the org.mozilla.javascript.WrapHandler interface.
     * This method overrides the default wrapping of native Java objects in
     * JavaScript objects to provide custom adapters for Lists and Maps.
     * When this method returns <code>null</code>, Rhino will use the default
     * wrapping.
     *
     * @see org.mozilla.javascript#wrap(Scriptable,Object,Class)
     */
    public Object wrap(Scriptable scope,
                       Object obj,
                       Class staticType) {

        // only try to wrap non-null objects with no static type (i.e. null)
        //   (I don't really understand that last condition, but it does seem
        //    to apply in all the cases tested... Rhino documentation is a bit
        //    vague about this)
        if ((obj != null) && (staticType == null)) {
            if (obj instanceof List) {
               return new NativeJavaList(scope, (List)obj);
            } else if (obj instanceof Map) {
                return new NativeJavaMap(scope, (Map)obj);
            }
        }

        return null;
    }



    // -------------------------------------------------------- Private Methods


    /**
     * Creates and initializes (or retrieves) the scope for the execution of
     * a Rhino script
     *
     * @param rhinoContext  the Rhino Context
     * @param pageContext   the JSP PageContext
     * @return the scope for script execution
     * @throws JavaScriptException if an uncaught JavaScript exception
     *         occurred while creating an object
     * @throws NotAFunctionException thrown if call is attempted on an object
     *         that is not a function.
     * @throws PropertyException thrown if errors are detected while attempting
     *         to define a property of a host object from a Java class or
     *         method, or if a property is not found.
     */
    private Scriptable getOrCreateScope(Context rhinoContext,
                                        PageContext pageContext)
        throws JavaScriptException, NotAFunctionException, PropertyException {

        // If this is the first evaluation done by this evaluator, initialize
        // the global scope and store it as application attribute.
        Scriptable globalScope =
            (Scriptable)pageContext.getAttribute(
                JS_GLOBAL_SCOPE, PageContext.APPLICATION_SCOPE);
        if (globalScope == null) {
            globalScope = rhinoContext.initStandardObjects(null);
            pageContext.setAttribute(JS_GLOBAL_SCOPE, globalScope,
                                     PageContext.APPLICATION_SCOPE);
        }

        // Create a scope for the page-context (which introduces the special
        // objects 'cookies', 'headers', 'initParams' and 'params'), and put
        // it into the prototype chain.
        Scriptable pageScope =
            (Scriptable)pageContext.getAttribute(JS_PAGE_SCOPE);
        if (pageScope == null) {
            pageScope = new ScriptablePageContext(pageContext);
            pageScope.setPrototype(globalScope);
            pageScope.setParentScope(null);
            pageContext.setAttribute(JS_PAGE_SCOPE, pageScope,
                                     PageContext.PAGE_SCOPE);
        }

        // Now create the local scope for this evaluation.
        // For more information on local vs. shared scope, see
        //   <http://www.mozilla.org/rhino/scopes.html>
        Scriptable localScope =
            (Scriptable)rhinoContext.newObject(pageScope);
        localScope.setPrototype(pageScope);
        localScope.setParentScope(null);

        return localScope;
    }

    /**
     * Tries to retrieve the compiled script from the cache. If no cache entry
     * is found, the expression is compiled and put in the cache. If the
     * expression is syntactically incorrect, an EvaluatorException (which is
     * a RuntimeException derived class) will be thrown by this method.
     *
     * @param rhinoContext  the Rhino Context
     * @param expression    the expression to compile
     * @return the script (compiled representation of the expression)
     */
    private Script getOrCompileScript(Context rhinoContext,
                                      String expression) {

        Script script = (Script)cachedScripts.get(expression);
        if (script == null) {
            // if the script wasn't found in the cache, compile the
            // expression and put the script in the cache.
            // Context.compileReader() will (by default) throw an
            // EvaluatorException if the expression is syntactically incurrect.
            try {
                script =
                    rhinoContext.compileReader(null,
                                               new StringReader(expression),
                                               "", 0, null);
                cachedScripts.put(expression, script);
            } catch (IOException ioe) {
                // this should never happen, or could it ?
            }
        }
        
        return script;
    }
}
