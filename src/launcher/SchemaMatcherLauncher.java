package launcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import models.graph.DependencyGraph;
import models.graph.Node;
import models.match.Match;
import models.source.Attribute;
import models.source.Source;
import tools.GraphCreator;
import tools.MatchFinder;
import tools.SourceFactory;

public class SchemaMatcherLauncher {
	public static void main(String[] args) {
		String datasetPath = args[0];
		String[] websites = {"www.shopmania.in", "www.shopping.com"};
		String[] categories = {"camera"};
		String[] aList1 = {"brand:", "optical zoom", "sensor type", "video resolution", "viewfinder type",
				"digital zoom", "screen type", "upc:"};
		String[] aList2 = {"brand", "optical zoom", "sensor type", "max video resolution", "viewfinder type",
				"digital zoom", "display type", "upc"};
		
		SourceFactory sf = new SourceFactory(datasetPath);
		ArrayList<String> wList = new ArrayList<>(Arrays.asList(websites));
		ArrayList<String> cList = new ArrayList<>(Arrays.asList(categories));
		ArrayList<String> vaList1 = new ArrayList<>(Arrays.asList(aList1));
		ArrayList<String> vaList2 = new ArrayList<>(Arrays.asList(aList2));
		Map<String, List<String>> validAttributes = new HashMap<>();
		validAttributes.put(websites[0]+"_#_"+categories[0], vaList1);
		validAttributes.put(websites[1]+"_#_"+categories[0], vaList2);
		
		
		double start = System.currentTimeMillis();
		ArrayList<Source> sources = sf.readByCatAndSite(wList, cList, validAttributes);
		
//		for(Attribute a : sources.get(0).getAttributes())
//			System.out.println(sources.get(0).getWebsite()+" - "+a.getLabel()+" "+a.getValues().stream()
//				    .filter(s -> !s.equals("#NULL#")).collect(Collectors.toList()).size());
//		for(Attribute a : sources.get(1).getAttributes())
//			System.out.println(sources.get(1).getWebsite()+" - "+a.getLabel()+" "+a.getValues().stream()
//				    .filter(s -> !s.equals("#NULL#")).collect(Collectors.toList()).size());
//			if(a.getLabel().equals("sensor type"))
//			System.out.println(a.getValues());
		
		MatchFinder mf = new MatchFinder();
		Match match = mf.getMatch(sources.get(1), sources.get(0));
		
		System.out.println(match.toString());
		System.out.println((System.currentTimeMillis()-start)/1000+" secondi");
		
		
//		ArrayList<String> l1 = new ArrayList<>();
//		ArrayList<String> l2 = new ArrayList<>();
//		ArrayList<String> l3 = new ArrayList<>();
//		ArrayList<String> l4 = new ArrayList<>();
//		ArrayList<String> l5 = new ArrayList<>();
//		ArrayList<String> l6 = new ArrayList<>();
//		ArrayList<String> l7 = new ArrayList<>();
//		ArrayList<String> l8 = new ArrayList<>();
//
//		l1.add("a1");l1.add("a3");l1.add("a1");l1.add("a4");
//		l2.add("b2");l2.add("b4");l2.add("b1");l2.add("b3");
//		l3.add("c1");l3.add("c2");l3.add("c1");l3.add("c2");
//		l4.add("d1");l4.add("d2");l4.add("d2");l4.add("d3");
//		l5.add("w2");l5.add("w4");l5.add("w3");l5.add("w1");
//		l6.add("x1");l6.add("x2");l6.add("x3");l6.add("x2");
//		l7.add("y1");l7.add("y3");l7.add("y3");l7.add("y1");
//		l8.add("z2");l8.add("z3");l8.add("z1");l8.add("z2");
//
//		Attribute a1 = new Attribute("A", l1);
//		Attribute a2 = new Attribute("B", l2);
//		Attribute a3 = new Attribute("C", l3);
//		Attribute a4 = new Attribute("D", l4);
//		Attribute a5 = new Attribute("W", l5);
//		Attribute a6 = new Attribute("X", l6);
//		Attribute a7 = new Attribute("Y", l7);
//		Attribute a8 = new Attribute("Z", l8);
//
//		ArrayList<Attribute> al1 = new ArrayList<>();
//		ArrayList<Attribute> al2 = new ArrayList<>();
//		al1.add(a1);al1.add(a2);al1.add(a3);al1.add(a4);
//		al2.add(a5);al2.add(a6);al2.add(a7);al2.add(a8);
//
//		Source s1 = new Source("web1", "c", al1);
//		Source s2 = new Source("web2", "c", al2);
//
//		GraphCreator gc = new GraphCreator();
//		DependencyGraph g1 = gc.source2Graph(s1);
//		DependencyGraph g2 = gc.source2Graph(s2);
//		
//		int[] in = {3,0,2,1};
//		System.out.println(Arrays.deepToString(g1.getAdjacencyMatrix()));
//		System.out.println(Arrays.deepToString(g2.getAdjacencyMatrix()));
//		System.out.println(new MatchFinder().getNormalDistance(g1.getAdjacencyMatrix(), g2.getAdjacencyMatrix(), in));
//		System.out.println(g1.getArcs().toString());
//		System.out.println(g2.getArcs().toString());
		
		
//		System.out.println(new MatchFinder().getMatch(s1, s2).toString());
	}
}
