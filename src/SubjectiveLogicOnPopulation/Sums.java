package SubjectiveLogicOnPopulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

public class Sums {

	public static void main(String[] args) throws Exception {
		Sums ab = new Sums();
		ab.load("getPirorTF", "yearFormat");
		ab.run();
	}

	public void run() throws Exception {
		Eval eval = new Eval();
		init();
		for (int i = 0; i < iter; i++) {
			update_city_pop_weight();
			update_user_weight();
		}

		String add = "data//population//results//result-Sums.txt";
		output(add);
		eval.getRMSE(add, 3, "Sums", "all testing", GDScore);
	}

	public void init() {
		double init_weight = 0.8;
		double init_tao = -Math.log(1 - init_weight);
		user_weight = new ArrayList<>();
		user_weight_tao = new ArrayList<>();
		for (int i = 0; i <= user_count; i++) {
			user_weight.add(init_weight);
			user_weight_tao.add(init_tao);
		}
	}

	public void update_city_pop_weight() {
		for (int city_id = 1; city_id <= city_count; city_id++) {
			// sums, hubs and authorities & avgerage.log
			HashMap<Double, Double> pop_wts = city_pop_weight.get(city_id);
			for (double pop : pop_wts.keySet()) {
				double vote_count = 0.0;
				HashSet<Integer> users = city_pop_user.get(city_id).get(pop);
				for (int user_id : users) {
					double user_wt = user_weight.get(user_id);
					vote_count += user_wt;
				}
				city_pop_weight.get(city_id).put(pop, vote_count);
			}

			// // investment & investment pooled
			// HashMap<Double, Double> pop_wts = city_pop_weight.get(city_id);
			// for (double pop : pop_wts.keySet()) {
			// double vote_count = 0.0;
			// HashSet<Integer> users = city_pop_user.get(city_id).get(pop);
			// for (int user_id : users) {
			// double user_wt = user_weight.get(user_id);
			// vote_count += user_wt / user_city_pop.get(user_id).size();
			// }
			// city_pop_weight.get(city_id).put(pop, vote_count);
			// }
		}

	}

	public void update_user_weight() {
		ArrayList<Double> user_weight_copy = new ArrayList<>(user_weight);
		for (int user_id = 1; user_id <= user_count; user_id++) {
			// sums, hubs and authorities
			double weight_sum = 0.0;
			HashMap<Integer, Double> city_pop = user_city_pop.get(user_id);
			for (int city_id : city_pop.keySet()) {
				double tmp_city_pop = city_pop.get(city_id);
				weight_sum += city_pop_weight.get(city_id).get(tmp_city_pop);
			}
			user_weight.set(user_id, weight_sum);

			// // avgerage.log
			// double weight_sum = 0.0;
			// HashMap<Integer, Double> city_pop = user_city_pop.get(user_id);
			// for (int city_id : city_pop.keySet()) {
			// double tmp_city_pop = city_pop.get(city_id);
			// weight_sum += city_pop_weight.get(city_id).get(tmp_city_pop);
			// }
			// double log_count = Math.log(city_pop.size()) / city_pop.size();
			// weight_sum *= log_count;
			// user_weight.set(user_id, weight_sum);

			// // investment
			// double weight_sum = 0.0;
			// HashMap<Integer, Double> city_pop = user_city_pop.get(user_id);
			// for (int city_id : city_pop.keySet()) {
			// double tmp_city_pop = city_pop.get(city_id);
			// double org_value_score = city_pop_weight.get(city_id).get(tmp_city_pop);
			// double value_score = Math.pow(org_value_score, 1.2);
			// if (org_value_score == 0.0 || value_score == 0.0) {
			// continue;
			// }
			// weight_sum += value_score / org_value_score;
			// }
			// weight_sum *= user_weight_copy.get(user_id);
			// weight_sum /= city_pop.size();
			// user_weight.set(user_id, weight_sum);

			// // investment-pooled
			// double weight_sum = 0.0;
			// HashMap<Integer, Double> city_pop = user_city_pop.get(user_id);
			// for (int city_id : city_pop.keySet()) {
			// double tmp_city_pop = city_pop.get(city_id);
			// double org_value_score = city_pop_weight.get(city_id).get(tmp_city_pop);
			// double value_score = Math.pow(org_value_score, 1.4);
			// if (org_value_score == 0.0 || value_score == 0.0) {
			// continue;
			// }
			//
			// double sum = value_score;
			// for (double pop_extra_support : city_pop_user.get(city_id).keySet()) {
			// if (tmp_city_pop == pop_extra_support) {
			// continue;
			// }
			// if (Math.abs(pop_extra_support - tmp_city_pop) / Math.max(pop_extra_support, tmp_city_pop) > 0.1) {
			// double another_value_score = city_pop_weight.get(city_id).get(pop_extra_support);
			// another_value_score = Math.pow(another_value_score, 1.4);
			// sum += another_value_score;
			// }
			// }
			// weight_sum += value_score / sum;
			// }
			// weight_sum *= user_weight_copy.get(user_id);
			// weight_sum /= city_pop.size();
			// user_weight.set(user_id, weight_sum);
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
			max_rate = max_rate * city_sd.get(city_id) + city_prior.get(city_id);

			// avg
			double avg_to_num = city_avg_t.get(city_id) * city_sd.get(city_id) + city_prior.get(city_id);

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
			max_voted_pop = max_voted_pop * city_sd.get(city_id) + city_prior.get(city_id);

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
			median = median * city_sd.get(city_id) + city_prior.get(city_id);

			writer2.write(cityId_cityName.get(city_id) + "\t" + max_rate + "\n");
			writer.write(city_id + "\t" + cityId_cityName.get(city_id) + "\t" + avg_to_num + "\t" + max_rate + "\t" + max_voted_pop + "\t" + median + "\n");
		}
		writer.close();
		writer2.close();
	}

	ArrayList<Double> user_weight_tao;
	int city_count = 0;
	int user_count = 0;
	int iter = 5;
	ArrayList<HashMap<Integer, Double>> city_user_pop;
	ArrayList<Double> city_completeness;
	ArrayList<Double> city_consistency;
	ArrayList<Double> city_discrimination;
	ArrayList<Double> city_avg_t;
	ArrayList<Double> city_max_t;
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
		city_sd = loadData.city_sd;
		city_prior = loadData.city_prior;
		user_count = loadData.user_count;
		city_count = loadData.city_count;
	}

}
