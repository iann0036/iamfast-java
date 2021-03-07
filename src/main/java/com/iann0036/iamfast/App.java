package com.iann0036.iamfast;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Not enough arguments");
            return;
        }

        processFile(args[0]);
    }

    public static void processFile(String filename) {
        File file = new File(filename);

        App app = new App();

        ArrayList<AWSCall> callLog = new ArrayList<AWSCall>();

        try {
            callLog = app.parseFile(file);
        } catch (FileNotFoundException e) {
            System.out.println("Couldn't file the specified file");
        }

        JSONArray statements = new JSONArray();

        Iterator<AWSCall> callLogIter = callLog.iterator();
        while (callLogIter.hasNext()) {
            AWSCall call = callLogIter.next();
            
            JSONArray callStatements = app.callToPrivileges(call);
            for (int i=0; i<callStatements.length(); i++) {
                statements.put(callStatements.getJSONObject(i));
            }
        };

        String policy = app.generatePolicy(statements);

        System.out.println(policy);
    }

    public JSONArray callToPrivileges(AWSCall call) {
        JSONArray statements = new JSONArray();

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("map.json");
        JSONTokener tokener = new JSONTokener(inputStream);
        JSONObject iamMap = new JSONObject(tokener);
        
        /*
        InputStream inputStream2 = classLoader.getResourceAsStream("lib/parliament/iam_definition.json");
        JSONTokener tokener2 = new JSONTokener(inputStream2);
        JSONObject iamDef = new JSONObject(tokener2);
        */

        JSONObject iamMapMethods = iamMap.getJSONObject("sdk_method_iam_mappings");

        Iterator<String> methodIter = iamMapMethods.keys();
        while (methodIter.hasNext()) {
            String k = methodIter.next();

            if (k.toLowerCase() == call.toString().toLowerCase()) {
                JSONArray methodsForCall = iamMapMethods.getJSONArray(k);
                
                for (int i=0; i<methodsForCall.length(); i++) {
                    JSONObject methodForCall = methodsForCall.getJSONObject(i);

                    String action = methodForCall.get("action").toString();

                    JSONObject statement = new JSONObject();
                    statement.put("Effect", "Allow");
                    statement.put("Action", action);
                    statement.put("Resource", "*");

                    statements.put(statement);
                }
            }
        }

        return statements;
    }

    public String generatePolicy(JSONArray statements) {
        JSONObject policy = new JSONObject();
        
        policy.put("Version", "2012-10-17");
        policy.put("Statement", statements);

        return policy.toString(4);
    }

    public ArrayList<AWSCall> parseFile(File file) throws FileNotFoundException {
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
