package com.amberj;
import com.amberj.ast.StatementNode;
import com.amberj.node.*;
import com.amberj.std.Console;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.List;

import static com.amberj.shared.VariableIndex.variableIndexMap;

public class CodeGenerator {
    private ClassWriter classWriter;
    int nextAvailableIndex = 1;

    public byte[] generate(List<StatementNode> ast) {
        classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        classWriter.visit(Opcodes.V21, Opcodes.ACC_PUBLIC, "main", null, "java/lang/Object", null);

        generateConstructor();

        generateMainMethod(ast);

        classWriter.visitEnd();
        return classWriter.toByteArray();
    }

    // Generate constructor
    private void generateConstructor() {
        MethodVisitor constructor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        constructor.visitCode();
        constructor.visitVarInsn(Opcodes.ALOAD, 0);
        constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        constructor.visitInsn(Opcodes.RETURN);
        constructor.visitMaxs(1, 1);
        constructor.visitEnd();
    }

    // Generate bytecode for variable declarations
    private void generateVariableDeclaration(MethodVisitor mv, VariableDeclarationNode node) {
        if (!variableIndexMap.containsKey(node.name())) {
            // Assign the next available index to this variable
            variableIndexMap.put(node.name(), nextAvailableIndex++);
        }

        int index = variableIndexMap.get(node.name());

        if (node.initializer() instanceof LiteralNode literalNode && literalNode.value() instanceof Integer value) {
            if (value >= -128 && value <= 127) {
                mv.visitIntInsn(Opcodes.BIPUSH, value);
            } else if (value >= -32768 && value <= 32767) {
                mv.visitIntInsn(Opcodes.SIPUSH, value);
            } else {
                mv.visitLdcInsn(value);
            }

            mv.visitVarInsn(Opcodes.ISTORE, index);
        }

        if (node.initializer() instanceof LiteralNode literalNode && literalNode.value() instanceof String value) {
            mv.visitLdcInsn(value);

            mv.visitVarInsn(Opcodes.ASTORE, index);
        }
    }

    private void generateMainMethod(List<StatementNode> ast) {
        MethodVisitor mv = classWriter.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        mv.visitCode();

        // Visit each statement in the AST and generate bytecode
        for (StatementNode statement : ast) {
            if (statement instanceof VariableDeclarationNode) {
                generateVariableDeclaration(mv, (VariableDeclarationNode) statement);
            } else if (statement instanceof ConsoleLogNode) {
                System.out.println(statement);
                Console.log(mv, (ConsoleLogNode) statement);
            }
        }

        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }
}
