import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

public class ProjectClassVisitor implements ProjectVisitor {
    private ArrayList<SymbolTable> symbolTables;
    private SymbolTable currentTable;
    private LinkedList stack = new LinkedList();
    private String inMethod = "";
    private FileWriter writer;
    private boolean show_semantic_analysis = true;
    private boolean show_code_generation = true;

    public ProjectClassVisitor(ArrayList<SymbolTable> symbolTables) {
        this.symbolTables = symbolTables;
        if (show_code_generation) {
            try {
                File file = new File("myfile.j");
                if (file.exists())
                    file.delete();// delete if exists
                writer = new FileWriter(file);
                /*
                * writer.write("exemplo de como escrever no ficheiro"); writer.flush();
                */
            } catch (IOException e) {
                System.out.println("Something went wrong on ProjectClassVisitor Constructor [CODE GENERATION].");
            }
        }

    }

    public Object defaultVisit(SimpleNode node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(SimpleNode node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTIntegerLiteral node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTIdentifier node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTProgram node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTClassDeclaration node, Object data) {
        String name = node.getName();
        SymbolTable table = new SymbolTable(name, "class", null);
        for (int i = 0; i < this.symbolTables.size(); i++) {
            if (this.symbolTables.get(i).get_name().equals(name)) {
                this.currentTable = this.symbolTables.get(i);
            }
        }
        if (this.currentTable == null) {
            System.out.println("Symbol Table " + name + " not found.");
            return null;
        }
        if (show_code_generation) {
            try {
                this.writer.write(".class public " + name + "\n");
                this.writer.write(".super java/lang/Object\n");
                if (!this.currentTable.get_symbols().isEmpty()){
                    this.writer.write("\n");
                    for( String key : this.currentTable.get_symbols().keySet() )
                        this.writer.write(".field public " + key + " " + this.getJasminType(this.currentTable.get_symbols().get(key), true) + "\n");
                }
                this.writer.write("\n; default constructor\n");
                this.writer.write(".method public <init>()V\n");
                this.writer.write("\taload_0\n"); 
                this.writer.write("\tinvokespecial java/lang/Object/<init>()V\n");
                this.writer.write("\treturn\n");
                this.writer.write(".end method\n\n");
                this.writer.flush(); 
                node.childrenAccept(this, data);
            } 
            catch (IOException e) {
                System.out.println("Something went wrong on visit(ASTClassDeclaration) Constructor [CODE GENERATION].");
            }
            finally{
                try {
                    this.writer.close();
                    
                } catch (IOException e) {
                    System.out.println("Something went wrong while closing the file [CODE GENERATION].");
                }
            }
        }
        else node.childrenAccept(this, data);
        
        return data;
    }

    public Object visit(ASTVarDeclaration node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTVarDeclarationWoIdent node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTMainDeclaration node, Object data) {
        this.currentTable = this.currentTable.get_functions().get("main");
        if (show_code_generation) {
            try {
                this.writer.write(".method public static main([Ljava/lang/String;)V\n");
                node.childrenAccept(this, data);
                this.writer.write(".end method\n\n");
                this.writer.flush();
            } 
            catch (IOException e) {
                System.out.println("Something went wrong on visit(ASTClassDeclaration) Constructor [CODE GENERATION].");
            }
        }
        node.childrenAccept(this, data);
        this.currentTable = this.currentTable.get_parent();
        return data;
    }

    public Object visit(ASTMethodDeclaration node, Object data) {
        this.currentTable = this.currentTable.get_functions().get(node.getName());

        if (show_code_generation) {
            String methodReturnType = this.getJasminType(this.currentTable.get_return_type(), true);
            try {
                this.writer.write(".method public " + node.getName() + "(");
                for (String arg : this.currentTable.get_args().values())
                    this.writer.write(this.getJasminType(arg, true));
                this.writer.write(")" + methodReturnType + "\n");
                node.childrenAccept(this, data);
                this.writer.write("\t.limit stack " + this.stack.size() + "\n"); //TODO: usar .limit de forma dinamica 
                this.writer.write("\t.limit locals " + this.currentTable.get_symbols().size() + "\n\n");
                this.writer.write(this.inMethod);
                this.writer.write("\tireturn\n");
                this.writer.write(".end method\n\n");
                this.writer.flush();
            } 
            catch (IOException e) {
                System.out.println("Something went wrong on visit(ASTClassDeclaration) Constructor [CODE GENERATION].");
            }
            this.inMethod = "";
        } else node.childrenAccept(this, data);

        this.currentTable = this.currentTable.get_parent();
        return data;
    }

    public Object visit(ASTReturn node, Object data) {
        node.childrenAccept(this, data);
        /* this.inMethod += "\t" + getJasminType(node.jjtGetChild(0).toString(), false) + "return\n"; */
        return data;
    }

    public Object visit(ASTMethodArgs node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTArgument node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTMainMethodBody node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTType node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTIsArray node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTTypeWoIdent node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTCondition node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTIfBody node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTElseBody node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTIfElseStatement node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTWhileStatement node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTWhileBody node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTStatementStartIdent node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTAND node, Object data) {
        String type = null;
        String name = null;
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTMINOR node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTADD node, Object data) {
        node.childrenAccept(this, data);

        // Code generation

        if (show_code_generation) {

            boolean LHS_instOf = node.jjtGetChild(0) instanceof ASTExpressionRestOfClauses;  //TODO: Check for ASTExpressionRestOfClausesWoIdent ?
            boolean RHS_instOf = node.jjtGetChild(1) instanceof ASTExpressionRestOfClauses;
            
            if(LHS_instOf){//Push para a stack
                this.inMethod += "\ticonst_" + extractLabel(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString()) + "\n";
            }
            if(RHS_instOf){ //Push para a stack
                this.inMethod += "\ticonst_" + extractLabel(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).toString()) + "\n";
            }
            this.inMethod += "\tiadd\n";
            
        }

        return data;
    }

