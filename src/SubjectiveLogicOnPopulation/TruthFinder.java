package SubjectiveLogicOnPopulation;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

public class TruthFinder {

	public static void main(String[] args) throws Exception {
		TruthFinder ab = new TruthFinder();
		ab.load("getORGdata", "yearFormat");
		ab.run();
	}

	public void run() throws Exception {
		Eval eval = new Eval();
		boolean flag = false;// 0.3
		double[] e1 = { 0, 0.01, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 0.99 };// 0, 0.01, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 0.99
		for (double a : e1) {
			natural_uncertain1 = a;
			init();
			for (int i = 0; i < iter; i++) {
				update_city_pop_weight();
				update_user_weight();
			}
			if (!flag) {
				flag = true;
				String add = "data//population//results//result-avg-voting-median-org.txt";
				output(add);
				System.out.print("-\t");
				eval.getRMSE(add, 2, "avg-org-data", "all testing", GDScore);

				System.out.print("-\t");
				eval.getRMSE(add, 4, "voting-org-data", "all testing", GDScore);

				System.out.print("-\t");
				eval.getRMSE(add, 5, "median-org-data", "all testing", GDScore);

			}

			String add = "data//population//results//result-TruthFinder-" + a + ".txt";
			output(add);
			System.out.print(a + "\t");
			eval.getRMSE(add, 3, "TruthFinder", "all testing", GDScore);
		}
	}

	public void update_city_pop_weight() {
		for (int city_id = 1; city_id <= city_count; city_id++) {
			HashMap<Double, Double> pop_wts = city_pop_weight.get(city_id);
			for (double pop : pop_wts.keySet()) {
				double vote_count_theta = 0.0;
				HashSet<Integer> users = city_pop_user.get(city_id).get(pop);
				for (int user_id : users) {
					double user_wt = user_weight.get(user_id);
					vote_count_theta += user_wt;
				}
				city_pop_weight_theta.get(city_id).put(pop, vote_count_theta);
				double vote_count = 1 - Math.exp(-vote_count_theta);
				city_pop_weight.get(city_id).put(pop, vote_count);
			}

			// truthfinder-similarity
			HashMap<Double, Double> extra_sim_support_theta = new HashMap<>();
			for (double pop_org : pop_wts.keySet()) {
				double extra_vote_count = 0.0;
				for (double pop_extra : pop_wts.keySet()) {
					if (pop_org == pop_extra) {
						continue;
					}
					double sim = pop_org > pop_extra ? pop_extra / pop_org : pop_org / pop_extra;
					extra_vote_count += city_pop_weight_theta.get(city_id).get(pop_extra) * sim;
				}
				extra_vote_count *= natural_uncertain1;
				extra_sim_support_theta.put(pop_org, extra_vote_count);
			}

			for (double pop : pop_wts.keySet()) {
				double vote_count_theta = city_pop_weight_theta.get(city_id).get(pop) + extra_sim_support_theta.get(pop);
				city_pop_weight_theta.get(city_id).put(pop, vote_count_theta);
				double vote_count = 1 - Math.exp(-vote_count_theta);
				city_pop_weight.get(city_id).put(pop, vote_count);
			}
		}

	}

	public void update_user_weight() {
		for (int user_id = 1; user_id <= user_count; user_id++) {
			double weight_sum = 0.0;
			HashMap<Integer, Double> city_pop = user_city_pop.get(user_id);
			for (int city_id : city_pop.keySet()) {
				double tmp_city_pop = city_pop.get(city_id);
				weight_sum += city_pop_weight.get(city_id).get(tmp_city_pop);
			}
			weight_sum /= city_pop.size();
			user_weight.set(user_id, weight_sum * 0.999);
		}
	}

