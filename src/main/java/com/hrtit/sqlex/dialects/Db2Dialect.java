package com.hrtit.sqlex.dialects;

import com.hrtit.sqlex.SqlexDialect;

public class Db2Dialect implements SqlexDialect{

	@Override
	public String applyPaging(String pesql) {
		return "select f.* from (select q.*, ROW_NUMBER() OVER() as rx  from ( "+pesql+" ) q ) f where f.rx between #p(\"_paging_start\") AND #p(\"_paging_end\")";
	}
}
