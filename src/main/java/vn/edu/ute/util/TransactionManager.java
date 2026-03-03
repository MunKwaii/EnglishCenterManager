package vn.edu.ute.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class TransactionManager {

    public <T> T runInTransaction(JpaWork<T> work) throws Exception{
        EntityManager em =Jpa.em();
        EntityTransaction tx = em.getTransaction();

        try{
            tx.begin();
            T result = work.execute(em);
            tx.commit();
            return result;
        }catch (Exception e){
            if (tx.isActive())
                tx.rollback();
            throw e;
        }finally {
            em.close();
        }

    }
}