	public void output(String add) throws Exception {
		FileWriter writer = new FileWriter(add);
		FileWriter writer2 = new FileWriter("data\\population\\population-prior.txt");
		for (int city_id = 1; city_id <= city_count; city_id++) {
			double max_rate = 0;
			double max_weight = -1;
			for (double movie_score : city_pop_weight.get(city_id).keySet()) {
				double tmp_weight = city_pop_weight.get(city_id).get(movie_score);
				if (max_weight < tmp_weight) {
					max_weight = tmp_weight;
					max_rate = movie_score;
				}
			}

			// voting
			double max_voted_pop = 0;
			double max_vote = 0;
			for (double tmp_pop : city_pop_user.get(city_id).keySet()) {
				int tmp_vote = city_pop_user.get(city_id).get(tmp_pop).size();
				if (max_vote < tmp_vote) {
					max_vote = tmp_vote;
					max_voted_pop = tmp_pop;
				}
			}
			// median
			ArrayList<Double> user_pop = new ArrayList<>();
			HashMap<Integer, Double> this_cityuser_pop = city_user_pop.get(city_id);
			for (int tmp_user_id : this_cityuser_pop.keySet()) {
				user_pop.add(this_cityuser_pop.get(tmp_user_id));
			}
			Comparator<Double> cmp = new Comparator<Double>() {
				@Override
				public int compare(Double a, Double b) {
					double tmp = a - b;
					if (tmp < 0)
						return 1;
					if (tmp > 0)
						return -1;
					return 0;
				}
			};
			user_pop.sort(cmp);
			int len = user_pop.size();
			double median = user_pop.get(len / 2);
			if (len % 2 == 0) {
				median = (user_pop.get(len / 2) + user_pop.get(len / 2 - 1)) / 2.0;
			}
			writer2.write(cityId_cityName.get(city_id) + "\t" + max_rate + "\n");
			writer.write(city_id + "\t" + cityId_cityName.get(city_id) + "\t" + city_avg_t.get(city_id) + "\t" + max_rate + "\t" + max_voted_pop + "\t" + median + "\n");
		}
		writer.close();
		writer2.close();
	}

	public void init() {
		user_weight = new ArrayList<>();
		for (int i = 0; i <= user_count; i++) {
			user_weight.add(1.0);
		}
	}

	int city_count = 0;
	int user_count = 0;
	double natural_uncertain1 = 0.1;
	int iter = 5;
	ArrayList<HashMap<Integer, Double>> city_user_pop;
	ArrayList<Double> city_completeness;
	ArrayList<Double> city_consistency;
	ArrayList<Double> city_discrimination;
	ArrayList<Double> city_avg_t;
	ArrayList<Double> city_max_t;
	ArrayList<Double> city_exp_pop;
	ArrayList<HashMap<Integer, Double>> user_city_pop;
	ArrayList<Double> user_weight;
	HashMap<String, Integer> cityName_cityId;
	HashMap<Integer, String> cityId_cityName;
	ArrayList<HashMap<Double, Double>> city_pop_weight;
	ArrayList<HashMap<Double, Double>> city_pop_weight_theta;
	ArrayList<HashMap<Double, HashSet<Integer>>> city_pop_user;
	HashMap<String, Integer> userName_userId;
	public HashMap<Integer, Double> GDScore;

	public void load(String input, String yearF) throws Exception {
		LoadData loadData = new LoadData(input, yearF);
		city_user_pop = loadData.city_user_pop;
		city_completeness = loadData.city_completeness;
		city_consistency = loadData.city_consistency;
		city_discrimination = loadData.city_discrimination;
		city_avg_t = loadData.city_avg_t;
		city_max_t = loadData.city_max_t;
		city_exp_pop = loadData.city_exp_pop;
		city_pop_weight = loadData.city_pop_weight;
		city_pop_user = loadData.city_pop_user;
		city_pop_weight_theta = loadData.city_pop_weight_theta;
		user_city_pop = loadData.user_city_pop;
		cityName_cityId = loadData.cityName_cityId;
		userName_userId = loadData.userName_userId;
		cityId_cityName = loadData.cityId_cityName;
		GDScore = loadData.GDScore;
		city_count = loadData.city_count;
		user_count = loadData.user_count;
	}

}
