package org.example.parser;

import org.example.lexer.Lexer;
import org.example.lexer.Token;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.example.lexer.TokenType.EQ;
import static org.example.lexer.TokenType.IDENT;
import static org.example.lexer.TokenType.QUOTED_IDENT;

public final class Parser {
    private final Lexer lexer;
    private Token currentToken;

    public Parser(Lexer lexer) {
        this.lexer = requireNonNull(lexer);
    }

    public List<Node> parseProgram() {
        final var program = new ArrayList<Node>();

        readToken();
        while (!currentToken.type().isEof()) {
            final var node = parseNode(currentToken);
            program.add(node);
            readToken();
        }

        return program;
    }

    private Node parseNode(Token token) {
        return switch (token.type()) {
            case IDENT, QUOTED_IDENT -> parseIdentifier(token);
            default -> throw new IllegalArgumentException("token type not supported [%s]".formatted(token.type()));
        };
    }

    private Node parseIdentifier(Token token) {
        final var eq = readToken();
        if (eq.type() != EQ) {
            throw new IllegalArgumentException("Expected EQ('=') but got [%s]".formatted(eq.type()));
        }

        final var value = readToken();
        if (value.type() == IDENT) {
            return new KeyValuePair<>(toValue(token), toValue(value));
        }
        if (value.type() == QUOTED_IDENT) {
            return new KeyValuePair<>(toValue(token), toValue(value));
        }

        throw new IllegalArgumentException("Expected EQ('=') but got [%s]".formatted(eq.type()));
    }

    private String toValue(Token identOrQuotedIdent) {
        return switch (identOrQuotedIdent.type()) {
            case IDENT -> identOrQuotedIdent.value();
            case QUOTED_IDENT -> identOrQuotedIdent.value().substring(1, identOrQuotedIdent.value().length() - 1);
            default -> throw new IllegalArgumentException("Only IDENT or QUOTED_IDENT are allowed, got [%s]"
                    .formatted(identOrQuotedIdent.type()));
        };
    }

    private Token readToken() {
        currentToken = lexer.nextToken();
        return currentToken;
    }
}
