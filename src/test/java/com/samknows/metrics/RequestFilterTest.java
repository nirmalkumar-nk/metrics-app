package com.samknows.metrics;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.samknows.responses.FailureResponse;
import com.samknows.responses.ResponseKey;
import com.samknows.server.ServerConfig;
import com.samknows.utility.JSONUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.mock.web.MockPart;
import org.springframework.web.context.support.HttpRequestHandlerServlet;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class RequestFilterTest {

    private final String url = "/metrics";
    @InjectMocks
    private RequestFilter requestFilter;

    private MockMultipartHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;
    private MockPart filePart;
    private byte[] file;

    private ObjectNode mockResponse;
    @BeforeEach
    public void setup(){
        try {
            requestFilter = new RequestFilter();

            request = new MockMultipartHttpServletRequest();
            request.setMethod(HttpMethod.GET.name());
            request.setRequestURI(url);
            request.setContentType(MediaType.MULTIPART_FORM_DATA_VALUE);
            request.addHeader("Accept", "*/*");
            request.addHeader("Accept-Encoding", "gzip, deflate, br");

            response = new MockHttpServletResponse();
            response.setContentType(MediaType.APPLICATION_JSON.getType());

            filterChain = new MockFilterChain(new HttpRequestHandlerServlet(), new TestFilter());

            ServerConfig.getInstance();

        }catch (Exception ex){
            System.out.println("Exception while setting up the test: "+ex);
        }

    }

    @Test
    public void testRequestFilter_Success(){
        try{
            file = Files.readAllBytes(Paths.get("src/test/resources/UploadFiles/Success/Inputs/1.json"));
            filePart = new MockPart("file", "1.json", file);
            request.addPart(filePart);
            System.out.println("File name:"+filePart.getSubmittedFileName());


            requestFilter.doFilter(request, response, filterChain);
            System.out.println(response.getStatus());
            assertTrue(response.getStatus() == HttpServletResponse.SC_OK);

        }catch (Exception ex){
            System.out.println(ex);
        }
    }

    @Test
    public void testRequestFilter_FailureFileType(){
        try {
            file = Files.readAllBytes(Paths.get("src/test/resources/UploadFiles/Failure/Inputs/file.txt"));
            filePart = new MockPart("file", "file.txt", file);
            request.addPart(filePart);
            System.out.println("File name:"+filePart.getSubmittedFileName());

            mockResponse = JSONUtility.getJSONNode();
            mockResponse.put(ResponseKey.CODE.name().toLowerCase(), FailureResponse.FILE_TYPE_NOT_ALLOWED.getCode());
            mockResponse.put(ResponseKey.MESSAGE.name().toLowerCase(), FailureResponse.FILE_TYPE_NOT_ALLOWED.getMessage());

            requestFilter.doFilter(request, response, filterChain);
            assertTrue(response.getStatus() == HttpServletResponse.SC_BAD_REQUEST);
            JsonNode responseNode = JSONUtility.getJSONNode(response.getContentAsString());
            assertTrue(responseNode.equals((JsonNode) mockResponse));


        }catch (Exception ex){
            System.out.println(ex);
        }
    }

    @Test
    public void testRequestFilter_FailureFileContent(){
        try {
            file = Files.readAllBytes(Paths.get("src/test/resources/UploadFiles/Failure/Inputs/malformed.json"));
            filePart = new MockPart("file", "malformed.json", file);
            request.addPart(filePart);
            System.out.println("File name:"+filePart.getSubmittedFileName());

            mockResponse = JSONUtility.getJSONNode();
            mockResponse.put(ResponseKey.CODE.name().toLowerCase(), FailureResponse.JSON_NOT_FORMED_WELL.getCode());
            mockResponse.put(ResponseKey.MESSAGE.name().toLowerCase(), FailureResponse.JSON_NOT_FORMED_WELL.getMessage());

            requestFilter.doFilter(request, response, filterChain);
            assertTrue(response.getStatus() == HttpServletResponse.SC_BAD_REQUEST);
            JsonNode responseNode = JSONUtility.getJSONNode(response.getContentAsString());
            assertTrue(responseNode.equals((JsonNode) mockResponse));


        }catch (Exception ex){
            System.out.println(ex);
        }
    }


    @Test
    public void testRequestFilter_FailurePath(){
        try {
            request.setRequestURI("/greetings");
            requestFilter.doFilter(request, response, filterChain);
            assertTrue(response.getStatus() == HttpServletResponse.SC_NOT_FOUND);


        }catch (Exception ex){
            System.out.println(ex);
        }
    }


    private static class TestFilter implements Filter{

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
            Filter.super.init(filterConfig);
        }

        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        }

        @Override
        public void destroy() {
            Filter.super.destroy();
        }
    }
}