package com.hrtit.sqlex.directives;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Locale;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.ASTBlock;
import org.apache.velocity.runtime.parser.node.Node;

import com.hrtit.sqlex.SqlexDialect;

public class RegionDirective extends Directive {

    public String getName() {
        return "region";
    }

    public int getType() {
        return BLOCK;
    }

    private String getBlockContent(InternalContextAdapter context, Node node) throws ResourceNotFoundException, ParseErrorException, MethodInvocationException, IOException{
    	StringWriter blockContent = new StringWriter();
    	for(int i=0; i<node.jjtGetNumChildren(); i++) {
            if (node.jjtGetChild(i) != null &&(node.jjtGetChild(i) instanceof ASTBlock)){
                node.jjtGetChild(i).render(context, blockContent);
            }
        }
    	return blockContent.toString();
    }
    
    @SuppressWarnings("unchecked")
	public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
    	Integer oldRegion=(Integer)context.get("_currentRegion");
    	context.put("_currentRegion", this.hashCode());
    	String name=null;
    	if (node.jjtGetNumChildren()>0&& node.jjtGetChild(0) != null &&!(node.jjtGetChild(0) instanceof ASTBlock))
    		name=String.valueOf(node.jjtGetChild(0).value(context));
    	if (name==null)
    		throw new RuntimeException("Region name is required");
    	name=name.toLowerCase(Locale.ENGLISH);
    	if ("data".equals(name)){
    		if (!context.containsKey("_count"))
    			writer.write(getBlockContent(context, node));
    	}else if ("count".equals(name)){
    		if (context.containsKey("_count"))
    			writer.write(getBlockContent(context, node));
    	}else if ("where".equals(name)){
    		boolean append=false;
    		if (node.jjtGetNumChildren()>1&& node.jjtGetChild(1) != null &&!(node.jjtGetChild(1) instanceof ASTBlock))
        		append=(Boolean)node.jjtGetChild(1).value(context);
        	if (append)
        		((List<Integer>)(context.get("_andRegions"))).add(this.hashCode());
    		String res=getBlockContent(context, node).trim();
			if (!res.isEmpty())
				writer.write(" where "+res);
    	}else if ("orderby".equals(name)){
    		if(!context.containsKey("_count")){
				String res=null;
				if (context.containsKey("_orderBy")&& context.get("_orderBy")!=null)
					res=(String)context.get("_orderBy");
				else
					res=getBlockContent(context, node);
				if (res!=null && !res.trim().isEmpty())
					writer.write(" order by "+res);
    		}
    	}else if ("paging".equals(name)){
    		if (context.containsKey("_count")){
    			context.put("_inside_paging", getBlockContent(context, node));
    		}else{
	    		if (context.containsKey("_apply_paging")){
		    		writer.write(((SqlexDialect)context.get("e")).applyPaging(getBlockContent(context, node)));
		    		context.put("_paging_applied",true);
	    		}else
	    			writer.write(getBlockContent(context, node));
    		}
    	}
    	context.put("_currentRegion", oldRegion);
    	return true;
    }
    
    

}