package org.example.hotel.dao;

import org.example.hotel.modelo.Huesped;
import org.example.hotel.modelo.Reserva;
import org.example.hotel.utils.JPAUtils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

public class HuespedDao {
    private EntityManager em;

    public HuespedDao(EntityManager em) {
        this.em = em;
    }

    public void guardar(Huesped huesped) {
        this.em.persist(huesped);
    }

    public List<Huesped> getAllHuespedes() {
        EntityManager em = JPAUtils.getEntityManager();
        List<Huesped> huespedes = null;
        try {
            String jpql = "SELECT h FROM Huesped h";
            huespedes = em.createQuery(jpql, Huesped.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return huespedes;
    }

    public Huesped obtenerPorId(Long id) {
        return em.find(Huesped.class, id);
    }

    public void actualizar(Huesped huesped) {
        EntityManager em = JPAUtils.getEntityManager();
        em.getTransaction().begin();
        try {
            // Obtenemos la reserva actual de la base de datos por su id
            Huesped huespedPersistence = em.find(Huesped.class, huesped.getId());

            // Actualizamos los campos de la reserva persistente con los de la nueva reserva
            huespedPersistence.setNombre(huesped.getNombre());
            huespedPersistence.setApellido(huesped.getApellido());
            huespedPersistence.setFechaNacimiento(huesped.getFechaNacimiento());
            huespedPersistence.setTelefono(huesped.getTelefono());

            // Confirmamos la transacción
            em.getTransaction().commit();
        } catch (Exception e) {
            // Manejo de excepciones
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public void eliminar(Long id) {
        EntityManager em = JPAUtils.getEntityManager();
        em.getTransaction().begin();
        try {
            Huesped huesped = em.find(Huesped.class, id);

            if (huesped != null) {
                em.remove(huesped);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
    public Huesped obtenerPorTelefono(String telefono) {
        Huesped huesped = null;
        try {
            String jpql = "SELECT h FROM Huesped h WHERE h.telefono = :telefono";
            huesped = this.em.createQuery(jpql, Huesped.class)
                    .setParameter("telefono", telefono)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return huesped;
    }
}