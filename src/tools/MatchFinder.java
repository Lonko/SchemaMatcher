package tools;

import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import math.utils.MatrixController;
import models.graph.DependencyGraph;
import models.graph.Node;
import models.match.Match;
import models.source.Source;

public class MatchFinder {
	
	private GraphCreator gc;
	private MatrixController mc;
	private static final double ALPHA = 1.0;
	private static final int N_SWITCH = 1000;
	
	public MatchFinder(){
		gc = new GraphCreator();
		mc = new MatrixController();
	}
	
	public Match getPartialMatch(Source s1, Source s2){
		int cardinalityS1 = s1.getAttributes().size();
		int cardinalityS2 = s2.getAttributes().size();
		int minCardinality = cardinalityS1 <= cardinalityS2 ? cardinalityS1 : cardinalityS2;
		//get all subsets of s1 and s2 with cardinality between 2 and Min(|s1|, |s2|)
		HashMap<Integer, ArrayList<Source>> ps1 = s1.getPowerSet(minCardinality);
		HashMap<Integer, ArrayList<Source>> ps2 = s2.getPowerSet(minCardinality);
		Match bestGlobalMatch = new Match("", Double.MIN_VALUE, new HashMap<String, String>());
		
		//try all matches between subsets of s1 and s2, keeping the one with the highest Normal Distance
		for(int i = minCardinality; i > 1; i--){
			double maxPossibleDistance = (i*i)+i;
			//check if it's impossible to get a better match
			if(maxPossibleDistance <= bestGlobalMatch.getDistance())
				break;
			Optional<Match> bestLocalMatch = ps1.get(i).stream()
					//for each subset of cardinality i in ps1
					.map(subset1 -> ps2.get(subset1.getAttributes().size()).parallelStream()
									//find best matching subset of cardinality i in ps2
									.map(subset2 -> getMatch(subset1, subset2))
									.reduce((Match a, Match b) -> a.getDistance() > b.getDistance() ? a : b))
					//map Optional<Match> to Match
					.map(m -> m.get())
					//get the best match among all the ones with cardinality "i"
					.reduce((Match a, Match b) -> a.getDistance() > b.getDistance() ? a : b);
			
			//check if the best local match is better than the current best global match
			if(bestLocalMatch.get().getDistance() > bestGlobalMatch.getDistance())
				bestGlobalMatch = bestLocalMatch.get();
			
			System.out.println("Finita cardinalità " + i);
		}
		
		return bestGlobalMatch;
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
		double finalDistance = getNormalDistance(m1, m2, matchIndex);
		
		return createMatch(g1, g2, s1.getWebsite(), s2.getWebsite(), matchIndex, category, finalDistance);
	}
	
	private Match createMatch(DependencyGraph g1, DependencyGraph g2, String w1, String w2,
								int[] matchIndex, String category, double nDistance){
		
		ArrayList<Node> nodesS1 = g1.getNodes();
		ArrayList<Node> nodesS2 = g2.getNodes();
		Map<String, String> matchedLabels = new HashMap<>();
		 
		for(int i1 = 0; i1 < nodesS1.size(); i1++){
			int i2 = matchIndex[i1];
			matchedLabels.put(w1+"//"+nodesS1.get(i1).getLabel(), w2+"//"+nodesS2.get(i2).getLabel());
		}
		
		return new Match(category, nDistance, matchedLabels);
	}
	
	/* Makes use of an hill-climbing approach to improve the initial match
	 * by switching couples of attributes matched and accepting new matches
	 * that maximize the normal distance 
	 */
	private int[] twoOptSwitch(double[][] m1, double[][] m2, int[] currentMatch){
		int[] candidateMatch = Arrays.copyOf(currentMatch, currentMatch.length);
		double maxNDistance = getNormalDistance(m1, m2, currentMatch);
		int l = currentMatch.length, switches = 0;
		boolean repeat;

		do{
			//repeat nested for loop if a switch has been accepted
			repeat = false;
			hill_climbing:
				for(int i = 0; i < l; i++){
					for(int j = 0; j < l; j++){
						if(i == j)
							continue;
						candidateMatch[j] = currentMatch[i];
						candidateMatch[i] = currentMatch[j];
						switches++;
						double currentNDistance = getNormalDistance(m1,m2,candidateMatch);
//						if(Arrays.toString(candidateMatch).equals("[1, 2, 0, 4, 6, 5, 7, 3]"))
//							System.out.println("trovato: "+currentNDistance);
						if(currentNDistance > maxNDistance){
							System.out.println("switch");
							currentMatch = Arrays.copyOf(candidateMatch, candidateMatch.length);;
							maxNDistance = currentNDistance;
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
		
		System.out.println(maxNDistance);
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
}
