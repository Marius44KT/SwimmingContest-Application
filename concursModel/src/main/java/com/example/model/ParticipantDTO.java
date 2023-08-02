package com.example.model;

import java.io.Serializable;

public class ParticipantDTO implements Serializable {
    private String nume;
    private int varsta;
    private String Stil;
    private String distanta;

    public ParticipantDTO(String nume, int varsta, String stil, String distanta)
    {
        this.nume = nume;
        this.varsta = varsta;
        Stil = stil;
        this.distanta = distanta;
    }

    public String getNume()
    {
        return nume;
    }

    public void setNume(String nume)
    {
        this.nume = nume;
    }

    public int getVarsta()
    {
        return varsta;
    }

    public void setVarsta(int varsta)
    {
        this.varsta = varsta;
    }

    public String getStil()
    {
        return Stil;
    }

    public void setStil(String stil)
    {
        Stil = stil;
    }

    public String getDistanta()
    {
        return distanta;
    }

    public void setDistanta(String distanta)
    {
        this.distanta = distanta;
    }
}
