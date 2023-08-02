package com.example.server;


import com.example.model.*;
import com.example.persistence.Database.AngajatiDatabaseRepository;
import com.example.persistence.Database.ParticipantiDatabaseRepository;
import com.example.persistence.Database.PersoaneDatabaseRepository;


import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Service {
    private AngajatiDatabaseRepository repo_angajati;
    private PersoaneDatabaseRepository repo_persoane;
    private ParticipantiDatabaseRepository repo_participanti;

    public Service(AngajatiDatabaseRepository repo_angajati, PersoaneDatabaseRepository repo_persoane, ParticipantiDatabaseRepository repo_participanti)
    {
        this.repo_angajati=repo_angajati;
        this.repo_persoane=repo_persoane;
        this.repo_participanti=repo_participanti;
    }





    public Long getNewPersoanaId()
    {
        return repo_persoane.getNewId();
    }

    public void addPersoana(Persoana p)
    {
        repo_persoane.save(p);
    }







    public Angajat getAngajatByEmailAndPassword(String email, String password)
    {
        return repo_angajati.findOneByEmailAndPassword(email,password);
    }

    public Angajat getAngajatByEmail(String email)
    {
        return repo_angajati.findOneByEmail(email);
    }








    public Long getNewParticipantId()
    {
        return repo_participanti.getNewId();
    }


    public Integer getNumarParticipantiDupaProba(String stil,String distanta)
    {
        return repo_participanti.getNumarParticipantiDupaProba(stil,distanta);
    }


    public List<ParticipantDTO> getParticipanti()
    {
        Map<Long, Participant> participanti=repo_participanti.findAll();
        Map<Long, Persoana> persoane=repo_persoane.findAll();
        Participant concurent; Persoana individ;
        List<ParticipantDTO> lista_participanti=new ArrayList<>();
        for(Map.Entry<Long,Participant> p: participanti.entrySet())
        {
            concurent=p.getValue();
            individ=persoane.get(concurent.getIdPersoana());
            lista_participanti.add(new ParticipantDTO(individ.getNume(),individ.getVarsta(),
                    concurent.getStil().toString(),concurent.getDistanta().toString()));
        }
        return lista_participanti;
    }



    public List<ParticipantDTO> getSearchedParticipanti(String stil,String distanta)
    {
        Map<Long,Participant> participanti=repo_participanti.findAllByCompetition(stil,distanta);
        Map<Long,Persoana> persoane=repo_persoane.findAll();
        Participant concurent; Persoana individ;
        List<ParticipantDTO> lista_participanti=new ArrayList<>();
        for(Map.Entry<Long,Participant> p: participanti.entrySet())
        {
            concurent=p.getValue();
            individ=persoane.get(concurent.getIdPersoana());
            lista_participanti.add(new ParticipantDTO(individ.getNume(),individ.getVarsta(),
                    concurent.getStil().toString(),concurent.getDistanta().toString()));
        }
        return lista_participanti;
    }



    public void inscriereParticipant(String nume, int varsta, Stil stil, Distanta distanta)
    {
        //de verificat daca participantul exista deja
        Persoana pers=repo_persoane.findOneByNumeAndVarsta(nume,varsta);
        if(pers==null)
        {
            pers=new Persoana(nume,varsta);
            pers.setId(getNewPersoanaId());
            addPersoana(pers);
        }
        Participant p=new Participant(pers.getId(),distanta,stil);
        p.setId(getNewParticipantId());
        repo_participanti.save(p);
    }
}
