/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

/**
 *
 * @author Justin Swanson
 */
public class LChart extends JPanel {

    /**
     *
     */
    protected JFreeChart chart;
    /**
     *
     */
    protected ChartPanel cPanel;
    /**
     *
     */
    protected LLabel title;

    /**
     *
     * @param title_
     * @param size_
     */
    public LChart(String title_, Dimension size_) {
        this.setLayout(null);
        super.setSize(size_.width, size_.height);
    }

    /**
     *
     * @param title_
     * @param size_
     * @param c
     */
    protected void init(String title_, Dimension size_, Color c) {

        if (title_ != null) {
            title = new LLabel(title_, new Font("Serif", Font.BOLD, 14), c);
            title.setLocation(getWidth() / 2 - title.getWidth() / 2, 0);
            add(title);
        }

        chart.setBackgroundPaint(new Color(0, 0, 0, 0));

        cPanel = new ChartPanel(chart);
        cPanel.setVisible(true);
        cPanel.setOpaque(false);
        Border border = BorderFactory.createLineBorder(Color.black, 0);
        cPanel.setBorder(border);
        if (title == null) {
            cPanel.setSize(size_);
        } else {
            cPanel.setSize(size_.width, size_.height - title.getHeight());
            cPanel.setLocation(0, title.getHeight());
        }

        add(cPanel);
        setOpaque(false);
    }

}
