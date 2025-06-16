<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Calculadora VLSM - Ángelo Hernández Cadena</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <style>
        body {
            font-family: 'Courier New', Courier, monospace;
            background-color: #f0f2f5;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            padding: 20px;
        }
        .container {
            max-width: 800px;
            width: 100%;
        }
        .card {
            background-color: #ffffff;
            color: #212529;
            border: 1px solid #dee2e6;
            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
            border-radius: 12px;
        }
        .card-header {
            background-color: #212529;
            color: white;
            font-weight: bold;
            font-size: 1.5rem;
            text-align: center;
            border-bottom: 1px solid #444;
            border-top-left-radius: 12px;
            border-top-right-radius: 12px;
        }
        .form-label {
            font-weight: bold;
        }
        .btn-primary {
            background-color: #212529;
            border-color: #212529;
        }
        .btn-primary:hover {
            background-color: #000000;
            border-color: #000000;
        }
    </style>
    </head>
<body>
    <div class="container">
        <div class="card">
            <div class="card-header">
                Calculadora VLSM
            </div>
            <div class="card-body p-4">
                <form action="CalcularVLSM" method="post">
                    <h5 class="card-title mb-4">Datos de la Red Principal</h5>
                    <div class="row">
                        <div class="col-md-8 mb-3">
                            <label for="ip" class="form-label">Dirección IP Base:</label>
                            <input type="text" class="form-control" id="ip" name="ip" placeholder="Ej: 170.45.0.0" required>
                        </div>
                        <div class="col-md-4 mb-3">
                            <label for="prefix" class="form-label">Prefijo:</label>
                            <input type="number" class="form-control" id="prefix" name="prefix" min="1" max="32" placeholder="Ej: 22" required>
                        </div>
                    </div>
                    <hr class="my-4">
                    <div class="mb-3">
                        <label for="lanSizes" class="form-label"><strong>Ingrese los Hosts para cada LAN (uno por línea):</strong></label>
                        <textarea class="form-control" id="lanSizes" name="lanSizes" rows="8" placeholder="Ejemplo:&#10;250&#10;130&#10;50&#10;20" required></textarea>
                    </div>
                    <div class="d-grid gap-2 mt-4">
                        <button type="submit" class="btn btn-primary btn-lg">Calcular Subredes</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</body>
</html>