/* Generated By:JJTree: Do not edit this line. ASTExpressionTokenWoIdent.java Version 6.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTExpressionTokenWoIdent extends SimpleNode {
  private String name;

  public ASTExpressionTokenWoIdent(int id) {
    super(id);
  }

  public ASTExpressionTokenWoIdent(Project p, int id) {
    super(p, id);
  }

  public void setName(String n) {
    name = n;
  }

  public String getName() {
    return name;
  }

  public String toString() {
    if(name == null)
      return "";
    if(name.equals("!"))
      return "Operation: !";
    return name;
  }

  /** Accept the visitor. **/
  public Object jjtAccept(ProjectVisitor visitor, Object data) {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=3bf35e7530f7878b7fcaf4be382f65b5 (do not edit this line) */
