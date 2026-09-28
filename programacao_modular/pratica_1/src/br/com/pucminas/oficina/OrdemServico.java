package br.com.pucminas.oficina;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OrdemServico {
    private final int codigo;
    private final String nomeCliente;
    private final String modeloVeiculo;
    private final String placaVeiculo;
    private final LocalDate data;
    private final BigDecimal valorEstimado;
    private final Servico servico;
    private StatusOrdem status = StatusOrdem.ABERTA;
    private Box boxUtilizado;
    private Mecanico mecanicoResponsavel;

    public OrdemServico(int codigo, String nomeCliente, String modeloVeiculo,
            String placaVeiculo, LocalDate data, BigDecimal valorEstimado, Servico servico) {
        if (codigo <= 0) {
            throw new IllegalArgumentException("O código deve ser positivo.");
        }
        if (data == null || servico == null || valorEstimado == null || valorEstimado.signum() < 0) {
            throw new IllegalArgumentException("Data, serviço e valor estimado válido são obrigatórios.");
        }
        this.codigo = codigo;
        this.nomeCliente = Mecanico.exigirTexto(nomeCliente, "Cliente");
        this.modeloVeiculo = Mecanico.exigirTexto(modeloVeiculo, "Modelo do veículo");
        this.placaVeiculo = Mecanico.exigirTexto(placaVeiculo, "Placa do veículo");
        this.data = data;
        this.valorEstimado = valorEstimado;
        this.servico = servico;
    }

    public int getCodigo() { return codigo; }
    public StatusOrdem getStatus() { return status; }
    public Servico getServico() { return servico; }
    public Box getBoxUtilizado() { return boxUtilizado; }
    public Mecanico getMecanicoResponsavel() { return mecanicoResponsavel; }

    void iniciarNoBox(Box box) {
        this.boxUtilizado = box;
        this.mecanicoResponsavel = box.getMecanicoResponsavel();
        this.status = StatusOrdem.EM_EXECUCAO;
    }

    void finalizar() {
        this.status = StatusOrdem.FINALIZADA;
    }

    public String detalhes() {
        return "Ordem " + codigo + "\n"
                + "  Cliente: " + nomeCliente + "\n"
                + "  Veículo: " + modeloVeiculo + " | Placa: " + placaVeiculo + "\n"
                + "  Data: " + data + " | Status: " + status.getDescricao() + "\n"
                + "  Valor estimado: R$ " + valorEstimado.toPlainString() + "\n"
                + "  Serviço: " + servico + "\n"
                + "  Box: " + (boxUtilizado == null ? "não atribuído" : boxUtilizado) + "\n"
                + "  Mecânico: " + (mecanicoResponsavel == null ? "não atribuído" : mecanicoResponsavel);
    }
}
