package com.iann0036.iamfast;

import static org.junit.Assert.fail;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class AppTest
{
    @Test
    public void s3CreateBucket()
    {
        File file = new File("./testFiles/s3CreateBucket.java");

        ArrayList<AWSCall> callLog = new ArrayList<AWSCall>();

        try {
            callLog = App.parseFile(file);
        } catch (FileNotFoundException e) {
            fail(e.getMessage());
        }

        String policy = App.generatePolicy(callLog);

        System.out.println(policy);
    }

    @Test
    public void ec2StartInstance()
    {
        File file = new File("./testFiles/ec2StartInstance.java");

        ArrayList<AWSCall> callLog = new ArrayList<AWSCall>();

        try {
            callLog = App.parseFile(file);
        } catch (FileNotFoundException e) {
            fail(e.getMessage());
        }

        String policy = App.generatePolicy(callLog);

        System.out.println(policy);
    }
}
