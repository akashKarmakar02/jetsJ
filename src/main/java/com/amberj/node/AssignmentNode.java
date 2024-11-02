package com.amberj.node;

import com.amberj.ast.ExpressionNode;
import com.amberj.ast.StatementNode;

record AssignmentNode(
    String name,
    ExpressionNode value
) implements StatementNode {}