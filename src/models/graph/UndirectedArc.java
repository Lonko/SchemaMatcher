package models.graph;

public class UndirectedArc {
	private Node node1;
	private Node node2;
	private double weigth;
	
	public UndirectedArc(Node n1, Node n2, double mutualInformation){
		this.node1 = n1;
		this.node2 = n2;
		this.weigth = mutualInformation;
	}

	public Node getNode1() {
		return this.node1;
	}

	public void setNode1(Node node1) {
		this.node1 = node1;
	}

	public Node getNode2() {
		return this.node2;
	}

	public void setNode2(Node node2) {
		this.node2 = node2;
	}

	public double getWeigth() {
		return this.weigth;
	}

	public void setWeigth(double weigth) {
		this.weigth = weigth;
	}

	@Override
	public String toString() {
		return "UndirectedArc [node1=" + node1 + ", node2=" + node2
				+ ", weigth=" + weigth + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		Node n1, n2;
		if(this.node1.getLabel().compareTo(this.node2.getLabel()) < 0){
			n1 = this.node1;
			n2 = this.node2;
		} else {
			n2 = this.node1;
			n1 = this.node2;
		}
		result = prime * result + ((n1 == null) ? 0 : n1.hashCode());
		result = prime * result + ((n2 == null) ? 0 : n2.hashCode());
		long temp;
		temp = Double.doubleToLongBits(this.weigth);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	//two UndirectedArcs are considered equal even if the nodes are inverted
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UndirectedArc other = (UndirectedArc) obj;
		if(this.node1 == null || this.node2 == null || other.node1 == null || other.node2 == null)
			return false; //arcs shouldn't have null nodes anyway
		
		if(!(  (this.node1.equals(other.node1) && this.node2.equals(other.node2)) ||
		       (this.node2.equals(other.node1) && this.node1.equals(other.node2))   ))
			return false;
		
		if (Double.doubleToLongBits(this.weigth) != Double
				.doubleToLongBits(other.weigth))
			return false;
		return true;
	}
}
