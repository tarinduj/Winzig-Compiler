import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;

public class Lexer{
    public File file;
    public Scanner scanner;
    
    public static String identifierStartAlphabet = "_abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static String identifierAlphabet = "_abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static String integerAlphabet = "0123456789";
    public static String whiteSpaceAlphabet = " \t\r";

    //SCANNER
    public ArrayList<Token> scan(String filePath) throws IOException {
        file = new File(filePath);
        scanner = new Scanner(file);
        ArrayList<Token> scannedTokens = new ArrayList<Token>();
        
        scanner.useDelimiter(""); // allows to scan symbol by symbol

        String symbol = getNextSymbol();

        while(!symbol.equals("END")){  
           
            //IDENTIFIERS
            if (identifierStartAlphabet.contains(symbol)){
                StringBuilder identifier = new StringBuilder(symbol);
                IntermediateToken intermediateToken = getIdentifier(identifier);
                symbol = intermediateToken.symbol;
                scannedTokens.add(intermediateToken.token);
                continue;
            }

            //INTEGERS
            if (integerAlphabet.contains(symbol)){
                StringBuilder integer = new StringBuilder(symbol);
                IntermediateToken intermediateToken = getInteger(integer);
                symbol = intermediateToken.symbol;
                scannedTokens.add(intermediateToken.token);
                continue;
            }

            //WHITE SPACE
            if (whiteSpaceAlphabet.contains(symbol)){
                StringBuilder whiteSpace = new StringBuilder(symbol);
                IntermediateToken intermediateToken = getWhiteSpace(whiteSpace);
                symbol = intermediateToken.symbol;
                scannedTokens.add(intermediateToken.token);
                continue;
            }

            //CHAR
            if (symbol.equals("'")){
                StringBuilder cha = new StringBuilder(symbol);
                IntermediateToken intermediateToken = getChar(cha);
                symbol = intermediateToken.symbol;
                scannedTokens.add(intermediateToken.token);
                continue;
            }

            //STRING
            if (symbol.equals("\"")){
                StringBuilder str = new StringBuilder(symbol);
                IntermediateToken intermediateToken = getString(str);
                symbol = intermediateToken.symbol;
                scannedTokens.add(intermediateToken.token);
                continue;
            }
            
            //COMMENT
            if ("#{".contains(symbol)){
                StringBuilder comment = new StringBuilder(symbol);
                IntermediateToken intermediateToken = getComment(comment, symbol);
                symbol = intermediateToken.symbol;
                scannedTokens.add(intermediateToken.token);
                continue;
            }
            
            //COLON
            if (symbol.equals(":")){
                StringBuilder colon = new StringBuilder(symbol);
                IntermediateToken intermediateToken = getColon(colon);
                symbol = intermediateToken.symbol;
                scannedTokens.add(intermediateToken.token);
                continue;
            }

            //DOT
            if (symbol.equals(".")){
                StringBuilder dot = new StringBuilder(symbol);
                IntermediateToken intermediateToken = getDot(dot);
                symbol = intermediateToken.symbol;
                scannedTokens.add(intermediateToken.token);
                continue;
            }

            //LESS THAN
            if (symbol.equals("<")){
                StringBuilder lessThan = new StringBuilder(symbol);
                IntermediateToken intermediateToken = getLessThan(lessThan);
                symbol = intermediateToken.symbol;
                scannedTokens.add(intermediateToken.token);
                continue;
            }

            //GREATER THAN
            if (symbol.equals(">")){
                StringBuilder greaterThan = new StringBuilder(symbol);
                IntermediateToken intermediateToken = getGreaterThan(greaterThan);
                symbol = intermediateToken.symbol;
                scannedTokens.add(intermediateToken.token);
                continue;
            }

            //ONE SYMBOL TOKEN
            if (symbol.equals("\n")){ 
                Token token = new Token(TokenType.NEWLINE, new StringBuilder(symbol));
                scannedTokens.add(token);
            } else if ("=+-*/".contains(symbol)){
                Token token = getOperator(symbol);
                scannedTokens.add(token);
            } else if (";,()".contains(symbol)){
                Token token = getPunctuation(symbol);
                scannedTokens.add(token);
            } else {
                throw new Error("Invalid Symbol: " + symbol);
            }

            symbol = getNextSymbol();   
        } 

        return scannedTokens;
    }

