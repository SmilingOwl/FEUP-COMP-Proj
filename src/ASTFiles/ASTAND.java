/* Generated By:JJTree: Do not edit this line. ASTAND.java Version 6.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTAND extends SimpleNode {
  private String op;

  public ASTAND(int id) {
    super(id);
  }

  public ASTAND(Project p, int id) {
    super(p, id);
  }

  public void setOp(String n) {
    op = n;
  }

  public String toString() {
    op = "&&";
    return "Operation: &&";
  }

  /** Accept the visitor. **/
  public Object jjtAccept(ProjectVisitor visitor, Object data) {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=129f78fc8ba2177d8231d0c134a706ab (do not edit this line) */
