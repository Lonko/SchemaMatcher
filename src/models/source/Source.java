package models.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Source {
	private String website;
	private String category;
	private ArrayList<Attribute> attributes;
	
	public Source(String website, String category, ArrayList<Attribute> attributes){
		this.website = website;
		this.category = category;
		this.attributes = new ArrayList<Attribute>(attributes);
	}

	public double getMutualInformation(String label1, String label2){
		double mi = 0.0;
		Attribute a1 = getAttribute(label1);
		Attribute a2 = getAttribute(label2);
		
		double[][] matrix = getJointProbDistr(a1.getValues(), a2.getValues(),
												a1.getDistinctValues(), a2.getDistinctValues());
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
	
	private double[] getMarginalProbabilityDistribution(double[][] jointPD, boolean row){
		int dim1 =  row ? jointPD.length : jointPD[0].length;
		int dim2 =  row ? jointPD[0].length : jointPD.length;
		double[] margProb = new double[dim1];

		/* iterates rows -> columns if row == true
		 * else  columns -> rows
		 */
		for(int i = 0; i < dim1; i++){
			double acc = 0.0;
			for(int j = 0; j < dim2; j++)
				if(row)
					acc += jointPD[i][j];
				else
					acc += jointPD[j][i];
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
			if(value1.equals("#NULL#") || value2.equals("#NULL#"))
				continue;
			int index1 = distinctValues1.indexOf(value1);
			int index2 = distinctValues2.indexOf(value2);
			matrix[index1][index2]++;
		}
		
		for(int i = 0; i < distinctValues1.size(); i++)
			for(int j = 0; j < distinctValues2.size(); j++)
				matrix[i][j] /= values1.size();
		
		return matrix;
	}

	/* Returns a map containing lists of sources, each with a subset of the original's 
	 * set of attributes. The cardinality of these sources is in the range [1, upperBound]
	 */
	public HashMap<Integer, ArrayList<Source>> getPowerSet(int upperBound){
		ArrayList<Source> powerSet = new ArrayList<>();
		powerSet.add(new Source(this.website, this.category, new ArrayList<Attribute>()));
		
		//create the powerset
		if(this.attributes.size() >= 1)
			for(Attribute a : this.attributes){
				ArrayList<Source> newPowerSet = new ArrayList<>();
				for(Source subset : powerSet){
					if(subset.getAttributes().size() == upperBound)
						continue;
					else{
						newPowerSet.add(subset);
						Source newSubset = new Source(this.website, this.category, subset.getAttributes());
						newSubset.addAttribute(a);
						newPowerSet.add(newSubset);
					}
				}
				powerSet = newPowerSet;
			}
		
		HashMap<Integer, ArrayList<Source>> psMap = new HashMap<>();
		/* trasform powerset into a map with entries in the form:
		 *    cardinality -> list of subsets 
		 */
		powerSet.forEach(source -> {
			int size = source.getAttributes().size();
			ArrayList<Source> sourceList = psMap.getOrDefault(size, new ArrayList<Source>());
			sourceList.add(source);
			psMap.put(size, sourceList);
		});
		
		return psMap;
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
	
	public void addAttribute(Attribute a){
		this.attributes.add(a);
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
