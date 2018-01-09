![alt text](https://travis-ci.org/vkorobkov/jfixtures.svg?branch=master "Build status")
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/cz.jirutka.rsql/rsql-parser/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.vkorobkov/jfixtures)

## Preface 
JFixtures creation is inspired by [Ruby On Rails fixtures](http://api.rubyonrails.org/v3.2/classes/ActiveRecord/Fixtures.html) - it helps to define test data in a human readable YML format and then to transform the data to the SQL language which your database understands. So it is a sort of YML to SQL converter.
As for java world, JFixtures could be compared with [DBUnit](http://dbunit.sourceforge.net/) library.

[Please read our WIKI for more info](https://github.com/vkorobkov/jfixtures/wiki)

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
or the compact form:
```yml
vlad: { first_name: 'Vladimir', last_name: 'Korobkov', sex: 'm', age: 29 }
homer: { first_name: 'Homer', last_name: 'Griffin', sex: 'm', age: 45 }
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
The project is written in pure Java and needs Java Runtime 8+. It is available on maven central [![Maven Central](https://maven-badges.herokuapp.com/maven-central/cz.jirutka.rsql/rsql-parser/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.vkorobkov/jfixtures) as a library or there is [a command line interface for it](https://github.com/vkorobkov/jfixtures-cmd)

JFixtures support a few database dialects:
* `SQL 99` which covers many databases(Postgres, Oracle, H2, SqlLite, Yandex ClickHouse, Sybase and many others)
* MySql
* Miscrosoft Sql 
* [Adding a custom database support is easy](https://github.com/vkorobkov/jfixtures/wiki/How-to-add-a-new-SQL-dialect) or [submit us an issue](https://github.com/vkorobkov/jfixtures/issues)

JFixtures can export the result into XML, so it is possible then to transform the XML into any other custom format, either SQL or not

### Dependencies
By the moment JFixtures has only one compile time dependency - `org.yaml:snakeyaml:jar` for parsing source YML files.

## Feedback and contribution
We are happy if [you contribute](https://github.com/vkorobkov/jfixtures/wiki/Contribution) or [submit an issue](https://github.com/vkorobkov/jfixtures/issues)

Also, feel free to email us, the emails are in [pom.xml](https://github.com/vkorobkov/jfixtures/blob/master/pom.xml)

And [join us on slack](https://jfixtures.slack.com)

[Please read our WIKI for more info](https://github.com/vkorobkov/jfixtures/wiki)
