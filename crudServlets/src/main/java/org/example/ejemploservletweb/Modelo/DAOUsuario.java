package org.example.ejemploservletweb.Modelo;

import jakarta.persistence.*;

import java.util.List;

public class DAOUsuario {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("unidad-biblioteca");
    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = em.getTransaction();

    public DAOUsuario(){
    }

    //INSERT
    public Usuario addUsuario(Usuario usuario){
        tx.begin();
        em.persist(usuario);
        tx.commit();
        return usuario;
    }

    //SELECT WHERE ID
    public Usuario getUsuarioById(int id){
        return em.find(Usuario.class, id);
    }

    //SELECT WHERE DNI
    public Usuario getUsuarioByDni(String dni){
        Query consulta = em.createQuery("SELECT u from Usuario u WHERE u.dni=:dni");
        consulta.setParameter("dni",dni);
        return (Usuario) consulta.getSingleResult();
    }
    //SELECT *
    public List<Usuario> getAllUsuarios(){
        return em.createQuery("SELECT u FROM Usuario u").getResultList();
    }

    //UPDATE
    public Usuario updateUsuario(Usuario usuario){
        tx.begin();
        usuario = em.merge(usuario);
        tx.commit();
        return usuario;
    }
    //DELETE WHERE usuario.id
    public boolean deleteUsuario(Usuario usuario){
        tx.begin();
        em.remove(usuario);
        tx.commit();
        return true;
    }

}
