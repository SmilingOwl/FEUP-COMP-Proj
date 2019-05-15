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
    //private LinkedList<ArrayList<String>> localVarsStack = new LinkedList<ArrayList<String>>(); TODO: Maybe delete
    private String inMethod = "";
    private FileWriter writer;
    private boolean show_semantic_analysis = true;
    private boolean show_code_generation = true;
    private int label_num = 0;
    private int max_label_used = 0;
    private LinkedList<Integer> labels_stack = new LinkedList<Integer>();

    public ProjectClassVisitor(ArrayList<SymbolTable> symbolTables) {
        this.symbolTables = symbolTables;
        labels_stack.push(0);
        if (show_code_generation) {
            try {
                File file = new File("JVM.j");
                if (file.exists())
                    file.delete();// delete if exists
                writer = new FileWriter(file);
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
                this.writer.write("\treturn\n.end method\n\n");
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
                this.currentTable.get_args().forEach((argName, argType) -> {
                    localVarsList.add(argName);
                });
                for (String argType : this.currentTable.get_args().values())    
                this.writer.write(this.getJasminType(argType, true));
                this.writer.write(")" + methodReturnType + "\n");
                node.childrenAccept(this, data);
                this.writer.write("\t.limit stack " + this.stack.size() + "\n"); //TODO: usar .limit de forma dinamica
                this.writer.write("\t.limit locals " + this.localVarsList.size() + "\n\n"); //TODO: perceber se aqui e isto ou localVarsList.size()
                //DEBUG:
                System.out.println( "this.currentTable.get_symbols()\n[");
                this.currentTable.get_symbols().forEach((a,b) -> {
                    System.out.println("\t" + a + "," + b + ",");
                });
                System.out.println( "]\n");
                System.out.println( "\nlocalVarsList\n[");
                localVarsList.forEach((a) -> {
                    System.out.println("\t" + a + ",");
                });
                System.out.println( "]\n");
                localVarsList = new ArrayList<String>(){{add("this");}}; // Reset the localVarsList para uma nova função puder ser chamada
                
                //----------------------------------
                this.writer.write(this.inMethod);
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
        if (!(node.jjtGetChild(0) instanceof ASTType)){
            if (!"".equals(node.jjtGetChild(0).jjtGetChild(0).toString())){
                if(node.jjtGetChild(0).jjtGetChild(0).toString().equals("true"))
                    this.inMethod+="\tldc 1\n\tireturn\n";
                else 
                    this.inMethod+="\tldc 0\n\tireturn\n";

            }
            else {
                if (node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier){
                    String varName = extractLabel(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString());
                    this.inMethod += "\tiload " + indexLocal(varName) + "\n";
                    this.inMethod += "\tireturn\n";
                }
                else {
                    this.inMethod+="\tldc " + extractLabel(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString()) + "\n\tireturn\n";
                }
            }
            
        }
        

        /* this.inMethod += "\t" + getJasminType(node.jjtGetChild(0).toString(), false) + "return\n"; */
        return data;
    }

    public Object visit(ASTMethodArgs node, Object data) {
        node.childrenAccept(this, data);

        if(show_code_generation){
            
        }

        return data;
    }

    public Object visit(ASTArgument node, Object data) {
        node.childrenAccept(this, data);
        if(show_code_generation){

        }
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
        /* System.out.println(node.toString()); */
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTTypeWoIdent node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTCondition node, Object data) {
        node.childrenAccept(this, data);
        //this.investigateNode(node, 5);
        if(node.jjtGetNumChildren() > 0 && node.jjtGetChild(0).jjtGetNumChildren() > 0
            && node.jjtGetChild(0).jjtGetChild(0).jjtGetNumChildren() > 0
            && node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier) {
            this.inMethod+="\tiload " + indexLocal(extractLabel(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString())) + "\n";
            this.inMethod += "\tifeq Label" + label_num + "\n";
        } else if (node.jjtGetNumChildren() > 0 && node.jjtGetChild(0).jjtGetNumChildren() > 0
            && node.jjtGetChild(0).jjtGetChild(0) instanceof ASTExpressionToken) {
            ASTExpressionToken new_node = (ASTExpressionToken) node.jjtGetChild(0).jjtGetChild(0);
            if(new_node.getName().equals("true")) {
                this.inMethod+="\tldc 1\n";
                this.inMethod += "\tifeq Label" + label_num + "\n";
            } else if(new_node.getName().equals("false")) {
                this.inMethod+="\tldc 0\n";
                this.inMethod += "\tifeq Label" + label_num + "\n";
            }
        }
        return data;
    }

    public Object visit(ASTIfBody node, Object data) {
        node.childrenAccept(this, data);
        this.inMethod += "\tgoto Label" + (label_num+1) + "\n";
        return data;
    }

    public Object visit(ASTElseBody node, Object data) {
        this.inMethod += "Label" + label_num + ":\n";
        node.childrenAccept(this, data);
        this.inMethod += "Label" + (label_num + 1) + ":\n";
        return data;
    }

    public Object visit(ASTIfElseStatement node, Object data) {
        label_num = max_label_used + 1;
        max_label_used += 2;
        labels_stack.push(label_num);
        node.childrenAccept(this, data);
        label_num = labels_stack.pop();
        label_num = labels_stack.getFirst();
        return data;
    }

    public Object visit(ASTWhileStatement node, Object data) {
        label_num = max_label_used + 1;
        max_label_used += 2;
        this.inMethod += "Label" + (label_num+1) + ":\n";
        labels_stack.push(label_num);
        node.childrenAccept(this, data);
        this.inMethod += "\tgoto Label" + (label_num+1) + "\n";
        this.inMethod += "Label" + label_num + ":\n";
        label_num = labels_stack.pop();
        label_num = labels_stack.getFirst();
        return data;
    }

    public Object visit(ASTWhileBody node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTStatementStartIdent node, Object data) {
        if(show_code_generation && node.jjtGetChild(0) instanceof ASTIdentifier) {
            ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(0);
            //System.out.println(node.toString());
            if (node.toString().equalsIgnoreCase("VarDeclaration ")){
                node.childrenAccept(this, data);
            }
            else {
                //this.inMethod += "\tinvokevirtual " + new_node.getName() + "/";
                //investigateNode(node, 1);
                node.childrenAccept(this, data);
                //this.inMethod = this.inMethod.substring(0, this.inMethod.length() - 1) + "\n";
            }
        }
        else node.childrenAccept(this, data);
        
        
        return data;
    }

    public Object visit(ASTAND node, Object data) {
        node.childrenAccept(this, data);
        if(node.jjtGetNumChildren() > 0 && node.jjtGetChild(0).jjtGetNumChildren() > 0
            && node.jjtGetChild(0).jjtGetChild(0).jjtGetNumChildren() > 0
            && node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier) {
            this.inMethod+="\tiload " + indexLocal(extractLabel(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString())) + "\n";
            this.inMethod += "\tifeq Label" + label_num + "\n";
        } else if(node.jjtGetNumChildren() > 1 && node.jjtGetChild(1).jjtGetNumChildren() > 0
            && node.jjtGetChild(1).jjtGetChild(0).jjtGetNumChildren() > 0
            && node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier) {
            this.inMethod+="\tiload " + indexLocal(extractLabel(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).toString())) + "\n";
            this.inMethod += "\tifeq Label" + label_num + "\n";
        }
        return data;
    }

    public Object visit(ASTMINOR node, Object data) {
        node.childrenAccept(this, data);

        if (show_code_generation) {
            aritmaticOps("cmp", node);
        }

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
            //investigateNode(node, 1);

            if(node.jjtGetChild(1) instanceof ASTAccessingArrayAt){
                String varName = extractLabel(node.jjtGetChild(0).toString()); 
                
                String indexSTR;
                if (node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier)
                    indexSTR = "\taload " + indexLocal(extractLabel(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString())) + "\n";
                else 
                    indexSTR = "\tldc " + extractLabel(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString()) + "\n";
                
                String valueSTR;
                if (node.jjtGetChild(2).jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier)
                    valueSTR = "\taload " + indexLocal(extractLabel(node.jjtGetChild(2).jjtGetChild(0).jjtGetChild(0).toString())) + "\n";
                else 
                    valueSTR = "\tldc " + extractLabel(node.jjtGetChild(2).jjtGetChild(0).jjtGetChild(0).toString()) + "\n";
                                                                                // x[2] = 123;
                this.inMethod += ("\taload " + indexLocal(varName) + "\n");     // » aload 1
                this.inMethod += indexSTR;                                      // » ldc 2
                this.inMethod += valueSTR;                                      // » ldc 123
                this.inMethod += ("\tiastore\n");                               // » iastore
            }
            else if (node.jjtGetChild(1) instanceof ASTExpressionRestOfClauses 
                && node.jjtGetChild(1).jjtGetNumChildren() == 2
                && !(node.jjtGetChild(1).jjtGetChild(1) instanceof ASTAcessing)){
                String varName = extractLabel(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).toString());
                int indexLocal = indexLocal(varName);

                String indexSTR;
                if (node.jjtGetChild(1).jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier)
                    indexSTR = "\taload " + indexLocal(extractLabel(node.jjtGetChild(1).jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString())) + "\n";
                else 
                    indexSTR = "\tldc " + extractLabel(node.jjtGetChild(1).jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString()) + "\n";
                                                                                // y = x[2];
                this.inMethod += ("\taload " + indexLocal + "\n");              // » aload 1 
                this.inMethod += indexSTR;                                      // » ldc 2      ; pode variar entre var ou integral
                this.inMethod += ("\tiaload\n");                                // » iaload
            }
            else {
                if (node.jjtGetChild(1).jjtGetChild(0) instanceof ASTExpressionToken 
                    && node.jjtGetChild(1).jjtGetChild(0).toString().matches("true|false")){
                    this.inMethod += ("\tldc " + (node.jjtGetChild(1).jjtGetChild(0).toString().equals("true") ? "1" : "0") + "\n");
                }
                else if (node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0) instanceof ASTIntegerLiteral 
                    && node.jjtGetChild(1).jjtGetChild(0).jjtGetNumChildren() == 1) {
                    this.inMethod += ("\tldc " + extractLabel(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).toString()) + "\n");
                }
                else if (node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0) instanceof ASTExpressionNew){
                    return data;
                }
                //tratar da parte do x em x = y 
                this.inMethod += ("\tistore " + indexLocal(extractLabel(node.jjtGetChild(0).toString())) + "\n");
            }

        }
        return data;
    }

	public Object visit(ASTStatementAux2 node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTCalling node, Object data) {
        //System.out.println(extractLabel(node.jjtGetParent().jjtGetParent().jjtGetChild(0).toString()));
        //TODO: perceber que tipo e que a funcao e!
        if(show_code_generation){
            //TODO: melhorar isto!
            String argsStr = "";
            for(int i = 0; i < node.jjtGetChild(0).jjtGetNumChildren(); i++){
                if (node.jjtGetChild(0).jjtGetChild(i).jjtGetChild(0).toString().matches("true|false")){
                    this.inMethod += "\tldc " + (node.jjtGetChild(0).jjtGetChild(i).jjtGetChild(0).toString().equalsIgnoreCase("true") ? "1" : "0") +"\n"; 
                    argsStr += "Z";
                }
                else if(node.jjtGetChild(0).jjtGetChild(i).jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier){
                    this.inMethod += "\tiload " + indexLocal(extractLabel(node.jjtGetChild(0).jjtGetChild(i).jjtGetChild(0).jjtGetChild(0).toString())) +"\n"; 
                    argsStr += "I"; //TODO: rever isto
                }
                else if(node.jjtGetChild(0).jjtGetChild(i).jjtGetChild(0).jjtGetChild(0) instanceof ASTIntegerLiteral){
                    this.inMethod += "\tldc " + extractLabel(node.jjtGetChild(0).jjtGetChild(i).jjtGetChild(0).jjtGetChild(0).toString())+"\n"; 
                    argsStr += "I"; 
                }

            }
            String methodName = node.jjtGetChild(0).toString().split("\\(")[0];
            this.inMethod += "\tinvokevirtual " + extractLabel(node.jjtGetParent().jjtGetParent().jjtGetChild(0).toString()) + "/" + methodName + "(" + argsStr +")I\n";
        }
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
                } else if(new_node.getName().equals("this") && node.jjtGetChild(1).jjtGetNumChildren() > 0
                            && node.jjtGetChild(1).jjtGetChild(0) instanceof ASTExpressionAuxDot) {
                    ASTExpressionAuxDot new_dot_node = (ASTExpressionAuxDot) node.jjtGetChild(1).jjtGetChild(0);
                    if (!new_dot_node.getName().equals("length")) {
                        for (int i = 0; i < this.symbolTables.size(); i++) {
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
                                        + " doesn't exist.");
                                System.exit(-1);
                            }
                            break;
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
                } else if(new_node.getName().equals("this") && node.jjtGetChild(1).jjtGetNumChildren() > 0
                            && node.jjtGetChild(1).jjtGetChild(0) instanceof ASTExpressionAuxDot) {
                    ASTExpressionAuxDot new_dot_node = (ASTExpressionAuxDot) node.jjtGetChild(1).jjtGetChild(0);
                    if (!new_dot_node.getName().equals("length")) {
                        for (int i = 0; i < this.symbolTables.size(); i++) {
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
                                        + " doesn't exist.");
                                System.exit(-1);
                            }
                            break;
                        }
                    }
                }
            }
        }
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTAcessing node, Object data) {
        if (node.jjtGetParent().jjtGetChild(0).toString().equalsIgnoreCase("this")){
            String className = this.currentTable.get_parent().get_name();
            String methodCall = node.jjtGetChild(0).toString(); //obter so o nome da funcao
            if(methodCall.contains(")")){
                String returnType;
                String methodName = methodCall.split("\\(")[0];
                if (this.currentTable.get_parent().get_functions().containsKey(methodName)){
                    returnType = this.getJasminType(this.currentTable.get_parent().get_functions().get(methodName).get_return_type().toString(), true);
                    this.inMethod += "\taload 0\t\t\t; Method " + methodName + "() call\n";         //aload 0 para ir buscar o this
                    for(int i = 0; i < node.jjtGetChild(0).jjtGetNumChildren(); i++){
                        if (node.jjtGetChild(0).jjtGetChild(i).jjtGetChild(0).toString().matches("true|false")){
                            this.inMethod += "\tldc " + (node.jjtGetChild(0).jjtGetChild(i).jjtGetChild(0).toString().equalsIgnoreCase("true") ? "1" : "0") +"\n"; 
                        }
                        else if(node.jjtGetChild(0).jjtGetChild(i).jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier){
                            this.inMethod += "\tiload " + indexLocal(extractLabel(node.jjtGetChild(0).jjtGetChild(i).jjtGetChild(0).jjtGetChild(0).toString())) +"\n"; 
                        }
                        else if(node.jjtGetChild(0).jjtGetChild(i).jjtGetChild(0).jjtGetChild(0) instanceof ASTIntegerLiteral){
                            this.inMethod += "\tldc " + extractLabel(node.jjtGetChild(0).jjtGetChild(i).jjtGetChild(0).jjtGetChild(0).toString())+"\n"; 
                        }

                    }
                    this.inMethod += "\tinvokevirtual " + className + "/" + methodName + "("; 
                    this.currentTable.get_parent().get_functions().get(methodName).get_args().forEach((arg, type) ->{
                        this.inMethod += getJasminType(type, true);
                    });
                    
                    this.inMethod += ")"+ returnType + "\n";    
                    //System.out.println(this.currentTable.get_parent().get_functions().containsKey(methodName) +className + "\n" + methodCall+ "\n"+returnType+ "\n" +"\n");
                }
            }
        }
        else{
            //TODO: aqui? quando nao e this...
        }
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTAccessingArrayAt node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTExpressionAuxDot node, Object data) {
        if (show_code_generation){
            if (node.getName().equalsIgnoreCase("length")){
                String arrayName = extractLabel(node.jjtGetParent().jjtGetParent().jjtGetChild(0).jjtGetChild(0).toString());                     
                int idxLocal = indexLocal(arrayName);                               // len = x.length; 
                this.inMethod += "\taload_" + idxLocal + "\n\tarraylength\n";       // » aload_0 » arraylength » x.length 
            }
        }
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
        if (show_code_generation && node.jjtGetChild(0) instanceof ASTIdentifier){
            ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(0);
            String varName = new_node.getName();
            this.inMethod += ("\tnew " + varName + "\n");    
            this.inMethod += ("\tdup\n");    
            this.inMethod += ("\tinvokenonvirtual " + varName + "/<init>()V\n");    
            localVarsList.add(varName);
            this.inMethod += "\tastore " + indexLocal(varName) + "\n";
        }
        else if (show_code_generation && node.jjtGetChild(0) instanceof ASTAccessingArrayAt){
            String varNamme = extractLabel(node.jjtGetParent().jjtGetParent().jjtGetParent().jjtGetChild(0).toString());
            localVarsList.add(varNamme);
            String sizeSTR;
            //investigateNode(node, 1);

            if (node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier)
                sizeSTR = "\taload " + indexLocal(extractLabel(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString())) + "\n";
            else 
                sizeSTR = "\tldc " + extractLabel(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString()) + "\n";
                                                                                                                                            //x = new int[5];
            this.inMethod += sizeSTR;                                                                                                       // » ldc 5 ;tamanho 
            this.inMethod += "\tnewarray int\n";                                                                                            // » newarray int
            this.inMethod += "\tastore " + indexLocal(varNamme) + "\n";                                                                     // » astore 1    
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
                this.inMethod += ("\tldc " + extractLabel(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString()) + "\n");
                break;
            case 1://Load da stack
                this.inMethod += ("\tiload " + indexLocal(extractLabel(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString())) + "\n");
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
                this.inMethod += ("\tldc " + extractLabel(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).toString()) + "\n");
                break;
            case 1://Load da stack
                this.inMethod += ("\tiload " + indexLocal(extractLabel(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).toString())) + "\n");
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
                this.inMethod += "\timul\n";
                break;

            case "div":
                this.inMethod += "\tidiv\n";
                break;

            case "cmp":
                this.inMethod += "\tif_icmpge Label" + label_num + "\n";
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