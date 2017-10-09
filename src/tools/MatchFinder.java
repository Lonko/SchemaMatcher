package tools;

import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

import math.utils.MatrixController;
import models.graph.DependencyGraph;
import models.graph.Node;
import models.match.Match;
import models.source.Source;

public class MatchFinder {
	
	private GraphCreator gc;
	private MatrixController mc;
	private static final double ALPHA = 1.0;
	private static final int N_SWITCH = 100;
	
	public MatchFinder(){
		gc = new GraphCreator();
		mc = new MatrixController();
	}
	
	public Match getMatch(Source s1, Source s2){
		double[][] m1, m2;
		int[] matchIndex;
		String category = s1.getCategory();
		
		DependencyGraph g1 = this.gc.source2Graph(s1);
		DependencyGraph g2 = this.gc.source2Graph(s2);
		m1 = g1.getAdjacencyMatrix();
		m2 = g2.getAdjacencyMatrix();
		matchIndex = this.mc.getMatchIndex(m1, m2);
		
		matchIndex = twoOptSwitch(m1, m2, matchIndex);
		
		return createMatch(g1, g2, s1.getWebsite(), s2.getWebsite(), matchIndex, category);
	}
	
	private Match createMatch(DependencyGraph g1, DependencyGraph g2, String w1, String w2,
								int[] matchIndex, String category){
		
		ArrayList<Node> nodesS1 = g1.getNodes();
		ArrayList<Node> nodesS2 = g2.getNodes();
		Map<String, String> matchedLabels = new HashMap<>();
		 
		for(int i1 = 0; i1 < nodesS1.size(); i1++){
			int i2 = matchIndex[i1];
			matchedLabels.put(w1+"//"+nodesS1.get(i1).getLabel(), w2+"//"+nodesS2.get(i2).getLabel());
		}
		
		return new Match(category, matchedLabels);
	}
	
	/* Makes use of an hill-climbing approach to improve the initial match
	 * by switching couples of attributes matched and accepting new matches
	 * that maximize the normal distance 
	 */
	private int[] twoOptSwitch(double[][] m1, double[][] m2, int[] currentMatch){
		int[] candidateMatch = Arrays.copyOf(currentMatch, currentMatch.length);
		double maxNDistance = getNormalDistance(m1, m2, currentMatch);
		int l = currentMatch.length, switches = 0;
		boolean repeat = false;

		do{
			//repeat nested for loop if a switch has been accepted
			hill_climbing:
				for(int i = 0; i < l-1; i++){
					repeat = false;
					for(int j = i+1; j < l; j++){
						candidateMatch[j] = currentMatch[i];
						candidateMatch[i] = currentMatch[j];
						double currentNDistance = getNormalDistance(m1,m2,candidateMatch);
						if(currentNDistance > maxNDistance){
							currentMatch = Arrays.copyOf(candidateMatch, candidateMatch.length);;
							maxNDistance = currentNDistance;
							switches++;
							repeat = true;
							//restart
							break hill_climbing;
						} else
							candidateMatch = Arrays.copyOf(currentMatch, currentMatch.length);
						if(switches == N_SWITCH)
							return currentMatch;
					}
				}
		}while(repeat);
		
		return currentMatch;
	}
	
	//m1, m2 must have equal cardinality
	private double getNormalDistance(double[][] m1, double[][] m2, int[] matchIndex){
		double normalDistance = 0.0;
		int length = m1.length;
		
		for(int i1 = 0; i1 < length; i1++)
			for(int j1 = 0; j1 < length; j1++){
				int i2 = matchIndex[i1];
				int j2 = matchIndex[j1];
				double absDiff = Math.abs(m1[i1][j1] - m2[i2][j2]);
				double sum = m1[i1][j1] + m2[i2][j2];
				normalDistance += 1-(ALPHA*(absDiff/sum));
			}
		
		return normalDistance;
	}
	
	
	/* Unlikely to be used, as Euclidean Distance doesn't apply to all possible cardinalities of matches.
	 * It's preferred to use the Normal Distance for executions on real datasets.
	 */
//	private double getEuclideanDistance(double[][] m1, double[][] m2, int[] matchIndex){
//		double euclideanDistance = 0.0;
//		int length = m1.length;
//		
//		for(int i1 = 0; i1 < length; i1++)
//			for(int j1 = 0; j1 < length; j1++){
//				int i2 = matchIndex[i1];
//				int j2 = matchIndex[j1];
//				double diff = m1[i1][j1] - m2[i2][j2];
//				euclideanDistance += diff*diff;
//			}
//		
//		return Math.sqrt(euclideanDistance);
//	}
}
