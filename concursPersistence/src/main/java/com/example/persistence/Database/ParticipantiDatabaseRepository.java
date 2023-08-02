package com.example.persistence.Database;


import com.example.model.Distanta;
import com.example.model.Participant;
import com.example.model.Stil;
import com.example.persistence.ParticipantiIRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class ParticipantiDatabaseRepository implements ParticipantiIRepository<Long, Participant> {


    private static final Logger logger= LogManager.getLogger();
    private Properties props;


    public ParticipantiDatabaseRepository(Properties props)
    {
        logger.info("Initializing ParticipantiRepository with properties: {} ",props);
        this.props=props;
    }



    @Override
    public Participant findOne(Long idParticipant)
    {
        logger.traceEntry();
        String sql="SELECT * from participanti where id=?";
        try (Connection connection = DriverManager.getConnection(props.getProperty("jdbc.url"),props.getProperty("jdbc.username"),props.getProperty("jdbc.password"));
             PreparedStatement ps=connection.prepareStatement(sql))
        {
            ps.setLong(1,idParticipant);
            ResultSet resultSet=ps.executeQuery();
            if(resultSet.next())
            {
                Long idPersoana=resultSet.getLong("idpersoana");
                String distanta=resultSet.getString("distanta");
                String stil=resultSet.getString("stil");

                Participant participant =new Participant(idPersoana, Distanta.valueOf(distanta), Stil.valueOf(stil));
                participant.setId(idParticipant);
                return participant;
            }
            else
                return null;
        } catch (SQLException e) {
            //e.printStackTrace();
            logger.error(e);
            return null;
        }
    }


    @Override
    public Map<Long, Participant> findAll()
    {
        logger.traceEntry();
        Map<Long, Participant> probe=new HashMap<>();
        String sql="SELECT * from participanti";
        try (Connection connection = DriverManager.getConnection(props.getProperty("jdbc.url"),props.getProperty("jdbc.username"),props.getProperty("jdbc.password"));
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery())
        {
            while (resultSet.next()) {
                Long idProba=resultSet.getLong("id");
                Long idPersoana=resultSet.getLong("idpersoana");
                String distanta=resultSet.getString("distanta");
                String stil=resultSet.getString("stil");

                Participant participant =new Participant(idPersoana, Distanta.valueOf(distanta), Stil.valueOf(stil));
                participant.setId(idProba);
                probe.put(idProba, participant);
            }
        } catch (SQLException e) {
            logger.error(e);
            return null;
        }
        return probe;
    }



    @Override
    public boolean save(Participant participant)
    {
        logger.traceEntry();
        String sql="insert into participanti (id,idpersoana,distanta,stil) values (?,?,?,?)";
        try (Connection connection=DriverManager.getConnection(props.getProperty("jdbc.url"),props.getProperty("jdbc.username"),props.getProperty("jdbc.password"));
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            System.out.println(participant.getIdPersoana()+" "+participant.getDistanta()+" "+participant.getStil());
            ps.setLong(1, participant.getId());
            ps.setLong(2, participant.getIdPersoana());
            ps.setString(3, participant.getDistanta().toString());
            ps.setString(4, participant.getStil().toString());
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            logger.error(e);
            return false;
        }
        logger.traceExit();
        return true;
    }


    @Override
    public boolean delete(Long idProba)
    {
        logger.traceEntry();
        String sql="delete from participanti where id=?";
        try (Connection connection = DriverManager.getConnection(props.getProperty("jdbc.url"),props.getProperty("jdbc.username"),props.getProperty("jdbc.password"));
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1,idProba);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error(e);
            return false;
        }
        logger.traceExit();
        return true;
    }


    @Override
    public boolean update(Participant participant)
    {
        logger.traceEntry();
        String sql="update participanti set idpersoana=?,distanta=?,stil=? where id=?";
        try (Connection connection = DriverManager.getConnection(props.getProperty("jdbc.url"),props.getProperty("jdbc.username"),props.getProperty("jdbc.password"));
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, participant.getIdPersoana());
            ps.setString(2, participant.getDistanta().toString());
            ps.setString(3, participant.getStil().toString());
            ps.setLong(4, participant.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error(e);
            return false;
        }
        logger.traceExit();
        return true;
    }




    public Map<Long, Participant> findAllByCompetition(String st, String dist)
    {
        logger.traceEntry();
        Map<Long, Participant> concurenti=new HashMap<>();
        String sql="SELECT * from participanti where idpersoana in (select idpersoana from participanti where stil=? and distanta=?)";
        try (Connection connection = DriverManager.getConnection(props.getProperty("jdbc.url"),props.getProperty("jdbc.username"),props.getProperty("jdbc.password"));
             PreparedStatement ps=connection.prepareStatement(sql))
        {
            ps.setString(1,st);
            ps.setString(2,dist);
            ResultSet resultSet=ps.executeQuery();
            while(resultSet.next())
            {
                Long id=resultSet.getLong("id");
                Long idPersoana=resultSet.getLong("idpersoana");
                String distanta=resultSet.getString("distanta");
                String stil=resultSet.getString("stil");

                Participant participant =new Participant(idPersoana, Distanta.valueOf(distanta), Stil.valueOf(stil));
                participant.setId(id);
                concurenti.put(id, participant);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return null;
        }
        logger.traceExit(concurenti);
        return concurenti;
    }



    public Integer getNumarParticipantiDupaProba(String stil,String dist)
    {
        logger.traceEntry();
        String sql="SELECT count(*) from participanti where stil=? and distanta=?";
        try (Connection connection = DriverManager.getConnection(props.getProperty("jdbc.url"),props.getProperty("jdbc.username"),props.getProperty("jdbc.password"));
             PreparedStatement ps= connection.prepareStatement(sql))
        {
            ps.setString(1,stil);
            ps.setString(2,dist);
            ResultSet resultSet=ps.executeQuery();
            if(resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
         catch (SQLException e) {
             logger.error(e.getMessage());
        }
        logger.traceExit();
        return null;
    }



    public Long getNewId()
    {
        logger.traceEntry();
        try (Connection connection = DriverManager.getConnection(props.getProperty("jdbc.url"),props.getProperty("jdbc.username"),props.getProperty("jdbc.password"));
             PreparedStatement ps= connection.prepareStatement("SELECT max(id) from participanti");
             ResultSet resultSet = ps.executeQuery()) {
            if(resultSet.next()) {
                return resultSet.getLong(1)+1;
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        logger.traceExit();
        return null;
    }
}
