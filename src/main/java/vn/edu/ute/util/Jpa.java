package vn.edu.ute.util;

import jakarta.persistence.Persistence;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public final class Jpa {
    private static final EntityManagerFactory EMF =
            Persistence.createEntityManagerFactory("MISCenterPU");

    private Jpa() {}

    public static EntityManager em(){
        return EMF.createEntityManager();
    }

    public static void shutdown(){
        EMF.close();
    }

}
