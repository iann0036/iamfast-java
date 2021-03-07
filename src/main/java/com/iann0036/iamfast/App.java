package com.iann0036.iamfast;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;

import org.json.JSONArray;
import org.json.JSONObject;

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

        ArrayList<AWSCall> callLog = new ArrayList<AWSCall>();

        try {
            callLog = parseFile(file);
        } catch (FileNotFoundException e) {
            System.out.println("Couldn't file the specified file");
        }

        String policy = generatePolicy(callLog);

        System.out.println(policy);
    }

    public static String generatePolicy(ArrayList<AWSCall> callLog) {
        JSONObject policy = new JSONObject();

        JSONArray statements = new JSONArray();

        Iterator<AWSCall> callLogIter = callLog.iterator();
        while (callLogIter.hasNext()) {
            AWSCall call = callLogIter.next();

            JSONObject statement = new JSONObject();
            statement.put("Effect", "Allow");
            statement.put("Action", call.service + ":" + call.method);
            statement.put("Resource", "*");

            statements.put(statement);
        }
        
        policy.put("Version", "2012-10-17");
        policy.put("Statement", statements);

        return policy.toString(4);
    }

    public static ArrayList<AWSCall> parseFile(File file) throws FileNotFoundException {
        ArrayList<AWSCall> callLog = new ArrayList<AWSCall>();
        ArrayList<String> imports = new ArrayList<String>();
        HashMap<String, String> clientMap = new HashMap<String, String>();
        HashMap<String, String> requestMap = new HashMap<String, String>();

        CompilationUnit compilationUnit = StaticJavaParser.parse(file);

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
            /*
            System.out.println(node);
            System.out.println(node.getScope().isPresent());
            System.out.println(node.getName());
            System.out.println(node.getTypeArguments());
            System.out.println(node.getArguments());

            System.out.println("---");
            */

            if (node.getScope().isPresent()) {
                String scope = node.getScope().get().toString();
                if (clientMap.containsKey(scope)) {
                    String clientType = clientMap.get(scope);
                    String[] clientTypeSplit = clientType.split("\\.");

                    if (clientTypeSplit.length >= 2) {
                        String service = clientTypeSplit[clientTypeSplit.length - 2];

                        String method = node.getName().asString();
                        method = method.substring(0, 1).toUpperCase() + method.substring(1);

                        callLog.add(new AWSCall(service, method));
                    }
                }
            }
        });

        return callLog;
    }
}
