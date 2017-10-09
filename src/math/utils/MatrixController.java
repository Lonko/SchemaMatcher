package math.utils;

import java.util.ArrayList;
import java.util.Collections;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

public class MatrixController {
	private static final String SUM_TYPE = "max";

	public MatrixController(){
	}
	
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
	
	private Matrix getAbsEigendec(double[][] m){
		Matrix matrix = new Matrix(m);
		EigenvalueDecomposition evd = matrix.eig();
		Matrix diagonal = evd.getD();
		Matrix vectorMatrix = evd.getV();
		vectorMatrix = getAbsOrderedMatrix(diagonal, vectorMatrix);
		return vectorMatrix;
	}
	
	private Matrix getAbsOrderedMatrix(Matrix d, Matrix v){
		int columns = d.getColumnDimension();
		int[] indexes = new int[columns];
		ArrayList<Integer> originalVector = new ArrayList<>();
		ArrayList<Integer> sortedVector = new ArrayList<>();
		Matrix orderedMatrix = new Matrix(columns, columns);
		
		for(int i = 0; i < columns; i++){
			originalVector.add(i);
			sortedVector.add(i);
		}
		
		Collections.sort(sortedVector,(a,b)->b.compareTo(a));
		
		for(int i = 0; i < columns; i++){
			int val = originalVector.get(i);
			indexes[i] = sortedVector.indexOf(val);
		}
		
		for(int i = 0; i < columns; i++)
			for(int j = 0; j < columns; j++){
				int index = indexes[j];
				orderedMatrix.set(i, j, Math.abs(v.get(i, index)));
			}
	
		return orderedMatrix;
	}
	
}
