package com.amberj;

public record Token(TokenType type, String value, Type dataType) {

    @Override
    public String toString() {
        return "Token{" +
            "type=" + type +
            ", value='" + value + '\'' +
            '}';
    }
}
