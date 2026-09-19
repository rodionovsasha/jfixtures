package io.github.rodionovsasha.jfixtures;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Shortcuts for rendering fixture files in the common output formats.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Shortcuts {
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Str {
        public static String sql99(String fixturesPath) {
            return JFixtures.noConfig()
                    .load(fixturesPath)
                    .compile()
                    .toSql99()
                    .toString();
        }

        public static String sql99(String fixturesPath, String configPath) {
            return JFixtures.withConfig(configPath)
                    .load(fixturesPath)
                    .compile()
                    .toSql99()
                    .toString();
        }
    }
}
