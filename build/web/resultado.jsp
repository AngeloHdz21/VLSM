<%-- Importamos solo la clase que necesitamos --%>
<%@page import="controladores.ResultadoVLSM"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Resultado del Cálculo VLSM</title>

        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            body {
                background-color: #f8f9fa;
            }
            .container {
                margin-top: 30px;
                margin-bottom: 50px;
            }
            .card {
                border: none;
                box-shadow: 0 4px 12px rgba(0,0,0,0.08);
                border-radius: 12px;
                margin-bottom: 30px;
            }
            .card-header {
                font-weight: bold;
                font-size: 1.25rem;
                text-align: center;
            }
            pre {
                background-color: #212529;
                color: #f8f9fa;
                padding: 20px;
                border-radius: 8px;
                font-size: 13px;
                white-space: pre;
                overflow-x: auto;
            }
            .table-container table {
                width: 100%;
                border-collapse: collapse;
            }
            .table-container th, .table-container td {
                border: 1px solid #dee2e6;
                padding: 8px;
                text-align: center;
            }
            .table-container th {
                background-color: #212529;
                color: white;
            }
        </style>
    </head>
    <body>
        <div class="container">

            <%
                ResultadoVLSM resultado = (ResultadoVLSM) request.getAttribute("resultadoCalculo");
            %>

            <% if (resultado != null) {%>

            <h1 class="text-center mb-4">Resultado del Cálculo VLSM</h1>

            <div class="card">
                <div class="card-header bg-dark text-white">
                    Proceso de Cálculos Binarios
                </div>
                <div class="card-body">
                    <pre><%= resultado.getDetalleProceso()%></pre>
                </div>
            </div>

            <div class="card">
                <div class="card-header bg-primary text-white">
                    Tabla Resumen
                </div>
                <div class="card-body table-container">
                    <%= resultado.getResumenFinal()%>
                </div>
            </div>

            <div class="text-center">
                <a href="index.jsp" class="btn btn-secondary btn-lg">Volver a Calcular</a>
                <form action="GenerarPDF" method="post" class="d-inline ms-2">
                    <input type="hidden" name="ip" value="<%= request.getParameter("ip")%>">
                    <input type="hidden" name="prefix" value="<%= request.getParameter("prefix")%>">
                    <input type="hidden" name="lanSizes" value="<%= request.getParameter("lanSizes")%>">
                    <button type="submit" class="btn btn-success btn-lg">Descargar PDF</button>
                </form>
            </div>

            <% } else { %>
            <div class="alert alert-warning text-center">
                <h4 class="alert-heading">No hay resultados que mostrar</h4>
                <p>Por favor, vuelve a la página principal para realizar un cálculo.</p>
                <hr>
                <a href="index.jsp" class="btn btn-primary">Ir a la Calculadora</a>
            </div>
            <% }%>
        </div>
    </body>
</html>