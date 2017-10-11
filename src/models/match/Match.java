package models.match;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

public class Match {
	
	private String category;
	private double distance;
	private Map<String, ArrayList<String>> matchedAttributes;

	public Match(String category, double nDistance, Map<String, String> labelsMatched){
		this.category = category;
		this.distance = nDistance;
		matchedAttributes = new HashMap<>();
		int cluster = 1;
		for(Map.Entry<String, String> labels : labelsMatched.entrySet()){
			ArrayList<String> lm = new ArrayList<>();
			lm.add(labels.getKey());
			lm.add(labels.getValue());
			matchedAttributes.put("c"+cluster, lm);
			cluster++;
		}
	}
	
	public JSONObject matchToJSON(){
		JSONObject json = new JSONObject();
		JSONObject attributeClusters = new JSONObject();
		
		for(Map.Entry<String, ArrayList<String>> e : this.matchedAttributes.entrySet())
			attributeClusters.put(e.getKey(), e.getValue());
		
		json.put("category", this.category);
		json.put("attribute_clusters", attributeClusters);
		
		return json;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
//	public void addMatchedAttribute(String label1, String label2){
//		this.matchedAttributes.get(label1);
//	}

	public Map<String, ArrayList<String>> getMatchedAttributes() {
		return matchedAttributes;
	}

	public void setMatchedAttributes(Map<String, ArrayList<String>> matchedAttributes) {
		this.matchedAttributes = matchedAttributes;
	}

	@Override
	public String toString() {
		return "Match [category=" + category + ", matchedAttributes="
				+ matchedAttributes + "]";
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

}
