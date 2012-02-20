LIB=~/lib/gs-core-1.1.1.jar:~/lib/gs-ui-1.1.1.jar
SRC=.
CP=$(LIB):$(SRC)

SENSORS=200
OBSTACLES=false
SIZE=1000
RADII=false
CAPTURE=""

all: compile execute

execute: compile
	java -classpath $(CP) Simulation $(SENSORS) $(OBSTACLES) $(SIZE) $(RADII) $(CAPTURE)

compile: Simulation.java BackgroundRenderer.java
	javac -classpath $(CP) *.java
