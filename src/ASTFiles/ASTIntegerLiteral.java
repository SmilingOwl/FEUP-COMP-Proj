/* Generated By:JJTree: Do not edit this line. ASTIntegerLiteral.java Version 6.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTIntegerLiteral extends SimpleNode {
  private String name;
  private int value;

  public ASTIntegerLiteral(int id) {
    super(id);
  }

  public ASTIntegerLiteral(Project p, int id) {
    super(p, id);
  }

  public void setName(String n) {
    name = n;
  }

  public int getValue() {
    return value;
  }

  public String toString() {
    return "Integer Literal: " + name;
  }

  /** Accept the visitor. **/
  public Object jjtAccept(ProjectVisitor visitor, Object data) {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=0547868bdca37a20e842192edda1687f (do not edit this line) */
