/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Justin Swanson
 */
public class LAreaChart extends LChart {

    ArrayList<XYSeries> series = new ArrayList<XYSeries>();
    /**
     *
     */
    public XYPlot plot;
    XYSeriesCollection collection;

    /**
     *
     * @param title_
     * @param size_
     * @param titleColor
     * @param seriesColor
     * @param XLabel
     * @param YLabel
     */
    public LAreaChart(String title_, Dimension size_, Color titleColor, Color seriesColor,
	    String XLabel, String YLabel) {
	super(title_, size_);


	collection = new XYSeriesCollection();

	chart = ChartFactory.createXYAreaChart(
		null,
		XLabel, YLabel,
		collection,
		PlotOrientation.VERTICAL,
		false, // legend
		true, // tool tips
		false // URLs
		);

	plot = chart.getXYPlot();
	plot.setBackgroundPaint(Color.lightGray);
	plot.setBackgroundAlpha(0.0f);
	plot.setForegroundAlpha(0.70f);
	plot.setDomainGridlinePaint(Color.white);
	plot.setRangeGridlinePaint(Color.white);
	plot.getDomainAxis().setLabelPaint(Color.lightGray);
	plot.getRangeAxis().setLabelPaint(Color.lightGray);
	plot.getRangeAxis().setTickLabelsVisible(false);
	plot.getDomainAxis().setTickLabelPaint(Color.lightGray);
	plot.getDomainAxis().setAutoRange(false);
	plot.getDomainAxis().setRange(-15, 15);

	plot.setDomainGridlinesVisible(false);
	plot.setRangeGridlinesVisible(false);
	plot.setOutlineVisible(false);
	plot.getRangeAxis().setAxisLineVisible(false);
	plot.getRangeAxis().setTickMarksVisible(false);

	addSeries(seriesColor);

	init(title_, size_, titleColor);
	cPanel.setSize(cPanel.getWidth() - 12, cPanel.getHeight());
    }

    @Override
    public void paint(Graphics g) {
	super.paint(g);
	g.setColor(new Color(50, 180, 180));
	g.drawLine(this.getSize().width / 2 + 4, 30,
		this.getSize().width / 2 + 4, this.getSize().height - 36);
    }

    /**
     *
     */
    public void clear() {
	for (XYSeries s : series) {
	    s.clear();
	}
    }

    /**
     *
     * @param nthSeries
     * @param x
     * @param y
     */
    public void putPoint(int nthSeries, double x, double y) {
	series.get(nthSeries).add(x, y);
    }

    /**
     *
     * @param seriesColor
     */
    public final void addSeries(Color seriesColor) {
	XYSeries seriesNew = new XYSeries("Series");
	series.add(seriesNew);
	collection.addSeries(seriesNew);

	XYItemRenderer renderer = plot.getRenderer();
	renderer.setSeriesPaint(series.size() - 1, seriesColor);
    }

    /**
     *
     */
    public void resetDomain() {
	Range domain = plot.getDataRange(plot.getDomainAxis());
	double set = domain.getLowerBound();
	if (domain.getUpperBound() > Math.abs(set)) {
	    set = domain.getUpperBound();
	}
	if (Math.abs(set) < 15) {
	    set = 15;
	}
	plot.getDomainAxis().setRange(-Math.abs(set), Math.abs(set));
    }
}
