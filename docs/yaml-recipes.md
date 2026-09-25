# YAML recipes for JFixtures

This file is a catalogue of self-contained scenarios. Each scenario lists the
files to create inside the `fixtures/` directory and the result of
`JFixtures.withConfig("fixtures/.conf.yml").load("fixtures")` (or
`JFixtures.noConfig().load("fixtures")` when no configuration is needed).

Row names (`vlad`, `first_post`, and so on) are readable labels. By default,
JFixtures assigns a stable integer `id` to each label and resolves label
references to the corresponding keys. To inspect the generated SQL, call
`compile().toSql99().toString()` instead of `apply(dataSource)`.

Every fixture YAML file below is followed by its SQL-99 output. A `.conf.yml`
file configures that output but does not create SQL statements by itself.

## 1. A regular table and SQL-safe YAML types

`fixtures/users.yml`:

```yml
vlad:
  name: "O'Reilly"
  active: true
  age: 29
  nickname: null
  born_on: 1995-06-14
  avatar: !!binary AQI=
  created_at: "sql:CURRENT_TIMESTAMP"
```

**Generated SQL (SQL-99):**

```sql
DELETE FROM "users";
INSERT INTO "users" ("id", "name", "active", "age", "nickname", "born_on", "avatar", "created_at") VALUES (3722233, 'O''Reilly', TRUE, 29, NULL, '1995-06-14', X'0102', CURRENT_TIMESTAMP);
```

**Result.** The `users` table is cleaned and then receives one row with an
automatically generated `id`. The string is escaped for SQL, `nickname`
becomes `NULL`, the boolean becomes `TRUE`, the date becomes a date string
literal, the binary value becomes a hexadecimal literal, and `created_at` is
the unquoted `CURRENT_TIMESTAMP` expression.

## 2. Compact notation, an empty table, and row order

`fixtures/roles.yml`:

```yml
admin: { name: Administrator, enabled: true }
reader: { name: Reader, enabled: false }
```

**Generated SQL (SQL-99):**

```sql
DELETE FROM "roles";
INSERT INTO "roles" ("id", "name", "enabled") VALUES (92768751, 'Administrator', TRUE);
INSERT INTO "roles" ("id", "name", "enabled") VALUES (935079389, 'Reader', FALSE);
```

`fixtures/audit_log.yml`:

```yml
[]
```

**Generated SQL (SQL-99):**

```sql
DELETE FROM "audit_log";
```

`fixtures/family.yml`:

```yml
!omap
- grandparent: { name: Grandparent }
- parent: { name: Parent, parent_id: grandparent }
- child: { name: Child, parent_id: parent }
```

**Generated SQL (SQL-99), with the configuration below:**

```sql
DELETE FROM "family";
INSERT INTO "family" ("id", "name") VALUES (268416490, 'Grandparent');
INSERT INTO "family" ("id", "name", "parent_id") VALUES (995524086, 'Parent', 268416490);
INSERT INTO "family" ("id", "name", "parent_id") VALUES (94731196, 'Child', 995524086);
```

`fixtures/.conf.yml`:

```yml
refs:
  family:
    parent_id: family
```

**Result.** Two rows are created in `roles`. `audit_log` is cleaned but
receives no rows. `!omap` preserves the `grandparent → parent → child` order;
the `parent_id` references resolve to keys of earlier rows in the same table.

## 3. Schemas through nested directories

```text
fixtures/
├── admin/
│   └── users.yml
└── content/
    └── posts.yml
```

`fixtures/admin/users.yml`:

```yml
alice:
  name: Alice
```

**Generated SQL (SQL-99):**

```sql
DELETE FROM "admin"."users";
INSERT INTO "admin"."users" ("id", "name") VALUES (93003040, 'Alice');
```

`fixtures/content/posts.yml`:

```yml
welcome:
  title: Welcome
```

**Generated SQL (SQL-99):**

```sql
DELETE FROM "content"."posts";
INSERT INTO "content"."posts" ("id", "title") VALUES (1233199618, 'Welcome');
```

**Result.** Rows are created in the schema-qualified `admin.users` and
`content.posts` tables. Dots are not allowed in YAML file names or ordinary
directory names: use `admin/users.yml` instead of `admin.users.yml`.

