package com.example.networking.servers;


import com.example.networking.protobuff.ProtobuffWorker;
import com.example.networking.rpcprotocol.ClientRpcWorker;
import com.example.services.IServices;

import java.net.Socket;

public class RpcConcurrentServer extends AbsConcurrentServer{

    private IServices server;

    public RpcConcurrentServer(int port, IServices s) {
        super(port);
        this.server = s;
        System.out.println("RpcConcurrentServer");
    }



    @Override
    protected Thread createWorker(Socket client) {
        ClientRpcWorker worker = new ClientRpcWorker(server, client);
//        ProtobuffWorker worker = new ProtobuffWorker(server, client);
        Thread t = new Thread(worker);
        return t;
    }

    @Override
    public void stop(){
        System.out.println("Stopping services ...");
    }
}
