package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.entity.Vet;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-06T15:50:51+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.13 (Ubuntu)"
)
@Component
public class VetMapperImpl implements VetMapper {

    @Override
    public VetDto toDto(Vet entity) {
        if ( entity == null ) {
            return null;
        }

        List<SpecialtyDto> specialties = null;
        Integer id = null;
        String firstName = null;
        String lastName = null;

        specialties = specialtiesToDtoList( entity.getSpecialties() );
        id = entity.getId();
        firstName = entity.getFirstName();
        lastName = entity.getLastName();

        VetDto vetDto = new VetDto( id, firstName, lastName, specialties );

        return vetDto;
    }

    @Override
    public Vet toEntity(VetRequestDto dto) {
        if ( dto == null ) {
            return null;
        }

        Vet vet = new Vet();

        vet.setFirstName( dto.firstName() );
        vet.setLastName( dto.lastName() );

        return vet;
    }

    @Override
    public void updateEntity(VetRequestDto dto, Vet entity) {
        if ( dto == null ) {
            return;
        }

        entity.setFirstName( dto.firstName() );
        entity.setLastName( dto.lastName() );
    }
}
