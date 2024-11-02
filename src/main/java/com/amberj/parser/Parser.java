package com.amberj.parser;

import com.amberj.Token;
import com.amberj.TokenType;
import com.amberj.Type;
import com.amberj.ast.ExpressionNode;
import com.amberj.ast.StatementNode;
import com.amberj.node.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Parser {
    private final List<Token> tokens;
    private int position = 0;
    private String optionalSkip = Arrays.toString(new String[]{"\"", "'", ";", "\\n"});

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    // Helper to get the current token
    private Token currentToken() {
        return tokens.get(position);
    }

    // Helper to advance to the next token
    private void advance() {
        if (position < tokens.size()) {
            position++;
        }
    }

    // Main function to parse a list of statements
    public List<StatementNode> parse() {
        List<StatementNode> statements = new ArrayList<>();
        while (currentToken().type() != TokenType.EOF) {
            statements.add(parseStatement());
        }
        return statements;
    }

    // Parse a single statement
    private StatementNode parseStatement() {
        Token token = currentToken();
        if (Objects.requireNonNull(token.type()) == TokenType.KEYWORD) {
            if (token.value() instanceof String value) {
                switch (value) {
                    case "const":
                        return parseVariableDeclaration(true);
                    case "let":
                    case "var":
                        return parseVariableDeclaration(false);
                    case "console":
                        return parseConsoleLog();
                }
            }
        }
        throw new IllegalArgumentException("Unexpected token: " + token.value());
    }

    // Parse a variable declaration (const, let, var)
    private VariableDeclarationNode parseVariableDeclaration(boolean isConstant) {
        advance();  // Skip the 'const', 'let', or 'var' keyword
        String name = currentToken().value();
        advance();  // Skip the identifier
        expect("=");
        expect("\"");
        expect("'");
        ExpressionNode initializer = parseExpression();
        expect("'");
        expect("\"");
        expect(";");
        expect("\\n");
        return new VariableDeclarationNode(name, initializer, isConstant);
    }

    // Parse console.log() statement
    private ConsoleLogNode parseConsoleLog() {
        advance();  // Skip 'console'
        expect(".");
        expect("log");
        expect("(");
        expect("\"");
        expect("'");
        ExpressionNode argument = parseExpression();  // Parse the argument inside console.log()
        expect("'");
        expect("\"");
        expect(")");
        expect(";");

        return new ConsoleLogNode(argument);
    }

    // Parse an expression (for now, we'll only handle literals and binary expressions)
    private ExpressionNode parseExpression() {
        ExpressionNode left = parsePrimary();  // Parse the left-hand side (literal or variable)

        // Check if there's a binary operator
        if (currentToken().type() == TokenType.OPERATOR) {
            String operator = currentToken().value();
            advance();  // Skip the operator
            ExpressionNode right = parsePrimary();  // Parse the right-hand side
            return new BinaryOperationNode(left, operator, right);
        }

        return left;  // If no operator, return the left-hand side as the expression
    }

    // Parse primary expressions (literals or variables)
    private ExpressionNode parsePrimary() {
        Token token = currentToken();
        return switch (token.type()) {
            case LITERAL -> {
                advance();
                if (token.dataType() == Type.Int) {
                    yield new LiteralNode(Integer.parseInt(token.value()));
                }
                yield new LiteralNode(token.value());
            }
            case IDENTIFIER -> {
                advance();
                System.out.println("Here");
                yield new VariableReferenceNode(token.value(), token.dataType());
            }
            default -> throw new IllegalArgumentException("Unexpected token: " + token.value());
        };
    }

    // Helper to expect a specific token and throw an error if it's not found
    private void expect(String expected) {
        if (!currentToken().value().equals(expected) && !optionalSkip.contains(expected)) {
            throw new IllegalArgumentException("Expected " + expected + " but found " + currentToken().value());
        }
        if (optionalSkip.contains(expected) && currentToken().value().equals(expected))
            advance();
        if (!optionalSkip.contains(expected))
            advance();
    }
}
