package com.samknows.metrics;
/**
 * Rest Controller for the application
 */

import com.samknows.responses.ResponseBuilder;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/metrics")
public class MetricsController {

    static Logger logger = LogManager.getLogger(MetricsController.class.getName());
    @GetMapping
    public ResponseEntity testRequest(){
        logger.log(Level.INFO, "Logging: Test Request");
        return ResponseBuilder.success();
    }
}
