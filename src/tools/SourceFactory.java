package tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.File;
import java.io.FileReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import models.source.Attribute;
import models.source.Source;

public class SourceFactory {
	
	private String datasetPath;
	private static final String KEY_SEPARATOR = "_#_";

	public SourceFactory(String datasetPath){
		this.datasetPath = datasetPath;
	}
	
	public ArrayList<Source> readByCatAndSite(ArrayList<String> websites, ArrayList<String> categories,
							Map<String, List<String>> validAttributes){
		
		ArrayList<Source> sources = new ArrayList<>();
		
		File[] directories = new File(this.datasetPath).listFiles(File::isDirectory);
		for(File dir : directories){
			if(!websites.contains(dir.getName()))
				continue;
			File[] specs = dir.listFiles();
			for(File s : specs){
				String category = s.getName().split("_")[0];
				if(!categories.contains(category))
					continue;
				String sourceKey = dir.getName()+KEY_SEPARATOR+category;
				if(validAttributes == null)
					sources.add(readSource(s, dir.getName(), category, null));
				if(validAttributes.containsKey(sourceKey))
					sources.add(readSource(s, dir.getName(), category, validAttributes.get(sourceKey)));
			}
		}
		
		return sources;
	}
	
	public Source readSource(File spec, String website, String category, 
						List<String> validAttributes){
		
		ArrayList<Attribute> attributes = new ArrayList<>();
		Map<String, ArrayList<String>> attributesValues = new HashMap<>();
		JSONParser parser = new JSONParser();
		try{
			JSONArray sourceJSON = (JSONArray) parser.parse(new FileReader(spec.getAbsolutePath()));
			if(validAttributes == null)
				validAttributes = getAllLabels(sourceJSON); //TO DO 
			//products
			for(Object obj : sourceJSON){
				JSONObject product = (JSONObject) obj;
				JSONObject attributesJson = (JSONObject) product.get("spec");
				//get value for all valid attributes
				for(String attrLabel : validAttributes){
					String value = getIgnoreCase(attributesJson, attrLabel);
					ArrayList<String> values = attributesValues.getOrDefault(attrLabel, new ArrayList<>());
					values.add(value);
					attributesValues.put(attrLabel, values);
				}
			}
			//create all Attribute objects
			for(Map.Entry<String, ArrayList<String>> valueList : attributesValues.entrySet()){
				Attribute a = new Attribute(valueList.getKey(), valueList.getValue());
				attributes.add(a);
			}			
		} catch(Exception e){
			e.printStackTrace();
        }
		
		return new Source(website, category, attributes);
	}
	
	private String getIgnoreCase(JSONObject json, String key) {

	    Set<String> keys = json.keySet();
	    for(String k : keys){
	    	if(k.equalsIgnoreCase(key)){
	    		return (String) json.get(k);
	    	}
	    }

	    return null;

	}
	
	private ArrayList<String> getAllLabels(JSONArray allProducts){
		//TO DO (remember lowercase!)
		return null;
	}

	public String getDatasetPath() {
		return datasetPath;
	}

	public void setDatasetPath(String datasetPath) {
		this.datasetPath = datasetPath;
	}
	 
}