## 4. Default columns and rules for a group of tables

`fixtures/.conf.yml`:

```yml
tables:
  all_application_tables:
    applies_to: "/(users|posts|comments)"
    default_columns:
      tenant_id: 42
      active: true
  users_override:
    applies_to: users
    default_columns:
      role: reader
```

`fixtures/users.yml`:

```yml
vlad:
  name: Vlad
  role: admin
```

**Generated SQL (SQL-99), with the configuration above:**

```sql
DELETE FROM "users";
INSERT INTO "users" ("id", "tenant_id", "active", "role", "name") VALUES (3722233, 42, TRUE, 'admin', 'Vlad');
```

`fixtures/posts.yml`:

```yml
first:
  title: First post
```

**Generated SQL (SQL-99), with the configuration above:**

```sql
DELETE FROM "posts";
INSERT INTO "posts" ("id", "tenant_id", "active", "title") VALUES (97540432, 42, TRUE, 'First post');
```

**Result.** Every `users` and `posts` row receives the missing
`tenant_id: 42` and `active: true` values. `users` would receive the default
`role: reader`, but the row's `role: admin` takes precedence. `applies_to`
accepts a table name, a list of table names, or a regular expression beginning
with `/`.

## 5. Generated, explicit, and disabled primary keys

`fixtures/.conf.yml`:

```yml
tables:
  manual_users:
    applies_to: users
    pk:
      generate: false
      column: user_id
  external_accounts:
    applies_to: accounts
    pk:
      column: account_id
```

`fixtures/users.yml`:

```yml
vlad:
  user_id: 101
  name: Vlad
```

**Generated SQL (SQL-99), with the configuration above:**

```sql
DELETE FROM "users";
INSERT INTO "users" ("user_id", "name") VALUES (101, 'Vlad');
```

`fixtures/accounts.yml`:

```yml
main:
  name: Main account
```

**Generated SQL (SQL-99), with the configuration above:**

```sql
DELETE FROM "accounts";
INSERT INTO "accounts" ("account_id", "name") VALUES (3443801, 'Main account');
```

**Result.** JFixtures does not generate a key for `users` and inserts the
provided `user_id: 101`. It generates a stable `account_id` for `accounts`
from the `main` label. If `generate: false` is specified without a key in the
row, that column is absent from the INSERT; every component is required for a
composite key.

## 6. UUID, string/long keys, and a key name derived from the table name

`fixtures/.conf.yml`:

```yml
tables:
  users:
    applies_to: users
    pk:
      column: ${TABLE}_ID
      id_generator: io.github.rodionovsasha.jfixtures.StringId.one
  sessions:
    applies_to: sessions
    pk:
      type: uuid
  audit_log:
    applies_to: audit_log
    pk:
      id_generator: io.github.rodionovsasha.jfixtures.LongId.one
```

`fixtures/users.yml`:

```yml
vlad: { name: Vlad }
```

**Generated SQL (SQL-99), with the configuration above:**

```sql
DELETE FROM "users";
INSERT INTO "users" ("USERS_ID", "name") VALUES ('vlad', 'Vlad');
```

`fixtures/sessions.yml`:

```yml
browser: { device: Firefox }
```

**Generated SQL (SQL-99), with the configuration above:**

```sql
DELETE FROM "sessions";
INSERT INTO "sessions" ("id", "device") VALUES ('8e3f1bbb-73f0-36c9-92fc-f873332eae9f', 'Firefox');
```

`fixtures/audit_log.yml`:

```yml
login: { event: LOGIN }
```

**Generated SQL (SQL-99), with the configuration above:**

```sql
DELETE FROM "audit_log";
INSERT INTO "audit_log" ("id", "event") VALUES (103149417, 'LOGIN');
```

**Result.** `users` receives a `USERS_ID` column with the string value
`vlad`; `${table}` would use lower case. `sessions` receives a deterministic
UUID, and `audit_log` uses `LongId.one`. A root-level `id_generator` can apply
to every table, while `pk.id_generator` overrides it; a configured generator
takes precedence even over `pk.type: uuid`. A custom generator must be a public
static `(String) -> value` method.

## 7. Configured and inline references, including a non-key column

`fixtures/.conf.yml`:

