package org.example.ejemploservletweb.Controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.ejemploservletweb.Modelo.Usuario;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "loginJson", value = "/loginJson")
public class loginUsuarioJson extends HttpServlet {
    private EntityManagerFactory emf;

    @Override
    public void init() {
        emf = Persistence.createEntityManagerFactory("unidad-biblioteca");
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        ObjectMapper conversor = new ObjectMapper();

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        EntityManager em = emf.createEntityManager();

        try {
            Usuario usuario = em.createQuery("SELECT u FROM Usuario u WHERE u.email = :email", Usuario.class)
                    .setParameter("email", email)
                    .getSingleResult();

            if (usuario != null && usuario.getPassword().equals(password)) {
                usuario.setPassword(null);
                out.print(conversor.writeValueAsString(usuario));
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"error\": \"Credenciales inválidas\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print("{\"error\": \"Usuario no encontrado\"}");
        } finally {
            em.close();
        }
    }

    @Override
    public void destroy() {
    }
}
