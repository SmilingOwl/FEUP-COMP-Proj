/* Generated By:JJTree: Do not edit this line. ASTExpressionMult.java Version 6.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTExpressionMult extends SimpleNode {
  private String op;
  
  public ASTExpressionMult(int id) {
    super(id);
  }

  public ASTExpressionMult(Project p, int id) {
    super(p, id);
  }
  
  public void setOp(String n) {
    op = n;
  }

  public String toString() {
    if(op == null)
      return "";
    return "Operation: " + op;
  }
}
/* JavaCC - OriginalChecksum=2c06982c2ce498b79bb9ba7ad96f3d23 (do not edit this line) */
