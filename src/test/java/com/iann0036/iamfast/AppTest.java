package com.iann0036.iamfast;

import static org.junit.Assert.fail;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;

public class AppTest 
{
    @Test
    public void s3CreateBucket()
    {
        File file = new File("./testFiles/s3CreateBucket.java");

        try {
            App.parseFile(file);
        } catch (FileNotFoundException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void ec2StartInstance()
    {
        File file = new File("./testFiles/ec2StartInstance.java");

        try {
            App.parseFile(file);
        } catch (FileNotFoundException e) {
            fail(e.getMessage());
        }
    }
}
