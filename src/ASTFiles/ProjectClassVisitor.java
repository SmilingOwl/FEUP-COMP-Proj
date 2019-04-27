import java.util.ArrayList;
import java.util.LinkedList;

public class ProjectClassVisitor implements ProjectVisitor{
  private ArrayList<SymbolTable> symbolTables;
  private SymbolTable currentTable;
  private LinkedList stack = new LinkedList();
  private boolean show_semantic_analysis = false;
  private boolean show_code_generation = true;

    public ProjectClassVisitor(ArrayList<SymbolTable> symbolTables) {
        this.symbolTables = symbolTables;
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
        node.childrenAccept(this, data);
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
        node.childrenAccept(this, data);
        this.currentTable = this.currentTable.get_parent();
        return data;
    }

    public Object visit(ASTMethodDeclaration node, Object data) {
        this.currentTable = this.currentTable.get_functions().get(node.getName());
        node.childrenAccept(this, data);
        this.currentTable = this.currentTable.get_parent();
        return data;
    }

    public Object visit(ASTReturn node, Object data) {
        node.childrenAccept(this, data);
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
                if(new_node.jjtGetNumChildren() != 0 && new_node.jjtGetChild(0) instanceof ASTIdentifier) {
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

    public void aritmaticOps(String op, Node node){
/*        if(in_node instanceof ASTADD){
            in_node = (ASTADD) in_node;
        }*/


            //boolean LHS_instOf = node.jjtGetChild(0) instanceof ASTExpressionRestOfClauses;  //TODO: Check for ASTExpressionRestOfClausesWoIdent ?
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
            case 0:
                System.out.println("iconst_" + extractLabel(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString()));
                break;
            case 1:
                System.out.println("iload " + extractLabel(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString()));
                break;
            case 2:
                System.out.println("calling " + node.jjtGetChild(1).jjtGetChild(1).jjtGetChild(0));
                break;
        
            default:
                //System.out.println("DEBUG: ENTERED DEFAULT");
                break;
        }

        switch (valRight) {
            case 0:
                System.out.println("iconst_" + extractLabel(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).toString()));
                break;
            case 1:
                System.out.println("iload " + extractLabel(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).toString()));
                break;
        
            default:
                //System.out.println("DEBUG: ENTERED DEFAULT");
                break;
        }

            /*
            if(LHS_instOf){//Push para a stack
                System.out.println("iconst_" + extractLabel(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString()));
            }
            if(RHS_instOf){ //Push para a stack
                System.out.println("iconst_" + extractLabel(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).toString()));
            }
            System.out.println("iadd");
            */
        switch (op) {
            case "add":
                System.out.println("iadd");
                break;

            case "sub":
                System.out.println("isub");
                break;

            case "mult":
                System.out.println("imult");
                break;

            case "div":
                System.out.println("idiv");
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
}