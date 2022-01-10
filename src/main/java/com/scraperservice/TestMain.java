package com.scraperservice;

import com.scraperservice.application.entity.Run;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class TestMain {
    public static void main(String[] args) throws Exception {
        if(args.length == 1) {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(args[0]));
            Run run = (Run) objectInputStream.readObject();
            objectInputStream.close();
            System.out.println(run);
        }
        else
            System.out.println("args.length = 0");
    }
}