```yml
refs:
  comments:
    author_id: users
    author_public_id:
      table: users
      column: public_id
```

`fixtures/users.yml`:

```yml
vlad:
  public_id: usr-vlad
  name: Vlad
```

**Generated SQL (SQL-99), with the configuration above:**

```sql
DELETE FROM "users";
INSERT INTO "users" ("id", "public_id", "name") VALUES (3722233, 'usr-vlad', 'Vlad');
```

`fixtures/comments.yml`:

```yml
configured:
  author_id: vlad
  text: Configured reference
inline:
  author_public_id: users:vlad:public_id
  text: Inline reference
```

**Generated SQL (SQL-99), with the configuration above:**

```sql
DELETE FROM "comments";
INSERT INTO "comments" ("id", "author_id", "text") VALUES (832602334, 3722233, 'Configured reference');
INSERT INTO "comments" ("id", "author_public_id", "text") VALUES (1184097287, 'usr-vlad', 'Inline reference');
```

**Result.** `users` is inserted first. In the `configured` row, the `vlad`
label becomes the user's primary key. The `inline` row receives `usr-vlad`,
the value of the non-key `public_id` column. The inline reference format is
`table:label` or `table:label:column`; it takes precedence over `refs`.

## 8. Relative references, label interpolation, and a stable ID

```text
fixtures/
└── admin/
    ├── users.yml
    └── comments.yml
```

`fixtures/admin/users.yml`:

```yml
vlad: { name: Vlad }
```

**Generated SQL (SQL-99):**

```sql
DELETE FROM "admin"."users";
INSERT INTO "admin"."users" ("id", "name") VALUES (3722233, 'Vlad');
```

`fixtures/admin/comments.yml`:

```yml
hello:
  author_id: users:vlad
  slug: comment-$LABEL
  owner_id: $ID(vlad)
```

**Generated SQL (SQL-99):**

```sql
DELETE FROM "admin"."comments";
INSERT INTO "admin"."comments" ("id", "author_id", "slug", "owner_id") VALUES (99262322, 3722233, 'comment-hello', 3722233);
```

**Result.** `admin.comments.author_id` receives the key of
`admin.users.vlad`: for an inline reference, the library first looks for a
table beside the current fixture file. `slug` becomes `comment-hello`, and
`owner_id` receives the stable integer ID of the `vlad` label.

## 9. Composite primary keys and composite references

`fixtures/.conf.yml`:

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

`fixtures/orders.yml`:

```yml
spring_sale:
  description: Spring sale
```

**Generated SQL (SQL-99), with the configuration above:**

```sql
DELETE FROM "orders";
INSERT INTO "orders" ("tenant_id", "order_id", "description") VALUES (1116434171, 1163869151, 'Spring sale');
```

`fixtures/line_items.yml`:

```yml
mug:
  order: spring_sale
  sku: MUG-01
```

**Generated SQL (SQL-99), with the configuration above:**

```sql
DELETE FROM "line_items";
INSERT INTO "line_items" ("id", "order_tenant_id", "order_number", "sku") VALUES (208479, 1116434171, 1163869151, 'MUG-01');
```

**Result.** `tenant_id` and `order_id` are generated for `orders.spring_sale`.
The `order` field is removed from the `line_items` row and replaced with
`order_tenant_id` and `order_number`, containing both order-key components. A
regular scalar reference to a table with a composite key is not allowed.

## 10. A polymorphic association

`fixtures/.conf.yml`:

```yml
polymorphic_refs:
  fruits:
    eater:
      id_column: eater_id
      type_column: eater_type
      types:
        Monkey: monkeys
        Person: people
```

`fixtures/monkeys.yml`:

```yml
george: { name: George }
```

**Generated SQL (SQL-99), with the configuration above:**

```sql
DELETE FROM "monkeys";
INSERT INTO "monkeys" ("id", "name") VALUES (1249569473, 'George');
```

`fixtures/people.yml`:

```yml
alice: { name: Alice }
```

**Generated SQL (SQL-99), with the configuration above:**

```sql
DELETE FROM "people";
INSERT INTO "people" ("id", "name") VALUES (93003040, 'Alice');
```

`fixtures/fruits.yml`:

