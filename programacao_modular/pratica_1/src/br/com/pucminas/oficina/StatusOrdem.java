package br.com.pucminas.oficina;

public enum StatusOrdem {
    ABERTA("aberta"),
    EM_EXECUCAO("em execução"),
    FINALIZADA("finalizada");

    private final String descricao;

    StatusOrdem(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
