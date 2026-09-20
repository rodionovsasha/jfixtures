# TODO

Open feature backlog, ordered from the least to the greatest estimated implementation effort as of 2026-09-19. Estimates include design work, API compatibility, and test surface.

## Completed

- [x] Add shortcut methods for converting fixture directories directly to SQL-99 text. [#194](https://github.com/rodionovsasha/jfixtures/issues/194)
- [x] Clean configured tables even when they have no fixture file. [#75](https://github.com/rodionovsasha/jfixtures/issues/75)
- [x] Render YAML binary and date values as valid SQL literals. [#92](https://github.com/rodionovsasha/jfixtures/issues/92), [#90](https://github.com/rodionovsasha/jfixtures/issues/90)

## Small

- [x] Review and close the stale Java 9 adoption task; the project now requires Java 17. [#63](https://github.com/rodionovsasha/jfixtures/issues/63)
- [x] Verify the implemented configuration-profile support against the issue acceptance criteria, document it, and close the still-open task. [#200](https://github.com/rodionovsasha/jfixtures/issues/200)
- [x] Publish a getting-started guide for JFixtures with Spring Boot and JUnit. [#71](https://github.com/rodionovsasha/jfixtures/issues/71)
- [x] Add custom SQL hooks that run once before or after the whole fixture set. [#77](https://github.com/rodionovsasha/jfixtures/issues/77)
- [x] Load `before_cleanup`, `before_inserts`, and `after_inserts` SQL from external files. [#30](https://github.com/rodionovsasha/jfixtures/issues/30)

## Medium

- [x] Make generated SQL formatting configurable. [#78](https://github.com/rodionovsasha/jfixtures/issues/78)
- [x] Allow a custom primary-key generator globally and per table. [#68](https://github.com/rodionovsasha/jfixtures/issues/68)
- [x] Add opt-in automatic values for conventional timestamp columns (`created_at`, `created_on`, `updated_at`, and `updated_on`) when a fixture omits them. [#214](https://github.com/rodionovsasha/jfixtures/issues/214)
- [x] Support computed primary-key column names. [#35](https://github.com/rodionovsasha/jfixtures/issues/35)
- [x] Define and implement handling for list or scalar values passed to the map-based fixture API. [#198](https://github.com/rodionovsasha/jfixtures/issues/198)
- [x] Allow explicit table ordering in addition to order inferred from foreign-key relationships. [#112](https://github.com/rodionovsasha/jfixtures/issues/112)
- [x] Support YAML anchors for reusable fixture fragments without inserting the anchor rows. [#123](https://github.com/rodionovsasha/jfixtures/issues/123)
- [x] Support YAML `!omap` so rows in one fixture file can be inserted in an explicit order, including self-referential foreign-key trees. [#210](https://github.com/rodionovsasha/jfixtures/issues/210)

## Large

- [x] Support inline, relative foreign-key references and references to non-primary-key columns. [#111](https://github.com/rodionovsasha/jfixtures/issues/111), [#32](https://github.com/rodionovsasha/jfixtures/issues/32)
- [x] Support fixture-label interpolation such as `$LABEL`, and expose a way to insert the deterministic identifier for another row label. [#211](https://github.com/rodionovsasha/jfixtures/issues/211)
- [x] Support UUID primary-key generation and label references for UUID-backed tables. [#215](https://github.com/rodionovsasha/jfixtures/issues/215)
- [x] Support polymorphic foreign-key references, resolving one readable value into both the target identifier and target type columns. [#212](https://github.com/rodionovsasha/jfixtures/issues/212)
- [x] Support inline many-to-many associations, expanding a list of row labels into rows for the configured join table. [#213](https://github.com/rodionovsasha/jfixtures/issues/213)
- [x] Add an optional fluent API for applying generated SQL directly through JDBC. [#34](https://github.com/rodionovsasha/jfixtures/issues/34)

## Extra large

- [x] Support composite primary keys and deterministic identifiers for their individual components when resolving relationships. [#216](https://github.com/rodionovsasha/jfixtures/issues/216)
- [x] Provide a safe, opt-in fixture template mechanism for generating repeated rows and computed values without executing arbitrary code. [#217](https://github.com/rodionovsasha/jfixtures/issues/217)
