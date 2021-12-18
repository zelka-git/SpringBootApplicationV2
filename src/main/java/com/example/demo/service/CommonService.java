package com.example.demo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommonService<T, ID> {

    Page<T> getAll(Pageable pageable);

    T getById(ID id);

    T save(T item);

    void update(ID id, T item);

    void delete(ID id);
}
