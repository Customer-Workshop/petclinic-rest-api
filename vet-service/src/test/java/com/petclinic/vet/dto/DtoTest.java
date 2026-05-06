package com.petclinic.vet.dto;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DtoTest {

    @Test
    void specialtyDto_recordFields() {
        SpecialtyDto dto = new SpecialtyDto(1, "radiology");
        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.name()).isEqualTo("radiology");
    }

    @Test
    void specialtyRequestDto_recordFields() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto("surgery");
        assertThat(dto.name()).isEqualTo("surgery");
    }

    @Test
    void vetDto_recordFields() {
        VetDto dto = new VetDto(1, "James", "Carter",
            List.of(new SpecialtyDto(1, "radiology")));
        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.firstName()).isEqualTo("James");
        assertThat(dto.lastName()).isEqualTo("Carter");
        assertThat(dto.specialties()).hasSize(1);
    }

    @Test
    void vetRequestDto_recordFields() {
        VetRequestDto dto = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyDto(1, "radiology")));
        assertThat(dto.firstName()).isEqualTo("James");
        assertThat(dto.lastName()).isEqualTo("Carter");
        assertThat(dto.specialties()).hasSize(1);
    }
}
