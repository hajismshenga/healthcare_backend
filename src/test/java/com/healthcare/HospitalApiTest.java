package com.healthcare;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Map;

@SpringBootTest
public class HospitalApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testRegisterHospital() throws Exception {
        Map<String, Object> request = Map.of(
            "name", "Test Hospital",
            "ownership", "Government",
            "district", "Test District",
            "location", "Test Location",
            "contactInfo", "1234567890",
            "username", "testuser",
            "password", "testpassword"
        );

        MvcResult result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/hospitals/register")
                .contentType("application/json")
                .content(request.toString())
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();

        System.out.println("Response: " + result.getResponse().getContentAsString());
    }

    @Test
    public void testGetAllHospitals() throws Exception {
        MvcResult result = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/hospitals")
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andReturn();

        System.out.println("Response: " + result.getResponse().getContentAsString());
    }
}
