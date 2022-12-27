package com.samknows.metrics;

import com.samknows.server.ServerConfig;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SamKnows {
    static Logger LOGGER = LogManager.getLogger(SamKnows.class.getName());
    public static void main(String[] args){
        ServerConfig serverConfig = ServerConfig.getInstance();
        if(serverConfig == null || !ServerConfig.isServerConfigured()){
            LOGGER.log(Level.FATAL, "Server not configured. Application server prohibited from starting");
            return;
        }
        LOGGER.log(Level.INFO, "***** Starting Application Server *****");
        SpringApplication.run(SamKnows.class);
    }
}
