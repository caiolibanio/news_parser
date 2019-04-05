package sensacionalista;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Teste {

	public static void main(String[] args) {
//		String csvFile = "csvNews_satire_est_folha_g1.csv";
//        String line = "";
//        String cvsSplitBy = ",";
//        List<String> listNews = new ArrayList<String>();
//        List<String> listClass = new ArrayList<String>();
//        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
//
//            while ((line = br.readLine()) != null) {
//
//                // use comma as separator
//                String[] l = line.split(cvsSplitBy);
//                listNews.add(stripAccents(l[0]));
//                listClass.add(l[1]);
//
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//		
//        try {
//			writeCSV(listNews, listClass, "csvNews_satire_est_folha_g1_NO_ACCENTS.csv");
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//
//	}
//	
//	private static void writeCSV(List<String> listNews, List<String> listClass, String csvPath) throws FileNotFoundException {
//		PrintWriter pw = new PrintWriter(new File(csvPath));
//        StringBuilder sb = new StringBuilder();
//        sb.append("text");
//        sb.append(',');
//        sb.append("label");
//        sb.append('\n');
//        
//        for (int i = 0; i < listNews.size(); i++) {
//        	if(listNews.get(i).length() > 30) {
//        		sb.append(listNews.get(i));
//                sb.append(',');
//                sb.append(listClass.get(i));
//                sb.append('\n');
//        	}
//        }
//
//        pw.write(sb.toString());
//        pw.close();
//	}
//	
//	public static String stripAccents(String s) {
//	    s = Normalizer.normalize(s, Normalizer.Form.NFD);
//	    s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
//	    return s;
//	}


	
	}
}
