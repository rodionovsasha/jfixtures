![alt text](https://travis-ci.org/vkorobkov/jfixtures.svg?branch=master "Build status")
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/cz.jirutka.rsql/rsql-parser/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.vkorobkov/jfixtures)

# JFixtures

## Preface
Nowadays almost every project has acceptance/integration/other tests which use a dedicated test database with some test 
data inside. JFixtures is a small java lib that helps to define the test data in a human-readable YAML format and to translate 
the data into a plain SQL. At some degree it is a port of 
[Ruby On Rails fixtures](http://api.rubyonrails.org/v3.2/classes/ActiveRecord/Fixtures.html) but for java world
(or any other JVM language such as groovy, scala, e.t.c.)

Databases which JFixtures supports:
* Postgres
* MySql
* H2
* Yandex ClickHouse

## What's wrong with SQL ?

Of course, all the test data could be defined as a SQL script(or scripts), so why JFixtures?

Because describing test data in SQL constructions is inconvenient:
 
* It is hard to match values to columns:
```sql
INSERT INTO users(id, first_name, last_name, middle_name, sex, age, is_admin, is_guest) VALUES (5, 'Vladimir', 'Korobkov',
'Vadimovich', 'm', 29, true, false); 
```
Imagine, when your're typing and your cursor is somewhere in the middle of `VALUES (...)` part of the statement, it is
hard enough to understand which value belongs to which column.
The more insert statements/more columns you have, the harder understanding and maintainability of such code.

* Hard management of references:
```sql
INSERT INTO comment(id, ticket_id, user_id, text) VALUES (1, 4, 8, 'Hello, world');
```
Table `comment` has two foreign keys: ticket_id and user_id. You need to match foreign keys of the `comment` table to 
primary keys of the referred tables manually which is not intuitive and looks really unreadable - you need to lookup
to other place of the script for get the values of ticket_id/user_id.

* Tables order matters and you need to take it into account. The _referred_ tables go first, the _referring_ tables go 
last. That means poor developer has to remember the whole tables hierarchy.

* It is verbose - for each row you need to duplicate all this ceremony: `INSERT INTO <table> (...) VALUES(...)`

## JFixtures way

* Human readable test data description with a set of yaml files
* SQL script as a result
* Supports Postgres SQL, MySql, H2, Yandex ClickHouse at the moment
* Human readable, defined by user, string keys for each row instead of numeric IDs
* Numeric PK's are auto generated, however, user can specify them manually
* Foreign key values get calculated automatically(see example below)
* Table references are defined explicitly in a special `.conf.yml` file.
* Tables in output SQL script appear in the right order(according to references between the tavles)
* Early errors detection: fixture processing fails on sytax errors, circular references, incorrect foreign key value,
e.t.c.
* A small java library with only dependency(org.yaml:snakeyaml)

JFixtures offers you to populate a number of readable YAML files with test data and than it converts these files into a 
SQL script. Let's define the following tables: user and ticket. Each user could be a reporter and an assignee of any
ticket.

Let's create an empty folder and add 2 new YML files - one file per table: user.yml and ticket.yml. 

user.yml:
```yaml
vlad:
    first_name: Vladimir
    second_name: Korobkov
    age: 29
    role: admin
alex:
    first_name: Alex
    second_name: Krasnov
    age: 20
    role: developer
    
```

ticket.yml:
```yaml
skeleton:
    reporter: vlad
    title: Project skeleton
    text: To create a project skeleton and push into github
    assignee: vlad
tests:
    reporter: vlad
    title: Include a test framework
    text: To include spock framework into pom.xml and to create a dummy unit test
    assignee: alex
```

Now we need to describe the relations between the tables in a special file:

.conf.yml:
```yaml
refs:
    ticket:
        reporter: user # ticket.reporter refers to user.id
        assignee: user # ticket.assignee refers to user.id
```

That's is really all - now we can generate a valid SQL file with all the test data. We just need a few lines of
java code(later I am planning to create also an executable JAR file and a maven plugin for doing that):
```java
import com.github.vkorobkov.jfixtures.JFixtures;

JFixtures.postgres("/path/to/fixtures/folder").toFile("output.sql");
```

That's all! Output SQL file will contain all the required INSERT instructions or correct order, with correct 
primary/foreign keys and with `DELETE FROM <table>` instruction for cleaning up every table before inserting a new
test data.````

No hard magic here - yml file names get converted as they are(but without .yml extension) into table names.
Each row has a human readable key like `vlad` and `alex` for `user` table and like `skeleton` and `tests`
for `ticket` table. These keys get converted into a numeric PK columns named `id` for each table. Foreign key values 
get resolved using row keys and table relation definitions from `.conf.yml:`. Tables are getting sorted accordingly: 
the _referred_ tables go first, the _referring_ tables go last. Circular dependencies get detected and an exception
will be thrown.


## Maven / Requirements / Dependencies
JFixtures is available on maven central: 
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/cz.jirutka.rsql/rsql-parser/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.vkorobkov/jfixtures)

