package com.hrtit.sqlex;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import com.hrtit.sqlex.directives.AndDirective;
import com.hrtit.sqlex.directives.HasCountRegionDirective;
import com.hrtit.sqlex.directives.NParameterDirective;
import com.hrtit.sqlex.directives.ParameterDirective;
import com.hrtit.sqlex.directives.PostParameterDirective;
import com.hrtit.sqlex.directives.PostParameterNameDirective;
import com.hrtit.sqlex.directives.RegionDirective;


public class SqlexProcessor {
	
	/**DB Specific code genreator*/
	private SqlexDialect dialect;
	
	/**PRE_PROCESSOR handle all directives expect #p and convert #param directives to #p directives*/
	private VelocityEngine PRE_PROCESSOR;
	
	/**POST_PROCESSOR handle only  #p directives*/
	private VelocityEngine POST_PROCESSOR;
	
	/**PARAM_NAME_PROCESSOR only find parameter names*/
	private VelocityEngine PARAM_NAME_PROCESSOR;
	
	/**HAS_COUNT_REGION_PROCESSOR only find is there any count region*/
	private VelocityEngine HAS_COUNT_REGION_PROCESSOR;
	
	
	public SqlexProcessor() {
		PRE_PROCESSOR=new VelocityEngine();
		PRE_PROCESSOR.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		PRE_PROCESSOR.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		PRE_PROCESSOR.setProperty("userdirective",
			RegionDirective.class.getCanonicalName()+","+
			ParameterDirective.class.getCanonicalName()+","+
			NParameterDirective.class.getCanonicalName()+","+
			AndDirective.class.getCanonicalName()
		);
		PRE_PROCESSOR.setProperty("runtime.log", "/tmp/velocity");
		PRE_PROCESSOR.init();
		
		POST_PROCESSOR=new VelocityEngine();
		POST_PROCESSOR.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
		POST_PROCESSOR.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		POST_PROCESSOR.setProperty("userdirective",PostParameterDirective.class.getCanonicalName());
		POST_PROCESSOR.setProperty("runtime.log", "/tmp/velocity");
		POST_PROCESSOR.init();

		PARAM_NAME_PROCESSOR=new VelocityEngine();
		PARAM_NAME_PROCESSOR.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
		PARAM_NAME_PROCESSOR.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		PARAM_NAME_PROCESSOR.setProperty("userdirective",PostParameterNameDirective.class.getCanonicalName());
		PARAM_NAME_PROCESSOR.setProperty("runtime.log", "/tmp/velocity");
		PARAM_NAME_PROCESSOR.init();

		HAS_COUNT_REGION_PROCESSOR=new VelocityEngine();
		HAS_COUNT_REGION_PROCESSOR.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
		HAS_COUNT_REGION_PROCESSOR.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		HAS_COUNT_REGION_PROCESSOR.setProperty("userdirective",
			HasCountRegionDirective.class.getCanonicalName()+","+
			ParameterDirective.class.getCanonicalName()+","+
			NParameterDirective.class.getCanonicalName()+","+
			AndDirective.class.getCanonicalName()
		);
		HAS_COUNT_REGION_PROCESSOR.setProperty("runtime.log", "/tmp/velocity");

	}	
	
	public SqlexResult prepareCountQuery(SqlexQuery query){
	  	SqlexResult result=new SqlexResult();		
	  	String countQuery=null;
	  	List<Object> countParams=new ArrayList<Object>();
		List<Integer> andRegion=new ArrayList<Integer>();
		VelocityContext context=new VelocityContext();
		context.put("_count",1);
		context.put("_paramValues", query.getParams());	
		context.put("_andRegions", andRegion);
		context.put("e", dialect);
		StringWriter writer=new StringWriter();
		PRE_PROCESSOR.evaluate(context, writer, "PESQLPREPROCESS", query.getQuery());
		VelocityContext context2=new VelocityContext();
		context2.put("_paramValues",query.getParams());	
		context2.put("_params", countParams);
		StringWriter writer2=new StringWriter();
		POST_PROCESSOR.evaluate(context2, writer2, "PESQLPOSTPROCESS", context.containsKey("_inside_paging")?context.get("_inside_paging").toString():writer.toString());
		countQuery=writer2.toString();
		result.setQuery(countQuery);
		result.setParams(countParams);
		return result;
	}
	
