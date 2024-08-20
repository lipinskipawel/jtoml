package org.example.lexer;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.example.lexer.TokenType.EOF;
import static org.example.lexer.TokenType.EQ;
import static org.example.lexer.TokenType.HASH;
import static org.example.lexer.TokenType.IDENT;
import static org.example.lexer.TokenType.QUOTED_IDENT;

class LexerTest implements WithAssertions {

    @Test
    void recognise_tokens() {
        var toml = """
                #some comment
                id
                  anotherId
                moreId   \s
                physical.shape
                3.123
                "idAroundQuotes"
                key . withSpace
                site."google.com"
                "# this is not a comment"
                key =
                keyWith = "value"
                key . withSpace = "some"
                """;
        var expected = List.of(
                new Token(HASH, "some comment"),
                new Token(IDENT, "id"),
                new Token(IDENT, "anotherId"),
                new Token(IDENT, "moreId"),
                new Token(IDENT, "physical.shape"),
                new Token(IDENT, "3.123"),
                new Token(QUOTED_IDENT, "\"idAroundQuotes\""),
                new Token(IDENT, "key.withSpace"),
                new Token(IDENT, "site.\"google.com\""),
                new Token(QUOTED_IDENT, "\"# this is not a comment\""),
                new Token(IDENT, "key"),
                EQ.token(),
                new Token(IDENT, "keyWith"),
                EQ.token(),
                new Token(QUOTED_IDENT, "\"value\""),
                new Token(IDENT, "key.withSpace"),
                EQ.token(),
                new Token(QUOTED_IDENT, "\"some\""),
                new Token(EOF, "")
        );
        var lexer = new Lexer(toml);

        for (var token : expected) {
            var result = lexer.nextToken();

            assertThat(result).isEqualTo(token);
        }
    }
}
