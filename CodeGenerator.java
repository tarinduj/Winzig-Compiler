import java.io.*;
import java.util.*;

import java.io.IOException;

public class CodeGenerator{
    public DeclarationTable decTable = new DeclarationTable();   
    public ArrayList<Object> stack;
    public AttributeTree attrTree;

    public void generateCode(AST ast) throws IOException{
        AttributeNode attrRoot = getAttributeNode(ast.root); 
        attrTree = new AttributeTree(attrRoot);
        synthesizeAttributes(attrRoot);

        File errorFile = new File("errorfile");
        if (errorFile.length() == 0){
            System.out.println("Assembly files generated successfully.");
        } else {
            System.out.println("The following errors occurred when compiling");

            BufferedReader br = new BufferedReader(new FileReader(errorFile)); 
            String st; 
            while ((st = br.readLine()) != null) 
                System.out.println(st); 
            br.close();
        }

        File tmpfile = new File("asm.temp"); 
        if(tmpfile.delete()){ System.out.print("");} 
        File tmperfile = new File("error.temp"); 
        if(tmperfile.delete()){ System.out.print("");} 
    }

    public AttributeNode getAttributeNode(Node ASTNode) {
        try {
            if (ASTNode != null) {
                AttributeNode attrNode = new AttributeNode(); 
                attrNode.value = ASTNode.value;
                attrNode.numChildren = ASTNode.numChildren;
                attrNode.lefChild = getAttributeNode(ASTNode.lefChild);
                attrNode.rightSibling = getAttributeNode(ASTNode.rightSibling);
                return attrNode;
            } else {
                return null;
            }
        } catch (NullPointerException e) {
            return null;
        }
    }

    public BufferedWriter openFile(String filename) throws IOException{
        BufferedWriter file = new BufferedWriter(new FileWriter(filename));
        return file;
    }

    public BufferedWriter closeFile(BufferedWriter file) throws IOException{
        file.close();
        return file;
    }

    public BufferedWriter gen(BufferedWriter file, String ...args) throws IOException{
        for (String arg: args){
            file.append(arg); file.append(" ");
            //System.out.print(arg + " ");
        }
        //System.out.println("");
        file.newLine(); 
        return file;
    }

