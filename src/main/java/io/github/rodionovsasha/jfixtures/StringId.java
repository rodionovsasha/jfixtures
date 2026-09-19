package io.github.rodionovsasha.jfixtures;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Uses fixture labels as stable string identifiers. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StringId {
    public static String one(String label) {
        return label;
    }
}
