import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

public class ProjectClassVisitor implements ProjectVisitor {
    private ArrayList<SymbolTable> symbolTables;
    private SymbolTable currentTable;
    private LinkedList stack = new LinkedList();
    private ArrayList<String> localVarsList = new ArrayList<String>(){{add("this");}};
    private String inMethod = "";
    private FileWriter writer;
    private boolean show_semantic_analysis = true;
    private boolean show_code_generation = true;

    public ProjectClassVisitor(ArrayList<SymbolTable> symbolTables) {
        this.symbolTables = symbolTables;
        if (show_code_generation) {
            try {
                File file = new File("JVM.j");
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
                        this.writer.write(".field private " + key + " " + this.getJasminType(this.currentTable.get_symbols().get(key), true) + "\n");
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

        if(show_code_generation){
            //Add var to list //TODO: Check if custom types go to a different list
            localVarsList.add(extractLabel(node.jjtGetChild(1).toString()));
        }

        return data;
    }

    public Object visit(ASTVarDeclarationWoIdent node, Object data) {
        node.childrenAccept(this, data);

        if(show_code_generation){
            //Add var to list
            localVarsList.add(extractLabel(node.jjtGetChild(1).toString()));
        }

        return data;
    }

    public Object visit(ASTMainDeclaration node, Object data) {
        this.currentTable = this.currentTable.get_functions().get("main");
        if (show_code_generation) {
            try {
                this.writer.write(".method public static main([Ljava/lang/String;)V\n");
                node.childrenAccept(this, data);
                this.writer.write("\t.limit stack " + this.stack.size() + "\n"); //TODO: usar .limit de forma dinamica 
                this.writer.write("\t.limit locals " + this.currentTable.get_symbols().size() + "\n\n");
                this.writer.write(this.inMethod);
                this.writer.write("\treturn\n");
                this.writer.write(".end method\n\n");
                this.writer.flush();
            } 
            catch (IOException e) {
                System.out.println("Something went wrong on visit(ASTClassDeclaration) Constructor [CODE GENERATION].");
            }
            this.inMethod = "";
            //TODO: nao esquecer de limpar a stack
        } else node.childrenAccept(this, data);
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
            //TODO: nao esquecer de limpar a stack
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
        if(show_code_generation && node.jjtGetChild(0) instanceof ASTIdentifier) {
            ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(0);
            if (node.toString().equalsIgnoreCase("VarDeclaration ")){
                this.inMethod += "\tinvokenonvirtual " + new_node.getName() + "/<init>()V";
                node.childrenAccept(this, data);
            }
            else {
                this.inMethod += "\tinvokevirtual " + new_node.getName() + "/";
                node.childrenAccept(this, data);
                this.inMethod = this.inMethod.substring(0, this.inMethod.length() - 1);
            }
            this.inMethod += "\n";
        }
        else node.childrenAccept(this, data);
        
        
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
            aritmaticOps("add", node);
        }

        return data;
    }

    public Object visit(ASTSUB node, Object data) {
        node.childrenAccept(this, data);

        // Code generation

        if (show_code_generation) {
            aritmaticOps("sub", node);
        }
        
        return data;
    }

    public Object visit(ASTMULT node, Object data) {
        node.childrenAccept(this, data);

        // Code generation

        if (show_code_generation) {
            aritmaticOps("mult", node);
        }
        
        return data;
    }

    public Object visit(ASTDIV node, Object data) {
        node.childrenAccept(this, data);

        // Code generation

        if (show_code_generation) {
            aritmaticOps("div", node);
        }
        
        return data;
    }

    public Object visit(ASTEQUAL node, Object data) {
        node.childrenAccept(this, data);

        if (show_code_generation) {
            
            //System.out.println("\nStart: \n");
            //investigateNode(node, 0);

            if(node.jjtGetChild(1) instanceof ASTExpressionRestOfClauses){
                this.inMethod += ("\ticonst_" + extractLabel(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).toString()) + "\n");
            }
            
            this.inMethod += ("\tistore_" + indexLocal(extractLabel(node.jjtGetChild(0).toString())) + "\n");
               
        }
        return data;
    }

    public Object visit(ASTStatementAux2 node, Object data) {
        /* System.out.println(node.jjtGetParent()); */

        /* else if (node.jjtGetParent() instanceof ASTStatementStartIdent){
            System.out.println('x');
        } */

        /* System.out.println(node.jjtGetParent() + "aii\n");
        System.out.println(node.getName() + "\n"); */
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
        if (show_code_generation)
            this.inMethod += node.jjtGetValue()  + "/";/* hmmm nao sei se isto esta bem */
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTExpressionAuxDot node, Object data) {
        if (show_code_generation)
            this.inMethod += node.getName()  + "()/";
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTExpressionToken node, Object data) {
        /* if(node.jjtGetParent() instanceof ASTCalling)
            System.out.println('x'); */
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTExpressionTokenWoIdent node, Object data) {
        /* if(node.jjtGetParent() instanceof ASTCalling) */
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTExpressionNew node, Object data) {
        if (show_code_generation && node.jjtGetChild(0) instanceof ASTIdentifier){
            ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(0);
            this.inMethod += ("\tnew " + new_node.getName() + "\n");    
        }
        node.childrenAccept(this, data);
        return data;
    }

