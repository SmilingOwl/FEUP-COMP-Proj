/* Generated By:JJTree: Do not edit this line. ASTMINOR.java Version 6.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTMINOR extends SimpleNode {
  private String op;

  public ASTMINOR(int id) {
    super(id);
  }

  public ASTMINOR(Project p, int id) {
    super(p, id);
  }

  public void setOp(String n) {
    op = n;
  }

  public String toString() {
    return "Operation: <";
  }
}
/* JavaCC - OriginalChecksum=01153543be00f1cbe2d92cd4c469748a (do not edit this line) */
