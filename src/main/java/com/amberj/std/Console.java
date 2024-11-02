package com.amberj.std;

import com.amberj.node.BinaryOperationNode;
import com.amberj.node.ConsoleLogNode;
import com.amberj.node.LiteralNode;
import com.amberj.node.VariableReferenceNode;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static com.amberj.shared.VariableIndex.variableIndexMap;

public class Console {

    public static void log(MethodVisitor mv, ConsoleLogNode node) {
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");

        if (node.argument() instanceof LiteralNode literalNode) {
            Object value = literalNode.value();

            if (value instanceof Integer) {
                mv.visitIntInsn(Opcodes.BIPUSH, (int) value);
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);

            } else if (value instanceof Float) {
                mv.visitLdcInsn(value);
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(F)V", false);

            } else if (value instanceof String) {
                mv.visitLdcInsn(value);
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            }

        } else if (node.argument() instanceof VariableReferenceNode referenceNode) {
            int index = variableIndexMap.get(referenceNode.name());
            mv.visitVarInsn(Opcodes.ALOAD, index);

            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
//            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/Object;)V", false);
//            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

        } else if (node.argument() instanceof BinaryOperationNode operationNode) {
            BinaryOperationNode.generateOperandBytecode(mv, operationNode.left());
            BinaryOperationNode.generateOperandBytecode(mv, operationNode.right());

            switch (operationNode.operator()) {
                case "+":
                    mv.visitInsn(Opcodes.IADD);
                    break;
                case "-":
                    mv.visitInsn(Opcodes.ISUB);
                    break;
                case "*":
                    mv.visitInsn(Opcodes.IMUL);
                    break;
                case "/":
                    mv.visitInsn(Opcodes.IDIV);
                    break;
            }


            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
        }
    }

}
