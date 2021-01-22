import java.util.*; 

public class Parser{
    private Stack<Node> stack = new Stack<Node>();
    ArrayList<Token> screenedTokens;
    int nextTokenIndex;

    public AST getAST(ArrayList<Token> screenedTokens){
        this.screenedTokens = screenedTokens;
        nextTokenIndex = 0;

        Winzig();

        AST ast = new AST(stack.pop());
        return ast;
    }

    private void Winzig(){
        read("program");
        Name();
        read(":");
        Consts();
        Types();
        Dclns();
        SubProgs();
        Body();
        Name();
        read(".");

        buildTree("program", 7);
    }

    private void Consts(){
        switch (peekNextToken().tokenType.toString()){

            case "const":
                read("const");
                Const();
                int numChildren = 1;
                while (peekNextToken().tokenType.toString().equals(",")){
                    read(",");
                    Const();
                    numChildren++;
                }
                read(";");

                buildTree("consts", numChildren);
                break;

            default:
                buildTree("consts", 0);
        }
    }

    private void Const(){
        Name();
        read("=");
        ConstValue();

        buildTree("const", 2);
    }

    private void ConstValue(){
        switch (peekNextToken().tokenType.toString()){

            case "<integer>":
                read(TokenType.INTEGER);
                break;

            case "<char>":
                read(TokenType.CHAR);
                break;

            default:
                Name();
        }
    }
    
    private void Types(){
        switch (peekNextToken().tokenType.toString()){

            case "type":
                read("type");
                Type();         //since one or more required
                read(";");
                int numChildren = 1;
                while (peekNextToken().tokenType.toString().equals("<identifier>")){
                    Type();
                    read(";");
                    numChildren++;
                }

                buildTree("types", numChildren);
                break;

            default:
                buildTree("types", 0);
        }
    }

    private void Type(){
        Name();
        read("=");
        LitList();

        buildTree("type", 2);
    }

    private void LitList(){
        read("(");
        Name();
        int numChildren = 1;
        while (peekNextToken().tokenType.toString().equals(",")){
            read(",");
            Name();
            numChildren++;
        }
        read(")");

        buildTree("lit", numChildren);
    }

    private void SubProgs(){
        int numChildren = 0;    // 0 or more functions
        while (peekNextToken().tokenType.toString().equals("function")){
            Fcn();
            numChildren++;
        }
        buildTree("subprogs", numChildren);
    }

    private void Fcn(){
        read("function");
        Name();
        read("(");
        Params();
        read(")");
        read(":");
        Name();
        read(";");
        Consts();
        Types();
        Dclns();
        Body();
        Name();
        read(";");

        buildTree("fcn", 8);
    }

    private void Params(){
        Dcln();
        int numChildren = 1;
        while (peekNextToken().tokenType.toString().equals(";")){
            read(";");
            Dcln();
            numChildren++;
        }

        buildTree("params", numChildren);
    }

    private void Dclns(){
        switch (peekNextToken().tokenType.toString()){

            case "var":
                read("var");
                Dcln();         // since one or more (+)
                read(";");
                int numChildren = 1;
                while (peekNextToken().tokenType.toString().equals("<identifier>")){
                    Dcln();
                    read(";");
                    numChildren++;
                }

                buildTree("dclns", numChildren);
                break;

            default:
                buildTree("dclns", 0);
        }
    }

    private void Dcln(){
        Name();
        int numChildren = 1;
        while (peekNextToken().tokenType.toString().equals(",")){
            read(",");
            Name();
            numChildren++;
        }
        read(":");
        Name();
        numChildren++;

        buildTree("var", numChildren);
    }

    private void Body(){
        read("begin");
        Statement(); 
        int numChildren = 1;
        while (peekNextToken().tokenType.toString().equals(";")){
            read(";");
            Statement();
            numChildren++;
        }
        read("end");
        buildTree("block", numChildren);
    }