**JFixtures requires Java 8** or higher and it is written in Java 8.

JFixtures itself has only one "compile" time dependency - it is `org.yaml:snakeyaml:jar:1.17:compile`. 
All other dependencies in the tree are for tests only:
```
com.github.vkorobkov:jfixtures:jar:1.0.2
+- org.yaml:snakeyaml:jar:1.17:compile
+- org.projectlombok:lombok:jar:1.16.14:provided
+- org.spockframework:spock-core:jar:1.1-groovy-2.4-rc-3:test
|  +- org.codehaus.groovy:groovy-all:jar:2.4.6:test
|  \- junit:junit:jar:4.12:test
|     \- org.hamcrest:hamcrest-core:jar:1.3:test
+- nl.jqno.equalsverifier:equalsverifier:jar:2.2.1:test
\- cglib:cglib-nodep:jar:3.2.5:test
```

## Usage
Once you included a dependency into your project, you're ready to use JFixtures.
Generally speaking, JFixtures is just a text processor - it expects to receive a folder with fixtures as an input and it 
writes a SQL file as the output:
```java
import com.github.vkorobkov.jfixtures.JFixtures;

JFixtures.postgres("/path/to/fixtures").toFile("test-data.sql");
```
So this code scans for fixtures(YML files) in `/path/to/fixtures` folder and will write the output into `test-data.sql`
file. If output file had presented before you launched the processing, it will be _recreated_.

It is also possible to get output SQL instructions as a string rather than as a file, for example, if you want to execute the 
sql against already opened SQL connection in your custom code or in your tests:
```java
import com.github.vkorobkov.jfixtures.JFixtures;

String sqlInstructions = JFixtures.postgres("/path/to/fixtures").asString();
```

For another kind of database, for example, MySql, just use corresponding method of `JFixtures` class:
```java
JFixtures.mysql("/path/to/fixtures").asString();
```

## Fixtures and tables

Fixture file names get mapped to table names directly without any hidden magic/conventions. These are case sensitive:

fixture file     | sql table
---------------- | ----------------
users.yml        | users
other_users.yml  | other_users
SomeUsers.yml    | SomeUsers

However, we don't recommend to use or to rely on case sensitivity(in you fixtures naming nor in your DB namings) - 
for example, in OS Windows, _file names are case insensitive_ and it does not allow to store files like `hello` and
`HELLo` inside the same directory.

Fixture file name _can not contain dots_ except the last dot before `yml` extension. For example, fixture files like
`dbo.users.yml` and `public.ticket.yml` are invalid. If you want to prepend a database name or schema or any other 
prefix before the table name itself, then create a subfolder in your fixtures directory:

fixture file     | sql table
---------------- | ----------------
ideas.yml        | ideas
public/users.yml | public.users
dbo/pub/chat.yml | dbo.pub.chat

## Tables and rows

YML fixture files should have strict structure like this:
```yaml
row_name_1:
    column1: value1
    column2: value2
row_name_2:
    column1: value3
    column2: value4
```
Each row must have a unique name(like `row_name_1` and `row_name_2`). These names do not affect the output
SQL script, but they used internally for creating relationships between the tables(traditional for relational DB
foreign key to primary key mappings). If any table has two or more rows with the same name then JFixture throws 
an exception.

Column names(`column1`, `column2`, e.t.c.) get mapped to corresponding SQL column names as they are, without
any conventions nor transformation.

The values(`value1`, `value2`, e.t.c.) also get mapped without any transformations except the string values.
The string values are getting escaped(like `vlad` converts to `'vlad'`) depending on your DB type.

