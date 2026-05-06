package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.VetMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import com.petclinic.vet.repository.VetRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
class VetServiceImplTest {

    @Mock
    private VetRepository vetRepository;

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Mock
    private VetMapper vetMapper;

    @InjectMocks
    private VetServiceImpl service;

    private Vet vet;
    private VetDto vetDto;
    private Specialty specialty;

    @BeforeEach
    void setUp() {
        specialty = new Specialty("radiology");
        specialty.setId(1);

        vet = new Vet("James", "Carter");
        vet.setId(1);
        vet.setSpecialties(Set.of(specialty));

        vetDto = new VetDto(1, "James", "Carter",
            List.of(new SpecialtyDto(1, "radiology")));
    }

    @Test
    void findAll_returnsAllVets() {
        when(vetRepository.findAll()).thenReturn(List.of(vet));
        when(vetMapper.toDto(vet)).thenReturn(vetDto);

        List<VetDto> result = service.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("James");
    }

    @Test
    void findAll_returnsEmptyList() {
        when(vetRepository.findAll()).thenReturn(List.of());

        List<VetDto> result = service.findAll();

        assertThat(result).isEmpty();
    }

    @Test
    void findById_returnsVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toDto(vet)).thenReturn(vetDto);

        VetDto result = service.findById(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.firstName()).isEqualTo("James");
        assertThat(result.specialties()).hasSize(1);
    }

    @Test
    void findById_throwsWhenNotFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet");
    }

    @Test
    void create_savesAndReturns() {
        VetRequestDto request = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyDto(1, "radiology")));
        Vet newVet = new Vet("James", "Carter");

        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toDto(vet)).thenReturn(vetDto);

        VetDto result = service.create(request);

        assertThat(result.firstName()).isEqualTo("James");
        assertThat(result.specialties()).hasSize(1);
    }

    @Test
    void create_withEmptySpecialties() {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of());
        Vet newVet = new Vet("James", "Carter");
        VetDto emptySpecDto = new VetDto(1, "James", "Carter", List.of());

        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toDto(vet)).thenReturn(emptySpecDto);

        VetDto result = service.create(request);

        assertThat(result.firstName()).isEqualTo("James");
    }

    @Test
    void create_withNullSpecialties() {
        VetRequestDto request = new VetRequestDto("James", "Carter", null);
        Vet newVet = new Vet("James", "Carter");
        VetDto dto = new VetDto(1, "James", "Carter", List.of());

        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toDto(vet)).thenReturn(dto);

        VetDto result = service.create(request);

        assertThat(result).isNotNull();
    }

    @Test
    void create_throwsWhenSpecialtyNotFound() {
        VetRequestDto request = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyDto(99, "unknown")));
        Vet newVet = new Vet("James", "Carter");

        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty");
    }

    @Test
    void update_updatesAndReturns() {
        VetRequestDto request = new VetRequestDto("Helen", "Leary",
            List.of(new SpecialtyDto(1, "radiology")));
        VetDto updatedDto = new VetDto(1, "Helen", "Leary",
            List.of(new SpecialtyDto(1, "radiology")));

        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        doNothing().when(vetMapper).updateEntity(request, vet);
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toDto(vet)).thenReturn(updatedDto);

        VetDto result = service.update(1, request);

        assertThat(result.firstName()).isEqualTo("Helen");
    }

    @Test
    void update_throwsWhenNotFound() {
        VetRequestDto request = new VetRequestDto("Helen", "Leary", List.of());
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_deletesVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));

        service.delete(1);

        verify(vetRepository).delete(vet);
    }

    @Test
    void delete_throwsWhenNotFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findBySpecialtyId_returnsVets() {
        when(vetRepository.findBySpecialtyId(1)).thenReturn(List.of(vet));
        when(vetMapper.toDto(vet)).thenReturn(vetDto);

        List<VetDto> result = service.findBySpecialtyId(1);

        assertThat(result).hasSize(1);
    }

    @Test
    void searchByName_returnsVets() {
        when(vetRepository.searchByName("Carter")).thenReturn(List.of(vet));
        when(vetMapper.toDto(vet)).thenReturn(vetDto);

        List<VetDto> result = service.searchByName("Carter");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).lastName()).isEqualTo("Carter");
    }

    @Test
    void create_specialtyWithNullId_skipped() {
        VetRequestDto request = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyDto(null, "radiology")));
        Vet newVet = new Vet("James", "Carter");
        VetDto dto = new VetDto(1, "James", "Carter", List.of());

        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toDto(vet)).thenReturn(dto);

        VetDto result = service.create(request);

        assertThat(result).isNotNull();
    }
}
