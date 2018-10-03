package com.hrtit.sqlex.dialects;

import com.hrtit.sqlex.SqlexDialect;

public class OracleDialect implements SqlexDialect{

	@Override
	public String applyPaging(String pesql) {
		return "select f.* from (select q.*, rownum rx  from ( "+pesql+" ) q ) f where f.rx between #p(\"_paging_start\") + 1 AND #p(\"_paging_end\")";
	}

}
