package models.source;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

public class Attribute {
	private String label;
	private ArrayList<String> values;
	private static final String NULL_VALUE = "#NULL#";
	
	public Attribute(String attributeLabel, ArrayList<String> attributeValues){
		this.label = attributeLabel;
		Collections.replaceAll(attributeValues, null, NULL_VALUE);
		this.values = attributeValues;
	}

	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public ArrayList<String> getDistinctValues(){
		return this.values.stream().distinct()
								   .filter(v -> !v.equals("#NULL#"))  //ignore null value
								   .collect(Collectors.toCollection(ArrayList::new));
	}
	
	/*
	 * filters and orders the values list based on the indexes list provided 
	 */
	public ArrayList<String> getRLValues(ArrayList<Integer> indexes){
		ArrayList<String> newValues = new ArrayList<>();
		
		for(Integer index : indexes)
			newValues.add(this.values.get(index));
		
		return newValues;
	}

	public ArrayList<String> getValues() {
		return this.values;
	}

	public void setValues(ArrayList<String> values) {
		this.values = values;
	}

	@Override
	public String toString() {
		return "Attribute [label=" + label + ", values=" + values + "]";
	}

}
