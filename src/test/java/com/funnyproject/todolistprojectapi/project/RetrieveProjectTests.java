package com.funnyproject.todolistprojectapi.project;

import jakarta.servlet.http.HttpServletRequest;
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
class RetrieveUserTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RetrieveProjectController retrieveUser;

    @Test
    public void requestWithBadBearerToken() throws Exception {
        when(retrieveUser.retrieveUser(Mockito.anyString(), Mockito.any(HttpServletRequest.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));  // Adjust the response as needed

        mvc.perform(MockMvcRequestBuilders.get("/users/me")
                        .header("Authorization", "Bearer validToken"))
                .andExpect(status().isBadRequest());
    }

}
