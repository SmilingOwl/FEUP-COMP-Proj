import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ProjectClassVisitor implements ProjectVisitor {
    private ArrayList<SymbolTable> symbolTables;
    private SymbolTable currentTable;
    private LinkedList stack = new LinkedList();
    private FileWriter writer;
    private boolean show_semantic_analysis = false;
    private boolean show_code_generation = true;

    public ProjectClassVisitor(ArrayList<SymbolTable> symbolTables) {
        this.symbolTables = symbolTables;
        try {
            File file = new File("myfile.j");
            if (file.exists()) 
                file.delete();//delete if exists
            writer = new FileWriter(file); 
            /* 
            writer.write("exemplo de como escrever no ficheiro");
            writer.flush();
            */
        } 
        catch (IOException e) {
            System.out.println("Something went wrong on ProjectClassVisitor Constructor.");
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
        try {
            this.writer.write(".class public " + name + "\n");
            this.writer.write(".super java/lang/Object\n\n");
            this.writer.flush();
            node.childrenAccept(this, data);
        } 
        catch (IOException e) {
            System.out.println("Something went wrong on visit(ASTClassDeclaration) Constructor.");
        }
        finally{
            try {
                this.writer.close();
            } catch (IOException e) {
                System.out.println("Something went wrong while closing the file.");
            }
        }
        return data;
    }

    public Object visit(ASTVarDeclaration node, Object data) {
        String type = "", name = "";
        if (node.jjtGetNumChildren() == 2) {
            if (node.jjtGetChild(0) instanceof ASTType) {
                ASTType new_node = (ASTType) node.jjtGetChild(0);
                type = new_node.getName();
            } else if (node.jjtGetChild(0) instanceof ASTTypeWoIdent) {
                ASTTypeWoIdent new_node = (ASTTypeWoIdent) node.jjtGetChild(0);
                type = new_node.getName();
            }
            if (node.jjtGetChild(1) instanceof ASTIdentifier) {
                ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(1);
                name = new_node.getName();
            }
        }
        if (!type.equals("int") && !type.equals("boolean") && this.currentTable.exists(name) != null) {
            boolean found = false;
            for (int i = 0; i < this.symbolTables.size(); i++) {
                if (this.symbolTables.get(i).get_name().equals(type)) {
                    found = true;
                    break;
                }
            }
            if (!found && show_semantic_analysis) {
                System.out.println("Semantic Error: Class " + type + " doesn't exist");
                System.exit(-1);
            }
        }
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTVarDeclarationWoIdent node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTMainDeclaration node, Object data) {
        this.currentTable = this.currentTable.get_functions().get("main");
        try {
            this.writer.write("; default constructor\n");
            this.writer.write(".method public <init>()V\n");
            node.childrenAccept(this, data);
            //TODO: nao sei bem como fazer aqui
            this.writer.write("\taload_0\n"); 
            this.writer.write("\tinvokespecial java/lang/Object/<init>()V\n");
            this.writer.write("\treturn\n");
            this.writer.write(".end method\n\n");
            this.writer.flush();
        } 
        catch (IOException e) {
            System.out.println("Something went wrong on visit(ASTClassDeclaration) Constructor.");
        }
        node.childrenAccept(this, data);
        this.currentTable = this.currentTable.get_parent();
        return data;
    }

    public Object visit(ASTMethodDeclaration node, Object data) {
        this.currentTable = this.currentTable.get_functions().get(node.getName());
        
        try {
            this.writer.write(".method public" + node.getName() + "("); //TODO: ver a parte do public
            /* this.writer.write("\t.limit stack 3\n"); //TODO: usar .limit de forma dinamica 
            this.writer.write("\t.limit locals 4\n"); */
            node.childrenAccept(this, data);
            this.writer.write(".end method\n\n");
            this.writer.flush();
        } 
        catch (IOException e) {
            System.out.println("Something went wrong on visit(ASTClassDeclaration) Constructor.");
        }
        this.currentTable = this.currentTable.get_parent();
        return data;
    }

    public Object visit(ASTReturn node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTMethodArgs node, Object data) {
        node.childrenAccept(this, data);
        try {
            this.writer.write(")\n");
            this.writer.flush();
        } 
        catch (IOException e) {
            System.out.println("Something went wrong on visit(ASTReturn) Constructor.");
        }
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
        if (node.get_type().equals("VarDeclaration")) {
            String type = "", name = "";
            if (node.jjtGetNumChildren() == 2) {
                if (node.jjtGetChild(0) instanceof ASTIdentifier) {
                    ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(0);
                    type = new_node.getName();
                }
                if (node.jjtGetChild(1) instanceof ASTStatementAux2) {
                    ASTStatementAux2 new_node = (ASTStatementAux2) node.jjtGetChild(1);
                    if (new_node.jjtGetNumChildren() == 0 && new_node.getName() != null) {
                        name = new_node.getName();
                    }
                }
            }
            if (this.currentTable.exists(name) != null) {
                boolean found = false;
                for (int i = 0; i < symbolTables.size(); i++) {
                    if (symbolTables.get(i).get_name().equals(type)) {
                        found = true;
                        break;
                    }
                }
                if (!found && show_semantic_analysis) {
                    System.out.println("Semantic Error: Class " + type + " doesn't exist");
                    System.exit(-1);
                }
            }
        }
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
            
            try {   
                if(LHS_instOf){//Push para a stack
                    this.writer.write("\ticonst_" + extractLabel(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString()) + "\n");
                }
                if(RHS_instOf){ //Push para a stack
                    this.writer.write("\ticonst_" + extractLabel(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).toString()) + "\n");
                }
                this.writer.write("\tiadd\n");
            }
            catch (IOException e) {
                System.out.println("Something went wrong on visit(ASTADD) Constructor.");
            }
        }

        return data;
    }

    public Object visit(ASTSUB node, Object data) {
        node.childrenAccept(this, data);

        // Code generation

        if (show_code_generation) {

            boolean LHS_instOf = node.jjtGetChild(0) instanceof ASTExpressionRestOfClauses;  //TODO: Check for ASTExpressionRestOfClausesWoIdent ?
            boolean RHS_instOf = node.jjtGetChild(1) instanceof ASTExpressionRestOfClauses;

            try {
                if(LHS_instOf){//Push para a stack
                    this.writer.write("\ticonst_" + extractLabel(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString()) + "\n");
                }
                if(RHS_instOf){ //Push para a stack
                    this.writer.write("\ticonst_" + extractLabel(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).toString()) + "\n");
                }
                this.writer.write("\tisub\n");
            } 
            catch (IOException e) {
                System.out.println("Something went wrong on visit(ASTSUB) Constructor.");
            }
        }

        return data;
    }

    public Object visit(ASTMULT node, Object data) {
        node.childrenAccept(this, data);

        // Code generation

        if (show_code_generation) {

            boolean LHS_instOf = node.jjtGetChild(0) instanceof ASTExpressionRestOfClauses;  //TODO: Check for ASTExpressionRestOfClausesWoIdent ?
            boolean RHS_instOf = node.jjtGetChild(1) instanceof ASTExpressionRestOfClauses;
            try {
                if(LHS_instOf){//Push para a stack
                    this.writer.write("\ticonst_" + extractLabel(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString()) + "\n");
                }
                if(RHS_instOf){ //Push para a stack
                    this.writer.write("\ticonst_" + extractLabel(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).toString()) + "\n");
                }
                this.writer.write("\timult\n");
            } 
            catch (IOException e) {
                System.out.println("Something went wrong on visit(ASTMULT) Constructor.");
            }

        }

        return data;
    }

    public Object visit(ASTDIV node, Object data) {
        node.childrenAccept(this, data);

        // Code generation

        if (show_code_generation) {

            boolean LHS_instOf = node.jjtGetChild(0) instanceof ASTExpressionRestOfClauses;  //TODO: Check for ASTExpressionRestOfClausesWoIdent ?
            boolean RHS_instOf = node.jjtGetChild(1) instanceof ASTExpressionRestOfClauses;
            try {
                if(LHS_instOf){//Push para a stack
                    this.writer.write("\ticonst_" + extractLabel(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString()) + "\n");
                }
                if(RHS_instOf){ //Push para a stack
                    this.writer.write("\ticonst_" + extractLabel(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).toString()) + "\n");
                }
                this.writer.write("\tidiv\n");
            } 
            catch (IOException e) {
                System.out.println("Something went wrong on visit(ASTDIV) Constructor.");
            }
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
                    if (type == null && show_semantic_analysis) {
                        System.out.println("Semantic Error: Variable " + new_id_node.getName() + " doesn't exist.");
                        System.exit(-1);
                    } else if (node.jjtGetChild(1).jjtGetChild(0) instanceof ASTExpressionAuxDot) {
                        ASTExpressionAuxDot new_dot_node = (ASTExpressionAuxDot) node.jjtGetChild(1).jjtGetChild(0);
                        if (new_dot_node.getName().equals("length") && !type.equals("int[]")
                                && show_semantic_analysis) {
                            System.out.println("Semantic Error: \"length\" only appliable to int[]. In variable "
                                    + new_id_node.getName() + ".");
                            System.exit(-1);
                        } else if (!new_dot_node.getName().equals("length") && show_semantic_analysis
                                && (type.equals("int") || type.equals("boolean") || type.equals("int[]"))) {
                            System.out.println(
                                    "Semantic Error: primitive type can't be called with functions. In variable "
                                            + new_id_node.getName() + ".");
                            System.exit(-1);
                        } else if (!new_dot_node.getName().equals("length")) {
                            boolean found = false;
                            for (int i = 0; i < this.symbolTables.size(); i++) {
                                if (this.symbolTables.get(i).get_name().equals(type)) {
                                    found = true;
                                    SymbolTable table = this.symbolTables.get(i).get_functions()
                                            .get(new_dot_node.getName());
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
                            if (!found) {
                                System.out.println("Semantic Error: Class " + type + " doesn't exist.");
                                System.exit(-1);
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
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTAcessing node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTAccessingArrayAt node, Object data) {
        String name = null;
        String type = null;
        node.childrenAccept(this, data);

        if (node.jjtGetNumChildren() == 1) {

            if (node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier) {

                ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0);
                name = new_node.getName();
                type = this.currentTable.exists(name);

                if (!type.equals("int") && show_semantic_analysis)
                    System.out.println("Error!");
            }

        } else {
            // expression...
        }
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
        if (type.equalsIgnoreCase("int")){
            return upercase ? "I" : "i";
        }
        return null;
    }
}