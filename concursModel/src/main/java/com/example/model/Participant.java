package com.example.model;

public class Participant extends Entity<Long>{
    private Long idPersoana;
    private Distanta distanta;
    private Stil stil;

    public Participant(){}


    public Participant(Long idPersoana, Distanta distanta, Stil stil)
    {
        this.idPersoana=idPersoana;
        this.distanta=distanta;
        this.stil=stil;
    }

    public Long getIdPersoana()
    {
        return idPersoana;
    }

    public void setIdPersoana(Long idPersoana)
    {
        this.idPersoana = idPersoana;
    }

    public Distanta getDistanta()
    {
        return distanta;
    }

    public void setDistanta(Distanta distanta)
    {
        this.distanta = distanta;
    }

    public Stil getStil()
    {
        return stil;
    }

    public void setStil(Stil stil)
    {
        this.stil = stil;
    }
}
