package com.example.networking.protobuff;

import com.example.model.Angajat;
import com.example.model.Distanta;
import com.example.model.ParticipantDTO;
import com.example.model.Stil;
import com.example.networking.rpcprotocol.Request;
import com.example.networking.rpcprotocol.Response;
import com.example.networking.rpcprotocol.ResponseType;
import com.example.services.ConcursException;
import com.example.services.IObserver;
import com.example.services.IServices;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ProtobuffWorker implements Runnable, IObserver {

    private IServices server;

    private Socket connection;

    private InputStream input;
    private OutputStream output;
    private volatile boolean connected;


    public ProtobuffWorker(IServices server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try{
            output=connection.getOutputStream();
            input=connection.getInputStream();
            connected=true;
        }catch (IOException e){
            e.printStackTrace();
        }
    }



    @Override
    public void run() {
        while (connected){
            try{
                AppProtobuffs.RequestP request= AppProtobuffs.RequestP.parseDelimitedFrom(input);
                AppProtobuffs.ResponseP response=handleRequest(request);
                if (response!=null){
                    sendResponse(response);
                }
            }catch (IOException e){
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
            System.out.println("Error : " + e);
        }

    }



    private AppProtobuffs.ResponseP handleRequest(AppProtobuffs.RequestP request){
        AppProtobuffs.ResponseP response=null;
        switch (request.getType()) {
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
//            case ADD_PARTICIPANT:
//            {
//                response=solveInscriereParticipant(request);
//                break;
//            }
        }
        return response;
    }



    private AppProtobuffs.ResponseP solveLogin(AppProtobuffs.RequestP request) {
        AppProtobuffs.AngajatP angajatP=request.getAngajat();
        Angajat angajat=new Angajat(angajatP.getEmail(),angajatP.getParola());
        String email= angajat.getEmail();
        String password = angajat.getParola();

        try {
            boolean loggedIn=server.login(email, password, this);
            return ProtoUtils.createLoginResponse(loggedIn);
        } catch (ConcursException e) {
            connected = false;
            return ProtoUtils.createErrorResponse(e.getMessage());
        }
    }



    private AppProtobuffs.ResponseP solveNumberOfParticipants(AppProtobuffs.RequestP request)
    {
        try
        {
            List<Integer> result=server.getNumarParticipantiDupaProba();
            return ProtoUtils.createGetNumberOfParticipantsResponse(result);
        } catch (ConcursException e) {
            return ProtoUtils.createErrorResponse(e.getMessage());
        }
    }



    private AppProtobuffs.ResponseP solveGetParticipanti(AppProtobuffs.RequestP request)
    {
        try {
            List<ParticipantDTO> participanti=server.getParticipanti();
            return ProtoUtils.createGetParticipantiResponse(participanti);
        } catch (ConcursException e) {
            return ProtoUtils.createErrorResponse(e.getMessage());
        }
    }



    private AppProtobuffs.ResponseP solveGetSearchedParticipanti(AppProtobuffs.RequestP request)
    {
        String stil_distanta=request.getStr();
        String[] parts=stil_distanta.split(" ");
        String stil=parts[0],distanta=parts[1];
        try
        {
            List<ParticipantDTO> participanti=server.getSearchedParticipanti(stil,distanta);
            return ProtoUtils.createGetSearchedParticipantiResponse(participanti);
        } catch (ConcursException e) {
            return ProtoUtils.createErrorResponse(e.getMessage());
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



    private AppProtobuffs.ResponseP solveLogout(AppProtobuffs.RequestP request) {
        AppProtobuffs.AngajatP angajatP=request.getAngajat();
        Angajat angajat=new Angajat(angajatP.getEmail(),angajatP.getParola());
        String email= angajat.getEmail();
        try {
            server.logout(email);
            connected = false;
            return ProtoUtils.createOkResponse();
        } catch (ConcursException e) {
            return ProtoUtils.createErrorResponse(e.getMessage());
        }
    }




    private void sendResponse(AppProtobuffs.ResponseP response) throws IOException{
        System.out.println("sending response : " + response);
        synchronized (output){
            response.writeDelimitedTo(output);
            output.flush();
        }
    }


    @Override
    public void participantInscris() {
//        Response resp = new Response.Builder().type(ResponseType.UPDATE).build();
//        System.out.println("Participant added");
//        try {
//            sendResponse(resp);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}