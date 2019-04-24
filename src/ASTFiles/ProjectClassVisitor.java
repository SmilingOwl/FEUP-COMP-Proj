import java.util.ArrayList;
import java.util.LinkedList;

public class ProjectClassVisitor implements ProjectVisitor{
  private ArrayList<SymbolTable> symbolTables;
  private SymbolTable currentTable;
  private LinkedList stack = new LinkedList();

  public ProjectClassVisitor() {
    this.symbolTables = new ArrayList<SymbolTable>();
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
    // System.out.println("There are " + this.symbolTables.size() + " tables in the system: \n");
    // for(int i = 0; i < this.symbolTables.size(); i++) {
    //   this.symbolTables.get(i).print();
    //   System.out.println("\n");
    // }
    return data;
  }
  public Object visit(ASTClassDeclaration node, Object data){
    String name = node.getName();
    SymbolTable table = new SymbolTable(name, "class", null);
    this.currentTable = table;
    this.symbolTables.add(table);
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTVarDeclaration node, Object data){
    if(node.jjtGetParent() instanceof ASTClassDeclaration) {
        ASTClassDeclaration parent_node = (ASTClassDeclaration) node.jjtGetParent();
    }
    String type = "", name = "";
    if(node.jjtGetNumChildren() == 2) {
        if(node.jjtGetChild(0) instanceof ASTType) {
            ASTType new_node = (ASTType) node.jjtGetChild(0);
            type = new_node.getName();
            if(new_node.jjtGetNumChildren() == 1)
                type+="[]";
        } else if(node.jjtGetChild(0) instanceof ASTTypeWoIdent) {
            ASTTypeWoIdent new_node = (ASTTypeWoIdent) node.jjtGetChild(0);
            type = new_node.getName();
            if(new_node.jjtGetNumChildren() == 1)
                type+="[]";
        }
        if(node.jjtGetChild(1) instanceof ASTIdentifier) {
            ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(1);
            name = new_node.getName();
        }
    }
    if(this.currentTable.exists(name) != null) {
      System.out.println("Semantic Error: variable " + name + " already exists.");
    }
    else this.currentTable.get_symbols().put(name, type);
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTVarDeclarationWoIdent node, Object data){
    if(node.jjtGetParent() instanceof ASTMainMethodBody) {
        ASTMainMethodBody parent_node = (ASTMainMethodBody) node.jjtGetParent();
    }
    String type = "", name = "";
    if(node.jjtGetNumChildren() == 2) {
        if(node.jjtGetChild(0) instanceof ASTType) {
            ASTType new_node = (ASTType) node.jjtGetChild(0);
            type = new_node.getName();
            if(new_node.jjtGetNumChildren() == 1)
                type+="[]";
        } else if(node.jjtGetChild(0) instanceof ASTTypeWoIdent) {
            ASTTypeWoIdent new_node = (ASTTypeWoIdent) node.jjtGetChild(0);
            type = new_node.getName();
            if(new_node.jjtGetNumChildren() == 1)
                type+="[]";
        }
        if(node.jjtGetChild(1) instanceof ASTIdentifier) {
            ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(1);
            name = new_node.getName();
        }
    }
    if(this.currentTable.exists(name) != null) {
      System.out.println("Semantic Error: variable " + name + " already exists.");
    }
    else this.currentTable.get_symbols().put(name, type);
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTMainDeclaration node, Object data){
    SymbolTable table = new SymbolTable("main", "main", this.currentTable);
    this.currentTable = table;
    this.symbolTables.add(table);
    node.childrenAccept(this, data);
    this.currentTable = this.currentTable.get_parent();
    return data;
  }
  public Object visit(ASTMethodDeclaration node, Object data){
    SymbolTable table = new SymbolTable(node.getName(), "method", this.currentTable);
    this.currentTable = table;
    this.symbolTables.add(table);
    node.childrenAccept(this, data);
    this.currentTable = this.currentTable.get_parent();
    return data;
  }
  public Object visit(ASTReturn node, Object data){
    if(node.jjtGetNumChildren() == 1) {
      if(node.jjtGetChild(0) instanceof ASTType) {
        ASTType new_node = (ASTType) node.jjtGetChild(0);
        this.currentTable.set_return_type(new_node.getName());
      }
      if(node.jjtGetChild(0) instanceof ASTIdentifier) {
        //compare type with return type of table.
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
    String type = null, name = null;
    if(node.jjtGetNumChildren() == 2) {
        if(node.jjtGetChild(0) instanceof ASTType) {
            ASTType new_node = (ASTType) node.jjtGetChild(0);
            type = new_node.getName();
            if(new_node.jjtGetNumChildren() == 1)
                type+="[]";
        } 
        if(node.jjtGetChild(1) instanceof ASTIdentifier) {
            ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(1);
            name = new_node.getName();
        }
    }
    if(this.currentTable.exists(name) != null) {
      System.out.println("Semantic Error: variable " + name + " already exists.");
    }
    else this.currentTable.get_args().put(name, type);
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
    return data;
  }
  public Object visit(ASTWhileStatement node, Object data){
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTWhileBody node, Object data){
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTStatementStartIdent node, Object data){
    if(node.get_type().equals("VarDeclaration")){
      String type = "", name = "";
      if(node.jjtGetNumChildren() == 2) {
          if(node.jjtGetChild(0) instanceof ASTType) {
              ASTType new_node = (ASTType) node.jjtGetChild(0);
              type = new_node.getName();
              if(new_node.jjtGetNumChildren() == 1)
                type+="[]";
          } else if(node.jjtGetChild(0) instanceof ASTTypeWoIdent) {
              ASTTypeWoIdent new_node = (ASTTypeWoIdent) node.jjtGetChild(0);
              type = new_node.getName();
              if(new_node.jjtGetNumChildren() == 1)
                type+="[]";
          } else if(node.jjtGetChild(0) instanceof ASTIdentifier) {
              ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(0);
              type = new_node.getName();
          }
          if(node.jjtGetChild(1) instanceof ASTStatementAux2) {
              ASTStatementAux2 new_node = (ASTStatementAux2) node.jjtGetChild(1);
              if(new_node.jjtGetNumChildren() == 0 && new_node.getName() != null) {
                name = new_node.getName();
              }
          }
      }
      if(this.currentTable.exists(name) != null) {
      System.out.println("Semantic Error: variable " + name + " already exists.");
    }
    else this.currentTable.get_symbols().put(name, type);
    }
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTAND node, Object data){
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTMINOR node, Object data){
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTADD node, Object data){
    node.childrenAccept(this, data);
    
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

    
    return data;
  }
  public Object visit(ASTSUB node, Object data){
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTMULT node, Object data){
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTDIV node, Object data){
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTEQUAL node, Object data){
    if(node.jjtGetChild(0) instanceof ASTIdentifier) {
      ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(0);
      if(currentTable.exists(new_node.getName()) == null) {
        System.out.println("Semantic error: variable " + new_node.getName() + " doesn't exist.");
      }
    }
    node.childrenAccept(this, data);

    System.out.println("\nAssign: ");
    System.out.println("\t" + node.jjtGetChild(0));
    System.out.println("\t" + node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0).getClass());

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
    if(node.jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier) {
      ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(0).jjtGetChild(0);
      if(currentTable.exists(new_node.getName()) == null) {
        // System.out.println(node.jjtGetChild(0).getClass());
        // System.out.println(node.jjtGetChild(0).jjtGetChild(0).getClass());
        System.out.println("Semantic error: variable " + new_node.getName() + " doesn't exist.");
      } /*else {
        System.out.println("\n!---");
        System.out.println(node.jjtGetChild(0).getClass());
        System.out.println(node.jjtGetChild(0).jjtGetChild(0).getClass());
        System.out.println("variable " + new_node.getName() + " exists.");
        System.out.println("---!");
      }*/
    } /*else {
      System.out.println("\n>-----------");
      System.out.println(node.jjtGetChild(0).getClass());
      System.out.println(node.jjtGetChild(0).jjtGetChild(0).getClass());
      System.out.println("-----------<");
    }*/
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
  public Object visit(ASTAccessingArrayAt node, Object data){
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