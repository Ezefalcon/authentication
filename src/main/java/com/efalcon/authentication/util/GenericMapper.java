package com.efalcon.authentication.util;

import org.modelmapper.ModelMapper;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;


public class GenericMapper<T,V> {

    private final ModelMapper modelMapper;
    private final Type entityType;
    private final Type dtoType;

    public GenericMapper(Type entityType, Type dtoType) {
        this.modelMapper = new ModelMapper();
        this.entityType = entityType;
        this.dtoType = dtoType;
    }

    public V convertToDTO(T entity) {
        return modelMapper.map(entity, this.dtoType);
    }

    public T convertToEntity(V dto) {
        return modelMapper.map(dto, this.entityType);
    }

    public List<V> convertToDTO(List<T> entities) {
        return entities.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
}