    //SCREENER
    public ArrayList<Token> screen(ArrayList<Token> scannerTokens) {
        ArrayList<Token> screenedTokens = new ArrayList<Token>();

        for (Token token : scannerTokens) {
            if (token.tokenType == TokenType.WHITESPACE || token.tokenType == TokenType.NEWLINE || token.tokenType == TokenType.COMMENT){
                continue;
            } else if (token.tokenType == TokenType.IDENTIFIER){
                screenedTokens.add(getKeyword(token));
            } else {
                screenedTokens.add(token);
            }
        } 
        return screenedTokens;
    }

    private String getNextSymbol(){
        if (scanner.hasNext()) {
            return scanner.next();
        } else {
            return "END";
        }
    }

    // scanner helper functions
    private IntermediateToken getIdentifier(StringBuilder identifier){

        String symbol = getNextSymbol();

        while (identifierAlphabet.contains(symbol)){
            identifier.append(symbol);
            symbol = getNextSymbol();
        }  
        Token token = new Token(TokenType.IDENTIFIER, identifier);
        return new IntermediateToken(token, symbol);
    }

    private IntermediateToken getInteger(StringBuilder integer){

        String symbol = getNextSymbol();

        while (integerAlphabet.contains(symbol)){
            integer.append(symbol);
            symbol = getNextSymbol();
        }
        Token token = new Token(TokenType.INTEGER, integer);
        return new IntermediateToken(token, symbol);
    }

    private IntermediateToken getWhiteSpace(StringBuilder whiteSpace){

        String symbol = getNextSymbol();

        while (whiteSpaceAlphabet.contains(symbol)){
            whiteSpace.append(symbol);
            symbol = getNextSymbol();
        }
        Token token = new Token(TokenType.WHITESPACE, whiteSpace);
        return new IntermediateToken(token, symbol);
    }

    private IntermediateToken getChar(StringBuilder cha){

        String symbol = getNextSymbol();
        if (! symbol.equals("'")){
            cha.append(symbol);
            symbol = getNextSymbol();
            if (symbol.equals("'")){
                cha.append(symbol);
                symbol = getNextSymbol();

                Token token = new Token(TokenType.CHAR, cha);
                return new IntermediateToken(token, symbol);
            }
        }
        throw new Error("Invalid Syntax: ' should always enclose a single symbol");
    }

    private IntermediateToken getString(StringBuilder str){

        String symbol = getNextSymbol();

        while (! (symbol.equals("\"") || symbol.equals("END"))) {
            str.append(symbol);
            symbol = getNextSymbol();
        } 

        if (symbol.equals("\"")){
            str.append(symbol);
            symbol = getNextSymbol();
            
            Token token = new Token(TokenType.STRING, str);
            return new IntermediateToken(token, symbol);
        }else{
            throw new Error("Invalid Syntax: \" should always enclose a string followed by another \"");
        }
    }

    private IntermediateToken getComment(StringBuilder comment, String symbol){
        // # comment 
        if (symbol.equals("#")){
            symbol = getNextSymbol();
            while (! (symbol.equals("\n") || symbol.equals("END"))) {
                comment.append(symbol);
                symbol = getNextSymbol();
            } 

            if (symbol.equals("\n")){
                comment.append(symbol);
                symbol = getNextSymbol();

                Token token = new Token(TokenType.COMMENT, comment);
                return new IntermediateToken(token, symbol);
            }else{
                Token token = new Token(TokenType.COMMENT, comment);
                return new IntermediateToken(token, symbol);
            }

        // { } comment
        }else{
            symbol = getNextSymbol();
            while (! (symbol.equals("}") || symbol.equals("END"))) {
                comment.append(symbol);
                symbol = getNextSymbol();
            } 

            if (symbol.equals("}")){
                comment.append(symbol);
                symbol = getNextSymbol();
                
                Token token = new Token(TokenType.COMMENT, comment);
                return new IntermediateToken(token, symbol);
            }else{
                throw new Error("Invalid Syntax: { should always be followed with a }");
            }
        }
    }

