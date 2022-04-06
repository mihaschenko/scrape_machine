package com.scraperservice.view;

import com.scraperservice.manager.StatisticManager;
import com.scraperservice.view.StatisticTextArea;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.Closeable;

@Component
@Scope("singleton")
public class StatisticFrameBuilder implements Closeable {
    private final Timer timer;
    private final JFrame jFrame;

    public StatisticFrameBuilder() {
        com.scraperservice.view.StatisticFrame statisticFrame = new com.scraperservice.view.StatisticFrame();
        jFrame = statisticFrame;
        StatisticTextArea statisticTextArea = new StatisticTextArea();
        statisticFrame.add(statisticTextArea);

        ActionListener taskPerformer = evt -> statisticTextArea.setText(StatisticManager.getInstance().toString());
        timer = new Timer(3000, taskPerformer);
        timer.start();
    }

    @Override
    public void close() {
        timer.stop();
        jFrame.dispose();
    }
}
