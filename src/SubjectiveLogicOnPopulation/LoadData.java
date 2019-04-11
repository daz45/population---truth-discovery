package SubjectiveLogicOnPopulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class LoadData {

	public static void main(String[] args) {

	}

	int city_count = 0;
	int user_count = 0;

	ArrayList<HashMap<Integer, Double>> city_user_pop;
	ArrayList<Double> city_completeness;
	ArrayList<Double> city_consistency;
	ArrayList<Double> city_discrimination;
	ArrayList<Double> city_avg_t;
	ArrayList<Double> city_max_t;
	ArrayList<Double> city_min_t;
	ArrayList<Double> city_exp_pop;
	ArrayList<Double> city_prior;
	ArrayList<Double> city_sd;
	ArrayList<HashMap<Integer, Double>> user_city_pop;
	ArrayList<Double> user_weight;
	HashMap<String, Integer> cityName_cityId;
	HashMap<Integer, String> cityId_cityName;
	ArrayList<HashMap<Double, Double>> city_pop_weight;
	ArrayList<HashMap<Double, Double>> city_pop_weight_theta;
	ArrayList<HashMap<Double, HashSet<Integer>>> city_pop_user;

	HashMap<String, Integer> userName_userId;
	HashMap<Integer, String> userId_userName;

	public HashMap<Integer, Double> validationScore;
	public HashMap<Integer, Double> testingScore;
	public HashMap<Integer, Double> GDScore;

	public LoadData(String input, String yearF) throws Exception {
		BasicClass basicClass = new BasicClass();
		city_count = basicClass.with_outlier_city_count;
		user_count = basicClass.with_outlier_user_count;
		if (input.equals("getPirorTF")) {
			city_count = basicClass.city_count;
			user_count = basicClass.user_count;
		}
		System.out.println("city_count\t" + city_count);
		System.out.println("user_count\t" + user_count);

		// init
		city_user_pop = new ArrayList<>();
		city_completeness = new ArrayList<>();
		city_consistency = new ArrayList<>();
		city_discrimination = new ArrayList<>();
		city_avg_t = new ArrayList<>();
		city_max_t = new ArrayList<>();
		city_min_t = new ArrayList<>();
		city_exp_pop = new ArrayList<>();
		city_pop_weight = new ArrayList<>();
		city_pop_user = new ArrayList<>();
		city_prior = new ArrayList<>();
		city_sd = new ArrayList<>();
		city_pop_weight_theta = new ArrayList<>();
		for (int i = 0; i <= city_count; i++) {
			city_user_pop.add(new HashMap<>());
			city_pop_weight.add(new HashMap<>());
			city_pop_user.add(new HashMap<>());
			city_completeness.add(0.0);
			city_consistency.add(0.0);
			city_discrimination.add(0.0);
			city_avg_t.add(0.0);
			city_max_t.add(0.0);
			city_min_t.add(0.0);
			city_exp_pop.add(0.0);
			city_pop_weight_theta.add(new HashMap<>());
			city_prior.add(0.0);
			city_sd.add(0.0);
			city_max_t.add(Double.MIN_VALUE);
			city_min_t.add(Double.MAX_VALUE);
		}
		userId_userName = new HashMap<>();
		user_city_pop = new ArrayList<>();
		for (int i = 0; i <= user_count; i++) {
			user_city_pop.add(new HashMap<>());
			userId_userName.put(i, "");
		}

		// load
		String line = "";
		cityName_cityId = new HashMap<>();
		userName_userId = new HashMap<>();
		cityId_cityName = new HashMap<>();
		int city_idx = 1;
		int user_idx = 1;
		String add = "data\\population\\population-new-new.txt";
		if (input.equals("getPirorTF")) {
			add = "data\\population\\population-new-new-new-TF.txt";
		}
		BufferedReader reader = new BufferedReader(new FileReader(new File(add)));
		while ((line = reader.readLine()) != null) {
			String[] tmp = line.split("\t");

			String city_name = tmp[0];
			// if (yearF.equals("NotyearFormat")) {
			// city_name = tmp[0].substring(0, tmp[0].indexOf("_"));
			// }

			if (!cityName_cityId.containsKey(city_name)) {
				cityName_cityId.put(city_name, city_idx);
				cityId_cityName.put(city_idx, city_name);
				city_idx++;
			}
			if (!userName_userId.containsKey(tmp[1])) {
				userName_userId.put(tmp[1], user_idx);
				userId_userName.put(user_idx, tmp[1]);
				user_idx++;
			}

			int city_id = cityName_cityId.get(city_name);
			int user_id = userName_userId.get(tmp[1]);
			double pop_t = Double.parseDouble(tmp[2]);

			if (input.equals("getPirorTF")) {
				double pop = Double.parseDouble(tmp[5]);
				double prior = Double.parseDouble(tmp[3]);
				double sd = Double.parseDouble(tmp[4]);
				city_prior.set(city_id, prior);
				city_sd.set(city_id, sd);
			}

			city_avg_t.set(city_id, city_avg_t.get(city_id) + pop_t);
			user_city_pop.get(user_id).put(city_id, pop_t);
			city_user_pop.get(city_id).put(user_id, pop_t);
			if (!city_pop_user.get(city_id).containsKey(pop_t)) {
				city_pop_user.get(city_id).put(pop_t, new HashSet<>());
			}
			city_pop_user.get(city_id).get(pop_t).add(user_id);

			if (city_max_t.get(city_id) < pop_t) {
				city_max_t.set(city_id, pop_t);
			}
			if (city_min_t.get(city_id) > pop_t) {
				city_min_t.set(city_id, pop_t);
			}
			city_pop_weight.get(city_id).put(pop_t, 0.0);
			city_pop_weight_theta.get(city_id).put(pop_t, 0.0);
		}
		reader.close();

		// avg
		for (int city_id = 1; city_id <= city_count; city_id++) {
			int count = city_user_pop.get(city_id).size();
			double completeness = 1;
			city_completeness.set(city_id, completeness);
			city_avg_t.set(city_id, city_avg_t.get(city_id) / count);

		}

		HashSet<String> removeLines = new HashSet<>();
//		removeLines.add("chattanooga, tennessee, 2006, 168293");
//		removeLines.add("chattanooga, tennessee, 2000, 155554");
//		removeLines.add("milwaukee, wisconsin, 2000, 596974");
//		removeLines.add("milwaukee, wisconsin, 2006, 602782");
//		removeLines.add("st. louis, missouri, 2005, 352572");
//		removeLines.add("st. louis, missouri, 2006, 353837");
		removeLines.add("st. joseph, missouri, 2000, 73990");
		removeLines.add("winston-salem, north carolina, 2000, 185776");
		removeLines.add("wilmington, delaware, 2000, 72664");
		removeLines.add("portland, oregon, 2000, 529121");
		removeLines.add("roswell, georgia, 2000, 79334");
		removeLines.add("minnetonka, minnesota, 2000, 51301");
		removeLines.add("lima, ohio, 2000, 40081");
		removeLines.add("vincennes, indiana, 2000, 18701");
		removeLines.add("douglasville, georgia, 2000, 20065");
		removeLines.add("nashua, new hampshire, 2000, 86605");
		removeLines.add("montague, michigan, 2000, 2407");
		removeLines.add("beverly, massachusetts, 2000, 39862");
		removeLines.add("albion, idaho, 2000, 262");
		removeLines.add("jessup, pennsylvania, 2000, 4718");
		removeLines.add("new berlin, wisconsin, 2000, 38220");
		removeLines.add("casper, wyoming, 2000, 49644");
		removeLines.add("kutztown, pennsylvania, 2000, 5067");
		removeLines.add("sweeny, texas, 2000, 3624");
		removeLines.add("john day, oregon, 2000, 1821");
		removeLines.add("winter park, colorado, 2000, 662");
		removeLines.add("fairfax, california, 2000, 7319");
		removeLines.add("rupert, idaho, 2000, 5645");
		removeLines.add("ettrick, wisconsin, 2000, 521");
		removeLines.add("heyburn, idaho, 2000, 2899");
		removeLines.add("minidoka, idaho, 2000, 129");
		removeLines.add("paul, idaho, 2000, 998");
		removeLines.add("bern, kansas, 2000, 204");
		removeLines.add("six mile, south carolina, 2000, 553");
		removeLines.add("poospatuck reservation, new york, 2000, 271");
		removeLines.add("ontario, california, 2000, 158007");
		removeLines.add("muskegon, michigan, 2000, 40105");

		GDScore = new HashMap<>();
		reader = new BufferedReader(new FileReader(new File("data\\population\\population-gd.txt")));// testing data validation data
		while ((line = reader.readLine()) != null) {
			if (removeLines.contains(line)) {
				continue;
			}
			String[] tmp = line.split(", ");
			String city = tmp[0] + ", " + tmp[1];
			if (yearF.equals("yearFormat")) {
				city = tmp[0] + ", " + tmp[1] + "_" + tmp[2];
			}
			if (!cityName_cityId.containsKey(city)) {
				System.out.println("-------------------" + line);
				continue;
			}

			int id = cityName_cityId.get(city);
			GDScore.put(id, Double.parseDouble(tmp[3]));
		}
		System.out.println(GDScore.size());
		reader.close();
	}

}
