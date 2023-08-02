package com.example.networking.rpcprotocol;

import com.example.model.*;
import com.example.services.ConcursException;
import com.example.services.IObserver;
import com.example.services.IServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientRpcWorker implements Runnable, IObserver {

    private IServices server;

    private Socket connection;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;

    private static final Logger logger= LogManager.getLogger();

    public ClientRpcWorker(IServices server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try{
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();

            input = new ObjectInputStream(connection.getInputStream());
            connected = true;

        }catch (IOException e){
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        while (connected){
            try{
                Object request = input.readObject();
                Object response = handleRequest((Request)request);
                if (response!=null){
                    sendResponse((Response) response);
                }
            }catch (IOException e){
                e.printStackTrace();
            }catch (ClassNotFoundException e){
                e.printStackTrace();
            }

            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        try{
            input.close();
            output.close();
            connection.close();
        }catch (IOException e){
            logger.error(e.getMessage());
        }

    }


    private Response handleRequest(Request request){
        Response response = null;
        switch (request.type()) {
            case LOGIN: {
                response = solveLogin(request);
                break;
            }
            case LOGOUT: {
                response = solveLogout(request);
                break;
            }
            case GET_NUMBER_OF_PARTICIPANTS: {
                response=solveNumberOfParticipants(request);
                break;
            }
            case GET_PARTICIPANTS:
            {
                response=solveGetParticipanti(request);
                break;
            }
            case GET_CONTEST_PARTICIPANTS:
            {
                response=solveGetSearchedParticipanti(request);
                break;
            }
            case ADD_PARTICIPANT:
            {
                response=solveInscriereParticipant(request);
                break;
            }
        }
        return response;
    }



    private Response solveLogin(Request request) {
        Angajat angajat= (Angajat) request.data();
        String email= angajat.getEmail();
        String password = angajat.getParola();

        try {
            Boolean loggedIn = server.login(email, password, this);
            return new Response.Builder().type(ResponseType.OK).data(loggedIn).build();
        } catch (ConcursException e) {
            connected = false;
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }



    private Response solveNumberOfParticipants(Request request)
    {
        try
        {
            List<Integer> result=server.getNumarParticipantiDupaProba();
            return new Response.Builder().type(ResponseType.OK).data(result).build();
        } catch (ConcursException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }



    private Response solveGetParticipanti(Request request)
    {
        try {
            List<ParticipantDTO> participanti=server.getParticipanti();
            return new Response.Builder().type(ResponseType.OK).data(participanti).build();
        } catch (ConcursException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }



    private Response solveGetSearchedParticipanti(Request request)
    {
        String stil_distanta=request.data().toString();
        String[] parts=stil_distanta.split(" ");
        String stil=parts[0],distanta=parts[1];
        try
        {
            List<ParticipantDTO> participanti=server.getSearchedParticipanti(stil,distanta);
            return new Response.Builder().type(ResponseType.OK).data(participanti).build();
        } catch (ConcursException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }



    private Response solveInscriereParticipant(Request request)
    {
        ParticipantDTO p=(ParticipantDTO) request.data();
        try
        {
            server.inscriereParticipant(p.getNume(),p.getVarsta(), Stil.valueOf(p.getStil()), Distanta.valueOf(p.getDistanta()));
            return new Response.Builder().type(ResponseType.OK).build();
        } catch (ConcursException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }



    private Response solveLogout(Request request) {
        Angajat angajat = (Angajat) request.data();
        String email= angajat.getEmail();
        try {
            server.logout(email);
            connected = false;
            return new Response.Builder().type(ResponseType.OK).data(true).build();
        } catch (ConcursException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }




    private void sendResponse(Response response) throws IOException{
        logger.trace("sending response : " + response);
        synchronized (output){
            output.writeObject(response);
            output.flush();
        }
    }

    @Override
    public void participantInscris() {
        Response resp = new Response.Builder().type(ResponseType.UPDATE).build();
        logger.info("Participant added");
        try {
            sendResponse(resp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
