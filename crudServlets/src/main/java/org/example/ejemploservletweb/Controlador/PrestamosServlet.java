package org.example.ejemploservletweb.Controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.ejemploservletweb.Modelo.DAOGenerico;
import org.example.ejemploservletweb.Modelo.Ejemplar;
import org.example.ejemploservletweb.Modelo.Prestamo;
import org.example.ejemploservletweb.Modelo.Usuario;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

@WebServlet(name = "prestamosServlet", value = "/prestamos-servlet")
public class PrestamosServlet extends HttpServlet {

    private DAOGenerico<Prestamo, Integer> daoPrestamo;
    private DAOGenerico<Usuario, Integer> daoUsuario;
    private DAOGenerico<Ejemplar, Integer> daoEjemplar;

    @Override
    public void init() {
        daoPrestamo = new DAOGenerico<>(Prestamo.class, Integer.class);
        daoUsuario = new DAOGenerico<>(Usuario.class, Integer.class);
        daoEjemplar = new DAOGenerico<>(Ejemplar.class, Integer.class);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter impresora = response.getWriter();
        ObjectMapper conversorJson = new ObjectMapper();
        conversorJson.registerModule(new JavaTimeModule());

        String action = request.getParameter("action");
        String json_response = "";

        switch (action) {
            case "add":
                String usuarioIdStr = request.getParameter("usuarioId");
                String ejemplarIdStr = request.getParameter("ejemplarId");
                String fechaInicioStr = request.getParameter("fechaInicio");

                LocalDate fechaInicio = (fechaInicioStr == null || fechaInicioStr.isEmpty())
                        ? LocalDate.now()
                        : LocalDate.parse(fechaInicioStr);

                Usuario usuario = daoUsuario.getById(Integer.parseInt(usuarioIdStr));
                Ejemplar ejemplar = daoEjemplar.getById(Integer.parseInt(ejemplarIdStr));

                if (usuario == null || ejemplar == null) {
                    json_response = conversorJson.writeValueAsString("Error: Usuario o ejemplar no encontrados");
                } else if (usuario.getPenalizacionHasta() != null && usuario.getPenalizacionHasta().isAfter(LocalDate.now())) {
                    json_response = conversorJson.writeValueAsString("Error: Usuario con penalización activa hasta " + usuario.getPenalizacionHasta());
                } else {
                    ejemplar.setEstado("Prestado");

                    Prestamo prestamoToAdd = new Prestamo(usuario, ejemplar, fechaInicio);
                    boolean addResult = daoPrestamo.add(prestamoToAdd);

                    if (addResult) {
                        daoEjemplar.update(ejemplar);
                        Prestamo prestamoAdded = daoPrestamo.getById(prestamoToAdd.getId());
                        json_response = conversorJson.writeValueAsString(prestamoAdded);
                    } else {
                        json_response = conversorJson.writeValueAsString("Error al agregar préstamo");
                    }
                }
                break;

            case "return":
                String prestamoIdStr = request.getParameter("id");
                Prestamo prestamoToReturn = daoPrestamo.getById(Integer.parseInt(prestamoIdStr));

                if (prestamoToReturn == null) {
                    json_response = conversorJson.writeValueAsString("Error: Préstamo no encontrado");
                } else {
                    LocalDate fechaLimite = prestamoToReturn.getFechaInicio().plusDays(15); // Calcular fecha límite
                    prestamoToReturn.setFechaDevolucion(LocalDate.now());
                    daoPrestamo.update(prestamoToReturn);

                    prestamoToReturn.getEjemplar().setEstado("Disponible");
                    daoEjemplar.update(prestamoToReturn.getEjemplar());

                    Usuario usuarioDePrestamo = prestamoToReturn.getUsuario();
                    if (prestamoToReturn.getFechaDevolucion().isAfter(fechaLimite)) {
                        long diasDeRetraso = java.time.temporal.ChronoUnit.DAYS.between(fechaLimite, prestamoToReturn.getFechaDevolucion());
                        int penalizaciones = (int) (diasDeRetraso / 15);
                        usuarioDePrestamo.setPenalizacionHasta(LocalDate.now().plusDays(penalizaciones * 15));
                        daoUsuario.update(usuarioDePrestamo);
                    }

                    json_response = conversorJson.writeValueAsString("Préstamo devuelto correctamente");
                }
                break;

            case "update":
                String prestamoIdToUpdateStr = request.getParameter("id");
                String fechaDevolucionStr = request.getParameter("fechaDevolucion");

                Prestamo prestamoToUpdate = daoPrestamo.getById(Integer.parseInt(prestamoIdToUpdateStr));
                if (prestamoToUpdate != null) {
                    LocalDate nuevaFechaDevolucion = LocalDate.parse(fechaDevolucionStr);
                    prestamoToUpdate.setFechaDevolucion(nuevaFechaDevolucion);

                    Prestamo updatedPrestamo = daoPrestamo.update(prestamoToUpdate);
                    json_response = conversorJson.writeValueAsString(
                            updatedPrestamo != null ? updatedPrestamo : "Error al actualizar préstamo"
                    );
                } else {
                    json_response = conversorJson.writeValueAsString("Error: Préstamo no encontrado");
                }
                break;

            case "delete":
                String idToDeleteStr = request.getParameter("id");
                Prestamo prestamoToDelete = daoPrestamo.getById(Integer.parseInt(idToDeleteStr));

                if (prestamoToDelete != null) {
                    boolean deleteResult = daoPrestamo.deleteUsuario(prestamoToDelete);
                    json_response = conversorJson.writeValueAsString(
                            deleteResult ? "Préstamo eliminado correctamente" : "Error al eliminar préstamo"
                    );
                } else {
                    json_response = conversorJson.writeValueAsString("Error: Préstamo no encontrado");
                }
                break;

            case "select":
                String idToSelectStr = request.getParameter("id");
                Prestamo prestamo = daoPrestamo.getById(Integer.parseInt(idToSelectStr));
                json_response = conversorJson.writeValueAsString(prestamo != null ? prestamo : "Préstamo no encontrado");
                break;

            default:
                json_response = conversorJson.writeValueAsString("Acción no válida");
                break;
        }

        impresora.println(json_response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        PrintWriter impresora = response.getWriter();
        ObjectMapper conversorJson = new ObjectMapper();
        conversorJson.registerModule(new JavaTimeModule());

        List<Prestamo> listaPrestamos = daoPrestamo.getAll();
        String json_response = conversorJson.writeValueAsString(listaPrestamos);

        impresora.println(json_response);
    }

    @Override
    public void destroy() {
    }
}