package com.example.services;
import com.example.model.Angajat;
import com.example.model.Distanta;
import com.example.model.ParticipantDTO;
import com.example.model.Stil;

import java.util.List;

public interface IServices {
    boolean login(String data, String password , IObserver client) throws ConcursException;

    List<ParticipantDTO> getParticipanti() throws ConcursException;

    List<ParticipantDTO> getSearchedParticipanti(String stil,String distanta);


    List<Integer> getNumarParticipantiDupaProba() throws ConcursException;

    void inscriereParticipant(String nume, int varsta, Stil stil, Distanta distanta) throws ConcursException;

    void logout(String email) throws ConcursException;
}
