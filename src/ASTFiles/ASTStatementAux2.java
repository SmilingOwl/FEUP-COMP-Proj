/* Generated By:JJTree: Do not edit this line. ASTStatementAux2.java Version 6.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTStatementAux2 extends SimpleNode {
  private String operation;
  private String name;

  public ASTStatementAux2(int id) {
    super(id);
  }

  public ASTStatementAux2(Project p, int id) {
    super(p, id);
  }

  public void setOperation(String n) {
    operation = n;
  }

  public void setName(String n) {
    name = n;
  }

  public String toString() {
    if(name != null){
      return "VarDeclaration: " + name;
    }

    if(operation == "="){
      return "";
    }
    else if(operation == "."){
      return "";
    }
    else if(operation == "["){
      return "";
    }
    return "ERROR STATEMENT!";
  }

}
/* JavaCC - OriginalChecksum=4c5af050f0ff749ef65d288922a275e8 (do not edit this line) */