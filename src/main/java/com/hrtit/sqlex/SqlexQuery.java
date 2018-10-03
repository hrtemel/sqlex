package com.hrtit.sqlex;

import java.util.Map;

public class SqlexQuery{
	
	private String query;
	private Map<String,Object> params;
	private int startNumber=-1;
	private int fetchCount;
	private String orderby;
	
	public String getQuery() {
		return query;
	}

	public void setQuery(String pesql) {
		this.query = pesql;
	}
	
	public Map<String, Object> getParams() {
		return params;
	}
	
	public void setParams(Map<String, Object> params) {
		this.params = params;
	}
		
	public int getStartNumber() {
		return startNumber;
	}
	
	public void setStartNumber(int startRowNumber) {
		this.startNumber = startRowNumber;
	}
	
	public int getFetchCount() {
		return fetchCount;
	}
	
	public void setFetchCount(int fetchRowCount) {
		this.fetchCount = fetchRowCount;
	}
	
	public String getOrderby() {
		return orderby;
	}
	
	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}

}