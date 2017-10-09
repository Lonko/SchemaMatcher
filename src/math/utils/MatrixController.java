package math.utils;

import java.util.ArrayList;
import java.util.Collections;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

public class MatrixController {
	private static final String SUM_TYPE = "max";

	public MatrixController(){
	}
	
	/* takes 2 matrixes representing 2 schemas in input and uses eigendecomposition to obtain a single matrix
	 * on which the hungarian method can be used to obtain a matrix representing a match between the 2 schemas 
	 */
	public int[] getMatchIndex(double[][] m1, double[][] m2){
		Matrix leftOperator = getAbsEigendec(m2);
		Matrix rightOperator = getAbsEigendec(m1).transpose();
		Matrix prodMatrix = leftOperator.times(rightOperator);
		String sumType = SUM_TYPE;
		int[][] matchIndexMatrix = new HungarianAlgorithm().hgAlgorithm(prodMatrix.getArrayCopy(), sumType);
		int[] matchIndexArray = new int[matchIndexMatrix.length];
		
		for(int i = 0; i < matchIndexMatrix.length; i++){
			int index = matchIndexMatrix[i][0];
			matchIndexArray[index] = matchIndexMatrix[i][1];
		}

		return matchIndexArray;
		
	}
	
	/* returns the absolute version of the eigenvector matrix obtained from the matrix m 
	 */
	private Matrix getAbsEigendec(double[][] m){
		Matrix matrix = new Matrix(m);
		EigenvalueDecomposition evd = matrix.eig();
		Matrix diagonal = evd.getD();
		Matrix vectorMatrix = evd.getV();
		vectorMatrix = getAbsOrderedMatrix(diagonal, vectorMatrix);
		return vectorMatrix;
	}
	
	
	/* returns the absolute version of a matrix whose columns are a permutation of the matrix v,
 	 * the permutation is obtained applying the same changes in the order of the columns
 	 * as the ones applyed to the diagonal matrix d to sort its elements in a decreasing order
	 */
	private Matrix getAbsOrderedMatrix(Matrix d, Matrix v){
		int columns = d.getColumnDimension();
		int[] indexes = new int[columns];
		ArrayList<Double> originalVector = new ArrayList<>();
		ArrayList<Double> sortedVector = new ArrayList<>();
		Matrix orderedMatrix = new Matrix(columns, columns);
		
		for(int i = 0; i < columns; i++){
			originalVector.add(d.get(i,i));
			sortedVector.add(d.get(i,i));
		}
		
		Collections.sort(sortedVector,(a,b)->b.compareTo(a));
		
		//get the new indexes of the sorted elements in the diagonal matrix
		for(int i = 0; i < columns; i++){
			double val = originalVector.get(i);
			indexes[i] = sortedVector.indexOf(val);
		}
		
		//create the new matrix
		for(int i = 0; i < columns; i++)
			for(int j = 0; j < columns; j++){
				int index = indexes[j];
				orderedMatrix.set(i, j, Math.abs(v.get(i, index)));
			}
	
		return orderedMatrix;
	}
	
}