```yml
banana: { eater: "george (Monkey)" }
apple: { eater: "alice (Person)" }
```

**Generated SQL (SQL-99), with the configuration above:**

```sql
DELETE FROM "fruits";
INSERT INTO "fruits" ("id", "eater_id", "eater_type") VALUES (1396455227, 1249569473, 'Monkey');
INSERT INTO "fruits" ("id", "eater_id", "eater_type") VALUES (93129210, 93003040, 'Person');
```

**Result.** The `eater` field is not inserted. The banana receives an
`eater_id` reference to `monkeys.george` and `eater_type: Monkey`; the apple
receives a reference to `people.alice` and `eater_type: Person`. The target
tables are inserted before `fruits`.

## 11. Many-to-many

`fixtures/.conf.yml`:

```yml
many_to_many:
  posts:
    tags:
      join_table: posts_tags
      source_column: post_id
      target_table: tags
      target_column: tag_id
```

`fixtures/tags.yml`:

```yml
blue: { title: Blue }
green: { title: Green }
```

**Generated SQL (SQL-99), with the configuration above:**

```sql
DELETE FROM "tags";
INSERT INTO "tags" ("id", "title") VALUES (3127034, 'Blue');
INSERT INTO "tags" ("id", "title") VALUES (98719139, 'Green');
```

`fixtures/posts.yml`:

```yml
first_post:
  title: First post
  tags: [blue, green]
```

**Generated SQL (SQL-99), with the configuration above:**

```sql
DELETE FROM "posts";
INSERT INTO "posts" ("id", "title") VALUES (161012177, 'First post');
INSERT INTO "posts_tags" ("post_id", "tag_id") VALUES (161012177, 3127034);
INSERT INTO "posts_tags" ("post_id", "tag_id") VALUES (161012177, 98719139);
```

**Result.** Regular rows are added to `tags` and `posts`. The `tags` field is
not inserted into `posts`; instead, two `posts_tags` rows are added with the
key of `first_post` and the keys of `blue` and `green`. The source table must
have a single-column key.

## 12. Row templates and arithmetic

`fixtures/.conf.yml`:

```yml
templates:
  enabled: true
```

`fixtures/users.yml`:

```yml
user_{{ number }}_{{ region }}:
  $template: number=1..2, region=10..11
  name: User {{ number }}
  region_id: "{{ region }}"
  sort_order: "{{ (number * 100) + region }}"
```

**Generated SQL (SQL-99), with the configuration above:**

```sql
DELETE FROM "users";
INSERT INTO "users" ("id", "name", "region_id", "sort_order") VALUES (337619841, 'User 1', 10, 110);
INSERT INTO "users" ("id", "name", "region_id", "sort_order") VALUES (337619842, 'User 1', 11, 111);
INSERT INTO "users" ("id", "name", "region_id", "sort_order") VALUES (337649632, 'User 2', 10, 210);
INSERT INTO "users" ("id", "name", "region_id", "sort_order") VALUES (337649633, 'User 2', 11, 211);
```

**Result.** Four rows are created with labels `user_1_10`, `user_1_11`,
`user_2_10`, and `user_2_11`. `region_id` and `sort_order` become numbers
computed from the expressions. Ranges are inclusive, may descend, and form a
Cartesian product; a template may create at most 10,000 rows. Without
`templates.enabled: true`, the markers remain ordinary text.

## 13. Timestamps and an explicit dependency

`fixtures/.conf.yml`:

```yml
tables:
  users:
    applies_to: users
    requires: [roles]
    timestamps:
      enabled: true
      value: "sql:TIMESTAMP '2020-01-01 00:00:00'"
```

`fixtures/roles.yml`:

```yml
admin: { name: Administrator }
```

**Generated SQL (SQL-99), with the configuration above:**

```sql
DELETE FROM "roles";
INSERT INTO "roles" ("id", "name") VALUES (92768751, 'Administrator');
```

`fixtures/users.yml`:

```yml
vlad:
  name: Vlad
  created_at: "sql:CURRENT_TIMESTAMP"
```

**Generated SQL (SQL-99), with the configuration above:**

