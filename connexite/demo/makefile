LIB=~/lib/gs-core-1.1.1.jar:~/lib/gs-ui-1.1.1.jar
SRC=.
CP=$(LIB):$(SRC)

SENSORS=200
SIZE=1000
RADII=false

all: compile execute

execute: compile
	java -classpath $(CP) Simulation $(SENSORS) $(SIZE) $(RADII)

compile: Simulation.java BackgroundRenderer.java
	javac -classpath $(CP) *.java
