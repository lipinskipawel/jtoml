package org.example.lexer;

import static java.lang.Character.isDigit;
import static java.lang.Character.isLetter;
import static java.util.Objects.requireNonNull;
import static org.example.lexer.TokenType.EOF;
import static org.example.lexer.TokenType.EQ;
import static org.example.lexer.TokenType.HASH;
import static org.example.lexer.TokenType.IDENT;
import static org.example.lexer.TokenType.QUOTED_IDENT;

public final class Lexer {
    private final String input;
    private final int inputLength;
    private int position; // current position in input (points to current char)
    private int readPosition; // current reading position in input (after current char)
    private char character; // current char under examination

    public Lexer(String input) {
        this.input = requireNonNull(input);
        this.inputLength = input.length();
        position = 0;
        readPosition = 0;
        character = 0;
        readChar();
    }

    public Token nextToken() {
        skipWhitespaces();
        return switch (character) {
            case '#' -> readComment();
            case '"' -> readQuotedIdentifiers();
            case '=' -> {
                readChar();
                yield EQ.token();
            }
            case 0 -> new Token(EOF, "");
            default -> {
                if (isIdentifier()) {
                    final var identifier = readIdentifier();
                    yield new Token(IDENT, identifier);
                }
                throw new IllegalArgumentException("Lexer does not support [%s]".formatted(character));
            }
        };
    }

    private void skipWhitespaces() {
        while (character == ' ' || character == '\b' || character == '\r' || character == '\n') {
            readChar();
        }
    }

    // LF -   0x0A
    // CRLF - 0x0D 0x0A
    private Token readComment() {
        var pos = readPosition;
        while (character != 0x0A) {
            readChar();
        }
        final var token = new Token(HASH, input.substring(pos, position));
        readChar();
        return token;
    }

    private Token readQuotedIdentifiers() {
        var pos = position;
        do {
            readChar();
        } while (character != '"');

        final var token = new Token(QUOTED_IDENT, input.substring(pos, readPosition));
        readChar();
        return token;
    }

    private boolean isIdentifier() {
        return isLetter(character) || isDigit(character) || character == '.' || character == '"' || character == ' ';
    }

    private String readIdentifier() {
        var pos = position;
        while (isIdentifier()) {
            readChar();
        }
        final var identifier = input.substring(pos, position).replace(" ", "");
        if (character == '=') {
            return identifier;
        }
        readChar();
        return identifier;
    }

    private void readChar() {
        if (readPosition < inputLength) {
            character = input.charAt(readPosition);
        } else {
            character = 0;
        }
        position = readPosition;
        readPosition++;
    }
}
