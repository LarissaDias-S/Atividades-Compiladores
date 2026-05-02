package br.ufscar.dc.compiladores.la.lexico;

/**
 * Representa uma entrada na Tabela de Símbolos do analisador semântico.
 *
 * Cada símbolo guarda o nome do identificador declarado, sua categoria
 * (variável, constante, função, etc.) e seu tipo dentro da linguagem LA.
 * Para registros, o campo nomeDoTipo armazena o nome da estrutura, permitindo
 * verificar compatibilidade entre dois registros (devem ter o mesmo nome).
 */
public class Simbolo {

    /**
     * Categorias possíveis de um símbolo na linguagem LA.
     */
    public enum Categoria {
        VARIAVEL,
        CONSTANTE,
        PROCEDIMENTO,
        FUNCAO,
        TIPO
    }

    /** Nome do identificador como escrito no código-fonte */
    private final String nome;

    /** Categoria semântica do símbolo */
    private final Categoria categoria;

    /** Tipo LA associado ao símbolo */
    private final TipoLA tipo;

    /**
     * Nome do tipo para registros e tipos definidos pelo usuário.
     * Null para tipos primitivos.
     */
    private final String nomeDoTipo;

    /**
     * Construtor para símbolos de tipo primitivo.
     */
    public Simbolo(String nome, Categoria categoria, TipoLA tipo) {
        this.nome       = nome;
        this.categoria  = categoria;
        this.tipo       = tipo;
        this.nomeDoTipo = null;
    }

    /**
     * Construtor para símbolos de tipo registro ou tipo definido pelo usuário.
     */
    public Simbolo(String nome, Categoria categoria, TipoLA tipo, String nomeDoTipo) {
        this.nome       = nome;
        this.categoria  = categoria;
        this.tipo       = tipo;
        this.nomeDoTipo = nomeDoTipo;
    }

    public String getNome()       { return nome; }
    public Categoria getCategoria() { return categoria; }
    public TipoLA getTipo()       { return tipo; }
    public String getNomeDoTipo() { return nomeDoTipo; }

    @Override
    public String toString() {
        return "Simbolo{nome='" + nome + "', categoria=" + categoria
                + ", tipo=" + tipo
                + (nomeDoTipo != null ? ", nomeDoTipo='" + nomeDoTipo + "'" : "")
                + "}";
    }
}
