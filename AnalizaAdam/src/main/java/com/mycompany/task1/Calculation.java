/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.task1;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Adam
 */
public class Calculation {

    private File file;
    private String separator;
    private BufferedReader br;
    private int numberOfColumn;
    private static final int lastColumn = 1;
    private int numberOfIntigerColumn;
    private SortedSet classTypeCollection;
    //DecimalFormat df;

    public Calculation(File file, String separator) {
        this.file = file;
        this.separator = separator;
        String line;
        String[] lineInList;
        String classType;
        classTypeCollection = new TreeSet();

        try {
            br = new BufferedReader(new FileReader(this.file));
            line = br.readLine();
            lineInList = line.split(separator);
            numberOfColumn = lineInList.length;

            while ((line = br.readLine()) != null) {
                lineInList = line.split(separator);
                classType = lineInList[lineInList.length - 1];
                classTypeCollection.add(classType);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Calculation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Calculation.class.getName()).log(Level.SEVERE, null, ex);
        }
        numberOfIntigerColumn = numberOfColumn - lastColumn;
    }

    private double round(double value, double accuracy) {
        return Math.round(value * accuracy) / accuracy;
    }

    public double quartile(double quartileNumber, int column, Object classType) {
        BufferedReader br;
        String line;
        String[] lineInList;
        List<Double> list;
        list = new ArrayList<>();
        int numberOfLine = 0;

        try {
            br = new BufferedReader(new FileReader(file));

            while ((line = br.readLine()) != null) {
                lineInList = line.split(separator);
                if ((lineInList[lineInList.length - 1]).equals(classType)) {
                    double value = Double.parseDouble(lineInList[column]);
                    list.add(value);
                    //numberOfLine++;
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Calculation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Calculation.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Sorting
        Collections.sort(list, new Comparator<Double>() {
            @Override
            public int compare(Double val1, Double val2) {

                return val1.compareTo(val2);
            }
        });

        int quartileIndex = (int) (list.size() * quartileNumber);

        return round(list.get(quartileIndex), 10000.0);

    }

    public double centralMoment(int power, int column, Object classType) {
        BufferedReader br;
        String line;
        String[] lineInList;
        double sum = 0;
        int numberOfLine = 0;

        try {
            br = new BufferedReader(new FileReader(file));

            while ((line = br.readLine()) != null) {
                lineInList = line.split(separator);
                if ((lineInList[lineInList.length - 1]).equals(classType)) {
                    double value = Double.parseDouble(lineInList[column]);
                    double substraction = value - arithmeticAverage(column, classType);
                    sum += Math.pow(substraction, power);
                    numberOfLine++;
                }

            }

        } catch (IOException ex) {
            Logger.getLogger(Calculation.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        return round(sum / numberOfLine, 1000.0);
    }

    public double standardDeviation(int column, Object classType) {
        double standardDeviation = Math.sqrt(centralMoment(2, column, classType));
        return round(standardDeviation, 10000.0);
    }

    //współczynnik skośności
    public double skewnessFactor(int col, Object classType) {
        double skewnessFactor
                = (arithmeticAverage(col, classType) - quartile(0.50, col, classType))
                / standardDeviation(col, classType);
        return round(skewnessFactor, 1000.0);
    }

    //współczynnik asymetrii
    public double skewness(int col, Object classType) {
        double skewness
                = centralMoment(3, col, classType) / standardDeviation(col, classType);
        return round(skewness, 1000.0);
    }

    public double kurtosis(int col, Object classType) {
        double kurtosis
                = centralMoment(4, col, classType) / Math.pow(standardDeviation(col, classType), 4);
        return round(kurtosis, 10000.0);
    }

    public ArrayList<Double> getDataFromColumn(int column, Object classType) {

        //Map data = new HashMap();
        ArrayList<Double> data = new ArrayList();
        BufferedReader br;
        String line;
        String[] lineInList;

        try {
            br = new BufferedReader(new FileReader(file));

            while ((line = br.readLine()) != null) {
                lineInList = line.split(separator);
                if ((lineInList[lineInList.length - 1]).equals(classType)) {
                    double value = Double.parseDouble(lineInList[column]);
                    data.add(value);
                }

            }

        } catch (IOException ex) {
            Logger.getLogger(Calculation.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        Collections.sort(data, new ValueComparator());
        return data;
    }

    /**
     *
     * @param column
     * @param classType
     * @return
     */
    public Map<Double, Integer> histogramData(int column, Object classType) {
        ArrayList<Double> dataWithDuplicate = getDataFromColumn(column, classType);

        SortedSet<Double> dataWithoutDuplicate = new TreeSet();

        for (double val1 : dataWithDuplicate) {
            for (double val2 : dataWithDuplicate) {
                if (val1 == val2 ) {
                    
                    dataWithoutDuplicate.add(val1);
                }
            }
        }

        Map<Double, Integer> histogramData = new TreeMap();
        for (double val1 : dataWithoutDuplicate) {
            int counter = 0;//usun 0
            for (double val2 : dataWithDuplicate) {
                if (val1 == val2 ) {
                    counter++;
                    histogramData.put(val1, counter);
                }
            }
        }

        return histogramData;
    }

    public Map standarization(int column, Object classType) {
        Map<Double, Integer> dataBeforStandarization
                = histogramData(column, classType);
        Map<Double, Integer> dataAfterStandarization = new TreeMap();

        for (double key : dataBeforStandarization.keySet()) {
            double keyAfter;
            keyAfter = (key - arithmeticAverage(column, classType))
                    / standardDeviation(column, classType);
            double roundKeyAfter = round(keyAfter, 1000.0);
            dataAfterStandarization.put(roundKeyAfter, dataBeforStandarization.get(key));
        }

        return dataAfterStandarization;
    }

    public double arithmeticAverage(int column, Object classType) {
        BufferedReader br;
        String line;
        String[] lineInList;
        double sum = 0;
        int numberOfLine = 0;

        try {
            br = new BufferedReader(new FileReader(file));

            while ((line = br.readLine()) != null) {
                lineInList = line.split(separator);
                if ((lineInList[lineInList.length - 1]).equals(classType)) {
                    double value = Double.parseDouble(lineInList[column]);
                    sum += value;
                    numberOfLine++;
                }

            }

        } catch (IOException ex) {
            Logger.getLogger(Calculation.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        double average = Math.round(sum / numberOfLine * 1000.0) / 1000.0;

        return average;
    }

    public StringBuilder hypothesisTesting() {
        StringBuilder result = new StringBuilder();
        int col = 1;
        double m1 = arithmeticAverage(col, "Iris-setosa");
        double m2 = arithmeticAverage(col, "Iris-versicolor");
        double s1 = standardDeviation(col, "Iris-setosa");
        double s2 = standardDeviation(col, "Iris-versicolor");
        int N1 = getDataFromColumn(col, "Iris-setosa").size();
        int N2 = getDataFromColumn(col, "Iris-versicolor").size();

        double counter = m1 - m2;
        double denominatorRoots
                = Math.pow(s1, 2) / N1 + Math.pow(s2, 2) / N2;
        double denominator = Math.pow(denominatorRoots, 0.5);

        double z = counter / denominator;

        result.append("m1 = ").append(m1).append(System.getProperty("line.separator"));
        result.append("m2 = ").append(m2).append(System.getProperty("line.separator"));
        result.append("s1 = ").append(s1).append(System.getProperty("line.separator"));
        result.append("s2 = ").append(s2).append(System.getProperty("line.separator"));
        result.append("z = ").append(z).append(System.getProperty("line.separator"));

        double s12_N1 = Math.pow(s1, 2) / N1;
        double s22_N2 = Math.pow(s2, 2) / N2;

        double studentCounter = Math.pow(s12_N1 + s22_N2, 2);
        double studentDeterminator
                = (Math.pow(s12_N1, 2)) / (N1 - 1) 
                + (Math.pow(s22_N2, 2)) / (N2 - 1);

        double d_f = studentCounter / studentDeterminator;
        
        result.append("Rozkład studenta= ").append(d_f).append(System.getProperty("line.separator"));

        return result;
    }

    public StringBuilder resultForOneClass(Object classType) {
        StringBuilder resultForClass = new StringBuilder();
        resultForClass.append("Wyniki dla klasy ").append(classType).append(System.getProperty("line.separator"));

        resultForClass.append("1. Miary srednie klasyczne i pozycyjne: ").append(System.getProperty("line.separator"));
        resultForClass.append("Srednia artmetyczna(wartość oczekiwana): ");
        for (int i = 0; i < numberOfIntigerColumn; i++) {
            resultForClass.append(arithmeticAverage(i, classType)).append(", ");
        }
        resultForClass.append(System.getProperty("line.separator"));

        resultForClass.append("Pierwszy kwartyl: ");
        for (int i = 0; i < numberOfIntigerColumn; i++) {
            resultForClass.append(quartile(0.25, i, classType)).append(", ");
        }
        resultForClass.append(System.getProperty("line.separator"));

        resultForClass.append("Drugi kwartyl: ");
        for (int i = 0; i < numberOfIntigerColumn; i++) {
            resultForClass.append(quartile(0.50, i, classType)).append(", ");
        }
        resultForClass.append(System.getProperty("line.separator"));

        resultForClass.append("Trzeci kwartyl: ");
        for (int i = 0; i < numberOfIntigerColumn; i++) {
            resultForClass.append(quartile(0.75, i, classType)).append(", ");
        }
        resultForClass.append(System.getProperty("line.separator"));

        resultForClass.append("2. Miary rozproszenia ").append(System.getProperty("line.separator"));
        resultForClass.append("2 moment centralny - wariancja: ");
        for (int i = 0; i < numberOfIntigerColumn; i++) {
            resultForClass.append(centralMoment(2, i, classType)).append(", ");
        }
        resultForClass.append(System.getProperty("line.separator"));

        resultForClass.append("Odchylenie standardowe: ");
        for (int i = 0; i < numberOfIntigerColumn; i++) {
            resultForClass.append(standardDeviation(i, classType)).append(", ");
        }
        resultForClass.append(System.getProperty("line.separator"));

        resultForClass.append("3. Miary asymetrii ").append(System.getProperty("line.separator"));

        resultForClass.append("Współczynnik skośności: ");
        for (int i = 0; i < numberOfIntigerColumn; i++) {
            resultForClass.append(skewnessFactor(i, classType)).append(", ");
        }
        resultForClass.append(System.getProperty("line.separator"));

        resultForClass.append("Współczynnik asymetrii: ");
        for (int i = 0; i < numberOfIntigerColumn; i++) {
            resultForClass.append(skewness(i, classType)).append(", ");
        }
        resultForClass.append(System.getProperty("line.separator"));

        resultForClass.append("4. Miary koncentracji ").append(System.getProperty("line.separator"));
        resultForClass.append("Współczynnik kurtozy: ");
        for (int i = 0; i < numberOfIntigerColumn; i++) {
            resultForClass.append(kurtosis(i, classType)).append(", ");
        }
        resultForClass.append(System.getProperty("line.separator"));

        resultForClass.append("5. Normalizacja zmiennej losowej. Dopasowanie rozkładu i analiza danych w jego kontekście.").
                append(System.getProperty("line.separator"));

        resultForClass.append("Dane przed normalizacją: ").append(System.getProperty("line.separator"));
        for (int i = 0; i < numberOfIntigerColumn; i++) {
            resultForClass.append("Kolumna").append(i).append(System.getProperty("line.separator"));
            resultForClass.append(getDataFromColumn(i, classType)).append(System.getProperty("line.separator"));
            resultForClass.append(histogramData(i, classType)).append(System.getProperty("line.separator"));
        }
        resultForClass.append(System.getProperty("line.separator"));

        resultForClass.append("Dane po normalizacji: ").append(System.getProperty("line.separator"));
        for (int i = 0; i < numberOfIntigerColumn; i++) {
            resultForClass.append("Kolumna").append(i).append(System.getProperty("line.separator"));
            resultForClass.append(standarization(i, classType)).append(System.getProperty("line.separator"));
        }
        resultForClass.append(System.getProperty("line.separator"));

        resultForClass.append("Testowanie hipotez: ").append(System.getProperty("line.separator"));
        resultForClass.append(hypothesisTesting()).append(System.getProperty("line.separator"));

        resultForClass.append(System.getProperty("line.separator"));

        resultForClass.append(System.getProperty("line.separator")).append(System.getProperty("line.separator"));

        return resultForClass;
    }

    public StringBuilder result() {
        StringBuilder finalResult = new StringBuilder();

        for (Object classType : getClassTypeCollection()) {
            finalResult.append(resultForOneClass(classType));
        }
        return finalResult;
    }

    int getNumberOfColumn() {
        return numberOfColumn;
    }

    /**
     * @return the classTypeCollection
     */
    public SortedSet getClassTypeCollection() {
        return classTypeCollection;
    }

    /**
     * @param classTypeCollection the classTypeCollection to set
     */
    public void setClassTypeCollection(SortedSet classTypeCollection) {
        this.classTypeCollection = classTypeCollection;
    }

}

class ValueComparator implements Comparator<Double> {

    @Override
    public int compare(Double o1, Double o2) {
        return o1.compareTo(o2);
    }

}