```sql
DELETE FROM "users";
INSERT INTO "users" ("id", "name", "created_at", "created_on", "updated_at", "updated_on") VALUES (3722233, 'Vlad', CURRENT_TIMESTAMP, TIMESTAMP '2020-01-01 00:00:00', TIMESTAMP '2020-01-01 00:00:00', TIMESTAMP '2020-01-01 00:00:00');
```

**Result.** `roles` is inserted before `users`, even without a foreign key.
The missing `created_on`, `updated_at`, and `updated_on` values are added to
the user row with the configured timestamp; the explicitly provided
`created_at` is retained. The shorthand `timestamps: true` uses
`CURRENT_TIMESTAMP`.

## 14. Table cleanup, including tables without fixtures

`fixtures/.conf.yml`:

```yml
clean_tables: [job_locks, audit_log]
tables:
  no_cleanup:
    applies_to: users
    clean_method: none
  truncate_posts:
    applies_to: posts
    clean_method: truncate
  cascade_comments:
    applies_to: comments
    clean_method: truncate_cascade
```

`fixtures/users.yml`:

```yml
vlad: { name: Vlad }
```

**Generated SQL (SQL-99), with the configuration above:**

```sql
INSERT INTO "users" ("id", "name") VALUES (3722233, 'Vlad');
```

`fixtures/posts.yml`:

```yml
first: { title: First }
```

**Generated SQL (SQL-99), with the configuration above:**

```sql
TRUNCATE TABLE "posts";
INSERT INTO "posts" ("id", "title") VALUES (97540432, 'First');
```

`fixtures/comments.yml`:

```yml
first: { text: Hello }
```

**Generated SQL (SQL-99), with the configuration above:**

```sql
TRUNCATE TABLE "comments" CASCADE;
INSERT INTO "comments" ("id", "text") VALUES (97540432, 'Hello');
```

**Result.** `job_locks` and `audit_log` are cleaned with `DELETE` before the
fixtures. `users` is inserted without cleanup, `posts` is cleaned with
`TRUNCATE TABLE`, and `comments` with `TRUNCATE TABLE … CASCADE`. The default
`clean_method` is `delete`.

## 15. SQL hooks: once, per table, and from files

```text
fixtures/
├── .before/10-prepare.sql
├── .after/10-finish.sql
├── sql/
│   ├── before-all.sql
│   └── reset-users.sql
├── .conf.yml
└── users.yml
```

`fixtures/.conf.yml`:

```yml
before_all:
  - "file:sql/before-all.sql"
  - "SET TIME ZONE 'UTC';"
after_all: "ANALYZE;"
tables:
  users:
    applies_to: users
    before_cleanup: "file:sql/reset-users.sql"
    before_inserts: "SET CONSTRAINTS ALL DEFERRED;"
    after_inserts: "ANALYZE $TABLE_NAME;"
```

`fixtures/sql/before-all.sql`:

```sql
SET application_name = 'fixture-load';
```

`fixtures/sql/reset-users.sql`:

```sql
DELETE FROM audit_log WHERE table_name = '$TABLE_NAME';
```

`fixtures/.before/10-prepare.sql`:

```sql
SELECT 'prepare';
```

`fixtures/.after/10-finish.sql`:

```sql
SELECT 'finish';
```

`fixtures/users.yml`:

```yml
vlad: { name: Vlad }
```

**Generated SQL, with the configuration and hook files above:**

```sql
SELECT 'prepare';
SET application_name = 'fixture-load';
SET TIME ZONE 'UTC';
DELETE FROM audit_log WHERE table_name = 'users';
DELETE FROM "users";
SET CONSTRAINTS ALL DEFERRED;
INSERT INTO "users" ("id", "name") VALUES (3722233, 'Vlad');
ANALYZE users;
ANALYZE;
SELECT 'finish';
```

**Result.** SQL runs in this order: `.before/*.sql` by file name,
`before_all`, the `before_cleanup` hook, cleanup of `users`, `before_inserts`,
the INSERT, `after_inserts`, `after_all`, and `.after/*.sql`. In table hooks,
`$TABLE_NAME` becomes `users`; `file:` is resolved relative to `.conf.yml`.

## 16. Configuration profiles and YAML anchors

`fixtures/.conf.yml`:

