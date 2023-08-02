package com.example.model;

public class Angajat extends Entity<Long> {
    private String email;
    private String parola;

    public Angajat(){}

    public Angajat(String email, String parola)
    {
        this.email=email;
        this.parola=parola;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getParola()
    {
        return parola;
    }

    public void setParola(String parola)
    {
        this.parola = parola;
    }
}
