package SubjectiveLogicOnPopulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

public class Eval {

	public void getRMSE(String add, int column, String note, String testFlag, HashMap<Integer, Double> GDScore) throws Exception {
		HashMap<Integer, Double> thisScore = GDScore;
		double mae = 0.0;
		double rmse = 0;
		double error_rate = 0;
		int count = 0;
		String line;
		BufferedReader reader = new BufferedReader(new FileReader(new File(add)));
		while ((line = reader.readLine()) != null) {
			String[] tmp = line.split("\t");
			int id = Integer.parseInt(tmp[0]);
			if (thisScore.containsKey(id)) {
				double score = Double.parseDouble(tmp[column]);
				double errScore = Math.abs(thisScore.get(id) - score);
//				System.out.println(tmp[1] + "\tgd\t" + thisScore.get(id) + "\tour\t" + score);
				rmse += errScore * errScore;
				mae += errScore;
				if (errScore > thisScore.get(id) * 0.01) {
					error_rate += 1.0;
				}
				count++;
			}
		}
		reader.close();
		rmse /= count;
		rmse = Math.sqrt(rmse);
		error_rate /= count;
		mae /= count;
		System.out.println(note + "\tadd\t" + mae + "\t" + rmse + "\t" + error_rate + "\t" + count);
	}
}
