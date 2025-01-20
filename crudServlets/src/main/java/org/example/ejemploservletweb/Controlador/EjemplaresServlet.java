package org.example.ejemploservletweb.Controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.ejemploservletweb.Modelo.DAOGenerico;
import org.example.ejemploservletweb.Modelo.Ejemplar;
import org.example.ejemploservletweb.Modelo.Libro;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "ejemplaresServlet", value = "/ejemplares-servlet")
public class EjemplaresServlet extends HttpServlet {

    private DAOGenerico<Ejemplar, Integer> daoEjemplar;
    private DAOGenerico<Libro, String> daoLibro;

    @Override
    public void init() {
        daoEjemplar = new DAOGenerico<>(Ejemplar.class, Integer.class);
        daoLibro = new DAOGenerico<>(Libro.class, String.class);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter impresora = response.getWriter();
        ObjectMapper conversorJson = new ObjectMapper();
        conversorJson.registerModule(new JavaTimeModule());

        String action = request.getParameter("action");
        String json_response;

        switch (action) {
            case "stock":
                String isbnStock = request.getParameter("isbn");

                if (isbnStock == null || isbnStock.isEmpty()) {
                    json_response = conversorJson.writeValueAsString("Error: ISBN no proporcionado");
                } else {
                    Libro libroStock = daoLibro.getById(isbnStock);
                    if (libroStock == null) {
                        json_response = conversorJson.writeValueAsString("Error: Libro no encontrado para el ISBN proporcionado");
                    } else {
                        long stockDisponible = daoEjemplar.getAll().stream()
                                .filter(ejemplar -> ejemplar.getLibro().getIsbn().equals(isbnStock) &&
                                        !"Prestado".equalsIgnoreCase(ejemplar.getEstado()))
                                .count();

                        json_response = conversorJson.writeValueAsString(
                                "Stock disponible para el libro con ISBN (incluyendo dañados)" + isbnStock + ": " + stockDisponible
                        );
                    }
                }
                break;

            case "add":
                String isbn = request.getParameter("isbn");
                String estado = request.getParameter("estado");

                Libro libro = daoLibro.getById(isbn);
                if (libro == null) {
                    json_response = conversorJson.writeValueAsString("Error: Libro no encontrado para el ISBN proporcionado");
                } else {
                    Ejemplar ejemplarToAdd = new Ejemplar(libro, estado != null ? estado : "Disponible");
                    boolean addResult = daoEjemplar.add(ejemplarToAdd);

                    if (addResult) {
                        Ejemplar ejemplarAdded = daoEjemplar.getById(ejemplarToAdd.getId());
                        json_response = conversorJson.writeValueAsString(ejemplarAdded);
                    } else {
                        json_response = conversorJson.writeValueAsString("Error al agregar ejemplar");
                    }
                }
                break;

            case "update":
                int ejemplarIdToUpdate = Integer.parseInt(request.getParameter("id"));
                String newEstado = request.getParameter("estado");

                Ejemplar ejemplarToUpdate = daoEjemplar.getById(ejemplarIdToUpdate);
                if (ejemplarToUpdate != null) {
                    ejemplarToUpdate.setEstado(newEstado);
                    Ejemplar updatedEjemplar = daoEjemplar.update(ejemplarToUpdate);
                    json_response = conversorJson.writeValueAsString(
                            updatedEjemplar != null ? updatedEjemplar : "Error al actualizar ejemplar"
                    );
                } else {
                    json_response = conversorJson.writeValueAsString("Error: Ejemplar no encontrado");
                }
                break;

            case "delete":
                int ejemplarIdToDelete = Integer.parseInt(request.getParameter("id"));

                Ejemplar ejemplarToDelete = daoEjemplar.getById(ejemplarIdToDelete);
                if (ejemplarToDelete != null) {
                    boolean deleteResult = daoEjemplar.deleteUsuario(ejemplarToDelete);
                    json_response = conversorJson.writeValueAsString(
                            deleteResult ? "Ejemplar eliminado correctamente" : "Error al eliminar ejemplar"
                    );
                } else {
                    json_response = conversorJson.writeValueAsString("Error: Ejemplar no encontrado");
                }
                break;

            case "select":
                int ejemplarIdToSelect = Integer.parseInt(request.getParameter("id"));

                Ejemplar ejemplar = daoEjemplar.getById(ejemplarIdToSelect);
                json_response = conversorJson.writeValueAsString(
                        ejemplar != null ? ejemplar : "Ejemplar no encontrado"
                );
                break;

            default:
                json_response = conversorJson.writeValueAsString("Acción no válida");
                break;
        }

        System.out.println("Respuesta JSON: " + json_response);
        impresora.println(json_response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter impresora = response.getWriter();
        ObjectMapper conversorJson = new ObjectMapper();
        conversorJson.registerModule(new JavaTimeModule());

        List<Ejemplar> listaEjemplares = daoEjemplar.getAll();
        System.out.println("En Java: " + listaEjemplares);

        String json_response = conversorJson.writeValueAsString(listaEjemplares);
        System.out.println("En Java JSON: " + json_response);
        impresora.println(json_response);
    }

    @Override
    public void destroy() {
    }
}