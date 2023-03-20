package api.blog.controller;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class BlogControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("ControllerTest_NoQuery")
    void ControllerTest_NoQuery() throws Exception {
        mockMvc.perform(get("/api/blog"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("ControllerTest_PageLessThanMin")
    void ControllerTest_PageLessThanMin() throws Exception {
        mockMvc.perform(get("/api/blog").param("query","test").param("page","0"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("ControllerTest_PageMoreThanMax")
    void ControllerTest_PageMoreThanMax() throws Exception {
        mockMvc.perform(get("/api/blog").param("query","test").param("page","51"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("ControllerTest_SizeLessThanMin")
    void ControllerTest_SizeLessThanMin() throws Exception {
        mockMvc.perform(get("/api/blog").param("query","test").param("size","0"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("ControllerTest_SizeMoreThanMax")
    void ControllerTest_SizeMoreThanMax() throws Exception {
        mockMvc.perform(get("/api/blog").param("query","test").param("size","51"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("ControllerTest_Success")
    void ControllerTest_Success() throws Exception {
        mockMvc.perform(get("/api/blog").param("query","test").param("page","2").param("size","10"))
            .andExpect(status().isOk());
    }
    
}