    private IntermediateToken getColon(StringBuilder colon){

        String symbol = getNextSymbol();
        if (symbol.equals("=")){
            colon.append(symbol);
            symbol = getNextSymbol();
            if (symbol.equals(":")){
                colon.append(symbol);
                symbol = getNextSymbol();
                
                Token token = new Token(TokenType.SWAP, colon);
                return new IntermediateToken(token, symbol);
            }
            Token token = new Token(TokenType.ASSIGN, colon);
            return new IntermediateToken(token, symbol);
        }
        Token token = new Token(TokenType.COLON, colon);
        return new IntermediateToken(token, symbol);
    }

    private IntermediateToken getDot(StringBuilder dot){

        String symbol = getNextSymbol();
        if (symbol.equals(".")){
            dot.append(symbol);
            symbol = getNextSymbol();
            
            Token token = new Token(TokenType.CASEEXP, dot);
            return new IntermediateToken(token, symbol);
        }
        Token token = new Token(TokenType.SINGLEDOT, dot);
        return new IntermediateToken(token, symbol);
    }

    private IntermediateToken getLessThan(StringBuilder lessThan){

        String symbol = getNextSymbol();
        if (symbol.equals("=")){
            lessThan.append(symbol);
            symbol = getNextSymbol();
            
            Token token = new Token(TokenType.LESSEQUAL, lessThan);
            return new IntermediateToken(token, symbol);

        } else if (symbol.equals(">")){
            lessThan.append(symbol);
            symbol = getNextSymbol();
            
            Token token = new Token(TokenType.NOTEQUAL, lessThan);
            return new IntermediateToken(token, symbol);
        }

        Token token = new Token(TokenType.LESSTHAN, lessThan);
        return new IntermediateToken(token, symbol);
    }

    private IntermediateToken getGreaterThan(StringBuilder greaterThan){

        String symbol = getNextSymbol();
        if (symbol.equals("=")){
            greaterThan.append(symbol);
            symbol = getNextSymbol();
            
            Token token = new Token(TokenType.GREATEREQUAL, greaterThan);
            return new IntermediateToken(token, symbol);
        } 

        Token token = new Token(TokenType.GREATERTHAN, greaterThan);
        return new IntermediateToken(token, symbol);
    }

    private Token getOperator(String symbol){
        if (symbol.equals("=")){ 
            return new Token(TokenType.EQUAL, new StringBuilder(symbol));
        } else if (symbol.equals("+")){ 
            return new Token(TokenType.PLUS, new StringBuilder(symbol));
        } else if (symbol.equals("-")){ 
            return new Token(TokenType.MINUS, new StringBuilder(symbol));
        } else if (symbol.equals("*")){ 
            return new Token(TokenType.MULTIPLY, new StringBuilder(symbol));
        } else { 
            return new Token(TokenType.DIVIDE, new StringBuilder(symbol));
        }
    }

    private Token getPunctuation(String symbol){
        if (symbol.equals(";")){ 
            return new Token(TokenType.SEMICOLON, new StringBuilder(symbol));
        } else if (symbol.equals(",")){ 
            return new Token(TokenType.COMMA, new StringBuilder(symbol));
        } else if (symbol.equals("(")){ 
            return new Token(TokenType.OPENBRACKET, new StringBuilder(symbol));
        } else { 
            return new Token(TokenType.CLOSEBRACKET, new StringBuilder(symbol));
        }
    }

