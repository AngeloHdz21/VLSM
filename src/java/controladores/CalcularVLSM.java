package controladores;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "CalcularVLSM", urlPatterns = {"/CalcularVLSM"})
public class CalcularVLSM extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String ip = request.getParameter("direccionIp");
        int prefijo = Integer.parseInt(request.getParameter("prefijo"));
        String textoTamanosLans = request.getParameter("tamanosLans");

        String[] tamanosRedesStr = textoTamanosLans.split("\\s+");

        List<Integer> listaDeTamanos = new ArrayList<>();

        for (String texto : tamanosRedesStr) {
            if (texto != null && !texto.isEmpty()) {
                listaDeTamanos.add(Integer.parseInt(texto));
            }
        }

        listaDeTamanos.sort(Collections.reverseOrder());

        int[] tamanosRedes = new int[listaDeTamanos.size()];
        for (int i = 0; i < listaDeTamanos.size(); i++) {
            tamanosRedes[i] = listaDeTamanos.get(i);
        }
        VLSM servicio = new VLSM();
        ResultadoVLSM resultado = servicio.calcularVLSM(ip, prefijo, tamanosRedes);

        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html><html><head><title>Resultado del Cálculo VLSM</title>");
            out.println("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet'>");
            out.println("<style>");
            out.println("body { font-family: 'Courier New', Courier, monospace; margin: 20px; background-color: #f4f4f9; }");
            out.println("h1, h2 { text-align: center; color: #333; }");
            out.println(".container { max-width: 900px; margin: auto; background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }");
            out.println(".proceso { white-space: pre; font-size: 1em; background-color: #282c34; color: #abb2bf; padding: 15px; border-radius: 5px; overflow-x: auto; }");
            out.println("table { width: 100%; border-collapse: collapse; margin-top: 20px; font-size: 0.9em; }");
            out.println("th, td { border: 1px solid #ddd; padding: 8px; text-align: center; }");
            out.println("th { background-color: #333; color: white; }");
            out.println("</style></head><body>");

            out.println("<div class='container'>");
            out.println("<h1>Resultado del Cálculo VLSM</h1>");
            out.println("<h2>Proceso de Cálculos Binarios</h2>");
            out.println("<div class='proceso'>" + resultado.getDetalleProceso() + "</div>");
            out.println("<h2>Tabla Resumen</h2>");
            out.println(resultado.getResumenFinal());

            out.println("<div style='text-align:center; margin-top:30px; display:flex; justify-content:center; gap: 15px;'>");
            out.println("<form action='GenerarPDF' method='post'>");
            out.println("<input type='hidden' name='direccionIp' value='" + ip + "'>");
            out.println("<input type='hidden' name='prefijo' value='" + prefijo + "'>");
            out.println("<input type='hidden' name='tamanosLans' value='" + textoTamanosLans.replace("\n", " ").trim() + "'>");
            out.println("<button type='submit' class='btn btn-success btn-lg'>Descargar PDF</button>");
            out.println("</form>");
            out.println("<div><a href='index.jsp' class='btn btn-secondary btn-lg'>Volver a Calcular</a></div>");
            out.println("</div>");

            out.println("</div></body></html>");
        }
    }
}
