package org.example.ejemploservletweb.Modelo;

import jakarta.persistence.*;

import java.util.List;

public class DAOGenerico<T, ID> {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("unidad-biblioteca");
    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = em.getTransaction();
    Class<T> clase;
    Class<ID> claseID;

    public DAOGenerico(Class<T> clase, Class<ID> claseID){
        this.clase=clase;
        this.claseID=claseID;
    }

    //INSERT
    public boolean add(T objeto) {
        try {
            tx.begin();
            em.persist(objeto);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }

    //SELECT WHERE ID
    public T getById(ID id){
        return em.find(clase, id);
    }

    //SELECT *
    public List<T> getAll(){
        return em.createQuery("SELECT c from "+ clase.getName()+" c").getResultList();
    }

    //UPDATE
    public T update(T objeto) {
        try {
            tx.begin();
            if (!em.contains(objeto)) {
                objeto = em.merge(objeto);
            }
            tx.commit();
            return objeto;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }
    //DELETE WHERE objeto.id
    public boolean deleteUsuario(T objeto) {
        try {
            tx.begin();
            em.remove(em.contains(objeto) ? objeto : em.merge(objeto));
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }
    public List<Prestamo> getPrestamosActivosPorUsuario(int usuarioId) {
        return em.createQuery("SELECT p FROM Prestamo p WHERE p.usuario.id = :usuarioId AND p.fechaDevolucion IS NULL", Prestamo.class)
                .setParameter("usuarioId", usuarioId)
                .getResultList();
    }

    @Override
    public String toString() {
        return "DAOGenerico{" +
                "clase=" + clase.getSimpleName() +
                "clase=" + clase.getName() +
                ", claseID=" + claseID +
                '}';
    }
}
