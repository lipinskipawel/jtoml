package org.example.lexer;

public enum TokenType {

    HASH("#"),
    IDENT(""),
    QUOTED_IDENT("\""),
    EQ("="),
    EOF("");

    private final String representation;

    TokenType(String representation) {
        this.representation = representation;
    }

    public Token token() {
        return new Token(this, representation);
    }

    public boolean isEof() {
        return this == EOF;
    }
}
