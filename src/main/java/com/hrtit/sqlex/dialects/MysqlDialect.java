package com.hrtit.sqlex.dialects;

import com.hrtit.sqlex.SqlexDialect;

public class MysqlDialect implements SqlexDialect{

	@Override
	public String applyPaging(String pesql) {
		return pesql+" LIMIT #p(\"_paging_start\") , #p(\"_paging_count\")";
	}

}
