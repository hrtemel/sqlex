package com.hrtit.sqlex.directives;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.ASTBlock;
import org.apache.velocity.runtime.parser.node.Node;

public class NParameterDirective extends Directive {

    public String getName() {
        return "nparam";
    }

    public int getType() {
        return BLOCK;
    }

    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
     	String variable=null;
    	if (node.jjtGetNumChildren()>0 && node.jjtGetChild(0) != null)
    		variable=String.valueOf(node.jjtGetChild(0).value(context));
    	@SuppressWarnings("unchecked")
		Map<String,Object> paramValues=(Map<String,Object>)context.get("_paramValues");
    	if (paramValues==null|| paramValues.containsKey(variable))
    		return true;
    	StringWriter blockContent = new StringWriter();
    	for(int i=0; i<node.jjtGetNumChildren(); i++) {
            if (node.jjtGetChild(i) != null &&(node.jjtGetChild(i) instanceof ASTBlock)){
                node.jjtGetChild(i).render(context, blockContent);
            }
        }
        writer.write(blockContent.toString());
        return true;
    }
    
    

}