package com.scraperservice.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StatisticTextArea extends JTextArea {
    private static final int WIDTH = 400;
    private static final int HEIGHT = 300;

    public StatisticTextArea() {
        setSize(new Dimension(400, 300));
        setBorder(new EmptyBorder(0,0,0,0));
        setEnabled(false);
        setDisabledTextColor(Color.BLACK);
    }
}
