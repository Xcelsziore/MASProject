package com.example.masproject;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;

public class LineGraph{

	public GraphicalView getView(Context context, double[] y1, double[] y2, double[] y3) {
		// Add data to series
		int[] x = {1, 2, 3, 4, 5, 6, 7}; 
		TimeSeries series = new TimeSeries("Physical     "); 
		for(int i = 0; i < x.length; i++) {
			series.add(x[i], y1[i]);
		}		
		TimeSeries series2 = new TimeSeries("Mental      "); 
		for(int i = 0; i < x.length; i++) {
			series2.add(x[i], y2[i]);
		}
		TimeSeries series3 = new TimeSeries("Social"); 
		for(int i = 0; i < x.length; i++) {
			series3.add(x[i], y3[i]);
		}
		// Create dataset
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(series);
		dataset.addSeries(series2);
		dataset.addSeries(series3);
		// Create renderer
		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer(); 
		XYSeriesRenderer renderer = new XYSeriesRenderer(); // This will be used to customize line 1
		XYSeriesRenderer renderer2 = new XYSeriesRenderer(); // This will be used to customize line 2
		XYSeriesRenderer renderer3 = new XYSeriesRenderer(); // This will be used to customize line 3
		mRenderer.addSeriesRenderer(renderer);
		mRenderer.addSeriesRenderer(renderer2);
		mRenderer.addSeriesRenderer(renderer3);
		//Customization for whole graph
		mRenderer.setShowGrid(true);		
		mRenderer.setGridColor(Color.LTGRAY);
		mRenderer.setBackgroundColor(Color.WHITE);
		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setMarginsColor(Color.WHITE);
		mRenderer.setMargins(new int[] { 25, 45, 50, 25 });
		mRenderer.setLabelsColor(Color.BLACK);
		mRenderer.setAxesColor(Color.BLACK);
		mRenderer.setXLabelsColor(Color.BLACK);
		mRenderer.setXLabels(0);
		mRenderer.setYLabels(8);
		mRenderer.setLabelsTextSize(25);
		mRenderer.setLegendTextSize(40);
		mRenderer.setFitLegend(true);
		mRenderer.setPointSize(6f);
		mRenderer.setYLabelsColor(0, Color.BLACK);
		mRenderer.setYLabelsAlign(Align.RIGHT);
		// Add day labels to x axis
		mRenderer.addXTextLabel(1, "M");
		mRenderer.addXTextLabel(2, "Tu");
		mRenderer.addXTextLabel(3, "W");
		mRenderer.addXTextLabel(4, "Th");
		mRenderer.addXTextLabel(5, "F");
		mRenderer.addXTextLabel(6, "Sa");
		mRenderer.addXTextLabel(7, "Su");
		// Customization time for line 1
		renderer.setColor(Color.BLUE);
		renderer.setPointStyle(PointStyle.DIAMOND);
		renderer.setFillPoints(true);
		// Customization time for line 2
		renderer2.setColor(Color.RED);
		renderer2.setPointStyle(PointStyle.SQUARE);
		renderer2.setFillPoints(true);
		// Customization time for line 3
		renderer3.setColor(Color.GREEN);
		renderer3.setPointStyle(PointStyle.CIRCLE);
		renderer3.setFillPoints(true);
		
		return ChartFactory.getLineChartView(context, dataset, mRenderer);
		
	}

}
