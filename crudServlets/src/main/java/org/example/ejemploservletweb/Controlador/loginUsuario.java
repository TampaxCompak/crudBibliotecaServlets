package org.example.ejemploservletweb.Controlador;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

@WebServlet(name = "login", value = "/login")
public class loginUsuario extends HttpServlet {
    private String nickname;
    private String password;

    public void init() {
        nickname="";
        password="";
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>Recepción de datos de parametros</h1>");
/*¿Porqué esto no funciona??*/
//        while(request.getParameterNames().hasMoreElements()){
//            String parametro =request.getParameterNames().nextElement();
//            out.println("En respuesta a cliente: "+parametro);
//            System.out.println("En consola: "+parametro);
//        }
        Enumeration<String> listaParametros = request.getParameterNames();
        while(listaParametros.hasMoreElements()){
            String parametro =listaParametros.nextElement();
            out.println("En respuesta a cliente: "+parametro);
            System.out.println("En consola: "+parametro);
        }


        out.println("<h1>Recepción de datos de cabeceras</h1>");
        out.println(request.getHeaderNames().toString());

        out.println("<h1>Recepción de datos de campos del formulario</h1>");
        out.println(request.getRemoteUser());
        out.println(request.getParameter("nickname"));
        out.println(request.getParameter("password"));

        out.println("</body></html>");
    }

    public void destroy() {
    }
}