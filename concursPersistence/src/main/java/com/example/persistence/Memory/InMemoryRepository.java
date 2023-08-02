package com.example.persistence.Memory;


import com.example.model.Entity;
import com.example.persistence.GenericRepository;

import java.util.HashMap;
import java.util.Map;

public class InMemoryRepository<ID,E extends Entity<ID>>implements GenericRepository<ID,E> {

    Map<ID,E> entities;

    public InMemoryRepository()
    {
        entities=new HashMap<ID,E>();
    }


    @Override
    public E findOne(ID id)
    {
        if (id==null || entities.get(id)==null)
            return null;
        return entities.get(id);
    }


    @Override
    public Map<ID, E> findAll()
    {
        return entities;
    }


    @Override
    public boolean save(E entity)
    {
        if(entity==null) return false;
        entities.put(entity.getId(),entity);
        return true;
    }


    @Override
    public boolean delete(ID id)
    {
        if(entities.get(id)==null)
            return false;
        entities.remove(id);
        return true;
    }


    @Override
    public boolean update(E entity)
    {
        if(entity==null || entities.get(entity.getId())!=null)
            return false;
        entities.put(entity.getId(),entity);
        return true;
    }
}
