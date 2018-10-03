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

public class AndDirective extends Directive {

	public String getName() {
		return "and";
	}

	public int getType() {
		return LINE;
	}

	public boolean render(InternalContextAdapter context, Writer writer,Node node) throws IOException, ResourceNotFoundException,ParseErrorException, MethodInvocationException {
		Integer region=(Integer)context.get("_currentRegion");
		@SuppressWarnings("unchecked")
		List<Integer> andRegions=(List<Integer>)context.get("_andRegions");
		if (andRegions.contains(region))
			writer.write(" and ");
		else
			andRegions.add(region);
		return true;

	}

}