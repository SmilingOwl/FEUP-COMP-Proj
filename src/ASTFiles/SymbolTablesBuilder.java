import java.util.ArrayList;
import java.util.LinkedList;

public class SymbolTablesBuilder implements ProjectVisitor {
  private ArrayList<SymbolTable> symbolTables;
  private SymbolTable currentTable;
  private boolean errors = false;
  private boolean show_symbol_tables = false;
  private boolean show_semantic_analysis = true;

  public SymbolTablesBuilder() {
    this.symbolTables = new ArrayList<SymbolTable>();
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
    return "int";
  }

  public Object visit(ASTIdentifier node, Object data) {
    node.childrenAccept(this, data);
    String type = this.currentTable.exists(node.getName());
    return type;
  }

  public Object visit(ASTProgram node, Object data) {
    node.childrenAccept(this, data);
    if (show_symbol_tables && !errors) {
      for (int i = 0; i < this.symbolTables.size(); i++) {
        this.symbolTables.get(i).print();
        System.out.println("\n");
      }
    }
    return data;
  }

  public Object visit(ASTClassDeclaration node, Object data) {
    String name = node.getName();
    SymbolTable table = new SymbolTable(name, "class", null);
    table.set_extends_class(node.getExtends());
    this.currentTable = table;
    this.symbolTables.add(table);
    node.childrenAccept(this, data);
    return data;
  }

  public Object visit(ASTVarDeclaration node, Object data) {
    String type = "", name = "";
    if (node.jjtGetNumChildren() == 2) {
      if (node.jjtGetChild(0) instanceof ASTType) {
        ASTType new_node = (ASTType) node.jjtGetChild(0);
        type = new_node.getName();
        if (new_node.jjtGetNumChildren() == 1)
          type += "[]";
      } else if (node.jjtGetChild(0) instanceof ASTTypeWoIdent) {
        ASTTypeWoIdent new_node = (ASTTypeWoIdent) node.jjtGetChild(0);
        type = new_node.getName();
        if (new_node.jjtGetNumChildren() == 1)
          type += "[]";
      }
      if (node.jjtGetChild(1) instanceof ASTIdentifier) {
        ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(1);
        name = new_node.getName();
      }
    }
    if (this.currentTable.exists_local(name) != null) {
      System.out.println("Semantic Error: variable " + name + " already exists.");
      errors = true;
    } else
      this.currentTable.get_symbols().put(name, type);
    node.childrenAccept(this, data);
    return data;
  }

  public Object visit(ASTVarDeclarationWoIdent node, Object data) {
    if (node.jjtGetParent() instanceof ASTMainMethodBody) {
      ASTMainMethodBody parent_node = (ASTMainMethodBody) node.jjtGetParent();
    }
    String type = "", name = "";
    if (node.jjtGetNumChildren() == 2) {
      if (node.jjtGetChild(0) instanceof ASTType) {
        ASTType new_node = (ASTType) node.jjtGetChild(0);
        type = new_node.getName();
        if (new_node.jjtGetNumChildren() == 1)
          type += "[]";
      } else if (node.jjtGetChild(0) instanceof ASTTypeWoIdent) {
        ASTTypeWoIdent new_node = (ASTTypeWoIdent) node.jjtGetChild(0);
        type = new_node.getName();
        if (new_node.jjtGetNumChildren() == 1)
          type += "[]";
      }
      if (node.jjtGetChild(1) instanceof ASTIdentifier) {
        ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(1);
        name = new_node.getName();
      }
    }
    if (this.currentTable.exists_local(name) != null) {
      System.out.println("Semantic Error: variable " + name + " already exists.");
      errors = true;
    } else
      this.currentTable.get_symbols().put(name, type);
    node.childrenAccept(this, data);
    return data;
  }

  public Object visit(ASTMainDeclaration node, Object data) {
    SymbolTable table = new SymbolTable("main", "main", this.currentTable);
    table.get_args().put(node.getName(), "String[]");
    this.currentTable.get_functions().add(table);
    this.currentTable = table;
    node.childrenAccept(this, data);
    this.currentTable = this.currentTable.get_parent();
    return data;
  }

  public Object visit(ASTMethodDeclaration node, Object data) {
    SymbolTable table = new SymbolTable(node.getName(), "method", this.currentTable);
    this.currentTable.get_functions().add(table);
    this.currentTable = table;
    node.childrenAccept(this, data);
    this.currentTable = this.currentTable.get_parent();
    return data;
  }

