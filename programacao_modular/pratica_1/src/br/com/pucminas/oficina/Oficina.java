package br.com.pucminas.oficina;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Oficina {
    private final Map<String, Mecanico> mecanicos = new LinkedHashMap<>();
    private final Map<Integer, Box> boxes = new LinkedHashMap<>();
    private final Map<Integer, OrdemServico> ordens = new LinkedHashMap<>();

    public void cadastrarMecanico(Mecanico mecanico) {
        if (mecanico == null || mecanicos.containsKey(mecanico.getCpf())) {
            throw new IllegalArgumentException("Mecânico inválido ou CPF já cadastrado.");
        }
        mecanicos.put(mecanico.getCpf(), mecanico);
    }

    public void cadastrarBox(Box box) {
        if (box == null || boxes.containsKey(box.getNumero())) {
            throw new IllegalArgumentException("Box inválido ou número já cadastrado.");
        }
        boxes.put(box.getNumero(), box);
    }

    public void cadastrarOrdem(OrdemServico ordem) {
        if (ordem == null || ordens.containsKey(ordem.getCodigo())) {
            throw new IllegalArgumentException("Ordem inválida ou código já cadastrado.");
        }
        if (ordem.getStatus() != StatusOrdem.ABERTA || ordem.getBoxUtilizado() != null) {
            throw new IllegalArgumentException("A nova ordem deve estar aberta e sem box.");
        }
        ordens.put(ordem.getCodigo(), ordem);
    }

    public List<Mecanico> listarMecanicos() {
        return Collections.unmodifiableList(new ArrayList<>(mecanicos.values()));
    }

    public List<Box> listarBoxes() {
        return Collections.unmodifiableList(new ArrayList<>(boxes.values()));
    }

    public Box buscarBox(int numero) {
        Box box = boxes.get(numero);
        if (box == null) {
            throw new IllegalArgumentException("Box não encontrado.");
        }
        return box;
    }

    public OrdemServico buscarOrdem(int codigo) {
        OrdemServico ordem = ordens.get(codigo);
        if (ordem == null) {
            throw new IllegalArgumentException("Ordem não encontrada.");
        }
        return ordem;
    }

    public void associarMecanico(String cpf, int numeroBox) {
        Mecanico mecanico = mecanicos.get(cpf);
        if (mecanico == null) {
            throw new IllegalArgumentException("Mecânico não encontrado.");
        }
        Box box = buscarBox(numeroBox);
        if (mecanico.getBoxResponsavel() != null) {
            throw new IllegalArgumentException("Este mecânico já é responsável por um box.");
        }
        if (box.getMecanicoResponsavel() != null) {
            throw new IllegalArgumentException("Este box já possui mecânico responsável.");
        }
        mecanico.definirBoxResponsavel(box);
        box.definirMecanicoResponsavel(mecanico);
    }

    public void atribuirOrdem(int codigo, int numeroBox) {
        OrdemServico ordem = buscarOrdem(codigo);
        Box box = buscarBox(numeroBox);
        if (ordem.getStatus() != StatusOrdem.ABERTA) {
            throw new IllegalArgumentException("Somente ordens abertas podem ser atribuídas.");
        }
        if (box.getMecanicoResponsavel() == null) {
            throw new IllegalArgumentException("Associe um mecânico ao box antes de atribuir ordens.");
        }
        if (!box.getTipoServicoPermitido().equalsIgnoreCase(ordem.getServico().getCategoria())) {
            throw new IllegalArgumentException("A categoria do serviço não é permitida neste box.");
        }
        if (box.getOrdensEmExecucao().size() >= box.getCapacidadeMaxima()) {
            throw new IllegalArgumentException("O box atingiu sua capacidade máxima.");
        }
        box.receber(ordem);
        ordem.iniciarNoBox(box);
    }

    public void finalizarOrdem(int codigo) {
        OrdemServico ordem = buscarOrdem(codigo);
        if (ordem.getStatus() != StatusOrdem.EM_EXECUCAO) {
            throw new IllegalArgumentException("Somente ordens em execução podem ser finalizadas.");
        }
        ordem.getBoxUtilizado().concluir(ordem);
        ordem.finalizar();
    }

    public List<OrdemServico> buscarPorStatus(StatusOrdem status) {
        if (status == null) {
            throw new IllegalArgumentException("Status é obrigatório.");
        }
        List<OrdemServico> resultado = new ArrayList<>();
        for (OrdemServico ordem : ordens.values()) {
            if (ordem.getStatus() == status) {
                resultado.add(ordem);
            }
        }
        return resultado;
    }

    public List<OrdemServico> listarOrdensDoBox(int numeroBox) {
        Box box = buscarBox(numeroBox);
        List<OrdemServico> resultado = new ArrayList<>();
        for (OrdemServico ordem : ordens.values()) {
            if (ordem.getBoxUtilizado() == box) {
                resultado.add(ordem);
            }
        }
        return resultado;
    }
}
