/* Generated By:JJTree: Do not edit this line. ASTIsArray.java Version 6.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTIsArray extends SimpleNode {
  public ASTIsArray(int id) {
    super(id);
  }

  public ASTIsArray(Project p, int id) {
    super(p, id);
  }

  public String toString() {
    return "is array";
  }

  /** Accept the visitor. **/
  public Object jjtAccept(ProjectVisitor visitor, Object data) {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=061dfa5f5051e13552085e9dcfd1e874 (do not edit this line) */
