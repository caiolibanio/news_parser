package sensacionalista;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class SensacionalistaMain {

	public static void main(String args[]) throws IOException {
		
		ParseNewsSensacionalista c = new ParseNewsSensacionalista();
		String baseUrl = "https://www.sensacionalista.com.br/pais/";
		
		//c.parseNews(baseUrl, 2, 451);
		
		//Something goes wrong in page: https://www.sensacionalista.com.br/pais/page/441/
		//Thu Nov 16 23:16:24 BRT 2017
		
		String satirePath = "2017-11-16 22_14_08.968";
		String fakeNewsPath = "fake_news_br";
		createCSVFile(satirePath);
		
		
		
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
		String title = text.split("<text> ")[1].replaceAll("<b>", "").replaceAll("</b></text>", "");
		return title;
	}
	
	private static void writeCSV(List<String> listFakeNews, List<String> listRealNews,String csvPath) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(new File(csvPath));
        StringBuilder sb = new StringBuilder();
        sb.append("text");
        sb.append(',');
        sb.append("label");
        sb.append('\n');
        for(String news : listFakeNews) {
        	if(news.length() > 30) {
        		sb.append(news);
                sb.append(',');
                sb.append("FAKE");
                sb.append('\n');
        	}
        }
        
        for(String news : listRealNews) {
        	sb.append(news);
            sb.append(',');
            sb.append("REAL");
            sb.append('\n');
        }

        pw.write(sb.toString());
        pw.close();
	}
	
	private static List<String> extractRealNews(int maxSize) {
		List<String> realNews = new ArrayList<String>();
		
		MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017/news_db"));
		DB database = mongoClient.getDB("news_db");
		DBCollection collection = database.getCollection("estadaoNoticias");
//		DBCollection collection1 = database.getCollection("folhaNoticias");
//		DBCollection collection2 = database.getCollection("g1Noticias");
		
		DBCursor cursor = collection.find();
		int size = 0;
		while (cursor.hasNext() && size < maxSize) {
		   DBObject obj = cursor.next();
		   String news = (String) obj.get("conteudo");
		   String title = (String) obj.get("titulo");
		   if(!news.equals("")) {
			   news = title + "----" + news;
			   news = formatToCSV(news);
			   realNews.add(news);
			   size++;
		   }
		}
		
		//estou colocando um a mais aqui, devido a um BUG!
//		DBCursor cursor = collection1.find(); 
//		int size = 20;
//		while (cursor.hasNext() && size >= 0) {
//		   DBObject obj = cursor.next();
//		   String news = (String) obj.get("conteudo");
//		   if(!news.equals("") && news.length() > 100) {
//			   news = formatToCSV(news);
//			   realNews.add(news);
//			   size--;
//		   }else {
//			   continue;
//		   }
//
//	}
		
//		DBCursor cursor1 = collection2.find();
//		int size1 = 20;
//		while (cursor1.hasNext() && size1 > 0) {
//		   DBObject obj1 = cursor1.next();
//		   String news1 = (String) obj1.get("conteudo");
//		   if(!news1.equals("") && news1.length() > 100) {
//			   news1 = formatToCSV(news1);
//			   realNews.add(news1);
//			   size1--;
//		   }else {
//			   continue;
//		   }
//		}
		System.out.println("tamanho lista: " + realNews.size());
		return realNews;
	}
	
	private static String formatToCSV(String text){
		String textNews = "\"" + text + "\"";
		textNews = textNews.replaceAll("\\r?\\n", "").replaceAll(",", "").replaceAll(";", "");
		return textNews;
	}

	private static void createCSVFile(String path) {
		final File folder = new File(path);
		List<File> listFiles = listFilesForFolder(folder);
		List<String> listFakeNews = new ArrayList<String>();
		List<String> listRealNews = new ArrayList<String>();
		

		listRealNews = extractRealNews(500);
//		listRealNews = listRealNews.subList(5000, 5040);
		
//		for(String news: listRealNewsWithTitle) {
//			listRealNews.add(news.split("----")[0]); //titulo = 0 corpo = 1
//		}
		
		//satiras - CORPO
//		for (File f : listFiles) {
//			String textFile = readFile(f);
//			String textNews = extractNewsFromDocument(textFile);
//			textNews = formatToCSV(textNews);
//			listFakeNews.add(textNews);
//			
//		}
		
		//satiras - TITULO
		for (File f : listFiles) {
			String textFile = readFile(f);
			String textNews = extractNewsFromDocument(textFile);
			textNews = formatToCSV(textNews);
			listFakeNews.add(textNews);
			
		}
		listFakeNews = listFakeNews.subList(0, 500);
		newsBalancing(listRealNews, listFakeNews);
		splitBodyOrTitleText(listRealNews, listFakeNews, "body");
		System.out.println();
		
		
		//fake genuina
//		for (File f : listFiles) {
//			String textFile = readFile(f);
//			textFile = formatToCSV(textFile);
//			listFakeNews.add(textFile);
//		}
		
		try {
			writeCSV(listFakeNews, listRealNews, "csvReal_VS_SatireAntagonists.csv");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	private static void splitBodyOrTitleText(List<String> listRealNews, List<String> listFakeNews, String flag) {
		if(flag.equals("body")) {
			for(int i = 0; i < listRealNews.size(); i++) {
				listRealNews.set(i, listRealNews.get(i).split("----")[1]);
				listFakeNews.set(i, listFakeNews.get(i).split("----")[1]);
			}
		}else {
			for(int i = 0; i < listRealNews.size(); i++) {
				listRealNews.set(i, listRealNews.get(i).split("----")[0]);
				listFakeNews.set(i, listFakeNews.get(i).split("----")[0]);
			}
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
	 

}
