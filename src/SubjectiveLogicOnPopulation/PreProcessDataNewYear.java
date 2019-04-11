package SubjectiveLogicOnPopulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import org.netlib.util.doubleW;

import weka.classifiers.evaluation.output.prediction.Null;

public class PreProcessDataNewYear {

	public static void main(String[] args) throws Exception {
		PreProcessDataNewYear abc = new PreProcessDataNewYear();
		abc.removeHistory();
		System.out.println("---");
		abc.removeTrivial();
		System.out.println("---");
		abc.getPrior();
		System.out.println("---");
		abc.removeOutlierAfterTruthFinder();
		System.out.println("---");
	}

	public void removeHistory() throws Exception {
		FileWriter error_wr = new FileWriter("data\\population\\population-removed.txt");
		HashSet<String> user_set = new HashSet<>();
		HashSet<String> city_set = new HashSet<>();
		HashSet<String> city_year_set = new HashSet<>();
		HashMap<String, struct> city_user_info = new HashMap<>();
		String line = "";
		BufferedReader reader = new BufferedReader(new FileReader(new File("data\\population\\population.txt")));
		while ((line = reader.readLine()) != null) {
			String[] tmp = line.toLowerCase().replace(" (town)", "").replace(" (city)", "").replace(" (village)", "").split("\t");

			String city = tmp[0];
			String user = tmp[4];
			String pop = tmp[7];
			user_set.add(user);
			city_set.add(city);

			String[] tmp6 = tmp[6].split(" ");
			int year = Integer.parseInt(tmp6[0].substring(tmp6[0].lastIndexOf("/") + 1));
			if (year >= 2008 || year < 2000) {
				continue;
			}
			city = city + "_" + year;
			city_year_set.add(city);

			int r_id = Integer.parseInt(tmp[5]);

			String key = city + "\t" + user;
			struct value = new struct(pop, r_id, year);
			if (city_user_info.containsKey(key)) {
				struct org = city_user_info.get(key);
				if (r_id > org.r_id) {
					city_user_info.put(key, value);
				}
			} else {
				city_user_info.put(key, value);
			}

		}
		error_wr.close();
		reader.close();
		HashMap<String, Integer> city_popCount = new HashMap<>();
		FileWriter writer = new FileWriter("data\\population\\population-new.txt");
		for (String key : city_user_info.keySet()) {
			String[] tmp = key.split("\t");
			String city = tmp[0];
			city_popCount.put(city, city_popCount.getOrDefault(city, 0) + 1);
			writer.write(key + "\t" + city_user_info.get(key).pop + "\t" + city_user_info.get(key).time + "\n");
		}
		writer.close();
		System.out.println("org city_year count\t" + city_year_set.size());
		System.out.println("org city_city count\t" + city_set.size());
		System.out.println("org user count\t" + user_set.size());
		writer = new FileWriter("data\\population\\city_tupleCount.txt");
		for (String key : city_popCount.keySet()) {
			writer.write(key + "\t" + city_popCount.get(key) + "\n");
		}
		writer.close();
	}

