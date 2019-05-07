
public class Match implements Comparable{
	public String gameId;
	public String startTime;
	public String tier;
	
	Match(String id,String time, String tier){
		gameId = id;
		startTime = time;
		this.tier = tier;
	}
	
	public String toString(){
		return "Game: " + gameId + " at " + startTime + " in " + tier;
	}
	
	public boolean equals(Match m){
		return gameId.equals(m.gameId);
	}
	
	public String toPrint(){
		return gameId + "\t" + startTime + "\t" + tier;
	}
	
	public int compareTo(Object o) {
		Match m = (Match) o;
		return gameId.compareTo(m.gameId);
	}
}
