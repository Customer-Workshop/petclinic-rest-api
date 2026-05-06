package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.service.SpecialtyService;
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

@WebMvcTest(SpecialtyController.class)
class SpecialtyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SpecialtyService specialtyService;

    @Test
    void listSpecialties_returnsOk() throws Exception {
        when(specialtyService.findAll()).thenReturn(
            List.of(new SpecialtyDto(1, "radiology"), new SpecialtyDto(2, "surgery")));

        mockMvc.perform(get("/specialties"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].name").value("radiology"));
    }

    @Test
    void getSpecialty_returnsOk() throws Exception {
        when(specialtyService.findById(1)).thenReturn(new SpecialtyDto(1, "radiology"));

        mockMvc.perform(get("/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("radiology"));
    }

    @Test
    void getSpecialty_returnsNotFound() throws Exception {
        when(specialtyService.findById(99)).thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(get("/specialties/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void addSpecialty_returnsOk() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("dentistry");
        when(specialtyService.create(any())).thenReturn(new SpecialtyDto(3, "dentistry"));

        mockMvc.perform(post("/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("dentistry"));
    }

    @Test
    void addSpecialty_returnsBadRequestForInvalidInput() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("");

        mockMvc.perform(post("/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateSpecialty_returnsOk() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated");
        when(specialtyService.update(eq(1), any())).thenReturn(new SpecialtyDto(1, "updated"));

        mockMvc.perform(put("/specialties/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("updated"));
    }

    @Test
    void updateSpecialty_returnsNotFound() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated");
        when(specialtyService.update(eq(99), any()))
            .thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(put("/specialties/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteSpecialty_returnsOk() throws Exception {
        doNothing().when(specialtyService).delete(1);

        mockMvc.perform(delete("/specialties/1"))
            .andExpect(status().isOk());
    }

    @Test
    void deleteSpecialty_returnsNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Specialty", 99)).when(specialtyService).delete(99);

        mockMvc.perform(delete("/specialties/99"))
            .andExpect(status().isNotFound());
    }
}
