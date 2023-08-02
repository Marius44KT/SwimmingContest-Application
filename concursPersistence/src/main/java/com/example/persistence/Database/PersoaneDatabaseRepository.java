package com.example.persistence.Database;


import com.example.model.Persoana;
import com.example.persistence.PersoaneIRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class PersoaneDatabaseRepository implements PersoaneIRepository<Long, Persoana> {


    private static final Logger logger= LogManager.getLogger();
    private final Properties props;


    public PersoaneDatabaseRepository(Properties props)
    {
        logger.info("Initializing PersoaneRepository with properties: {} ",props);
        this.props=props;
    }


    @Override
    public Persoana findOne(Long idParticipant)
    {
        logger.traceEntry();
        String sql="SELECT * from persoane where id=?";
        try (Connection connection = DriverManager.getConnection(props.getProperty("jdbc.url"),props.getProperty("jdbc.username"),props.getProperty("jdbc.password"));
             PreparedStatement ps=connection.prepareStatement(sql))
        {
            ps.setLong(1,idParticipant);
            ResultSet resultSet=ps.executeQuery();
            if(resultSet.next())
            {
                String nume=resultSet.getString("nume");
                int varsta=resultSet.getInt("varsta");
                Persoana p=new Persoana(nume,varsta);
                p.setId(idParticipant);
                logger.traceExit();
                return p;
            }
            else
                return null;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return null;
        }
    }


    @Override
    public Map<Long, Persoana> findAll()
    {
        logger.traceEntry();
        Map<Long, Persoana> participanti=new HashMap<>();
        String sql="SELECT * from persoane";
        try (Connection connection = DriverManager.getConnection(props.getProperty("jdbc.url"),props.getProperty("jdbc.username"),props.getProperty("jdbc.password"));
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery())
        {
            while (resultSet.next()) {
                Long participantId=resultSet.getLong("id");
                String nume=resultSet.getString("nume");
                int varsta=resultSet.getInt("varsta");
                Persoana p=new Persoana(nume,varsta);
                p.setId(participantId);
                participanti.put(participantId,p);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return null;
        }
        logger.traceExit(participanti);
        return participanti;
    }


    @Override
    public boolean save(Persoana persoana)
    {
        logger.traceEntry();
        String sql="insert into persoane (id,nume,varsta) values (?,?,?)";
        try (Connection connection=DriverManager.getConnection(props.getProperty("jdbc.url"),props.getProperty("jdbc.username"),props.getProperty("jdbc.password"));
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setLong(1, persoana.getId());
            ps.setString(2, persoana.getNume());
            ps.setInt(3, persoana.getVarsta());
            ps.executeUpdate();
        }
        catch (SQLException e)
        {
            logger.error(e.getMessage());
            return false;
        }
        logger.traceExit();
        return true;
    }


    @Override
    public boolean delete(Long idParticipant)
    {
        logger.traceEntry();
        String sql="delete from persoane where id=?";
        try (Connection connection = DriverManager.getConnection(props.getProperty("jdbc.url"),props.getProperty("jdbc.username"),props.getProperty("jdbc.password"));
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1,idParticipant);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return false;
        }
        logger.traceExit();
        return true;
    }


    @Override
    public boolean update(Persoana persoana)
    {
        logger.traceEntry();
        String sql="update persoane set nume=?,varsta=? where id=?";
        try (Connection connection =DriverManager.getConnection(props.getProperty("jdbc.url"),props.getProperty("jdbc.username"),props.getProperty("jdbc.password"));
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, persoana.getNume());
            ps.setInt(2, persoana.getVarsta());
            ps.setLong(3, persoana.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return false;
        }
        logger.traceExit();
        return true;
    }


    public Persoana findOneByNumeAndVarsta(String nume,int varsta)
    {
        logger.traceEntry();
        String sql="SELECT * from persoane where nume=? and varsta=?";
        try (Connection connection = DriverManager.getConnection(props.getProperty("jdbc.url"),props.getProperty("jdbc.username"),props.getProperty("jdbc.password"));
             PreparedStatement ps=connection.prepareStatement(sql))
        {
            ps.setString(1,nume);
            ps.setInt(2,varsta);
            ResultSet resultSet=ps.executeQuery();
            if(resultSet.next())
            {
                Long id=resultSet.getLong("id");
                Persoana p=new Persoana(nume,varsta);
                p.setId(id);
                logger.traceExit();
                return p;
            }
            else
                return null;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return null;
        }
    }


    public Long getNewId()
    {
        logger.traceEntry();
        try (Connection connection = DriverManager.getConnection(props.getProperty("jdbc.url"),props.getProperty("jdbc.username"),props.getProperty("jdbc.password"));
             PreparedStatement ps= connection.prepareStatement("SELECT max(id) from persoane");
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
