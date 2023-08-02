package com.example.server;


import com.example.model.*;
import com.example.networking.servers.ServerException;
import com.example.persistence.AngajatiIRepository;
import com.example.persistence.ParticipantiIRepository;
import com.example.persistence.PersoaneIRepository;
import com.example.services.ConcursException;
import com.example.services.IObserver;
import com.example.services.IServices;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ServiceImpl implements IServices {
    private AngajatiIRepository<Long,Angajat> repo_angajati;
    private PersoaneIRepository<Long,Persoana> repo_persoane;
    private ParticipantiIRepository<Long,Participant> repo_participanti;

    private Map<String, IObserver> loggedClients;
    private final int defaultThreadsNo = 5;

    public ServiceImpl(AngajatiIRepository<Long,Angajat> repo_angajati, PersoaneIRepository<Long,Persoana>
            repo_persoane,ParticipantiIRepository<Long,Participant> repo_participanti) {
        this.repo_angajati=repo_angajati;
        this.repo_persoane=repo_persoane;
        this.repo_participanti=repo_participanti;
        loggedClients = new ConcurrentHashMap<>();
    }


    public Long getNewPersoanaId()
    {
        return repo_persoane.getNewId();
    }

    public void addPersoana(Persoana p)
    {
        repo_persoane.save(p);
    }


    public Long getNewParticipantId()
    {
        return repo_participanti.getNewId();
    }


    public synchronized boolean login(String email, String password, IObserver client) throws ConcursException {
        Angajat a=repo_angajati.findOneByEmailAndPassword(email,password);
        if(a!=null) {
            if (loggedClients.get(a.getEmail()) != null)
                throw new ConcursException("User already logged in.");
            loggedClients.put(email, client);
            return true;
        }
        return false;
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
        System.out.println("pas 3");
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




    @Override
    public List<Integer> getNumarParticipantiDupaProba()
    {
        String[] styles={"liber","spate","flutur","mixt"};
        String[] distances= {"dist50m","dist200m","dist800m","dist1500m"};
        List<Integer> valori=new ArrayList<>();
        for(String stil:styles)
            for(String distanta:distances)
                valori.add(repo_participanti.getNumarParticipantiDupaProba(stil,distanta));
        return valori;
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
        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNo);

        loggedClients.forEach((email, client) -> {
            executor.execute(() -> {
                System.out.println("Notifying [" + email + "] about new participant.");
                client.participantInscris();
            });
        });
        executor.shutdown();
    }



    @Override
    public void logout(String email) throws ConcursException
    {
        IObserver localClient = loggedClients.remove(email);
        if (localClient == null)
            throw new ConcursException("User " + email + " is not logged in.");
    }
}
