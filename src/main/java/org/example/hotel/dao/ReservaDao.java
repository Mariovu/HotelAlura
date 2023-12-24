package org.example.hotel.dao;

import org.example.hotel.modelo.Reserva;

import javax.persistence.EntityManager;
import java.util.List;

import org.example.hotel.utils.JPAUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class ReservaDao {
    private EntityManager em;
    public ReservaDao (EntityManager em){
        this.em=em;
    }
    public void guardar(Reserva reserva){
        this.em.persist(reserva);
    }

    public List<Reserva> getAllReservas() {
        List<Reserva> reservas = null;
        try {
            // Consulta JPA para seleccionar todas las reservas
            String jpql = "SELECT r FROM Reserva r";
            reservas = this.em.createQuery(jpql, Reserva.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reservas;
    }

    public Reserva obtenerPorId(Long id) {
        return em.find(Reserva.class, id);
    }

    public void actualizar(Reserva reserva) {
        EntityManager em = JPAUtils.getEntityManager();
        em.getTransaction().begin();
        try {
            // Obtenemos la reserva actual de la base de datos por su id
            Reserva reservaPersistente = em.find(Reserva.class, reserva.getId());

            // Actualizamos los campos de la reserva persistente con los de la nueva reserva
            reservaPersistente.setFechaEntrada(reserva.getFechaEntrada());
            reservaPersistente.setFechaSalida(reserva.getFechaSalida());
            reservaPersistente.setValor(reserva.getValor());
            reservaPersistente.setFormaPago(reserva.getFormaPago());

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
            Reserva reserva = em.find(Reserva.class, id);

            if (reserva != null) {
                em.remove(reserva);
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

}
