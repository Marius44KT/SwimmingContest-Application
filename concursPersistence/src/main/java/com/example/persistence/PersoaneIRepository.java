package com.example.persistence;


import com.example.model.Entity;
import com.example.model.Persoana;

public interface PersoaneIRepository<ID,T extends Entity<ID>> extends GenericRepository<ID,T> {
    Persoana findOneByNumeAndVarsta(String nume, int varsta);

    public Long getNewId();
}
