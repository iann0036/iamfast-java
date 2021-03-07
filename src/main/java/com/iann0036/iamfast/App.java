package com.iann0036.iamfast;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class App {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Not enough arguments");
            return;
        }

        File file = new File(args[0]);

        try {
            parseFile(file);
        } catch (FileNotFoundException e) {
            System.out.println("Couldn't file the specified file");
        }
    }

    public static void parseFile(File file) throws FileNotFoundException {
        CompilationUnit compilationUnit = StaticJavaParser.parse(file);

        ArrayList<String> imports = new ArrayList<String>();
        HashMap<String, String> clientMap = new HashMap<String, String>();
        HashMap<String, String> requestMap = new HashMap<String, String>();

        compilationUnit.findAll(ImportDeclaration.class).forEach(node -> {
            imports.add(node.getNameAsString());
        });

        compilationUnit.findAll(VariableDeclarationExpr.class).forEach(node -> {
            node.getVariables().forEach(v -> {
                String name = v.getNameAsString();
                String type = v.getTypeAsString();

                Iterator<String> iter = imports.iterator();
                while (iter.hasNext()) {
                    String importItem = iter.next();
                    if (importItem.endsWith("." + type) && type.endsWith("Client")) {
                        clientMap.put(name, importItem);
                    } else if (importItem.endsWith(".model." + type) && type.endsWith("Request")) {
                        requestMap.put(name, importItem);
                    }
                }
            });
        });

        compilationUnit.findAll(MethodCallExpr.class).forEach(node -> {
            System.out.println(node);

            System.out.println(node.getScope());
            System.out.println(node.getName());
            System.out.println(node.getTypeArguments());
            System.out.println(node.getArguments());

            System.out.println("---");
        });
    }
}
