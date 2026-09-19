package io.github.rodionovsasha.jfixtures.processor

import spock.lang.Specification

class IdentifierGeneratorTest extends Specification {

    def "invokes valid static generators"() {
        expect:
        IdentifierGenerator.generate("${Generators.name}.text", "vlad") == "id-vlad"
    }

    def "rejects invalid generator signatures and reflection failures"() {
        expect:
        invalid(path)

        where:
        path << [null, "", "method", "method.", "missing.Type.method", "${Generators.name}.instance",
                 "${Generators.name}.empty", "${Generators.name}.throwsError"]
    }

    private static boolean invalid(String path) {
        try {
            IdentifierGenerator.generate(path, "vlad")
            false
        } catch (ProcessorException ignored) {
            true
        }
    }

    static class Generators {
        static String text(String label) {
            "id-${label}"
        }

        String instance(String label) {
            label
        }

        static void empty(String label) {
        }

        static String throwsError(String label) {
            throw new IllegalStateException(label)
        }
    }
}