Let's take an example. Let's assume that I've got a fixture file called `users.yml` with the following content:
```yaml
vlad:
    first_name: "Vladimir's name"
    second_name: Korobkov
    age: 29
alex:
    first_name: Alex
    second_name: Krasnov
    age: 20
```
JFixture coverts the fixture in the following SQL code(I selected Postgres dialect):
```postgresql
DELETE FROM "users";
INSERT INTO "users" ("id", "first_name", "second_name", "age") VALUES (10000, 'Vladimir''s name', 'Korobkov', 29);
INSERT INTO "users" ("id", "first_name", "second_name", "age") VALUES (10001, 'Alex', 'Krasnov', 20);
```
if you look at the `VALUES` section you'll see that every string value was turned into a SQL string:

* `"Vladimir's name"` turned into `'Vladimir''s name'` (surrounded with apostrophes and the apostrophe inside the string 
was replaced with double apostrophes)
* `Korobkov` turned into `'Korobkov'`(surrounded with apostrophes)

Sometimes you don't want to store a string value as a string, for example, when you want to insert a result of some
SQL statement:
```yaml
vlad:
    first_name: SELECT name FROM names ORDER BY name LIMIT 1
    second_name: Korobkov
```
JFixtures will convert `first_name` column value into a SQL string: `'SELECT name FROM names ORDER BY name LIMIT 1'`.
For the DB this is just a string literal but not a SQL expression.

But there is a way to say to JFixtures "leave the value as it is, don't convert it to string":
```yaml
vlad:
    # take a look at "value" and "type" subnodes - that switched off the conversion of the value into a string
    name:
      value: SELECT name FROM names ORDER BY name LIMIT 1
      type: sql
    role: dev
    age: 29
```
turns into
```postgresql
DELETE FROM "users";
INSERT INTO "users" ("id", "name", "role", "age") VALUES (10000, SELECT name FROM names ORDER BY name LIMIT 1, 'dev', 29);
```
The `name` column was not converted to a string because we asked not to. The `role` column was converted to a string
by the default behaviour of JFixtures.

## How to add a custom database support (e.g. H2)
It is a quite simple:
1. Add a new class H2 to `com.github.vkorobkov.jfixtures.sql.dialects`.
2. Update `JFixtures.java` with a new static method `h2` which creates new instance of JFixturesResult based on H2 class.
3. Add unit tests for `H2.java` and `JFixtures.java` classes. Ensure code coverage remains 100%(see `target/site/jacoco/index.html`).
That's all!

## Configuration
Sometimes a better control over the fixtures/test data generation is required. Configuration could be located in
`.conf.yml` file which should be placed in the same directory with the fixture yaml files. The config file is optional -
nothing fails if you don't have it at all.
What exactly can be done within the config file will be explain in other sections below.

## Inheriting tables
It happens that designing the database we can set some conventions - for example, in many cases we want to add
a versioning support for a few/all tables. In the very basic case it means that every row in such table(s)
has a field called `version` and has some numeric type. By default it might equal to `1`, for instance, and on every
row modification it gets incremented:
```yaml
vlad:
  name: Vlad
  role: dev
  version: 1
alex:
  name: Alexander
  role: ui
  version: 1
dimon:
  name: Diman
  role: qa
  version: 1
```
`version: 1` was duplicated 3 times - for every single row. It might be convenient when you have 3 rows, but when
you have, for example, 300 rows, it makes sense to avoid such duplicates for every single row. There are two ways of
doing so: 
1. Set the default value for `version` column on the DB level 
2. Use inheritance on JFixtures side, e.g. to add some shared behaviour for every row of a table or tables.

The first variant is preferable, of course - it is simple and natural for relational databases, does not need any
extra knowledge or skills to implement it. However, due to some reasons, your DB design might not have the default
values for cases like that: for example, if the architect or DBA prefer to set these values explicitely from your app,
or if it is a legacy problem and the DB refactoring is risky/going to take much/etc or when you need to put values
which are different from table's default values in SQL.

So for the second case JFixtures allows you to inherit tables. If we create a `.conf.yml` and put in it following
lines:
```yaml
base_columns:
  concerns:
    has_version:
      version: 1

  apply:
    every_table_has_version:
      to: /.+
      concerns: has_version
```
then we can simplify our fixtures and remove `version: 1` from every single row:
```yaml
vlad:
  name: Vlad
  role: dev
alex:
  name: Alexander
  role: ui
dimon:
  name: Diman
  role: qa
```
So how does it work?

