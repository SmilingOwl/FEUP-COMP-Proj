/* Generated By:JJTree: Do not edit this line. ASTExpressionAuxDot.java Version 6.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTExpressionAuxDot extends SimpleNode {
  private String name;

  public ASTExpressionAuxDot(int id) {
    super(id);
  }

  public ASTExpressionAuxDot(Project p, int id) {
    super(p, id);
  }

  public void setName(String n) {
    name = n;
  }

  public String toString() {
    if(name.equals("length"))
      return "length";
    return name + "()";
  }

  /** Accept the visitor. **/
  public Object jjtAccept(ProjectVisitor visitor, Object data) {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=4c600ad96a25f932d531116573da93ec (do not edit this line) */
