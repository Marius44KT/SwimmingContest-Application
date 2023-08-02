package com.example.persistence;


import com.example.model.Entity;

import java.util.Map;

public interface ParticipantiIRepository<ID,T extends Entity<ID>> extends GenericRepository<ID,T> {
    Map<ID,T> findAllByCompetition(String distanta, String stil);

   Integer getNumarParticipantiDupaProba(String stil,String dist);

    public Long getNewId();
}