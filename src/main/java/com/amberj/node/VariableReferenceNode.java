package com.amberj.node;

import com.amberj.Type;
import com.amberj.ast.ExpressionNode;

public record VariableReferenceNode(
    String name,
    Type type
) implements ExpressionNode {
}
