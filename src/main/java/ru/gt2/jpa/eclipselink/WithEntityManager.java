package ru.gt2.jpa.eclipselink;

import javax.persistence.EntityManager;

public interface WithEntityManager {
    void run(EntityManager entityManager);
}
