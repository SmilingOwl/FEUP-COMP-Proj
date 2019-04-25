import java.util.ArrayList;
import java.util.LinkedList;

public class ProjectClassVisitor implements ProjectVisitor{
  private ArrayList<SymbolTable> symbolTables;
  private SymbolTable currentTable;
  private LinkedList stack = new LinkedList();
  private boolean show_semantic_analysis = false;
  private boolean show_code_generation = false;

  public ProjectClassVisitor(ArrayList<SymbolTable> symbolTables) {
    this.symbolTables = symbolTables;
  }

  public Object defaultVisit(SimpleNode node, Object data){
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(SimpleNode node, Object data){
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTIntegerLiteral node, Object data){
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTIdentifier node, Object data){
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTProgram node, Object data){
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTClassDeclaration node, Object data){
    String name = node.getName();
    SymbolTable table = new SymbolTable(name, "class", null);
    for(int i = 0; i < this.symbolTables.size(); i++) {
      if(this.symbolTables.get(i).get_name().equals(name)) {
        this.currentTable = this.symbolTables.get(i);
      }
    }
    if(this.currentTable == null) {
      System.out.println("Symbol Table " + name + " not found.");
      return null;
    }
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTVarDeclaration node, Object data){
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTVarDeclarationWoIdent node, Object data){
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTMainDeclaration node, Object data){
    this.currentTable = this.currentTable.get_functions().get("main@1");
    node.childrenAccept(this, data);
    this.currentTable = this.currentTable.get_parent();
    return data;
  }
  public Object visit(ASTMethodDeclaration node, Object data){
    this.currentTable = this.currentTable.get_functions().get(node.getName() + "@1");
    node.childrenAccept(this, data);
    this.currentTable = this.currentTable.get_parent();
    return data;
  }
  public Object visit(ASTReturn node, Object data){

    if(node.jjtGetNumChildren() == 1) {
      if(node.jjtGetChild(0) instanceof ASTType) {
        ASTType new_node = (ASTType) node.jjtGetChild(0);
        this.currentTable.set_return_type(new_node.getName());
      } else if(node.jjtGetChild(0).jjtGetChild(0) instanceof ASTExpressionToken) {
        //compare type with return type of table.
        ASTExpressionToken new_node = (ASTExpressionToken) node.jjtGetChild(0).jjtGetChild(0);
        if(new_node.jjtGetNumChildren() != 0 && new_node.jjtGetChild(0) instanceof ASTIdentifier) {
          ASTIdentifier new_identifier_node = (ASTIdentifier) new_node.jjtGetChild(0);
          String type = this.currentTable.exists(new_identifier_node.getName());
          if(type != null && !type.equals(this.currentTable.get_return_type()) && show_semantic_analysis) {
            System.out.println("Semantic Error: Invalid return value for function: " + this.currentTable.get_name() + ".");
          }
        } else if(new_node.jjtGetNumChildren() != 0 && new_node.jjtGetChild(0) instanceof ASTIntegerLiteral) {
          if(!this.currentTable.get_return_type().equals("int") && show_semantic_analysis) {
            System.out.println("Semantic Error: Invalid return value for function: " + this.currentTable.get_name() + ".");
          }
        } else if(new_node.getName() != null && (new_node.getName().equals("true") || new_node.getName().equals("false")) 
            && !this.currentTable.get_return_type().equals("boolean") && show_semantic_analysis) {
          System.out.println("Semantic Error: Invalid return value for function: " + this.currentTable.get_name() + ".");
        }
      }
    }
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTMethodArgs node, Object data){
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTArgument node, Object data){
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTMainMethodBody node, Object data){
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTType node, Object data){
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTIsArray node, Object data){
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTTypeWoIdent node, Object data){
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTCondition node, Object data){
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTIfBody node, Object data){
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTElseBody node, Object data){
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTIfElseStatement node, Object data){
    node.childrenAccept(this, data);
    int scope = this.currentTable.get_scope() + 1;
    SymbolTable table = this.currentTable.get_parent().get_functions().get(this.currentTable.get_name() + "@" + scope);
    this.currentTable = table;
    return data;
  }
  public Object visit(ASTWhileStatement node, Object data){
    node.childrenAccept(this, data);
    int scope = this.currentTable.get_scope() + 1;
    SymbolTable table = this.currentTable.get_parent().get_functions().get(this.currentTable.get_name() + "@" + scope);
    this.currentTable = table;
    return data;
  }
  public Object visit(ASTWhileBody node, Object data){
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTStatementStartIdent node, Object data){
    node.childrenAccept(this, data);
    return data;
  }
   public Object visit(ASTAND node, Object data) {
    String type = null;
    String name = null;
    if (node.jjtGetNumChildren() == 2) {

      if ((node.jjtGetChild(0) instanceof ASTADD | node.jjtGetChild(0) instanceof ASTSUB
          | node.jjtGetChild(0) instanceof ASTMULT | node.jjtGetChild(0) instanceof ASTDIV
          | node.jjtGetChild(0) instanceof ASTIntegerLiteral | node.jjtGetChild(1) instanceof ASTADD
          | node.jjtGetChild(1) instanceof ASTSUB | node.jjtGetChild(1) instanceof ASTMULT
          | node.jjtGetChild(1) instanceof ASTDIV | node.jjtGetChild(1) instanceof ASTIntegerLiteral) 
          && show_semantic_analysis)
        System.out.println("Error!");

      else if (node.jjtGetChild(0) instanceof ASTIdentifier) {
        ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(1);
        name = new_node.getName();
        type = this.currentTable.exists(name);

        if (type.equals("int") && show_semantic_analysis)
          System.out.println("Error!");
      }
    }

    node.childrenAccept(this, data);
    return data;
  }

  public Object visit(ASTMINOR node, Object data) {
    String name = null;
    String type = null;
    node.childrenAccept(this, data);

    if (node.jjtGetNumChildren() == 2) {

      if (node.jjtGetChild(0) instanceof ASTIntegerLiteral) {

        if (node.jjtGetChild(1) instanceof ASTIdentifier) {

          ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(1);
          name = new_node.getName();
          type = this.currentTable.exists(name);

          if (type.equals("boolean") && show_semantic_analysis)
            System.out.println("Error!");
        }

      } else if (node.jjtGetChild(0) instanceof ASTIdentifier) {

        ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(0);
        name = new_node.getName();
        type = this.currentTable.exists(name);

        if (type.equals("boolean") && show_semantic_analysis) {
          System.out.println("Error!");
        }
      }
    }
    return data;
  }

  public Object visit(ASTADD node, Object data) {
    String name = null;
    String type = null;
    node.childrenAccept(this, data);
    
    //Semantic

    if (node.jjtGetNumChildren() == 2) {

      if (node.jjtGetChild(0) instanceof ASTIntegerLiteral) {

        if (node.jjtGetChild(1) instanceof ASTIdentifier) {

          ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(1);
          name = new_node.getName();
          type = this.currentTable.exists(name);

          if (type.equals("boolean") && show_semantic_analysis)
            System.out.println("Error!");
        }

      } else if (node.jjtGetChild(0) instanceof ASTIdentifier) {

        ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(0);
        name = new_node.getName();
        type = this.currentTable.exists(name);

        if (type.equals("boolean") && show_semantic_analysis) {
          System.out.println("Error!");
        }
      }
    }

    //Code generation

    if(show_code_generation)
    {

    System.out.println("\nAdd: ");
    //System.out.println("\t" + node.jjtGetChild(0).jjtGetChild(0).getClass());
    //System.out.println("\t" + node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0));
    //System.out.println("\t" + node.jjtGetChild(0).getClass());
    
    if(node.jjtGetChild(0) instanceof ASTADD){ // Second and foward
      System.out.println("\t" + node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0));
    }
    else{ //First operation, has 2 literals
      System.out.println("\t" + node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0));
      System.out.println("\t" + node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0));
    }
    }
    
    return data;
  }

  public Object visit(ASTSUB node, Object data) {
    String name = null;
    String type = null;
    node.childrenAccept(this, data);

    //Semantic

    if (node.jjtGetNumChildren() == 2) {

      if (node.jjtGetChild(0) instanceof ASTIntegerLiteral) {

        if (node.jjtGetChild(1) instanceof ASTIdentifier) {

          ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(1);
          name = new_node.getName();
          type = this.currentTable.exists(name);

          if (type.equals("boolean") && show_semantic_analysis)
            System.out.println("Error!");
        }

      } else if (node.jjtGetChild(0) instanceof ASTIdentifier) {

        ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(0);
        name = new_node.getName();
        type = this.currentTable.exists(name);

        if (type.equals("boolean") && show_semantic_analysis) {
          System.out.println("Error!");
        }
      }
    }

    //Code generation

  if(show_code_generation) {

    System.out.println("\nSub: ");

    if(node.jjtGetChild(0) instanceof ASTSUB){ // Second and foward
      System.out.println("\t" + node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0));
    }
    else{ //First operation, has 2 literals
      System.out.println("\t" + node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0));
      System.out.println("\t" + node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0));
    }
  }
    return data;
  }

  public Object visit(ASTMULT node, Object data) {
    String name = null;
    String type = null;
    node.childrenAccept(this, data);

    //Semantic

    if (node.jjtGetNumChildren() == 2) {

      if (node.jjtGetChild(0) instanceof ASTIntegerLiteral) {

        if (node.jjtGetChild(1) instanceof ASTIdentifier) {

          ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(1);
          name = new_node.getName();
          type = this.currentTable.exists(name);

          if (type.equals("boolean") && show_semantic_analysis)
            System.out.println("Error!");
        }

      } else if (node.jjtGetChild(0) instanceof ASTIdentifier) {

        ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(0);
        name = new_node.getName();
        type = this.currentTable.exists(name);

        if (type.equals("boolean") && show_semantic_analysis) {
          System.out.println("Error!");
        }
      }
    }

    //Code generation
  if(show_code_generation) {
    System.out.println("\nMult: ");
    
    if(node.jjtGetChild(0) instanceof ASTMULT){ // Second and foward
      System.out.println("\t" + node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0));
    }
    else{ //First operation, has 2 literals
      System.out.println("\t" + node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0));
      System.out.println("\t" + node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0));
    }
  }
    return data;
  }

  public Object visit(ASTDIV node, Object data) {
    String name = null;
    String type = null;
    node.childrenAccept(this, data);

    //Semantic

    if (node.jjtGetNumChildren() == 2) {

      if (node.jjtGetChild(0) instanceof ASTIntegerLiteral) {

        if (node.jjtGetChild(1) instanceof ASTIdentifier) {

          ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(1);
          name = new_node.getName();
          type = this.currentTable.exists(name);

          if (type.equals("boolean") && show_semantic_analysis)
            System.out.println("Error!");
        }

      } else if (node.jjtGetChild(0) instanceof ASTIdentifier) {

        ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(0);
        name = new_node.getName();
        type = this.currentTable.exists(name);

        if (type.equals("boolean") && show_semantic_analysis) {
          System.out.println("Error!");
        }
      }
    }

    //Code generation
  if(show_code_generation) {
    System.out.println("\nDiv: ");
    
    if(node.jjtGetChild(0) instanceof ASTDIV){ // Second and foward
      System.out.println("\t" + node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0));
    }
    else{ //First operation, has 2 literals
      System.out.println("\t" + node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0));
      System.out.println("\t" + node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0));
    }
  }
    return data;
  }
  public Object visit(ASTEQUAL node, Object data){
    if(node.jjtGetChild(0) instanceof ASTIdentifier) {
      ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(0);
      if(currentTable.exists(new_node.getName()) == null && show_semantic_analysis) {
        System.out.println("Semantic error: variable " + new_node.getName() + " doesn't exist.");
      }
    }
    node.childrenAccept(this, data);

  if(show_code_generation){
    System.out.println("\nAssign: ");
    System.out.println("\t" + node.jjtGetChild(0));
    System.out.println("\t" + node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).getClass());
  }
    return data;
  }
  public Object visit(ASTStatementAux2 node, Object data){
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTCalling node, Object data){
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTExpressionRestOfClauses node, Object data){
    if(node.jjtGetNumChildren() != 0 && node.jjtGetChild(0).jjtGetNumChildren() != 0 && node.jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier) {
      ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(0).jjtGetChild(0);
      if(currentTable.exists(new_node.getName()) == null && show_semantic_analysis) {
        System.out.println("Semantic error: variable " + new_node.getName() + " doesn't exist.");
      }
    }
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTExpressionRestOfClausesWoIdent node, Object data){
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTAcessing node, Object data){
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTAccessingArrayAt node, Object data) {
    String name = null;
    String type = null;
    node.childrenAccept(this, data);
    
    if(node.jjtGetNumChildren() == 1){

      if (node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier) {
       
        ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0);
        name = new_node.getName();
        type = this.currentTable.exists(name);

        if (!type.equals("int") && show_semantic_analysis)
          System.out.println("Error!");
      }

    }else{
      //expression...
    }
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTExpressionAuxDot node, Object data){
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTExpressionToken node, Object data){
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTExpressionTokenWoIdent node, Object data){
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTExpressionNew node, Object data){
    node.childrenAccept(this, data);
    return data;
  }

/*
  public Object pop(){
    return stack.removeFirst();
  }

  public void push(Object o){
    stack.addFirst(o);
  }
  */
}