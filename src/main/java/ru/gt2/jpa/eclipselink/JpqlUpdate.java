package ru.gt2.jpa.eclipselink;

import ru.gt2.jpa.eclipselink.entity.Field;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JpqlUpdate {
    private static final String CREATE_SQL = "CREATE TABLE FIELD (" +
            "  ID INT PRIMARY KEY NOT NULL," +
            "  POSITION INT," +
            "  CONSTRAINT UK_FIELD_POSITION UNIQUE (POSITION)" +
            ")";
    private final EntityManagerFactory entityManagerFactory;

    public JpqlUpdate(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public static void main(String[] args) {
        createTable();
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("eclipselink-update-jpql");
        try {
            JpqlUpdate jpqlUpdate = new JpqlUpdate(entityManagerFactory);
            jpqlUpdate.run();
        } finally {
            entityManagerFactory.close();
        }
    }

    private void run() {
        withinEntityManager(new WithEntityManager() {
            @Override
            public void run(EntityManager entityManager) {
                createAndPersistEntity(1, entityManager);
                createAndPersistEntity(2, entityManager);
            }
        });

        // Same methods with different results!
        runUpdateAndPrint();
        runUpdateAndPrint();
    }

    private static void createTable() {
        try (Connection connection =
                     DriverManager.getConnection("jdbc:derby:memory:eclipselink-update-jpql;create=true");
             CallableStatement callableStatement = connection.prepareCall(CREATE_SQL)) {
                callableStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void runUpdateAndPrint() {
        withinEntityManager(new WithEntityManager() {
            @Override
            public void run(EntityManager entityManager) {
                entityManager.createNamedQuery(Field.UNSET_POSITION_ALL).executeUpdate();

                // The following line also a workaround for this bug!
                // entityManager.getEntityManagerFactory().getCache().evict(Field.class, 2);
                Field field = entityManager.find(Field.class, 2);
                // And this line too
//                entityManager.refresh(field);

                field.setPosition(2);
            }
        });

        withinEntityManager(new WithEntityManager() {
            @Override
            public void run(EntityManager entityManager) {
                Field field = entityManager.find(Field.class, 2);
                Integer position = field.getPosition();
                String message;
                if (Integer.valueOf(2).equals(position)) {
                    message = "as expected.";
                } else {
                    message = "expected 2!";
                }
                System.out.println("Field position: " + position + ", " + message);
            }
        });
    }

    private static void createAndPersistEntity(int id, EntityManager entityManager) {
        Field field = new Field();
        field.setId(id);
        entityManager.persist(field);
        entityManager.flush();
    }

    private void withinEntityManager(WithEntityManager withEntityManager) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            withEntityManager.run(entityManager);
            transaction.commit();
        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            entityManager.close();
        }
    }
}
