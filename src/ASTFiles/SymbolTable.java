import java.util.LinkedHashMap;
import java.util.ArrayList;
/*
Estrutura da Symbol Table:
 - name: nome da função / classe à qual pertence a tabela de símbolos
 - type: tipo da função / classe
 - parent: se for função, a que classe pertence
 - symbols: nome - tipo 
 - args: se for função, os seus argumentos
 - functions: se for classe, as suas funções
 - return_type: tipo de retorno (se for função)
 - scope: a que parte da função a tabela pertence (inicio, depois de ifs ou whiles, etc)

 Na classe ProjectClassVisitor, está uma lista de tabelas de símbolos, para podermos fazer print de todas, e
o argumento currentTable que indica em que tabela se está no momento da visita. ou seja, quando estiverem a fazer
código numa das funções visit() do nó que vocês quiserem, a tabela de símbolos à qual é preciso aceder para análise
semântica, por exemplo, é a que está em currentTable.
*/
public class SymbolTable {
    private String name;
    private String type; //class, method, main
    private SymbolTable parent;
    private LinkedHashMap<String, String> args;
    private LinkedHashMap<String, String> symbols;
    private LinkedHashMap<String, SymbolTable> functions;
    private String return_type;
    private int scope;

    public SymbolTable(String name, String type, SymbolTable parent) {
        this.symbols = new LinkedHashMap<String, String>();
        this.args = new LinkedHashMap<String, String>();
        this.functions = new LinkedHashMap<String, SymbolTable>();
        this.name = name;
        this.type = type;
        this.scope = 1;
        if(this.type.equals("main")) {
            this.return_type = "void";
        }
        this.parent = parent;
    }

    public String get_return_type() {
        return this.return_type;
    }

    public String get_name() {
        return this.name;
    }

    public String get_type() {
        return this.type;
    }

    public int get_scope() {
        return this.scope;
    }

    public SymbolTable get_parent() {
        return this.parent;
    }
    
    public LinkedHashMap<String, String> get_symbols() {
        return this.symbols;
    }

    public LinkedHashMap<String, String> get_args() {
        return this.args;
    }

    public LinkedHashMap<String, SymbolTable> get_functions() {
        return this.functions;
    }

    public void set_return_type(String ret_type) {
        this.return_type = ret_type;
    }

    public void set_scope(int scope) {
        this.scope = scope;
    }

    public String exists(String n) {
        SymbolTable checking = this;
        while(checking != null){
            if(checking.get_symbols().get(n) != null)
                return checking.get_symbols().get(n);
            if(checking.get_args().get(n) != null)
                return checking.get_args().get(n);
            for(String key : functions.keySet()) {
                if(this.functions.get(key).get_name().equals(n) && this.functions.get(key).get_scope() < this.scope){
                    checking = this.functions.get(key);
                    if(checking.get_symbols().get(n) != null)
                        return checking.get_symbols().get(n);
                    if(checking.get_args().get(n) != null)
                        return checking.get_args().get(n);
                }
            }
            checking = checking.parent;
        }
        return null;
    }

    public void print() {
        if(this.type.equals("class"))
            System.out.println("\n\n\nclass@" + this.name);
        else 
            System.out.println("\n\n\nfunction@" + this.name + " -> Scope: " + scope);

        if(this.type.equals("class") && this.functions.size() > 0) {
            System.out.println("\n\n" + this.name + "@functions");
            ArrayList<String> appeared = new ArrayList<String>();
            for(String key : functions.keySet()) {
                if(appeared.contains(this.functions.get(key).get_name()))
                    continue;
                System.out.println(" - " + this.functions.get(key).get_name() + " -> Type: " 
                                    + this.functions.get(key).get_return_type());
                appeared.add(this.functions.get(key).get_name());
            }
        }
        
        if(!args.isEmpty()) {
            System.out.println("\n\n" + this.name + "@arguments");
            for(String key : args.keySet()) {
                System.out.println(" - " + key + " -> Type: " + args.get(key));
            }
        }

        if(!symbols.isEmpty()) {
            System.out.println("\n\n" + this.name + "@symbols");
            for(String key : symbols.keySet()) {
                System.out.println(" - " + key + " -> Type: " + symbols.get(key));
            }
        }

        for(String key : functions.keySet()){
            functions.get(key).print();
        }
    }
}