package com.example.persistence;

import com.example.model.Angajat;
import com.example.model.Entity;

public interface AngajatiIRepository<ID,T extends Entity<ID>> extends GenericRepository<ID,T> {
    Angajat findOneByEmailAndPassword(String email, String pass);

    Angajat findOneByEmail(String email);
}
