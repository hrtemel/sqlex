package com.hrtit.sqlex.directives;

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.ASTBlock;
import org.apache.velocity.runtime.parser.node.Node;


public class HasCountRegionDirective extends Directive {
    
	public String getName() {
        return "region";
    }

    public int getType() {
        return BLOCK;
    }
        
	public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
    	String name=null;
    	if (node.jjtGetNumChildren()>0&& node.jjtGetChild(0) != null &&!(node.jjtGetChild(0) instanceof ASTBlock))
    		name=String.valueOf(node.jjtGetChild(0).value(context));
    	if (name==null)
    		throw new RuntimeException("Region name is required");
    	name=name.toLowerCase(Locale.ENGLISH);
    	if ("count".equals(name))
    		context.put("_hasCount", true);
    	return true;
    }    

}