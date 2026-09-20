# Getting started with Spring Boot and JUnit

JFixtures can load deterministic test data into the same `DataSource` used by a Spring Boot application. This guide uses JUnit 5 and Spring Boot's test support.

Add JFixtures to the test classpath:

```xml
<dependency>
  <groupId>io.github.rodionovsasha</groupId>
  <artifactId>jfixtures</artifactId>
  <version>3.1.0</version>
  <scope>test</scope>
</dependency>
```

Create `src/test/resources/fixtures/users.yml`:

```yml
vlad:
  name: Vladimir
  email: vlad@example.test
```

For an integration test, clean the relevant tables and then load the fixtures before each test. JFixtures uses the passed `DataSource` connection and closes it after applying the statements.

```java
import io.github.rodionovsasha.jfixtures.JFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;

@SpringBootTest
class UserRepositoryTest {
    private final DataSource dataSource;

    UserRepositoryTest(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @BeforeEach
    void loadFixtures() {
        JFixtures.withConfig("src/test/resources/fixtures/.conf.yml")
                .load("src/test/resources/fixtures")
                .apply(dataSource);
    }

    @Test
    void findsVladimir() {
        // exercise the repository
    }
}
```

The matching `src/test/resources/fixtures/.conf.yml` can keep cleanup separate from the fixture data:

```yml
tables:
  users:
    applies_to: users
    clean_method: delete
```

For transactional unit tests, use a profile that disables cleanup. The default profile can contain shared references, and a named profile can inherit it with YAML anchors:

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

Select it with `.withProfile("unit")`. Calling `.withDefaultProfile()` selects `default`. An unknown non-default profile fails fast, so a spelling error cannot silently use production-like cleanup settings.
