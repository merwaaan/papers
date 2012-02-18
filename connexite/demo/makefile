LIB=~/lib/gs-core-1.1.1.jar:~/lib/gs-ui-1.1.1.jar
SRC=.
CP=$(LIB):$(SRC)

all: compile execute

execute: compile
	java -classpath $(CP) Simulation

compile: Simulation.java BackgroundRenderer.java
	javac -classpath $(CP) *.java
