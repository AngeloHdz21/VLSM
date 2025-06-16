package controladores;

public class ResultadoVLSM {
    private final String detalleProceso;
    private final String resumenFinal;

    public ResultadoVLSM(String detalleProceso, String resumenFinal) {
        this.detalleProceso = detalleProceso;
        this.resumenFinal = resumenFinal;
    }

    public String getDetalleProceso() {
        return detalleProceso;
    }

    public String getResumenFinal() {
        return resumenFinal;
    }
}