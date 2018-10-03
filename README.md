
# SQLex

### Language Specifications
## What is Sqlex?
Sqlex is SQL templating language based on Apache Velocity templating engine.
 
## Development

## Installation
Clone software and build via mvn or just donwload from [
here](a%C5%9Fsldk%C5%9Falskd%C5%9Flksad)

## Prerequisites

Sqlex library depends only Apache Velocity 0.7 library. Compiled with Java  1.8

## Basic usage

Code:

```java
import java.util.HashMap;
import java.util.Map;
import com.hrtit.sqlex.SqlexProcessor;
import com.hrtit.sqlex.SqlexQuery;
import com.hrtit.sqlex.SqlexResult;
import com.hrtit.sqlex.dialects.PostgresqlDialect;

public class DemoCode {

	static public void main(String args[]){
		SqlexProcessor processor=new SqlexProcessor();
		processor.setDialect(new PostgresqlDialect());
		
		Map<String,Object> params=new HashMap<>();
		params.put("id", "test_id");
		SqlexQuery query=new SqlexQuery();
		query.setQuery("select\n"+
			"  #region('data') * #end \n"+
			"  #region('count') 1 #end \n"+
			"from hr\n"+
			"#region('where')\n"+
			"  #param('id') #and hr.id=? #end\n"+
			"  #param('id2') #and hr.id2=? #end\n"+
			"#end"
		);

		query.setParams(params);
		query.setFetchCount(50);
		query.setStartNumber(0);	
		SqlexResult c= processor.prepareDataQuery(query);
		System.out.println(c.toString());
	}
	
}
```
Result:

```sql
select
* from hr
where hr.id=?  LIMIT ?  OFFSET ?  [test_id,50,0]
```

## Examples

```sql
select
	#region('data') * #end
	#region('count') 1 #end
from hr
#region('where')
	#param('id') #and hr.id=? #end
	#param('id2') #and hr.id2=? #end
#end
```
## Authors and Contributors

## Support
By [Haydar Rıdvan TEMEL](https://github.com/hrtemel)

### Backers
Support us with a monthly donation and help us continue our activities. [[Become a backer](https://opencollective.com/sqlex#backer)]

### Sponsors
Become a sponsor and get your logo on our README on Github with a link to your site. [[Become a sponsor](https://opencollective.com/sqlex#sponsor)]

## License

This project is licensed under the terms of the [MIT license]
