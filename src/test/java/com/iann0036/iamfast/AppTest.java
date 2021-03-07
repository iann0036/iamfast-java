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
        String filename = "./testFiles/s3CreateBucket.java";
        System.out.println("FILE: " + filename);
        App.processFile(filename);
    }

    @Test
    public void ec2StartInstance()
    {
        String filename = "./testFiles/ec2StartInstance.java";
        System.out.println("FILE: " + filename);
        App.processFile(filename);
    }
}
