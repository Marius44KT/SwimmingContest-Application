package com.example.persistence.Database;


import com.example.model.Angajat;
import com.example.persistence.AngajatiIRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Component
public class AngajatiDatabaseRepository implements AngajatiIRepository<Long, Angajat> {


    private static final Logger logger= LogManager.getLogger();
    private final Properties props;


    public AngajatiDatabaseRepository(Properties props)
    {
        logger.info("Initializing AngajatiRepository with properties: {} ",props);
        this.props=props;
    }


    @Override
    public Angajat findOne(Long idAngajat)
    {
        logger.traceEntry();
        String sql="SELECT * from angajati where id=?";
        try (Connection connection = DriverManager.getConnection(props.getProperty("jdbc.url"),props.getProperty("jdbc.username"),props.getProperty("jdbc.password"));
             PreparedStatement ps=connection.prepareStatement(sql))
        {
            ps.setLong(1,idAngajat);
            ResultSet resultSet=ps.executeQuery();
            if(resultSet.next())
            {
                String email=resultSet.getString("email");
                String parola=resultSet.getString("parola");
                Angajat p=new Angajat(email,parola);
                p.setId(idAngajat);
                logger.traceExit();
                return p;
            }
            else
                return null;
        } catch (SQLException e) {
            logger.error(e);
            return null;
        }
    }


    @Override
    public Map<Long, Angajat> findAll()
    {
        logger.traceEntry();
        Map<Long,Angajat> Angajati=new HashMap<>();
        String sql="SELECT * from angajati";
        try (Connection connection = DriverManager.getConnection(props.getProperty("jdbc.url"),props.getProperty("jdbc.username"),props.getProperty("jdbc.password"));
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery())
        {
            while (resultSet.next()) {
                Long AngajatId=resultSet.getLong("id");
                String email=resultSet.getString("email");
                String parola=resultSet.getString("parola");
                Angajat p=new Angajat(email,parola);
                p.setId(AngajatId);
                Angajati.put(AngajatId,p);
            }
        } catch (SQLException e) {
            logger.error(e);
            return null;
        }
        logger.traceExit(Angajati);
        return Angajati;
    }