    private void Statement(){
        int numChildren;
        switch (peekNextToken().tokenType.toString()){
            case "<identifier>":
                Assignment();
                break;

            case "output":
                read("output");
                read("(");
                OutExp();
                numChildren = 1;
                while (peekNextToken().tokenType.toString().equals(",")){
                    read(",");
                    OutExp();
                    numChildren++;
                }
                read(")");
                buildTree("output", numChildren);
                break;

            case "if":
                read("if");
                Expression();
                read("then");
                Statement();
                numChildren = 2;
                if(peekNextToken().tokenType.toString().equals("else")){
                    read("else");
                    Statement();
                    numChildren++;
                }
                buildTree("if", numChildren);
                break;
            
            case "while":
                read("while");
                Expression();
                read("do");
                Statement();
                buildTree("while", 2);
                break;

            case "repeat":
                read("repeat");
                Statement();
                numChildren = 1;
                while (peekNextToken().tokenType.toString().equals(";")){
                    read(";");
                    Statement();
                    numChildren++;
                }
                read("until");
                Expression();
                numChildren++;
                buildTree("repeat", numChildren);
                break;
            
            case "for":
                read("for");
                read("(");
                ForStat();
                read(";");
                ForExp();
                read(";");
                ForStat();
                read(")");
                Statement();   
                buildTree("for", 4);
                break;

            case "loop":
                read("loop");
                Statement();
                numChildren = 1;
                while (peekNextToken().tokenType.toString().equals(";")){
                    read(";");
                    Statement();
                    numChildren++;
                }
                read("pool");
                buildTree("loop",numChildren);
                break;

            case "case":
                read("case");
                Expression();
                read("of");
                Caseclause();         //since one or more required
                read(";");
                numChildren = 2;
                while (peekNextToken().tokenType.toString().equals("<integer>") || peekNextToken().tokenType.toString().equals("<char>") || peekNextToken().tokenType.toString().equals("<identifier>")){
                    Caseclause();
                    read(";");
                    numChildren++;
                }
                if (peekNextToken().tokenType.toString().equals("otherwise")){
                    OtherwiseClause();
                    numChildren++;
                }
                read("end");
                buildTree("case", numChildren);
                break;

            case "read":
                read("read");
                read("(");
                Name();
                numChildren = 1;
                while (peekNextToken().tokenType.toString().equals(",")){
                    read(",");
                    Name();
                    numChildren++;
                }
                read(")");
                buildTree("read", numChildren);
                break;

            case "exit":
                read("exit");
                buildTree("exit", 0);
                break;

            case "return":
                read("return");
                Expression();
                buildTree("return", 1);
                break;
            
            case "begin":
                Body();
                break;
            
            default:
                buildTree("<null>", 0);
        }
    }

    private void OutExp(){
        switch (peekNextToken().tokenType.toString()){
            case "<string>":
                StringNode();
                buildTree("string", 1);
                break;
            default:
                Expression();
                buildTree("integer", 1);
        }
    }

    private void StringNode(){
        read(TokenType.STRING);
    }
    
    private void Caseclause(){
        CaseExpression();
        int numChildren = 1;
        while (peekNextToken().tokenType.toString().equals(",")){
            read(",");
            CaseExpression();
            numChildren++;
        }
        read(":");
        Statement();
        numChildren++;
        buildTree("case_clause", numChildren);
    }

    private void CaseExpression(){
        ConstValue();
        if (peekNextToken().tokenType.toString().equals("..")){
            read("..");
            ConstValue();
            buildTree("..", 2);
        }
    }

    private void OtherwiseClause(){
        read("otherwise");
        Statement();
        buildTree("otherwise", 1);
    }

    private void Assignment(){
        Name();
        switch (peekNextToken().tokenType.toString()){
            case ":=":
                read(":=");
                Expression();
                buildTree("assign", 2);
                break;

            default:
                read(":=:");
                Name();
                buildTree("swap", 2);
        }
    }

    private void ForStat(){
        switch (peekNextToken().tokenType.toString()){
            case "<identifier>":
                Assignment();
                break;

            default:
                buildTree("<null>",0);
                
        }
    }

    private void ForExp(){
        switch (peekNextToken().tokenType.toString()){
            case ";":
                buildTree("true",0);
                break;

            default:
                Expression();
        }
    }

    private void Expression(){
        Term();
        switch (peekNextToken().tokenType.toString()){
            case "<=":
                read("<=");
                Term();
                buildTree("<=", 2);
                break;

            case "<":
                read("<");
                Term();
                buildTree("<", 2);
                break;

            case ">=":
                read(">=");
                Term();
                buildTree(">=", 2);
                break;

            case ">":
                read(">");
                Term();
                buildTree(">", 2);
                break;
            
            case "=":
                read("=");
                Term();
                buildTree("=", 2);
                break;

            case "<>":
                read("<>");
                Term();
                buildTree("<>", 2);
                break;
        }
    } 

    private void Term() {
        Factor();
        while (peekNextToken().tokenType.toString().equals("+") || peekNextToken().tokenType.toString().equals("-") || peekNextToken().tokenType.toString().equals("or")){
            switch (peekNextToken().tokenType.toString()) {
                case "+":
                    read("+");
                    Factor();
                    buildTree("+", 2);
                    break;

                case "-":
                    read("-");
                    Factor();
                    buildTree("-", 2);
                    break;

                case "or":
                    read("or");
                    Factor();
                    buildTree("or", 2);
                    break;
            }
        }
    }
    
    private void Factor(){
        Primary();
        while (peekNextToken().tokenType.toString().equals("*") || peekNextToken().tokenType.toString().equals("/") || peekNextToken().tokenType.toString().equals("and") || peekNextToken().tokenType.toString().equals("mod")){
            switch (peekNextToken().tokenType.toString()) {
                case "*":
                    read("*");
                    Factor();
                    buildTree("*", 2);
                    break;

                case "/":
                    read("/");
                    Factor();
                    buildTree("/", 2);
                    break;

                case "and":
                    read("and");
                    Factor();
                    buildTree("and", 2);
                    break;

                case "mod":
                    read("mod");
                    Factor();
                    buildTree("mod", 2);
                    break;
            }
        }
    }

    void Primary(){
        switch(peekNextToken().tokenType.toString()){
            case "-":
                read("-");
                Primary();
                buildTree("-",1);
                break;

            case "+":
                read("+");
                Primary();
                break;

            case "not":
                read("not");
                Primary();
                buildTree("not", 1);
                break;

            case "eof":
                read("eof");
                buildTree("eof",0);
                break;

            case "<identifier>":
                Name();
                if(peekNextToken().tokenType.toString().equals("(")){
                    read("(");
                    Expression();
                    int numChildren = 2;
                    while (peekNextToken().tokenType.toString().equals(",")){
                        read(",");
                        Expression();
                        numChildren++;
                    }
                    read(")");
                    buildTree("call", numChildren);
                }
                break;
            
            case "<integer>":
                read(TokenType.INTEGER); 
                break;

            case "<char>":
                read(TokenType.CHAR); 
                break;
            
            case "(":
                read("(");
                Expression();
                read(")");
                break;

            case "succ":
                read("succ");
                read("(");
                Expression();
                read(")");
                buildTree("succ", 1);
                break;

            case "pred":
                read("pred");
                read("(");
                Expression();
                read(")");
                buildTree("pred", 1);
                break;

            case "chr":
                read("chr");
                read("(");
                Expression();
                read(")");
                buildTree("chr", 1);
                break;

            case "ord":
                read("ord");
                read("(");
                Expression();
                read(")");
                buildTree("ord", 1);
                break;
        }
    }
  
    private void Name(){
        read(TokenType.IDENTIFIER);
    }

    // HELPER FUNCTIONS
    private void read(String keyword){
        Token token = getNextToken();
        if (! keyword.equals(token.value.toString())){
            throw new Error("Invalid Keyword: " + token.value + "\n" + keyword + " expected."); 
        }
    }

    private void read(TokenType type){
        Token token = getNextToken();
        if (token.tokenType != type){
            throw new Error("Invalid Type: " + token.tokenType.toString() + "\n" + type.toString() + " expected."); 
        }
        Node valueNode = new Node(token.value.toString(), 0);
        Node typeNode = new Node(token.tokenType.toString(), 1, valueNode, null);
        stack.push(typeNode);
    }

    private Token getNextToken(){
        Token token = screenedTokens.get(nextTokenIndex);
        nextTokenIndex++;
        return token;
    }

    private void buildTree(String key, int n){
        Node prevNode = null;

        for(int i = 0; i < n; i++){
            Node currNode = stack.pop();
            currNode.rightSibling = prevNode;
            prevNode = currNode;
        }

        stack.push(new Node(key, n, prevNode, null));
    }

    private Token peekNextToken(){
        return screenedTokens.get(nextTokenIndex);
    }
}

class Node {
    String value;
    Node lefChild, rightSibling;
    int numChildren;
  
    public Node(String value, int numChildren) {
        this.value = value;
        this.numChildren = numChildren;
        this.lefChild = null;
        this.rightSibling = null;
    }

    public Node(String value, int numChildren, Node lefChild, Node rightSibling) {
        this.value = value;
        this.numChildren = numChildren;
        this.lefChild = lefChild;
        this.rightSibling = rightSibling;
    }
}
  
class AST {
    Node root;
  
    public AST(Node root){
        this.root = root;
    }

    // Traverse AST
    public void traverseTree(Node node, int traverseDepth) {
        if (node != null) {
            System.out.println(". ".repeat(traverseDepth) + node.value + "(" + node.numChildren + ")");
            traverseTree(node.lefChild, traverseDepth+1);
            traverseTree(node.rightSibling, traverseDepth);
        }
    }
}