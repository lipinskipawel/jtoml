package org.example.parser;

import org.assertj.core.api.WithAssertions;
import org.example.lexer.Lexer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

class ParserTest implements WithAssertions {

    private static Stream<Arguments> keyValuePairs() {
        return Stream.of(
                arguments("""
                        some = "text"
                        """, "some", "text"),
                arguments("""
                        "some_quoted" = "text"
                        """, "some_quoted", "text")
        );
    }

    @ParameterizedTest
    @MethodSource("keyValuePairs")
    void parse_key_value_pair(String input, String expectedKey, String expectedValue) {
        var parser = parser(input);

        var program = parser.parseProgram();

        assertThat(program).satisfies(it -> {
            assertThat(it).hasSize(1);
            assertThat(it.getFirst()).isInstanceOf(KeyValuePair.class);
            var keyValuePair = (KeyValuePair<?>) it.getFirst();
            assertThat(keyValuePair.key()).isEqualTo(expectedKey);
            assertThat(keyValuePair.value()).isEqualTo(expectedValue);
        });
    }

    private Parser parser(String input) {
        final var lexer = new Lexer(input);
        return new Parser(lexer);
    }
}