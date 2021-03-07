package com.iann0036.iamfast;

public class AWSCall {
    public String service;
    public String method;

    public AWSCall(String serviceInput, String methodInput) {
        service = serviceInput;
        method = methodInput;
    }
}
