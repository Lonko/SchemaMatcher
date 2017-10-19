package tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.io.BufferedReader;
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
	
	/* Reads sources from dataset, filtering by website and categories
	 */
	public ArrayList<Source> readByCatAndSite(ArrayList<String> websites, ArrayList<String> categories){
		
		return readByCatAndSite(websites, categories, null);
		
	}
	
	/* Reads sources from dataset, filtering by website and categories.
	 * It's also possible to filter the attributes to consider (if validAttributes is null then all attributes
	 * will be included)
	 * !! Key in the validAttributes map must be in the form "website_#_category" !!
	 */
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
				else if(validAttributes.containsKey(sourceKey))
					sources.add(readSource(s, dir.getName(), category, validAttributes.get(sourceKey)));
			}
		}
		
		return sources;
	}
	
	public Source readSource(File spec, String website, String category, List<String> validAttributes){
		
		ArrayList<Attribute> attributes = new ArrayList<>();
		ArrayList<String> urls = new ArrayList<>();
		Map<String, ArrayList<String>> attributesValues = new HashMap<>();
		JSONParser parser = new JSONParser();
		try{
			JSONArray sourceJSON = (JSONArray) parser.parse(new FileReader(spec.getAbsolutePath()));
			if(validAttributes == null)
				validAttributes = getAllLabels(sourceJSON);  
			//products
			for(Object obj : sourceJSON){
				JSONObject product = (JSONObject) obj;
				JSONObject attributesJson = (JSONObject) product.get("spec");
				urls.add(normalizeUrl((String) product.get("url")));
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
		
		return new Source(website, category, urls, attributes);
	}
	
	public Source getRLSource(Source s, String pathRL){
		String website = s.getWebsite();
		String category = s.getCategory();
		ArrayList<String> urls = readRLTsv(s.getWebsite(), pathRL);
		ArrayList<Attribute> attributes = new ArrayList<>();
		
		//check the current row position of the records the urls refer to
		ArrayList<Integer> indexes = new ArrayList<>();
		ArrayList<String> oldUrls = s.getUrls();
		for(String url : urls)
			indexes.add(oldUrls.indexOf(url));
		
		//get a filtered and ordered version of the value list for each attribute
		for(Attribute a : s.getAttributes())
			attributes.add(new Attribute(a.getLabel(), a.getRLValues(indexes)));

		return new Source(website, category, urls, attributes);
	}
	
	private ArrayList<String> readRLTsv(String website, String path){
		BufferedReader br = null;
        String line = "";
    	String[] splitLine;
        ArrayList<String> urls = new ArrayList<>();
        int index = -1;
        
        try {

            br = new BufferedReader(new FileReader(path));
            if((line = br.readLine()) != null){
            	splitLine = line.split("\t");
            	if(website.equals(line.split("\t")[0]))
            		index = 0;
            	else if(website.equals(line.split("\t")[1]))
            		index = 1;
            	else {
            		br.close();
            		throw new IllegalArgumentException(
            			"The record linkage file (located at: " + path +
            			") is not relative to the website (" + website + ")");
            	}
            }
            
            
            while ((line = br.readLine()) != null) {
            	splitLine = line.split("\t");
            	urls.add(splitLine[index]);
            }

        }catch(Exception e){
        	e.printStackTrace();
        }
        
        return urls;
	}
	
	private String normalizeUrl(String url){
		String normUrl = url;
		if(url.startsWith("http://"))
			normUrl = url.substring(7);
		else if(url.startsWith("https://"))
			normUrl = url.substring(8);
		if(normUrl.startsWith("www."))
			normUrl = normUrl.substring(4);
		
		return normUrl;
	}
	
	private ArrayList<String> getAllLabels(JSONArray allProducts){
		Set<String> labels = new TreeSet<>();
		
		//products
		for(Object obj : allProducts){
			JSONObject product = (JSONObject) obj;
			JSONObject attributesJson = (JSONObject) product.get("spec");
		    Set<String> keys = attributesJson.keySet();
			//get all new labels
			for(String label : keys){
				labels.add(label.toLowerCase());
			}
		}
		
		return new ArrayList<String>(labels);
	}
	
	//case insensitive get on JSONObject
	private String getIgnoreCase(JSONObject json, String key) {
	    Set<String> keys = json.keySet();
	    for(String k : keys){
	    	if(k.equalsIgnoreCase(key)){
	    		return (String) json.get(k);
	    	}
	    }

	    return null;
	}

	public String getDatasetPath() {
		return datasetPath;
	}

	public void setDatasetPath(String datasetPath) {
		this.datasetPath = datasetPath;
	}
	 
}