	public void removeTrivial() throws Exception {
		String line = "";
		HashMap<String, ArrayList<StringLong>> city_user_pop = new HashMap<>();
		BufferedReader reader = new BufferedReader(new FileReader(new File("data\\population\\population-new.txt")));
		HashSet<String> city_count_set = new HashSet<>();
		HashSet<String> user_count_set = new HashSet<>();
		HashSet<String> city_year_count_set = new HashSet<>();
		while ((line = reader.readLine()) != null) {
			String[] tmp = line.split("\t");
			String city = tmp[0];
			String user = tmp[1];
			long pop = Long.parseLong(tmp[2]);
			// System.out.println(city);
			city_year_count_set.add(city);
			city_count_set.add(city.substring(0, city.indexOf("_")));
			user_count_set.add(user);
			if (!city_user_pop.containsKey(city)) {
				city_user_pop.put(city, new ArrayList<>());
			}
			city_user_pop.get(city).add(new StringLong(user, pop));
		}
		reader.close();
		System.out.println("city_year count\t" + city_year_count_set.size());
		System.out.println("city_city count\t" + city_count_set.size());
		System.out.println("     user count\t" + user_count_set.size());

		// check trivial data
		int count = 0;
		user_count_set = new HashSet<>();
		for (String city : city_user_pop.keySet()) {
			ArrayList<StringLong> user_pop = city_user_pop.get(city);
			// remove city with same pop
			boolean flagOK = false;
			long pop1 = user_pop.get(0).longV;
			for (int i = 1; i < user_pop.size(); i++) {
				if (pop1 != user_pop.get(i).longV) {
					flagOK = true;
				}
			}
			if (!flagOK) {
				city_user_pop.put(city, null);
				continue;
			}
			count++;
			for (int i = 0; i < user_pop.size(); i++) {
				user_count_set.add(user_pop.get(i).stringV);
			}
		}
		System.out.println("city count\t" + count);
		System.out.println("user count\t" + user_count_set.size());
		BasicClass basicClass = new BasicClass();
		basicClass.update(count, user_count_set.size(), 0, 0);

		FileWriter writer = new FileWriter("data\\population\\population-new-new.txt");
		reader = new BufferedReader(new FileReader(new File("data\\population\\population-new.txt")));
		while ((line = reader.readLine()) != null) {
			String[] tmp = line.split("\t");
			String city = tmp[0];
			if (city_user_pop.containsKey(city) && city_user_pop.get(city) != null) {
				writer.write(line + "\n");
			}
		}
		writer.close();
	}

	public void getPrior() throws Exception {
		Eval eval = new Eval();
		TruthFinder ab = new TruthFinder();
		ab.load("getORGdata", "yearFormat");
		ab.natural_uncertain1 = 0.2;
		ab.init();
		for (int i = 0; i < ab.iter; i++) {
			ab.update_city_pop_weight();
			ab.update_user_weight();
		}
		 String add = "data//population//results//result-avg-voting-median-org.txt";
		 ab.output(add);
		 System.out.print("-\t");
		 eval.getRMSE(add, 2, "avg-org-data", "all testing", ab.GDScore);
		
		 System.out.print("-\t");
		 eval.getRMSE(add, 4, "voting-org-data", "all testing", ab.GDScore);
		
		 System.out.print("-\t");
		 eval.getRMSE(add, 5, "median-org-data", "all testing", ab.GDScore);

		String addd = "data//population//results//result-TruthFinder-0.txt";
		ab.output(addd);
		System.out.print(ab.natural_uncertain1 + "\t");
		eval.getRMSE(addd, 3, "TruthFinder", "all testing", ab.GDScore);
	}

	double theta1 = 0.91;
	double theta2 = 2.5;
	HashMap<String, Double> cityName_prior;

