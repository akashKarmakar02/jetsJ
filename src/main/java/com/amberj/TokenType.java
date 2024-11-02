package com.amberj;

public enum TokenType {
    KEYWORD,       // const, let, var
    IDENTIFIER,    // variable names like x, y
    OPERATOR,      // +, -, =, etc.
    LITERAL,       // numbers, strings
    PUNCTUATION,   // (, ), ;
    EOF            // End of file
}