  public Object visit(ASTReturn node, Object data) {
    if (node.jjtGetNumChildren() == 1) {
      if (node.jjtGetChild(0) instanceof ASTType) {
        ASTType new_node = (ASTType) node.jjtGetChild(0);
        String type = new_node.getName();
        if (new_node.jjtGetNumChildren() == 1)
          type += "[]";
        this.currentTable.set_return_type(type);
      } else if (node.jjtGetChild(0).jjtGetChild(0) instanceof ASTExpressionToken) {
        // compare type with return type of table.
        ASTExpressionToken new_node = (ASTExpressionToken) node.jjtGetChild(0).jjtGetChild(0);
        if (new_node.jjtGetNumChildren() != 0 && new_node.jjtGetChild(0) instanceof ASTIdentifier) {
          ASTIdentifier new_identifier_node = (ASTIdentifier) new_node.jjtGetChild(0);
          String type = this.currentTable.exists(new_identifier_node.getName());
          if (type != null && !type.equals(this.currentTable.get_return_type()) && show_semantic_analysis) {
            System.out
                .println("Semantic Error: Invalid return value for function: " + this.currentTable.get_name() + ".");
            errors = true;
          }
        } else if (new_node.jjtGetNumChildren() != 0 && new_node.jjtGetChild(0) instanceof ASTIntegerLiteral) {
          if (!this.currentTable.get_return_type().equals("int") && show_semantic_analysis) {
            System.out
                .println("Semantic Error: Invalid return value for function: " + this.currentTable.get_name() + ".");
            errors = true;
          }
        } else if (new_node.getName() != null
            && (new_node.getName().equals("true") || new_node.getName().equals("false"))
            && !this.currentTable.get_return_type().equals("boolean") && show_semantic_analysis) {
          System.out
              .println("Semantic Error: Invalid return value for function: " + this.currentTable.get_name() + ".");
          errors = true;
        }
      }
    }
    node.childrenAccept(this, data);
    return data;
  }

  public Object visit(ASTMethodArgs node, Object data) {
    node.childrenAccept(this, data);
    return data;
  }