	public void removeOutlierAfterTruthFinder() throws Exception {
		String line = "";
		cityName_prior = new HashMap<>();
		BufferedReader reader = new BufferedReader(new FileReader(new File("data\\population\\population-prior.txt")));
		while ((line = reader.readLine()) != null) {
			String[] tmp = line.split("\t");
			cityName_prior.put(tmp[0], Double.parseDouble(tmp[1]));
		}
		reader.close();

		HashMap<String, ArrayList<StringLong>> city_user_pop = new HashMap<>();
		reader = new BufferedReader(new FileReader(new File("data\\population\\population-new-new.txt")));
		while ((line = reader.readLine()) != null) {
			String[] tmp = line.split("\t");

			String city = tmp[0];
			if (!city_user_pop.containsKey(city)) {
				city_user_pop.put(city, new ArrayList<>());
			}

			String user = tmp[1];
			long pop = Long.parseLong(tmp[2]);
			city_user_pop.get(city).add(new StringLong(user, pop));
		}
		reader.close();

		HashMap<String, Integer> user_count = new HashMap<>();
		HashMap<String, Integer> city_count = new HashMap<>();
		HashMap<String, Integer> city_city_count = new HashMap<>();
		FileWriter writer = new FileWriter("data\\population\\population-new-new-new-TF.txt");
		FileWriter writer_count = new FileWriter("data\\population\\city_tupleCount-TF.txt");
		for (String city : city_user_pop.keySet()) {
			// get priors
			ArrayList<StringLong> user_pop = city_user_pop.get(city);
			int len = user_pop.size();
			

			// init outlier
			double prior = cityName_prior.get(city);
			for (int i = 0; i < len; i++) {
				StringLong this_user_pop = user_pop.get(i);
				double tmp_v = Math.abs(this_user_pop.longV - prior) / prior;
				if (tmp_v > theta1) {
					this_user_pop.outlier = true;
					if(city.indexOf("greenfield, california")>=0){
						System.out.println("herheherheherheherheherheherheherheherheherheherhe");
					}
				}
			}
			// estimate sd
			double sd = calculateSD(user_pop);

			// repeat
			int new_outlier_appear = 1;
			while (new_outlier_appear > 0) {
				new_outlier_appear = 0;

				for (int i = 0; i < len; i++) {
					StringLong this_user_pop = user_pop.get(i);
					double tmp_v = Math.abs(this_user_pop.longV - prior) / sd;
					if (sd == 0) {
						tmp_v = 0;
					}
					if (tmp_v > theta2 && this_user_pop.outlier == false) {
						this_user_pop.outlier = true;
						new_outlier_appear++;
					}
				}
				sd = calculateSD(user_pop);
			}

			// get new data
			int user_count_of_city = 0;
			for (int i = 0; i < len; i++) {
				StringLong this_user_pop = user_pop.get(i);
				if (this_user_pop.outlier) {
					continue;
				}
				user_count_of_city++;
				user_count.put(this_user_pop.stringV, user_count.getOrDefault(this_user_pop.stringV, 0) + 1);
				double new_pop = (sd == 0) ? 0 : (this_user_pop.longV - prior) / sd;
				writer.write(city + "\t" + this_user_pop.stringV + "\t" + new_pop + "\t" + prior + "\t" + sd + "\t" + this_user_pop.longV + "\n");
			}
			writer_count.write(city + "\t" + user_count_of_city + "\t" + len + "\n");
			city_count.put(city, user_count_of_city);
			String city_city = city.substring(0, city.indexOf("_"));
			city_city_count.put(city_city, city_city_count.getOrDefault(city_city, 0) + 1);
		}
		writer.close();
		writer_count.close();
		System.out.println("user count: " + user_count.size());
		System.out.println("city count: " + city_count.size());
		System.out.println("city_city count: " + city_city_count.size());

		BasicClass basicClass = new BasicClass();
		basicClass.update(basicClass.with_outlier_city_count, basicClass.with_outlier_user_count, user_count.size(), city_user_pop.size());
	}

	public double calculateSD(ArrayList<StringLong> list) {
		double sum = 0.0;
		double standardDeviation = 0.0;
		int length = 0;
		for (StringLong cell : list) {
			if (cell.outlier) {
				continue;
			}
			sum += cell.longV;
			length++;
		}
		double mean = sum / length;
		for (StringLong cell : list) {
			if (cell.outlier) {
				continue;
			}
			standardDeviation += Math.pow(cell.longV - mean, 2);
		}
		if (length > 0) {
			return Math.sqrt(standardDeviation / length);
		}

		// no city has such situation.
		System.out.println("city all outliers");
		for (StringLong cell : list) {
			sum += cell.longV;
			length++;
		}
		mean = sum / length;
		for (StringLong cell : list) {
			standardDeviation += Math.pow(cell.longV - mean, 2);
		}
		return Math.sqrt(standardDeviation / length);
	}

	public Comparator<StringLong> cmp = new Comparator<StringLong>() {
		@Override
		public int compare(StringLong a, StringLong b) {
			double tmp = a.longV - b.longV;
			if (tmp < 0)
				return 1;
			if (tmp > 0)
				return -1;
			return 0;
		}
	};

	class StringLong {
		String stringV;
		long longV;
		boolean outlier = false;

		public StringLong(String a, long b) {
			stringV = a;
			longV = b;
		}
	}

	class struct {
		String pop;
		int r_id;
		int time;

		public struct(String a, int h, int c) {
			pop = a;
			r_id = h;
			time = c;
		}
	}

}
