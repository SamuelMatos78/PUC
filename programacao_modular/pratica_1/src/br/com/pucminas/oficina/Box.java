package br.com.pucminas.oficina;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Box {
    private final int numero;
    private final String tipoServicoPermitido;
    private final int capacidadeMaxima;
    private final String localizacao;
    private Mecanico mecanicoResponsavel;
    private final List<OrdemServico> ordensEmExecucao = new ArrayList<>();
    private int totalOrdensFinalizadas;

    public Box(int numero, String tipoServicoPermitido, int capacidadeMaxima, String localizacao) {
        if (numero <= 0 || capacidadeMaxima <= 0) {
            throw new IllegalArgumentException("Número e capacidade do box devem ser positivos.");
        }
        this.numero = numero;
        this.tipoServicoPermitido = Mecanico.exigirTexto(tipoServicoPermitido, "Tipo de serviço");
        this.capacidadeMaxima = capacidadeMaxima;
        this.localizacao = Mecanico.exigirTexto(localizacao, "Localização");
    }

    public int getNumero() { return numero; }
    public String getTipoServicoPermitido() { return tipoServicoPermitido; }
    public int getCapacidadeMaxima() { return capacidadeMaxima; }
    public String getLocalizacao() { return localizacao; }
    public Mecanico getMecanicoResponsavel() { return mecanicoResponsavel; }
    public int getTotalOrdensFinalizadas() { return totalOrdensFinalizadas; }

    public List<OrdemServico> getOrdensEmExecucao() {
        return Collections.unmodifiableList(ordensEmExecucao);
    }

    void definirMecanicoResponsavel(Mecanico mecanico) {
        this.mecanicoResponsavel = mecanico;
    }

    void receber(OrdemServico ordem) {
        ordensEmExecucao.add(ordem);
    }

    void concluir(OrdemServico ordem) {
        if (!ordensEmExecucao.remove(ordem)) {
            throw new IllegalStateException("A ordem não está em execução neste box.");
        }
        totalOrdensFinalizadas++;
    }

    @Override
    public String toString() {
        return "Box " + numero + " (tipo: " + tipoServicoPermitido + ", capacidade: "
                + capacidadeMaxima + ", localização: " + localizacao + ")";
    }
}
