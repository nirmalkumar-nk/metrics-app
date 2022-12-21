package com.samknows.metrics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.aMapWithSize;

@ExtendWith(MockitoExtension.class)
class MetricsControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private MetricsController metricsController;

    @BeforeEach
    public void setUp(){
        this.mockMvc = MockMvcBuilders.standaloneSetup(metricsController).build();
    }

    @Test
    public void testMetricControllerSuccess(){
        try{
            mockMvc.perform(MockMvcRequestBuilders
                            .get("/metrics"))
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$", aMapWithSize(2)));
        }catch (Exception ex){
            System.out.println("Exception occured while Tesing testMetricControllerSuccess: "+ex);
        }
    }

}