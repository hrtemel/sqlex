package com.hrtit.sqlex;

public interface SqlexDialect {
	/*Create paging region for given pesql */
	public String applyPaging(String pesql);
}
