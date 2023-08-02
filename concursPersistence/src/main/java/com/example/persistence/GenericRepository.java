package com.example.persistence;


import com.example.model.Entity;
import java.util.Map;

public interface GenericRepository<ID,E extends Entity<ID>>{
    E findOne(ID id);

    Map<ID,E> findAll();

    boolean save(E entity);

    boolean delete(ID id);

    boolean update(E entity);
}