package io.github.rodionovsasha.jfixtures;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/** Creates stable UUID identifiers from fixture labels. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UuidId {
    public static UUID one(String label) {
        return UUID.nameUUIDFromBytes(label.getBytes(StandardCharsets.UTF_8));
    }
}
