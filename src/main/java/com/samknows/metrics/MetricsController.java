package com.samknows.metrics;
/**
 * Rest Controller for the application
 */

import com.fasterxml.jackson.databind.JsonNode;
import com.samknows.responses.FailureResponse;
import com.samknows.responses.ResponseBuilder;
import com.samknows.utility.FileUtility;
import com.samknows.utility.JSONUtility;
import com.samknows.utility.Metrics;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

@RestController
@RequestMapping("/metrics")
public class MetricsController {

    private final static double MEGABITS = 125000;
    private final static DecimalFormat df = new DecimalFormat("#.##");
    private final static long DAY_IN_MILLIS = 86400000;
    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    static Logger LOGGER = LogManager.getLogger(MetricsController.class.getName());
    @PostMapping
    public ResponseEntity analyseMetrics(@RequestParam("file") MultipartFile file){
        try{

            String fileAsString = new String(file.getBytes());
            JsonNode inputNode = JSONUtility.getJSONNode(fileAsString);

            //Preliminary checks
            if(inputNode.isNull() || inputNode.isEmpty()){
             LOGGER.log(Level.FATAL, "The json in the input file is null or empty");
             return ResponseBuilder.badRequest(FailureResponse.EMPTY_JSON);
            }

            if(!inputNode.isArray()){
                LOGGER.log(Level.FATAL, "Expected JSON Array but received JSON");
                return ResponseBuilder.badRequest(FailureResponse.JSON_ARRAY_EXPECTED);
            }


            Double total = 0d; //for calculating the total
            ArrayList<Metrics> metrics = new ArrayList<>();
            ArrayList<Metrics> underPerformance = new ArrayList<>();

            //to calculate overall time frame of the file
            Calendar from = Calendar.getInstance();
            Calendar to = Calendar.getInstance();

            from.setTime(sdf.parse(inputNode.get(0).get(Metrics.DTIME_STRING).asText()));
            to.setTime(sdf.parse(inputNode.get(0).get(Metrics.DTIME_STRING).asText()));

            for (int i=0; i<inputNode.size(); i++){
                Double metricsValue = inputNode.get(i).get(Metrics.METRICVALUE_STRING).doubleValue();
                total+=metricsValue;

                Calendar date = Calendar.getInstance();
                date.setTime(sdf.parse(inputNode.get(i).get(Metrics.DTIME_STRING).asText()));

                if(date.before(from)){
                    from.setTime(date.getTime());
                } else if (date.after(to)) {
                    to.setTime(date.getTime());
                }

                Metrics metric = new Metrics(metricsValue, date);

                metrics.add(metric);

                if (metricsValue < 10000000){
                    underPerformance.add(metric);
                }
            }

            //sorting the values with respect to metricValue
            metrics.sort(Metrics::compareTo);

            //sorting the values with respect to dtime - later to analyse date
            underPerformance.sort(Metrics::dateCompare);

            Double median;
            Double avg = total/ metrics.size()/ MEGABITS;
            Double min = metrics.get(0).metricValue / MEGABITS;
            Double max = metrics.get(metrics.size()-1).metricValue / MEGABITS;

            if(metrics.size()%2 == 0){
                median = ((metrics.get(metrics.size()/2 -1).metricValue + metrics.get(metrics.size()/2).metricValue) / 2) / MEGABITS;
            }else {
                median = metrics.get(metrics.size()/2 - 1).metricValue / MEGABITS;
            }

            //Under-performance Calculations
            ArrayList<Calendar> u_from = new ArrayList<>();
            ArrayList<Calendar> u_to = new ArrayList<>();

            String underPerformanceMsg;
            if(underPerformance.size() > 0){
                u_from.add(underPerformance.get(0).date);
                u_to.add(underPerformance.get(0).date);

                int i = 0;
                for (Metrics metric: underPerformance) {
                    if(metric.date.getTimeInMillis() - u_to.get(i).getTimeInMillis() <= DAY_IN_MILLIS){
                        u_to.set(i, metric.date);
                    }else{
                        ++i;
                        u_from.add(metric.date);
                        u_to.add(metric.date);
                    }
                }

                StringBuilder uMsg = new StringBuilder(); //Under Performance msg
                for (int j = 0; j < u_from.size(); j++) {
                    uMsg.append(sdf.format(u_from.get(j).getTime()));
                    uMsg.append(FileUtility.UNDER_PERFORMANCE_AND);
                    uMsg.append(sdf.format(u_to.get(j).getTime()));
                    if(j != u_from.size()-1){
                        uMsg.append(",\n\t\t");
                    }
                }

                underPerformanceMsg = FileUtility.formatUnderPerformance(uMsg.toString());
            }else {
                underPerformanceMsg = "";
            }

            Hashtable<String, String> replacements = new Hashtable<>();
            replacements.put(FileUtility.MetricsPatterns.FROM.getPattern(), sdf.format(from.getTime()));
            replacements.put(FileUtility.MetricsPatterns.TO.getPattern(), sdf.format(to.getTime()));
            replacements.put(FileUtility.MetricsPatterns.MINIMUM.getPattern(), df.format(min));
            replacements.put(FileUtility.MetricsPatterns.MAXIMUM.getPattern(), df.format(max));
            replacements.put(FileUtility.MetricsPatterns.AVERAGE.getPattern(), df.format(avg));
            replacements.put(FileUtility.MetricsPatterns.MEDIAN.getPattern(), df.format(median));
            replacements.put(FileUtility.MetricsPatterns.PERFORMANCE.getPattern(), underPerformanceMsg);

            return ResponseBuilder.writeFile(FileUtility.writeMetricsFile(replacements));
        }catch (Exception ex){
            LOGGER.log(Level.FATAL, "Exception: {}", new Object[]{ex});
            return ResponseBuilder.internalServerError();
        }
    }
}
