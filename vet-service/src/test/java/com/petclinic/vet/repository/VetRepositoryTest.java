package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class VetRepositoryTest {

    @Autowired
    private VetRepository vetRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Test
    void findAll_returnsSeedData() {
        List<Vet> all = vetRepository.findAll();
        assertThat(all).hasSizeGreaterThanOrEqualTo(6);
    }

    @Test
    void findByLastNameContainingIgnoreCase_findsMatches() {
        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("carter");
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void findByLastNameContainingIgnoreCase_noMatches() {
        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("nonexistent");
        assertThat(results).isEmpty();
    }

    @Test
    void findBySpecialtyId_findsVetsWithSpecialty() {
        List<Vet> results = vetRepository.findBySpecialtyId(1);
        assertThat(results).isNotEmpty();
    }

    @Test
    void findBySpecialtyName_findsVets() {
        List<Vet> results = vetRepository.findBySpecialtyName("radiology");
        assertThat(results).isNotEmpty();
    }

    @Test
    void searchByName_findsByFirstName() {
        List<Vet> results = vetRepository.searchByName("James");
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void searchByName_findsByLastName() {
        List<Vet> results = vetRepository.searchByName("Carter");
        assertThat(results).isNotEmpty();
    }

    @Test
    void searchByName_noMatches() {
        List<Vet> results = vetRepository.searchByName("zzzzz");
        assertThat(results).isEmpty();
    }

    @Test
    void saveAndFind_worksCorrectly() {
        Vet vet = new Vet("Test", "Vet");
        Vet saved = vetRepository.save(vet);

        assertThat(saved.getId()).isNotNull();
        assertThat(vetRepository.findById(saved.getId())).isPresent();
    }

    @Test
    void saveWithSpecialties_worksCorrectly() {
        Specialty specialty = specialtyRepository.findAll().get(0);
        Vet vet = new Vet("New", "Doctor");
        vet.setSpecialties(Set.of(specialty));
        Vet saved = vetRepository.save(vet);

        assertThat(saved.getSpecialties()).hasSize(1);
    }

    @Test
    void delete_removesEntity() {
        Vet vet = new Vet("Temp", "Vet");
        Vet saved = vetRepository.save(vet);
        Integer id = saved.getId();

        vetRepository.delete(saved);
        vetRepository.flush();

        assertThat(vetRepository.findById(id)).isEmpty();
    }
}
