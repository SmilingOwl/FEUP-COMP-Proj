/* Generated By:JJTree: Do not edit this line. ASTMethodDeclaration.java Version 6.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTMethodDeclaration extends SimpleNode {
  private String name;
  
  public ASTMethodDeclaration(int id) {
    super(id);
  }

  public ASTMethodDeclaration(Project p, int id) {
    super(p, id);
  }

  public void setName(String n) {
    name = n;
  }

  public String getName() {
    return name;
  }

  public String toString() {
    return "MethodDeclaration: " + name + "()";
  }

  /** Accept the visitor. **/
  public Object jjtAccept(ProjectVisitor visitor, Object data) {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=2a9fc55e59af6b78d76542d87ce4346e (do not edit this line) */
