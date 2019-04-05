package sensacionalista;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.plaf.basic.BasicTreeUI.TreeHomeAction;

import org.apache.commons.io.IOUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class SensacionalistaMain {

	public static void main(String args[]) throws Exception {
		
		ParseNewsSensacionalista c = new ParseNewsSensacionalista();
		String baseUrl = "https://www.sensacionalista.com.br/pais/";
		
//		c.parseNews(baseUrl, 2, 460);
		
		//Something goes wrong in page: https://www.sensacionalista.com.br/pais/page/441/
		//Thu Nov 16 23:16:24 BRT 2017
		
		String satirePath = "2017-11-16 22_14_08.968";
//		String satirePath = "2018-03-24 12_44_46.493";
		String fakeNewsPath = "fake_news_br";
		String fakeUsp = "fake_usp";
		String esportesPath = "noticias_esporte";
		String esporteEconomiaPath = "esporte_economia";
		String realOldNewsG1 = "g1_rss";
		String realPolitical = "realPolitical";
		String diarioPernambucano = "diarioPernambucano_brasil";
		String blogsFolha = "blogsFolha";
		String sensacinalistaDiversos = "sensacinalista_comportamento-2";
		
		String fake_horne = "Fake_horne";
		String satire_horne = "Satire_horne";
		String real_horne = "Real_horne";
		String fake_top_buzz_2016 = "top_fake_2016_buzzfeed";
		String fake_top_buzz_2017 = "top_fake_2017_buzzfeed";
		createCSVFile("", diarioPernambucano);
//		createCSVFileByYearBuckets(satirePath);
		
		
		
	}
	
	public static List<File> listFilesForFolder(final File folder) {
		List<File> listFiles = new ArrayList<File>();
		
	    for (final File fileEntry : folder.listFiles()) {
	    	listFiles.add(fileEntry.getAbsoluteFile());
	    }
	    return listFiles;
	}
	
	private static String readFile(File file) {
		String text = null;
		try(FileInputStream inputStream = new FileInputStream(file)) {     
		    text = IOUtils.toString(inputStream);
		    // do something with everything string
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return text;
	}

	private static String extractNewsFromDocument(String text) {
		text = text.replaceAll("<text>", "<text> ");
		String title = extractNewsTitleFromDocument(text);
		String newsText = text.split("<text> ")[2].split("</text>")[0];
		String full_text = title + "----" + newsText;
		return full_text;
	}
	
	private static String extractNewsTitleFromDocument(String text) {
		text = text.replaceAll("<text>", "<text> ");
		String title = null;
		if(text.contains("</b></text>")) {
			title = text.split("<text> ")[1].replaceAll("<b>", "").replaceAll("</b></text>", "");
		}else {
			title = text.split("<text> ")[1].replaceAll("</text>", "");
		}
		return title;
	}
	
	private static void writeCSV(List<String> listFakeNews, List<String> listRealNews,String csvPath) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(new File(csvPath));
        StringBuilder sb = new StringBuilder();
        sb.append("title");
        sb.append(',');
        sb.append("text");
        sb.append(',');
        sb.append("label");
        sb.append('\n');
        for(String news : listFakeNews) {
        	String title_body[] = news.split("----");
        	String title = title_body[0];
        	String body = title_body[1].replaceAll("\\.","\\. ");
        	if(body.length() > 30) {
        		title = title.replaceAll("\"", "");
        		body = body.replaceAll("\"", "");
        		sb.append("\"" + title + "\"");
        		sb.append(',');
        		sb.append("\"" + body + "\"");
                sb.append(',');
                sb.append("fake");
                sb.append('\n');
        	}
        }
        
        
// fake USP
//        for(String news : listFakeNews) {
//        	String title_body[] = news.split("\\. ", 2);
//        	String title = title_body[0];
//        	String body = "";
//        	if (title_body.length > 1) {
//        		body = title_body[1].replaceAll("\\.","\\. ");
//        	}else {
//        		body = title;
//        	}
//        	
//        	if(body.length() > 30) {
//        		title = title.replaceAll("\"", "");
//        		body = body.replaceAll("\"", "");
//        		sb.append("\"" + title + "\"");
//        		sb.append(',');
//        		sb.append("\"" + body + "\"");
//                sb.append(',');
//                sb.append("fake");
//                sb.append('\n');
//        	}
//        }
        
        for(String news : listRealNews) {
        	String title_body[] = news.split("----");
        	String title = title_body[0];
        	String body = title_body[1];
        	if(body.length() > 30) {
        		sb.append(title + "\"");
        		sb.append(',');
        		sb.append("\"" + body);
                sb.append(',');
                sb.append("real");
                sb.append('\n');
        	}
        }

        pw.write(sb.toString());
        pw.close();
	}
	
	private static List<String> extractRealNewsByYear(long timestampTop, long timestampDown){
		List<String> realNews = new ArrayList<String>();
		MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017/news_db"));
		DB database = mongoClient.getDB("news_db");
		DBCollection collection = database.getCollection("estadaoNoticias");
		DBCollection collection1 = database.getCollection("folhaNoticias");
		DBCollection collection2 = database.getCollection("g1Noticias");
		
		BasicDBObject gtQuery = new BasicDBObject();
		gtQuery.put("timestamp", new BasicDBObject("$gt", timestampDown).append("$lt", timestampTop));
		DBCursor cursor = collection.find(gtQuery);
		DBCursor cursor1 = collection1.find(gtQuery);
		DBCursor cursor2 = collection2.find(gtQuery);
		
		while (cursor.hasNext()) {
		   DBObject obj = cursor.next();
		   String news = (String) obj.get("conteudo");
		   String title = (String) obj.get("titulo");
		   if(!news.equals("") && news.length() > 100) {
			   news = title + "----" + news;
			   news = formatToCSV(news);
			   realNews.add(news);
		   }
		}
		
		//estou colocando um a mais aqui, devido a um BUG!
		while (cursor1.hasNext()) {
		   DBObject obj = cursor1.next();
		   String news = (String) obj.get("conteudo");
		   String title = (String) obj.get("titulo");
		   if(!news.equals("") && news.length() > 100) {
			   news = title + "----" + news;
			   news = formatToCSV(news);
			   realNews.add(news);
		   }
		}
		
		while (cursor2.hasNext()) {
		   DBObject obj = cursor2.next();
		   String news = (String) obj.get("conteudo");
		   String title = (String) obj.get("titulo");
		   if(!news.equals("") && news.length() > 100) {
			   news = title + "----" + news;
			   news = formatToCSV(news);
			   realNews.add(news);
		   }
		}
		mongoClient.close();
		System.out.println("tamanho lista: " + realNews.size());
		return realNews;
	}
	
	private static ArrayList<List<String>> extractRealNews() {
		ArrayList<List<String>> listOfLists = new ArrayList<List<String>>();
		
		List<String> realNewsEstadao = new ArrayList<String>();
		List<String> realNewsFolha = new ArrayList<String>();
		List<String> realNewsG1 = new ArrayList<String>();
		
		MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017/news_db"));
		DB database = mongoClient.getDB("news_db");
//		DBCollection collection = database.getCollection("estadaoNoticias");
		DBCollection collection1 = database.getCollection("noticiasFolha_17_16_15_14");
//		DBCollection collection2 = database.getCollection("g1Noticias");
		
//		BasicDBObject gtQuery = new BasicDBObject();
//		gtQuery.put("caderno", "economia");
//		DBCursor cursor = collection.find(gtQuery);
//		
//		
////		DBCursor cursor = collection.find();
//		int size = 0;
//		while (cursor.hasNext()) {
//		   DBObject obj = cursor.next();
//		   String news = (String) obj.get("conteudo");
//		   String title = (String) obj.get("titulo");
//		   if(!news.equals("") && news.length() > 100) {
//			   news = title.replaceAll("-", "") + "----" + news.replaceAll("-", "");
//			   news = formatToCSV(news);
//			   realNewsEstadao.add(news);
//			   size++;
//		   }
//		}
		
		BasicDBObject gtQueryFolha = new BasicDBObject();
		gtQueryFolha.put("caderno", "cultura");
		DBCursor cursor1 = collection1.find(gtQueryFolha);
		
//		DBCursor cursor1 = collection1.find(); 
		int size1 = 0;
		while (cursor1.hasNext()) {
		   DBObject obj = cursor1.next();
		   String news = (String) obj.get("conteudo");
		   String title = (String) obj.get("titulo");
		   if(!news.equals("") && news.length() > 100 && !title.contains("em inglês")) {
			   news = title.replaceAll("-", "") + "----" + news.replaceAll("-", "");
			   news = formatToCSV(news.replaceAll("\"", ""));
			   realNewsFolha.add(news);
			   size1++;
		   }

	}
		
//		DBCursor cursor2 = collection2.find();
//		int size2 = 0;
//		while (cursor2.hasNext()) {
//		   DBObject obj = cursor2.next();
//		   String news = (String) obj.get("conteudo");
//		   String title = (String) obj.get("titulo");
//		   if(!news.equals("") && news.length() > 100) {
//			   news = title.replaceAll("-", "") + "----" + news.replaceAll("-", "");
//			   news = formatToCSV(news.replaceAll("\"", ""));
//			   realNewsG1.add(news);
//			   size2++;
//		   }
//		}
		listOfLists.add(realNewsEstadao);
		listOfLists.add(realNewsFolha);
		listOfLists.add(realNewsG1);
		mongoClient.close();
		//System.out.println("tamanho lista: " + realNews.size());
		return listOfLists;
	}
	
	private static String formatToCSV(String text){
		text = text.replaceAll("\"", "'");
		String textNews = "\"" + text + "\"";
		textNews = textNews.replaceAll("\\r?\\n", " ").replaceAll(",", "").replaceAll(";", "");
		return textNews;
	}
	
	private static void createCSVFileByYearBuckets(String path) {
		final File folder = new File(path);
		List<File> listFiles = listFilesForFolder(folder);
		List<String> listFakeNewsFinal = new ArrayList<String>();
		List<String> listRealNewsFinal = new ArrayList<String>();
		Map<String, List<Long>> mapYear = buildYearMap();
		String[] yearsArray = {"2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017"}; 
//		String[] yearsArray = {"2010", "2011"}; 
		
		
		for(String year: yearsArray) {
			List<String> listFakeNews = new ArrayList<String>();
			List<String> listRealNews = new ArrayList<String>();
			//satiras
			for (File f : listFiles) {
				String[] fileNameArray = f.getName().split("_");
				String fileYear = fileNameArray[fileNameArray.length - 1].replace(".html", "");
				if(fileYear.equals(year)) {
					String textFile = readFile(f);
					String textNews = extractNewsFromDocument(textFile);
					textNews = formatToCSV(textNews);
					listFakeNews.add(textNews);
				}
			}
			listRealNews = extractRealNewsByYear(mapYear.get(year).get(1), mapYear.get(year).get(0));
			System.out.println("Total Real News: " + listRealNews.size());
			System.out.println("Total Fake News: " + listFakeNews.size());
			newsBalancing(listRealNews, listFakeNews);
			//splitBodyOrTitleText(listRealNews, listFakeNews);
			listFakeNewsFinal.addAll(listFakeNews);
			listRealNewsFinal.addAll(listRealNews);
			System.out.println("Finished: " + year + " - fake: " + listFakeNews.size() + " - real: " + listRealNews.size());
			
		}

		try {
			writeCSV(listFakeNewsFinal, listRealNewsFinal, "csvReal_allsources_VS_Satire_2010To2017_antagonists.csv");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		

	}
	
	private static Map<String, List<Long>> buildYearMap(){
		Map<String, List<Long>> map = new HashMap<String, List<Long>>();
		List<Long> list2010 = new ArrayList<Long>();
		list2010.add(new Long(1262304000));
		list2010.add(new Long(1293753600));
		map.put("2010", list2010);
		
		List<Long> list2011 = new ArrayList<Long>();
		list2011.add(new Long(1293840000));
		list2011.add(new Long(1325289600));
		map.put("2011", list2011);
		
		List<Long> list2012 = new ArrayList<Long>();
		list2012.add(new Long(1325376000));
		list2012.add(new Long(1356912000));
		map.put("2012", list2012);
		
		List<Long> list2013 = new ArrayList<Long>();
		list2013.add(new Long(1356998400));
		list2013.add(new Long(1388448000));
		map.put("2013", list2013);
		
		List<Long> list2014 = new ArrayList<Long>();
		list2014.add(new Long(1388534400));
		list2014.add(new Long(1419984000));
		map.put("2014", list2014);

		List<Long> list2015 = new ArrayList<Long>();
		list2015.add(new Long(1420070400));
		list2015.add(new Long(1451520000));
		map.put("2015", list2015);
		
		List<Long> list2016 = new ArrayList<Long>();
		list2016.add(new Long(1451606400));
		list2016.add(new Long(1483142400));
		map.put("2016", list2016);

		List<Long> list2017 = new ArrayList<Long>();
		list2017.add(new Long(1483228800));
		list2017.add(new Long(1514678400));
		map.put("2017", list2017);
		
		return map;

	}

	private static void createCSVFile(String pathReal, String pathFake) throws Exception {
		List<String> listFakeNews = new ArrayList<String>();
		List<String> listRealNews = new ArrayList<String>();
		
		if(pathReal.equals("g1_rss") || pathReal.equals("blogsFolha")) {
			final File folderG1 = new File(pathReal);
			List<File> listFilesG1 = listFilesForFolder(folderG1);
			//reais old G1
			for (File f : listFilesG1) {
				String textFile = readFile(f);
				if(!textFile.equals("")) {
					String textNews = extractNewsFromDocument(textFile);
					textNews = formatToCSV(textNews);
					listRealNews.add(textNews);
				}
				
			}
		}else if(pathReal.equals("noticias_esporte") || pathReal.equals("esporte_economia")) {
			//esportes
			final File folderEsporte = new File(pathReal);
			List<File> listFilesEsporte = listFilesForFolder(folderEsporte);
			for (File f : listFilesEsporte) {
				String textFile = readFile(f);
				textFile = formatToCSV(textFile);
				listRealNews.add(textFile);
			}
		}else if(pathReal.equals("realPolitical")) {
			ArrayList<List<String>> listOfLists = extractRealNews();
			//seed = 3
//			shuffleList(listOfLists.get(0), 3);
			shuffleList(listOfLists.get(1), 3);
//			shuffleList(listOfLists.get(2), 3);
			
//			List<String> listRealNewsEstadao = listOfLists.get(0).subList(0, 4999);
//			List<String> listRealNewsFolha = listOfLists.get(1).subList(0, 4999);
//			List<String> listRealNewsG1 = listOfLists.get(2).subList(0, 4999);
			
//			List<String> listRealNewsEstadao = listOfLists.get(0).subList(0, 1458);
//			List<String> listRealNewsFolha = listOfLists.get(1).subList(0, 1458);
//			List<String> listRealNewsG1 = listOfLists.get(2).subList(0, 1458);
			
			//List<String> listRealNewsEstadao = listOfLists.get(0).subList(5000, 5100);
			//List<String> listRealNewsFolha = listOfLists.get(1).subList(5000, 5100);
			//List<String> listRealNewsG1 = listOfLists.get(2).subList(5000, 5100);
			
//			List<String> listRealNewsEstadao = listOfLists.get(0).subList(5000, 5024);
//			List<String> listRealNewsFolha = listOfLists.get(1).subList(5000, 5024);
//			List<String> listRealNewsG1 = listOfLists.get(2).subList(5000, 5023);
			
//			List<String> listRealNewsEstadao = listOfLists.get(0);
			List<String> listRealNewsFolha = listOfLists.get(1);
//			List<String> listRealNewsG1 = listOfLists.get(2);
			
			
//			listRealNews.addAll(listRealNewsEstadao);
			listRealNews.addAll(listRealNewsFolha);
//			listRealNews.addAll(listRealNewsG1);
			
			System.out.println(listRealNews.size());
		}else {
			System.out.println("Sem noticias reais no output...");
		}
		
		if(pathFake.equals("2017-11-16 22_14_08.968") || pathFake.contains("diarioPernambucano") || 
				pathFake.contains("sensacinalista")) {
			//satiras
			final File folder = new File(pathFake);
			List<File> listFiles = listFilesForFolder(folder);
			for (File f : listFiles) {
				String textFile = readFile(f);
				String textNews = extractNewsFromDocument(textFile);
				textNews = formatToCSV(textNews);
				listFakeNews.add(textNews);
				
			}
		}else if(pathFake.equals("fake_news_br") || pathFake.equals("fake_usp")
				|| pathFake.equals("Fake_horne") || pathFake.equals("Satire_horne")
				|| pathFake.equals("Real_horne") || pathFake.equals("top_fake_2016_buzzfeed") 
				|| pathFake.equals("top_fake_2017_buzzfeed")) {
			//fake genuina
			final File folder = new File(pathFake);
			List<File> listFiles = listFilesForFolder(folder);
			for (File f : listFiles) {
				String textFile = readFile(f);
				textFile = formatToCSV(textFile);
				listFakeNews.add(textFile);
			}
		}else {
			System.out.println("Sem noticias falsas no output...");
		}
		

		//stripAccents(listFakeNews);
		//stripAccents(listRealNews);
		
		
		
		try {
			writeCSV(listFakeNews, listRealNews, "diario_brasil_virgula.csv");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	private static void splitBodyOrTitleText(List<String> listRealNews, List<String> listFakeNews) {

		for(int i = 0; i < listRealNews.size(); i++) {
			String titleReal = listRealNews.get(i).split("----")[0];
			String titleFake = listFakeNews.get(i).split("----")[0];
			String bodyReal = listRealNews.get(i).split("----")[1];
			String bodyFake = listFakeNews.get(i).split("----")[1];
			listRealNews.set(i, titleReal + "<sep>" + bodyReal);
			listFakeNews.set(i, titleFake + "<sep>" + bodyFake);
		}
		
	}

	private static void newsBalancing(List<String> list_real_news, 
			List<String> list_fake_news) {
		List<String> list_real_news_processed = new ArrayList<String>();
		List<String> list_fake_news_processed = new ArrayList<String>();
		
		NewsBalancing balancing = new NewsBalancing();
		balancing.newsAntagonistGenerator(list_real_news, list_fake_news);
		List<Antagonists> antagonists = balancing.balanceAntagonistList();
		
		//only for text body here
		for(Antagonists ant: antagonists) {
			list_real_news_processed.add(ant.getRealText());
			list_fake_news_processed.add(ant.getFakeText());
		}
		list_real_news.clear();
		list_real_news.addAll(list_real_news_processed);
		list_fake_news.clear();
		list_fake_news.addAll(list_fake_news_processed);
		
	}
	
	static void shuffleList(List<String> ar, int seed){
		Collections.shuffle(ar, new Random(seed));
	  }
	
	public static void stripAccents(List<String> list) {
		List<String> newList = new ArrayList<String>();
		for (int i = 0; i < list.size(); i++) {
			list.set(i, Normalizer.normalize(list.get(i), Normalizer.Form.NFD).replaceAll("[\\p{InCombiningDiacriticalMarks}]", ""));
		}
		
		
//		for(String s: list) {
//			s = Normalizer.normalize(s, Normalizer.Form.NFD);
//		    s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
//		    newList.add(s);
//		}
//	    return newList;
	}
	 

}
