/* Generated By:JJTree: Do not edit this line. ASTWhileStatement.java Version 6.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTWhileStatement extends SimpleNode {
  public ASTWhileStatement(int id) {
    super(id);
  }

  public ASTWhileStatement(Project p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(ProjectVisitor visitor, Object data) {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=13d46c3861bdb05efaa2e4188364ed80 (do not edit this line) */
