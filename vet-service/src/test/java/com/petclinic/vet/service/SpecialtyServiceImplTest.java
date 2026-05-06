package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.SpecialtyMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpecialtyServiceImplTest {

    @Mock
    private SpecialtyRepository repository;

    @Mock
    private SpecialtyMapper mapper;

    @InjectMocks
    private SpecialtyServiceImpl service;

    private Specialty specialty;
    private SpecialtyDto specialtyDto;

    @BeforeEach
    void setUp() {
        specialty = new Specialty("radiology");
        specialty.setId(1);
        specialtyDto = new SpecialtyDto(1, "radiology");
    }

    @Test
    void findAll_returnsAllSpecialties() {
        when(repository.findAll()).thenReturn(List.of(specialty));
        when(mapper.toDto(specialty)).thenReturn(specialtyDto);

        List<SpecialtyDto> result = service.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("radiology");
    }

    @Test
    void findAll_returnsEmptyList() {
        when(repository.findAll()).thenReturn(List.of());

        List<SpecialtyDto> result = service.findAll();

        assertThat(result).isEmpty();
    }

    @Test
    void findById_returnsSpecialty() {
        when(repository.findById(1)).thenReturn(Optional.of(specialty));
        when(mapper.toDto(specialty)).thenReturn(specialtyDto);

        SpecialtyDto result = service.findById(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("radiology");
    }

    @Test
    void findById_throwsWhenNotFound() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty");
    }

    @Test
    void create_savesAndReturns() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("surgery");
        Specialty newSpecialty = new Specialty("surgery");
        Specialty saved = new Specialty("surgery");
        saved.setId(2);
        SpecialtyDto savedDto = new SpecialtyDto(2, "surgery");

        when(mapper.toEntity(request)).thenReturn(newSpecialty);
        when(repository.save(newSpecialty)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(savedDto);

        SpecialtyDto result = service.create(request);

        assertThat(result.id()).isEqualTo(2);
        assertThat(result.name()).isEqualTo("surgery");
    }

    @Test
    void update_updatesAndReturns() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated");
        Specialty updated = new Specialty("updated");
        updated.setId(1);
        SpecialtyDto updatedDto = new SpecialtyDto(1, "updated");

        when(repository.findById(1)).thenReturn(Optional.of(specialty));
        doNothing().when(mapper).updateEntity(request, specialty);
        when(repository.save(specialty)).thenReturn(updated);
        when(mapper.toDto(updated)).thenReturn(updatedDto);

        SpecialtyDto result = service.update(1, request);

        assertThat(result.name()).isEqualTo("updated");
    }

    @Test
    void update_throwsWhenNotFound() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated");
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_deletesSpecialty() {
        when(repository.findById(1)).thenReturn(Optional.of(specialty));

        service.delete(1);

        verify(repository).delete(specialty);
    }

    @Test
    void delete_throwsWhenNotFound() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}
