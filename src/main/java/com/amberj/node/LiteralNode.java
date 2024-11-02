package com.amberj.node;

import com.amberj.ast.ExpressionNode;

public record LiteralNode(
    Object value
) implements ExpressionNode {
}
