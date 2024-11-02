package com.amberj.node;

import com.amberj.ast.ExpressionNode;
import com.amberj.ast.StatementNode;

public record VariableDeclarationNode(
    String name,
    ExpressionNode initializer,
    boolean isConstant
) implements StatementNode {
}
