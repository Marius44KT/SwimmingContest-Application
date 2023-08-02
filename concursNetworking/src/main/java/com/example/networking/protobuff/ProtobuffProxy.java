package com.example.networking.protobuff;

import com.example.model.Angajat;
import com.example.model.Distanta;
import com.example.model.ParticipantDTO;
import com.example.model.Stil;
import com.example.networking.rpcprotocol.Request;
import com.example.networking.rpcprotocol.RequestType;
import com.example.networking.rpcprotocol.Response;
import com.example.networking.rpcprotocol.ResponseType;
import com.example.services.ConcursException;
import com.example.services.IObserver;
import com.example.services.IServices;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class ProtobuffProxy implements IServices {
    private String host;
    private int port;
    private InputStream input;  //
    private OutputStream output; //
    private Socket connection;
    private BlockingQueue<AppProtobuffs.ResponseP> qresponses;
    private volatile boolean finished;
    private IObserver client = null;

    public ProtobuffProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingDeque<AppProtobuffs.ResponseP>();
    }


    @Override
    public boolean login(String data, String password, IObserver client) throws ConcursException
    {
        initializeConnection();
        Angajat angajat=new Angajat(data,password);
        //Request request = new Request.Builder().type(RequestType.LOGIN).data(angajat).build();
        AppProtobuffs.RequestP request=ProtoUtils.createLoginRequest(angajat);
        sendRequest(request);
        AppProtobuffs.ResponseP response = readResponse();
        if (response.getType()==AppProtobuffs.ResponseP.Type.OK) {
                if(response.getBoolean())
                {
                    this.client = client;
                    return true;
                }
                else
                    return false;
        } else if (response.getType() ==  AppProtobuffs.ResponseP.Type.ERROR) {
            String err = response.getMessage();
            closeConnection();
            throw new ConcursException(err);
        }
        return false;
    }



    @Override
    public List<Integer> getNumarParticipantiDupaProba() throws ConcursException
    {
        //Request req = new Request.Builder().type(RequestType.GET_NUMBER_OF_PARTICIPANTS).build();
        AppProtobuffs.RequestP request=ProtoUtils.createGetNrParticipantiDupaProbaRequest();
        sendRequest(request);
        AppProtobuffs.ResponseP response=readResponse();
        if (response.getType() == AppProtobuffs.ResponseP.Type.ERROR) {
            String err = response.getMessage();
            throw new ConcursException(err);
        }
        return ProtoUtils.getStatisticsFromResponse(response);
    }



    public List<ParticipantDTO> getParticipanti() throws ConcursException
    {
        //Request req = new Request.Builder().type(RequestType.GET_PARTICIPANTS).build();
        AppProtobuffs.RequestP request=ProtoUtils.createGetParticipantiRequest();
        sendRequest(request);
        AppProtobuffs.ResponseP response=readResponse();
        if (response.getType() == AppProtobuffs.ResponseP.Type.ERROR) {
            String err = response.getMessage();
            throw new ConcursException(err);
        }
        List<ParticipantDTO> participanti=ProtoUtils.getAllFromResponse(response);
        return participanti;
    }



    public List<ParticipantDTO> getSearchedParticipanti(String stil,String distanta) throws ConcursException
    {
        //Request req=new Request.Builder().type(RequestType.GET_CONTEST_PARTICIPANTS).data(stil+" "+distanta).build();
        AppProtobuffs.RequestP request=ProtoUtils.createGetSearchedParticipantiRequest(stil,distanta);
        sendRequest(request);
        AppProtobuffs.ResponseP response=readResponse();
        if (response.getType() == AppProtobuffs.ResponseP.Type.ERROR) {
            String err = response.getMessage();
            throw new ConcursException(err);
        }
        List<ParticipantDTO> participanti=ProtoUtils.getSearchedFromResponse(response);
        return participanti;
    }



    public void inscriereParticipant(String nume, int varsta, Stil stil, Distanta distanta) throws ConcursException
    {
        ParticipantDTO participantDTO=new ParticipantDTO(nume,varsta,stil.toString(),distanta.toString());
        //Request req = new Request.Builder().type(RequestType.ADD_PARTICIPANT).data(participantDTO).build();
        AppProtobuffs.RequestP request=ProtoUtils.createAddParticipantRequest(participantDTO);
        sendRequest(request);
        AppProtobuffs.ResponseP response=readResponse();
        if (response.getType() == AppProtobuffs.ResponseP.Type.ERROR) {
            String err = response.getMessage();
            throw new ConcursException(err);
        }
    }



    @Override
    public void logout(String notused) throws ConcursException {
        Angajat angajat=new Angajat(notused,"password");
        //Request req = new Request.Builder().type(RequestType.LOGOUT).data(angajat).build();
        AppProtobuffs.RequestP request=ProtoUtils.createLogoutRequest(angajat);
        sendRequest(request);
        AppProtobuffs.ResponseP response = readResponse();
        closeConnection();
        client = null;
        if (response.getType() == AppProtobuffs.ResponseP.Type.ERROR) {
            String err = response.getMessage();
            throw new ConcursException(err);
        }
    }


    private void sendRequest(AppProtobuffs.RequestP request)throws ConcursException {
        try {
            request.writeDelimitedTo(output);
            output.flush();
        } catch (IOException e) {
            throw new ConcursException("Error sending object "+e);
        }
    }

    private AppProtobuffs.ResponseP readResponse() throws ConcursException {
        AppProtobuffs.ResponseP response=null;
        try{
            response=qresponses.take();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }



    private void initializeConnection() {
        try {
            connection = new Socket(host, port);
            output=connection.getOutputStream(); //
            input=connection.getInputStream(); //
            finished=false;
            startReader();
        }catch (IOException e){
            e.printStackTrace();
        }
    }



    private void closeConnection() {
        finished=true;
        try {
            input.close();
            output.close();
            connection.close();
            client=null;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void startReader(){
        Thread tw=new Thread(new ProtobuffProxy.ReaderThread());
        tw.start();
    }



    private class ReaderThread implements Runnable {
        public void run() {
            while (!finished) {
                try {
                    AppProtobuffs.ResponseP response= AppProtobuffs.ResponseP.parseDelimitedFrom(input);
                    System.out.println("response received " + response);

                    if (response.getType()== AppProtobuffs.ResponseP.Type.UPDATE)
                        client.participantInscris();
                    else {
                        try {
                            qresponses.put(response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Reading error " + e);
                }
            }
        }
    }

}