package SubjectiveLogicOnPopulation;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class GTM {

	public static void main(String[] args) throws Exception {
		GTM ab = new GTM();
		ab.load("getPirorTF", "yearFormat");
		ab.run();
	}

	public void run() throws Exception {
		Eval eval = new Eval();
		init();
		for (int i = 0; i < iter; i++) {
			update_city_exp_pop();
			update_user_weight();
		}
		String add = "data//population//results//result-avg.txt";
		output(add);
		System.out.print("-\t-\t");
		eval.getRMSE(add, 2, "avg", "all testing", GDScore);
		add = "data//population//results//result-GTM-" + alpha + "-" + beta + "-" + mu0 + "-" + theta02 + ".txt";
		output(add);
		System.out.print(alpha + "\t" + beta + "\t" + mu0 + "\t" + theta02 + "\t");
		eval.getRMSE(add, 3, "GTM", "all testing", GDScore);

		double[] e1 = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25 };// , 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36
		double[] e2 = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25 };
		double[] e3 = { 0 };
		double[] e4 = { 1 };
		for (double a : e1) {
			for (double b : e2) {
				for (double c : e3) {
					for (double d : e4) {
						alpha = a;
						beta = b;
						mu0 = c;
						theta02 = d;

						init();
						for (int i = 0; i < iter; i++) {
							update_city_exp_pop();
							update_user_weight();
						}

						add = "data//population//results//result-GTM-" + alpha + "-" + beta + "-" + mu0 + "-" + theta02 + ".txt";
						output(add);
						System.out.print(alpha + "\t" + beta + "\t" + mu0 + "\t" + theta02 + "\t");
						eval.getRMSE(add, 3, "GTM", "all testing", GDScore);
					}
				}
			}
		}
	}

	public void update_city_exp_pop() {
		for (int city_id = 1; city_id <= city_count; city_id++) {
			double fenzi = mu0 / theta02;
			double femmu = 1.0 / theta02;
			HashMap<Integer, Double> user_rate = city_user_pop.get(city_id);
			for (int user_id : user_rate.keySet()) {
				double tmp_user_rate = user_rate.get(user_id);
				double tmp_user_wt = user_weight.get(user_id);

				fenzi += tmp_user_rate / tmp_user_wt;
				femmu += 1.0 / tmp_user_wt;
			}
			city_exp_pop.set(city_id, fenzi / femmu);
		}
	}

	public void update_user_weight() {
		for (int user_id = 1; user_id <= user_count; user_id++) {
			HashMap<Integer, Double> city_pop = user_city_pop.get(user_id);
			double fenzi = 2 * beta;
			double femmu = 2 * alpha + 2 + city_pop.size();
			for (int city_id : city_pop.keySet()) {
				double tmp_movie_given_rate = city_pop.get(city_id);
				double tmp_movie_exp_rate = city_exp_pop.get(city_id);
				fenzi += (tmp_movie_given_rate - tmp_movie_exp_rate) * (tmp_movie_given_rate - tmp_movie_exp_rate);
			}
			user_weight.set(user_id, fenzi / femmu);
		}
	}

	public void output(String add) throws Exception {
		FileWriter writer = new FileWriter(add);
		for (int city_id = 1; city_id <= city_count; city_id++) {
			double avg_to_num = city_avg_t.get(city_id) * city_sd.get(city_id) + city_prior.get(city_id);

			double score = city_exp_pop.get(city_id);
			score = score * city_sd.get(city_id) + city_prior.get(city_id);

			writer.write(city_id + "\t" + cityId_cityName.get(city_id) + "\t" + avg_to_num + "\t" + score + "\n");
		}
		writer.close();
	}

	double alpha = 10;
	double beta = 10;
	double mu0 = 0;
	double theta02 = 1;

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

	public void init() {
		user_weight = new ArrayList<>();
		for (int i = 0; i <= user_count; i++) {
			user_weight.add(1.0);
		}
	}

}
