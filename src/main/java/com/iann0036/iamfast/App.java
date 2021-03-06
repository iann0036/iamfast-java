package com.iann0036.iamfast;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;

public class App 
{
    public static void main( String[] args )
    {
        CompilationUnit compilationUnit = StaticJavaParser.parse("class A {}");
        
        compilationUnit.findAll(FieldDeclaration.class).stream()
            .filter(f -> f.isPublic() && !f.isStatic())
            .forEach(f -> System.out.println("Check field at line" +
                f.getRange().map(r -> r.begin.line).orElse(-1)));

        System.out.println("TBC");
    }
}
