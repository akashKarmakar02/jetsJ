package com.amberj.node;

import com.amberj.ast.ExpressionNode;
import com.amberj.ast.StatementNode;

public record ConsoleLogNode(ExpressionNode argument) implements StatementNode {
}