	public SqlexResult prepareDataQuery(SqlexQuery pesql){
	  	SqlexResult result=new SqlexResult();		
	  	String dataQuery=null;
	  	List<Object> dataParams=new ArrayList<Object>();
		List<Integer> andRegions=new ArrayList<Integer>();
		VelocityContext context=new VelocityContext();
		context.put("_paramValues", pesql.getParams());	
		context.put("_andRegions", andRegions);
		context.put("_orderBy", pesql.getOrderby());
		if (pesql.getFetchCount()>0 && hasCountRegion(pesql.getQuery())){
			context.put("_apply_paging", true);
			pesql.getParams().put("_paging_start", pesql.getStartNumber());
			pesql.getParams().put("_paging_count", pesql.getFetchCount());
			pesql.getParams().put("_paging_end", pesql.getFetchCount()+pesql.getStartNumber());
		}
		context.put("e", dialect);
		StringWriter writer=new StringWriter();
		PRE_PROCESSOR.evaluate(context, writer, "PESQLPREPROCESS", pesql.getQuery());
		VelocityContext context2=new VelocityContext();
		context2.put("_params", dataParams);
		context2.put("_paramValues", pesql.getParams());
		String query=writer.toString();
		if(pesql.getFetchCount()>0 && hasCountRegion(pesql.getQuery()) &&!context.containsKey("_paging_applied"))
			query=dialect.applyPaging(query);
		StringWriter writer2=new StringWriter();
		POST_PROCESSOR.evaluate(context2, writer2, "PESQLPOSTPROCESS", query);
		dataQuery=writer2.toString();
		result.setQuery(dataQuery);
		result.setParams(dataParams);
		return result;
    }
	
	public List<String> listParamNames(String pesql){
		List<Integer> andRegions=new ArrayList<Integer>();
		VelocityContext context=new VelocityContext();	
		context.put("_andRegions", andRegions);
		context.put("e", dialect);
		StringWriter writer=new StringWriter();
		PRE_PROCESSOR.evaluate(context, writer, "PESQLPREPROCESS", pesql);
		VelocityContext context2=new VelocityContext();
	  	List<String> paramNames=new ArrayList<String>();
		context2.put("_params", paramNames);
		String query=writer.toString();
		StringWriter writer2=new StringWriter();
		PARAM_NAME_PROCESSOR.evaluate(context2, writer2, "PESQLPNAMEPROCESS", query);
		return paramNames;
	}

	public SqlexDialect getDialect() {
		if (dialect==null)
			throw new RuntimeException("Dialect cannot be null");
		return dialect;
	}

	public void setDialect(SqlexDialect dialect) {
		this.dialect = dialect;
	}

	public SqlexResult prepareNullParamsQuery(String pesql) {
		SqlexResult result=new SqlexResult();		
	  	String dataQuery=null;
	  	List<Object> dataParams=new ArrayList<Object>();
		List<Integer> andRegions=new ArrayList<Integer>();
		VelocityContext context=new VelocityContext();
		context.put("_paramValues", new HashMap<String, Object>());	
		context.put("_andRegions", andRegions);
		context.put("e", dialect);
		context.put("_skipUnknownParameters", new HashMap<String, Object>());
		StringWriter writer=new StringWriter();
		PRE_PROCESSOR.evaluate(context, writer, "PESQLPREPROCESS",pesql);
		VelocityContext context2=new VelocityContext();
		context2.put("_params", dataParams);
		context2.put("_paramValues", new HashMap<String, Object>());
		context2.put("_skipUnknownParameters", new HashMap<String, Object>());
		String query=writer.toString();
		StringWriter writer2=new StringWriter();
		POST_PROCESSOR.evaluate(context2, writer2, "PESQLPOSTPROCESS", query);
		dataQuery=writer2.toString();
		result.setQuery(dataQuery);
		result.setParams(dataParams);
		return result;
	}
	
	public boolean hasCountRegion(String pesql){
		VelocityContext context=new VelocityContext();	
		context.put("e", dialect);
		StringWriter writer=new StringWriter();
		HAS_COUNT_REGION_PROCESSOR.evaluate(context, writer, "PESQLPREPROCESS", pesql);
		return context.containsKey("_hasCount");
	}
	
}
