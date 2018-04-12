package com.github.vkorobkov.jfixtures;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class JFixtures {
    private final Optional<String> config;

    public static JFixtures ofConfig(String config) {
        return new JFixtures(Optional.of(config));
    }

    public static JFixtures noConfig() {
        return new JFixtures(Optional.empty());
    }
}
