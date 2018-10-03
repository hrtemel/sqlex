package com.hrtit.sqlex.dialects;

import com.hrtit.sqlex.SqlexDialect;

public class MssqlDialect implements SqlexDialect{

	@Override
	public String applyPaging(String pesql) {
		return pesql+" OFFSET #p(\"_paging_start\") ROWS FETCH NEXT #p(\"_paging_count\") ROWS ONLY";
	}
	
}
