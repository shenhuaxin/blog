#### JAP Entity如何给表指定库名

背景：
我们在项目开发中，可能不止使用一个库， 可能需要同时使用多个库。
我们会在datasource.url中默认使用一个库名外， 如果我们需要使用另外一个库该怎么办呢？ 


解决方法：    
在JPA， 默认情况下，我们只需要在实体类上加上@Entity即可。  
如果我们需要指定表名，我们则需要使用@Table注解。     
@Table类如下所示：
```
@Target(TYPE) 
@Retention(RUNTIME)
public @interface Table {
    /**
     * (Optional) The name of the table.
     * <p> Defaults to the entity name.
     */
    String name() default "";
    /** (Optional) The catalog of the table.
     * <p> Defaults to the default catalog.
     */
    String catalog() default "";
    /** (Optional) The schema of the table.
     * <p> Defaults to the default schema for user.
     */
    String schema() default "";
    /**
     * (Optional) Unique constraints that are to be placed on 
     * the table. These are only used if table generation is in 
     * effect. These constraints apply in addition to any constraints 
     * specified by the <code>Column</code> and <code>JoinColumn</code> 
     * annotations and constraints entailed by primary key mappings.
     * <p> Defaults to no additional constraints.
     */
    UniqueConstraint[] uniqueConstraints() default {};
    /**
     * (Optional) Indexes for the table.  These are only used if
     * table generation is in effect.  Note that it is not necessary
     * to specify an index for a primary key, as the primary key
     * index will be created automatically.
     *
     * @since Java Persistence 2.1 
     */
    Index[] indexes() default {};
}
```


其中name为类对应的表的名称。    
那么catalog和schema代表什么意思呢。 

通常来说， 一个catalog中包含一个或多个schema, 一个schema包含多张表。 

但是在Mysql中， schema和database是等价的。catalog和database也是等价的。因此在JDBC驱动中，有一个属性表示是使用catalog还是schema来表示database.   
这个属性就是databaseTerm。 默认值是CATALOG。 我们可以在datasource.url中设置该值。  
例如：datasource.url=jdbc:mysql://localhost:3306/blog?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&useSSL=true&databaseTerm=SCHEMA


```
databaseTerm

MySQL uses the term "schema" as a synonym of the term "database," while Connector/J historically takes the JDBC term "catalog" as synonymous to "database".     
This property sets for Connector/J which of the JDBC terms "catalog" and "schema" is used in an application to refer to a database.     
The property takes one of the two values CATALOG or SCHEMA and uses it to determine (1) which Connection methods can be used to set/get the current database (e.g. setCatalog() or setSchema()?),   
(2) which arguments can be used within the various DatabaseMetaData methods to filter results (e.g. the catalog or schemaPattern argument of getColumns()?),    
and (3) which fields in the ResultSet returned by DatabaseMetaData methods contain the database identification information (i.e., the TABLE_CAT or TABLE_SCHEM field in the ResultSet returned by getTables()?).

If databaseTerm=CATALOG, schemaPattern for searches are ignored and calls of schema methods (like setSchema() or get Schema()) become no-ops, and vice versa.

Default: CATALOG

Since version: 8.0.17
```
详情见：  https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-configuration-properties.html