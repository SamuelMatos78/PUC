package br.com.pucminas.oficina;

import java.math.BigDecimal;

public class Servico {
    private final String nome;
    private final int tempoEstimadoMinutos;
    private final BigDecimal valor;
    private final String categoria;

    public Servico(String nome, int tempoEstimadoMinutos, BigDecimal valor, String categoria) {
        this.nome = Mecanico.exigirTexto(nome, "Nome do serviço");
        if (tempoEstimadoMinutos <= 0) {
            throw new IllegalArgumentException("O tempo estimado deve ser positivo.");
        }
        if (valor == null || valor.signum() < 0) {
            throw new IllegalArgumentException("O valor do serviço não pode ser negativo.");
        }
        this.tempoEstimadoMinutos = tempoEstimadoMinutos;
        this.valor = valor;
        this.categoria = Mecanico.exigirTexto(categoria, "Categoria");
    }

    public String getNome() { return nome; }
    public int getTempoEstimadoMinutos() { return tempoEstimadoMinutos; }
    public BigDecimal getValor() { return valor; }
    public String getCategoria() { return categoria; }

    @Override
    public String toString() {
        return nome + " (categoria: " + categoria + ", tempo: " + tempoEstimadoMinutos
                + " min, valor: R$ " + valor.toPlainString() + ")";
    }
}
