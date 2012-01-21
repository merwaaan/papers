// Floyd-Warshall algorithm - iterative version
//
// Author: Rahul Simha
// Date: Oct, 2001.

public class FloydWarshallIter {
  
    int numVertices;
    double[][] adjMatrix;
    double[][] D; // Matrix used in dynamic programming.
 
    public void initialize (int numVertices)
    {
	this.numVertices = numVertices;

	D = new double [numVertices][];
	for (int i=0; i < numVertices; i++)
	    D[i] = new double [numVertices];
    }

    public void print(double[][] m) {

	for(int i = 0; i < numVertices; ++i) {
	    for(int j = 0; j < numVertices; ++j)
		System.out.print(m[i][j] + " ");
	    System.out.print("\n");
	}
    }

    public void allPairsShortestPaths (double[][] adjMatrix)
    {

	// D = weights when k = -1
	for (int i=0; i<numVertices; i++) {
	    for (int j=0; j<numVertices; j++) {
		if (i != j) {
		    if (adjMatrix[i][j] > 0)
			D[i][j] = adjMatrix[i][j];
		    else 
			D[i][j] = Double.MAX_VALUE;
		}
		else
		    D[i][j] = 0;
	    }
	}
    
	print(adjMatrix);
	print(D);

	// We will stop when there are no further changes
	// Therefore, we need a variable to track whether a changed occured.
	boolean changeOccurred = true;

	while(changeOccurred) {

	    changeOccurred = false;

	    for (int i=0; i<numVertices; i++) {
		for (int d=0; d<numVertices; d++) {   // NOTE: "d" instead of "j"

		    if (i != d) {

			// For the shortest-cost between i and d, explore neighbors j.
			for (int j=0; j<numVertices; j++) {
			    if ( (j != i) && (adjMatrix[i][j] > 0) ) {

				// If it's better via j, then reflect the new cost.
				if (Math.min(D[j][d], adjMatrix[i][j]) > D[i][d]) {
				    D[i][d] = Math.min(D[j][d], adjMatrix[i][j]);
				    // Change has occured, which may propagate.
				    changeOccurred = true;
				}
			    }
			}
            
		    }
          
		}
        
	    }
      
	}
    
	// ... path construction ...

	print(D);
    }
  

    public static void main (String[] argv)
    {

	double[][] adjMatrix = {
	    {0, 12, 14, 10, 0, 0, 0, 0},
	    {12, 0, 0, 17, 8, 0, 0, 0},
	    {14, 0, 0, 5, 0, 3, 0, 0},
	    {10, 17, 5, 0, 11, 6, 15, 0},
	    {0, 8, 0, 11, 0, 0, 18, 11},
	    {0, 0, 3, 6, 0, 0, 4,15},
	    {0, 0, 0, 15, 18, 4, 0, 9},
	    {0, 0, 0, 0, 11, 15, 9, 0}
	};

	FloydWarshallIter fwAlg = new FloydWarshallIter();
	fwAlg.initialize(adjMatrix.length);
	fwAlg.allPairsShortestPaths(adjMatrix);
    }
}
