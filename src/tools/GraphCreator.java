package tools;

import java.util.ArrayList;

import models.graph.DependencyGraph;
import models.graph.Node;
import models.graph.UndirectedArc;
import models.source.Attribute;
import models.source.Source;

public class GraphCreator {
	
	public GraphCreator(){	
	}
	
	public DependencyGraph source2Graph(Source source){
		ArrayList<Node> nodes = new ArrayList<>();
		ArrayList<UndirectedArc> arcs = new ArrayList<>();
		
		for(Attribute a : source.getAttributes()){
			String label = a.getLabel();
			Node n = new Node(label, source.getMutualInformation(label, label));
			nodes.add(n);
		}
		
		for(Node firstNode : nodes)
			for(Node secondNode : nodes){
				String label1 = firstNode.getLabel();
				String label2 = secondNode.getLabel();
				if(!label1.equals(label2)){
					UndirectedArc arc = new UndirectedArc(firstNode, secondNode, 
												source.getMutualInformation(label1, label2));
					if(!arcs.contains(arc))
						arcs.add(arc);
				}
			}
		
		return new DependencyGraph(nodes, arcs);
	}
	
}
