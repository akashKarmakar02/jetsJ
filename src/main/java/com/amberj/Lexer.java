package com.amberj;

import java.util.Set;

public class Lexer {
    private static final Set<String> keywords = Set.of("const", "let", "var", "console", "log");
    private static final Set<String> operators = Set.of("+", "-", "=", "*", "/", "%");
    private static final Set<String> punctuation = Set.of("!", "\"", "#", "$", "&", "'", "(", ")", ",", ".", ":", ";", "<", ">", "?", "@", "[", "\\", "]", "^", "_", "`", "{", "|", "}", "~");

    private boolean isStringStarted = false;

    private final String input;
    private int position = 0;

    public Lexer(String input) {
        this.input = input;
    }

    public Token nextToken() {
        skipWhitespace();

        if (position >= input.length()) {
            return new Token(TokenType.EOF, "", Type.Special);
        }

        char currentChar = input.charAt(position);

        // Check for strings
        if (currentChar == '"' || currentChar == '\'') {
            isStringStarted = !isStringStarted;
        }

        if (punctuation.contains(String.valueOf(currentChar))) {
            String punct = input.substring(position, position + 1);
            position++;
            return new Token(TokenType.PUNCTUATION, punct, Type.Special);
        }


        if (isDigit(currentChar)) {
            int start = position;
            while (position < input.length() && isDigit(input.charAt(position))) {
                position++;
            }
            String value = input.substring(start, position);
            return new Token(TokenType.LITERAL, value, Type.Int);
        }

        // Check for keywords and identifiers
        if (isLetter(currentChar)) {
            int start = position;
            while (position < input.length() && isLetterOrDigit(input.charAt(position))) {
                position++;
            }
            String value = input.substring(start, position);
            if (keywords.contains(value)) {
                return new Token(TokenType.KEYWORD, value, Type.Special);
            } else if (isStringStarted) {
                return new Token(TokenType.LITERAL, value, Type.String);
            } else {
                return new Token(TokenType.IDENTIFIER, value, Type.Special);
            }
        }

        // Check for operators
        if (operators.contains(String.valueOf(currentChar))) {
            String operator = input.substring(position, position + 1);
            position++;
            return new Token(TokenType.OPERATOR, operator, Type.Special);
        }

        throw new IllegalArgumentException("Unexpected character: " + currentChar);
    }


    // Helper functions to check characters
    private boolean isLetter(char c) {
        return Character.isLetter(c);
    }

    private boolean isLetterOrDigit(char c) {
        return Character.isLetterOrDigit(c);
    }

    private boolean isDigit(char c) {
        return Character.isDigit(c);
    }

    // Skips over any whitespace (spaces, tabs, newlines)
    private void skipWhitespace() {
        while (position < input.length() && Character.isWhitespace(input.charAt(position))) {
            position++;
        }
    }
}
