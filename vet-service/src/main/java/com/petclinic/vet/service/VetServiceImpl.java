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
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VetServiceImpl implements VetService {

    private final VetRepository vetRepository;
    private final SpecialtyRepository specialtyRepository;
    private final VetMapper vetMapper;

    public VetServiceImpl(VetRepository vetRepository,
                          SpecialtyRepository specialtyRepository,
                          VetMapper vetMapper) {
        this.vetRepository = vetRepository;
        this.specialtyRepository = specialtyRepository;
        this.vetMapper = vetMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetDto> findAll() {
        return vetRepository.findAll().stream()
            .map(vetMapper::toDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public VetDto findById(Integer id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        return vetMapper.toDto(vet);
    }

    @Override
    public VetDto create(VetRequestDto request) {
        Vet vet = vetMapper.toEntity(request);
        Set<Specialty> specialties = resolveSpecialties(request.specialties());
        vet.setSpecialties(specialties);
        Vet saved = vetRepository.save(vet);
        return vetMapper.toDto(saved);
    }

    @Override
    public VetDto update(Integer id, VetRequestDto request) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        vetMapper.updateEntity(request, vet);
        Set<Specialty> specialties = resolveSpecialties(request.specialties());
        vet.setSpecialties(specialties);
        Vet saved = vetRepository.save(vet);
        return vetMapper.toDto(saved);
    }

    @Override
    public void delete(Integer id) {
        Vet vet = vetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vet", id));
        vetRepository.delete(vet);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetDto> findBySpecialtyId(Integer specialtyId) {
        return vetRepository.findBySpecialtyId(specialtyId).stream()
            .map(vetMapper::toDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VetDto> searchByName(String name) {
        return vetRepository.searchByName(name).stream()
            .map(vetMapper::toDto)
            .toList();
    }

    private Set<Specialty> resolveSpecialties(List<SpecialtyDto> specialtyDtos) {
        Set<Specialty> specialties = new HashSet<>();
        if (specialtyDtos == null) {
            return specialties;
        }
        for (SpecialtyDto dto : specialtyDtos) {
            if (dto.id() != null) {
                Specialty specialty = specialtyRepository.findById(dto.id())
                    .orElseThrow(() -> new ResourceNotFoundException("Specialty", dto.id()));
                specialties.add(specialty);
            }
        }
        return specialties;
    }
}
