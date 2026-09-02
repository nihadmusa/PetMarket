package com.example.animalservice.mapper;

import com.example.animalservice.dao.entity.AnimalEntity;
import com.example.animalservice.dto.request.AnimalRequestDto;
import com.example.animalservice.dto.response.AnimalResponseDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AnimalMapper {
    AnimalEntity dtoToEntity(AnimalRequestDto dto);
    AnimalResponseDto entityToDto(AnimalEntity entity);
    List<AnimalResponseDto> entityListToDtoList(List<AnimalEntity> entityList);
}
