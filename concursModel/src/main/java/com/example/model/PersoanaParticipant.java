package com.example.model;

public class PersoanaParticipant extends Entity<Long> {
    private Persoana persoana;
    private Participant participant;

    public PersoanaParticipant(Persoana persoana, Participant participant)
    {
        this.persoana = persoana;
        this.participant = participant;
    }

    public Persoana getPersoana()
    {
        return persoana;
    }

    public void setPersoana(Persoana persoana)
    {
        this.persoana = persoana;
    }

    public Participant getParticipant()
    {
        return participant;
    }

    public void setParticipant(Participant participant)
    {
        this.participant = participant;
    }
}