    public void synthesizeAttributes(AttributeNode node) throws IOException{
        //System.out.println("** " + node.value + " :" + node.inhertitedAttr.next);
        AttributeNode currChild, prevChild;
        AttributeNode child1, child2, child3, child4, child5, child6, child7;
        String x, y;
        BufferedWriter tempasm = new BufferedWriter(new FileWriter("asm.temp"));
        BufferedWriter temperror = new BufferedWriter(new FileWriter("error.temp"));
        if (node != null) {
            switch (node.value) {
                case "program":
                    child1 = node.lefChild;
                    child2 = child1.rightSibling;
                    child3 = child2.rightSibling;
                    child4 = child3.rightSibling;
                    child5 = child4.rightSibling;
                    child6 = child5.rightSibling;
                    child7 = child6.rightSibling;

                    x = child1.lefChild.value;
                    y = child7.lefChild.value;
                    
                    child2.inhertitedAttr.code = openFile("asmfile");
                    child2.inhertitedAttr.next = 1;
                    child2.inhertitedAttr.top = 0;
                    child2.inhertitedAttr.error = openFile("errorfile");
                    synthesizeAttributes(child2);

                    child3.inhertitedAttr.code = child2.synthesizedAttr.code;
                    child3.inhertitedAttr.next = child2.synthesizedAttr.next;
                    child3.inhertitedAttr.top = child2.synthesizedAttr.top;
                    child3.inhertitedAttr.error = child2.synthesizedAttr.error;
                    synthesizeAttributes(child3);

                    child4.inhertitedAttr.code = child3.synthesizedAttr.code;
                    child4.inhertitedAttr.next = child3.synthesizedAttr.next;
                    child4.inhertitedAttr.top = child3.synthesizedAttr.top;
                    child4.inhertitedAttr.error = child3.synthesizedAttr.error;
                    synthesizeAttributes(child4);

                    child5.inhertitedAttr.code = child4.synthesizedAttr.code;
                    child5.inhertitedAttr.next = child4.synthesizedAttr.next;
                    child5.inhertitedAttr.top = child4.synthesizedAttr.top;
                    child5.inhertitedAttr.error = child4.synthesizedAttr.error;
                    synthesizeAttributes(child5);

                    child6.inhertitedAttr.code = child5.synthesizedAttr.code;
                    child6.inhertitedAttr.next = child5.synthesizedAttr.next;
                    child6.inhertitedAttr.top = child5.synthesizedAttr.top;
                    child6.inhertitedAttr.error = child5.synthesizedAttr.error;
                    synthesizeAttributes(child6);

                    node.synthesizedAttr.code = closeFile(gen(child6.synthesizedAttr.code, "stop"));  
                    if (x.equals(y)){
                        node.synthesizedAttr.error = closeFile(child6.synthesizedAttr.error);
                    }else{
                        node.synthesizedAttr.error = closeFile(gen(child6.synthesizedAttr.error, "program names don't match"));
                    }
                    
                    break;
                                   
                case "var":
                    node.synthesizedAttr.code = node.inhertitedAttr.code;  
                    node.synthesizedAttr.next = node.inhertitedAttr.next;   
                    node.synthesizedAttr.top = node.inhertitedAttr.top; 
                    node.synthesizedAttr.error = node.inhertitedAttr.error; 
                    break;
                
                case "assign":
                    child1 = node.lefChild; //<identifier>
                    child2 = child1.rightSibling; //Expression
                    x = child1.lefChild.value;
                    
                    child2.inhertitedAttr.code = node.inhertitedAttr.code;  
                    child2.inhertitedAttr.next = node.inhertitedAttr.next; 
                    child2.inhertitedAttr.top = node.inhertitedAttr.top; 
                    child2.inhertitedAttr.error = node.inhertitedAttr.error; 
                    synthesizeAttributes(child2); 

                    if (decTable.lookup(x) == 0){
                        decTable.enter(x, child2.synthesizedAttr.top);
                        node.synthesizedAttr.code = child2.synthesizedAttr.code;
                        node.synthesizedAttr.next = child2.synthesizedAttr.next;
                        node.synthesizedAttr.top = child2.synthesizedAttr.top;
                    } else {
                        node.synthesizedAttr.code = gen(child2.synthesizedAttr.code, "save", Integer.toString(decTable.lookup(x)));
                        node.synthesizedAttr.next = child2.synthesizedAttr.next + 1;
                        node.synthesizedAttr.top = child2.synthesizedAttr.top - 1;
                    }

                    if (child2.synthesizedAttr.type.equals("integer")) {
                        node.synthesizedAttr.error = child2.synthesizedAttr.error;
                    } else {
                        node.synthesizedAttr.error = gen(child2.synthesizedAttr.error, "Assignment type clash");
                    }

                    node.synthesizedAttr.type = "statement";

                    break; 

                case "consts": //deafaults
                    if (node.numChildren == 0){
                        node.synthesizedAttr.code = node.inhertitedAttr.code;  
                        node.synthesizedAttr.next = node.inhertitedAttr.next;   
                        node.synthesizedAttr.top = node.inhertitedAttr.top; 
                        node.synthesizedAttr.error = node.inhertitedAttr.error; 
                    } else {
                        currChild = node.lefChild;
                        currChild.inhertitedAttr.code = node.inhertitedAttr.code;  
                        currChild.inhertitedAttr.next = node.inhertitedAttr.next;   
                        currChild.inhertitedAttr.top = node.inhertitedAttr.top;  
                        currChild.inhertitedAttr.error = node.inhertitedAttr.error;    
                        synthesizeAttributes(currChild); 
                        prevChild = currChild;

                        for (int i = 1; i < node.numChildren; i++) {
                            currChild = currChild.rightSibling;
                            currChild.inhertitedAttr.code = prevChild.synthesizedAttr.code;  
                            currChild.inhertitedAttr.next = prevChild.synthesizedAttr.next;   
                            currChild.inhertitedAttr.top = prevChild.synthesizedAttr.top; 
                            currChild.inhertitedAttr.error = prevChild.synthesizedAttr.error;   
                            synthesizeAttributes(currChild);    
                            prevChild = currChild;
                        } 

                        node.synthesizedAttr.code = prevChild.synthesizedAttr.code;  
                        node.synthesizedAttr.next = prevChild.synthesizedAttr.next;   
                        node.synthesizedAttr.top = prevChild.synthesizedAttr.top;  
                        node.synthesizedAttr.error = prevChild.synthesizedAttr.error;  
                    }  
                    node.synthesizedAttr.type = "constants";
                    break;  

                case "types": //defaults
                    if (node.numChildren == 0){
                        node.synthesizedAttr.code = node.inhertitedAttr.code;  
                        node.synthesizedAttr.next = node.inhertitedAttr.next;   
                        node.synthesizedAttr.top = node.inhertitedAttr.top; 
                        node.synthesizedAttr.error = node.inhertitedAttr.error; 
                    } else {
                        currChild = node.lefChild;
                        currChild.inhertitedAttr.code = node.inhertitedAttr.code;  
                        currChild.inhertitedAttr.next = node.inhertitedAttr.next;   
                        currChild.inhertitedAttr.top = node.inhertitedAttr.top;  
                        currChild.inhertitedAttr.error = node.inhertitedAttr.error;    
                        synthesizeAttributes(currChild); 
                        prevChild = currChild;

                        for (int i = 1; i < node.numChildren; i++) {
                            currChild = currChild.rightSibling;
                            currChild.inhertitedAttr.code = prevChild.synthesizedAttr.code;  
                            currChild.inhertitedAttr.next = prevChild.synthesizedAttr.next;   
                            currChild.inhertitedAttr.top = prevChild.synthesizedAttr.top; 
                            currChild.inhertitedAttr.error = prevChild.synthesizedAttr.error;   
                            synthesizeAttributes(currChild);    
                            prevChild = currChild;
                        } 

                        node.synthesizedAttr.code = prevChild.synthesizedAttr.code;  
                        node.synthesizedAttr.next = prevChild.synthesizedAttr.next;   
                        node.synthesizedAttr.top = prevChild.synthesizedAttr.top;  
                        node.synthesizedAttr.error = prevChild.synthesizedAttr.error;  
                    }  
                    node.synthesizedAttr.type = "types";
                    break;   

                case "dclns": //defaults
                    if (node.numChildren == 0){
                        node.synthesizedAttr.code = node.inhertitedAttr.code;  
                        node.synthesizedAttr.next = node.inhertitedAttr.next;   
                        node.synthesizedAttr.top = node.inhertitedAttr.top; 
                        node.synthesizedAttr.error = node.inhertitedAttr.error; 
                    } else {
                        currChild = node.lefChild;
                        currChild.inhertitedAttr.code = node.inhertitedAttr.code;  
                        currChild.inhertitedAttr.next = node.inhertitedAttr.next;   
                        currChild.inhertitedAttr.top = node.inhertitedAttr.top;  
                        currChild.inhertitedAttr.error = node.inhertitedAttr.error;    
                        synthesizeAttributes(currChild); 
                        prevChild = currChild;

                        for (int i = 1; i < node.numChildren; i++) {
                            currChild = currChild.rightSibling;
                            currChild.inhertitedAttr.code = prevChild.synthesizedAttr.code;  
                            currChild.inhertitedAttr.next = prevChild.synthesizedAttr.next;   
                            currChild.inhertitedAttr.top = prevChild.synthesizedAttr.top; 
                            currChild.inhertitedAttr.error = prevChild.synthesizedAttr.error;   
                            synthesizeAttributes(currChild);    
                            prevChild = currChild;
                        } 

                        node.synthesizedAttr.code = prevChild.synthesizedAttr.code;  
                        node.synthesizedAttr.next = prevChild.synthesizedAttr.next;   
                        node.synthesizedAttr.top = prevChild.synthesizedAttr.top;  
                        node.synthesizedAttr.error = prevChild.synthesizedAttr.error;  
                    }  
                    break;      

                case "subprogs": //defaults
                    if (node.numChildren == 0){
                        node.synthesizedAttr.code = node.inhertitedAttr.code;  
                        node.synthesizedAttr.next = node.inhertitedAttr.next;   
                        node.synthesizedAttr.top = node.inhertitedAttr.top; 
                        node.synthesizedAttr.error = node.inhertitedAttr.error; 
                    } else {
                        currChild = node.lefChild;
                        currChild.inhertitedAttr.code = node.inhertitedAttr.code;  
                        currChild.inhertitedAttr.next = node.inhertitedAttr.next;   
                        currChild.inhertitedAttr.top = node.inhertitedAttr.top;  
                        currChild.inhertitedAttr.error = node.inhertitedAttr.error;    
                        synthesizeAttributes(currChild); 
                        prevChild = currChild;

                        for (int i = 1; i < node.numChildren; i++) {
                            currChild = currChild.rightSibling;
                            currChild.inhertitedAttr.code = prevChild.synthesizedAttr.code;  
                            currChild.inhertitedAttr.next = prevChild.synthesizedAttr.next;   
                            currChild.inhertitedAttr.top = prevChild.synthesizedAttr.top; 
                            currChild.inhertitedAttr.error = prevChild.synthesizedAttr.error;   
                            synthesizeAttributes(currChild);    
                            prevChild = currChild;
                        } 

                        node.synthesizedAttr.code = prevChild.synthesizedAttr.code;  
                        node.synthesizedAttr.next = prevChild.synthesizedAttr.next;   
                        node.synthesizedAttr.top = prevChild.synthesizedAttr.top;  
                        node.synthesizedAttr.error = prevChild.synthesizedAttr.error;  
                    }  
                    node.synthesizedAttr.type = "subprogs";
                    break;  

                case "block": //defaults
                    currChild = node.lefChild;
                    currChild.inhertitedAttr.code = node.inhertitedAttr.code;  
                    currChild.inhertitedAttr.next = node.inhertitedAttr.next;   
                    currChild.inhertitedAttr.top = node.inhertitedAttr.top;  
                    currChild.inhertitedAttr.error = node.inhertitedAttr.error;    
                    synthesizeAttributes(currChild); 
                    prevChild = currChild;

                    for (int i = 1; i < node.numChildren; i++) {
                        currChild = currChild.rightSibling;
                        currChild.inhertitedAttr.code = prevChild.synthesizedAttr.code;  
                        currChild.inhertitedAttr.next = prevChild.synthesizedAttr.next;   
                        currChild.inhertitedAttr.top = prevChild.synthesizedAttr.top; 
                        currChild.inhertitedAttr.error = prevChild.synthesizedAttr.error;   
                        synthesizeAttributes(currChild);    
                        prevChild = currChild;
                    } 

                    node.synthesizedAttr.code = prevChild.synthesizedAttr.code;  
                    node.synthesizedAttr.next = prevChild.synthesizedAttr.next;   
                    node.synthesizedAttr.top = prevChild.synthesizedAttr.top;  
                    node.synthesizedAttr.error = prevChild.synthesizedAttr.error; 
                    node.synthesizedAttr.type = "statement";   
                    break; 

                case "output":
                    currChild = node.lefChild;
                    currChild.inhertitedAttr.code = node.inhertitedAttr.code;  
                    currChild.inhertitedAttr.next = node.inhertitedAttr.next;   
                    currChild.inhertitedAttr.top = node.inhertitedAttr.top;   
                    currChild.inhertitedAttr.error = node.inhertitedAttr.error;   
                    synthesizeAttributes(currChild); 
                    prevChild = currChild;

                    for (int i = 1; i < node.numChildren; i++) {
                        System.out.println(i);
                        currChild = currChild.rightSibling;
                        currChild.inhertitedAttr.code = gen(prevChild.synthesizedAttr.code, "print");  
                        currChild.inhertitedAttr.next = prevChild.synthesizedAttr.next + 1;   
                        currChild.inhertitedAttr.top = prevChild.synthesizedAttr.top - 1; 

                        if (prevChild.synthesizedAttr.type.equals("integer") || prevChild.synthesizedAttr.type.equals("string")){
                            currChild.inhertitedAttr.error = prevChild.synthesizedAttr.error; 
                        } else {
                            currChild.inhertitedAttr.error = gen(prevChild.synthesizedAttr.error, "Illegal type for output");
                        }

                        synthesizeAttributes(currChild);    
                        prevChild = currChild;
                    } 
                
                    node.synthesizedAttr.code = gen(prevChild.synthesizedAttr.code, "print"); 
                    node.synthesizedAttr.next = prevChild.synthesizedAttr.next + 1;   
                    node.synthesizedAttr.top = prevChild.synthesizedAttr.top - 1;  
                    
                    if (prevChild.synthesizedAttr.type.equals("integer") || prevChild.synthesizedAttr.type.equals("string")){
                        node.synthesizedAttr.error = prevChild.synthesizedAttr.error; 
                    } else {
                        node.synthesizedAttr.error = gen(prevChild.synthesizedAttr.error, "Illegal type for output");
                    }

                    break;  
                
                case "if":
                    if (node.numChildren == 3){
                        child1 = node.lefChild; 
                        child2 = child1.rightSibling; 
                        child3 = child2.rightSibling;

                        child1.inhertitedAttr.code = node.inhertitedAttr.code;  
                        child1.inhertitedAttr.next = node.inhertitedAttr.next;   
                        child1.inhertitedAttr.top = node.inhertitedAttr.top;  
                        synthesizeAttributes(child1);  

                        child2.inhertitedAttr.code = gen(tempasm, "iffalse", Integer.toString(child2.synthesizedAttr.next + 1));
                        child2.inhertitedAttr.next = child1.synthesizedAttr.next + 1;   
                        child2.inhertitedAttr.top = child1.synthesizedAttr.top - 1; 
                        synthesizeAttributes(child2);  
                        child2.inhertitedAttr.code = gen(child1.synthesizedAttr.code, "iffalse", Integer.toString(child2.synthesizedAttr.next + 1));
                        synthesizeAttributes(child2);  

                        child3.inhertitedAttr.code = gen(tempasm, "goto", Integer.toString(child3.synthesizedAttr.next));
                        child3.inhertitedAttr.next = child2.synthesizedAttr.next + 1;   
                        child3.inhertitedAttr.top = child2.synthesizedAttr.top; 
                        synthesizeAttributes(child3);  
                        child3.inhertitedAttr.code = gen(child2.synthesizedAttr.code, "goto", Integer.toString(child3.synthesizedAttr.next));
                        synthesizeAttributes(child3);  
                        
                        node.synthesizedAttr.code = child3.synthesizedAttr.code;
                        node.synthesizedAttr.next = child3.synthesizedAttr.next;
                        node.synthesizedAttr.top = child3.synthesizedAttr.top;
                    } else {
                        child1 = node.lefChild; 
                        child2 = child1.rightSibling; 

                        child1.inhertitedAttr.code = node.inhertitedAttr.code;  
                        child1.inhertitedAttr.next = node.inhertitedAttr.next;   
                        child1.inhertitedAttr.top = node.inhertitedAttr.top;  
                        synthesizeAttributes(child1);  

                        child2.inhertitedAttr.code = gen(tempasm, "iffalse", Integer.toString(child2.synthesizedAttr.next));
                        child2.inhertitedAttr.next = child1.synthesizedAttr.next + 1;   
                        child2.inhertitedAttr.top = child1.synthesizedAttr.top - 1; 
                        synthesizeAttributes(child2);  
                        child2.inhertitedAttr.code = gen(child1.synthesizedAttr.code, "iffalse", Integer.toString(child2.synthesizedAttr.next));
                        synthesizeAttributes(child2);  

                        node.synthesizedAttr.code = child2.synthesizedAttr.code;
                        node.synthesizedAttr.next = child2.synthesizedAttr.next;
                        node.synthesizedAttr.top = child2.synthesizedAttr.top;
                    }
                    break;

                case "while":
                    child1 = node.lefChild; 
                    child2 = child1.rightSibling; 

                    child1.inhertitedAttr.code = node.inhertitedAttr.code;  
                    child1.inhertitedAttr.next = node.inhertitedAttr.next;   
                    child1.inhertitedAttr.top = node.inhertitedAttr.top;  
                    child1.inhertitedAttr.error = node.inhertitedAttr.error; 
                    synthesizeAttributes(child1);  

                    child2.inhertitedAttr.code = tempasm;
                    child2.inhertitedAttr.next = child1.synthesizedAttr.next + 1;   
                    child2.inhertitedAttr.top = child1.synthesizedAttr.top - 1; 
                    child2.inhertitedAttr.error = temperror;
                    synthesizeAttributes(child2);  
                    
                    child2.inhertitedAttr.code = gen(child1.synthesizedAttr.code, "iffalse", Integer.toString(child2.synthesizedAttr.next + 1));
                    if (child1.synthesizedAttr.type.equals("boolean")){
                        child2.inhertitedAttr.error = child1.synthesizedAttr.error;
                    } else {
                        child2.inhertitedAttr.error = gen(child1.synthesizedAttr.error, "Illegal expression in while");
                    }
                    synthesizeAttributes(child2);  
                    
                    node.synthesizedAttr.code = gen(child2.synthesizedAttr.code, "goto", Integer.toString(node.inhertitedAttr.next));
                    node.synthesizedAttr.next = child2.synthesizedAttr.next + 1;
                    node.synthesizedAttr.top = child2.synthesizedAttr.top;
                    node.synthesizedAttr.type = "statement";
                    if (child2.synthesizedAttr.type.equals("statement")){
                        node.synthesizedAttr.error = child2.synthesizedAttr.error;
                    } else {
                        node.synthesizedAttr.error = gen(child2.synthesizedAttr.error, "Statement required in while");
                    }
                    break;

                case "=":
                    child1 = node.lefChild; 
                    child2 = child1.rightSibling;

                    child1.inhertitedAttr.code = node.inhertitedAttr.code;  
                    child1.inhertitedAttr.next = node.inhertitedAttr.next;   
                    child1.inhertitedAttr.top = node.inhertitedAttr.top;   
                    child1.inhertitedAttr.error = node.inhertitedAttr.error;  
                    synthesizeAttributes(child1);  

                    child2.inhertitedAttr.code = child1.synthesizedAttr.code;  
                    child2.inhertitedAttr.next = child1.synthesizedAttr.next;   
                    child2.inhertitedAttr.top = child1.synthesizedAttr.top; 
                    child2.inhertitedAttr.error = child1.synthesizedAttr.error; 
                    synthesizeAttributes(child2);  

                    node.synthesizedAttr.code = gen(child2.synthesizedAttr.code, "equal");
                    node.synthesizedAttr.next = child2.synthesizedAttr.next + 1;
                    node.synthesizedAttr.top = child2.synthesizedAttr.top - 1;
                    node.synthesizedAttr.type = "boolean";
                    if (child1.synthesizedAttr.type.equals(child2.synthesizedAttr.type)){
                        node.synthesizedAttr.error = child2.synthesizedAttr.error;
                    } else {
                        node.synthesizedAttr.error = gen(child2.synthesizedAttr.error, "Type clash in equal comparison");
                    }
                    break;

                case "+":
                    child1 = node.lefChild; 
                    child2 = child1.rightSibling;

                    child1.inhertitedAttr.code = node.inhertitedAttr.code;  
                    child1.inhertitedAttr.next = node.inhertitedAttr.next;   
                    child1.inhertitedAttr.top = node.inhertitedAttr.top;   
                    child1.inhertitedAttr.error = node.inhertitedAttr.error;  
                    synthesizeAttributes(child1);  

                    child2.inhertitedAttr.code = child1.synthesizedAttr.code;  
                    child2.inhertitedAttr.next = child1.synthesizedAttr.next;   
                    child2.inhertitedAttr.top = child1.synthesizedAttr.top; 
                    child2.inhertitedAttr.error = child1.synthesizedAttr.error; 
                    synthesizeAttributes(child2);  

                    node.synthesizedAttr.code = gen(child2.synthesizedAttr.code, "add");
                    node.synthesizedAttr.next = child2.synthesizedAttr.next + 1;
                    node.synthesizedAttr.top = child2.synthesizedAttr.top - 1;
                    node.synthesizedAttr.type = "integer";
                    if (child1.synthesizedAttr.type.equals("integer") && child2.synthesizedAttr.type.equals("integer")){
                        node.synthesizedAttr.error = child2.synthesizedAttr.error;
                    } else {
                        node.synthesizedAttr.error = gen(child2.synthesizedAttr.error, "Illegal type for plus");
                    }
                    break;

                case "-":
                    child1 = node.lefChild;

                    child1.inhertitedAttr.code = node.inhertitedAttr.code;  
                    child1.inhertitedAttr.next = node.inhertitedAttr.next;   
                    child1.inhertitedAttr.top = node.inhertitedAttr.top;   
                    child1.inhertitedAttr.error = node.inhertitedAttr.error;  
                    synthesizeAttributes(child1);  

                    if (node.numChildren == 1){
                        node.synthesizedAttr.code = gen(child1.synthesizedAttr.code, "negate");
                        node.synthesizedAttr.next = child1.synthesizedAttr.next + 1;   
                        node.synthesizedAttr.top = child1.synthesizedAttr.top; 
                        node.synthesizedAttr.type = "integer";
                        if (child1.synthesizedAttr.type.equals("integer")){
                            node.synthesizedAttr.error = child1.synthesizedAttr.error;
                        } else {
                            node.synthesizedAttr.error = gen(child1.synthesizedAttr.error, "Illegal type for negation");
                        }
                    } else {
                        child2 = child1.rightSibling;
                        
                        child2.inhertitedAttr.code = child1.synthesizedAttr.code;  
                        child2.inhertitedAttr.next = child1.synthesizedAttr.next;   
                        child2.inhertitedAttr.top = child1.synthesizedAttr.top; 
                        child2.inhertitedAttr.error = child1.synthesizedAttr.error; 
                        synthesizeAttributes(child2); 
                        
                        node.synthesizedAttr.code = gen(child2.synthesizedAttr.code, "subtract");
                        node.synthesizedAttr.next = child2.synthesizedAttr.next + 1;
                        node.synthesizedAttr.top = child2.synthesizedAttr.top - 1;
                        node.synthesizedAttr.type = "integer";
                        if (child1.synthesizedAttr.type.equals("integer") && child2.synthesizedAttr.type.equals("integer")){
                            node.synthesizedAttr.error = child2.synthesizedAttr.error;
                        } else {
                            node.synthesizedAttr.error = gen(child2.synthesizedAttr.error, "Illegal type for subtraction");
                        }
                    }
                    break;
                
                case "*":
                    child1 = node.lefChild; 
                    child2 = child1.rightSibling;

                    child1.inhertitedAttr.code = node.inhertitedAttr.code;  
                    child1.inhertitedAttr.next = node.inhertitedAttr.next;   
                    child1.inhertitedAttr.top = node.inhertitedAttr.top;   
                    child1.inhertitedAttr.error = node.inhertitedAttr.error;  
                    synthesizeAttributes(child1);  

                    child2.inhertitedAttr.code = child1.synthesizedAttr.code;  
                    child2.inhertitedAttr.next = child1.synthesizedAttr.next;   
                    child2.inhertitedAttr.top = child1.synthesizedAttr.top; 
                    child2.inhertitedAttr.error = child1.synthesizedAttr.error; 
                    synthesizeAttributes(child2);  

                    node.synthesizedAttr.code = gen(child2.synthesizedAttr.code, "mul");
                    node.synthesizedAttr.next = child2.synthesizedAttr.next + 1;
                    node.synthesizedAttr.top = child2.synthesizedAttr.top - 1;
                    node.synthesizedAttr.type = "integer";
                    if (child1.synthesizedAttr.type.equals("integer") && child2.synthesizedAttr.type.equals("integer")){
                        node.synthesizedAttr.error = child2.synthesizedAttr.error;
                    } else {
                        node.synthesizedAttr.error = gen(child2.synthesizedAttr.error, "Illegal type for multiplication");
                    }
                    break;

                case "/":
                    child1 = node.lefChild; 
                    child2 = child1.rightSibling;

                    child1.inhertitedAttr.code = node.inhertitedAttr.code;  
                    child1.inhertitedAttr.next = node.inhertitedAttr.next;   
                    child1.inhertitedAttr.top = node.inhertitedAttr.top;   
                    child1.inhertitedAttr.error = node.inhertitedAttr.error;  
                    synthesizeAttributes(child1);  

                    child2.inhertitedAttr.code = child1.synthesizedAttr.code;  
                    child2.inhertitedAttr.next = child1.synthesizedAttr.next;   
                    child2.inhertitedAttr.top = child1.synthesizedAttr.top; 
                    child2.inhertitedAttr.error = child1.synthesizedAttr.error; 
                    synthesizeAttributes(child2);  

                    node.synthesizedAttr.code = gen(child2.synthesizedAttr.code, "div");
                    node.synthesizedAttr.next = child2.synthesizedAttr.next + 1;
                    node.synthesizedAttr.top = child2.synthesizedAttr.top - 1;
                    node.synthesizedAttr.type = "integer";
                    if (child1.synthesizedAttr.type.equals("integer") && child2.synthesizedAttr.type.equals("integer")){
                        node.synthesizedAttr.error = child2.synthesizedAttr.error;
                    } else {
                        node.synthesizedAttr.error = gen(child2.synthesizedAttr.error, "Illegal type for division");
                    }
                    break;

                case "not":
                    child1 = node.lefChild;

                    child1.inhertitedAttr.code = node.inhertitedAttr.code;  
                    child1.inhertitedAttr.next = node.inhertitedAttr.next;   
                    child1.inhertitedAttr.top = node.inhertitedAttr.top; 
                    child1.inhertitedAttr.error = node.inhertitedAttr.error;    
                    synthesizeAttributes(child1);  

                    node.synthesizedAttr.code = gen(child1.synthesizedAttr.code, "not");
                    node.synthesizedAttr.next = child1.synthesizedAttr.next + 1;   
                    node.synthesizedAttr.top = child1.synthesizedAttr.top; 
                    node.synthesizedAttr.type = "boolean";
                    if (child1.synthesizedAttr.type.equals("boolean")){
                        node.synthesizedAttr.error = child1.synthesizedAttr.error;
                    } else {
                        node.synthesizedAttr.error = gen(child1.synthesizedAttr.error, "Illegal type for not");
                    }
                    break;
                
                case "integer":
                    child1 = node.lefChild; 

                    child1.inhertitedAttr.code = node.inhertitedAttr.code;  
                    child1.inhertitedAttr.next = node.inhertitedAttr.next;   
                    child1.inhertitedAttr.top = node.inhertitedAttr.top;
                    child1.inhertitedAttr.error = node.inhertitedAttr.error;    
                    synthesizeAttributes(child1);  

                    node.synthesizedAttr.code = child1.synthesizedAttr.code;  
                    node.synthesizedAttr.next = child1.synthesizedAttr.next;   
                    node.synthesizedAttr.top = child1.synthesizedAttr.top; 
                    node.synthesizedAttr.type = "integer";
                    if (child1.synthesizedAttr.type.equals("integer")){
                        node.synthesizedAttr.error = child1.synthesizedAttr.error;
                    } else {
                        node.synthesizedAttr.error = gen(child1.synthesizedAttr.error, "Illegal type for integer");
                    }
                    break;
                
                case "<integer>":
                    x = node.lefChild.value;

                    node.synthesizedAttr.code = gen(node.inhertitedAttr.code, "lit", x); 
                    node.synthesizedAttr.next = node.inhertitedAttr.next + 1;
                    node.synthesizedAttr.top = node.inhertitedAttr.top + 1; 
                    node.synthesizedAttr.error = node.inhertitedAttr.error;  
                    node.synthesizedAttr.type = "integer";

                    break;
                
                case "<identifier>":
                    x = node.lefChild.value;

                    node.synthesizedAttr.code = gen(node.inhertitedAttr.code, "load", Integer.toString(decTable.lookup(x)));
                    node.synthesizedAttr.next = node.inhertitedAttr.next + 1;
                    node.synthesizedAttr.top = node.inhertitedAttr.top + 1; 
                    node.synthesizedAttr.error = node.inhertitedAttr.error;  
                    node.synthesizedAttr.type = "integer";

                    break;
            }
        }
    }  
}