`base_columns` section in `.conf.yml` is responsible for tables inheritance. It consists of two main subsections:
`concerns` and `apply`. 

`concerns` just describes behaviours which could be added to tables, like `has_version` behaviour in our case. 
It has exactly the same format as rows in the fixtures. `has_version` concern sets column named `version` to `1` 
for every row. 

`apply` section defines which concerns should be applied to which tables: `to: /.+` means "apply to every table", 
`concerns: has_version` specified which concern or concerns to apply.

It is possible to apply many concerns to many tables at the same time: sections `apply:to` and `apply:concerns` can
receive many items(tables and concerns):

* A single value: `to: users`, `concerns: has_version`
Applies to a single table/concern
* Comma separated string: `to: table_1, table_2, table_3`, `concerns: has_version, has_cr_date`
Applies to every comma-separated table/concern.
* An array: `to: [table_1, table_2, table_3]`, `concerns: [has_version, has_cr_date]`.
Array could also contain another arrays. The depth in not limited.
Every array(or sub array) item could be either a string(for one table/concern) or a comma separated string(
for many tables/concerns). This pattern opens a very powerful ability of YAML: you can define and include lists into 
one another if you need to group and reuse items:
```yaml
base_columns:
  concerns:
    has_version:
      version: 1
      
  # Define a list to reuse it later
  _user_related_tables: &user_related_tables
    - users
    - profiles
    - avatars
    - raitings

  apply:
    every_table_has_version:
      # applies to every table from user_related_tables list and to comments and tickets
      to: [*user_related_tables, comments, tickets]
      concerns: has_version
    # every_table_has_cr_date ...
    # etc 
```

The items of `apply:to` could be a either a string or a regular expression. 
Use `/` prefix to start a regexp: `to: /.+` means `.+` will be tested against every table, and when table name matches, 
all the concerns from `concerns` section will be applied.

The order of items in `apply:to` does not matter; any duplicates are ignored;
The order of items in `apply:concerns` matters: concerns will be applied in the same order as they were defined,
from left to right. Duplicates are not getting removed. Whem many concerns, they are getting merged, it means, 
that the further concerns complement(add rows) or override(replace rows) the previous ones.

Note, that columns in fixtures have a priority over the columns from `base_columns:concerns`. It means that when
fixture defines the same columns as defined in `base_columns:concerns` for that table, the column value from
fixture will override the value from `base_columns:concerns`.

The `apply` may have a number of named subsections like `every_table_has_version`, `user_table_has_cr_date`, etc.
These named sections are getting scanned and applied in top down order:
```yaml
base_columns:
  concerns:
    has_version:
      version: 1
      
    has_cr_date:
      cr_date: NOW()
      
    has_next_version:
      version: 2

  apply:
    # Adds [version: 1] to every row of users table
    users_has_version:
      to: users
      concerns: has_version
    # Adds [cr_date: NOW()] to every row of users table, keeping previous [version: 1]
    users_has_cr_date:
      to: users
      concerns: has_cr_date
    # Replaces [version: 1] with [version: 2]   
    users_has_version_2:
      to: users
      concerns: has_next_version
```
This example results into two columns, which will be added into every row of `users` table: 
`cr_date: NOW()` and `version: 2`

## Enable/disable primary key generation (id column)
We can describe rules for primary key generation in a special file `.conf.yml`:
```yaml
".conf.yml":
  tables:
    all_tables_do_not_have_generated_pk:
      applies_to: "friends"
      pk:
        generate: false

"friends.yml":
  vlad:
    name: Vlad
    age: 29
```
where `friends` is a table name.
* `applies_to` can be a regular expression starts with /
* `applies_to` can be a comma separated string like "table1, table2, /user.+"
* `applies_to` can be an array of strings, regexps or other arrays

Accepted values for `generate` section: `true, false, on, off`.

If configuration for tables is not set the system will generate `id` column by default.

Another option: not all tables can contain `id` column as a primary key. In this case any custom id can be set using `column` section.
Source YAML:

```yaml
".conf.yml":
  tables:
    tables_with_generated_pk:
      applies_to: "friends"
      pk:
        generate: true
        column: custom_id

"friends.yml":
  vlad:
    name: Vlad
    age: 29
```
Output SQL:
```sql
INSERT INTO "friends" ("custom_id", "name", "age") VALUES (10000, 'Vlad', 29);
```
