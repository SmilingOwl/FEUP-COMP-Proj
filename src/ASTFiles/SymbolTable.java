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
    private ArrayList<SymbolTable> functions;
    private String return_type;

    public SymbolTable(String name, String type, SymbolTable parent) {
        this.symbols = new LinkedHashMap<String, String>();
        this.args = new LinkedHashMap<String, String>();
        this.functions = new ArrayList<SymbolTable>();
        this.name = name;
        this.type = type;
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

    public SymbolTable get_parent() {
        return this.parent;
    }
    
    public LinkedHashMap<String, String> get_symbols() {
        return this.symbols;
    }

    public LinkedHashMap<String, String> get_args() {
        return this.args;
    }

    public ArrayList<SymbolTable> get_functions() {
        return this.functions;
    }

    public void set_return_type(String ret_type) {
        this.return_type = ret_type;
    }

    public String exists(String n) {
        SymbolTable checking = this;
        while(checking != null){
            if(checking.get_symbols().get(n) != null)
                return checking.get_symbols().get(n);
            if(checking.get_args().get(n) != null)
                return checking.get_args().get(n);
            checking = checking.parent;
        }
        return null;
    }

    public SymbolTable get_functions_key(String name) {
        for(int i = 0; i < this.functions.size(); i++) {
            if(this.functions.get(i).get_name().equals(name)) {
                return this.functions.get(i);
            }
        }
        return null;
    }

    public void print() {
        if(this.type.equals("class"))
            System.out.println("\n\n\nclass@" + this.name);
        else 
            System.out.println("\n\n\nfunction@" + this.name);

        if(this.type.equals("class") && this.functions.size() > 0) {
            System.out.println("\n\n" + this.name + "@functions");
            ArrayList<String> appeared = new ArrayList<String>();
            for(int i = 0; i < this.functions.size(); i++) {
                System.out.println(" - " + this.functions.get(i).get_name() + " -> Type: " 
                                    + this.functions.get(i).get_return_type());
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

        for(int i = 0; i < this.functions.size(); i++){
            this.functions.get(i).print();
        }
    }
}