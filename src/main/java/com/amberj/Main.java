package com.amberj;

import com.amberj.parser.Parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Main {
        public static void main(String[] args) {

            String code = "";
            String fileName = "index.js";

//            if (args.length > 0) {
//                fileName = args[0];
//            } else {
//                System.out.println("No file.");
//                return;
//            }

            try {
                Path filePath = Paths.get(fileName);
                code = Files.readString(filePath);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (Objects.equals(code, "")) {
                return;
            }

            Lexer lexer = new Lexer(code);
            List<Token> tokens = new ArrayList<>();
            Token token;

            do {
                token = lexer.nextToken();
                tokens.add(token);
            } while (token.type() != TokenType.EOF);

//            for(var t: tokens) {
//                System.out.println(t);
//            }

            // Parse tokens to create the AST
            Parser parser = new Parser(tokens);
            var ast = parser.parse();

            System.out.println(ast);
//
            CodeGenerator codeGenerator = new CodeGenerator();
            byte[] bytecode = codeGenerator.generate(ast);

            Path path = Paths.get("main.class");
            try {
                Files.write(path, bytecode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }