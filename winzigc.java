import java.io.IOException;
import java.util.ArrayList;

public class winzigc{
    public static void main(String[] args){
        
        Lexer lexer = new Lexer(); //tokenizer
        Parser parser  = new Parser(); //syntax analysis
        CodeGenerator codegen = new CodeGenerator(); //semantic analysis and code geneneration

        try{
            String flag = args[0];
            String inputPath = args[1];
        
            try {
                ArrayList<Token> scannedTokens = lexer.scan(inputPath); 
                ArrayList<Token> screenedTokens = lexer.screen(scannedTokens);

                // to get the tokens
                if (flag.equals("-tokens")){
                    for (Token token : screenedTokens) {
                        System.out.println(token.tokenType + " -> " + token.value);
                    } 
                // to get the AST    
                } else if (flag.equals("-ast")){
                    AST ast = parser.getAST(screenedTokens);
                    ast.traverseTree(ast.root, 0);
                // to get the machine code. assembly code is dumped to the current directory as "asmfile"
                } else if (flag.equals("-code")){
                    AST ast = parser.getAST(screenedTokens);
                    codegen.generateCode(ast);
                }
            }
            catch(IOException e) {
                System.out.println("Invalid File!");
            }
        }
        catch(ArrayIndexOutOfBoundsException e){
            System.out.println("Incompatible Args!");
        }
    }
}