package com.scraperservice.view;

import javax.swing.*;
import java.awt.*;

public class StatisticFrame extends JFrame {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 480;

    public StatisticFrame() {
        setSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setVisible(true);
    }
}
