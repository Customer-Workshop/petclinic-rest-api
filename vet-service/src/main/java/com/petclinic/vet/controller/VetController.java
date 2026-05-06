package com.petclinic.vet.controller;

import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.service.VetService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vets")
public class VetController {

    private final VetService vetService;

    public VetController(VetService vetService) {
        this.vetService = vetService;
    }

    @GetMapping
    public ResponseEntity<List<VetDto>> listVets() {
        return ResponseEntity.ok(vetService.findAll());
    }

    @GetMapping("/{vetId}")
    public ResponseEntity<VetDto> getVet(@PathVariable Integer vetId) {
        return ResponseEntity.ok(vetService.findById(vetId));
    }

    @PostMapping
    public ResponseEntity<VetDto> addVet(@Valid @RequestBody VetRequestDto request) {
        VetDto created = vetService.create(request);
        return ResponseEntity.status(HttpStatus.OK).body(created);
    }

    @PutMapping("/{vetId}")
    public ResponseEntity<VetDto> updateVet(
            @PathVariable Integer vetId,
            @Valid @RequestBody VetRequestDto request) {
        return ResponseEntity.ok(vetService.update(vetId, request));
    }

    @DeleteMapping("/{vetId}")
    public ResponseEntity<Void> deleteVet(@PathVariable Integer vetId) {
        vetService.delete(vetId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(params = "specialtyId")
    public ResponseEntity<List<VetDto>> findBySpecialty(@RequestParam Integer specialtyId) {
        return ResponseEntity.ok(vetService.findBySpecialtyId(specialtyId));
    }

    @GetMapping(params = "name")
    public ResponseEntity<List<VetDto>> searchByName(@RequestParam String name) {
        return ResponseEntity.ok(vetService.searchByName(name));
    }
}
