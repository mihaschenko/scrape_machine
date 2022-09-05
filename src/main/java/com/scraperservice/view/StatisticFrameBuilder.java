package com.scraperservice.view;

import com.scraperservice.manager.StatisticManager;
import com.scraperservice.view.StatisticTextArea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.Closeable;

@Component
public class StatisticFrameBuilder implements Closeable {
    private final Timer timer;
    private final JFrame jFrame;
    @Autowired
    private StatisticManager statisticManager;

    public StatisticFrameBuilder() {
        com.scraperservice.view.StatisticFrame statisticFrame = new com.scraperservice.view.StatisticFrame();
        jFrame = statisticFrame;
        StatisticTextArea statisticTextArea = new StatisticTextArea();
        statisticFrame.add(statisticTextArea);

        ActionListener taskPerformer = evt -> statisticTextArea.setText(statisticManager.toString());
        timer = new Timer(10000, taskPerformer);
        timer.start();
    }

    @Override
    @PreDestroy
    public void close() {
        timer.stop();
        jFrame.dispose();
    }
}
