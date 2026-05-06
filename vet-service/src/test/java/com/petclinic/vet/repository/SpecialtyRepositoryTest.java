package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class SpecialtyRepositoryTest {

    @Autowired
    private SpecialtyRepository repository;

    @Test
    void findByNameContainingIgnoreCase_findsMatches() {
        List<Specialty> results = repository.findByNameContainingIgnoreCase("radio");
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getName()).containsIgnoringCase("radio");
    }

    @Test
    void findByNameContainingIgnoreCase_noMatches() {
        List<Specialty> results = repository.findByNameContainingIgnoreCase("nonexistent");
        assertThat(results).isEmpty();
    }

    @Test
    void findByNameIgnoreCase_findsExactMatch() {
        List<Specialty> results = repository.findByNameIgnoreCase("RADIOLOGY");
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void findAll_returnsSeedData() {
        List<Specialty> all = repository.findAll();
        assertThat(all).hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    void saveAndFind_worksCorrectly() {
        Specialty specialty = new Specialty("oncology");
        Specialty saved = repository.save(specialty);

        assertThat(saved.getId()).isNotNull();
        assertThat(repository.findById(saved.getId())).isPresent();
    }

    @Test
    void delete_removesEntity() {
        Specialty specialty = new Specialty("temp");
        Specialty saved = repository.save(specialty);
        Integer id = saved.getId();

        repository.delete(saved);
        repository.flush();

        assertThat(repository.findById(id)).isEmpty();
    }
}
