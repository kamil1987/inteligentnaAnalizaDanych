/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.task1;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author Adam
 */
public class Chart {

    private Calculation calculation;

    public Chart(Calculation calculation) {
        this.calculation = calculation;
    }

    // ten sam obiekt
    public void makeChart(int kolumna) {

        SortedSet sortedSet = calculation.getClassTypeCollection();
        DefaultCategoryDataset dataSet = new DefaultCategoryDataset();

        for (Object object : sortedSet) {
            Map<Double, Integer> mapa = calculation.histogramData(kolumna, object);
            loadData(mapa, object, dataSet);
        }
        try {
            JFreeChart chart = displayChart(dataSet);
            ChartUtilities.saveChartAsJPEG(new File("chart.jpg"), chart, 1000, 700);
        } catch (IOException ex) {
            Logger.getLogger(Chart.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void makeGroupedDataChart(int kolumna) {
        SortedSet sortedSet = calculation.getClassTypeCollection();
        DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
        TreeMap<Double, Integer> groupedMap;
        groupedMap = new TreeMap<Double, Integer>();
        
        // mapa zawiera dane jednej kolumny dla jednej cechy nominalnej
        for (Object object : sortedSet) {

            Map<Double, Integer> mapa = calculation.histogramData(kolumna, object);
            prepareRangeMap(groupedMap, mapa);
            group(groupedMap, mapa);
            loadData(groupedMap, object, dataSet);
        }

        try {
            JFreeChart chart = displayChart(dataSet);
            ChartUtilities.saveChartAsJPEG(new File("GroupedData.jpg"), chart, 1000, 700);
        } catch (IOException ex) {
            Logger.getLogger(Chart.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void prepareRangeMap(TreeMap<Double, Integer> groupedMap, Map<Double, Integer> mapa) {
        //Object[] tab = null;
       // tab = mapa.keySet().toArray();
        double min =0; //(Double) tab[0];
        double max =6; //(Double) tab[tab.length - 1];
        double range = (max - min) / 10;
        double newRange = range;

        for (int i = 0; i < 10; i++) {
            groupedMap.put(newRange, 0);
            newRange += range;
        }
    }

    private void group(TreeMap<Double, Integer> groupedMap, Map<Double, Integer> mapa) {
        // przejdz przez pogrupowana mape i zwieksz wartosc jezeli wartosc klucza z niepogrupowanej mapy 
        //znajduje sie w przedziale
        for (Map.Entry<Double, Integer> entry : mapa.entrySet()) {
            double prevRange = 0;
            for (Map.Entry<Double, Integer> entryGroupedMap : groupedMap.entrySet()) {

                if (prevRange <= entry.getKey() && entry.getKey() <= entryGroupedMap.getKey()) {

                    int numberOfAccurence = entryGroupedMap.getValue();
                    numberOfAccurence++;
                    groupedMap.put(entryGroupedMap.getKey(), numberOfAccurence);
                }

                prevRange = entryGroupedMap.getKey();
            }

        }

    }

    private void loadData(Map<Double, Integer> mapa, Object object, DefaultCategoryDataset dataSet) {

        for (Map.Entry<Double, Integer> entry : mapa.entrySet()) {

            dataSet.setValue(entry.getValue(), (String) object, String.format("%.2f",entry.getKey()));
           
        }

        //return dataSet;
    }

    private JFreeChart displayChart(DefaultCategoryDataset dataSet) {

        JFreeChart chart;
        chart = ChartFactory.createBarChart("Irysy", "ilość", " ", dataSet, PlotOrientation.VERTICAL, true, true, true);
        CategoryPlot plot = chart.getCategoryPlot();
        //chart.removeLegend();
        plot.setRangeGridlinePaint(Color.black);
        ChartFrame frame;
        frame = new ChartFrame("Irysy", chart);
        frame.setVisible(true);
        frame.setSize(700, 550);

        return chart;
    }

}
