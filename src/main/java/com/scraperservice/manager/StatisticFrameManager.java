package com.scraperservice.manager;

import com.scraperservice.view.StatisticFrame;
import com.scraperservice.view.StatisticTextArea;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.Closeable;

public class StatisticFrameManager implements Closeable {
    private static final StatisticFrameManager statisticFrameManager = new StatisticFrameManager();
    private final Timer timer;
    private final JFrame jFrame;

    public static StatisticFrameManager getInstance() {
        return statisticFrameManager;
    }

    private StatisticFrameManager() {
        StatisticFrame statisticFrame = new StatisticFrame();
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
