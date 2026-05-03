package com.tl.tutor_link.common.mapper;

public interface Mapper<E, D> {
    D toDto(E entity);
}

