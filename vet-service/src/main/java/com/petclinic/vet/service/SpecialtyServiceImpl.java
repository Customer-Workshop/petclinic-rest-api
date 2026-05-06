package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.SpecialtyMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository repository;
    private final SpecialtyMapper mapper;

    public SpecialtyServiceImpl(SpecialtyRepository repository, SpecialtyMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpecialtyDto> findAll() {
        return repository.findAll().stream()
            .map(mapper::toDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SpecialtyDto findById(Integer id) {
        Specialty specialty = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        return mapper.toDto(specialty);
    }

    @Override
    public SpecialtyDto create(SpecialtyRequestDto request) {
        Specialty specialty = mapper.toEntity(request);
        Specialty saved = repository.save(specialty);
        return mapper.toDto(saved);
    }

    @Override
    public SpecialtyDto update(Integer id, SpecialtyRequestDto request) {
        Specialty specialty = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        mapper.updateEntity(request, specialty);
        Specialty saved = repository.save(specialty);
        return mapper.toDto(saved);
    }

    @Override
    public void delete(Integer id) {
        Specialty specialty = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Specialty", id));
        repository.delete(specialty);
    }
}
