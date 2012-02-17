LIB=~/lib/gs-core-1.1.1.jar:~/lib/gs-ui-1.1.1.jar
SRC=.
CP=$(LIB):$(SRC)

all: compile execute

execute: compile
	java -classpath $(CP) Test

compile: Test.java Simulation.java
	javac -classpath $(CP) *.java
