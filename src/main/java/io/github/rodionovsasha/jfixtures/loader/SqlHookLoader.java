package io.github.rodionovsasha.jfixtures.loader;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/** Loads conventional fixture-set hooks from the hidden .before and .after directories. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SqlHookLoader {
    public static Hooks load(Path fixtureDirectory) {
        return new Hooks(loadDirectory(fixtureDirectory.resolve(".before")),
                loadDirectory(fixtureDirectory.resolve(".after")));
    }

    @SneakyThrows
    private static List<String> loadDirectory(Path directory) {
        if (!Files.isDirectory(directory)) {
            return Collections.emptyList();
        }
        try (Stream<Path> files = Files.list(directory)) {
            return files.filter(Files::isRegularFile)
                    .filter(SqlHookLoader::isSql)
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .map(SqlHookLoader::read)
                    .toList();
        }
    }

    private static boolean isSql(Path path) {
        return path.getFileName().toString().endsWith(".sql");
    }

    @SneakyThrows
    private static String read(Path path) {
        return Files.readString(path).stripTrailing();
    }

    public record Hooks(List<String> before, List<String> after) {
        public Hooks {
            before = List.copyOf(before);
            after = List.copyOf(after);
        }

        public static Hooks empty() {
            return new Hooks(Collections.emptyList(), Collections.emptyList());
        }

        public Hooks append(Hooks additional) {
            return new Hooks(Stream.concat(before.stream(), additional.before.stream()).toList(),
                    Stream.concat(after.stream(), additional.after.stream()).toList());
        }
    }
}
