package com.hrtit.sqlex;

import java.util.List;

public class SqlexResult{
	
	private String query;
	private List<Object> params;
	
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public List<Object> getParams() {
		return params;
	}
	public void setParams(List<Object> params) {
		this.params = params;
	}
	
	@Override
	public String toString() {
		StringBuilder str=new StringBuilder();
		str.append(query);
		if (params!=null &&params.size()>0){
			boolean first=true;
			for (Object o:params){
				if (first){
					str.append(" [");
					first=false;
				}else
					str.append(",");
				str.append(o);
			}
			str.append("]");
		}
		return str.toString();
	}
	
	
}