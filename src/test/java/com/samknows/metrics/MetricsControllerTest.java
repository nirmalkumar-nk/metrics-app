package com.samknows.metrics;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.samknows.responses.FailureResponse;
import com.samknows.responses.ResponseKey;
import com.samknows.server.ServerConfig;
import com.samknows.utility.JSONUtility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletResponse;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.aMapWithSize;

@ExtendWith(MockitoExtension.class)
class MetricsControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private MetricsController metricsController;
    private MockMultipartFile filePart;
    private byte[] file;

    @BeforeAll
    public static void configServer(){
        ServerConfig.getInstance();
    }

    @BeforeEach
    public void setUp(){
        try {
            mockMvc = MockMvcBuilders.standaloneSetup(metricsController).build();
        }catch (Exception ex){
            System.out.println(ex);
        }
    }

    @Test
    public void testAnalyseMetrics_Success(){
        try {
            file = Files.readAllBytes(Paths.get("src/test/resources/UploadFiles/Success/Inputs/2.json"));
            filePart = new MockMultipartFile("file",file);

            byte[] expectedFile = Files.readAllBytes(Paths.get("src/test/resources/UploadFiles/Success.Outputs/2.output"));

            mockMvc.perform(MockMvcRequestBuilders
                    .multipart("/metrics")
                    .file(filePart)
                    ).andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.TEXT_PLAIN))
                    .andExpect(MockMvcResultMatchers.content().bytes(expectedFile));
        }catch (Exception ex){
            System.out.println(ex);
        }
    }

    @Test
    public void testAnalyseMetrics_FailureEmptyJson(){
        try {
            file = Files.readAllBytes(Paths.get("src/test/resources/UploadFiles/Failure/Inputs/malformed.json"));
            filePart = new MockMultipartFile("file",file);

            ObjectNode responseMap = JSONUtility.getJSONNode();

            responseMap.put(ResponseKey.CODE.name().toLowerCase(), FailureResponse.EMPTY_JSON.getCode());
            responseMap.put(ResponseKey.MESSAGE.name().toLowerCase(), FailureResponse.EMPTY_JSON.getMessage());

            mockMvc.perform(MockMvcRequestBuilders
                            .multipart("/metrics")
                            .file(filePart)
                    ).andExpect(MockMvcResultMatchers.status().is(HttpServletResponse.SC_BAD_REQUEST))
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.content().json(responseMap.toString()));
        }catch (Exception ex){
            System.out.println("Exception: "+ex);
        }
    }

    @Test
    public void testAnalyseMetrics_FailureArray(){
        try {
            file = Files.readAllBytes(Paths.get("src/test/resources/UploadFiles/Failure/Inputs/malformed.json"));
            filePart = new MockMultipartFile("file",file);

            ObjectNode responseMap = JSONUtility.getJSONNode();

            responseMap.put(ResponseKey.CODE.name().toLowerCase(), FailureResponse.JSON_ARRAY_EXPECTED.getCode());
            responseMap.put(ResponseKey.MESSAGE.name().toLowerCase(), FailureResponse.JSON_ARRAY_EXPECTED.getMessage());

            mockMvc.perform(MockMvcRequestBuilders
                            .multipart("/metrics")
                            .file(filePart)
                    ).andExpect(MockMvcResultMatchers.status().is(HttpServletResponse.SC_BAD_REQUEST))
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.content().json(responseMap.toString()));
        }catch (Exception ex){
            System.out.println("Exception: "+ex);
        }
    }

//    @Test
//    public void testMetricControllerSuccess(){
//        try{
//            mockMvc.perform(MockMvcRequestBuilders
//                            .get("/metrics"))
//                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(MockMvcResultMatchers.status().isOk())
//                    .andExpect(MockMvcResultMatchers.jsonPath("$", aMapWithSize(2)));
//        }catch (Exception ex){
//            System.out.println("Exception occurred while Testing testMetricControllerSuccess: "+ex);
//        }
//    }
}