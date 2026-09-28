package br.com.pucminas.oficina;

public class Mecanico {
    private final String nome;
    private final String cpf;
    private final String especialidade;
    private final String telefone;
    private Box boxResponsavel;

    public Mecanico(String nome, String cpf, String especialidade, String telefone) {
        this.nome = exigirTexto(nome, "Nome");
        this.cpf = exigirTexto(cpf, "CPF");
        this.especialidade = exigirTexto(especialidade, "Especialidade");
        this.telefone = exigirTexto(telefone, "Telefone");
    }

    static String exigirTexto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " é obrigatório.");
        }
        return valor.trim();
    }

    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getEspecialidade() { return especialidade; }
    public String getTelefone() { return telefone; }
    public Box getBoxResponsavel() { return boxResponsavel; }

    void definirBoxResponsavel(Box box) {
        this.boxResponsavel = box;
    }

    @Override
    public String toString() {
        return nome + " (CPF: " + cpf + ", especialidade: " + especialidade
                + ", telefone: " + telefone + ")";
    }
}