    public Object visit(ASTSUB node, Object data) {
        node.childrenAccept(this, data);

        // Code generation

        if (show_code_generation) {

            boolean LHS_instOf = node.jjtGetChild(0) instanceof ASTExpressionRestOfClauses;  //TODO: Check for ASTExpressionRestOfClausesWoIdent ?
            boolean RHS_instOf = node.jjtGetChild(1) instanceof ASTExpressionRestOfClauses;

            if(LHS_instOf){//Push para a stack
                this.inMethod += "\ticonst_" + extractLabel(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString()) + "\n";
            }
            if(RHS_instOf){ //Push para a stack
                this.inMethod += "\ticonst_" + extractLabel(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).toString()) + "\n";
            }
            this.inMethod += "\tisub\n";
        }
        
        return data;
    }

    public Object visit(ASTMULT node, Object data) {
        node.childrenAccept(this, data);

        // Code generation

        if (show_code_generation) {

            boolean LHS_instOf = node.jjtGetChild(0) instanceof ASTExpressionRestOfClauses;  //TODO: Check for ASTExpressionRestOfClausesWoIdent ?
            boolean RHS_instOf = node.jjtGetChild(1) instanceof ASTExpressionRestOfClauses;

            if(LHS_instOf){//Push para a stack
                this.inMethod += "\ticonst_" + extractLabel(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString()) + "\n";
            }
            if(RHS_instOf){ //Push para a stack
                this.inMethod += "\ticonst_" + extractLabel(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).toString()) + "\n";
            }
            this.inMethod += "\timult\n";
        }
        
        return data;
    }

    public Object visit(ASTDIV node, Object data) {
        node.childrenAccept(this, data);

        // Code generation

        if (show_code_generation) {

            boolean LHS_instOf = node.jjtGetChild(0) instanceof ASTExpressionRestOfClauses;  //TODO: Check for ASTExpressionRestOfClausesWoIdent ?
            boolean RHS_instOf = node.jjtGetChild(1) instanceof ASTExpressionRestOfClauses;

            if(LHS_instOf){//Push para a stack
                this.inMethod += "\ticonst_" + extractLabel(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString()) + "\n";
            }
            if(RHS_instOf){ //Push para a stack
                this.inMethod += "\ticonst_" + extractLabel(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).toString()) + "\n";
            }
            this.inMethod += "\tidiv\n";
        }
        
        return data;
    }

    public Object visit(ASTEQUAL node, Object data) {
        node.childrenAccept(this, data);

        if (show_code_generation) {
            /*
            System.out.println("\nAssign: ");
            System.out.println("\t" + node.jjtGetChild(0));
            System.out.println("\t" + node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).getClass());
            */
        }
        return data;
    }

    public Object visit(ASTStatementAux2 node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTCalling node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTExpressionRestOfClauses node, Object data) {
        if (node.jjtGetNumChildren() == 2) {
            if (node.jjtGetChild(0) instanceof ASTExpressionToken) {
                ASTExpressionToken new_node = (ASTExpressionToken) node.jjtGetChild(0);
                if (new_node.jjtGetNumChildren() != 0 && new_node.jjtGetChild(0) instanceof ASTIdentifier) {
                    ASTIdentifier new_id_node = (ASTIdentifier) new_node.jjtGetChild(0);
                    String type = this.currentTable.exists(new_id_node.getName());
                    if (node.jjtGetChild(1).jjtGetChild(0) instanceof ASTExpressionAuxDot) {
                        ASTExpressionAuxDot new_dot_node = (ASTExpressionAuxDot) node.jjtGetChild(1).jjtGetChild(0);
                        if (!new_dot_node.getName().equals("length") && show_semantic_analysis
                                && type != null && (type.equals("int") || type.equals("boolean") || type.equals("int[]"))) {
                            System.out.println("Semantic Error: primitive type can't be called with functions. In variable "
                                            + new_id_node.getName() + ".");
                            System.exit(-1);
                        } else if (!new_dot_node.getName().equals("length") && type != null) {
                            boolean found = false;
                            for (int i = 0; i < this.symbolTables.size(); i++) {
                                if (this.symbolTables.get(i).get_name().equals(type)) {
                                    found = true;
                                    SymbolTable table = this.symbolTables.get(i).get_functions().get(new_dot_node.getName());
                                    if (table != null) {
                                        if (table.get_args().size() != new_dot_node.jjtGetNumChildren()
                                                && show_semantic_analysis) {
                                            System.out.println("Semantic Error: Wrong number of arguments in function "
                                                    + new_dot_node.getName());
                                            System.exit(-1);
                                        } 
                                    } else if (show_semantic_analysis) {
                                        System.out.println("Semantic Error: Function " + new_dot_node.getName()
                                                + " of class " + new_id_node.getName() + " doesn't exist.");
                                        System.exit(-1);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTExpressionRestOfClausesWoIdent node, Object data) {
        if (node.jjtGetNumChildren() == 2) {
            if (node.jjtGetChild(0) instanceof ASTExpressionToken) {
                ASTExpressionToken new_node = (ASTExpressionToken) node.jjtGetChild(0);
                if (new_node.jjtGetNumChildren() != 0 && new_node.jjtGetChild(0) instanceof ASTIdentifier) {
                    ASTIdentifier new_id_node = (ASTIdentifier) new_node.jjtGetChild(0);
                    String type = this.currentTable.exists(new_id_node.getName());
                    if (node.jjtGetChild(1).jjtGetChild(0) instanceof ASTExpressionAuxDot) {
                        ASTExpressionAuxDot new_dot_node = (ASTExpressionAuxDot) node.jjtGetChild(1).jjtGetChild(0);
                        if (!new_dot_node.getName().equals("length") && show_semantic_analysis
                                && type != null && (type.equals("int") || type.equals("boolean") || type.equals("int[]"))) {
                            System.out.println("Semantic Error: primitive type can't be called with functions. In variable "
                                            + new_id_node.getName() + ".");
                            System.exit(-1);
                        } else if (!new_dot_node.getName().equals("length") && type != null) {
                            boolean found = false;
                            for (int i = 0; i < this.symbolTables.size(); i++) {
                                if (this.symbolTables.get(i).get_name().equals(type)) {
                                    found = true;
                                    SymbolTable table = this.symbolTables.get(i).get_functions().get(new_dot_node.getName());
                                    if (table != null) {
                                        if (table.get_args().size() != new_dot_node.jjtGetNumChildren()
                                                && show_semantic_analysis) {
                                            System.out.println("Semantic Error: Wrong number of arguments in function "
                                                    + new_dot_node.getName());
                                            System.exit(-1);
                                        }
                                    } else if (show_semantic_analysis) {
                                        System.out.println("Semantic Error: Function " + new_dot_node.getName()
                                                + " of class " + new_id_node.getName() + " doesn't exist.");
                                        System.exit(-1);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTAcessing node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTAccessingArrayAt node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTExpressionAuxDot node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTExpressionToken node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTExpressionTokenWoIdent node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTExpressionNew node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    /*
     * public Object pop(){ return stack.removeFirst(); }
     * 
     * public void push(Object o){ stack.addFirst(o); }
     */

    public String extractLabel(String input){
        int i = input.indexOf(":");
        if(i != -1)
            return input.substring(i+2);
        return input;
    }

    public String getJasminType(String type, boolean upercase){
        if (type.equalsIgnoreCase("int") || type.equalsIgnoreCase("Type: int")){
            return upercase ? "I" : "i";
        }
        else if (type.equalsIgnoreCase("boolean")){
            return upercase ? "Z" : "i";
        }
        else if (type.equalsIgnoreCase("int[]")){
            return upercase ? "Z" : "i";
        }
        return "";
    }
}