    /*
     * public Object pop(){ return stack.removeFirst(); }
     * 
     * public void push(Object o){ stack.addFirst(o); }
     */

    public void aritmaticOps(String op, Node node){

            //boolean LHS_instOf = node.jjtGetChild(0) instanceof ASTExpressionRestOfClauses; 
            //boolean RHS_instOf = node.jjtGetChild(1) instanceof ASTExpressionRestOfClauses;
/*
            System.out.println("Adding");

            System.out.println(node.jjtGetChild(0).getClass());
            System.out.println(node.jjtGetChild(1).getClass());
            System.out.println(node.jjtGetChild(0).jjtGetChild(0).getClass());
            System.out.println(node.jjtGetChild(1).jjtGetChild(0).getClass());
            System.out.println(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).getClass());
            System.out.println(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).getClass());
            System.out.println(node.jjtGetNumChildren());
            System.out.println(node.jjtGetChild(0).jjtGetNumChildren());
            System.out.println(node.jjtGetChild(1).jjtGetNumChildren());
            if(node.jjtGetChild(1).jjtGetNumChildren() == 2){
                System.out.println("IF");
                System.out.println(node.jjtGetChild(1).jjtGetChild(0));
                System.out.println(node.jjtGetChild(1).jjtGetChild(1).jjtGetChild(0));
            }
            */



        int valLeft = -1;
        int valRight = -1;

        //Check all possible cases

        if((node.jjtGetChild(0) instanceof ASTExpressionRestOfClauses) && (node.jjtGetChild(0).jjtGetNumChildren() == 1)){
            if(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier){
                valLeft = 1;
            }
            else{
                valLeft = 0;
            }   
        }


        if((node.jjtGetChild(1) instanceof ASTExpressionRestOfClauses) && (node.jjtGetChild(1).jjtGetNumChildren() == 1)){
            if(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier){
                valRight = 1;
            }
            else{
                valRight = 0;
            }   
        }
        
        switch (valLeft) {
            case 0://Push para a stack
                this.inMethod += ("\ticonst_" + extractLabel(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString()) + "\n");
                break;
            case 1:
                this.inMethod += ("\tiload_" + indexLocal(extractLabel(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString())) + "\n");
                break;
            case 2:
                //this.inMethod += ("\tcalling " + node.jjtGetChild(1).jjtGetChild(1).jjtGetChild(0) + "\n");
                break;
        
            default:
                //System.out.println("DEBUG: ENTERED DEFAULT");
                break;
        }

        switch (valRight) {
            case 0://Push para a stack
                this.inMethod += ("\ticonst_" + extractLabel(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).toString()) + "\n");
                break;
            case 1:
                this.inMethod += ("\tiload_" + indexLocal(extractLabel(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).toString())) + "\n");
                break;
        
            default:
                //System.out.println("DEBUG: ENTERED DEFAULT");
                break;
        }


        switch (op) {
            case "add":
                this.inMethod += "\tiadd\n";
                break;

            case "sub":
                this.inMethod += "\tisub\n";
                break;

            case "mult":
                this.inMethod += "\timult\n";
                break;

            case "div":
                this.inMethod += "\tidiv\n";
                break;
        
            default:
                System.out.println("DEBUG: ENTERED OP DEFAULT");
                break;
        }

    }

    public String extractLabel(String input){
        int i = input.indexOf(":");
        if(i != -1)
            return input.substring(i+2);
        return input;
    }

    public int indexLocal(String varName){
        return localVarsList.indexOf(varName);
    }

    public void investigateNode(Node node, int depth){
        String t = "";
        for(int i = 0; i < depth; i++){
            t += "\t";
        }
        System.out.println();
        System.out.println(t + "In Node: " + node.getClass());
        System.out.println(t + "Value: " + node);
        for(int i = 0; i < node.jjtGetNumChildren(); i++){
            investigateNode(node.jjtGetChild(i), depth+1);
        }

    }

    public String getJasminType(String type, boolean upercase){
        if (type.equalsIgnoreCase("int") || type.equalsIgnoreCase("Type: int")){
            return upercase ? "I" : "i";
        }
        else if (type.equalsIgnoreCase("boolean")){
            return upercase ? "Z" : "i";
        }
        else if (type.equalsIgnoreCase("int[]")){
            return upercase ? "[I" : "";
        }
        return "";
    }
}