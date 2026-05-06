package com.petclinic.vet.entity;

import java.time.Instant;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EntityTest {

    @Test
    void specialty_gettersAndSetters() {
        Specialty specialty = new Specialty();
        specialty.setId(1);
        specialty.setName("radiology");
        Instant now = Instant.now();
        specialty.setCreatedAt(now);
        specialty.setUpdatedAt(now);

        assertThat(specialty.getId()).isEqualTo(1);
        assertThat(specialty.getName()).isEqualTo("radiology");
        assertThat(specialty.getCreatedAt()).isEqualTo(now);
        assertThat(specialty.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void specialty_constructorWithName() {
        Specialty specialty = new Specialty("surgery");
        assertThat(specialty.getName()).isEqualTo("surgery");
        assertThat(specialty.getId()).isNull();
    }

    @Test
    void vet_gettersAndSetters() {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        Instant now = Instant.now();
        vet.setCreatedAt(now);
        vet.setUpdatedAt(now);

        Specialty specialty = new Specialty("radiology");
        vet.setSpecialties(Set.of(specialty));

        assertThat(vet.getId()).isEqualTo(1);
        assertThat(vet.getFirstName()).isEqualTo("James");
        assertThat(vet.getLastName()).isEqualTo("Carter");
        assertThat(vet.getCreatedAt()).isEqualTo(now);
        assertThat(vet.getUpdatedAt()).isEqualTo(now);
        assertThat(vet.getSpecialties()).hasSize(1);
    }

    @Test
    void vet_constructorWithNames() {
        Vet vet = new Vet("Helen", "Leary");
        assertThat(vet.getFirstName()).isEqualTo("Helen");
        assertThat(vet.getLastName()).isEqualTo("Leary");
        assertThat(vet.getId()).isNull();
        assertThat(vet.getSpecialties()).isEmpty();
    }
}
