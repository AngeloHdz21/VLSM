package controladores;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VLSM {

    private static class NodoSubred {

        String direccionIp;
        int prefijo;
        Map.Entry<String, Integer> requerimientoAsignado = null;
        NodoSubred hijoIzquierdo = null;
        NodoSubred hijoDerecho = null;

        public NodoSubred(String direccionIp, int prefijo) {
            this.direccionIp = direccionIp;
            this.prefijo = prefijo;
        }

        public void dividir() {
            if (this.hijoIzquierdo == null && this.prefijo < 32) {
                int ipPadreInt = ipAEntero(this.direccionIp);
                int bitsDeHost = 32 - (this.prefijo + 1);
                int incremento = (int) Math.pow(2, bitsDeHost);
                this.hijoIzquierdo = new NodoSubred(this.direccionIp, this.prefijo + 1);
                this.hijoDerecho = new NodoSubred(enteroAIp(ipPadreInt + incremento), this.prefijo + 1);
            }
        }
    }

    private StringBuilder detalleProceso;
    private List<NodoSubred> nodosAsignadosParaTabla;

    private String formatearConEspacios(String parteBinaria, String parteDecimal) {
        int longitudDeseada = 45;
        int espacios = longitudDeseada - parteBinaria.length();
        if (espacios < 1) {
            espacios = 1;
        }
        return parteBinaria + " ".repeat(espacios) + parteDecimal;
    }

    private int calcularBitsNecesarios(int hosts) {
        if (hosts <= 0) {
            return 0;
        }
        int n = 0;
        while (Math.pow(2, n) < hosts + 2) {
            n++;
        }
        return n;
    }

    private static String ipABinario(String ip) {
        String[] octetos = ip.split("\\.");
        StringBuilder binario = new StringBuilder();
        for (String octeto : octetos) {
            binario.append(String.format("%8s", Integer.toBinaryString(Integer.parseInt(octeto))).replace(' ', '0'));
        }
        return binario.toString();
    }

    private static String binarioAIp(String binario) {
        StringBuilder ip = new StringBuilder();
        for (int i = 0; i < 32; i += 8) {
            ip.append(Integer.parseInt(binario.substring(i, i + 8), 2));
            if (i < 24) {
                ip.append(".");
            }
        }
        return ip.toString();
    }

    private static int ipAEntero(String ip) {
        String[] octetos = ip.split("\\.");

        long octeto1 = Long.parseLong(octetos[0]);
        long octeto2 = Long.parseLong(octetos[1]);
        long octeto3 = Long.parseLong(octetos[2]);
        long octeto4 = Long.parseLong(octetos[3]);

        long resultado = (octeto1 * 256 * 256 * 256)
                + (octeto2 * 256 * 256)
                + (octeto3 * 256)
                + octeto4;

        return (int) resultado;
    }

    private static String enteroAIp(int ip) {
        long ipComoLong = ip & 0xFFFFFFFFL;

        long octeto1 = ipComoLong / 16777216; // (256^3)
        long resto1 = ipComoLong % 16777216;

        long octeto2 = resto1 / 65536; // (256^2)
        long resto2 = resto1 % 65536;

        long octeto3 = resto2 / 256;
        long octeto4 = resto2 % 256;

        return octeto1 + "." + octeto2 + "." + octeto3 + "." + octeto4;
    }

    private String calcularPrimeraUtilizable(String red, int prefijo) {
        if (prefijo >= 31) {
            return "N/A";
        }
        return enteroAIp(ipAEntero(red) + 1);
    }

    private String calcularUltimaUtilizable(String red, int prefijo) {
        if (prefijo >= 31) {
            return "N/A";
        }
        return enteroAIp(ipAEntero(calcularBroadcast(red, prefijo)) - 1);
    }

    private String calcularMascaraDePrefijo(int prefijo) {
        if (prefijo < 0 || prefijo > 32) {
            throw new IllegalArgumentException("El prefijo debe estar entre 0 y 32");
        }

        StringBuilder mascaraBinaria = new StringBuilder();

        for (int i = 0; i < 32; i++) {
            if (i < prefijo) {
                mascaraBinaria.append("1");
            }
            else {
                mascaraBinaria.append("0");
            }
        }
        long mascaraComoLong = Long.parseLong(mascaraBinaria.toString(), 2);

        return enteroAIp((int) mascaraComoLong);
    }

    private String calcularBroadcast(String red, int prefijo) {
        if (prefijo >= 31) {
            return "N/A";
        }
        int redInt = ipAEntero(red);
        int mascaraInvertida = (1 << (32 - prefijo)) - 1;
        return enteroAIp(redInt | mascaraInvertida);
    }

    private static String formatearIPBinaria(String binario, int prefijo, int nuevaMascara) {
        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < binario.length(); i++) {
            if (i > 0 && i % 8 == 0) {
                resultado.append(".");
            }
            if (i == prefijo || i == nuevaMascara) {
                resultado.append("|");
            }
            resultado.append(binario.charAt(i));
        }
        return resultado.toString();
    }

    private void buscarNodosAsignados(NodoSubred nodo, List<NodoSubred> lista) {
        if (nodo == null) {
            return;
        }
        if (nodo.requerimientoAsignado != null) {
            lista.add(nodo);
        }
        buscarNodosAsignados(nodo.hijoIzquierdo, lista);
        buscarNodosAsignados(nodo.hijoDerecho, lista);
    }

    private String generarTablaResumenHTML() {
        nodosAsignadosParaTabla.sort((n1, n2) -> n2.requerimientoAsignado.getValue().compareTo(n1.requerimientoAsignado.getValue()));

        StringBuilder sb = new StringBuilder();
        sb.append("<table><thead><tr><th>Nombre LAN</th><th>Hosts Req.</th><th>Asignados</th><th>Dirección Red</th><th>Máscara</th><th>Rango IP</th><th>Broadcast</th></tr></thead><tbody>");
        for (NodoSubred nodo : nodosAsignadosParaTabla) {
            int bitsNecesarios = 32 - nodo.prefijo;
            int hostsAsignados = (int) (Math.pow(2, bitsNecesarios) - 2);
            sb.append("<tr>");
            sb.append("<td>").append(nodo.requerimientoAsignado.getKey()).append("</td>");
            sb.append("<td>").append(nodo.requerimientoAsignado.getValue()).append("</td>");
            sb.append("<td>").append(hostsAsignados).append("</td>");
            sb.append("<td>").append(nodo.direccionIp).append("/").append(nodo.prefijo).append("</td>");
            sb.append("<td>").append(calcularMascaraDePrefijo(nodo.prefijo)).append("</td>");
            sb.append("<td>").append(calcularPrimeraUtilizable(nodo.direccionIp, nodo.prefijo)).append(" - ").append(calcularUltimaUtilizable(nodo.direccionIp, nodo.prefijo)).append("</td>");
            sb.append("<td>").append(calcularBroadcast(nodo.direccionIp, nodo.prefijo)).append("</td>");
            sb.append("</tr>");
        }
        sb.append("</tbody></table>");
        return sb.toString();
    }

    private void generarDetalleProceso(NodoSubred nodo, int nivel) {
        if (nodo == null) {
            return;
        }
        String sangria = "    ".repeat(nivel);
        String ipBinaria = formatearIPBinaria(ipABinario(nodo.direccionIp), -1, nodo.prefijo);
        String linea = formatearConEspacios(ipBinaria, nodo.direccionIp + "/" + nodo.prefijo);
        if (nodo.requerimientoAsignado != null) {
            linea += " ==> " + nodo.requerimientoAsignado.getKey();
        }
        detalleProceso.append(sangria).append(linea).append("\n");
        if (nodo.hijoIzquierdo != null) {
            generarDetalleProceso(nodo.hijoIzquierdo, nivel + 1);
            generarDetalleProceso(nodo.hijoDerecho, nivel + 1);
        }
    }

    private boolean asignarRed(NodoSubred nodo, Map.Entry<String, Integer> requerimiento) {
        if (nodo == null || nodo.requerimientoAsignado != null) {
            return false;
        }

        int tamanoRequerido = requerimiento.getValue();
        int prefijoNecesario = 32 - calcularBitsNecesarios(tamanoRequerido);

        if (nodo.prefijo > prefijoNecesario) {
            return false;
        }

        boolean asignado = false;
        if (nodo.prefijo < prefijoNecesario) {
            nodo.dividir();
            asignado = asignarRed(nodo.hijoIzquierdo, requerimiento);
            if (!asignado) {
                asignado = asignarRed(nodo.hijoDerecho, requerimiento);
            }
        } else {
            nodo.requerimientoAsignado = requerimiento;
            nodosAsignadosParaTabla.add(nodo);
            return true;
        }
        return asignado;
    }

    public ResultadoVLSM calcularVLSM(String ip, int prefijo, int[] tamanosRedes) {
        this.detalleProceso = new StringBuilder();
        this.nodosAsignadosParaTabla = new ArrayList<>();

        Map<String, Integer> requerimientos = new LinkedHashMap<>();
        for (int i = 0; i < tamanosRedes.length; i++) {
            requerimientos.put("LAN " + String.format("%02d", i + 1), tamanosRedes[i]);
        }

        List<Map.Entry<String, Integer>> listaOrdenada = new ArrayList<>(requerimientos.entrySet());
        listaOrdenada.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        NodoSubred raiz = new NodoSubred(ip, prefijo);

        for (Map.Entry<String, Integer> requerimiento : listaOrdenada) {
            asignarRed(raiz, requerimiento);
        }

        generarDetalleProceso(raiz, 0);
        String tablaResumenHTML = generarTablaResumenHTML();

        return new ResultadoVLSM(detalleProceso.toString(), tablaResumenHTML);
    }
}
