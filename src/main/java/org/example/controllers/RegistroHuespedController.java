package org.example.controllers;

import org.example.hotel.dao.HuespedDao;
import org.example.hotel.dao.ReservaDao;
import org.example.hotel.modelo.Huesped;
import org.example.hotel.modelo.Reserva;
import org.example.hotel.utils.JPAUtils;
import org.example.views.MenuPrincipal;
import org.example.views.MenuUsuario;
import org.example.views.RegistroHuesped;
import org.example.views.RegistroReservas;

import javax.persistence.EntityManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RegistroHuespedController {
    private RegistroHuesped view;

    private long idreserva;

    public RegistroHuespedController(RegistroHuesped view) {
        this.view = view;
        this.idreserva=view.idreserva;
        initializeListeners();
    }

    private void initializeListeners() {

        view.btnAtras.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                RegistroReservas reservas = new RegistroReservas();
                reservas.setVisible(true);
                view.dispose();
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                view.btnAtras.setBackground(Color.white);
                view.labelAtras.setForeground(Color.black);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                view.btnAtras.setBackground(new Color(12, 138, 199));
                view.labelAtras.setForeground(Color.white);
            }
        });

        //Boton Cerrar
        view.btnexit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                MenuPrincipal principal = new MenuPrincipal();
                principal.setVisible(true);
                view.dispose();
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                view.btnexit.setBackground(Color.red);
                view.labelExit.setForeground(Color.white);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                view.btnexit.setBackground(new Color(12, 138, 199));
                view.labelExit.setForeground(Color.white);
            }
        });

        view.btnguardar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (validarCampos()) {
                    guardarHuesped();
                    MenuUsuario principal = new MenuUsuario();
                    principal.setVisible(true);
                    view.dispose();
                } else {
                    JOptionPane.showMessageDialog(view, "Por favor, complete todos los campos correctamente.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


    }

    private void guardarHuesped() {
        EntityManager em = JPAUtils.getEntityManager();
        em.getTransaction().begin();

        try {
            HuespedDao huespedDao = new HuespedDao(em);
            ReservaDao reservaDao = new ReservaDao(em);

            Huesped huesped = crearHuespedDesdeVista();

            Huesped huespedExistente = huespedDao.obtenerPorTelefono(huesped.getTelefono());

            if (huespedExistente != null) {
                Reserva reservaPersistente = reservaDao.obtenerPorId(idreserva);
                reservaPersistente.setHuesped(huespedExistente);

                List<Reserva> reservas = huespedExistente.getReservas();
                reservas.add(reservaPersistente);
                huespedExistente.setReservas(reservas);

                huespedDao.actualizar(huespedExistente);
            } else {
                huespedDao.guardar(huesped);

                Reserva reservaPersistente = reservaDao.obtenerPorId(idreserva);
                reservaPersistente.setHuesped(huesped);

                reservaDao.actualizar(reservaPersistente);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    private Huesped crearHuespedDesdeVista(){
        Huesped huesped = new Huesped();
        huesped.setNombre(view.txtNombre.getText());
        huesped.setApellido(view.txtApellido.getText());
        huesped.setFechaNacimiento(view.txtFechaN.getDate());
        huesped.setNacionalidad((String) view.txtNacionalidad.getSelectedItem());
        huesped.setTelefono(view.txtTelefono.getText());
        return huesped;
    }

    private boolean validarCampos() {
        if (view.txtNombre.getText().isEmpty() || view.txtApellido.getText().isEmpty() || view.txtFechaN.getDate() == null || view.txtTelefono.getText().isEmpty()) {
            return false;
        }
        if (!validarEdad(view.txtFechaN.getDate())) {
            JOptionPane.showMessageDialog(view, "La fecha de nacimiento debe ser mayor de 18 años.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean validarEdad(Date fechaNacimiento) {
        Calendar calNacimiento = Calendar.getInstance();
        calNacimiento.setTime(fechaNacimiento);
        Calendar calHoy = Calendar.getInstance();
        int edad = calHoy.get(Calendar.YEAR) - calNacimiento.get(Calendar.YEAR);
        if (calHoy.get(Calendar.DAY_OF_YEAR) < calNacimiento.get(Calendar.DAY_OF_YEAR)) {
            edad--;
        }
        return edad >= 18;
    }

}
