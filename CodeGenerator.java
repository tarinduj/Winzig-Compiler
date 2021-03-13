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
        AttributeNode child1, child2, child3, child4, child5, child6;
        String x;
        BufferedWriter temp = new BufferedWriter(new FileWriter("asm.temp"));
        if (node != null) {
            switch (node.value) {
                case "program":
                    child1 = node.lefChild;
                    child2 = child1.rightSibling;
                    child3 = child2.rightSibling;
                    child4 = child3.rightSibling;
                    child5 = child4.rightSibling;
                    child6 = child5.rightSibling;
                    
                    child2.inhertitedAttr.code = openFile("asmfile");
                    child2.inhertitedAttr.next = 1;
                    child2.inhertitedAttr.top = 0;
                    synthesizeAttributes(child2);

                    child3.inhertitedAttr.code = child2.synthesizedAttr.code;
                    child3.inhertitedAttr.next = child2.synthesizedAttr.next;
                    child3.inhertitedAttr.top = child2.synthesizedAttr.top;
                    synthesizeAttributes(child3);

                    child4.inhertitedAttr.code = child3.synthesizedAttr.code;
                    child4.inhertitedAttr.next = child3.synthesizedAttr.next;
                    child4.inhertitedAttr.top = child3.synthesizedAttr.top;
                    synthesizeAttributes(child4);

                    child5.inhertitedAttr.code = child4.synthesizedAttr.code;
                    child5.inhertitedAttr.next = child4.synthesizedAttr.next;
                    child5.inhertitedAttr.top = child4.synthesizedAttr.top;
                    synthesizeAttributes(child5);

                    child6.inhertitedAttr.code = child5.synthesizedAttr.code;
                    child6.inhertitedAttr.next = child5.synthesizedAttr.next;
                    child6.inhertitedAttr.top = child5.synthesizedAttr.top;
                    synthesizeAttributes(child6);

                    node.synthesizedAttr.code = closeFile(gen(child6.synthesizedAttr.code, "stop"));  
                    break;

                case "consts": //deafaults
                    if (node.numChildren == 0){
                        node.synthesizedAttr.code = node.inhertitedAttr.code;  
                        node.synthesizedAttr.next = node.inhertitedAttr.next;   
                        node.synthesizedAttr.top = node.inhertitedAttr.top; 
                    } else {
                        currChild = node.lefChild;
                        currChild.inhertitedAttr.code = node.inhertitedAttr.code;  
                        currChild.inhertitedAttr.next = node.inhertitedAttr.next;   
                        currChild.inhertitedAttr.top = node.inhertitedAttr.top;   
                        synthesizeAttributes(currChild); 
                        prevChild = currChild;

                        for (int i = 1; i < node.numChildren; i++) {
                            currChild = currChild.rightSibling;
                            currChild.inhertitedAttr.code = prevChild.synthesizedAttr.code;  
                            currChild.inhertitedAttr.next = prevChild.synthesizedAttr.next;   
                            currChild.inhertitedAttr.top = prevChild.synthesizedAttr.top; 
                            synthesizeAttributes(currChild);    
                            prevChild = currChild;
                        } 

                        node.synthesizedAttr.code = prevChild.synthesizedAttr.code;  
                        node.synthesizedAttr.next = prevChild.synthesizedAttr.next;   
                        node.synthesizedAttr.top = prevChild.synthesizedAttr.top;  
                    }  
                    break;   

                case "types": //defaults
                    if (node.numChildren == 0){
                        node.synthesizedAttr.code = node.inhertitedAttr.code;  
                        node.synthesizedAttr.next = node.inhertitedAttr.next;   
                        node.synthesizedAttr.top = node.inhertitedAttr.top; 
                    } else {
                        currChild = node.lefChild;
                        currChild.inhertitedAttr.code = node.inhertitedAttr.code;  
                        currChild.inhertitedAttr.next = node.inhertitedAttr.next;   
                        currChild.inhertitedAttr.top = node.inhertitedAttr.top;   
                        synthesizeAttributes(currChild); 
                        prevChild = currChild;

                        for (int i = 1; i < node.numChildren; i++) {
                            currChild = currChild.rightSibling;
                            currChild.inhertitedAttr.code = prevChild.synthesizedAttr.code;  
                            currChild.inhertitedAttr.next = prevChild.synthesizedAttr.next;   
                            currChild.inhertitedAttr.top = prevChild.synthesizedAttr.top; 
                            synthesizeAttributes(currChild);    
                            prevChild = currChild;
                        } 

                        node.synthesizedAttr.code = prevChild.synthesizedAttr.code;  
                        node.synthesizedAttr.next = prevChild.synthesizedAttr.next;   
                        node.synthesizedAttr.top = prevChild.synthesizedAttr.top;  
                    }  
                    break;   

                case "dclns": //defaults
                    if (node.numChildren == 0){
                        node.synthesizedAttr.code = node.inhertitedAttr.code;  
                        node.synthesizedAttr.next = node.inhertitedAttr.next;   
                        node.synthesizedAttr.top = node.inhertitedAttr.top; 
                    } else {
                        currChild = node.lefChild;
                        currChild.inhertitedAttr.code = node.inhertitedAttr.code;  
                        currChild.inhertitedAttr.next = node.inhertitedAttr.next;   
                        currChild.inhertitedAttr.top = node.inhertitedAttr.top;   
                        synthesizeAttributes(currChild); 
                        prevChild = currChild;

                        for (int i = 1; i < node.numChildren; i++) {
                            currChild = currChild.rightSibling;
                            currChild.inhertitedAttr.code = prevChild.synthesizedAttr.code;  
                            currChild.inhertitedAttr.next = prevChild.synthesizedAttr.next;   
                            currChild.inhertitedAttr.top = prevChild.synthesizedAttr.top; 
                            synthesizeAttributes(currChild);    
                            prevChild = currChild;
                        } 

                        node.synthesizedAttr.code = prevChild.synthesizedAttr.code;  
                        node.synthesizedAttr.next = prevChild.synthesizedAttr.next;   
                        node.synthesizedAttr.top = prevChild.synthesizedAttr.top;  
                    }  
                    break;  

                case "subprogs": //defaults
                    if (node.numChildren == 0){
                        node.synthesizedAttr.code = node.inhertitedAttr.code;  
                        node.synthesizedAttr.next = node.inhertitedAttr.next;   
                        node.synthesizedAttr.top = node.inhertitedAttr.top; 
                    } else {
                        currChild = node.lefChild;
                        currChild.inhertitedAttr.code = node.inhertitedAttr.code;  
                        currChild.inhertitedAttr.next = node.inhertitedAttr.next;   
                        currChild.inhertitedAttr.top = node.inhertitedAttr.top;   
                        synthesizeAttributes(currChild); 
                        prevChild = currChild;

                        for (int i = 1; i < node.numChildren; i++) {
                            currChild = currChild.rightSibling;
                            currChild.inhertitedAttr.code = prevChild.synthesizedAttr.code;  
                            currChild.inhertitedAttr.next = prevChild.synthesizedAttr.next;   
                            currChild.inhertitedAttr.top = prevChild.synthesizedAttr.top; 
                            synthesizeAttributes(currChild);    
                            prevChild = currChild;
                        } 

                        node.synthesizedAttr.code = prevChild.synthesizedAttr.code;  
                        node.synthesizedAttr.next = prevChild.synthesizedAttr.next;   
                        node.synthesizedAttr.top = prevChild.synthesizedAttr.top;  
                    }  
                    break; 

                case "block": //defaults
                    currChild = node.lefChild;
                    currChild.inhertitedAttr.code = node.inhertitedAttr.code;  
                    currChild.inhertitedAttr.next = node.inhertitedAttr.next;   
                    currChild.inhertitedAttr.top = node.inhertitedAttr.top;
                    synthesizeAttributes(currChild); 
                    prevChild = currChild;

                    for (int i = 1; i < node.numChildren; i++) {
                        currChild = currChild.rightSibling;
                        currChild.inhertitedAttr.code = prevChild.synthesizedAttr.code;  
                        currChild.inhertitedAttr.next = prevChild.synthesizedAttr.next;   
                        currChild.inhertitedAttr.top = prevChild.synthesizedAttr.top; 
                        synthesizeAttributes(currChild);    
                        prevChild = currChild;
                    } 
                  
                    node.synthesizedAttr.code = prevChild.synthesizedAttr.code;  
                    node.synthesizedAttr.next = prevChild.synthesizedAttr.next;   
                    node.synthesizedAttr.top = prevChild.synthesizedAttr.top;     
                    break; 
                
                case "var":
                    node.synthesizedAttr.code = node.inhertitedAttr.code;  
                    node.synthesizedAttr.next = node.inhertitedAttr.next;   
                    node.synthesizedAttr.top = node.inhertitedAttr.top; 
                    break;

                case "assign":
                    child1 = node.lefChild; //<identifier>
                    child2 = child1.rightSibling; //Expression
                    x = child1.lefChild.value;
                    
                    child2.inhertitedAttr.code = node.inhertitedAttr.code;  
                    child2.inhertitedAttr.next = node.inhertitedAttr.next; 
                    child2.inhertitedAttr.top = node.inhertitedAttr.top; 
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

                    break;
                
                case "while":
                    child1 = node.lefChild; 
                    child2 = child1.rightSibling; 

                    child1.inhertitedAttr.code = node.inhertitedAttr.code;  
                    child1.inhertitedAttr.next = node.inhertitedAttr.next;   
                    child1.inhertitedAttr.top = node.inhertitedAttr.top;  
                    synthesizeAttributes(child1);  

                    child2.inhertitedAttr.code = gen(temp, "iffalse", Integer.toString(child2.synthesizedAttr.next + 1));
                    child2.inhertitedAttr.next = child1.synthesizedAttr.next + 1;   
                    child2.inhertitedAttr.top = child1.synthesizedAttr.top - 1; 
                    synthesizeAttributes(child2);  
                    child2.inhertitedAttr.code = gen(child1.synthesizedAttr.code, "iffalse", Integer.toString(child2.synthesizedAttr.next + 1));
                    synthesizeAttributes(child2);  
                    
                    node.synthesizedAttr.code = gen(child2.synthesizedAttr.code, "goto", Integer.toString(node.inhertitedAttr.next));
                    node.synthesizedAttr.next = child2.synthesizedAttr.next + 1;
                    node.synthesizedAttr.top = child2.synthesizedAttr.top;
                    break;

                case "not":
                    child1 = node.lefChild;

                    child1.inhertitedAttr.code = node.inhertitedAttr.code;  
                    child1.inhertitedAttr.next = node.inhertitedAttr.next;   
                    child1.inhertitedAttr.top = node.inhertitedAttr.top;   
                    synthesizeAttributes(child1);  

                    node.synthesizedAttr.code = gen(child1.synthesizedAttr.code, "not");
                    node.synthesizedAttr.next = child1.synthesizedAttr.next + 1;   
                    node.synthesizedAttr.top = child1.synthesizedAttr.top; 
                    break;

                case "=":
                    child1 = node.lefChild; 
                    child2 = child1.rightSibling;
                    x = child1.lefChild.value;

                    child1.inhertitedAttr.code = node.inhertitedAttr.code;  
                    child1.inhertitedAttr.next = node.inhertitedAttr.next;   
                    child1.inhertitedAttr.top = node.inhertitedAttr.top;   
                    synthesizeAttributes(child1);  

                    child2.inhertitedAttr.code = child1.synthesizedAttr.code;  
                    child2.inhertitedAttr.next = child1.synthesizedAttr.next;   
                    child2.inhertitedAttr.top = child1.synthesizedAttr.top; 
                    synthesizeAttributes(child2);  

                    node.synthesizedAttr.code = gen(child2.synthesizedAttr.code, "equal");
                    node.synthesizedAttr.next = child2.synthesizedAttr.next + 1;
                    node.synthesizedAttr.top = child2.synthesizedAttr.top - 1;
                    break;

                case "+":
                    child1 = node.lefChild; 
                    child2 = child1.rightSibling;

                    child1.inhertitedAttr.code = node.inhertitedAttr.code;  
                    child1.inhertitedAttr.next = node.inhertitedAttr.next;   
                    child1.inhertitedAttr.top = node.inhertitedAttr.top;   
                    synthesizeAttributes(child1);  

                    child2.inhertitedAttr.code = child1.synthesizedAttr.code;  
                    child2.inhertitedAttr.next = child1.synthesizedAttr.next;   
                    child2.inhertitedAttr.top = child1.synthesizedAttr.top; 
                    synthesizeAttributes(child2);  

                    node.synthesizedAttr.code = gen(child2.synthesizedAttr.code, "add");
                    node.synthesizedAttr.next = child2.synthesizedAttr.next + 1;
                    node.synthesizedAttr.top = child2.synthesizedAttr.top - 1;
                    break;
                
                case "output":
                    currChild = node.lefChild;
                    currChild.inhertitedAttr.code = node.inhertitedAttr.code;  
                    currChild.inhertitedAttr.next = node.inhertitedAttr.next;   
                    currChild.inhertitedAttr.top = node.inhertitedAttr.top;   
                    synthesizeAttributes(currChild); 
                    prevChild = currChild;

                    for (int i = 1; i < node.numChildren; i++) {
                        System.out.println(i);
                        currChild = currChild.rightSibling;
                        currChild.inhertitedAttr.code = gen(prevChild.synthesizedAttr.code, "print");  
                        currChild.inhertitedAttr.next = prevChild.synthesizedAttr.next + 1;   
                        currChild.inhertitedAttr.top = prevChild.synthesizedAttr.top - 1; 
                        synthesizeAttributes(currChild);    
                        prevChild = currChild;
                    } 
                
                    node.synthesizedAttr.code = gen(prevChild.synthesizedAttr.code, "print"); 
                    node.synthesizedAttr.next = prevChild.synthesizedAttr.next + 1;   
                    node.synthesizedAttr.top = prevChild.synthesizedAttr.top - 1;     
                    break;  
                
                case "integer":
                    child1 = node.lefChild; 

                    child1.inhertitedAttr.code = node.inhertitedAttr.code;  
                    child1.inhertitedAttr.next = node.inhertitedAttr.next;   
                    child1.inhertitedAttr.top = node.inhertitedAttr.top;   
                    synthesizeAttributes(child1);  

                    node.synthesizedAttr.code = child1.synthesizedAttr.code;  
                    node.synthesizedAttr.next = child1.synthesizedAttr.next;   
                    node.synthesizedAttr.top = child1.synthesizedAttr.top; 
                    break;
                
                case "<integer>":
                    x = node.lefChild.value;

                    node.synthesizedAttr.code = gen(node.inhertitedAttr.code, "lit", x); 
                    node.synthesizedAttr.next = node.inhertitedAttr.next + 1;
                    node.synthesizedAttr.top = node.inhertitedAttr.top + 1; 

                    break;
                
                case "<identifier>":
                    x = node.lefChild.value;

                    node.synthesizedAttr.code = gen(node.inhertitedAttr.code, "load", Integer.toString(decTable.lookup(x)));
                    node.synthesizedAttr.next = node.inhertitedAttr.next + 1;
                    node.synthesizedAttr.top = node.inhertitedAttr.top + 1; 

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