    @Override
    public boolean save(Angajat Angajat)
    {
        logger.trace(Angajat);
        String sql="insert into angajati (id,email,parola) values (?,?,?)";
        try (Connection connection=DriverManager.getConnection(props.getProperty("jdbc.url"),props.getProperty("jdbc.username"),props.getProperty("jdbc.password"));
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setLong(1,Angajat.getId());
            ps.setString(2,Angajat.getEmail());
            ps.setString(3,Angajat.getParola());
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
    public boolean delete(Long idAngajat)
    {
        logger.traceEntry();
        String sql="delete from angajati where id=?";
        try (Connection connection = DriverManager.getConnection(props.getProperty("jdbc.url"),props.getProperty("jdbc.username"),props.getProperty("jdbc.password"));
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1,idAngajat);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error(e);
            return false;
        }
        logger.traceExit();
        return true;
    }


    @Override
    public boolean update(Angajat Angajat)
    {
        return true;
    }



    public Angajat findOneByEmailAndPassword(String email,String pass) {
        logger.traceEntry();
        logger.info("ok");
        String sql="SELECT * from angajati where email=? and parola=?";
        try (Connection connection = DriverManager.getConnection(props.getProperty("jdbc.url"),props.getProperty("jdbc.username"),props.getProperty("jdbc.password"));
             PreparedStatement ps=connection.prepareStatement(sql))
        {
            ps.setString(1,email);
            ps.setString(2,pass);
            ResultSet resultSet=ps.executeQuery();
            if(resultSet.next())
            {
                Long id=resultSet.getLong("id");
                Angajat a=new Angajat(email,pass);
                a.setId(id);
                return a;
            }
            else
                return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }



    public Angajat findOneByEmail(String email) {
        logger.traceEntry("a intrat aici");
        String sql="SELECT * from angajati where email=?";
        try (Connection connection = DriverManager.getConnection(props.getProperty("jdbc.url"),props.getProperty("jdbc.username"),props.getProperty("jdbc.password"));
             PreparedStatement ps=connection.prepareStatement(sql))
        {
            ps.setString(1,email);
            ResultSet resultSet=ps.executeQuery();
            if(resultSet.next())
            {
                Long id=resultSet.getLong("id");
                String password=resultSet.getString("parola");
                Angajat a=new Angajat(email,password);
                a.setId(id);
                return a;
            }
            else
                return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public Long getNewId()
    {
        try (Connection connection = DriverManager.getConnection(props.getProperty("jdbc.url"),props.getProperty("jdbc.username"),props.getProperty("jdbc.password"));
             PreparedStatement ps= connection.prepareStatement("SELECT max(id) from angajati");
             ResultSet resultSet = ps.executeQuery()) {
            if(resultSet.next()) {
                return resultSet.getLong(1)+1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



//    @Override
//    public Angajat findOne(Long idAngajat)
//    {
//        logger.traceEntry();
//        Configuration configuration = new Configuration();
//        configuration.configure("hibernate.cfg.xml");
//        try (SessionFactory sessionFactory = configuration.buildSessionFactory(); Session session = sessionFactory.openSession()) {
//            Transaction tx = null;
//            try {
//                tx = session.beginTransaction();
//                Angajat angajat = session.get(Angajat.class, idAngajat);
//                Hibernate.initialize(angajat);
//                tx.commit();
//                return angajat;
//            } catch (RuntimeException ex) {
//                if (tx != null) {
//                    logger.error(ex.getMessage());
//                    tx.rollback();
//                }
//                return null;
//            }
//        }
//    }
//
//
//    @Override
//    public Map<Long, Angajat> findAll()
//    {
//        logger.traceEntry();
//        Configuration configuration = new Configuration();
//        configuration.configure("hibernate.cfg.xml");
//        try (SessionFactory sessionFactory = configuration.buildSessionFactory(); Session session = sessionFactory.openSession()) {
//            Transaction tx = null;
//            try {
//                tx = session.beginTransaction();
//                List<Angajat> angajati = session.createQuery("from Angajat", Angajat.class).stream().toList();
//                angajati.forEach(Hibernate::initialize);
//                tx.commit();
//                Map<Long,Angajat> angajatiMap=new HashMap<>();
//                for(Angajat a:angajati)
//                    angajatiMap.put(a.getId(),a);
//                return angajatiMap;
//            } catch (RuntimeException ex) {
//                if (tx != null) {
//                    logger.error(ex.getMessage());
//                    tx.rollback();
//                }
//                return null;
//            }
//        }
//    }
//
//
//    @Override
//    public boolean save(Angajat angajat)
//    {
//        logger.trace(angajat);
//        Configuration configuration = new Configuration();
//        configuration.configure("hibernate.cfg.xml");
//        try (SessionFactory sessionFactory = configuration.buildSessionFactory(); Session session = sessionFactory.openSession()) {
//            Transaction tx = null;
//            try {
//                tx = session.beginTransaction();
//                session.save(angajat);
//                tx.commit();
//                return true;
//            } catch (RuntimeException ex) {
//                if (tx != null) {
//                    tx.rollback();
//                    return false;
//                }
//            }
//            return true;
//        }
//    }
//
//
//    @Override
//    public boolean delete(Long idAngajat)
//    {
//        logger.traceEntry();
//        Configuration configuration = new Configuration();
//        configuration.configure("hibernate.cfg.xml");
//        try (SessionFactory sessionFactory = configuration.buildSessionFactory(); Session session = sessionFactory.openSession()) {
//            Transaction tx = null;
//            try {
//                tx = session.beginTransaction();
//                Angajat angajat=(Angajat) session.createQuery("from Angajat where id = :idAngajat", Angajat.class)
//                        .setParameter("idAngajat", idAngajat)
//                        .setMaxResults(1)
//                        .uniqueResult();
//                session.delete(angajat);
//                tx.commit();
//                return true;
//            } catch (RuntimeException ex) {
//                if (tx != null) {
//                    logger.error(ex.getMessage());
//                    tx.rollback();
//                    return false;
//                }
//            }
//        }
//        return true;
//    }
//
//
//    @Override
//    public boolean update(Angajat Angajat)
//    {
//        return true;
//    }
//
//
//
//    public Angajat findOneByEmailAndPassword(String email,String password) {
//        Configuration configuration = new Configuration();
//        configuration.configure("hibernate.cfg.xml");
//        try (SessionFactory sessionFactory = configuration.buildSessionFactory(); Session session = sessionFactory.openSession()) {
//            Transaction tx = null;
//            try {
//                tx = session.beginTransaction();
//                Angajat angajat = session.createQuery("from Angajat where email = :emailParameter and parola = :passwordParameter", Angajat.class)
//                        .setParameter("emailParameter", email)
//                        .setParameter("passwordParameter", password)
//                        .setMaxResults(1)
//                        .uniqueResult();
//                Hibernate.initialize(angajat);
//                tx.commit();
//                return angajat;
//            } catch (RuntimeException ex) {
//                if (tx != null) {
//                    logger.error(ex.getMessage());
//                    tx.rollback();
//                }
//                return null;
//            }
//        }
//    }
//
//
//
//    public Angajat findOneByEmail(String email) {
//        Configuration configuration = new Configuration();
//        configuration.configure("hibernate.cfg.xml");
//        try (SessionFactory sessionFactory = configuration.buildSessionFactory(); Session session = sessionFactory.openSession()) {
//            Transaction tx = null;
//            try {
//                tx = session.beginTransaction();
//                Angajat angajat=session.createQuery("from Angajat where email = :emailParameter", Angajat.class)
//                        .setParameter("emailParameter", email)
//                        .setMaxResults(1)
//                        .uniqueResult();
//                Hibernate.initialize(angajat);
//                tx.commit();
//                return angajat;
//            } catch (RuntimeException ex) {
//                if (tx != null) {
//                    logger.error(ex.getMessage());
//                    tx.rollback();
//                }
//                return null;
//            }
//        }
//    }
}