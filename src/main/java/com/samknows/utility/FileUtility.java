package com.samknows.utility;

import com.samknows.server.ServerConfig;
import com.samknows.server.ServerConfigProperties;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to handle file related operations
 */
public class FileUtility {

    private static final Pattern PATTERN = Pattern.compile("(\\$\\{[^}]+})");
    public static final String UNDER_PERFORMANCE_AND = " and ";

    private static final String UNDER_PERFORMANCE = "Under-performing periods:\n" +
            "\n" +
            "    * The period between ${UNDER_PERFORMANCE_TIME_RANGE}\n" +
            "      was under-performing.";
    static Logger LOGGER = LogManager.getLogger(FileUtility.class.getName());

    public enum MetricsPatterns{
        FROM("${FROM}"),
        TO("${TO}"),
        AVERAGE("${AVG}"),
        MINIMUM("${MIN}"),
        MAXIMUM("${MAX}"),
        MEDIAN("${MEDIAN}"),
        PERFORMANCE("${PERFORMANCE}"),
        UNDER_PERFORMANCE_TIME_RANGE("${UNDER_PERFORMANCE_TIME_RANGE}");

        private final String pattern;
        MetricsPatterns(String pattern){
            this.pattern = pattern;
        }

        public String getPattern() {
            return pattern;
        }
    }

    public static byte[] writeMetricsFile(Hashtable<String, String> templateMap){
        try{
            String templateString = Files.readString(Paths.get(ServerConfig.getServerProperties().getProperty(ServerConfigProperties.ANALYSER_OUTPUT_TEMPLATE.getProperty())));

            StringBuilder stringBuilder = new StringBuilder();
            Matcher matcher = PATTERN.matcher(templateString);

            while (matcher.find()){
                String repString = templateMap.get(matcher.group(1));
                if(repString != null){
                    matcher.appendReplacement(stringBuilder, repString);
                }
            }
            matcher.appendTail(stringBuilder);

            return stringBuilder.toString().trim().getBytes();
        }catch (Exception ex){
            LOGGER.log(Level.FATAL, "Exception occurred while writing the file from file template {0}", new Object[]{ex} );
            return null;
        }
    }

    public static String formatUnderPerformance(String value){
        try {
            StringBuilder stringBuilder = new StringBuilder();
            Matcher matcher = PATTERN.matcher(UNDER_PERFORMANCE);
            while (matcher.find()){
                matcher.appendReplacement(stringBuilder, value);
            }
            matcher.appendTail(stringBuilder);
            return stringBuilder.toString();
        }catch (Exception ex){
            LOGGER.log(Level.FATAL, "Exception occurred while writing the file from file template {0}", new Object[]{ex} );
            return null;
        }
    }
}
