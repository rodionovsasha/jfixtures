package com.github.vkorobkov.jfixtures.util;

import lombok.SneakyThrows;

import java.util.function.Function;

public final class WithResource {
    private WithResource() {
    }

    @SneakyThrows
    public static <TResource extends AutoCloseable, TResult> TResult touch(
            ThrowingSupplier<TResource> resourceSupplier,
            Function<TResource, TResult> callback) {
        try (TResource resource = resourceSupplier.get()) {
            return callback.apply(resource);
        }
    }

    public interface ThrowingSupplier<T> {
        T get() throws Exception;
    }
}
