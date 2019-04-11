package SubjectiveLogicOnPopulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class BasicClass {

	public int with_outlier_city_count = 0;
	public int with_outlier_user_count = 0;

	public int user_count = 0;
	public int city_count = 0;

	public BasicClass() throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(new File("data\\population\\settings.txt")));
		String line = reader.readLine();
		with_outlier_city_count = Integer.parseInt(line.split("\t")[1]);
		line = reader.readLine();
		with_outlier_user_count = Integer.parseInt(line.split("\t")[1]);
		line = reader.readLine();
		user_count = Integer.parseInt(line.split("\t")[1]);
		line = reader.readLine();
		city_count = Integer.parseInt(line.split("\t")[1]);
		reader.close();
	}

	public void update(int a, int b, int c, int d) throws Exception {
		with_outlier_city_count = a;
		with_outlier_user_count = b;
		user_count = c;
		city_count = d;
		FileWriter writer = new FileWriter("data\\population\\settings.txt");
		writer.write("with_outlier_city_count\t" + a + "\n");
		writer.write("with_outlier_user_count\t" + b + "\n");
		writer.write("user_count\t" + c + "\n");
		writer.write("city_count\t" + d + "\n");
		writer.close();
	}

	public static void main(String[] args) throws Exception {
		CATD ab = new CATD();
		ab.load("getPirorTF", "yearFormat");
		ab.run();
		CRH abc = new CRH();
		abc.load("getPirorTF", "yearFormat");
		abc.run();
		Sums abcdd = new Sums();
		abcdd.load("getPirorTF", "yearFormat");
		abcdd.run();
		// GTM abcd = new GTM();
		// abcd.load("getPirorTF", "NotyearFormat");
		// abcd.run();
		// TruthFinderNoOutlier abcde = new TruthFinderNoOutlier();
		// abcde.load("getPirorTF", "NotyearFormat");
		// abcde.run();
		// version1 aa = new version1();
		// aa.load("getPirorTF", "NotyearFormat");
		// aa.run();
		// version2 aab = new version2();
		// aab.load("getPirorTF", "NotyearFormat");
		// aab.run();
	}

}
