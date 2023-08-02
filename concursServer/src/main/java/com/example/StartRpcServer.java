package com.example;

import com.example.networking.servers.AbstractServer;
import com.example.networking.servers.RpcConcurrentServer;
import com.example.networking.servers.ServerException;
import com.example.persistence.Database.AngajatiDatabaseRepository;
import com.example.persistence.Database.ParticipantiDatabaseRepository;
import com.example.persistence.Database.PersoaneDatabaseRepository;
import com.example.server.ServiceImpl;
import com.example.services.IServices;


import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class StartRpcServer {
    private static int defaultPort=55555;

    public static void main(String[] args){
        Properties serverProps=new Properties();
        try {
            serverProps.load(new FileReader("C:\\Users\\Marius Andreiasi\\JavaProjects\\concursInot\\concursServer\\src\\main\\resources\\server.properties"));
            System.out.println("Server properties set. ");
            serverProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find server.properties "+e);
            return;
        }
        AngajatiDatabaseRepository repo_angajati=new AngajatiDatabaseRepository(serverProps);
        PersoaneDatabaseRepository repo_persoane=new PersoaneDatabaseRepository(serverProps);
        ParticipantiDatabaseRepository repo_participanti=new ParticipantiDatabaseRepository(serverProps);



        IServices ServerImpl = new ServiceImpl(repo_angajati,repo_persoane,repo_participanti);
        int serverPort = defaultPort;
        try {
            serverPort = Integer.parseInt(serverProps.getProperty("server.port"));
        } catch (NumberFormatException nef) {
            System.err.println("Wrong Port Number" + nef.getMessage());
            System.err.println("Using default port " + defaultPort);
        }
        System.out.println("Starting server on port: " + serverPort);
        AbstractServer server = new RpcConcurrentServer(serverPort, ServerImpl);
        try {
            server.start();
        } catch (ServerException e) {
            System.err.println("Error starting the server" + e.getMessage());
        } finally {
            try {
                server.stop();
            } catch (ServerException e) {
                System.err.println("Error stopping server " + e.getMessage());
            }
        }
    }
}
