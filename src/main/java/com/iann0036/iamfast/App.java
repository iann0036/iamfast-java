package com.iann0036.iamfast;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.VariableDeclarator;
import java.io.File;
import java.io.FileNotFoundException;

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

        compilationUnit.findAll(VariableDeclarator.class).stream().forEach(
                f -> System.out.println("Check var at line" + f.getRange().map(r -> r.begin.line).orElse(-1)));
    }
}
