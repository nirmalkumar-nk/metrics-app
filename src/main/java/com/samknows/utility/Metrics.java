package com.samknows.utility;

import java.util.Calendar;

/**
 * Class to hold the values from Input Json for "analyseMetrics" method in MetricsController
 */

public class Metrics implements Comparable<Metrics>{

    public static final String METRICVALUE_STRING = "metricValue";
    public static final String DTIME_STRING = "dtime";
    public Double metricValue;
    public Calendar date;

    public Metrics(Double metricValue, Calendar date){
        this.metricValue = metricValue;
        this.date = date;
    }

    @Override
    public int compareTo(Metrics o) {
        return metricValue.compareTo(o.metricValue);
    }

    public int dateCompare(Metrics o){
        return date.compareTo(o.date);
    }
}
