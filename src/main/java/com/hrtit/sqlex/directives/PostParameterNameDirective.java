package com.hrtit.sqlex.directives;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;

public class PostParameterNameDirective extends Directive {

    public String getName() {
        return "p";
    }

    public int getType() {
        return LINE;
    }

    @SuppressWarnings("unchecked")
	public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
     	String variable=null;
     	if (node.jjtGetNumChildren()>0 &&node.jjtGetChild(0) != null)
    		variable=String.valueOf(node.jjtGetChild(0).value(context));
    	if (variable!=null &&!variable.isEmpty()&& !((List<String>)context.get("_params")).contains(variable))
            ((List<String>)context.get("_params")).add(variable);
        return true;        

    }
    
    

}