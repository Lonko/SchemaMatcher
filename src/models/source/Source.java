package models.source;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

public class Source {
	private String website;
	private String category;
	private ArrayList<Attribute> attributes;
	
	public Source(String website, String category, ArrayList<Attribute> attributes){
		this.website = website;
		this.category = category;
		this.attributes = attributes;
	}
	
	public double getEntropy(String label){
		double entropy = 0.0;
		ArrayList<String> valueList, distinctValueList;
		
		valueList = getAttribute(label).getValues();
		distinctValueList = valueList.stream().distinct().collect(Collectors.toCollection(ArrayList::new));
		for(String val : distinctValueList){
			int n = Collections.frequency(valueList, val);
			double p = (double)n/valueList.size();
			if(label.equals("C"))
				System.out.println(p*(Math.log(p)/Math.log(2)));
			entropy -= p*(Math.log(p)/Math.log(2));
		}
		
		return entropy;
	}

	public double getMutualInformation(String label1, String label2){
		ArrayList<String> valueList1, valueList2, distinctValueList1, distinctValueList2;
		double mi = 0.0;
		
		valueList1 = getAttribute(label1).getValues();
		valueList2 = getAttribute(label2).getValues();
		distinctValueList1 = valueList1.stream().distinct().collect(Collectors.toCollection(ArrayList::new));
		distinctValueList2 = valueList2.stream().distinct().collect(Collectors.toCollection(ArrayList::new));	
		if(valueList1 == null || valueList2 == null){
			System.err.println("Entrambe le label devono essere corrette! Label utilizzate:" +
					label1 + "\t" + label2);
			return -1.0;
		}
		
		double[][] matrix = getJointProbDistr(valueList1, valueList2, distinctValueList1, distinctValueList2);
		double[] margProb1, margProb2;
		margProb1 = getMarginalProbabilityDistribution(matrix, true);
		margProb2 = getMarginalProbabilityDistribution(matrix, false);	
		
		for(int i = 0; i < margProb1.length; i++)
			for(int j = 0; j < margProb2.length; j++){
				if(matrix[i][j] == 0) 
					continue;
				double logArg = matrix[i][j]/(margProb1[i]*margProb2[j]);
				mi += matrix[i][j]*(Math.log(logArg)/Math.log(2));
			}
		
		return mi;
	}
	
	private double[] getMarginalProbabilityDistribution(double[][] matrix, boolean row){
		int rows = matrix.length;
		int columns = matrix[0].length;
		int length = row ? rows : columns;
		double[] margProb = new double[length];
		
		if(row) //iterate rows->columns
			for(int i = 0; i < rows; i++){
				double acc = 0.0;
				for(int j = 0; j < columns; j++)
					acc += matrix[i][j];
				margProb[i] = acc;
			}
		else	//iterate columns->rows
			for(int i = 0; i < columns; i++){
				double acc = 0.0;
				for(int j = 0; j < rows; j++)
					acc += matrix[j][i];
				margProb[i] = acc;
			}
			
		return margProb;
	}
	
	private double[][] getJointProbDistr(ArrayList<String> values1, ArrayList<String> values2, 
						ArrayList<String> distinctValues1, ArrayList<String> distinctValues2){
		
		double[][] matrix = new double[distinctValues1.size()][distinctValues2.size()];
		for(int i = 0; i < values1.size(); i++){
			String value1 = values1.get(i);
			String value2 = values2.get(i);
			int index1 = distinctValues1.indexOf(value1);
			int index2 = distinctValues2.indexOf(value2);
			matrix[index1][index2]++;
		}
		
		for(int i = 0; i < distinctValues1.size(); i++)
			for(int j = 0; j < distinctValues2.size(); j++)
				matrix[i][j] /= values1.size();
		
		return matrix;
	}
	
	public String getWebsite() {
		return this.website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getCategory() {
		return this.category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	public Attribute getAttribute(String label){
		for(Attribute a : this.attributes){
			if(a.getLabel().equals(label))
				return a;
		}
		return null;
	}

	public ArrayList<Attribute> getAttributes() {
		return this.attributes;
	}

	public void setAttributes(ArrayList<Attribute> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String toString() {
		return "Source [website=" + website + ", category=" + category
				+ ", attributes=" + attributes + "]";
	}
	
}
