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
    private int CurrentStackSize = 0;
    private int MaxStackSize = 0;
    private boolean isOptmized = true;
    private boolean not_oper = false;
    boolean not_and = false;

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

    //TODO: REVER ESTA PARTE DAS VARS
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
        this.currentTable = this.currentTable.get_functions_key("main");
        if (show_code_generation) {
            this.resetStackLimit();
            localVarsList = new ArrayList<String>(){{add("this");}};
            try {
                this.writer.write(".method public static main([Ljava/lang/String;)V\n");
                node.childrenAccept(this, data);
                this.writer.write("\t.limit stack " + this.MaxStackSize + "\n"); 
                this.writer.write("\t.limit locals " + this.localVarsList.size() + "\n\n");
                this.writer.write(this.inMethod);
                this.writer.write("\treturn\n.end method\n\n");
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

    public Object visit(ASTMethodDeclaration node, Object data) {
        int num_args = node.jjtGetChild(1).jjtGetNumChildren();
        for(int a = 0; a < this.currentTable.get_functions().size(); a++) {
            if(this.currentTable.get_functions().get(a).get_name().equals(node.getName())
                && this.currentTable.get_functions().get(a).get_args().size() == num_args)
            this.currentTable = this.currentTable.get_functions().get(a);
        }
        if (show_code_generation) {
            String methodReturnType = this.getJasminType(this.currentTable.get_return_type(), true);
            this.resetStackLimit();
            localVarsList = new ArrayList<String>(){{add("this");}}; // Reset the localVarsList para uma nova função puder ser chamada
            try {
                this.writer.write(".method public " + node.getName() + "(");
                this.currentTable.get_args().forEach((argName, argType) -> {
                    localVarsList.add(argName);
                });
                for (String argType : this.currentTable.get_args().values())    
                    this.writer.write(this.getJasminType(argType, true));
                this.writer.write(")" + methodReturnType + "\n");
                node.childrenAccept(this, data);
                this.writer.write("\t.limit stack " + this.MaxStackSize + "\n"); 
                this.writer.write("\t.limit locals " + this.localVarsList.size() + "\n\n"); 
                //DEBUG:
                // System.out.println( "this.currentTable.get_symbols()\n[");
                // this.currentTable.get_symbols().forEach((a,b) -> {
                //     System.out.println("\t" + a + "," + b + ",");
                // });
                // System.out.println( "]\n");
                // System.out.println( "\nlocalVarsList\n[");
                // localVarsList.forEach((a) -> {
                //     System.out.println("\t" + a + ",");
                // });
                // System.out.println( "]\n");
                
                //----------------------------------
                this.writer.write(this.inMethod);
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
        if (!(node.jjtGetChild(0) instanceof ASTType)){
            if (!"".equals(node.jjtGetChild(0).jjtGetChild(0).toString())){
                if(node.jjtGetChild(0).jjtGetChild(0).toString().equals("true"))
                this.inMethod+="\t" + this.pushConstant(1) + "\n\tireturn\n";
                else 
                this.inMethod+="\t" + this.pushConstant(0) + "\n\tireturn\n";
                
            }
            else {
                if (node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier){
                    String varName = extractLabel(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString());
                    
                    if (this.currentTable.get_parent().get_symbols().containsKey(varName)){
                        this.inMethod += "\t" + "aload_0" + "\n"; 
                        this.inMethod += "\t" + "getfield " + this.currentTable.get_parent().get_name() + "/" + varName + " " + this.getJasminType(this.currentTable.get_parent().get_symbols().get(varName), true) + "\n";
                        if (this.currentTable.get_parent().get_symbols().get(varName).equalsIgnoreCase("int[]"))
                        this.inMethod += "\tareturn\n";
                        else 
                        this.inMethod += "\tireturn\n";
                        this.incrementStackLimit();
                        return data;
                    }
                    
                    String type = this.currentTable.get_symbols().get(varName);
                    if (type.equalsIgnoreCase("int[]")){
                        int idx = indexLocal(varName);
                        this.inMethod += "\taload" + (idx > 3 ? " " : "_") + idx + "\n";
                        this.inMethod += "\tareturn\n";
                    }
                    else {
                        this.inMethod += "\t" + this.loadLocal(varName) + "\n";
                        this.inMethod += "\tireturn\n";
                    }
                    this.incrementStackLimit();
                }
                else {
                    if (this.isAritmaticOps(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0)) || this.isAritmaticOps(node.jjtGetChild(0))){
                        node.childrenAccept(this, data);
                        this.inMethod+="\tireturn\n";
                        return data;
                    }
                    else if (this.isBoolOps(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0))){
                        node.childrenAccept(this, data);
                        this.inMethod+="\tireturn\n";
                        return data;
                    }
                    else {
                        if (node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTExpressionRestOfClauses)
                            node.childrenAccept(this, data);
                        else 
                            this.inMethod+="\t" + this.pushConstant(Integer.parseInt(extractLabel(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString()))) + "\n\tireturn\n";
                    }
                    
                }
            }
            
        }
        
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
        /* System.out.println(node.toString()); */
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTTypeWoIdent node, Object data) {
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTCondition node, Object data) {
        if (node.jjtGetNumChildren() > 0 && node.jjtGetChild(0).jjtGetNumChildren() > 0
            && node.jjtGetChild(0).jjtGetChild(0) instanceof ASTExpressionToken) {
            ASTExpressionToken new_node = (ASTExpressionToken) node.jjtGetChild(0).jjtGetChild(0);
            if(new_node.getName() != null && new_node.getName().equals("!")) {
                not_oper = true;
            }
        }
        node.childrenAccept(this, data);
        if(node.jjtGetNumChildren() > 0 && node.jjtGetChild(0).jjtGetNumChildren() > 0
            && node.jjtGetChild(0).jjtGetChild(0).jjtGetNumChildren() == 1
            && node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier) {
            this.inMethod+="\t" + this.loadLocal(extractLabel(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString())) + "\n";
            this.inMethod += "\tifeq Label" + label_num + "\n";
            this.incrementStackLimit();
        } else if (node.jjtGetNumChildren() > 0 && node.jjtGetChild(0).jjtGetNumChildren() > 0
            && node.jjtGetChild(0).jjtGetChild(0) instanceof ASTExpressionToken) {
            ASTExpressionToken new_node = (ASTExpressionToken) node.jjtGetChild(0).jjtGetChild(0);
            if(new_node.getName().equals("true")) {
                this.inMethod+="\t" + this.pushConstant(1) + "\n";
                this.inMethod += "\tifeq Label" + label_num + "\n";
            } else if(new_node.getName().equals("false")) {
                this.inMethod+="\t" + this.pushConstant(0) + "\n";
                this.inMethod += "\tifeq Label" + label_num + "\n";
            } else if(new_node.getName().equals("this")) {
                this.inMethod += "\tifeq Label" + label_num + "\n";
            } else if(new_node.jjtGetNumChildren() > 1 && new_node.jjtGetChild(0) instanceof ASTIdentifier) {
                this.inMethod += "\tifeq Label" + label_num + "\n";
            } else if(new_node.getName().equals("!") && new_node.jjtGetChild(0) instanceof ASTExpressionToken) {
                not_oper = false;
                if(new_node.jjtGetChild(0).jjtGetNumChildren() > 0 && new_node.jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier) {
                    this.inMethod+="\t" + this.loadLocal(extractLabel(new_node.jjtGetChild(0).jjtGetChild(0).toString())) + "\n";
                    this.inMethod += "\tifne Label" + label_num + "\n";
                    this.incrementStackLimit();
                } else {
                    ASTExpressionToken new_child_node = (ASTExpressionToken) new_node.jjtGetChild(0);
                    if(new_child_node.getName() != null && new_child_node.getName().equals("true")) {
                        this.inMethod+="\t" + this.pushConstant(1) + "\n";
                        this.inMethod += "\tifne Label" + label_num + "\n";
                    } else if(new_child_node.getName() != null && new_child_node.getName().equals("false")) {
                        this.inMethod+="\t" + this.pushConstant(0) + "\n";
                        this.inMethod += "\tifne Label" + label_num + "\n";
                    } else if(new_child_node.getName() != null && new_child_node.getName().equals("this")) {
                        this.inMethod += "\tifne Label" + label_num + "\n";
                    } else if(new_child_node.getName() != null && new_child_node.jjtGetNumChildren() > 1 && new_node.jjtGetChild(0) instanceof ASTIdentifier) {
                        this.inMethod += "\tifne Label" + label_num + "\n";
                    }
                }
            }
        }
        return data;
    }

    public Object visit(ASTIfBody node, Object data) {
        if(this.not_and) {
            this.not_and = false;
            this.inMethod += "Label" + (label_num+2) + "\n";
        }
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
        if(this.not_and) {
            System.out.println("Entered " + (label_num+2));
            this.not_and = false;
            this.inMethod += "Label" + (label_num+2) + "\n";
        }
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTStatementStartIdent node, Object data) {  
        node.childrenAccept(this, data);
        return data;
    }

    public Object visit(ASTAND node, Object data) {
        boolean save_not_and = false;
        this.not_and = false;
        if(this.not_oper) {
            this.max_label_used += 1;
            this.not_and = true;
            save_not_and = true;
        }
        node.jjtGetChild(0).jjtAccept(this, data);
        if(node.jjtGetNumChildren() > 0 && node.jjtGetChild(0).jjtGetNumChildren() > 0
            && node.jjtGetChild(0).jjtGetChild(0).jjtGetNumChildren() > 0
            && node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier) {
            this.inMethod+="\t" + this.loadLocal(extractLabel(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString())) + "\n";
            if(this.not_and)
                this.inMethod += "\tifeq Label" + (label_num+2) + "\n";
            else
                this.inMethod += "\tifeq Label" + label_num + "\n";
            this.incrementStackLimit();
        }
        this.not_and = false;
        node.jjtGetChild(1).jjtAccept(this, data);
        if(save_not_and) {
            this.not_and = true;
        }
        if(node.jjtGetNumChildren() > 1 && node.jjtGetChild(1).jjtGetNumChildren() > 0
            && node.jjtGetChild(1).jjtGetChild(0).jjtGetNumChildren() > 0
            && node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier) {
            this.inMethod+="\t" + this.loadLocal(extractLabel(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).toString())) + "\n";
            if(this.not_oper) {
                this.inMethod += "\tifne Label" + label_num + "\n";
                this.not_oper = false;
            }
            else
                this.inMethod += "\tifeq Label" + label_num + "\n";
            this.incrementStackLimit();
        }
        return data;
    }

    public Object visit(ASTMINOR node, Object data) {
        if((node.jjtGetChild(0) instanceof ASTExpressionRestOfClauses) && (node.jjtGetChild(0).jjtGetNumChildren() == 1)){
            if(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier){
                this.inMethod += ("\t" + this.loadLocal(extractLabel(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString())) + "\n");
                this.incrementStackLimit();
            }
            else{
                try {
                    Integer.parseInt(extractLabel(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString()));
                    this.inMethod += ("\t" + this.pushConstant(Integer.parseInt(extractLabel(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString()))) + "\n");
                } catch (Exception e) {}
            }   
        }

        node.childrenAccept(this, data);

        if((node.jjtGetChild(1) instanceof ASTExpressionRestOfClauses) && (node.jjtGetChild(1).jjtGetNumChildren() == 1)){
            if(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier){
                this.inMethod += ("\t" + this.loadLocal(extractLabel(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).toString())) + "\n");
                this.incrementStackLimit();
            }
            else{
                try {
                    Integer.parseInt(extractLabel(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).toString()));
                    this.inMethod += ("\t" + this.pushConstant(Integer.parseInt(extractLabel(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).toString()))) + "\n");
                } catch (Exception e) {}
            }   
        }

        if(this.not_and) {
            this.inMethod += "\tif_icmpge Label" + (label_num+2) + "\n";
        } else if(this.not_oper) {
            this.inMethod += "\tif_icmplt Label" + label_num + "\n";
            this.not_oper = false;
        } else {
            this.inMethod += "\tif_icmpge Label" + label_num + "\n";
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
                
        if (show_code_generation) {
            SymbolTable classTable = this.currentTable.get_parent();
            
            String XidentifierName = this.extractLabel(node.jjtGetChild(0).toString());
            //em x = y ... x e um atributo? 
            boolean xIsField = classTable.get_symbols().containsKey(XidentifierName);
            if (xIsField) 
                this.inMethod += ("\taload_0\n");

            if(node.jjtGetChild(1) instanceof ASTAccessingArrayAt){
                                
                String varName = extractLabel(node.jjtGetChild(0).toString()); 
                String arrayVarSTR = "";
                if (classTable.get_symbols().containsKey(varName)){ //troquei de arrayVarSTR para varName
                    //arrayVarSTR += "\t" + "aload_0" + "\n"; 
                    arrayVarSTR += "\t" + "getfield " + classTable.get_name() + "/" + varName + " " + this.getJasminType(classTable.get_symbols().get(varName), true) + "\n";
                }
                else {
                    arrayVarSTR = ("\taload " + indexLocal(varName) + "\n"); 
                }
                
                String indexSTR = "";
                if (!this.isAritmaticOps(node.jjtGetChild(1).jjtGetChild(0))){
                    if (node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier){
                        String identifierName = this.extractLabel(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString());
                        indexSTR += this.loadVar(identifierName);
                        this.incrementStackLimit();
                    }    
                    else 
                        indexSTR = "\t" + this.pushConstant(Integer.parseInt(extractLabel(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString()))) + "\n";
                }
                String valueSTR = "";
                if (!this.isAritmaticOps(node.jjtGetChild(2))){
                    if (node.jjtGetChild(2).jjtGetChild(0).toString().equals("")){
                        if(node.jjtGetChild(2).jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier){
                            String identifierName = this.extractLabel(node.jjtGetChild(2).jjtGetChild(0).jjtGetChild(0).toString());
                            valueSTR += this.loadVar(identifierName);
                            this.incrementStackLimit();
                        }
                        else if (node.jjtGetChild(2).jjtGetChild(0).jjtGetChild(0) instanceof ASTIntegerLiteral){
                            valueSTR = "\t" + this.pushConstant(Integer.parseInt(extractLabel(node.jjtGetChild(2).jjtGetChild(0).jjtGetChild(0).toString()))) + "\n";
                        }
                    }
                }                                                              // x[2] = 123;
                this.inMethod += arrayVarSTR;                                   // » aload 1
                this.inMethod += indexSTR;                                      // » ldc 2
                node.childrenAccept(this, data);                                  
                this.inMethod += valueSTR;                                      // » ldc 123
                this.inMethod += ("\tiastore\n");                               // » iastore
                this.incrementStackLimit();
            }
            else if (node.jjtGetChild(1) instanceof ASTExpressionRestOfClauses 
                && node.jjtGetChild(1).jjtGetNumChildren() == 2
                && !(node.jjtGetChild(1).jjtGetChild(1) instanceof ASTAcessing)){

                String varName = extractLabel(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).toString());
                int indexLocal = indexLocal(varName);

                String arrayVarSTR = "";
                if (classTable.get_symbols().containsKey(varName)){
                    arrayVarSTR += "\t" + "aload_0" + "\n"; 
                    arrayVarSTR += "\t" + "getfield " + classTable.get_name() + "/" + varName + " " + this.getJasminType(classTable.get_symbols().get(varName), true) + "\n";
                }
                else 
                    arrayVarSTR = ("\taload " + indexLocal(varName) + "\n"); 

                String indexSTR = "";
                if (!this.isAritmaticOps(node.jjtGetChild(1))){
                    if (node.jjtGetChild(1).jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier){
                        //indexSTR = "\taload " + indexLocal(extractLabel(node.jjtGetChild(1).jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString())) + "\n";
                        indexSTR = "\t" + this.loadLocal(extractLabel(node.jjtGetChild(1).jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString())) + "\n";
                        this.incrementStackLimit();
                    }
                    else 
                        indexSTR = "\t" + this.pushConstant(Integer.parseInt(extractLabel(node.jjtGetChild(1).jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString()))) + "\n";
                }                                             
                                                                                                    // y = x[2];
                this.inMethod += arrayVarSTR;                                                       // » aload 1 
                this.inMethod += indexSTR;  
                node.childrenAccept(this, data);                                                    // » ldc 2      ; pode variar entre var ou integral
                this.inMethod += ("\tiaload\n");                                                    // » iaload
                if (xIsField) 
                    this.inMethod += "\t" + "putfield " + classTable.get_name() + "/" + XidentifierName + " " + this.getJasminType(classTable.get_symbols().get(XidentifierName), true) + "\n";
                else
                    this.inMethod += ("\t" + this.storeLocal(XidentifierName) + "\n");
                this.incrementStackLimit();
                this.incrementStackLimit();
            }
            else {
                node.childrenAccept(this, data);

                if (node.jjtGetChild(1) instanceof ASTExpressionRestOfClauses 
                && node.jjtGetChild(1).jjtGetNumChildren() == 2
                && node.jjtGetChild(1).jjtGetChild(1) instanceof ASTAcessing){
                    this.getInvokeVirtual(node, false);
                }
                else {
                    if (node.jjtGetChild(1).jjtGetChild(0) instanceof ASTExpressionToken 
                        && node.jjtGetChild(1).jjtGetChild(0).toString().matches("true|false")){
                        this.inMethod += ("\t" + (node.jjtGetChild(1).jjtGetChild(0).toString().equals("true") ? this.pushConstant(1) : this.pushConstant(0)) + "\n");
                    }
                    else if (node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0) instanceof ASTIntegerLiteral 
                        && node.jjtGetChild(1).jjtGetChild(0).jjtGetNumChildren() == 1) {
                        this.inMethod += ("\t" + this.pushConstant(Integer.parseInt(extractLabel(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).toString()))) + "\n");
                    }
                    else if  (node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier){
                        String YidentifierName = this.extractLabel(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).toString());
                        this.inMethod += this.loadVar(YidentifierName);
                        this.incrementStackLimit();
                    }
                    else if (node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0) instanceof ASTExpressionNew){
                        if(node.jjtGetChild(0) instanceof ASTIdentifier && !xIsField) {
                            ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(0);
                            String varName = new_node.getName();
                            int number = indexLocal(varName);
                            if(number >= 0 && number <=3) 
                                this.inMethod += "\tastore_" + number + "\n";
                            else
                                this.inMethod += "\tastore " + number + "\n";
                        }
                        return data;
                    }
                }
                //tratar da parte do x em x = y 
                if (xIsField)
                    this.inMethod += "\t" + "putfield " + classTable.get_name() + "/" + XidentifierName + " " + this.getJasminType(classTable.get_symbols().get(XidentifierName), true) + "\n";
                else
                    this.inMethod += ("\t" + this.storeLocal(XidentifierName) + "\n");
            }

        }
        else node.childrenAccept(this, data);

        return data;
    }

	public Object visit(ASTStatementAux2 node, Object data) {
        node.childrenAccept(this, data);
        if(node.jjtGetNumChildren() == 0) {
            localVarsList.add(node.getName());
        }
        return data;
    }

    public Object visit(ASTCalling node, Object data) {
        if(show_code_generation)
            this.getInvokeVirtual(node, true);
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
                            SymbolTable table = null;
                            boolean entered = false;
                            for(int n = 0; n < this.currentTable.get_parent().get_functions().size(); n++) {
                                if(this.currentTable.get_parent().get_functions().get(n).get_name().equals(new_dot_node.getName())
                                    && this.currentTable.get_parent().get_functions().get(n).get_args().size() == new_dot_node.jjtGetNumChildren()) {
                                    table = this.currentTable.get_parent().get_functions().get(n);
                                } else if (this.currentTable.get_parent().get_functions().get(n).get_name().equals(new_dot_node.getName())){
                                    entered = true;
                                }
                            }
                            if(table != null) {
                                node.childrenAccept(this, data);
                                return table.get_return_type();
                            } else if(entered) {
                                System.out.println("Semantic Error: Wrong number of arguments in function " + new_dot_node.getName());
                                System.exit(-1);
                            }
                        }
                    }
                } else if(new_node.getName().equals("this") && node.jjtGetChild(1).jjtGetNumChildren() > 0
                            && node.jjtGetChild(1).jjtGetChild(0) instanceof ASTExpressionAuxDot) {
                    ASTExpressionAuxDot new_dot_node = (ASTExpressionAuxDot) node.jjtGetChild(1).jjtGetChild(0);
                    if (!new_dot_node.getName().equals("length")) {
                        SymbolTable table = null;
                        boolean entered = false;
                        for(int n = 0; n < this.currentTable.get_parent().get_functions().size(); n++) {
                            if(this.currentTable.get_parent().get_functions().get(n).get_name().equals(new_dot_node.getName())
                                && this.currentTable.get_parent().get_functions().get(n).get_args().size() == new_dot_node.jjtGetNumChildren()) {
                                table = this.currentTable.get_parent().get_functions().get(n);
                            } else if (this.currentTable.get_parent().get_functions().get(n).get_name().equals(new_dot_node.getName())){
                                entered = true;
                            }
                        }
                        if(table != null) {
                            node.childrenAccept(this, data);
                            return table.get_return_type();
                        } else if(entered) {
                            System.out.println("Semantic Error: Wrong number of arguments in function " + new_dot_node.getName());
                            System.exit(-1);
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
                            SymbolTable table = null;
                            boolean entered = false;
                            for(int n = 0; n < this.currentTable.get_parent().get_functions().size(); n++) {
                                if(this.currentTable.get_parent().get_functions().get(n).get_name().equals(new_dot_node.getName())
                                    && this.currentTable.get_parent().get_functions().get(n).get_args().size() == new_dot_node.jjtGetNumChildren()) {
                                    table = this.currentTable.get_parent().get_functions().get(n);
                                } else if (this.currentTable.get_parent().get_functions().get(n).get_name().equals(new_dot_node.getName())){
                                    entered = true;
                                }
                            }
                            if(table != null) {
                                node.childrenAccept(this, data);
                                return table.get_return_type();
                            } else if(entered) {
                                System.out.println("Semantic Error: Wrong number of arguments in function " + new_dot_node.getName());
                                System.exit(-1);
                            }
                        }
                    }
                } else if(new_node.getName().equals("this") && node.jjtGetChild(1).jjtGetNumChildren() > 0
                            && node.jjtGetChild(1).jjtGetChild(0) instanceof ASTExpressionAuxDot) {
                    ASTExpressionAuxDot new_dot_node = (ASTExpressionAuxDot) node.jjtGetChild(1).jjtGetChild(0);
                    if (!new_dot_node.getName().equals("length")) {
                        SymbolTable table = null;
                        boolean entered = false;
                        for(int n = 0; n < this.currentTable.get_parent().get_functions().size(); n++) {
                            if(this.currentTable.get_parent().get_functions().get(n).get_name().equals(new_dot_node.getName())
                                && this.currentTable.get_parent().get_functions().get(n).get_args().size() == new_dot_node.jjtGetNumChildren()) {
                                table = this.currentTable.get_parent().get_functions().get(n);
                            } else if (this.currentTable.get_parent().get_functions().get(n).get_name().equals(new_dot_node.getName())){
                                entered = true;
                            }
                        }
                        if(table != null) {
                            node.childrenAccept(this, data);
                            return table.get_return_type();
                        } else if(entered) {
                            System.out.println("Semantic Error: Wrong number of arguments in function " + new_dot_node.getName());
                            System.exit(-1);
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
                if (this.currentTable.get_parent().get_functions_key(methodName) != null){
                    returnType = this.getJasminType(this.currentTable.get_parent().get_functions_key(methodName).get_return_type().toString(), true);
                    this.inMethod += "\taload 0\t\t\t; Method " + methodName + "() call\n";         //aload 0 para ir buscar o this
                    this.incrementStackLimit();
                    for(int i = 0; i < node.jjtGetChild(0).jjtGetNumChildren(); i++){
                        if (node.jjtGetChild(0).jjtGetChild(i).jjtGetChild(0).toString().matches("true|false")){
                            this.inMethod += "\t" + (node.jjtGetChild(0).jjtGetChild(i).jjtGetChild(0).toString().equalsIgnoreCase("true") ? this.pushConstant(1) : this.pushConstant(0)) +"\n"; 
                        }
                        else if(node.jjtGetChild(0).jjtGetChild(i).jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier){
                            this.inMethod += "\t" + this.loadLocal(extractLabel(node.jjtGetChild(0).jjtGetChild(i).jjtGetChild(0).jjtGetChild(0).toString())) +"\n"; 
                            this.incrementStackLimit();
                        }
                        else if(node.jjtGetChild(0).jjtGetChild(i).jjtGetChild(0).jjtGetChild(0) instanceof ASTIntegerLiteral){
                            this.inMethod += "\t" + this.pushConstant(Integer.parseInt(extractLabel(node.jjtGetChild(0).jjtGetChild(i).jjtGetChild(0).jjtGetChild(0).toString())))+"\n"; 
                        }

                    }
                    this.inMethod += "\tinvokevirtual " + className + "/" + methodName + "("; 
                    this.currentTable.get_parent().get_functions_key(methodName).get_args().forEach((arg, type) ->{
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
                this.incrementStackLimit();
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
            //this.inMethod += "\tastore " + indexLocal(varName) + "\n";
        }
        else if (show_code_generation && node.jjtGetChild(0) instanceof ASTAccessingArrayAt){
            String varNamme = extractLabel(node.jjtGetParent().jjtGetParent().jjtGetParent().jjtGetChild(0).toString());
            String sizeSTR;

            if (node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier){
                sizeSTR = "\taload " + indexLocal(extractLabel(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString())) + "\n";
                this.incrementStackLimit();
            }
            else 
                sizeSTR = "\t" + this.pushConstant(Integer.parseInt(extractLabel(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString()))) + "\n";
                                                                                                                                            //x = new int[5];
            this.inMethod += sizeSTR;                                                                                                       // » ldc 5 ;tamanho 
            this.inMethod += "\tnewarray int\n";                                                                                            // » newarray int
            SymbolTable classTable = this.currentTable.get_parent();                                                                        // » astore 1
            int idx = indexLocal(varNamme);          
            if (classTable.get_symbols().containsKey(varNamme)){
                this.inMethod += "\t" + "putfield " + classTable.get_name() + "/" + varNamme + " " 
                + this.getJasminType(classTable.get_symbols().get(varNamme), true) + "\n";
            }                   
            else
                this.inMethod += "\tastore" + (idx > 3 ? " " : "_") + idx + "\n";                                                                                                                   
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
                try {
                    Integer.parseInt(extractLabel(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString()));
                    this.inMethod += ("\t" + this.pushConstant(Integer.parseInt(extractLabel(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString()))) + "\n");
                } catch (Exception e) {
                    break;
                }
                break;
            case 1://Load da stack
                this.inMethod += ("\t" + this.loadLocal(extractLabel(node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).toString())) + "\n");
                this.incrementStackLimit();
                break;
        
            default:
                //System.out.println("DEBUG: ENTERED DEFAULT");
                break;
        }

        switch (valRight) {
            case 0://Push para a stack
                try {
                    Integer.parseInt(extractLabel(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).toString()));
                    this.inMethod += ("\t" + this.pushConstant(Integer.parseInt(extractLabel(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).toString()))) + "\n");
                } catch (Exception e) {
                    break;
                }
                break;
            case 1://Load da stack
                this.inMethod += ("\t" + this.loadLocal(extractLabel(node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).toString())) + "\n");
                this.incrementStackLimit();
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
            return upercase ? "[I" : "a";
        }
        return "";
    }

    public String pushConstant(int number, boolean opt) {
        if (opt) {
            if (number >= 0 && number <= 5)
                return "iconst_" + number;              //constante [0-5]
            if (number == -1)
                return "iconst_m1";                     //constante -1
            if (number >= -128 && number < 128)
                return  "bipush " + number;             //numero de 1-byte
            if (number >= -32768 && number < 32768)
                return "sipush " + number;              //numero de 2-byte
        }
        return "ldc " + number;                         //nao optimizado ou numero de 4-byte
    }

    public String pushConstant(int number) {
        return pushConstant(number, this.isOptmized);
    }


    public String storeLocal(String localName, boolean opt) {
        int indexLocal = indexLocal(localName);
        String typeLocal = this.getJasminType(this.currentTable.get_symbols().get(localName), false);
        String optChange = (opt && indexLocal >= 0 && indexLocal <= 3) ? "_" : " "; //store [0-3] com _
        return typeLocal + "store" + optChange + indexLocal;
    }

    public String storeLocal(String localName) {
        return storeLocal(localName, this.isOptmized);
    }
    

    public String loadLocal(String localName, boolean opt) {
        int indexLocal = indexLocal(localName);
        String typeLocal;
        if(this.currentTable.get_symbols().get(localName) != null)
            typeLocal = this.getJasminType(this.currentTable.get_symbols().get(localName), false);
        else
            typeLocal = this.getJasminType(this.currentTable.get_args().get(localName), false);
        String optChange = (opt && indexLocal >= 0 && indexLocal <= 3) ? "_" : " "; //store [0-3] com _
        return typeLocal + "load" + optChange + indexLocal;
    }

    public String loadLocal(String localName) {
        return loadLocal(localName, this.isOptmized);
    }
    
    public String loadVar(String identifierName) {
        SymbolTable classTable = this.currentTable.get_parent();
        if (classTable.get_symbols().containsKey(identifierName)){
            return "\t" + "aload_0" + "\n" 
                    + "\t" + "getfield " + classTable.get_name() + "/" + identifierName + " " 
                    + this.getJasminType(classTable.get_symbols().get(identifierName), true) + "\n";
        }
        return "\t" + this.loadLocal(identifierName) + "\n"; 
    }

    public void resetStackLimit() {
        this.CurrentStackSize = 0;
        this.MaxStackSize = 0;
    }

    public void incrementStackLimit() {
        this.CurrentStackSize++;
        this.MaxStackSize = this.CurrentStackSize > this.MaxStackSize ? this.CurrentStackSize : this.MaxStackSize;
    }

    public boolean isAritmaticOps(Node node) {
        return (node instanceof ASTSUB) 
                || (node instanceof ASTADD) 
                || (node instanceof ASTMULT) 
                || (node instanceof ASTDIV);
    }

    public boolean isBoolOps(Node node) {
        return (node instanceof ASTMINOR) 
                || (node instanceof ASTAND);
    }

    public void getInvokeVirtual(Node node, boolean calling) {
        node = calling ? node : node.jjtGetChild(1).jjtGetChild(1);
        String aux = null;
        if(calling) {
            aux = node.jjtGetParent().jjtGetParent().jjtGetChild(0).toString();
        } else if(node.jjtGetParent().jjtGetChild(0).jjtGetNumChildren() > 0) {
            aux = node.jjtGetParent().jjtGetChild(0).jjtGetChild(0).toString();
        } else { //this
            aux = this.currentTable.get_parent().get_name();
        }
        String className = extractLabel(aux);
        String invokeMethod = "invokevirtual";
        if(this.currentTable.exists(className) == null && className != "") {
            invokeMethod = "invokestatic";
        }
        if (className.equals("")) {
            className = this.currentTable.get_parent().get_name();
        }

        if(invokeMethod.equals("invokevirtual")) {
            int num = indexLocal(className);
            if(num >= 0 && num <= 3)
                this.inMethod += "\taload_" + num + "\n";
            else if (num >= 0)
                this.inMethod += "\taload " + num + "\n";
        }

        String type = "V";
        if (!calling){
            String identifierName = this.extractLabel(node.jjtGetParent().jjtGetParent().jjtGetChild(0).toString());
            if(this.currentTable.get_symbols().get(identifierName) != null)
                type = this.getJasminType(this.currentTable.get_symbols().get(identifierName), true);
            else
                type = this.getJasminType(this.currentTable.get_args().get(identifierName), true);
        }
        String argsStr = "";
        for(int i = 0; i < node.jjtGetChild(0).jjtGetNumChildren(); i++){
            if (node.jjtGetChild(0).jjtGetChild(i).jjtGetChild(0).toString().matches("true|false")){
                this.inMethod += "\t" + (node.jjtGetChild(0).jjtGetChild(i).jjtGetChild(0).toString().equalsIgnoreCase("true") ? this.pushConstant(1) : this.pushConstant(0)) +"\n"; 
                argsStr += "Z";
            }
            else if(node.jjtGetChild(0).jjtGetChild(i).jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier){
                if (node.jjtGetChild(0).jjtGetChild(i).jjtGetNumChildren() == 1){
                    //identificador normal
                    String varName = extractLabel(node.jjtGetChild(0).jjtGetChild(i).jjtGetChild(0).jjtGetChild(0).toString());
                    this.inMethod += "\t" + this.loadLocal(varName) +"\n";
                    if(this.currentTable.get_symbols().get(varName) != null)
                        argsStr += this.getJasminType(this.currentTable.get_symbols().get(varName),true);
                    else
                        argsStr += this.getJasminType(this.currentTable.get_args().get(varName),true);
                    this.incrementStackLimit();
                }
                else if (node.jjtGetChild(0).jjtGetChild(i).jjtGetNumChildren() == 2){
                    //invoke function 
                    //TODO: rever isto
                    argsStr += "I"; 
                }                
            }
            else if(node.jjtGetChild(0).jjtGetChild(i).jjtGetChild(0).jjtGetChild(0) instanceof ASTIntegerLiteral){
                this.inMethod += "\t" + this.pushConstant(Integer.parseInt(extractLabel(node.jjtGetChild(0).jjtGetChild(i).jjtGetChild(0).jjtGetChild(0).toString())))+"\n"; 
                argsStr += "I"; 
            }
            else if (this.isAritmaticOps(node.jjtGetChild(0).jjtGetChild(i))){
                argsStr += "I";
            }
            else if (this.isBoolOps(node.jjtGetChild(0).jjtGetChild(i))){
                argsStr += "Z";
            }
        }
        if(this.currentTable.exists(className) != null) {
            className = this.currentTable.exists(className);
        }
        String methodName = node.jjtGetChild(0).toString().split("\\(")[0];
        this.inMethod += "\t" + invokeMethod + " " + className + "/" + methodName + "(" + argsStr +")" + type + "\n";
    }

    
}