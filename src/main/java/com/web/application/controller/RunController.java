package com.web.application.controller;

import com.scraperservice.ServerApplicationMain;
import com.web.application.entity.Run;
import com.web.application.service.RunService;
import com.scraperservice.utils.RandomStringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class RunController {
    @Autowired
    private RunService runService;

    @PostMapping(value = "/run", params = {"hash"})
    public ResponseEntity<String> getRunByHash(@RequestParam("hash") String hash) throws IOException {
        Run run = runService.getRunByHash(hash);

        if(run != null) {
            final String fileName = "temp/run/rod_" + RandomStringHelper.getRandomStringOnlyLetters(20) + ".txt";
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(fileName));
            objectOutputStream.writeObject(run);
            objectOutputStream.close();

            new Thread(() -> {
                try {
                    exec(ServerApplicationMain.class, null, Collections.singletonList(fileName));
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public static int exec(Class<?> clazz, List<String> jvmArgs, List<String> args) throws IOException,
            InterruptedException {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String className = clazz.getName();

        List<String> command = new ArrayList<>();
        command.add(javaBin);
        if(jvmArgs != null)
            command.addAll(jvmArgs);
        command.add("-cp");
        command.add(classpath);
        command.add(className);
        if(args != null)
            command.addAll(args);

        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = builder.inheritIO().start();
        process.waitFor();
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String str;
            while ((str = bufferedReader.readLine()) != null)
                System.out.println(str);
        }
        return process.exitValue();
    }
}
