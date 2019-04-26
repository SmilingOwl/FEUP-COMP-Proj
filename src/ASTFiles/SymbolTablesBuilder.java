import java.util.ArrayList;
import java.util.LinkedList;

public class SymbolTablesBuilder implements ProjectVisitor{
  private ArrayList<SymbolTable> symbolTables;
  private SymbolTable currentTable;
  private boolean errors = false;
  private boolean show_symbol_tables = false;
  private boolean show_semantic_analysis = false;

  public SymbolTablesBuilder() {
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
    return "int";
  }
  public Object visit(ASTIdentifier node, Object data){
    node.childrenAccept(this, data);
    String type = this.currentTable.exists(node.getName());
    return type;
  }
  public Object visit(ASTProgram node, Object data){
    node.childrenAccept(this, data);
    if(show_symbol_tables && !errors) {
        for(int i = 0; i < this.symbolTables.size(); i++) {
        this.symbolTables.get(i).print();
        System.out.println("\n");
        }
    }
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
      errors = true;
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
      errors = true;
    }
    else this.currentTable.get_symbols().put(name, type);
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTMainDeclaration node, Object data){
    SymbolTable table = new SymbolTable("main", "main", this.currentTable);
    this.currentTable.get_functions().put("main", table);
    this.currentTable = table;
    node.childrenAccept(this, data);
    this.currentTable = this.currentTable.get_parent();
    return data;
  }
  public Object visit(ASTMethodDeclaration node, Object data){
    SymbolTable table = new SymbolTable(node.getName(), "method", this.currentTable);
    this.currentTable.get_functions().put(node.getName(), table);
    this.currentTable = table;
    node.childrenAccept(this, data);
    this.currentTable = this.currentTable.get_parent();
    return data;
  }
  public Object visit(ASTReturn node, Object data){
    if(node.jjtGetNumChildren() == 1) {
      if(node.jjtGetChild(0) instanceof ASTType) {
        ASTType new_node = (ASTType) node.jjtGetChild(0);
        String type = new_node.getName();
        if(new_node.jjtGetNumChildren() == 1)
                type+="[]";
        this.currentTable.set_return_type(type);
      } else if(node.jjtGetChild(0).jjtGetChild(0) instanceof ASTExpressionToken) {
        //compare type with return type of table.
        ASTExpressionToken new_node = (ASTExpressionToken) node.jjtGetChild(0).jjtGetChild(0);
        if(new_node.jjtGetNumChildren() != 0 && new_node.jjtGetChild(0) instanceof ASTIdentifier) {
          ASTIdentifier new_identifier_node = (ASTIdentifier) new_node.jjtGetChild(0);
          String type = this.currentTable.exists(new_identifier_node.getName());
          if(type != null && !type.equals(this.currentTable.get_return_type()) && show_semantic_analysis) {
            System.out.println("Semantic Error: Invalid return value for function: " + this.currentTable.get_name() + ".");
            errors = true;
          }
        } else if(new_node.jjtGetNumChildren() != 0 && new_node.jjtGetChild(0) instanceof ASTIntegerLiteral) {
          if(!this.currentTable.get_return_type().equals("int") && show_semantic_analysis) {
            System.out.println("Semantic Error: Invalid return value for function: " + this.currentTable.get_name() + ".");
            errors = true;
          }
        } else if(new_node.getName() != null && (new_node.getName().equals("true") || new_node.getName().equals("false")) 
            && !this.currentTable.get_return_type().equals("boolean") && show_semantic_analysis) {
          System.out.println("Semantic Error: Invalid return value for function: " + this.currentTable.get_name() + ".");
          errors = true;
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
      errors = true;
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
    return "boolean";
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
        errors = true;
      } else this.currentTable.get_symbols().put(name, type);
    }
    node.childrenAccept(this, data);
    return data;
  }
   public Object visit(ASTAND node, Object data) {
    node.childrenAccept(this, data);
    String type = null;
    String name = null;
    if (node.jjtGetNumChildren() == 2) {

      if ((node.jjtGetChild(0) instanceof ASTADD | node.jjtGetChild(0) instanceof ASTSUB
          | node.jjtGetChild(0) instanceof ASTMULT | node.jjtGetChild(0) instanceof ASTDIV
          | (node.jjtGetChild(0) instanceof ASTExpressionRestOfClauses && node.jjtGetChild(0).jjtGetNumChildren() > 0
                && node.jjtGetChild(0).jjtGetChild(0).jjtGetNumChildren() > 0
                && node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTIntegerLiteral)
          | node.jjtGetChild(1) instanceof ASTADD | node.jjtGetChild(1) instanceof ASTSUB 
          | node.jjtGetChild(1) instanceof ASTMULT | node.jjtGetChild(1) instanceof ASTDIV
          | (node.jjtGetChild(1) instanceof ASTExpressionRestOfClauses && node.jjtGetChild(1).jjtGetNumChildren() > 0
                && node.jjtGetChild(1).jjtGetChild(0).jjtGetNumChildren() > 0
                && node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0) instanceof ASTIntegerLiteral)) 
          && show_semantic_analysis){
        System.out.println("Error!");
        errors = true;
      }

      else if (node.jjtGetChild(0) instanceof ASTExpressionRestOfClauses && node.jjtGetChild(1).jjtGetNumChildren() > 0
                && node.jjtGetChild(1).jjtGetChild(0).jjtGetNumChildren() > 0
                && node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier) {
        ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0);
        name = new_node.getName();
        type = this.currentTable.exists(name);

        if (type != null && !type.equals("boolean") && show_semantic_analysis){
          System.out.println("Error!");
          errors = true;
        }
      }
    }
    return "boolean";
  }

  public Object visit(ASTMINOR node, Object data) {
    node.childrenAccept(this, data);
    String name = null;
    String type = null;
    if (node.jjtGetNumChildren() == 2) {
        if (node.jjtGetChild(0) instanceof ASTExpressionRestOfClauses 
                && node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTIntegerLiteral) {
            if (node.jjtGetChild(1) instanceof ASTExpressionRestOfClauses
                    && node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier) {

              ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0);
              name = new_node.getName();
              type = this.currentTable.exists(name);

              if (type.equals("boolean") && show_semantic_analysis) {
                  System.out.println("Error!");
                  errors = true;
              }
            }
        } else if (node.jjtGetChild(0) instanceof ASTExpressionRestOfClauses 
                    && node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier) {

            ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0);
            name = new_node.getName();
            type = this.currentTable.exists(name);

            if (type != null && type.equals("boolean") && show_semantic_analysis) {
                System.out.println("Error!");
                errors = true;
            }
        }
    }
    return "boolean";
  }

  public Object visit(ASTADD node, Object data) {
    node.childrenAccept(this, data);
    String name = null;
    String type = null;
    if (node.jjtGetNumChildren() == 2) {
        if (node.jjtGetChild(0) instanceof ASTExpressionRestOfClauses 
                && node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTIntegerLiteral) {
            if (node.jjtGetChild(1) instanceof ASTExpressionRestOfClauses
                    && node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier) {

              ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0);
              name = new_node.getName();
              type = this.currentTable.exists(name);

              if (type.equals("boolean") && show_semantic_analysis) {
                System.out.println("Error!");
                errors = true;
              }
            }
        } else if (node.jjtGetChild(0) instanceof ASTExpressionRestOfClauses 
                    && node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier) {

            ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0);
            name = new_node.getName();
            type = this.currentTable.exists(name);

            if (type != null && type.equals("boolean") && show_semantic_analysis) {
                System.out.println("Error!");
                errors = true;
            }
        }
    }
    return "int";
  }

  public Object visit(ASTSUB node, Object data) {
    node.childrenAccept(this, data);
    String name = null;
    String type = null;
    if (node.jjtGetNumChildren() == 2) {
        if (node.jjtGetChild(0) instanceof ASTExpressionRestOfClauses 
                && node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTIntegerLiteral) {
            if (node.jjtGetChild(1) instanceof ASTExpressionRestOfClauses
                    && node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier) {

              ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0);
              name = new_node.getName();
              type = this.currentTable.exists(name);

              if (type.equals("boolean") && show_semantic_analysis) {
                System.out.println("Error!");
                errors = true;
              }
            }
        } else if (node.jjtGetChild(0) instanceof ASTExpressionRestOfClauses 
                    && node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier) {

            ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0);
            name = new_node.getName();
            type = this.currentTable.exists(name);

            if (type != null && type.equals("boolean") && show_semantic_analysis) {
                System.out.println("Error!");
                errors = true;
            }
        }
    }
    return "int";
  }

  public Object visit(ASTMULT node, Object data) {
    node.childrenAccept(this, data);
    String name = null;
    String type = null;
    if (node.jjtGetNumChildren() == 2) {
        if (node.jjtGetChild(0) instanceof ASTExpressionRestOfClauses 
                && node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTIntegerLiteral) {
          if (node.jjtGetChild(1) instanceof ASTExpressionRestOfClauses
                    && node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier) {

            ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0);
            name = new_node.getName();
            type = this.currentTable.exists(name);

            if (type != null && type.equals("boolean") && show_semantic_analysis) {
                System.out.println("Error!");
                errors = true;
            }
          }
        } else if (node.jjtGetChild(0) instanceof ASTExpressionRestOfClauses 
                    && node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier) {

            ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0);
            name = new_node.getName();
            type = this.currentTable.exists(name);

            if (type != null && type.equals("boolean") && show_semantic_analysis) {
                System.out.println("Error!");
                errors = true;
            }
        }
    }
    return "int";
  }

  public Object visit(ASTDIV node, Object data) {
    node.childrenAccept(this, data);
    String name = null;
    String type = null;
    if (node.jjtGetNumChildren() == 2) {
        if (node.jjtGetChild(0) instanceof ASTExpressionRestOfClauses 
                && node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTIntegerLiteral) {
          if (node.jjtGetChild(1) instanceof ASTExpressionRestOfClauses
                    && node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier) {

            ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0);
            name = new_node.getName();
            type = this.currentTable.exists(name);

            if (type != null && type.equals("boolean") && show_semantic_analysis) {
                System.out.println("Error!");
                errors = true;
            }
          }
        } else if (node.jjtGetChild(0) instanceof ASTExpressionRestOfClauses 
                    && node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier) {

            ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0);
            name = new_node.getName();
            type = this.currentTable.exists(name);

            if (type != null && type.equals("boolean") && show_semantic_analysis) {
                System.out.println("Error!");
                errors = true;
            }
        }
    }
    return "int";
  }
  public Object visit(ASTEQUAL node, Object data){
    if(node.jjtGetChild(0) instanceof ASTIdentifier) {
      ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(0);
      if(currentTable.exists(new_node.getName()) == null && show_semantic_analysis) {
        System.out.println("Semantic error: variable " + new_node.getName() + " doesn't exist.");
        errors = true;
      }
    }
    String[] answer = new String[node.jjtGetNumChildren()]; //all answers must be the same
    if (node.jjtGetNumChildren() != 0) {
      for (int i = 0; i < node.jjtGetNumChildren(); i++) {
        answer[i] = (String) node.jjtGetChild(i).jjtAccept(this, data);
      }
      if(answer[0] == null) {
        System.out.println("Semantic Error: Variable undefined in assign operation.");
        errors = true;
      } else if(node.jjtGetNumChildren() == 2 && !answer[0].equals(answer[1])) {
        System.out.println("Semantic Error: Different types in assign operation: " + answer[0] + " and " + answer[1]);
        errors = true;
      } else if(node.jjtGetNumChildren() == 3 && !answer[2].equals("int")) {
        System.out.println("Semantic Error: Different types in assign operation: int[] and " + answer[1]);
        errors = true;
      }
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
        errors = true;
      }
    }
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTExpressionRestOfClausesWoIdent node, Object data){
    if(node.jjtGetNumChildren() != 0 && node.jjtGetChild(0).jjtGetNumChildren() != 0 && node.jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier) {
      ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(0).jjtGetChild(0);
      if(currentTable.exists(new_node.getName()) == null && show_semantic_analysis) {
        System.out.println("Semantic error: variable " + new_node.getName() + " doesn't exist.");
        errors = true;
      }
    }
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTAcessing node, Object data){
    node.childrenAccept(this, data);
    return data;
  }
  public Object visit(ASTAccessingArrayAt node, Object data) {
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

  public boolean get_errors() {
    return errors;
  }

  public ArrayList<SymbolTable> get_symbol_tables() {
      return this.symbolTables;
  }
}