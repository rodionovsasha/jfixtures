# TODO

Open feature backlog, based on the repository issues as of 2026-09-19.

- [ ] Define and implement handling for list or scalar values passed to the map-based fixture API. [#198](https://github.com/rodionovsasha/jfixtures/issues/198)
- [ ] Add shortcut methods for common conversions such as a directory directly to SQL-99 text. [#194](https://github.com/rodionovsasha/jfixtures/issues/194)
- [ ] Support YAML anchors for reusable fixture fragments without inserting the anchor rows. [#123](https://github.com/rodionovsasha/jfixtures/issues/123)
- [ ] Allow explicit table ordering in addition to ordering inferred from foreign-key relationships. [#112](https://github.com/rodionovsasha/jfixtures/issues/112)
- [ ] Support inline, relative foreign-key references and references to non-primary-key columns. [#111](https://github.com/rodionovsasha/jfixtures/issues/111), [#32](https://github.com/rodionovsasha/jfixtures/issues/32)
- [ ] Render YAML binary and date values as valid SQL literals. [#92](https://github.com/rodionovsasha/jfixtures/issues/92), [#90](https://github.com/rodionovsasha/jfixtures/issues/90)
- [ ] Make generated SQL formatting configurable. [#78](https://github.com/rodionovsasha/jfixtures/issues/78)
- [ ] Add custom SQL hooks that run once before or after the whole fixture set. [#77](https://github.com/rodionovsasha/jfixtures/issues/77)
- [ ] Clean configured tables even when they have no fixture file. [#75](https://github.com/rodionovsasha/jfixtures/issues/75)
- [ ] Publish a getting-started guide for JFixtures with Spring Boot and JUnit. [#71](https://github.com/rodionovsasha/jfixtures/issues/71)
- [ ] Allow a custom primary-key generator globally and per table. [#68](https://github.com/rodionovsasha/jfixtures/issues/68)
- [ ] Support computed primary-key column names. [#35](https://github.com/rodionovsasha/jfixtures/issues/35)
- [ ] Add an optional fluent API for applying generated SQL directly through JDBC. [#34](https://github.com/rodionovsasha/jfixtures/issues/34)
- [ ] Load `before_cleanup`, `before_inserts`, and `after_inserts` SQL from external files. [#30](https://github.com/rodionovsasha/jfixtures/issues/30)
- [ ] Review and close the stale Java 9 adoption task; the project now requires Java 17. [#63](https://github.com/rodionovsasha/jfixtures/issues/63)
- [ ] Verify the implemented configuration-profile support against the issue acceptance criteria, document it, and close the still-open task. [#200](https://github.com/rodionovsasha/jfixtures/issues/200)
