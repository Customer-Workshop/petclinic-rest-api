package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.entity.Specialty;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-06T15:50:51+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.13 (Ubuntu)"
)
@Component
public class SpecialtyMapperImpl implements SpecialtyMapper {

    @Override
    public SpecialtyDto toDto(Specialty entity) {
        if ( entity == null ) {
            return null;
        }

        Integer id = null;
        String name = null;

        id = entity.getId();
        name = entity.getName();

        SpecialtyDto specialtyDto = new SpecialtyDto( id, name );

        return specialtyDto;
    }

    @Override
    public Specialty toEntity(SpecialtyRequestDto dto) {
        if ( dto == null ) {
            return null;
        }

        Specialty specialty = new Specialty();

        specialty.setName( dto.name() );

        return specialty;
    }

    @Override
    public Specialty toEntity(SpecialtyDto dto) {
        if ( dto == null ) {
            return null;
        }

        Specialty specialty = new Specialty();

        specialty.setId( dto.id() );
        specialty.setName( dto.name() );

        return specialty;
    }

    @Override
    public void updateEntity(SpecialtyRequestDto dto, Specialty entity) {
        if ( dto == null ) {
            return;
        }

        entity.setName( dto.name() );
    }
}
