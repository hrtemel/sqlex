
# SQLex

### Language Specifications
## What is Sqlex?
Sqlex is a template engine to generate SQL statements for querying data or calculating count with respect to given parameters, order by conditions.It is based on apache velocity, please check Apache Velocity Library documentation for language specifications. This library is only generates SQL statements.

 
## Development

## Installation
Clone software and build via mvn or just donwload from [
here](a%C5%9Fsldk%C5%9Falskd%C5%9Flksad)

## Prerequisites

SQLex library depends only Apache Velocity 0.7 library. Compiled with Java  1.8

## Defined Directives

### #region(“data”)
This region’s content is only included when query is executing for data. While count query SQL code generation this part will be omitted.

### #region(“count”)
This region’s content is only included when query is executing for count. While data query SQL code generation this part will be omitted.
    
### #region(“where”)
This region’s content is always evaluated. If content is not empty, SQL’s “where“ closure and than the content are written into result SQL statement.
    
### #region(“orderby”)
If _orderby velocity template parameter is defined, _orderby parameter is used instead of region content.
    
### #and() directive
And directive skip first occurrence in containing region and for other occurrences append “and” operator to SQL statement.

### #param(name,replacement)
Optional parameter directive. It’s content will evaluated and included if specified parameter exists in parameters list. When SQL code generation replacement will be replaced with “?” and Velocity template parameter with “name” name is registered as SQL parameter.

If replacement is not used “?” is used as parameters holder.

### #p(name) directive
Required parameter directive.  It will be replaced by “?” in execution.

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
            "from foo\n"+
            "#region('where')\n"+
            "  #param('id') #and foo.id=? #end\n"+
            "  #param('id2') #and foo.id2=? #end\n"+
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
* from foo
where foo.id=?  LIMIT ?  OFFSET ?  [test_id,50,0]
```

## Examples

### SQL as SQLex
Any SQL statement (without parameters) is already SQLex.

```sql
select x.id,x.txt from foo x where x.id is not null; --valid
select x.id,x.txt from foo x where x.id =?; --invalid
```

### All region
```sql
select 
    #region(“data”) x.id,x.txt,fnc_sample(#p(xid)) #end
    #region(“count”)count(1)#end 
from 
    foo x
#region(“where”) 
    #param(“xid”) #and() x.id=?#end
    #param(“xtxt”) #and() x.txt like ?||’%’ #end
#end
#region(“orderby”) x.id #end 
```
### Sort insensitive
```sql
select 
    #region(“data”) x.id,x.txt #end
    #region(“count”)count(1)#end 
from 
    foo x
#region(“where”) 
    #param(“xid”) #and() x.id=?#end
    #param(“xtxt”) #and() x.txt like ?||’%’ #end
#end
order by x.id
```
### Data Only
```sql
select 
    x.id,x.txt
from 
    foo x
#region(“where”) 
    #param(“xid”) #and() x.id=?#end
    #param(“xtxt”) #and() x.txt like ?||’%’ #end
#end
order by x.id
```

### Without where region
```sql
select 
    x.id,x.txt
from 
    foo x
where
    x.id is not null 
    #param(“xid”) and x.id=?#end
    #param(“xtxt”) and x.txt like ?||’%’ #end
order by x.id
```
### Nested Regions
```sql
#region(“data”) select 
        x.id,y.dsc 
    from 
        foo x 
    left 
        join sample table y 
    on 
        x.id=y.id 
    #region(“where”)  
        #param(“xid”) x.id=? #end
    #end 
    #region(“orderby”)x.id#end 
#end
#region(“count”) select 
        1 
    from 
        foo x 
    #region(“where”)  
        #param(“xid”) x.id=? #end 
    #end
#end
```

### Multi Data Region
```sql
#region(“data”) select 
        x.id,y.dsc 
    from 
        foo x 
    left join 
        sample table y 
    on 
        x.id=y.id 
    #region(“where”)  
        #param(“xid”) #and() x.id=? #end 
    #end
#end
#region(“count”) select 
        1 
    from 
        foo x 
    #region(“where”)  
    #param(“xid”) #and() x.id=? #end 
    #end
#end
#region(“data”)
    #region(“orderby”)x.id#end
#end
```
### Parameters with replacement specifier
```sql
select 
    #region(“data”) x.id,x.txt #end 
    #region(“count”)count(1)#end 
from 
    foo x
where 
    x.id is not null 
    #param(“xid”,”:xid”) and x.id=:xid#end
    #param(“xtxt”,”:xtxt) and x.txt like :xtxt||’%’ #end
#region(“orderby”) x.id#end
```
### Nested Parameters
```sql
select 
    #region(“data”) x.id,x.txt #end 
    #region(“count”)count(1)#end 
from 
    foo x
where 
    x.id is not null 
    #param(“xid”,”:xid”) 
        #param(“xtxt”,”:xtxt) and x.txt like :xtxt||’%’ and x.id=:xid#end
    #end
#region(“orderby”) x.id #end
```
### Required Parameters
```sql
select 
    #region(“data”) x.id,x.txt,fnc_sample(#p(“xrequired”)) #end 
    #region(“count”)count(1)#end 
from 
    foo x
where 
    x.id is not null 
    #param(“xid”,”:xid”) 
        #param(“xtxt”,”:xtxt) and x.txt like :xtxt||’%’ and x.id=:xid#end
    #end
#region(“orderby”) x.id#end

````
### Parameter with multi replacement
```sql
select 
    #region(“data”) x.id,x.txt,fnc_sample(#p(“xrequired”)) #end 
    #region(“count”)count(1)#end 
from 
    foo x
where 
    x.id is not null 
    #param(“xid”) (x.id=? or x.dsc=?)#end
    #region(“orderby”) x.id#end
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
