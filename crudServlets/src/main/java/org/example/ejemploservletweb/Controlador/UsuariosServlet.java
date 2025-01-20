package org.example.ejemploservletweb.Controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.ejemploservletweb.Modelo.DAOUsuario;
import org.example.ejemploservletweb.Modelo.Usuario;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "usuariosServlet", value = "/usuarios-servlet")
public class UsuariosServlet extends HttpServlet {

    private DAOUsuario daoUsuario;

    public void init() {
        daoUsuario = new DAOUsuario();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter impresora = response.getWriter();
        ObjectMapper conversorJson = new ObjectMapper();

        String action = request.getParameter("action");
        String jsonResponse;

        switch (action) {
            case "add":
                String dni = request.getParameter("dni");
                String nombre = request.getParameter("nombre");
                String email = request.getParameter("email");
                String password = request.getParameter("password");
                String tipo = request.getParameter("tipo");

                Usuario nuevoUsuario = new Usuario(dni, nombre, password, tipo);
                nuevoUsuario.setEmail(email);

                Usuario usuarioAgregado = daoUsuario.addUsuario(nuevoUsuario);

                if (usuarioAgregado != null) {
                    jsonResponse = conversorJson.writeValueAsString(usuarioAgregado);
                } else {
                    jsonResponse = conversorJson.writeValueAsString("Error al agregar usuario");
                }
                break;

            case "update":
                Integer idToUpdate = Integer.parseInt(request.getParameter("id"));
                String updatedDni = request.getParameter("dni");
                String updatedNombre = request.getParameter("nombre");
                String updatedEmail = request.getParameter("email");
                String updatedPassword = request.getParameter("password");
                String updatedTipo = request.getParameter("tipo");

                Usuario usuarioToUpdate = daoUsuario.getUsuarioById(idToUpdate);
                if (usuarioToUpdate != null) {
                    usuarioToUpdate.setDni(updatedDni);
                    usuarioToUpdate.setNombre(updatedNombre);
                    usuarioToUpdate.setEmail(updatedEmail);
                    usuarioToUpdate.setPassword(updatedPassword);
                    usuarioToUpdate.setTipo(updatedTipo);

                    Usuario updatedUsuario = daoUsuario.updateUsuario(usuarioToUpdate);
                    jsonResponse = conversorJson.writeValueAsString(
                            updatedUsuario != null ? updatedUsuario : "Error al actualizar usuario"
                    );
                } else {
                    jsonResponse = conversorJson.writeValueAsString("Usuario no encontrado");
                }
                break;

            case "delete":
                Integer idToDelete = Integer.parseInt(request.getParameter("id"));
                Usuario usuarioToDelete = daoUsuario.getUsuarioById(idToDelete);

                if (usuarioToDelete != null) {
                    boolean deleteResult = daoUsuario.deleteUsuario(usuarioToDelete);
                    jsonResponse = conversorJson.writeValueAsString(
                            deleteResult ? "Usuario eliminado correctamente" : "Error al eliminar usuario"
                    );
                } else {
                    jsonResponse = conversorJson.writeValueAsString("Usuario no encontrado");
                }
                break;

            case "select":
                Integer idToSelect = Integer.parseInt(request.getParameter("id"));
                Usuario usuario = daoUsuario.getUsuarioById(idToSelect);
                jsonResponse = conversorJson.writeValueAsString(usuario != null ? usuario : "Usuario no encontrado");
                break;

            default:
                jsonResponse = conversorJson.writeValueAsString("Acción no válida");
                break;
        }

        impresora.println(jsonResponse);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter impresora = response.getWriter();
        ObjectMapper conversorJson = new ObjectMapper();

        List<Usuario> listaUsuarios = daoUsuario.getAllUsuarios();
        String jsonResponse = conversorJson.writeValueAsString(listaUsuarios);
        impresora.println(jsonResponse);
    }

    public void destroy() {
    }
}