package com.example.networking.protobuff;

import com.example.model.Angajat;
import com.example.model.Participant;
import com.example.model.ParticipantDTO;

import java.util.ArrayList;
import java.util.List;

public class ProtoUtils {
    public static AppProtobuffs.RequestP createLoginRequest(Angajat angajat){
        AppProtobuffs.AngajatP angajatP=AppProtobuffs.AngajatP.newBuilder().setEmail(angajat.getEmail()).setParola(angajat.getParola()).build();
        AppProtobuffs.RequestP request= AppProtobuffs.RequestP.newBuilder().setType(AppProtobuffs.RequestP.Type.LOGIN)
                .setAngajat(angajatP).build();
        return request;
    }


    public static AppProtobuffs.RequestP createGetParticipantiRequest()
    {
        AppProtobuffs.RequestP request=AppProtobuffs.RequestP.newBuilder().setType(AppProtobuffs.RequestP.Type.GET_PARTICIPANTS).build();
        return request;
    }



    public static AppProtobuffs.RequestP createGetNrParticipantiDupaProbaRequest()
    {
        AppProtobuffs.RequestP request=AppProtobuffs.RequestP.newBuilder().setType(AppProtobuffs.RequestP.Type.GET_NUMBER_OF_PARTICIPANTS).build();
        return request;
    }


    public static AppProtobuffs.RequestP createGetSearchedParticipantiRequest(String stil, String distanta)
    {
        AppProtobuffs.RequestP request=AppProtobuffs.RequestP.newBuilder().setType(AppProtobuffs.RequestP.Type.GET_CONTEST_PARTICIPANTS).setStr(stil+" "+distanta).build();
        return request;
    }


    public static AppProtobuffs.RequestP createAddParticipantRequest(ParticipantDTO participantDTO)
    {
        AppProtobuffs.ParticipantDTOP participantDTOP=AppProtobuffs.ParticipantDTOP.newBuilder().setNume(participantDTO.getNume()).setVarsta(participantDTO.getVarsta()).
                setStil(participantDTO.getStil()).setDistanta(participantDTO.getDistanta()).build();
        AppProtobuffs.RequestP request=AppProtobuffs.RequestP.newBuilder().setType(AppProtobuffs.RequestP.Type.ADD_PARTICIPANT).setParticipantDTO(participantDTOP).build();
        return request;
    }


    public static AppProtobuffs.RequestP createLogoutRequest(Angajat angajat){
        AppProtobuffs.AngajatP angajatP=AppProtobuffs.AngajatP.newBuilder().setEmail(angajat.getEmail()).setParola(angajat.getParola()).build();
        AppProtobuffs.RequestP request= AppProtobuffs.RequestP.newBuilder().setType(AppProtobuffs.RequestP.Type.LOGOUT)
                .setAngajat(angajatP).build();
        return request;
    }



    public static List<Integer> getStatisticsFromResponse(AppProtobuffs.ResponseP response)
    {
        List<Integer> statistics=new ArrayList<>();
        response.getStatisticsList().forEach(el->{
                statistics.add(el);});
        return statistics;
    }



    public static List<ParticipantDTO> getAllFromResponse(AppProtobuffs.ResponseP response)
    {
        List<ParticipantDTO> lista_participanti=new ArrayList<>();
        response.getAllList().forEach(el->{
            ParticipantDTO p=new ParticipantDTO(el.getNume(),el.getVarsta(),el.getStil(),el.getDistanta());
            lista_participanti.add(p);
        });
        return lista_participanti;
    }


    public static List<ParticipantDTO> getSearchedFromResponse(AppProtobuffs.ResponseP response)
    {
        List<ParticipantDTO> lista_participanti=new ArrayList<>();
        response.getSearchedList().forEach(el->{
            ParticipantDTO p=new ParticipantDTO(el.getNume(),el.getVarsta(),el.getStil(),el.getDistanta());
            lista_participanti.add(p);
        });
        return lista_participanti;
    }



    public static AppProtobuffs.ResponseP createOkResponse(){
        AppProtobuffs.ResponseP response=AppProtobuffs.ResponseP.newBuilder()
                .setType(AppProtobuffs.ResponseP.Type.OK).build();
        return response;
    }


    public static AppProtobuffs.ResponseP createErrorResponse(String text){
        AppProtobuffs.ResponseP response=AppProtobuffs.ResponseP.newBuilder()
                .setType(AppProtobuffs.ResponseP.Type.ERROR)
                .setMessage(text).build();
        return response;
    }



    public static AppProtobuffs.ResponseP createLoginResponse(boolean loggedIn){
        AppProtobuffs.ResponseP response=AppProtobuffs.ResponseP.newBuilder()
                .setType(AppProtobuffs.ResponseP.Type.OK)
                .setBoolean(loggedIn).build();
        return response;
    }


    public static AppProtobuffs.ResponseP createGetNumberOfParticipantsResponse(List<Integer> statistics)
    {
        AppProtobuffs.ResponseP.Builder response=AppProtobuffs.ResponseP.newBuilder()
                .setType(AppProtobuffs.ResponseP.Type.OK);
        for(Integer value:statistics)
            response.addStatistics(value);
        return response.build();
    }


    public static AppProtobuffs.ResponseP createGetParticipantiResponse(List<ParticipantDTO> participants)
    {
        AppProtobuffs.ResponseP.Builder response=AppProtobuffs.ResponseP.newBuilder()
                .setType(AppProtobuffs.ResponseP.Type.OK);
        for(ParticipantDTO p:participants)
        {
            AppProtobuffs.ParticipantDTOP participantDTOP=AppProtobuffs.ParticipantDTOP.newBuilder().setNume(p.getNume()).setVarsta(p.getVarsta()).
                    setStil(p.getStil()).setDistanta(p.getDistanta()).build();
            response.addAll(participantDTOP);
        }
        return response.build();
    }


    public static AppProtobuffs.ResponseP createGetSearchedParticipantiResponse(List<ParticipantDTO> participants)
    {
        AppProtobuffs.ResponseP.Builder response=AppProtobuffs.ResponseP.newBuilder()
                .setType(AppProtobuffs.ResponseP.Type.OK);
        for(ParticipantDTO p:participants)
        {
            AppProtobuffs.ParticipantDTOP participantDTOP=AppProtobuffs.ParticipantDTOP.newBuilder().setNume(p.getNume()).setVarsta(p.getVarsta()).
                    setStil(p.getStil()).setDistanta(p.getDistanta()).build();
            response.addSearched(participantDTOP);
        }
        return response.build();
    }
}
