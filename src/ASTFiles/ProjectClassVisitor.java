import java.util.HashMap;

public class ProjectClassVisitor implements ProjectVisitor {
  private HashMap<String, String> symbolTable = new HashMap<String, String>();
  private SymbolTable currentTable;
  
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
    node.childrenAccept(this, data);
    return data;
  }

  public Object visit(ASTVarDeclaration node, Object data) {
    node.childrenAccept(this, data);
    System.out.println("Inside VarDeclaration");
    if (node.jjtGetParent() instanceof ASTClassDeclaration) {
      ASTClassDeclaration parent_node = (ASTClassDeclaration) node.jjtGetParent();
      System.out.println(" Belongs to class: " + parent_node.getName());
    }
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
    System.out.println(" Type = " + type + "\n Name = " + name + "\n");
    symbolTable.put(name, type);
    return data;
  }

  public Object visit(ASTVarDeclarationWoIdent node, Object data) {
    node.childrenAccept(this, data);
    System.out.println("Inside VarDeclarationWoIdent");
    if (node.jjtGetParent() instanceof ASTMainMethodBody) {
      ASTMainMethodBody parent_node = (ASTMainMethodBody) node.jjtGetParent();
      // System.out.println(" Belongs to class: " + parent_node.getName());
    }
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
    System.out.println(" Type = " + type + "\n Name = " + name + "\n");
    symbolTable.put(name, type);
    return data;
  }

  public Object visit(ASTMainDeclaration node, Object data) {
    node.childrenAccept(this, data);
    return data;
  }

  public Object visit(ASTMethodDeclaration node, Object data) {
    node.childrenAccept(this, data);
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
    node.childrenAccept(this, data);
    if (node.get_type().equals("VarDeclaration")) {
      System.out.println("Inside ASTStatementStartIdent for VarDeclaration");
      /*
       * if(node.jjtGetParent() instanceof ASTClassDeclaration) { ASTClassDeclaration
       * parent_node = (ASTClassDeclaration) node.jjtGetParent();
       * System.out.println(" Belongs to class: " + parent_node.getName()); }
       */
      String type = "", name = "";
      System.out.println("Num children: " + node.jjtGetNumChildren());
      if (node.jjtGetNumChildren() == 2) {
        if (node.jjtGetChild(0) instanceof ASTType) {
          ASTType new_node = (ASTType) node.jjtGetChild(0);
          type = new_node.getName();
        } else if (node.jjtGetChild(0) instanceof ASTTypeWoIdent) {
          ASTTypeWoIdent new_node = (ASTTypeWoIdent) node.jjtGetChild(0);
          type = new_node.getName();
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
      } else
        System.out.println("child type = " + node.jjtGetChild(0).getClass());
      System.out.println(" Type = " + type + "\n Name = " + name + "\n");
      symbolTable.put(name, type);
    }
    return data;
  }

  public Object visit(ASTAND node, Object data) {
    String type = null;
    String name = null;
    if (node.jjtGetNumChildren() == 2) {

      if (node.jjtGetChild(0) instanceof ASTADD | node.jjtGetChild(0) instanceof ASTSUB
          | node.jjtGetChild(0) instanceof ASTMULT | node.jjtGetChild(0) instanceof ASTDIV
          | node.jjtGetChild(0) instanceof ASTIntegerLiteral | node.jjtGetChild(1) instanceof ASTADD
          | node.jjtGetChild(1) instanceof ASTSUB | node.jjtGetChild(1) instanceof ASTMULT
          | node.jjtGetChild(1) instanceof ASTDIV | node.jjtGetChild(1) instanceof ASTIntegerLiteral)
        System.out.println("Error!");

      else if (node.jjtGetChild(0) instanceof ASTIdentifier) {
        ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(1);
        name = new_node.getName();
        type = this.currentTable.exists(name);

        if (type.equals("int"))
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

          if (type.equals("boolean"))
            System.out.println("Error!");
        }

      } else if (node.jjtGetChild(0) instanceof ASTIdentifier) {

        ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(0);
        name = new_node.getName();
        type = this.currentTable.exists(name);

        if (type.equals("boolean")) {
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

    if (node.jjtGetNumChildren() == 2) {

      if (node.jjtGetChild(0) instanceof ASTIntegerLiteral) {

        if (node.jjtGetChild(1) instanceof ASTIdentifier) {

          ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(1);
          name = new_node.getName();
          type = this.currentTable.exists(name);

          if (type.equals("boolean"))
            System.out.println("Error!");
        }

      } else if (node.jjtGetChild(0) instanceof ASTIdentifier) {

        ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(0);
        name = new_node.getName();
        type = this.currentTable.exists(name);

        if (type.equals("boolean")) {
          System.out.println("Error!");
        }
      }
    }
    return data;
  }

  public Object visit(ASTSUB node, Object data) {
    String name = null;
    String type = null;
    node.childrenAccept(this, data);

    if (node.jjtGetNumChildren() == 2) {

      if (node.jjtGetChild(0) instanceof ASTIntegerLiteral) {

        if (node.jjtGetChild(1) instanceof ASTIdentifier) {

          ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(1);
          name = new_node.getName();
          type = this.currentTable.exists(name);

          if (type.equals("boolean"))
            System.out.println("Error!");
        }

      } else if (node.jjtGetChild(0) instanceof ASTIdentifier) {

        ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(0);
        name = new_node.getName();
        type = this.currentTable.exists(name);

        if (type.equals("boolean")) {
          System.out.println("Error!");
        }
      }
    }
    return data;
  }

  public Object visit(ASTMULT node, Object data) {
    String name = null;
    String type = null;
    node.childrenAccept(this, data);

    if (node.jjtGetNumChildren() == 2) {

      if (node.jjtGetChild(0) instanceof ASTIntegerLiteral) {

        if (node.jjtGetChild(1) instanceof ASTIdentifier) {

          ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(1);
          name = new_node.getName();
          type = this.currentTable.exists(name);

          if (type.equals("boolean"))
            System.out.println("Error!");
        }

      } else if (node.jjtGetChild(0) instanceof ASTIdentifier) {

        ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(0);
        name = new_node.getName();
        type = this.currentTable.exists(name);

        if (type.equals("boolean")) {
          System.out.println("Error!");
        }
      }
    }
    return data;
  }

  public Object visit(ASTDIV node, Object data) {
    String name = null;
    String type = null;
    node.childrenAccept(this, data);

    if (node.jjtGetNumChildren() == 2) {

      if (node.jjtGetChild(0) instanceof ASTIntegerLiteral) {

        if (node.jjtGetChild(1) instanceof ASTIdentifier) {

          ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(1);
          name = new_node.getName();
          type = this.currentTable.exists(name);

          if (type.equals("boolean"))
            System.out.println("Error!");
        }

      } else if (node.jjtGetChild(0) instanceof ASTIdentifier) {

        ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(0);
        name = new_node.getName();
        type = this.currentTable.exists(name);

        if (type.equals("boolean")) {
          System.out.println("Error!");
        }
      }
    }
    return data;
  }

  public Object visit(ASTEQUAL node, Object data) {
    node.childrenAccept(this, data);
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
    
    if(node.jjtGetNumChildren() == 1){

      if (node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0) instanceof ASTIdentifier) {
       
        ASTIdentifier new_node = (ASTIdentifier) node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0);
        name = new_node.getName();
        type = this.currentTable.exists(name);

        if (!type.equals("int"))
          System.out.println("Error!");
      }

    }else{
      //expression...
    }
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
}