// Floyd-Warshall algorithm - iterative version
//
// Author: Rahul Simha
// Adapted by Merwan Achibet

public class FloydWarshallIter {

    int numVertices;
    double[][] adjMatrix;
    double[][] D;

    public void initialize (int numVertices)
    {
	this.numVertices = numVertices;

	D = new double[numVertices][];
	for(int i = 0; i < numVertices; ++i)
	    D[i] = new double[numVertices];
    }

    public void print(double[][] m) {

	for(int i = 0; i < numVertices; ++i) {

	    for(int j = 0; j < numVertices; ++j)
		System.out.print(m[i][j] == Double.MAX_VALUE ? "inf " : (int)m[i][j] + " ");
	    
	    System.out.print("\n");
	}
    }

    public void allPairsMaximumCapacity (double[][] adjMatrix)
    {

	for(int i = 0; i < numVertices; ++i)
	    for(int j = 0; j < numVertices; ++j)
		D[i][j] = adjMatrix[i][j];

	boolean changeOccurred = true;

	while(changeOccurred) {

	    changeOccurred = false;

	    for (int i=0; i<numVertices; i++) {
		for (int d=0; d<numVertices; d++) {

		    if (i != d) {

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

	print(D);
    }


    public static void main (String[] argv)
    {

	double inf = Double.MAX_VALUE;

	double[][] adjMatrix = {
	    {inf, 12, 14, 10, 0, 0, 0, 0},
	    {12, inf, 0, 17, 8, 0, 0, 0},
	    {14, 0, inf, 5, 0, 3, 0, 0},
	    {10, 17, 5, inf, 11, 6, 15, 0},
	    {0, 8, 0, 11, inf, 0, 18, 11},
	    {0, 0, 3, 6, 0, inf, 4,15},
	    {0, 0, 0, 15, 18, 4, inf, 9},
	    {0, 0, 0, 0, 11, 15, 9, inf}
	};

	FloydWarshallIter fwAlg = new FloydWarshallIter();
	fwAlg.initialize(adjMatrix.length);
	fwAlg.allPairsMaximumCapacity(adjMatrix);
    }
}