  public Object visit(ASTArgument node, Object data) {
    String type = null, name = null;
    if (node.jjtGetNumChildren() == 2) {
      if (node.jjtGetChild(0) instanceof ASTType) {
        ASTType new_node = (ASTType) node.jjtGetChild(0);
        type = new_node.getName();
        if (new_node.jjtGetNumChildren() == 1)
          type += "[]";
      }
      if (node.jjtGetChild(1) instanceof ASTIdentifier) {
        ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(1);
        name = new_node.getName();
      }
    }
    if (this.currentTable.exists_local(name) != null) {
      System.out.println("Semantic Error: variable " + name + " already exists.");
      errors = true;
    } else
      this.currentTable.get_args().put(name, type);
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
    Object answer = node.jjtGetChild(0).jjtAccept(this, data);
    if (answer != null && !answer.equals("boolean") && show_semantic_analysis) {
      System.out.println("Semantic Error: Condition must be boolean in if and while statements.");
      errors = true;
    }
    return "boolean";
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
        if (node.jjtGetChild(0) instanceof ASTType) {
          ASTType new_node = (ASTType) node.jjtGetChild(0);
          type = new_node.getName();
          if (new_node.jjtGetNumChildren() == 1)
            type += "[]";
        } else if (node.jjtGetChild(0) instanceof ASTTypeWoIdent) {
          ASTTypeWoIdent new_node = (ASTTypeWoIdent) node.jjtGetChild(0);
          type = new_node.getName();
          if (new_node.jjtGetNumChildren() == 1)
            type += "[]";
        } else if (node.jjtGetChild(0) instanceof ASTIdentifier) {
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
      if (this.currentTable.exists_local(name) != null) {
        System.out.println("Semantic Error: variable " + name + " already exists.");
        errors = true;
      } else
        this.currentTable.get_symbols().put(name, type);
    } else if(node.jjtGetNumChildren() >= 2 && node.jjtGetChild(0) instanceof ASTIdentifier 
                  && node.jjtGetChild(1).jjtGetNumChildren() > 0
                  && node.jjtGetChild(1).jjtGetChild(0).jjtGetNumChildren() > 0
                  && node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0) instanceof ASTExpressionAuxDot){
          ASTExpressionAuxDot new_dot_node = (ASTExpressionAuxDot) node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0);
          ASTIdentifier new_id_node = (ASTIdentifier) node.jjtGetChild(0);
          String type = this.currentTable.exists(new_id_node.getName());
          if (new_dot_node.getName().equals("length") && !type.equals("int[]")
                 && show_semantic_analysis) {
            System.out.println("Semantic Error: \"length\" only appliable to int[]. In variable " + new_id_node.getName() + ".");
            errors = true;
          } else if (!new_dot_node.getName().equals("length") && show_semantic_analysis
                      && type != null && (type.equals("int") || type.equals("boolean") || type.equals("int[]"))) {
            System.out.println("Semantic Error: primitive type can't be called with functions. In variable " + new_id_node.getName() + ".");
            errors = true;
          }
        }
    node.childrenAccept(this, data);
    return data;
  }

  public Object visit(ASTAND node, Object data) {
    String[] answer = new String[node.jjtGetNumChildren()];
    if (node.jjtGetNumChildren() != 0) {
      for (int i = 0; i < node.jjtGetNumChildren(); i++) {
        answer[i] = (String) node.jjtGetChild(i).jjtAccept(this, data);
        if (answer[i] != null && !answer[i].equals("boolean") && show_semantic_analysis) {
          System.out.println("Semantic Error: Wrong type in && operation: " + answer[i] + ". Expected: boolean.");
          errors = true;
        }
      }
    }
    return "boolean";
  }

  public Object visit(ASTMINOR node, Object data) {
    String[] answer = new String[node.jjtGetNumChildren()];
    if (node.jjtGetNumChildren() != 0) {
      for (int i = 0; i < node.jjtGetNumChildren(); i++) {
        answer[i] = (String) node.jjtGetChild(i).jjtAccept(this, data);
        if (answer[i] != null && !answer[i].equals("int") && show_semantic_analysis) {
          System.out.println("Semantic Error: Wrong type in < operation: " + answer[i] + ". Expected: int.");
          errors = true;
        }
      }
    }
    return "boolean";
  }

  public Object visit(ASTADD node, Object data) {
    String[] answer = new String[node.jjtGetNumChildren()];
    if (node.jjtGetNumChildren() != 0) {
      for (int i = 0; i < node.jjtGetNumChildren(); i++) {
        answer[i] = (String) node.jjtGetChild(i).jjtAccept(this, data);
        if (answer[i] != null && !answer[i].equals("int") && show_semantic_analysis) {
          System.out.println("Semantic Error: Wrong type in + operation: " + answer[i] + ". Expected: int.");
          errors = true;
        }
      }
    }
    return "int";
  }

  public Object visit(ASTSUB node, Object data) {
    String[] answer = new String[node.jjtGetNumChildren()];
    if (node.jjtGetNumChildren() != 0) {
      for (int i = 0; i < node.jjtGetNumChildren(); i++) {
        answer[i] = (String) node.jjtGetChild(i).jjtAccept(this, data);
        if (answer[i] != null && !answer[i].equals("int") && show_semantic_analysis) {
          System.out.println("Semantic Error: Wrong type in - operation: " + answer[i] + ". Expected: int.");
          errors = true;
        }
      }
    }
    return "int";
  }

  public Object visit(ASTMULT node, Object data) {
    String[] answer = new String[node.jjtGetNumChildren()];
    if (node.jjtGetNumChildren() != 0) {
      for (int i = 0; i < node.jjtGetNumChildren(); i++) {
        answer[i] = (String) node.jjtGetChild(i).jjtAccept(this, data);
        if (answer[i] != null && !answer[i].equals("int") && show_semantic_analysis) {
          System.out.println("Semantic Error: Wrong type in * operation: " + answer[i] + ". Expected: int.");
          errors = true;
        }
      }
    }
    return "int";
  }

  public Object visit(ASTDIV node, Object data) {
    String[] answer = new String[node.jjtGetNumChildren()];
    if (node.jjtGetNumChildren() != 0) {
      for (int i = 0; i < node.jjtGetNumChildren(); i++) {
        answer[i] = (String) node.jjtGetChild(i).jjtAccept(this, data);
        if (answer[i] != null && !answer[i].equals("int") && show_semantic_analysis) {
          System.out.println("Semantic Error: Wrong type in / operation: " + answer[i] + ". Expected: int.");
          errors = true;
        }
      }
    }
    return "int";
  }

  public Object visit(ASTEQUAL node, Object data) {
    if (node.jjtGetChild(0) instanceof ASTIdentifier) {
      ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(0);
      if (currentTable.exists(new_node.getName()) == null && show_semantic_analysis) {
        System.out.println("Semantic error: Variable " + new_node.getName() + " doesn't exist.");
        errors = true;
      }
    }
    String[] answer = new String[node.jjtGetNumChildren()];
    if (node.jjtGetNumChildren() != 0) {
      for (int i = 0; i < node.jjtGetNumChildren(); i++) {
        answer[i] = (String) node.jjtGetChild(i).jjtAccept(this, data);
      }
      if (answer[0] == null && show_semantic_analysis) {
        errors = true;
      } else if (node.jjtGetNumChildren() == 2 && !answer[0].equals(answer[1]) && show_semantic_analysis) {
        if (answer[1] != null && !answer[1].equals(this.currentTable.get_parent().get_name())) {
          System.out.println("Semantic Error: Different types in assign operation: " + answer[0] + " and " + answer[1]);
          errors = true;
        } /*else {
          System.out.println("Ignored null in EQUAL. Message to be deleted, only for testing purposes ");
        }*/
      } else if (node.jjtGetNumChildren() == 3 && answer[2] != null && !answer[2].equals("int") && show_semantic_analysis) {
        if (answer[2] != null) {
          System.out.println("Semantic Error: Different types in assign operation: int[] and " + answer[1]);
          errors = true;
        } /*else {
          System.out.println("Ignored null in EQUAL. Message to be deleted, only for testing purposes");
        }*/
      }
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
    if(node.jjtGetNumChildren() == 2) {
      if(node.jjtGetChild(0) instanceof ASTExpressionToken) {
        ASTExpressionToken new_node = (ASTExpressionToken) node.jjtGetChild(0);
        if(new_node.getName() != null && new_node.getName().equals("this") && node.jjtGetChild(1).jjtGetChild(0) instanceof ASTExpressionAuxDot) {
          ASTExpressionAuxDot new_dot_node = (ASTExpressionAuxDot) node.jjtGetChild(1).jjtGetChild(0);
          if(new_dot_node.getName().equals("length")) {
            node.childrenAccept(this, data);
            return "int";
          }
        } else if(node.jjtGetChild(1) instanceof ASTAccessingArrayAt) {
          ASTIdentifier new_id_node = (ASTIdentifier) node.jjtGetChild(0).jjtGetChild(0);
          if (currentTable.exists(new_id_node.getName()) == null && show_semantic_analysis) {
            System.out.println("Semantic error: Variable " + new_id_node.getName() + " doesn't exist.");
            errors = true;
          }
          node.childrenAccept(this, data);
          return "int";
        } else if(new_node.jjtGetNumChildren() != 0 && new_node.jjtGetChild(0) instanceof ASTIdentifier 
                  && node.jjtGetChild(1).jjtGetChild(0) instanceof ASTExpressionAuxDot){
          ASTExpressionAuxDot new_dot_node = (ASTExpressionAuxDot) node.jjtGetChild(1).jjtGetChild(0);
          ASTIdentifier new_id_node = (ASTIdentifier) new_node.jjtGetChild(0);
          String type = this.currentTable.exists(new_id_node.getName());
          if (new_dot_node.getName().equals("length") && !type.equals("int[]")
                 && show_semantic_analysis) {
            System.out.println("Semantic Error: \"length\" only appliable to int[]. In variable " + new_id_node.getName() + ".");
            errors = true;
          } else if (!new_dot_node.getName().equals("length") && show_semantic_analysis
                      && type != null && (type.equals("int") || type.equals("boolean") || type.equals("int[]"))) {
            System.out.println("Semantic Error: primitive type can't be called with functions. In variable " + new_id_node.getName() + ".");
            errors = true;
          }
        }
      }
    } else if(node.jjtGetNumChildren() == 1) {
      if(node.jjtGetChild(0).jjtGetNumChildren() == 1 && node.jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier) {
        ASTIdentifier new_id_node = (ASTIdentifier) node.jjtGetChild(0).jjtGetChild(0);
        if (currentTable.exists(new_id_node.getName()) == null && show_semantic_analysis) {
          System.out.println("Semantic error: Variable " + new_id_node.getName() + " doesn't exist.");
          errors = true;
        }
      }
      return node.jjtGetChild(0).jjtAccept(this, data);
    }
    node.childrenAccept(this, data);
    return data;
  }

  public Object visit(ASTExpressionRestOfClausesWoIdent node, Object data) {
    if (node.jjtGetNumChildren() == 2) {
      if (node.jjtGetChild(0) instanceof ASTExpressionToken) {
        ASTExpressionToken new_node = (ASTExpressionToken) node.jjtGetChild(0);
        if (new_node.getName() != null && new_node.getName().equals("this")
            && node.jjtGetChild(1).jjtGetChild(0) instanceof ASTExpressionAuxDot) {
          ASTExpressionAuxDot new_dot_node = (ASTExpressionAuxDot) node.jjtGetChild(1).jjtGetChild(0);
          if (new_dot_node.getName().equals("length")) {
            node.childrenAccept(this, data);
            return "int";
          }
        } else if (node.jjtGetChild(1) instanceof ASTAccessingArrayAt) {
          ASTIdentifier new_id_node = (ASTIdentifier) node.jjtGetChild(0).jjtGetChild(0);
          if (currentTable.exists(new_id_node.getName()) == null && show_semantic_analysis) {
            System.out.println("Semantic error: Variable " + new_id_node.getName() + " doesn't exist.");
            errors = true;
          }
          node.childrenAccept(this, data);
          return "int";
        }
      }
    } else if (node.jjtGetNumChildren() == 1) {
      if (node.jjtGetChild(0).jjtGetNumChildren() == 1 && node.jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier) {
        ASTIdentifier new_id_node = (ASTIdentifier) node.jjtGetChild(0).jjtGetChild(0);
        if (currentTable.exists(new_id_node.getName()) == null && show_semantic_analysis) {
          System.out.println("Semantic error: Variable " + new_id_node.getName() + " doesn't exist.");
          errors = true;
        }
      }
      return node.jjtGetChild(0).jjtAccept(this, data);
    }
    node.childrenAccept(this, data);
    return data;
  }

  public Object visit(ASTAcessing node, Object data) {
    node.childrenAccept(this, data);
    return data;
  }

  public Object visit(ASTAccessingArrayAt node, Object data) {
    String type = null;
    if (node.jjtGetNumChildren() == 1) {
      type = (String) node.jjtGetChild(0).jjtAccept(this, data);
      if (type != null && !type.equals("int[]") && !type.equals("int") && show_semantic_analysis) {
        System.out.println("Semantic Error: Expected int in array access. Received type: " + type + ".");
      }
    }
    return data;
  }

  public Object visit(ASTExpressionAuxDot node, Object data) {
    node.childrenAccept(this, data);
    if (node.getName().equals("length")) {
      return "int";
    }
    return data;
  }

  public Object visit(ASTExpressionToken node, Object data) {

    if (node.jjtGetNumChildren() == 1 && node.getName() == null) {
      return node.jjtGetChild(0).jjtAccept(this, data);
    } else if (node.getName() != null && node.getName().equals("!")) {
      if (node.jjtGetChild(0) instanceof ASTExpressionToken && node.jjtGetChild(0).jjtGetNumChildren() > 0) {
        String answer = (String) node.jjtGetChild(0).jjtGetChild(0).jjtAccept(this, data);
        if (answer != null && !answer.equals("boolean") && show_semantic_analysis) {
          System.out.println("Semantic Error: Expected boolean in NOT.");
          errors = true;
        }
      }
      return "boolean";
    } else if (node.getName() != null && (node.getName().equals("true") || node.getName().equals("false"))) {
      return "boolean";
    } else if (node.getName() != null && node.getName().equals("new")) {
      return node.jjtGetChild(0).jjtAccept(this, data);
    }

    return data;
  }

  public Object visit(ASTExpressionTokenWoIdent node, Object data) {

    // node.childrenAccept(this, data);
    if (node.jjtGetNumChildren() == 1 && node.getName() == null) {
      return node.jjtGetChild(0).jjtAccept(this, data);
    } else if (node.getName() != null && node.getName().equals("!")) {
      if (node.jjtGetChild(0) instanceof ASTExpressionToken) {
        String answer = (String) node.jjtGetChild(0).jjtGetChild(0).jjtAccept(this, data);
        if (answer != null && !answer.equals("boolean") && show_semantic_analysis) {
          System.out.println("Semantic Error: Expected boolean in NOT.");
          errors = true;
        }
      }
      return "boolean";
    } else if (node.getName() != null && (node.getName().equals("true") || node.getName().equals("false"))) {
      return "boolean";
    }
    return data;
  }

  public Object visit(ASTExpressionNew node, Object data) {

    node.childrenAccept(this, data);
    if (node.jjtGetChild(0) instanceof ASTIdentifier) {
      ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(0);
      return new_node.getName();
    }
    return "int[]";
  }

  public boolean get_errors() {
    return errors;
  }

  public ArrayList<SymbolTable> get_symbol_tables() {
    return this.symbolTables;
  }
}