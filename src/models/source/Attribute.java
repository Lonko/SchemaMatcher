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
		return this.values.stream().distinct().collect(Collectors.toCollection(ArrayList::new));
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
