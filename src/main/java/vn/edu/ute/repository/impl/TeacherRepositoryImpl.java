package vn.edu.ute.repository.impl;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Teacher;
import vn.edu.ute.repository.TeacherRepository;
import vn.edu.ute.util.Jpa;
import vn.edu.ute.util.TransactionManager;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TeacherRepositoryImpl implements TeacherRepository {
    private final TransactionManager txManager;

    public TeacherRepositoryImpl() {
        this.txManager = new TransactionManager();
    }

    @Override
    public void save(Teacher teacher){
        try {
            txManager.runInTransaction(em -> {
                if (teacher.getTeacherId() == null)
                    em.persist(teacher); //Thêm mới (INSERT)
                else
                    em.merge(teacher); //Cập nhật (UPDATE)
                return null;
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Teacher> findById(Long id){
        try{
            return txManager.runInTransaction(em -> {
                //ùng em.find thay vì jpql dài
                Teacher teacher = em.find(Teacher.class, id);
                return Optional.ofNullable(teacher);
            });
        }catch (Exception e){
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<Teacher> findAll(){
       try{
           return txManager.runInTransaction(em ->{
               //Dùng JPQL để truy vấn tất cả teacher
               String jpql = "SELECT t FROM Teacher t WHERE t.status = 'Active'";
               return em.createQuery(jpql, Teacher.class).getResultList();
           });
       }catch (Exception e){
           e.printStackTrace();
           return Collections.emptyList();
       }
    }

    @Override
    public void deleteById(Long id){
        try{
            txManager.runInTransaction(em ->{
                Teacher teacher = em.find(Teacher.class, id);
                if (teacher != null){
                    em.remove(teacher);
                }
                return null;
            });
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
