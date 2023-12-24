package org.example.controllers;

import org.example.hotel.dao.ReservaDao;
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
import java.beans.PropertyChangeEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;



public class RegistroReservasController {
    private RegistroReservas view;
    private static final int COSTO_DIARIO = 600;

    public RegistroReservasController(RegistroReservas view) {
        this.view = view;
        initializeListeners();
    }

    private void initializeListeners() {

        view.txtFechaEntrada.addPropertyChangeListener(evt -> validarFechaEntrada(evt));

        view.txtFechaSalida.addPropertyChangeListener(evt -> validarFechaSalida(evt));

        //Boton Siguiente
        view.btnsiguiente.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                procesarReserva();
            }
        });


        //Boton atras
        view.btnAtras.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                MenuUsuario usuario = new MenuUsuario();
                usuario.setVisible(true);
                view.dispose(); //
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                view.btnAtras.setBackground(new Color(12, 138, 199));
                view.labelAtras.setForeground(Color.white);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                view.btnAtras.setBackground(Color.white);
                view.labelAtras.setForeground(Color.black);
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

    }

    private long calcularCantidadDias(Date fechaEntrada, Date fechaSalida) {
        if (fechaEntrada != null && fechaSalida != null) {
            LocalDate localDateEntrada = fechaEntrada.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate localDateSalida = fechaSalida.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            return ChronoUnit.DAYS.between(localDateEntrada, localDateSalida);
        }
        return 0;
    }

    private long guardarReserva(){
        EntityManager em = JPAUtils.getEntityManager();
        em.getTransaction().begin();
        try{
            Reserva reserva = reservaDesdeVista();

            ReservaDao reservaDao = new ReservaDao(em);
            reservaDao.guardar(reserva);

            em.getTransaction().commit();
            em.close();

            return reserva.getId();
        }catch(Exception e){
            em.getTransaction().rollback();
            em.close();
            e.printStackTrace();
            return 0;
        }
    }

    private Reserva reservaDesdeVista(){
        Reserva reserva=new Reserva();
        reserva.setFechaEntrada(view.txtFechaEntrada.getDate());
        reserva.setFechaSalida(view.txtFechaSalida.getDate());
        reserva.setValor(new BigDecimal(view.txtValor.getText()));
        reserva.setFormaPago((String)view.txtFormaPago.getSelectedItem());
        return reserva;
    }

    private boolean camposDeFechaNoNulos() {
        return RegistroReservas.txtFechaEntrada.getDate() != null && RegistroReservas.txtFechaSalida.getDate() != null;
    }

    // Función para mostrar mensajes de error
    private void mostrarMensajeError(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje);
    }
    private boolean validarFechas() {
        if (fechaEntradaEsHoyOEnElFuturo() && fechaSalidaEsUnDiaDespues()) {
            return true;
        } else {
            mostrarMensajeError("Las fechas no son válidas.");
            return false;
        }
    }

    private boolean fechaEntradaEsHoyOEnElFuturo() {
        Date fechaEntrada = RegistroReservas.txtFechaEntrada.getDate();
        Date hoy = new Date();
        return fechaEntrada != null && !fechaEntrada.before(hoy);
    }

    private boolean fechaSalidaEsUnDiaDespues() {
        Date fechaEntrada = RegistroReservas.txtFechaEntrada.getDate();
        Date fechaSalida = RegistroReservas.txtFechaSalida.getDate();
        return fechaEntrada != null && fechaSalida != null && fechaSalida.after(fechaEntrada);
    }

    private void procesarReserva() {
        if (validarReserva()) {
            long idReserva = guardarReserva();
            if (idReserva != 0) {
                RegistroHuesped registro = new RegistroHuesped(idReserva);
                registro.setVisible(true);
            }
        }else{
            mostrarMensajeError("Favor de llenar los campos");
        }
    }

    private boolean validarReserva() {
        return camposDeFechaNoNulos() && validarFechas();
    }

    private void validarFechaEntrada(PropertyChangeEvent evt) {
        if ("date".equals(evt.getPropertyName())) {
            Date selectedDate = view.txtFechaEntrada.getDate();
            Date today = new Date();
            if (selectedDate != null && selectedDate.before(today)) {
                mostrarMensajeError("Fecha Invalida para registrar una reserva");
                view.txtFechaEntrada.setDate(null);
            }
        }
    }

    private void validarFechaSalida(PropertyChangeEvent evt) {
        if ("date".equals(evt.getPropertyName())) {
            Date fechaEntrada = view.txtFechaEntrada.getDate();
            Date fechaSalida = view.txtFechaSalida.getDate();

            if (fechaSalida != null && (fechaEntrada == null || fechaSalida.compareTo(fechaEntrada) <= 0)) {
                mostrarMensajeError("Fecha de salida debe ser posterior a la fecha de entrada");
                view.txtFechaSalida.setDate(null);
            } else {
                fechaEntrada = Date.from(fechaEntrada.toInstant().truncatedTo(ChronoUnit.DAYS));
                long diasHospedados = calcularCantidadDias(fechaEntrada, fechaSalida);
                view.txtValor.setText(String.valueOf(diasHospedados * COSTO_DIARIO));
            }
        }
    }
}