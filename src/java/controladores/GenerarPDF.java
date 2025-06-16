package controladores;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "GenerarPDF", urlPatterns = {"/GenerarPDF"})
public class GenerarPDF extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String ip = request.getParameter("ip");
        int prefijo = Integer.parseInt(request.getParameter("prefix"));
        String lanSizesRaw = request.getParameter("lanSizes");
        
        String[] tamanosRedesStr = lanSizesRaw.split("\\s+");
        int[] tamanosRedes = Arrays.stream(tamanosRedesStr)
                                   .filter(s -> s != null && !s.isEmpty())
                                   .mapToInt(Integer::parseInt)
                                   .toArray();
        
        VLSM servicio = new VLSM();
        ResultadoVLSM resultadoOriginal = servicio.calcularVLSM(ip, prefijo, tamanosRedes);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"ResultadoVLSM.pdf\"");

        Document document = new Document(PageSize.A4.rotate(), 20, 20, 20, 20);

        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Font fuenteBase = FontFactory.getFont(FontFactory.COURIER, 9, BaseColor.BLACK);
            Font fuenteTitulo = FontFactory.getFont(FontFactory.COURIER_BOLD, 14, BaseColor.BLACK);
            Font fuenteSubtitulo = FontFactory.getFont(FontFactory.COURIER_BOLD, 11, BaseColor.BLACK);
            Font fuenteHeader = FontFactory.getFont(FontFactory.COURIER_BOLD, 8, BaseColor.WHITE);

            Paragraph titulo = new Paragraph("Resultado del Calculo VLSM", fuenteTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Proceso de Calculos Binarios", fuenteSubtitulo));
            
            PdfPTable tablaProceso = new PdfPTable(2);
            tablaProceso.setWidthPercentage(100);
            tablaProceso.setSpacingBefore(10f);
            tablaProceso.setWidths(new float[]{2.8f, 2f});
            tablaProceso.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
            
            String detalleCompleto = resultadoOriginal.getDetalleProceso();
            String[] lineasProceso = detalleCompleto.split("\n");
            
            for(String linea : lineasProceso) {
                if (linea.trim().isEmpty()) continue;
                String[] columnas = linea.trim().split("\\s{2,}", 2);
                if (columnas.length == 2) {
                    tablaProceso.addCell(new Phrase(columnas[0], fuenteBase));
                    tablaProceso.addCell(new Phrase(columnas[1], fuenteBase));
                } else {
                    PdfPCell celdaCompleta = new PdfPCell(new Phrase(linea, fuenteBase));
                    celdaCompleta.setColspan(2);
                    celdaCompleta.setBorder(PdfPCell.NO_BORDER);
                    tablaProceso.addCell(celdaCompleta);
                }
            }
            document.add(tablaProceso);
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Tabla Resumen", fuenteSubtitulo));
            
            String tablaHTML = resultadoOriginal.getResumenFinal();
            PdfPTable tablaResumen = new PdfPTable(7);
            tablaResumen.setWidthPercentage(100);
            tablaResumen.setSpacingBefore(10f);

            String[] headers = {"Nombre LAN", "Hosts Req.", "Asignados", "Dirección Red", "Máscara", "Rango IP", "Broadcast"};
            for(String header : headers) {
                PdfPCell headerCell = new PdfPCell(new Phrase(header, fuenteHeader));
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setBackgroundColor(new BaseColor(40, 44, 52));
                tablaResumen.addCell(headerCell);
            }
            
            String tablaBody = tablaHTML.substring(tablaHTML.indexOf("<tbody>") + 7, tablaHTML.indexOf("</tbody>"));
            String[] filasHTML = tablaBody.split("<tr>");
            for (String fila : filasHTML) {
                if (!fila.contains("<td>")) continue;
                String[] celdasHTML = fila.split("</td>");
                for (String celda : celdasHTML) {
                    if (celda.contains("<td>")) {
                        String texto = celda.substring(celda.indexOf("<td>") + 4).trim();
                        tablaResumen.addCell(new Phrase(texto, fuenteBase));
                    }
                }
            }
            document.add(tablaResumen);

        } catch (DocumentException e) {
            throw new IOException(e.getMessage());
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }
}