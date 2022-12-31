package com.samknows.metrics;

import com.samknows.responses.FailureResponse;
import com.samknows.responses.ResponseBuilder;
import com.samknows.server.ServerConfig;
import com.samknows.utility.JSONUtility;
import com.samknows.utility.MetricsFileTypes;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.stream.Collectors;

/**
 * Class to filter all requests before they reach the Spring Boot Container
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestFilter implements Filter {

    Logger LOGGER = LogManager.getLogger(RequestFilter.class.getName());
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        try {

            HttpServletRequest request = (HttpServletRequest) servletRequest;
            String path_method = request.getRequestURI()+"_"+request.getMethod();

            Hashtable<String, Node> requestMap = ServerConfig.serverConfig.getXmlRequestsMap();

            if(!requestMap.containsKey(path_method)){
                servletResponse = ResponseBuilder.requestNotFound(servletResponse);
                return;
            }

            //This filter functionality shall be scaled with minimal rewriting

            if(request.getContentType() == null || !request.getContentType().contains(MediaType.MULTIPART_FORM_DATA_VALUE)){
                LOGGER.log(Level.FATAL, "The request has no attachments");
                servletResponse = ResponseBuilder.badRequest(servletResponse, FailureResponse.ATTACHMENTS_MISSING);
                return;
            }

            Element requestNode = (Element) requestMap.get(path_method);
            Element attachment = (Element) requestNode.getElementsByTagName("attachments").item(0);
            ArrayList<String> allowedTypes = new ArrayList<>(Arrays.asList(attachment.getAttribute("type").split(",")));

            for (Part part: request.getParts()){
                LOGGER.log(Level.INFO, "Submitted File name: {}", new Object[]{part.getSubmittedFileName()});
                String submittedType =  part.getSubmittedFileName().split("\\.")[1];
                if(!allowedTypes.contains(submittedType)){
                    LOGGER.log(Level.FATAL, "The uploaded file type {} is not allowed for this request", new Object[]{submittedType});
                    servletResponse = ResponseBuilder.badRequest(servletResponse, FailureResponse.FILE_TYPE_NOT_ALLOWED);
                    return;
                }

                if(submittedType.equals(MetricsFileTypes.JSON.getFileType())){
                    //This is only for JSON file types
                    String inputJson = new BufferedReader(new InputStreamReader(part.getInputStream())).lines().collect(Collectors.joining("\n"));
                    if(!JSONUtility.isValidJSON(inputJson)){
                        LOGGER.log(Level.FATAL, "The JSON content of the file is not formed well");
                        servletResponse =  ResponseBuilder.badRequest(servletResponse, FailureResponse.JSON_NOT_FORMED_WELL);
                        return;
                    }
                }
            }

            LOGGER.log(Level.INFO, "Request filtered successfully");
            filterChain.doFilter(servletRequest, servletResponse);

        }catch (Exception ex){
            LOGGER.log(Level.FATAL, "Exception occurred while filtering the request {}", new Object[]{ex});
            servletResponse = ResponseBuilder.internalServerError(servletResponse);
        }
     }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
