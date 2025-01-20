package org.example.ejemploservletweb.Controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.ejemploservletweb.Modelo.DAOGenerico;
import org.example.ejemploservletweb.Modelo.Libro;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "librosServlet", value = "/libros-servlet")
public class LibrosServlet extends HttpServlet {

        DAOGenerico<Libro, String> daolibro;

    public void init(){
            daolibro = new DAOGenerico<>(Libro.class,String.class);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter impresora = response.getWriter();
        ObjectMapper conversorJson = new ObjectMapper();
        conversorJson.registerModule(new JavaTimeModule());

        String action = request.getParameter("action");
        String json_response = "";

        switch (action) {
            case "add":
                String isbn = request.getParameter("isbn");
                String titulo = request.getParameter("titulo");
                String autor = request.getParameter("autor");

                Libro libroToAdd = new Libro(isbn, titulo, autor);

                boolean addResult = daolibro.add(libroToAdd);

                json_response = conversorJson.writeValueAsString(
                        addResult ? "Libro agregado correctamente" : "Error al agregar libro"
                );
                break;

            case "update":
                String isbnToUpdate = request.getParameter("isbn");
                String newTitulo = request.getParameter("titulo");
                String newAutor = request.getParameter("autor");

                Libro libroToUpdate = new Libro(isbnToUpdate, newTitulo, newAutor);

                Libro updatedLibro = daolibro.update(libroToUpdate);
                json_response = conversorJson.writeValueAsString(
                        updatedLibro != null ? updatedLibro : "Error al actualizar libro"
                );
                break;

            case "delete":
                String isbnToDelete = request.getParameter("isbn");

                Libro libroToDelete = daolibro.getById(isbnToDelete);

                if (libroToDelete != null) {
                    boolean deleteResult = daolibro.deleteUsuario(libroToDelete);

                    json_response = conversorJson.writeValueAsString(
                            deleteResult ? "Libro eliminado correctamente" : "Error al eliminar libro"
                    );
                } else {
                    json_response = conversorJson.writeValueAsString("Libro no encontrado");
                }
                break;

            case "select":
                String isbnToSelect = request.getParameter("isbn");
                Libro libro = daolibro.getById(isbnToSelect);
                json_response = conversorJson.writeValueAsString(libro != null ? libro : "Libro no encontrado");
                break;

            default:
                json_response = conversorJson.writeValueAsString("Acción no válida");
                break;
        }

        System.out.println("Respuesta JSON: " + json_response);
        impresora.println(json_response);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

            PrintWriter impresora = response.getWriter();
            ObjectMapper conversorJson = new ObjectMapper();
            conversorJson.registerModule(new JavaTimeModule());

            List<Libro> listaLibros  = daolibro.getAll();
            System.out.println("En java" + listaLibros);

            String json_response = conversorJson.writeValueAsString(listaLibros);
            System.out.println("En java json" + json_response);
            impresora.println(json_response);

    }

    public void destroy(){
    }
}
