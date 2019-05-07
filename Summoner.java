
public class Summoner implements Comparable {
	public String id;
	public String accId;
	public String tier;
	public String rank;
	
	Summoner(String id, String ai, String tier, String rank){
		this.id = id;
		accId = ai;
		this.tier = tier;
		this.rank = rank;
	}
	
	public String toString(){
		return "Summoner Id: " + id + " Account: " + accId + " in Tier " + tier + " " + rank;
	}
	
	public String toPrint(){
		return id + "\t" + accId + "\t" + tier + "\t" + rank;
	}
	
	public boolean equals(Summoner s){
		return s.id.equals(id);
	}

	public int compareTo(Object o) {
		Summoner s = (Summoner) o;
		return accId.compareTo(s.accId);
	}

}
