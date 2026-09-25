[![Build](https://github.com/rodionovsasha/jfixtures/actions/workflows/maven-verify.yml/badge.svg)](https://github.com/rodionovsasha/jfixtures/actions/workflows/maven-verify.yml)
[![Coverage Status](https://coveralls.io/repos/github/rodionovsasha/jfixtures/badge.svg?branch=master)](https://coveralls.io/github/rodionovsasha/jfixtures?branch=master)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.rodionovsasha/jfixtures?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.rodionovsasha/jfixtures)
[![License](https://img.shields.io/github/license/rodionovsasha/jfixtures)](https://github.com/rodionovsasha/jfixtures/blob/master/LICENSE)

## Preface 
JFixtures creation is inspired by [Ruby On Rails fixtures](http://api.rubyonrails.org/v3.2/classes/ActiveRecord/Fixtures.html) - it helps to define test data in a human-readable YML format and then to transform the data to the SQL language which your database understands. So it is a sort of YML to SQL converter.

JFixtures requires Java 17 or newer. For a Spring Boot and JUnit setup, see the [getting-started guide](docs/getting-started-spring-boot-junit.md).
For a catalogue of YAML fixture and configuration recipes, see [YAML recipes](docs/yaml-recipes.md).
As for java world, JFixtures could be compared with [DBUnit](http://dbunit.sourceforge.net/) library.

[Please read our WIKI for more info](https://github.com/rodionovsasha/jfixtures/wiki)

## Implemented features

The following capabilities are implemented in the current codebase. The links point to the issues in which the work was tracked.

* Load fixtures from a directory or a single `.yml`/`.yaml` file; nested directories become schema-qualified table names. [#76](https://github.com/rodionovsasha/jfixtures/issues/76), [#113](https://github.com/rodionovsasha/jfixtures/issues/113), [#165](https://github.com/rodionovsasha/jfixtures/issues/165)
* Build fixture data directly in Java with an immutable fluent API: add tables and rows, accept `Map` data, and merge datasets by table and row alias. [#154](https://github.com/rodionovsasha/jfixtures/issues/154), [#167](https://github.com/rodionovsasha/jfixtures/issues/167), [#176](https://github.com/rodionovsasha/jfixtures/issues/176)
* Configure input through `String`, `Path`, or `File`, and choose a YAML configuration profile for a test scenario. [#192](https://github.com/rodionovsasha/jfixtures/issues/192), [#193](https://github.com/rodionovsasha/jfixtures/issues/193), [#200](https://github.com/rodionovsasha/jfixtures/issues/200)
* Render a fixture directory directly to SQL-99 text with `Shortcuts.Str.sql99(path)`, with an overload that accepts a configuration path. [#194](https://github.com/rodionovsasha/jfixtures/issues/194)
* Compile fixtures into an intermediate instruction list, visit those instructions programmatically, and render them as SQL or XML. [#127](https://github.com/rodionovsasha/jfixtures/issues/127), [#182](https://github.com/rodionovsasha/jfixtures/issues/182), [#184](https://github.com/rodionovsasha/jfixtures/issues/184)
* Produce SQL for SQL-99-compatible databases, MySQL, and Microsoft SQL Server; a custom SQL dialect can also be supplied. [#48](https://github.com/rodionovsasha/jfixtures/issues/48), [#49](https://github.com/rodionovsasha/jfixtures/issues/49), [#57](https://github.com/rodionovsasha/jfixtures/issues/57), [#121](https://github.com/rodionovsasha/jfixtures/issues/121)
* Generate deterministic primary keys from row aliases, configure their lower bound and increment, or specify a primary key explicitly. Duplicate user-defined keys in one table fail with a clear error. [#23](https://github.com/rodionovsasha/jfixtures/issues/23), [#24](https://github.com/rodionovsasha/jfixtures/issues/24), [#25](https://github.com/rodionovsasha/jfixtures/issues/25), [#26](https://github.com/rodionovsasha/jfixtures/issues/26), [#64](https://github.com/rodionovsasha/jfixtures/issues/64)
* Resolve table dependencies from configured references, order the generated statements accordingly, and report circular dependencies and invalid references.
* Clean tables with `DELETE`, `TRUNCATE`, `TRUNCATE CASCADE`, or no cleanup; list tables in `clean_tables` to clean them first even without fixture files; run table-specific custom SQL before cleanup, before inserts, or after inserts. [#21](https://github.com/rodionovsasha/jfixtures/issues/21), [#22](https://github.com/rodionovsasha/jfixtures/issues/22), [#27](https://github.com/rodionovsasha/jfixtures/issues/27), [#28](https://github.com/rodionovsasha/jfixtures/issues/28), [#29](https://github.com/rodionovsasha/jfixtures/issues/29), [#74](https://github.com/rodionovsasha/jfixtures/issues/74), [#75](https://github.com/rodionovsasha/jfixtures/issues/75)
* Preserve SQL-safe scalar output: strings are escaped; `NULL`, boolean, YAML binary, and date/timestamp literals render correctly. [#1](https://github.com/rodionovsasha/jfixtures/issues/1), [#89](https://github.com/rodionovsasha/jfixtures/issues/89), [#91](https://github.com/rodionovsasha/jfixtures/issues/91), [#98](https://github.com/rodionovsasha/jfixtures/issues/98), [#103](https://github.com/rodionovsasha/jfixtures/issues/103), [#90](https://github.com/rodionovsasha/jfixtures/issues/90), [#92](https://github.com/rodionovsasha/jfixtures/issues/92)
* Use inline and configured foreign keys, including relative table names, non-primary-key target columns, UUID primary keys, label interpolation, and composite primary keys. [#32](https://github.com/rodionovsasha/jfixtures/issues/32), [#111](https://github.com/rodionovsasha/jfixtures/issues/111), [#211](https://github.com/rodionovsasha/jfixtures/issues/211), [#215](https://github.com/rodionovsasha/jfixtures/issues/215), [#216](https://github.com/rodionovsasha/jfixtures/issues/216)
* Expand configured polymorphic and many-to-many associations from readable fixture values. [#212](https://github.com/rodionovsasha/jfixtures/issues/212), [#213](https://github.com/rodionovsasha/jfixtures/issues/213)
* Expand opt-in fixture templates with bounded integer ranges and arithmetic expressions. [#217](https://github.com/rodionovsasha/jfixtures/issues/217)
* Apply compiled SQL directly with a caller-provided JDBC `Connection` or `DataSource`. [#34](https://github.com/rodionovsasha/jfixtures/issues/34)

### YAML and generated SQL examples

The examples below follow the feature list in the same order. Each SQL block is the SQL-99 result for the YAML fixture and configuration shown above it.

#### 1. Directory structure and schema-qualified tables

```yml
# fixtures/admin/users.yml
alice:
  name: Alice
```

```sql
DELETE FROM "admin"."users";
INSERT INTO "admin"."users" ("id", "name") VALUES (93003040, 'Alice');
```

#### 2. Java fixture API

The Java API builds the same table and row model as this equivalent YAML fixture:

```yml
# users.yml
vlad:
  name: Vlad
```

```sql
DELETE FROM "users";
INSERT INTO "users" ("id", "name") VALUES (3722233, 'Vlad');
```

#### 3. Input paths and configuration profiles

```yml
# .conf.yml
profiles:
  unit:
    tables:
      users:
        applies_to: users
        clean_method: none

# users.yml
vlad:
  name: Vlad
```

Selecting `withProfile("unit")` produces:

```sql
INSERT INTO "users" ("id", "name") VALUES (3722233, 'Vlad');
```

#### 4. `Shortcuts.Str.sql99`

```yml
# fixtures/users.yml
vlad:
  name: Vlad
```

`Shortcuts.Str.sql99("fixtures")` returns:

```sql
DELETE FROM "users";
INSERT INTO "users" ("id", "name") VALUES (3722233, 'Vlad');
```

#### 5. Compiled instructions and renderers

```yml
# notes.yml
first:
  text: Remember the milk
```

The compiled instruction list contains a cleanup and an insert; its SQL renderer writes:

```sql
DELETE FROM "notes";
INSERT INTO "notes" ("id", "text") VALUES (97540432, 'Remember the milk');
```

#### 6. SQL dialects

```yml
# users.yml
vlad:
  name: Vlad
```

The same fixture renders with dialect-specific identifier quoting:

```sql
-- SQL-99
DELETE FROM "users";
INSERT INTO "users" ("id", "name") VALUES (3722233, 'Vlad');

-- MySQL
DELETE FROM `users`;
INSERT INTO `users` (`id`, `name`) VALUES (3722233, 'Vlad');

-- Microsoft SQL Server
DELETE FROM [users];
INSERT INTO [users] ([id], [name]) VALUES (3722233, 'Vlad');
```

#### 7. Primary keys

```yml
# .conf.yml
tables:
  users:
    applies_to: users
    pk:
      generate: false

# users.yml
vlad:
  id: 7
  name: Vlad
```

```sql
DELETE FROM "users";
INSERT INTO "users" ("id", "name") VALUES (7, 'Vlad');
```

#### 8. Dependencies and configured references

```yml
# .conf.yml
refs:
  comments:
    author_id: users

# users.yml
vlad:
  name: Vlad

# comments.yml
comment:
  author_id: vlad
  text: Hello
```

The user table is emitted before the dependent comments table:

```sql
DELETE FROM "users";
INSERT INTO "users" ("id", "name") VALUES (3722233, 'Vlad');
DELETE FROM "comments";
INSERT INTO "comments" ("id", "author_id", "text") VALUES (950498559, 3722233, 'Hello');
```

#### 9. Cleanup modes and table hooks

```yml
# .conf.yml
tables:
  users:
    applies_to: users
    clean_method: truncate
    before_cleanup: "DELETE FROM audit_log;"
    before_inserts: "SET CONSTRAINTS ALL DEFERRED;"
    after_inserts: "ANALYZE users;"

# users.yml
vlad:
  name: Vlad
```

```sql
DELETE FROM audit_log;
TRUNCATE TABLE "users";
SET CONSTRAINTS ALL DEFERRED;
INSERT INTO "users" ("id", "name") VALUES (3722233, 'Vlad');
ANALYZE users;
```

#### 10. SQL-safe scalar values

```yml
# files.yml
example:
  quoted: "O'Reilly"
  missing: null
  enabled: true
  published_on: 2001-11-23
  binary: !!binary Cg==
  created_at: "sql:CURRENT_TIMESTAMP"
```

```sql
DELETE FROM "files";
INSERT INTO "files" ("id", "quoted", "missing", "enabled", "published_on", "binary", "created_at") VALUES (1323070774, 'O''Reilly', NULL, TRUE, '2001-11-23', X'0a', CURRENT_TIMESTAMP);
```

#### 11. Foreign-key variants

```yml
# .conf.yml
tables:
  users:
    applies_to: users
    pk:
      type: uuid
refs:
  comments:
    author_id: users

# users.yml
vlad:
  name: Vlad

# comments.yml
comment:
  author_id: vlad
```

```sql
DELETE FROM "users";
INSERT INTO "users" ("id", "name") VALUES ('d701fde5-9d74-3768-8308-7b6632186caf', 'Vlad');
DELETE FROM "comments";
INSERT INTO "comments" ("id", "author_id") VALUES (950498559, 'd701fde5-9d74-3768-8308-7b6632186caf');
```

#### 12. Polymorphic and many-to-many associations

```yml
# .conf.yml
many_to_many:
  posts:
    tags:
      join_table: posts_tags
      source_column: post_id
      target_table: tags
      target_column: tag_id

# tags.yml
blue: {}

# posts.yml
post_one:
  title: First post
  tags: [blue]
```

```sql
DELETE FROM "tags";
INSERT INTO "tags" ("id") VALUES (3127034);
DELETE FROM "posts";
INSERT INTO "posts" ("id", "title") VALUES (757443815, 'First post');
INSERT INTO "posts_tags" ("post_id", "tag_id") VALUES (757443815, 3127034);
```

#### 13. Fixture templates

```yml
# .conf.yml
templates:
  enabled: true

# users.yml
user_{{ number }}:
  $template: number=1..2
  name: User {{ number }}
```

```sql
DELETE FROM "users";
INSERT INTO "users" ("id", "name") VALUES (836130275, 'User 1');
INSERT INTO "users" ("id", "name") VALUES (836130274, 'User 2');
```

#### 14. Applying SQL through JDBC

```yml
# users.yml
vlad:
  name: Vlad
```

`JFixtures.noConfig().load("fixtures").apply(dataSource)` applies this generated script to the supplied `DataSource`:

```sql
DELETE FROM "users";
INSERT INTO "users" ("id", "name") VALUES (3722233, 'Vlad');
```

## Advanced references

An inline reference has the form `table:label` or `table:label:column`. JFixtures first looks for the table beside the referring fixture and then from the fixture root. An inline value overrides a configured `refs` entry.

```yml
vlad_comment:
  user_public_id: users:vlad:public_id
  slug: comment-$LABEL
  author_id: $ID(vlad)
```

For `comments.yml` above and `users.yml` containing `vlad: { public_id: vlad-001 }`, the SQL-99 output is:

```sql
DELETE FROM "users";
INSERT INTO "users" ("id", "public_id") VALUES (3722233, 'vlad-001');
DELETE FROM "comments";
INSERT INTO "comments" ("id", "user_public_id", "slug", "author_id") VALUES (1675564327, 'vlad-001', 'comment-vlad_comment', 3722233);
```

`$LABEL` is replaced with the current row label. `$ID(label)` is the stable integer identifier returned by `IntId.one(label)`. `UuidId.one(label)` exposes the matching stable UUID API. Configure UUID IDs with `pk.type: uuid`; normal label references then resolve that UUID automatically.

```yml
tables:
  uuid_users:
    applies_to: users
    pk:
      type: uuid
refs:
  comments:
    user_public_id:
      table: users
      column: public_id
```

For `users.yml` containing `vlad: { public_id: vlad-001 }` and `comments.yml` containing `comment: { user_public_id: vlad }`, this configuration produces:

```sql
DELETE FROM "users";
INSERT INTO "users" ("id", "public_id") VALUES ('d701fde5-9d74-3768-8308-7b6632186caf', 'vlad-001');
DELETE FROM "comments";
INSERT INTO "comments" ("id", "user_public_id") VALUES (950498559, 'vlad-001');
```

Polymorphic and many-to-many associations are opt-in and remove the association field from the row being inserted.

```yml
polymorphic_refs:
  fruits:
    eater:
      id_column: eater_id
      type_column: eater_type
      types:
        Monkey: monkeys
many_to_many:
  posts:
    tags:
      join_table: posts_tags
      source_column: post_id
      target_table: tags
      target_column: tag_id
```

For `fruits.yml` containing `banana: { eater: "george (Monkey)" }`, `monkeys.yml` containing `george: {}`, `posts.yml` containing `post_one: { title: First post, tags: [blue, green] }`, and `tags.yml` containing `blue: {}` and `green: {}`, the SQL-99 script is:

```sql
DELETE FROM "monkeys";
INSERT INTO "monkeys" ("id") VALUES (1249569473);
DELETE FROM "fruits";
INSERT INTO "fruits" ("id", "eater_id", "eater_type") VALUES (1396455227, 1249569473, 'Monkey');
DELETE FROM "tags";
INSERT INTO "tags" ("id") VALUES (3127034);
INSERT INTO "tags" ("id") VALUES (98719139);
DELETE FROM "posts";
INSERT INTO "posts" ("id", "title") VALUES (757443815, 'First post');
INSERT INTO "posts_tags" ("post_id", "tag_id") VALUES (757443815, 3127034);
INSERT INTO "posts_tags" ("post_id", "tag_id") VALUES (757443815, 98719139);
```

With that configuration, `eater: george (Monkey)` inserts `eater_id` and `eater_type`, while `tags: [blue, green]` produces `posts_tags` rows.

### Composite primary keys

Use `pk.columns` to declare a composite key. Each generated component is stable for the row label and column name; for example, the `tenant_id` below is `IntId.one("spring_sale.tenant_id")`. Set `generate: false` when the fixture provides the values itself; then every component is required.

```yml
tables:
  orders:
    applies_to: orders
    pk:
      columns: [tenant_id, order_id]
refs:
  line_items:
    order:
      table: orders
      columns:
        tenant_id: order_tenant_id
        order_id: order_number
```

For `orders.yml` containing `spring_sale: { description: Spring sale }` and `line_items.yml` containing `sale_item: { order: spring_sale, sku: mug }`, the SQL-99 output is:

```sql
DELETE FROM "orders";
INSERT INTO "orders" ("tenant_id", "order_id", "description") VALUES (1116434171, 1163869151, 'Spring sale');
DELETE FROM "line_items";
INSERT INTO "line_items" ("id", "order_tenant_id", "order_number", "sku") VALUES (101383829, 1116434171, 1163869151, 'mug');
```

The `order: spring_sale` value in a `line_items` row is removed and expanded into `order_tenant_id` and `order_number`. The mapping keys name target-key columns and the values name columns to write in the referring row. A scalar reference to a composite-key table is rejected because it cannot represent all key components.

### Fixture templates

Templates are disabled unless the configuration contains `templates.enabled: true`. A template row has a `$template` directive containing one or more inclusive integer ranges. Use `{{ expression }}` in a row label or string column; expressions contain only declared integer variables, integer literals, parentheses, and `+`, `-`, `*`, `/`, or `%`.

```yml
# .conf.yml
templates:
  enabled: true

# users.yml
user_{{ number }}:
  $template: number=1..3
  name: User {{ number }}
  position: "{{ number * 10 }}"
```

With this configuration and `users.yml`, the generated SQL-99 output is:

```sql
DELETE FROM "users";
INSERT INTO "users" ("id", "name", "position") VALUES (836130275, 'User 1', 10);
INSERT INTO "users" ("id", "name", "position") VALUES (836130274, 'User 2', 20);
INSERT INTO "users" ("id", "name", "position") VALUES (836130273, 'User 3', 30);
```

This creates `user_1` through `user_3`, with numeric `position` values 10, 20, and 30. Ranges may descend and multiple comma-separated variables produce their Cartesian product. A row may expand to at most 10,000 rows. Templates do not load classes, call methods, access files, execute SQL, or run arbitrary code. With templates disabled, their markers are treated as ordinary fixture text and are never evaluated.

### Output and fixture options

SQL rendering can use a chosen line separator and number of blank lines between statements. The same formatting is used for `toString()`, `toFile()`, and custom appenders.

```java
String sql = JFixtures.noConfig().load("fixtures").compile().toSql99()
    .withFormatting(new SqlFormatting("\\r\\n", 1))
    .toString();
```

Set `id_generator` to the fully qualified name of a public static method that accepts a row label (`String`) and returns the primary-key value. A table-level generator overrides the root setting. `StringId.one` and `LongId.one` are supplied deterministic generators. Primary-key column names may include `${TABLE}` (upper case) or `${table}` (lower case).

```yml
id_generator: io.github.rodionovsasha.jfixtures.StringId.one
tables:
  users:
    applies_to: users
    pk:
      column: ${TABLE}_ID
  audit_log:
    applies_to: audit_log
    pk:
      id_generator: com.example.Ids.auditId
```

For `users.yml` containing `vlad: { name: Vlad }`, the configured `StringId.one` generator and `${TABLE}` placeholder produce:

```sql
DELETE FROM "users";
INSERT INTO "users" ("USERS_ID", "name") VALUES ('vlad', 'Vlad');
```

For `audit_log.yml`, the statement has the same shape, but its primary-key value is the value returned by the application method `com.example.Ids.auditId` for that row label.

Set `timestamps: true` or provide `timestamps.enabled: true` for a table to add missing `created_at`, `created_on`, `updated_at`, and `updated_on` values as `CURRENT_TIMESTAMP`. A fixture value is retained. `timestamps.value` may provide a different literal, including `sql:` SQL.

`requires` inserts named prerequisite tables before a table even if no foreign key declares the relationship. It uses the same relative-table lookup and circular-dependency checks as references.

```yml
tables:
  users:
    applies_to: users
    requires: [roles]
    timestamps:
      enabled: true
      value: "sql:CURRENT_TIMESTAMP"
```

For `roles.yml` containing `admin: { name: Admin }` and `users.yml` containing `vlad: { name: Vlad }`, `requires` places the role first and timestamps add the four missing columns:

```sql
DELETE FROM "roles";
INSERT INTO "roles" ("id", "name") VALUES (92768751, 'Admin');
DELETE FROM "users";
INSERT INTO "users" ("id", "name", "created_at", "created_on", "updated_at", "updated_on") VALUES (3722233, 'Vlad', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
```

The map API accepts a map for a fixture table and accepts an empty list as an empty table. A scalar value or a non-empty list is rejected because neither identifies named rows. YAML anchors are available through SnakeYAML; name reusable helper rows with a leading `.` so they are not inserted. Root-level YAML `!omap` preserves the declared row order, which lets a self-referential tree place each parent before its children.

```yml
.base: &base
  active: true
vlad:
  <<: *base
  name: Vlad
```

If this is `users.yml`, the anchor is expanded before SQL is generated:

```sql
DELETE FROM "users";
INSERT INTO "users" ("id", "active", "name") VALUES (3722233, TRUE, 'Vlad');
```

```yml
!omap
- grandparent: { age: 100 }
- parent: { parent_id: grandparent }
```

When this is `people.yml` and `refs.people.parent_id: people` is configured, `!omap` preserves the parent-before-child insertion order:

```sql
DELETE FROM "people";
INSERT INTO "people" ("id", "age") VALUES (268416490, 100);
INSERT INTO "people" ("id", "parent_id") VALUES (995524086, 268416490);
```

### Profiles and fixture-set hooks

Configuration may have a `profiles` section. `default` uses `profiles.default` when it is present and otherwise uses the configuration root for backward compatibility. Named profiles must exist; use `withProfile("unit")` to select one and `withDefaultProfile()` to return to `default`. YAML anchors can share settings between profiles.

```yml
profiles:
  default: &base
    refs:
      posts:
        author_id: users
  unit:
    <<: *base
    tables:
      users:
        applies_to: users
        clean_method: none
```

With `withProfile("unit")`, `users.yml` containing `vlad: { name: Vlad }`, and `posts.yml` containing `welcome: { author_id: vlad, title: Welcome }`, the profile inherits the reference and suppresses cleanup for `users`:

```sql
INSERT INTO "users" ("id", "name") VALUES (3722233, 'Vlad');
DELETE FROM "posts";
INSERT INTO "posts" ("id", "author_id", "title") VALUES (1233199618, 3722233, 'Welcome');
```

Use `before_all` and `after_all` for SQL that runs once around the complete fixture set. SQL can be inline or use a `file:` reference relative to the configuration file. The same `file:` form works in a table's `before_cleanup`, `before_inserts`, and `after_inserts`; `$TABLE_NAME` is replaced in loaded SQL just as it is for inline SQL.

```yml
before_all: "file:sql/start.sql"
after_all:
  - "ANALYZE"
  - "file:sql/finish.sql"
tables:
  users:
    applies_to: users
    before_cleanup: "file:sql/reset-users.sql"
```

If `start.sql` contains `SET TIME ZONE 'UTC';`, `reset-users.sql` contains `DELETE FROM audit_log;`, `finish.sql` contains `VACUUM;`, and `users.yml` contains `vlad: { name: Vlad }`, the resulting SQL is:

```sql
SET TIME ZONE 'UTC';
DELETE FROM audit_log;
DELETE FROM "users";
INSERT INTO "users" ("id", "name") VALUES (3722233, 'Vlad');
ANALYZE
VACUUM;
```

When loading a fixture directory, SQL files in `.before` run first and files in `.after` run last. Each directory is optional; its `*.sql` files run in ascending filename order. These conventional hooks surround the configured `before_all` and `after_all` hooks.

Compiled results can be applied directly:

```java
JFixtures.noConfig().load("fixtures").apply(dataSource);
JFixtures.noConfig().load("fixtures").compile().toMySql().apply(connection);
```

## JFixtures VS plain SQL
* With plain SQL it is hard to match values to column names even when you format SQL well:
```sql
INSERT 
  INTO users(id, first_name, last_name, middle_name, sex, age, is_admin, is_guest) 
  VALUES (5, 'Vladimir', 'Korobkov', 'Vadimovich', 'm', 29, true, false); 
```
With JFixtures it is easier:
```yml
user_1:
  id: 5
  first_name: Vladimir
  last_name: Korobkov
  middle_name: Vadimovich
  sex: m
  age: 29
  is_admin: true
  is_guest: false
```

For `users.yml`, the default SQL-99 output is:

```sql
DELETE FROM "users";
INSERT INTO "users" ("id", "first_name", "last_name", "middle_name", "sex", "age", "is_admin", "is_guest") VALUES (5, 'Vladimir', 'Korobkov', 'Vadimovich', 'm', 29, TRUE, FALSE);
```
or the compact form:
```yml
vlad: { first_name: 'Vladimir', last_name: 'Korobkov', sex: 'm', age: 29 }
homer: { first_name: 'Homer', last_name: 'Griffin', sex: 'm', age: 45 }
```

For `users.yml`, JFixtures generates stable primary keys from the row aliases:

```sql
DELETE FROM "users";
INSERT INTO "users" ("id", "first_name", "last_name", "sex", "age") VALUES (3722233, 'Vladimir', 'Korobkov', 'm', 29);
INSERT INTO "users" ("id", "first_name", "last_name", "sex", "age") VALUES (99560979, 'Homer', 'Griffin', 'm', 45);
```
* References to other tables _are numbers_ in plain SQL:
```sql
INSERT INTO comment(id, ticket_id, user_id, text) VALUES (1, 4, 8, 'Hello, world');
```
Numbers are hard to remember and to manage, they do not bring any associated and readable information. Like foreign key `4` says nothing about what is in the referred row of `user` table.

JFixtures allows to define your own text alias for every row and then to refer tables by aliases:
```yml
good_comment: # This is the alias for the row below
  text: This service is really great
  rate: 10
  ticket_id: write_wiki_ticket # refers to tickets table by write_wiki_ticket alias
  user_id: vlad_admin # refers to users table by vlad_admin alias
```

With references for `comments.ticket_id` and `comments.user_id` configured, the generated SQL-99 statement resolves those aliases to the primary keys of their rows:

```sql
DELETE FROM "comments";
INSERT INTO "comments" ("id", "text", "rate", "ticket_id", "user_id") VALUES (1131253629, 'This service is really great', 10, 2084435077, 728039863);
```

The SQL examples use the SQL-99 dialect. They use the default integer ID generator unless the adjacent YAML example configures a different generator; configuration snippets include the minimal fixture data needed to make the output concrete.

* Since every row of every table has an alias, JFixtures takes care of automatic primary keys generation so no need to deal with these numbers at in 99% of cases. For the remaining 1% there is an ability to define primary keys values manually.

* JFixtures resolves references to foreign tables by aliases instead of foreign key numeric values

* JFixtures takes care of the tables order, finding the dependencies between tables and aligning the dependent tables first. For example, if table `comments` has foreign keys to tables `users` and `tickets`, then `users`/`tickets` go first in the result SQL to satisfy `comments`.

* JFixtures provides a nice file system structure to break data into files:
```
  permissions.yml   # converts to "permissions" table
  addresses.yml     # converts to "addresses" table
- geo/ 
    coordinates.yml # converts to "geo.addresses" table
    locations.yml   # converts to "geo.locations" table
- chat/
    comments.yml    # converts to "chat.comments" table
    rooms.yml       # converts to "chat.rooms" table
  - customer/
      rooms.yml     # converts to "chat.customer.rooms" table
      costs.yml     # converts to "chat.customer.costs" table
``` 

* JFixtures has circular dependencies detection between tables

## Tech details
The project is written in pure Java and needs Java Runtime 17+. It is available on maven central [![Maven Central](https://img.shields.io/maven-central/v/io.github.rodionovsasha/jfixtures?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.rodionovsasha/jfixtures) as a library or there is [a command line interface for it](https://github.com/rodionovsasha/jfixtures-cmd)

JFixtures support a few database dialects:
* `SQL 99` format, which covers many databases(Postgres, Oracle, H2, SqlLite, Yandex ClickHouse, Sybase and many others)
* MySql
* Microsoft SQL 
* [Adding a custom database support is easy](https://github.com/rodionovsasha/jfixtures/wiki/How-to-add-a-new-SQL-dialect) or [submit us an issue](https://github.com/rodionovsasha/jfixtures/issues)

JFixtures can export the result into XML, so it is possible then to transform the XML into any other custom format, either SQL or not

### Dependencies
JFixtures uses SnakeYAML to parse YML files and Jakarta XML Binding (JAXB) for XML export.

## Feedback and contribution
We are happy if [you contribute](https://github.com/rodionovsasha/jfixtures/wiki/Contribution) or [submit an issue](https://github.com/rodionovsasha/jfixtures/issues)

Also, feel free to email us, the emails are in [pom.xml](https://github.com/rodionovsasha/jfixtures/blob/master/pom.xml)

[Please read our WIKI for more info](https://github.com/rodionovsasha/jfixtures/wiki)
