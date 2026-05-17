package br.ufscar.dc.compiladores.la.lexico;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa uma entrada na Tabela de Simbolos do analisador semantico.
 *
 * Cada simbolo guarda o nome do identificador declarado, sua categoria
 * (variavel, constante, funcao, etc.) e seu tipo dentro da linguagem LA.
 *
 * T4: adicionados os campos 'parametros' e 'tipoRetorno' para suportar
 * assinatura completa de funcoes e procedimentos, necessaria para validar
 * chamadas (Erro 3 do T4).
 */
public class Simbolo {

    /**
     * Categorias possiveis de um simbolo na linguagem LA.
     */
    public enum Categoria {
        VARIAVEL,
        CONSTANTE,
        PROCEDIMENTO,
        FUNCAO,
        TIPO
    }

    /** Nome do identificador como escrito no codigo-fonte */
    private final String nome;

    /** Categoria semantica do simbolo */
    private final Categoria categoria;

    /** Tipo LA associado ao simbolo */
    private final TipoLA tipo;

    /**
     * Nome do tipo para registros e tipos definidos pelo usuario.
     * Null para tipos primitivos.
     */
    private final String nomeDoTipo;

    /**
     * Lista de campos para simbolos que representam registros ou tipos customizados.
     */
    private final List<Simbolo> campos;

    /**
     * T4: Lista de parametros formais de funcoes e procedimentos.
     * Cada entrada e um Simbolo representando um parametro (nome + tipo).
     * Usada pela Pessoa 2 para validar chamadas (quantidade, ordem e tipo).
     */
    private final List<Simbolo> parametros;

    /**
     * T4: Tipo de retorno da funcao. Null para procedimentos e variaveis.
     * Diferente de 'tipo' (que guarda o tipo do simbolo em geral),
     * tipoRetorno guarda especificamente o que a funcao retorna.
     */
    private TipoLA tipoRetorno;

    // -------------------------------------------------------------------------
    // Construtores
    // -------------------------------------------------------------------------

    /** Construtor para simbolos de tipo primitivo (variavel, constante). */
    public Simbolo(String nome, Categoria categoria, TipoLA tipo) {
        this.nome        = nome;
        this.categoria   = categoria;
        this.tipo        = tipo;
        this.nomeDoTipo  = null;
        this.campos      = new ArrayList<>();
        this.parametros  = new ArrayList<>();
        this.tipoRetorno = null;
    }

    /**
     * Construtor para simbolos de tipo registro ou tipo definido pelo usuario.
     * O nomeDoTipo permite checar compatibilidade entre dois registros
     * (devem ter o mesmo nome de tipo).
     */
    public Simbolo(String nome, Categoria categoria, TipoLA tipo, String nomeDoTipo) {
        this.nome        = nome;
        this.categoria   = categoria;
        this.tipo        = tipo;
        this.nomeDoTipo  = nomeDoTipo;
        this.campos      = new ArrayList<>();
        this.parametros  = new ArrayList<>();
        this.tipoRetorno = null;
    }

    // -------------------------------------------------------------------------
    // Getters basicos
    // -------------------------------------------------------------------------

    public String getNome()         { return nome; }
    public Categoria getCategoria() { return categoria; }
    public TipoLA getTipo()         { return tipo; }
    public String getNomeDoTipo()   { return nomeDoTipo; }

    // -------------------------------------------------------------------------
    // Campos de registro
    // -------------------------------------------------------------------------

    /** Retorna os campos internos deste registro. */
    public List<Simbolo> getCampos()         { return campos; }

    /** Adiciona um campo ao registro. */
    public void adicionarCampo(Simbolo s)    { this.campos.add(s); }

    // -------------------------------------------------------------------------
    // T4: Parametros e tipo de retorno de funcoes/procedimentos
    // -------------------------------------------------------------------------

    /**
     * Retorna a lista de parametros formais da funcao/procedimento.
     * A ordem importa: usada para validar chamadas na Pessoa 2.
     */
    public List<Simbolo> getParametros()          { return parametros; }

    /**
     * Adiciona um parametro formal a assinatura da funcao/procedimento.
     * Deve ser chamado na ordem em que os parametros aparecem na declaracao.
     */
    public void adicionarParametro(Simbolo param) { this.parametros.add(param); }

    /**
     * Retorna o tipo de retorno da funcao, ou null se for procedimento/variavel.
     */
    public TipoLA getTipoRetorno()                { return tipoRetorno; }

    /**
     * Define o tipo de retorno da funcao.
     * Chamado em visitDeclaracao_global quando isFuncao == true.
     */
    public void setTipoRetorno(TipoLA tipoRetorno) { this.tipoRetorno = tipoRetorno; }

    // -------------------------------------------------------------------------
    // Debug
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return "Simbolo{nome='" + nome + "', categoria=" + categoria
                + ", tipo=" + tipo
                + (nomeDoTipo  != null   ? ", nomeDoTipo='" + nomeDoTipo + "'" : "")
                + (tipoRetorno != null   ? ", tipoRetorno=" + tipoRetorno      : "")
                + (campos.isEmpty()     ? "" : ", campos="     + campos.size())
                + (parametros.isEmpty() ? "" : ", parametros=" + parametros.size())
                + "}";
    }
}
