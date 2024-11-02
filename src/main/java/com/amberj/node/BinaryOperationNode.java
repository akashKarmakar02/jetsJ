package com.amberj.node;

import com.amberj.ast.ExpressionNode;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static com.amberj.shared.VariableIndex.variableIndexMap;

public record BinaryOperationNode(
    ExpressionNode left,
    String operator,
    ExpressionNode right
) implements ExpressionNode {

    public static String eval(BinaryOperationNode node) {
        String leftValue = evalNode(node.left());

        // Evaluate right operand
        String rightValue = evalNode(node.right());

        // Check the operator and perform the operation
        return switch (node.operator()) {
            case "+" -> String.valueOf(Integer.parseInt(leftValue) + Integer.parseInt(rightValue));
            case "-" -> String.valueOf(Integer.parseInt(leftValue) - Integer.parseInt(rightValue));
            case "*" -> String.valueOf(Integer.parseInt(leftValue) * Integer.parseInt(rightValue));
            case "/" -> String.valueOf(Integer.parseInt(leftValue) / Integer.parseInt(rightValue));
            default -> throw new UnsupportedOperationException("Unsupported operator: " + node.operator());
        };
    }

    public static void generateOperandBytecode(MethodVisitor mv, ExpressionNode node) {
        if (node instanceof LiteralNode literal) {
            int value = Integer.parseInt(literal.value().toString());
            mv.visitIntInsn(Opcodes.BIPUSH, value);
        } else if (node instanceof VariableReferenceNode referenceNode) {
            // If the operand is a variable, load it from the local variable table
            // Assuming variable index 1 for simplicity (this depends on your actual local variable index)
            mv.visitVarInsn(Opcodes.ILOAD, variableIndexMap.get(referenceNode.name()));
        } else if (node instanceof BinaryOperationNode binaryNode) {
            // Recursively handle nested binary operations
            if ("+".equals(binaryNode.operator())) {
                generateOperandBytecode(mv, binaryNode.left());
                generateOperandBytecode(mv, binaryNode.right());
                mv.visitInsn(Opcodes.IADD);
            }
            if ("*".equals(binaryNode.operator())) {
                generateOperandBytecode(mv, binaryNode.left());
                generateOperandBytecode(mv, binaryNode.right());
                mv.visitInsn(Opcodes.IMUL);
            }
            if ("-".equals(binaryNode.operator())) {
                generateOperandBytecode(mv, binaryNode.left());
                generateOperandBytecode(mv, binaryNode.right());
                mv.visitInsn(Opcodes.ISUB);
            }
            if ("/".equals(binaryNode.operator())) {
                generateOperandBytecode(mv, binaryNode.left());
                generateOperandBytecode(mv, binaryNode.right());
                mv.visitInsn(Opcodes.IDIV);
            }
            // Add support for other operators (-, *, /) similarly here if needed
        }
    }

    private static String evalNode(ExpressionNode node) {
        if (node instanceof LiteralNode literal) {
            return literal.value().toString(); // Assuming value() gives an Integer for simplicity
        } else if (node instanceof BinaryOperationNode binaryNode) {
            return eval(binaryNode); // Recursively evaluate binary operations
        }
        throw new UnsupportedOperationException("Unsupported node type: " + node.getClass().getSimpleName());
    }

}
