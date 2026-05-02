package br.ufscar.dc.compiladores.la.lexico;

import java.util.HashMap;
import java.util.Map;

/**
 * Tabela de Símbolos de um único escopo léxico.
 *
 * Armazena os identificadores declarados dentro de um bloco mapeando
 * o nome do identificador ao seu Simbolo. HashMap garante O(1) amortizado.
 */
public class TabelaDeSimbolos {

    /** Mapa interno: nome do identificador -> Simbolo correspondente */
    private final Map<String, Simbolo> tabela = new HashMap<>();

    /**
     * Adiciona um novo símbolo à tabela.
     * Verificar duplicatas é responsabilidade do visitador semântico.
     */
    public void adicionar(String nome, Simbolo simbolo) {
        tabela.put(nome, simbolo);
    }

    /**
     * Verifica se um identificador já foi declarado neste escopo.
     */
    public boolean contem(String nome) {
        return tabela.containsKey(nome);
    }

    /**
     * Recupera o símbolo associado a um nome.
     * Retorna null se não declarado neste escopo.
     */
    public Simbolo obter(String nome) {
        return tabela.get(nome);
    }

    /**
     * Retorna todos os símbolos desta tabela (útil para depuração).
     */
    public Map<String, Simbolo> getTodos() {
        return tabela;
    }
}