class SynthesizedAttributes{
    BufferedWriter code;
    int next;
    BufferedWriter error;
    int top;
    String type;

    public SynthesizedAttributes(){
        this.code = null;
        this.next = -1;
        this.error = null;
        this.top = -1;
        this.type = null;
    }
}

class InhertitedAttributes{
    BufferedWriter code;
    int next;
    BufferedWriter error;
    int top;

    public InhertitedAttributes(){
        this.code = null;
        this.next = -1;
        this.error = null;
        this.top = -1;
    }
}

class AttributeNode{
    String value;
    AttributeNode lefChild, rightSibling;
    int numChildren;
    SynthesizedAttributes synthesizedAttr; // { code ↑, next ↑, error ↑, top ↑, type ↑ }
    InhertitedAttributes inhertitedAttr; // { code ↓, next ↓, error ↓, top ↓ }

    public AttributeNode() {
        this.value = null;
        this.numChildren = 0;
        this.lefChild = null;
        this.rightSibling = null;
        this.synthesizedAttr = new SynthesizedAttributes();
        this.inhertitedAttr = new InhertitedAttributes();
    }

    public AttributeNode(String value, int numChildren, AttributeNode lefChild, AttributeNode rightSibling) {
        this.value = value;
        this.numChildren = numChildren;
        this.lefChild = lefChild;
        this.rightSibling = rightSibling;
        this.synthesizedAttr = new SynthesizedAttributes();
        this.inhertitedAttr = new InhertitedAttributes();
    }
}
  
class AttributeTree{
    AttributeNode root;
  
    public AttributeTree(AttributeNode root){
        this.root = root;
    }

    public void traverseTree(AttributeNode node, int traverseDepth) {
        if (node != null) {
            System.out.println(". ".repeat(traverseDepth) + node.value + "(" + node.numChildren + ")");
            traverseTree(node.lefChild, traverseDepth+1);
            traverseTree(node.rightSibling, traverseDepth);
        }
    }
}

class DeclarationTable{
    Hashtable<String,Integer> decTable = new Hashtable<String,Integer>(); 

    public int enter(String name, int l){
        decTable.put(name, l);
        return l;
    }

    public int lookup(String name){
        int l;
        try{
            l = decTable.get(name);
        } catch (NullPointerException e){
            l = 0;
        }
        return l;  
    }
}

