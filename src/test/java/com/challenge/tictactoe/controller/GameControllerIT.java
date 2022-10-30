package com.challenge.tictactoe.controller;

import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.challenge.tictactoe.api.request.GameRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
public class GameControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private static UUID id = null;

    private static ObjectWriter objectWriter;

    @BeforeAll
    public static void init() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        objectWriter = mapper.writer().withDefaultPrettyPrinter();
    }

    @Test
    @Order(1)
    public void givenGameURIWithGet_thenReturnOK() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/game"))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andReturn();
    }

    @Test
    @Order(1)
    public void givenGameAndIdURIWithGet_whenIDDoesNotExist_thenReturnNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/game/" + UUID.randomUUID()))
                .andExpect(MockMvcResultMatchers.status()
                        .isNotFound())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.errorMessage").value("Game ID not found"))
                .andReturn();
    }

    @Test
    @Order(1)
    public void givenGameAndIdURIWithPatch_whenIDDoesNotExist_thenReturnNotFound() throws Exception {
        final String requestJson = objectWriter.writeValueAsString(new GameRequest());

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/game/" + UUID.randomUUID()).contentType(
                        MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(MockMvcResultMatchers.status()
                        .isNotFound())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.errorMessage").value("Game ID not found"))
                .andReturn();
    }

    @Test
    @Order(2)
    public void givenGameURIWithPost_thenReturnOK() throws Exception {
        GameRequest gameRequest = new GameRequest();
        gameRequest.setSymbol("X");
        gameRequest.setX(1);
        gameRequest.setY(1);

        final String requestJson = objectWriter.writeValueAsString(gameRequest);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/game")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status()
                        .isCreated())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.nextSymbol").value("O"))
                .andReturn();

        final String response = result.getResponse().getContentAsString();
        id = UUID.fromString(JsonPath.parse(response).read("$.id"));
    }

    @Test
    @Order(3)
    public void givenGameURIWithGet_whenDBHasValue_thenReturnOK() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/game"))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$").isNotEmpty())
                .andReturn();
    }

    @Test
    @Order(3)
    public void givenGameAndIdURIWithGet_whenDBHasValue_thenReturnOK() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/game/" + id))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.nextSymbol").value("O"))
                .andReturn();
    }

    @Test
    @Order(4)
    public void givenGameAndIdURIWithPatch_whenDBHasValue_thenReturnOK() throws Exception {
        GameRequest gameRequest = new GameRequest();
        gameRequest.setSymbol("O");
        gameRequest.setX(1);
        gameRequest.setY(0);

        final String requestJson = objectWriter.writeValueAsString(gameRequest);

        mockMvc.perform(MockMvcRequestBuilders
                .patch("/game/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.nextSymbol").value("X"))
                .andReturn();
    }

    @Test
    @Order(5)
    public void givenGameAndIdURIWithPatch_whenSymboldIsInvalid_thenReturnBadRequest() throws Exception {
        GameRequest gameRequest = new GameRequest();
        gameRequest.setSymbol("Y");
        gameRequest.setX(2);
        gameRequest.setY(0);

        final String requestJson = objectWriter.writeValueAsString(gameRequest);

        mockMvc.perform(MockMvcRequestBuilders
                .patch("/game/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status()
                        .isBadRequest())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.errorMessage")
                        .value("Invalid symbol"))
                .andReturn();
    }

    @Test
    @Order(5)
    public void givenGameAndIdURIWithPatch_whenSymboldMatchesLast_thenReturnBadRequest() throws Exception {
        GameRequest gameRequest = new GameRequest();
        gameRequest.setSymbol("O");
        gameRequest.setX(2);
        gameRequest.setY(2);

        final String requestJson = objectWriter.writeValueAsString(gameRequest);

        mockMvc.perform(MockMvcRequestBuilders
                .patch("/game/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status()
                        .isBadRequest())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.errorMessage").value("Invalid symbol"))
                .andReturn();
    }

    @Test
    @Order(5)
    public void givenGameAndIdURIWithPatch_whenCellIsFilled_thenReturnBadRequest() throws Exception {
        GameRequest gameRequest = new GameRequest();
        gameRequest.setSymbol("X");
        gameRequest.setX(1);
        gameRequest.setY(1);

        final String requestJson = objectWriter.writeValueAsString(gameRequest);

        mockMvc.perform(MockMvcRequestBuilders
                .patch("/game/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status()
                        .isBadRequest())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.errorMessage").value("Invalid request"))
                .andReturn();
    }

    @Test
    @Order(6)
    public void givenGameAndIdURIWithPatch_whenDBHasWinnerValue_thenReturnOK()
            throws Exception {
        GameRequest gameRequest = new GameRequest();

        gameRequest.setSymbol("X");
        gameRequest.setX(2);
        gameRequest.setY(2);
        executePatch(objectWriter.writeValueAsString(gameRequest));

        gameRequest.setSymbol("O");
        gameRequest.setX(2);
        gameRequest.setY(0);
        executePatch(objectWriter.writeValueAsString(gameRequest));

        gameRequest.setSymbol("X");
        gameRequest.setX(0);
        gameRequest.setY(0);

        final String requestJson = objectWriter.writeValueAsString(gameRequest);

        mockMvc.perform(MockMvcRequestBuilders
                .patch("/game/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.winner").value("X"))
                .andReturn();

    }

    @Test
    @Order(7)
    public void givenGameAndIdURIWithGet_whenDBHasWinnerValue_thenReturnOK() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/game/" + id))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.winner").value("X"))
                .andReturn();
    }

    @Test
    @Order(7)
    public void givenGameAndIdURIWithPatch_whenWinnerIsAlreadyDecided_thenReturnBadRequest() throws Exception {
        GameRequest gameRequest = new GameRequest();
        gameRequest.setSymbol("O");
        gameRequest.setX(1);
        gameRequest.setY(2);

        final String requestJson = objectWriter.writeValueAsString(gameRequest);

        mockMvc.perform(MockMvcRequestBuilders
                .patch("/game/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status()
                        .isBadRequest())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.errorMessage").value(
                                "Game winner has already been decided"))
                .andReturn();
    }

    @Test
    @Order(8)
    public void givenGameAndIdURIWithDelete_thenReturnNoContent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/game/" + id))
                .andExpect(MockMvcResultMatchers.status()
                        .isNoContent())
                .andReturn();
    }

    private MvcResult executePatch(String requestJson) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .patch("/game/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status()
                        .isOk())
                .andReturn();
    }
}
