package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VetMapperTest {

    private VetMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new VetMapperImpl();
    }

    @Test
    void toDto_mapsAllFields() {
        Specialty specialty = new Specialty("radiology");
        specialty.setId(1);
        Vet vet = new Vet("James", "Carter");
        vet.setId(1);
        vet.setSpecialties(Set.of(specialty));

        VetDto dto = mapper.toDto(vet);

        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.firstName()).isEqualTo("James");
        assertThat(dto.lastName()).isEqualTo("Carter");
        assertThat(dto.specialties()).hasSize(1);
        assertThat(dto.specialties().get(0).name()).isEqualTo("radiology");
    }

    @Test
    void toDto_handlesNull() {
        assertThat(mapper.toDto(null)).isNull();
    }

    @Test
    void toDto_handlesEmptySpecialties() {
        Vet vet = new Vet("James", "Carter");
        vet.setId(1);
        vet.setSpecialties(new HashSet<>());

        VetDto dto = mapper.toDto(vet);

        assertThat(dto.specialties()).isEmpty();
    }

    @Test
    void toDto_handlesNullSpecialties() {
        Vet vet = new Vet("James", "Carter");
        vet.setId(1);
        vet.setSpecialties(null);

        VetDto dto = mapper.toDto(vet);

        assertThat(dto.specialties()).isEmpty();
    }

    @Test
    void toEntity_fromRequestDto() {
        VetRequestDto request = new VetRequestDto("Helen", "Leary",
            List.of(new SpecialtyDto(1, "radiology")));

        Vet entity = mapper.toEntity(request);

        assertThat(entity.getFirstName()).isEqualTo("Helen");
        assertThat(entity.getLastName()).isEqualTo("Leary");
        assertThat(entity.getId()).isNull();
    }

    @Test
    void toEntity_handlesNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void updateEntity_updatesFields() {
        Vet entity = new Vet("Old", "Name");
        entity.setId(1);
        VetRequestDto request = new VetRequestDto("New", "Name", List.of());

        mapper.updateEntity(request, entity);

        assertThat(entity.getFirstName()).isEqualTo("New");
        assertThat(entity.getLastName()).isEqualTo("Name");
        assertThat(entity.getId()).isEqualTo(1);
    }

    @Test
    void updateEntity_handlesNullDto() {
        Vet entity = new Vet("Unchanged", "Name");
        mapper.updateEntity(null, entity);
        assertThat(entity.getFirstName()).isEqualTo("Unchanged");
    }

    @Test
    void specialtiesToDtoList_convertsCorrectly() {
        Specialty s1 = new Specialty("radiology");
        s1.setId(1);
        Specialty s2 = new Specialty("surgery");
        s2.setId(2);

        List<SpecialtyDto> dtos = mapper.specialtiesToDtoList(Set.of(s1, s2));

        assertThat(dtos).hasSize(2);
    }

    @Test
    void specialtiesToDtoList_handlesNull() {
        List<SpecialtyDto> dtos = mapper.specialtiesToDtoList(null);
        assertThat(dtos).isEmpty();
    }

    @Test
    void specialtiesToDtoList_handlesEmpty() {
        List<SpecialtyDto> dtos = mapper.specialtiesToDtoList(new HashSet<>());
        assertThat(dtos).isEmpty();
    }
}
