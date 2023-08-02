package com.example.networking.rpcprotocol;


import com.example.model.Angajat;
import com.example.model.Distanta;
import com.example.model.ParticipantDTO;
import com.example.model.Stil;
import com.example.services.ConcursException;
import com.example.services.IObserver;
import com.example.services.IServices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class ServicesRpcProxy implements IServices {
    private String host;
    private int port;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;
    private BlockingQueue<Response> qresponses;
    private volatile boolean finished;
    private IObserver client = null;
    private static final Logger logger= LogManager.getLogger();

    public ServicesRpcProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingDeque<Response>();
    }

    @Override
    public boolean login(String data, String password, IObserver client) throws ConcursException {
        initializeConnection();
        Angajat angajat=new Angajat(data,password);
        Request request = new Request.Builder().type(RequestType.LOGIN).data(angajat).build();
        sendRequest(request);
        Response response = readResponse();

        Boolean loggedIn = false;

        if (response.type() == ResponseType.OK) {
            loggedIn = (Boolean) response.data();
            if (loggedIn) {
                this.client = client;
                return true;
            }
        } else if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            closeConnection();
            throw new ConcursException(err);
        }
        return loggedIn;
    }



    @Override
    public List<Integer> getNumarParticipantiDupaProba() throws ConcursException
    {
        Request req = new Request.Builder().type(RequestType.GET_NUMBER_OF_PARTICIPANTS).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new ConcursException(err);
        }
        return (List<Integer>)response.data();
    }



    public List<ParticipantDTO> getParticipanti() throws ConcursException
    {
        Request req = new Request.Builder().type(RequestType.GET_PARTICIPANTS).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new ConcursException(err);
        }
        List<ParticipantDTO> participanti=(List<ParticipantDTO>)response.data();
        return participanti;
    }



    public List<ParticipantDTO> getSearchedParticipanti(String stil,String distanta) throws ConcursException
    {
        Request req=new Request.Builder().type(RequestType.GET_CONTEST_PARTICIPANTS).data(stil+" "+distanta).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new ConcursException(err);
        }
        List<ParticipantDTO> participanti=(List<ParticipantDTO>)response.data();
        return participanti;
    }



    public void inscriereParticipant(String nume, int varsta, Stil stil, Distanta distanta) throws ConcursException
    {
        ParticipantDTO participantDTO=new ParticipantDTO(nume,varsta,stil.toString(),distanta.toString());
        Request req = new Request.Builder().type(RequestType.ADD_PARTICIPANT).data(participantDTO).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new ConcursException(err);
        }
    }


    @Override
    public void logout(String notused) throws ConcursException {
        Angajat angajat=new Angajat(notused,null);
        Request req = new Request.Builder().type(RequestType.LOGOUT).data(angajat).build();
        sendRequest(req);
        Response response = readResponse();
        closeConnection();
        client = null;
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new ConcursException(err);
        }
    }


    private void sendRequest(Request request)throws ConcursException {
        try {
            output.writeObject(request);
            output.flush();
        } catch (IOException e) {
            throw new ConcursException("Error sending object "+e);
        }
    }

    private Response readResponse() throws ConcursException {
        Response response=null;
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
            output=new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input=new ObjectInputStream(connection.getInputStream());
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
        Thread tw=new Thread(new ReaderThread());
        tw.start();
    }



    private class ReaderThread implements Runnable {
        public void run() {
            while (!finished) {
                try {
                    Object response = input.readObject();
                    logger.trace("response received " + response);

                    if (((Response)response).type() == ResponseType.UPDATE)
                        client.participantInscris();
                    else {
                        try {
                            qresponses.put((Response) response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    logger.trace("Reading error " + e);
                }
            }
        }
    }

}
