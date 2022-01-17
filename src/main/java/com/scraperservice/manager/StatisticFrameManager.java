package com.scraperservice.manager;

import com.scraperservice.view.StatisticFrame;
import com.scraperservice.view.StatisticTextArea;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.Closeable;

@Component
@Scope("singleton")
public class StatisticFrameManager implements Closeable {
    private final Timer timer;
    private final JFrame jFrame;

    public StatisticFrameManager() {
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
