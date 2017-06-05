package se.munhunger.oauth.util.database.jpa;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * Created by Marcus Münger on 2017-04-27.
 */
public class Database
{
    private static SessionFactory sessionFactory;

    public static void resetSessions()
    {
        sessionFactory = null;
    }

    private static void init()
    {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure() // configures settings from hibernate.cfg.xml
                                                                                     .build();
        sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            StandardServiceRegistryBuilder.destroy(registry);
        }));
    }

    public static void runScriptFile(String scriptFile)
    {
        try
        {
            scriptFile = Database.class.getClassLoader().getResource(scriptFile).getPath();
            scriptFile = new File(scriptFile).getCanonicalPath();
            String scriptString = new String(Files.readAllBytes(Paths.get(scriptFile)), Charset.forName("UTF-8"));
            if (sessionFactory == null)
                init();
            try (Session session = sessionFactory.openSession())
            {
                session.beginTransaction();
                for (String query : scriptString.trim().split(";"))
                {
                    Query q = session.createNativeQuery(query);
                    q.executeUpdate();
                }
                session.getTransaction().commit();
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void saveObjects(List<Object> objects) throws Exception
    {
        if (sessionFactory == null)
            init();
        try (Session session = sessionFactory.openSession())
        {
            session.beginTransaction();
            objects.forEach(o -> session.save(o));
            session.getTransaction().commit();
        }
    }

    public static void saveObject(Object obj) throws Exception
    {
        if (sessionFactory == null)
            init();
        try (Session session = sessionFactory.openSession())
        {
            session.beginTransaction();
            session.save(obj);
            session.getTransaction().commit();
        }
    }

    public static List getObjects(String hibernateQuery) throws Exception
    {
        if (sessionFactory == null)
            init();
        try (Session session = sessionFactory.openSession())
        {
            Query query = session.createQuery(hibernateQuery);
            return query.getResultList();
        }
    }

    public static List getObjects(String hibernateQuery, Map<String, Object> parameters) throws Exception
    {
        if (sessionFactory == null)
            init();
        try (Session session = sessionFactory.openSession())
        {
            Query query = session.createQuery(hibernateQuery);
            for (String key : parameters.keySet())
                query.setParameter(key, parameters.get(key));
            return query.getResultList();
        } catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    public static void deleteObjects(Object o) throws Exception
    {
        if (sessionFactory == null)
            init();
        try (Session session = sessionFactory.openSession())
        {
            session.beginTransaction();
            session.delete(o);
            session.getTransaction().commit();
        }
    }
}
