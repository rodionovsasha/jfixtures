package io.github.rodionovsasha.jfixtures;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Creates stable long identifiers from fixture labels. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LongId {
    public static long one(String label) {
        return Integer.toUnsignedLong(label.hashCode());
    }
}
