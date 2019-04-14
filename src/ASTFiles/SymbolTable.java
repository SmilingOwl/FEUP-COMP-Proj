import java.util.HashMap;
/*
Estrutura da Symbol Table:
 - name: nome da função / classe à qual pertence a tabela de símbolos
 - type: tipo da função / classe (não sei se é necessário, já não me lembro do meu objetivo quando a criei, 
 mas se depois não usarmos apaga-se...)
 - parent: se for função, a que classe pertence. Serve para a análise sintática: quando estamos a analisar um statement
 queremos saber se uma variável existe não só na tabela de símbolos atual, mas também nas dos scopes anteriores a essa.
 - symbols: nome - tipo (o stor tinha sugerido usar I para int e isso, mas para já ainda só está guardar literalmente
 int, porque não sei bem como fazer no caso dos tipos inventados por nós, tipo myClass2 no caso do Example.txt)

 Na classe ProjectClassVisitor, está uma lista de tabelas de símbolos, para podermos fazer print de todas, e
o argumento currentTable que indica em que tabela se está no momento da visita. ou seja, quando estiverem a fazer
código numa das funções visit() do nó que vocês quiserem, a tabela de símbolos à qual é preciso aceder para análise
semântica, por exemplo, é a que está em currentTable.
*/
public class SymbolTable {
    private String name;
    private String type; //class, method, main
    private SymbolTable parent;
    private HashMap<String, String> symbols;

    public SymbolTable(String name, String type, SymbolTable parent) {
        this.symbols = new HashMap<String, String>();
        this.name = name;
        this.type = type;
        this.parent = parent;
    }

    public String get_name() {
        return this.name;
    }

    public String get_type() {
        return this.type;
    }
    
    public HashMap<String, String> get_symbols() {
        return this.symbols;
    }

    public SymbolTable get_parent() {
        return this.parent;
    }

    public void print() {
        System.out.println("- My name is " + this.name + " -");
        for(String key : symbols.keySet()) {
            System.out.println(key + " - " + symbols.get(key));
        }
        if(this.parent != null)
            System.out.println("- My parent's name is " + parent.get_name() + " -");
        else
            System.out.println("- I have no parent -");
    }
}