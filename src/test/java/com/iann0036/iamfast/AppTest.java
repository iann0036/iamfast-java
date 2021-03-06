package com.iann0036.iamfast;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;

public class AppTest 
{
    @Test
    public void shouldAnswerWithTrue()
    {
        File file = new File("./testFiles/s3Op.java");

        try {
            App.parseFile(file);
        } catch (FileNotFoundException e) {
            fail(e.getMessage());
        }

        assertTrue( true );

        System.out.println("Done.");
    }
}
