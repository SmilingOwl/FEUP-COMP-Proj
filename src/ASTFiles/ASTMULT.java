/* Generated By:JJTree: Do not edit this line. ASTMULT.java Version 6.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTMULT extends SimpleNode {
  private String op;

  public ASTMULT(int id) {
    super(id);
  }

  public ASTMULT(Project p, int id) {
    super(p, id);
  }

  public void setOp(String n) {
    op = n;
  }

  public String toString() {
    op = "*";
    return "Operation: *";
  }
  
  /** Accept the visitor. **/
  public Object jjtAccept(ProjectVisitor visitor, Object data) {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=fbaa08be3d52fdbeb549b28f96242ae0 (do not edit this line) */
