JAR file was 45kb

1. Implemented ability to merge config nodes(maps):
```
com.github.vkorobkov.jfixtures.util.MapMerger
com.github.vkorobkov.jfixtures.util.MapMergerTest
```

2. YamlConfig can read nodes(maps) now:
```
com.github.vkorobkov.jfixtures.config.ConfigDigger
com.github.vkorobkov.jfixtures.config.ConfigDiggerTest

com.github.vkorobkov.jfixtures.config.EmptyDigger
com.github.vkorobkov.jfixtures.config.EmptyDiggerTest

com.github.vkorobkov.jfixtures.config.YamlConfig
com.github.vkorobkov.jfixtures.config.YamlConfigTest
```

3. Create "row conflict resolver" for `MapMerger`(when merging nodes with values), 
it is a dependency for the further points:
```
com.github.vkorobkov.jfixtures.util.RowMergeConflictResolver
com.github.vkorobkov.jfixtures.util.RowMergeConflictResolverTest
```

4. teach `com.github.vkorobkov.jfixtures.config.Config` reading nodes
`base_table:$table_name` and `base_table:.every` merging them with `MapMerger`
```
com.github.vkorobkov.jfixtures.config.Config
com.github.vkorobkov.jfixtures.config.ConfigTest
```

5. Broadcast `Config` class into `com.github.vkorobkov.jfixtures.loader.FixturesLoader`
and into `com.github.vkorobkov.jfixtures.loader.YmlRowsLoader`(See 
`com.github.vkorobkov.jfixtures.loader.FixturesLoader.loadFixture`)
```
com.github.vkorobkov.jfixtures.loader.FixturesLoader
com.github.vkorobkov.jfixtures.fluent.JFixturesResultImpl
com.github.vkorobkov.jfixtures.loader.YmlRowsLoader
com.github.vkorobkov.jfixtures.loader.YmlRowsLoaderTest
```

6. Modify `com.github.vkorobkov.jfixtures.loader.YmlRowsLoader.fixtureRow`: use
`MapMerger` to merge rows with corresponding nodes from config(if exist)
```
com.github.vkorobkov.jfixtures.loader.FixturesLoader
com.github.vkorobkov.jfixtures.loader.FixturesLoaderTest
```

Flexible base tables like:
```yaml
parent_tables:
  has_version:
    applies_to: ^*
      columns:
        version: 1
    
  has_cr_date:
    includes: has_version
    applies_to: user, ticket, comment, ^comment_*
      columns:
        cr_date:
          type: sql
          value: NOW()
```

`Applies to` could contain a list. Each item could be a single table or a comma
separated number of tables:
```yaml
applies_to: 
  - user, user_roles, user_profiles
  - ticket
  - comment
  - ^comment_*
```
Each value of `applies_to` could be either a string for direct table name match or
a regular expression(ant matching maybe?) if more flexibility is required

A special symbol for "applies to nothing should be introduced"

Includes section should work the same as applies_to.
Includes section should fail of circular references

Output merged `parent_tables` to logs maybe

Yaml inheritance instead of `include`?

```yaml
base_columns:

  concerns:
    has_version:
      version: 1
    has_creation_date:
      cr_date:
        type: sql
        value: NOW()
        
  _has_standard_fields: &has_standard_fields
    - has_version
    - has_creation_date
    
  _comment_related_tables: &comment_related_tables
    - comment, comment_rate, comment_image
    - user_to_comment, /comment_reply_(.*)

  apply:
    every_table_has_version:
      concerns: has_version # could be a list
      to: /.+ # every table. Could be a list which contains table names or regexps
    comment_tables_have_all_standard_fields:
      concerns: *has_standard_fields
      to: *comment_related_tables
```

7. Write documentation for:
* Default SQL type for a column(s) of a table
* Default SQL type for a column(s) of every table
* Default value for a column(s) of a table
* Default value for a column(s) of every table

8. Update README, TODO.md

9. Increment version in the pom.xml and 

10. Remove this file

11. Push

12. __Release the Cracken__