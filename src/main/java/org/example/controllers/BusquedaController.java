package org.example.controllers;

import org.example.hotel.dao.HuespedDao;
import org.example.hotel.dao.ReservaDao;
import org.example.hotel.modelo.Huesped;
import org.example.hotel.modelo.Reserva;
import org.example.hotel.utils.JPAUtils;
import org.example.views.Busqueda;
import org.example.views.MenuUsuario;

import javax.persistence.EntityManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class BusquedaController {
    private Busqueda busqueda;
    private EntityManager em=JPAUtils.getEntityManager();
    public BusquedaController(Busqueda busqueda) {
        this.busqueda=busqueda;

        busqueda.btnAtras.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                MenuUsuario usuario = new MenuUsuario();
                usuario.setVisible(true);
                busqueda.dispose();
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                busqueda.btnAtras.setBackground(new Color(12, 138, 199));
                busqueda.labelAtras.setForeground(Color.white);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                busqueda.btnAtras.setBackground(Color.white);
                busqueda.labelAtras.setForeground(Color.black);
            }
        });

        busqueda.btnexit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                MenuUsuario usuario = new MenuUsuario();
                usuario.setVisible(true);
                busqueda.dispose();
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                busqueda.btnexit.setBackground(Color.red);
                busqueda.labelExit.setForeground(Color.white);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                busqueda.btnexit.setBackground(Color.white);
                busqueda.labelExit.setForeground(Color.black);
            }
        });

        busqueda.panel.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {

                int tabIndex = busqueda.panel.getSelectedIndex();

                if (tabIndex == 0) {
                    cargarDatosTablaReserva();
                } else if (tabIndex == 1) {
                    cargarDatosTablaHuesped();
                }
            }
        });

        busqueda.btnEditar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int tabIndex = busqueda.panel.getSelectedIndex();

                if (tabIndex == 0) {
                    editarYActualizarReserva();
                } else if (tabIndex == 1) {
                    editarYActualizarHuesped();

                }
            }
        });

        busqueda.btnEliminar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int tabIndex = busqueda.panel.getSelectedIndex();

                if (tabIndex == 0) {
                    eliminarReserva();
                } else if (tabIndex == 1) {
                    eliminarHuesped();

                }
            }
        });
}

    private void cargarDatosTablaReserva() {
        DefaultTableModel modelo = (DefaultTableModel) busqueda.tbReservas.getModel();
        modelo.setRowCount(0);

        EntityManager em = JPAUtils.getEntityManager();
        ReservaDao reservaDao = new ReservaDao(em);
        List<Reserva> reservas = reservaDao.getAllReservas();

        for (Reserva reserva : reservas) {
            Object[] fila = new Object[]{
                    reserva.getId(),
                    reserva.getFechaEntrada(),
                    reserva.getFechaSalida(),
                    reserva.getValor(),
                    reserva.getFormaPago()
            };
            modelo.addRow(fila);
        }
    }
    private void cargarDatosTablaHuesped() {
        DefaultTableModel modelo = (DefaultTableModel) busqueda.tbHuespedes.getModel();
        modelo.setRowCount(0);

        EntityManager em = JPAUtils.getEntityManager();
        HuespedDao huespedDao = new HuespedDao(em);
        List<Huesped> huespedes = huespedDao.getAllHuespedes();

        for (Huesped huesped : huespedes) {
            List<Reserva> reservas = huesped.getReservas();
            long numreservas=reservas.size();
            Object[] fila = new Object[]{
                    huesped.getId(),
                    huesped.getNombre(),
                    huesped.getApellido(),
                    huesped.getFechaNacimiento(),
                    huesped.getNacionalidad(),
                    huesped.getTelefono(),
                    numreservas
            };
            modelo.addRow(fila);

        }
    }
    private void editarYActualizarReserva() {
        ReservaDao reservaDao=new ReservaDao(em);
        int selectedRow = busqueda.tbReservas.getSelectedRow();
        if (selectedRow != -1) {  // Si hay una fila seleccionada
            Long idReserva = (Long) busqueda.tbReservas.getValueAt(selectedRow, 0);
            Reserva reserva = reservaDao.obtenerPorId(idReserva);

            reserva.setFechaEntrada((Date) busqueda.tbReservas.getValueAt(selectedRow, 1));
            reserva.setFechaSalida((Date) busqueda.tbReservas.getValueAt(selectedRow, 2));
            BigDecimal valor = (BigDecimal) busqueda.tbReservas.getValueAt(selectedRow, 3);
            reserva.setValor(valor);
            reserva.setFormaPago((String) busqueda.tbReservas.getValueAt(selectedRow, 4));

            reservaDao.actualizar(reserva);
            em.refresh(reserva);
            cargarDatosTablaReserva();
        }
    }

    private void editarYActualizarHuesped() {
        HuespedDao huespedDao=new HuespedDao(em);
        int selectedRow = busqueda.tbHuespedes.getSelectedRow();
        if (selectedRow != -1) {
            Long idHuesped = (Long) busqueda.tbHuespedes.getValueAt(selectedRow, 0);
            Huesped huesped = huespedDao.obtenerPorId(idHuesped);

            // Resto del código para actualizar el huésped...
            huesped.setNombre((String)busqueda.tbHuespedes.getValueAt(selectedRow,1));
            huesped.setApellido((String)busqueda.tbHuespedes.getValueAt(selectedRow,2));
            huesped.setFechaNacimiento((Date)busqueda.tbHuespedes.getValueAt(selectedRow,3));
            huesped.setNacionalidad((String)busqueda.tbHuespedes.getValueAt(selectedRow,4));
            huesped.setTelefono((String)busqueda.tbHuespedes.getValueAt(selectedRow,5));

            huespedDao.actualizar(huesped);
            // Actualizar la tabla después de la edición
            cargarDatosTablaHuesped();
        }
    }

    private void eliminarReserva() {
        ReservaDao reservaDao = new ReservaDao(em);
        int selectedRow = busqueda.tbReservas.getSelectedRow();
        if (selectedRow != -1) {
            Long idReserva = (Long) busqueda.tbReservas.getValueAt(selectedRow, 0);
            reservaDao.eliminar(idReserva);
            cargarDatosTablaReserva();
        }
    }

    private void eliminarHuesped() {
        HuespedDao huespedDao = new HuespedDao(em);
        int selectedRow = busqueda.tbHuespedes.getSelectedRow();
        if (selectedRow != -1) {
            Long idHuesped = (Long) busqueda.tbHuespedes.getValueAt(selectedRow, 0);
            huespedDao.eliminar(idHuesped);
            cargarDatosTablaHuesped();
        }
    }
}
