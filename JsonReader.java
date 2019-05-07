import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonReader {

	static long time;
	static long endTime;
	static ArrayList<Summoner> list;
	static ArrayList<Match> mList;
	static int counter = 0;
	static String newNames = "";
	static TreeSet<Summoner> namesSet;
	static long lastTime = 0;


	static TreeSet<Match> matchSet;

	static String code = "InsertLOLCode";

	private static JSONObject contactAPI(String what) throws IOException, JSONException{

		while(lastTime+2000 > System.currentTimeMillis());

		String URL = "https://na1.api.riotgames.com/lol/"+what+"api_key="+code;
		System.out.println(URL);
		JSONObject json = readJsonFromUrl(URL);
		lastTime = System.currentTimeMillis();
		return json;
	}

	private static JSONArray contactAPIForArray(String what) throws IOException, JSONException{

		while(lastTime+2000 > System.currentTimeMillis());

		String URL = "https://na1.api.riotgames.com/lol/"+what+"?api_key="+code;
		System.out.println(URL);
		JSONArray json = readArrayFromUrl(URL);
		lastTime = System.currentTimeMillis();
		return json;
	}

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}
	public static JSONArray readArrayFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONArray json = new JSONArray(jsonText);
			return json;
		} finally {
			is.close();
		}
	}

	//arrayList of names
	public static void getNames(){
		list = new ArrayList<Summoner>();
		try {
			Scanner scan = new Scanner(new File("names.txt"));
			time = scan.nextLong();
			endTime = scan.nextLong();
			//System.out.println(new Date(time));
			while(scan.hasNext()){
				Summoner summ = new Summoner(scan.next(), scan.next(), scan.next(), scan.next());
				System.out.println(summ);
				list.add(summ);
			}

			scan.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	//adds matches to an already created matchSet - getMatchesSet,getNames, updateMatches
	public static void getMatches(){

		System.out.println("Start time: " + System.currentTimeMillis());

		getMatchesSet();
		getNames();



		//420 queue id?
		for(Summoner summ: list){
			try {
				JSONObject json = contactAPI("match/v3/matchlists/by-account/"+summ.accId+"?queue=420&endTime="+endTime+"&beginTime="+time+"&");
				//System.out.println(json.toString());
				JSONArray matches = json.getJSONArray("matches");
				for(int i = 0; i < matches.length(); i++){
					matchSet.add(new Match(String.valueOf(matches.getJSONObject(i).get("gameId")), String.valueOf(matches.getJSONObject(i).get("timestamp")), summ.tier));
				}
				
				if(json.get("totalGames").equals("0")){
					//list.remove(summ);
					updateMatches();
					System.out.println("early update");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("ERROR: " + e.getMessage());
				System.out.println("\t Error at " + summ.accId);
				if(e.getMessage().contains("Server returned HTTP response code: 4")){
					if(e.getMessage().contains("Server returned HTTP response code: 404")){
						//list.remove(summ);
						
						continue;
					} else{
						System.out.println("boop");
						break;
					}
				}
				updateMatches();
				System.out.println("early update");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		updateMatches();
	}

	//uses file to create a matchSet
	public static void getMatchesSet(){
		matchSet = new TreeSet<Match>();
		try {
			Scanner scan = new Scanner(new File("matches.txt"));
			//System.out.println(new Date(time));
			while(scan.hasNext()){
				Match mat = new Match(scan.next(), scan.next(), scan.next());
				//System.out.println(mat);
				matchSet.add(mat);
			}

			scan.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		}	
	}

	// uses match file to create an arraylist
	public static void matchesLook(){
		try {
			Scanner scan = new Scanner(new File("matches.txt"));
			mList = new ArrayList<Match>();

			while(scan.hasNext()){
				Match mat = new Match(scan.next(), scan.next(), scan.next());
				mList.add(mat);
			}
			scan.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void updateNames(){
		try {
			PrintWriter writer = new PrintWriter(new File("names.txt"));
			//System.out.println(new Date(time));
			Iterator<Summoner> it = namesSet.iterator();
			writer.println(time + "\t");
			writer.println(endTime);

			while(it.hasNext()){
				Summoner s = it.next();
				writer.println(s.toPrint());
			}

			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	//creates Set from file
	static void getNamesSet(){
		namesSet = new TreeSet<Summoner>();
		try {
			Scanner scan = new Scanner(new File("names.txt"));
			time = scan.nextLong();
			endTime = scan.nextLong();
			//System.out.println(new Date(time));
			while(scan.hasNext()){
				Summoner summ = new Summoner(scan.next(), scan.next(), scan.next(), scan.next());
				//System.out.println(summ);
				namesSet.add(summ);
			}

			scan.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		}	
	}

	//Uses set to update matches.txt
	public static void updateMatches(){
		try {
			PrintWriter writer = new PrintWriter(new File("matches.txt"));
			//System.out.println(new Date(time));
			Iterator<Match> it = matchSet.iterator();

			while(it.hasNext()){
				Match mat = it.next();
				writer.println(mat.toPrint());
			}

			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	//uses Arraylist to get details for excel - matchLook, getNamesSet, updateNames
	public static void getDetailMatches(){

		matchesLook();
		getNamesSet(); //adds new summoners to

		try {
			PrintWriter pw = new PrintWriter(new File("detail.csv"));

			int c = 0;
			//420 queue id?
			for(Match mat: mList){
				c++;
				JSONObject json = null;
				try {
					json = contactAPI("match/v3/matches/"+ mat.gameId+"?");
					//System.out.println(json.toString());
					if(!json.get("platformId").equals("NA1")){
						System.out.println("Not in NA" + mat.gameId + " " + json.get("platformId"));
						continue;		
					}
					//JSONArray participantsIdent = json.getJSONArray("participantIdentities");
					//System.out.println(participants);
					//for(int i = 0; i < participantsIdent.length(); i++){
						//pw.print(matches.getJSONObject(i).get("gameId") + ",");
						//JSONObject person = participantsIdent.getJSONObject(i).getJSONObject("player");
						//System.out.println(person);
						//namesSet.add(new Summoner("XXX",person.get("accountId").toString(),"?","?"));
						//pw.print();
					//}

					int countKills1 = 0, countDeaths1 = 0, countWards1 = 0;
					int countKills2 = 0, countDeaths2 = 0, countWards2 = 0;
					JSONArray participants = json.getJSONArray("participants");

					boolean win = participants.getJSONObject(0).getJSONObject("stats").getBoolean("win");
					//System.out.println(participants);
					for(int i = 0; i < participants.length(); i++){
						JSONObject person = participants.getJSONObject(i).getJSONObject("stats");

						if(i <= 4){
							countKills1 += person.getInt("kills");
							countDeaths1 += person.getInt("deaths");
							countWards1 += person.getInt("wardsPlaced");
						} else{
							countKills2 += person.getInt("kills");
							countDeaths2 += person.getInt("deaths");
							countWards2 += person.getInt("wardsPlaced");
						}

					}

					pw.print("Game " + mat.gameId + " ");
					pw.print("Win? " + win + " ");

					JSONArray teams = json.getJSONArray("teams");
					//System.out.println(teams);
					for(int i = 0; i < teams.length(); i++){
						pw.print("Team" + i + " ");
						if(i == 0){
							pw.print(countKills1 + " " + countDeaths1 + " " + countWards1 + " ");
						}else if(i == 1){
							pw.print(countKills2 + " " + countDeaths2 + " " + countWards2 + " ");
						}
						JSONObject team = teams.getJSONObject(i);
						pw.print("Towers " + team.get("towerKills") + " ");
						pw.print("FirstBlood " + team.get("firstBlood") + " ");

					}

					pw.println();
					//System.out.println();
				} catch (IOException e) {
					System.out.println("ERROR: " + e.getMessage());
					System.out.println("\t Error at " + mat.gameId);
					if(e.getMessage().contains("4")){
						System.exit(-1);
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//break;
				System.out.println(c);
			}

			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println();
		System.out.println();
		updateNames();
	}

	public static void updateNamestxtInfo(){
		getNamesSet();

		Iterator<Summoner> it = namesSet.iterator();
		boolean good = true;

		while(it.hasNext()){
			//System.out.println("boop");
			Summoner s = it.next();
			if(s.id.equals("XXX")){
				JSONObject json;
				try {
					json = contactAPI("summoner/v3/summoners/by-account/"+s.accId+"?");

					s.id = String.valueOf(json.get("id"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("ERROR: " + e.getMessage());
					System.out.println("\t Error at " + s.accId);
					if(e.getMessage().contains("Server returned HTTP response code: 4")){
						good = false;
						break;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		System.out.println("updateing");
		updateNames();

		it = namesSet.iterator();
		while(it.hasNext()){
			Summoner s = it.next();
			if(s.tier.equals("?")){
				JSONArray json;
				try {
					if(good == false) break;
					if(s.id.equals("XXX")) continue;
					json = contactAPIForArray("league/v3/positions/by-summoner/"+s.id);
					JSONObject info = null;
					for(int i = 0; i < json.length(); i++){
						info = json.getJSONObject(i);
						if(info.get("queueType").equals("RANKED_SOLO_5x5")){
							break;
						} 
						info = null;
					}
					if(info==null){
						System.out.println("INFO NULL ERROR - " + s.id);
						continue;
					}
					s.tier = (String) info.get("tier");
					s.rank = (String) info.get("rank");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("ERROR: " + e.getMessage());
					System.out.println("\t Error at " + s.id);
					if(e.getMessage().contains("Server returned HTTP response code: 4")){
						good = false;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		System.out.println("updateing");
		updateNames();
	}

	public static void main(String[] args) throws IOException, JSONException {
		getMatches(); //uses summ array to get and update Matches using matchSet


		//getDetailMatches(); // gets details on Matches - adds Summs to Set and update

		//updateNamestxtInfo(); //updates the XXX and ?s after update from detail Matches


		//JSONObject json = contactAPI("summoner/v3/summoners/by-name/AGreatPretender"+"?");
		//System.out.println(json.toString());
		//System.out.println(json.get("name"));

	}
}