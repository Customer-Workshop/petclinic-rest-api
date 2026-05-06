package com.petclinic.vet.controller;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.service.SpecialtyService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/specialties")
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    public SpecialtyController(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }

    @GetMapping
    public ResponseEntity<List<SpecialtyDto>> listSpecialties() {
        return ResponseEntity.ok(specialtyService.findAll());
    }

    @GetMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyDto> getSpecialty(@PathVariable Integer specialtyId) {
        return ResponseEntity.ok(specialtyService.findById(specialtyId));
    }

    @PostMapping
    public ResponseEntity<SpecialtyDto> addSpecialty(@Valid @RequestBody SpecialtyRequestDto request) {
        SpecialtyDto created = specialtyService.create(request);
        return ResponseEntity.status(HttpStatus.OK).body(created);
    }

    @PutMapping("/{specialtyId}")
    public ResponseEntity<SpecialtyDto> updateSpecialty(
            @PathVariable Integer specialtyId,
            @Valid @RequestBody SpecialtyRequestDto request) {
        return ResponseEntity.ok(specialtyService.update(specialtyId, request));
    }

    @DeleteMapping("/{specialtyId}")
    public ResponseEntity<Void> deleteSpecialty(@PathVariable Integer specialtyId) {
        specialtyService.delete(specialtyId);
        return ResponseEntity.ok().build();
    }
}