    private Token getKeyword(Token token){
        StringBuilder tokenValue = token.value;

        switch(tokenValue.toString()) {
            case "program":
                return new Token(TokenType.PROGRAM, tokenValue);
            case "var": 
                return new Token(TokenType.VAR, tokenValue);
            case "const": 
                return new Token(TokenType.CONST, tokenValue);
            case "type": 
                return new Token(TokenType.TYPE, tokenValue);
            case "function": 
                return new Token(TokenType.FUNCTION, tokenValue);
            case "return": 
                return new Token(TokenType.RETURN, tokenValue);
            case "begin": 
                return new Token(TokenType.BEGIN, tokenValue);
            case "end": 
                return new Token(TokenType.END, tokenValue);
            case "output": 
                return new Token(TokenType.OUTPUT, tokenValue);
            case "if": 
                return new Token(TokenType.IF, tokenValue);
            case "then": 
                return new Token(TokenType.THEN, tokenValue);
            case "else": 
                return new Token(TokenType.ELSE, tokenValue);
            case "while": 
                return new Token(TokenType.WHILE, tokenValue);
            case "do": 
                return new Token(TokenType.DO, tokenValue);
            case "case": 
                return new Token(TokenType.CASE, tokenValue);
            case "of": 
                return new Token(TokenType.OF, tokenValue);
            case "otherwise": 
                return new Token(TokenType.OTHERWISE, tokenValue);
            case "repeat": 
                return new Token(TokenType.REPEAT, tokenValue);
            case "for": 
                return new Token(TokenType.FOR, tokenValue);
            case "until": 
                return new Token(TokenType.UNTIL, tokenValue);
            case "loop": 
                return new Token(TokenType.LOOP, tokenValue);
            case "pool": 
                return new Token(TokenType.POOL, tokenValue);
            case "exit": 
                return new Token(TokenType.EXIT, tokenValue);
            case "mod": 
                return new Token(TokenType.MOD, tokenValue);
            case "and": 
                return new Token(TokenType.AND, tokenValue);
            case "or": 
                return new Token(TokenType.OR, tokenValue);
            case "not": 
                return new Token(TokenType.NOT, tokenValue);
            case "read": 
                return new Token(TokenType.READ, tokenValue);
            case "succ": 
                return new Token(TokenType.SUCC, tokenValue);
            case "pred": 
                return new Token(TokenType.PRED, tokenValue);
            case "chr": 
                return new Token(TokenType.CHR, tokenValue);
            case "ord": 
                return new Token(TokenType.ORD, tokenValue);
            case "eof": 
                return new Token(TokenType.EOF, tokenValue);
            default:
                return token;
          }
    }

}

class IntermediateToken {
    public final Token token;
    public final String symbol;

    public IntermediateToken(Token token, String symbol) {
        this.token = token;
        this.symbol = symbol;
    }
}

enum TokenType{
    IDENTIFIER  ("<identifier>"),           
    INTEGER     ("<integer>"),               
    WHITESPACE  ("whitespace"),
    CHAR        ("<char>"),                  
    STRING      ("<string>"),                
    COMMENT     ("comment"),               
    NEWLINE     ("\\n"),
    PROGRAM     ("program"),
    VAR         ("var"),
    CONST       ("const"),
    TYPE        ("type"),
    FUNCTION    ("function"),
    RETURN      ("return"),
    BEGIN       ("begin"),
    END         ("end"),
    SWAP        (":=:"),
    ASSIGN      (":="),
    OUTPUT      ("output"),
    IF          ("if"),
    THEN        ("then"),
    ELSE        ("else"),
    WHILE       ("while"),
    DO          ("do"),
    CASE        ("case"),
    OF          ("of"),
    CASEEXP     (".."),
    OTHERWISE   ("otherwise"),
    REPEAT      ("repeat"),
    FOR         ("for"),
    UNTIL       ("until"),
    LOOP        ("loop"),
    POOL        ("pool"),
    EXIT        ("exit"),
    LESSEQUAL   ("<="),
    NOTEQUAL    ("<>"),
    LESSTHAN    ("<"),
    GREATEREQUAL(">="),
    GREATERTHAN (">"),
    EQUAL       ("="),
    MOD         ("mod"),
    AND         ("and"),
    OR          ("or"),
    NOT         ("not"),
    READ        ("read"),
    SUCC        ("succ"),
    PRED        ("pred"),
    CHR         ("chr"),
    ORD         ("ord"),
    EOF         ("eof"),
    COLON       (":"),
    SEMICOLON   (";"),
    SINGLEDOT   ("."),
    COMMA       (","),
    OPENBRACKET ("("),
    CLOSEBRACKET(")"),
    PLUS        ("+"),
    MINUS       ("-"),
    MULTIPLY    ("*"),
    DIVIDE      ("/");
    
    private String value;
    private TokenType(String value){
        this.value = value;
    }

    public String toString()
    {
        return this.value; //will return , or ' instead of COMMA or APOSTROPHE
    }
}

class Token{
    TokenType tokenType;
    StringBuilder value;

    public Token(TokenType tokenType, StringBuilder value){
        this.tokenType = tokenType;
        this.value = value;
    }
}
