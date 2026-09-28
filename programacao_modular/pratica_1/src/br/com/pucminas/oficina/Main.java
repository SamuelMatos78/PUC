package br.com.pucminas.oficina;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    private final Oficina oficina = new Oficina();
    private final Scanner entrada = new Scanner(System.in);

    public static void main(String[] args) {
        Main programa = new Main();
        programa.criarDadosIniciais();
        programa.executar();
    }

    private void criarDadosIniciais() {
        oficina.cadastrarMecanico(new Mecanico("Ana Souza", "11111111111", "Mecânica", "31999990001"));
        oficina.cadastrarMecanico(new Mecanico("Bruno Lima", "22222222222", "Elétrica", "31999990002"));
        oficina.cadastrarMecanico(new Mecanico("Carla Rocha", "33333333333", "Funilaria", "31999990003"));

        oficina.cadastrarBox(new Box(1, "Mecânica", 2, "Setor A"));
        oficina.cadastrarBox(new Box(2, "Elétrica", 2, "Setor B"));
        oficina.cadastrarBox(new Box(3, "Funilaria", 1, "Setor C"));
    }

    private void executar() {
        while (true) {
            exibirMenu();
            try {
                int opcao = lerInteiro("Opção: ");
                switch (opcao) {
                    case 1: cadastrarOrdem(); break;
                    case 2: associarMecanico(); break;
                    case 3: atribuirOrdem(); break;
                    case 4: listarOrdensDoBox(); break;
                    case 5: exibirTotaisFinalizados(); break;
                    case 6: buscarPorStatus(); break;
                    case 7: exibirDetalhesOrdem(); break;
                    case 8: finalizarOrdem(); break;
                    case 0: System.out.println("Até logo!"); return;
                    default: System.out.println("Opção inválida.");
                }
            } catch (NoSuchElementException e) {
                System.out.println("\nEntrada encerrada.");
                return;
            } catch (IllegalArgumentException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private void exibirMenu() {
        System.out.println("\n=== OFICINA MECÂNICA ===");
        System.out.println("1. Cadastrar ordem de serviço");
        System.out.println("2. Associar mecânico a box");
        System.out.println("3. Atribuir ordem a box");
        System.out.println("4. Exibir ordens de um box");
        System.out.println("5. Total de ordens finalizadas por box");
        System.out.println("6. Buscar ordens por status");
        System.out.println("7. Exibir detalhes de uma ordem");
        System.out.println("8. Finalizar ordem");
        System.out.println("0. Sair");
    }

    private void cadastrarOrdem() {
        int codigo = lerInteiro("Código da ordem: ");
        String cliente = lerTexto("Nome do cliente: ");
        String modelo = lerTexto("Modelo do veículo: ");
        String placa = lerTexto("Placa do veículo: ");
        LocalDate data = lerData("Data (AAAA-MM-DD): ");
        String nomeServico = lerTexto("Nome do serviço: ");
        String categoria = lerTexto("Categoria do serviço: ");
        int tempo = lerInteiro("Tempo estimado (minutos): ");
        BigDecimal valorServico = lerValor("Valor do serviço: R$ ");
        BigDecimal valorEstimado = lerValor("Valor estimado da ordem: R$ ");

        Servico servico = new Servico(nomeServico, tempo, valorServico, categoria);
        oficina.cadastrarOrdem(new OrdemServico(codigo, cliente, modelo, placa, data,
                valorEstimado, servico));
        System.out.println("Ordem cadastrada com status aberta.");
    }

    private void associarMecanico() {
        System.out.println("Mecânicos disponíveis:");
        for (Mecanico mecanico : oficina.listarMecanicos()) {
            System.out.println("  " + mecanico + " | Box: "
                    + (mecanico.getBoxResponsavel() == null ? "nenhum" : mecanico.getBoxResponsavel().getNumero()));
        }
        exibirBoxes();
        oficina.associarMecanico(lerTexto("CPF do mecânico: "), lerInteiro("Número do box: "));
        System.out.println("Mecânico associado ao box.");
    }

    private void atribuirOrdem() {
        exibirBoxes();
        oficina.atribuirOrdem(lerInteiro("Código da ordem: "), lerInteiro("Número do box: "));
        System.out.println("Ordem atribuída e colocada em execução.");
    }

    private void listarOrdensDoBox() {
        Box box = oficina.buscarBox(lerInteiro("Número do box: "));
        List<OrdemServico> ordens = oficina.listarOrdensDoBox(box.getNumero());
        System.out.println(box);
        for (OrdemServico ordem : ordens) {
            System.out.println(ordem.detalhes());
        }
        System.out.println("Total de ordens atribuídas ao box: " + ordens.size());
    }

    private void exibirTotaisFinalizados() {
        for (Box box : oficina.listarBoxes()) {
            System.out.println("Box " + box.getNumero() + ": " + box.getTotalOrdensFinalizadas()
                    + " ordem(ns) finalizada(s)");
        }
    }

    private void buscarPorStatus() {
        System.out.println("1. Aberta | 2. Em execução | 3. Finalizada");
        int escolha = lerInteiro("Status: ");
        StatusOrdem status;
        switch (escolha) {
            case 1: status = StatusOrdem.ABERTA; break;
            case 2: status = StatusOrdem.EM_EXECUCAO; break;
            case 3: status = StatusOrdem.FINALIZADA; break;
            default: throw new IllegalArgumentException("Status inválido.");
        }
        List<OrdemServico> resultado = oficina.buscarPorStatus(status);
        for (OrdemServico ordem : resultado) {
            System.out.println(ordem.detalhes());
        }
        System.out.println("Total encontrado: " + resultado.size());
    }

    private void exibirDetalhesOrdem() {
        System.out.println(oficina.buscarOrdem(lerInteiro("Código da ordem: ")).detalhes());
    }

    private void finalizarOrdem() {
        oficina.finalizarOrdem(lerInteiro("Código da ordem: "));
        System.out.println("Ordem finalizada. O box foi liberado.");
    }

    private void exibirBoxes() {
        System.out.println("Boxes:");
        for (Box box : oficina.listarBoxes()) {
            System.out.println("  " + box + " | Mecânico: "
                    + (box.getMecanicoResponsavel() == null ? "nenhum" : box.getMecanicoResponsavel().getNome()));
        }
    }

    private String lerTexto(String mensagem) {
        System.out.print(mensagem);
        return Mecanico.exigirTexto(entrada.nextLine(), mensagem);
    }

    private int lerInteiro(String mensagem) {
        return Integer.parseInt(lerTexto(mensagem));
    }

    private BigDecimal lerValor(String mensagem) {
        return new BigDecimal(lerTexto(mensagem).replace(',', '.'));
    }

    private LocalDate lerData(String mensagem) {
        try {
            return LocalDate.parse(lerTexto(mensagem));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data inválida. Use AAAA-MM-DD.");
        }
    }
}
