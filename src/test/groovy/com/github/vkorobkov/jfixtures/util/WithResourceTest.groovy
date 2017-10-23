package com.github.vkorobkov.jfixtures.util

import spock.lang.Specification

import java.util.function.Function

class WithResourceTest extends Specification {

    AutoCloseable resource
    Function<TestAutoCloseableImpl, String> function

    void setup() {
        resource = Spy(TestAutoCloseableImpl)
        function = Spy(FunctionImpl)
    }

    def "positive case"() {
        when:
        1 * function.apply(resource) >> "result"
        1 * resource.close()

        then:
        WithResource.touch({ resource }, function) == "result"
    }

    def "closes the resource even when function throws an exception"() {
        given:
        1 * function.apply(resource) >> { resource ->
            throw new RuntimeException("die")
        }

        when:
        WithResource.touch({ resource }, function)

        then:
        RuntimeException exception = thrown()
        exception.message == "die"

        and:
        1 * resource.close()
    }

    def "broadcasts the exception when the resource is null and function fails"() {
        given:
        1 * function.apply(null) >> { resource ->
            throw new RuntimeException("die")
        }

        when:
        WithResource.touch({ null }, function)

        then:
        RuntimeException exception = thrown()
        exception.message == "die"
    }

    def "works if resource is null"() {
        when:
        1 * function.apply(null) >> "result"

        then:
        WithResource.touch({ null }, function) == "result"
    }

    def "throws when resource's close method throws"() {
        given:
        1 * function.apply(resource) >> "result"
        1 * resource.close() >> { throw new RuntimeException("die") }

        when:
        WithResource.touch({ resource }, function)

        then:
        RuntimeException exception = thrown()
        exception.message == "die"
    }

    def "add suppressed throwable when both apply and close failed"() {
        given:
        1 * function.apply(resource) >> { resource ->
            throw new RuntimeException("Apply failed")
        }
        1 * resource.close() >> { throw new RuntimeException("Close failed") }

        when:
        WithResource.touch({ resource }, function)

        then:
        RuntimeException exception = thrown()
        exception.message == "Apply failed"
        (exception.suppressed as RuntimeException).message.endsWith("Close failed")
    }

    static class TestAutoCloseableImpl implements AutoCloseable {
        @Override
        void close() throws Exception {
        }
    }

    static class FunctionImpl implements Function<TestAutoCloseableImpl, String> {
        @Override
        String apply(TestAutoCloseableImpl testAutoCloseable) {
            return null
        }
    }
}
