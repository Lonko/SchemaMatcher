package models.graph;

import java.util.ArrayList;

public class DependencyGraph {
	private ArrayList<Node> nodes;
	private ArrayList<UndirectedArc> arcs;
	
	public DependencyGraph(ArrayList<Node> nodes, ArrayList<UndirectedArc> arcs){
		this.nodes = nodes;
		this.arcs = arcs;
	}
	
	public double[][] getAdjacencyMatrix(){
		int l = this.nodes.size();
		double[][] matrix = new double[l][l];
		
		for(int i = 0; i < l; i++)
			for(int j = 0; j < l; j++){
				matrix[i][j] = i != j ?
								getArc(this.nodes.get(i), this.nodes.get(j)).getWeigth() : 
								this.nodes.get(i).getWeight();
			}
		
		return matrix;
	}

	public ArrayList<Node> getNodes() {
		return this.nodes;
	}

	public void setNodes(ArrayList<Node> nodes) {
		this.nodes = nodes;
	}
	
	public UndirectedArc getArc(Node n1, Node n2){
		for(UndirectedArc a : this.arcs)
			if((a.getNode1().equals(n1) && a.getNode2().equals(n2)) ||
		       (a.getNode1().equals(n2) && a.getNode2().equals(n1)) )
				return a;
		return null;
	}

	public ArrayList<UndirectedArc> getArcs() {
		return this.arcs;
	}

	public void setArcs(ArrayList<UndirectedArc> arcs) {
		this.arcs = arcs;
	}

	@Override
	public String toString() {
		return "DependencyGraph [nodes=" + nodes + ", arcs=" + arcs + "]";
	}
}
