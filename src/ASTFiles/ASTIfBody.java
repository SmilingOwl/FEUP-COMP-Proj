/* Generated By:JJTree: Do not edit this line. ASTIfBody.java Version 6.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTIfBody extends SimpleNode {
  public ASTIfBody(int id) {
    super(id);
  }

  public ASTIfBody(Project p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(ProjectVisitor visitor, Object data) {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=98a1850bc58cee8d498a76377f85537c (do not edit this line) */
