package io.github.rodionovsasha.jfixtures.processor;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/** Invokes the intentionally narrow static-method generator configured for a primary key. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class IdentifierGenerator {
    static Object generate(String methodPath, String label) {
        if (methodPath == null || methodPath.isBlank()) {
            throw invalid(String.valueOf(methodPath), null);
        }
        int separator = methodPath.lastIndexOf('.');
        if (separator <= 0 || separator == methodPath.length() - 1) {
            throw invalid(methodPath, null);
        }
        try {
            Class<?> type = Class.forName(methodPath.substring(0, separator));
            Method method = type.getMethod(methodPath.substring(separator + 1), String.class);
            if (!Modifier.isStatic(method.getModifiers()) || method.getReturnType() == Void.TYPE) {
                throw invalid(methodPath, null);
            }
            return method.invoke(null, label);
        } catch (ReflectiveOperationException cause) {
            throw invalid(methodPath, cause);
        }
    }

    private static ProcessorException invalid(String methodPath, Exception cause) {
        String message = "ID generator [" + methodPath
                + "] must be a public static method accepting String and returning a value";
        return cause == null ? new ProcessorException(message) : new ProcessorException(message, cause);
    }
}
