package com.funnyproject.todolistprojectapi.project;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RetrieveProjectTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RetrieveProjectController retrieveProjectController;

    @Test
    public void requestWithBadBearerTokenOne() throws Exception {
        when(retrieveProjectController.retrieveProjectUsers(Mockito.anyString()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mvc.perform(MockMvcRequestBuilders.get("/projects/user-project/1")
                        .header("Authorization", "Bearer badToken"))
                .andExpect(status().isBadRequest());
    }

}
