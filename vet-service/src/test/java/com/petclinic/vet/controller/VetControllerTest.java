package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.service.VetService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VetController.class)
class VetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VetService vetService;

    private final VetDto sampleVet = new VetDto(1, "James", "Carter",
        List.of(new SpecialtyDto(1, "radiology")));

    @Test
    void listVets_returnsOk() throws Exception {
        when(vetService.findAll()).thenReturn(List.of(sampleVet));

        mockMvc.perform(get("/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].firstName").value("James"));
    }

    @Test
    void getVet_returnsOk() throws Exception {
        when(vetService.findById(1)).thenReturn(sampleVet);

        mockMvc.perform(get("/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("James"))
            .andExpect(jsonPath("$.specialties[0].name").value("radiology"));
    }

    @Test
    void getVet_returnsNotFound() throws Exception {
        when(vetService.findById(99)).thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(get("/vets/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void addVet_returnsOk() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyDto(1, "radiology")));
        when(vetService.create(any())).thenReturn(sampleVet);

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("James"));
    }

    @Test
    void addVet_returnsBadRequestForMissingFirstName() throws Exception {
        VetRequestDto request = new VetRequestDto("", "Carter", List.of());

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_returnsBadRequestForMissingLastName() throws Exception {
        VetRequestDto request = new VetRequestDto("James", "", List.of());

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_returnsBadRequestForNullSpecialties() throws Exception {
        String json = """
            {"firstName": "James", "lastName": "Carter"}
            """;

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateVet_returnsOk() throws Exception {
        VetRequestDto request = new VetRequestDto("Helen", "Leary",
            List.of(new SpecialtyDto(1, "radiology")));
        VetDto updated = new VetDto(1, "Helen", "Leary",
            List.of(new SpecialtyDto(1, "radiology")));
        when(vetService.update(eq(1), any())).thenReturn(updated);

        mockMvc.perform(put("/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Helen"));
    }

    @Test
    void updateVet_returnsNotFound() throws Exception {
        VetRequestDto request = new VetRequestDto("Helen", "Leary", List.of());
        when(vetService.update(eq(99), any()))
            .thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(put("/vets/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteVet_returnsOk() throws Exception {
        doNothing().when(vetService).delete(1);

        mockMvc.perform(delete("/vets/1"))
            .andExpect(status().isOk());
    }

    @Test
    void deleteVet_returnsNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Vet", 99)).when(vetService).delete(99);

        mockMvc.perform(delete("/vets/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void findBySpecialty_returnsOk() throws Exception {
        when(vetService.findBySpecialtyId(1)).thenReturn(List.of(sampleVet));

        mockMvc.perform(get("/vets").param("specialtyId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void searchByName_returnsOk() throws Exception {
        when(vetService.searchByName("Carter")).thenReturn(List.of(sampleVet));

        mockMvc.perform(get("/vets").param("name", "Carter"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }
}