```yml
profiles:
  default: &shared
    refs:
      posts:
        author_id: users
    tables:
      common:
        applies_to: "/.*"
        clean_method: delete
  unit:
    <<: *shared
    tables:
      no_cleanup_users:
        applies_to: users
        clean_method: none
```

`fixtures/users.yml`:

```yml
vlad: { name: Vlad }
```

**Generated SQL (SQL-99), with the `default` profile above:**

```sql
DELETE FROM "users";
INSERT INTO "users" ("id", "name") VALUES (3722233, 'Vlad');
```

`fixtures/posts.yml`:

```yml
welcome: { author_id: vlad, title: Welcome }
```

**Generated SQL (SQL-99), with the `default` profile above:**

```sql
DELETE FROM "posts";
INSERT INTO "posts" ("id", "author_id", "title") VALUES (1233199618, 3722233, 'Welcome');
```

**Result.** `withProfile("default")` cleans and inserts both tables after
resolving `author_id`. `withProfile("unit")` inherits that reference but does
not clean `users`; `posts` is still cleaned. When `profiles.default` is in the
file, it is used as the default profile; an unknown named profile causes an
error.

## 17. Reusable row fragments with anchors

`fixtures/users.yml`:

```yml
.base: &base
  active: true
  locale: ru_RU

vlad:
  <<: *base
  name: Vlad

anna:
  <<: *base
  name: Anna
  locale: en_US
```

**Generated SQL (SQL-99):**

```sql
DELETE FROM "users";
INSERT INTO "users" ("id", "active", "locale", "name") VALUES (3722233, TRUE, 'ru_RU', 'Vlad');
INSERT INTO "users" ("id", "active", "locale", "name") VALUES (3098944, TRUE, 'en_US', 'Anna');
```

**Result.** The `.base` row is only a template and is not inserted. `vlad` is
created with `active: true, locale: ru_RU`, and `anna` with the same `active`
value and an overridden `locale: en_US`.

## Complete configuration template

The following lists every root-configuration key read by the current library
version. It is a **template**, not a single ready-to-use configuration: retain
only sections that apply to your fixtures. Its values are compatible when the
referenced files and tables exist. For profiles, move the required root
sections under `profiles.<name>`; recipe 16 shows an example.

```yml
# .conf.yml
id_generator: io.github.rodionovsasha.jfixtures.StringId.one
before_all: "SELECT 'fixture load started';"
after_all: "SELECT 'fixture load finished';"
clean_tables: [job_locks]

templates:
  enabled: true

tables:
  common:
    applies_to: "/.*"
    clean_method: delete # delete | truncate | truncate_cascade | none
    default_columns:
      tenant_id: 1
  users:
    applies_to: users
    pk:
      generate: true
      column: ${TABLE}_ID
      type: int # int | uuid
      id_generator: io.github.rodionovsasha.jfixtures.StringId.one
    timestamps:
      enabled: true
      value: "sql:CURRENT_TIMESTAMP"
    requires: [roles]
    before_cleanup: "DELETE FROM audit_log WHERE table_name = '$TABLE_NAME';"
    before_inserts: "SET CONSTRAINTS ALL DEFERRED;"
    after_inserts: "ANALYZE $TABLE_NAME;"
  orders:
    applies_to: orders
    pk:
      columns: [tenant_id, order_id]

refs:
  posts:
    author_id: users
  comments:
    author_public_id:
      table: users
      column: public_id
  line_items:
    order:
      table: orders
      columns:
        tenant_id: order_tenant_id
        order_id: order_number

polymorphic_refs:
  attachments:
    owner:
      id_column: owner_id
      type_column: owner_type
      types:
        User: users
        Post: posts

many_to_many:
  posts:
    tags:
      join_table: posts_tags
      source_column: post_id
      target_table: tags
      target_column: tag_id
```

### Format constraints

* Fixture files use the `.yml` or `.yaml` extension; both variants cannot
  exist for the same name. Hidden YAML files, including `.conf.yml`, do not
  become tables.
* The top level of a table file is a map of `row label → column map`. An empty
  list `[]` is also valid for an empty table. Scalars and non-empty lists are
  not supported.
* A label beginning with `.` is a helper and is not inserted.
* References, `requires`, and associations must point to existing tables and
  labels. Circular table dependencies produce a clear error.
