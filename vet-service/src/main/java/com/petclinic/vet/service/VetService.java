package com.petclinic.vet.service;

import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.dto.VetRequestDto;
import java.util.List;

public interface VetService {

    List<VetDto> findAll();

    VetDto findById(Integer id);

    VetDto create(VetRequestDto request);

    VetDto update(Integer id, VetRequestDto request);

    void delete(Integer id);

    List<VetDto> findBySpecialtyId(Integer specialtyId);

    List<VetDto> searchByName(String name);
}
