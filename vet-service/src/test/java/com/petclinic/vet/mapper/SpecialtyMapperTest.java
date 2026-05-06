package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SpecialtyMapperTest {

    private SpecialtyMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SpecialtyMapperImpl();
    }

    @Test
    void toDto_mapsAllFields() {
        Specialty entity = new Specialty("radiology");
        entity.setId(1);

        SpecialtyDto dto = mapper.toDto(entity);

        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.name()).isEqualTo("radiology");
    }

    @Test
    void toDto_handlesNull() {
        assertThat(mapper.toDto(null)).isNull();
    }

    @Test
    void toEntity_fromRequestDto() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("surgery");

        Specialty entity = mapper.toEntity(request);

        assertThat(entity.getName()).isEqualTo("surgery");
        assertThat(entity.getId()).isNull();
    }

    @Test
    void toEntity_fromRequestDto_handlesNull() {
        assertThat(mapper.toEntity((SpecialtyRequestDto) null)).isNull();
    }

    @Test
    void toEntity_fromDto() {
        SpecialtyDto dto = new SpecialtyDto(1, "dentistry");

        Specialty entity = mapper.toEntity(dto);

        assertThat(entity.getId()).isEqualTo(1);
        assertThat(entity.getName()).isEqualTo("dentistry");
    }

    @Test
    void toEntity_fromDto_handlesNull() {
        assertThat(mapper.toEntity((SpecialtyDto) null)).isNull();
    }

    @Test
    void updateEntity_updatesFields() {
        Specialty entity = new Specialty("old");
        entity.setId(1);
        SpecialtyRequestDto request = new SpecialtyRequestDto("new");

        mapper.updateEntity(request, entity);

        assertThat(entity.getName()).isEqualTo("new");
        assertThat(entity.getId()).isEqualTo(1);
    }

    @Test
    void updateEntity_handlesNullDto() {
        Specialty entity = new Specialty("unchanged");
        mapper.updateEntity(null, entity);
        assertThat(entity.getName()).isEqualTo("unchanged");
    }
}
