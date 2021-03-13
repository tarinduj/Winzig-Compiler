JC = javac

CLASSES = \
	winzigc.java\
	Lexer.java\
	Parser.java

default:
	$(JC) *.java

classes: 
	$(JC) $(CLASSES)

run: $(MAIN).class
	$(JVM) $(MAIN)

clean:
	$(RM) *.class
	$(RM) tree.*
	$(RM) asmfile