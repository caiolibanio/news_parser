package parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import sensacionalista.ParseNewsSensacionalista;

public class MainBlogsFolha {
	
	private static BufferedWriter bw;
	
	private static List<String> globalList = new ArrayList<String>();
	
	public static void parseNews(String baseLink, int minPages, int maxPages, String blogName) {
		List<String> all_pages = generateUrls(baseLink, minPages, maxPages);
		String folderName = createFolder(blogName);
		String actual_page = "";
		try {
			for(String page : all_pages) {
				actual_page = page;
				System.out.println("Processing page: " + actual_page);
				List<String> links = extractAllLinks(page, globalList);
				globalList.addAll(links);
				for (String link : links) {
					Document doc = getUrlData(link);
					String year = link.split("/")[3];
					createFile(folderName, year);
					String news = writeNewsOnFile(doc, link);
					bw.close();
				}
			}
		} catch (Exception e) {
			System.err.println("Something goes wrong in page: " + actual_page);
			System.out.println(e);
			Date date = new Date();
			System.out.println(date);
		}
		

	}
	
	public static List<String> extractAllLinks(String path, List<String> globalList) throws IOException {
		List<String> listLinks = new ArrayList<String>();
		
		Document doc =getUrlData(path);
		Elements body = doc.select("h2[class=c-content-head__title]").select("a");
		
		for (Element link: body) {
			String url = link.attr("href");
			if (!globalList.contains(url) && !listLinks.contains(url)) {
				listLinks.add(url);
			}
			
		}
		return listLinks;
		
		
	}
	
	private static String createFolder(String blogName) {
		String timeStamp = generateTimeStamp();
		String folderName = "blogsFolha/" + timeStamp.replace(":", "_");
		folderName = folderName + "_" + blogName;
		if (!new File(folderName).exists()) { // Verifica se o diret�rio existe.   
             (new File(folderName)).mkdirs();   // Cria o diret�rio   
         }
		return folderName;
	}
	
	private static String generateTimeStamp() {
		String timeStamp = new Timestamp(new Date().getTime()).toString();
		return timeStamp;
	}
	
	private static String createFile(String folderName, String year) throws IOException {
		Random r = new Random();
		int name = r.nextInt(100000000);
		
		String fileName = System.getProperty("user.dir") + "/" + folderName + "/" + name + "_" + year + ".html";
		File file = new File( fileName );
		file.createNewFile();

		bw = new BufferedWriter
				(new OutputStreamWriter(new FileOutputStream(fileName),"UTF-8"));
		return fileName;
	}
	
	private static Document getUrlData(String link) throws IOException {
		Document doc;
		doc = Jsoup.connect(link).get();
		return doc;
	}
	
	private static String writeNewsOnFile(Document doc, String link) throws IOException {
		Elements newsHeadlines = doc.select("p");
		Elements listProcessed = removePClassTag(newsHeadlines);
		String news = "<text>";
		for(Element el: listProcessed ){
			news += el.text() + System.getProperty("line.separator");
		}
		
		writeHeader();
		
		writeHeaderNews(link, doc);
		
		news += "</text>";
		bw.write( news );
		bw.newLine();
		bw.write( "</body>" );
		bw.newLine();
		bw.write( "</html>" );
		return news;
		
	}
	
	private static void writeHeaderNews(String link, Document doc) throws IOException {
		bw.write( "<link>" + link + "</link>" );
		bw.newLine();
		bw.write( "<text>" + getNewsTitle(doc) + "</text>" );
		bw.newLine();
		bw.newLine();
		
	}
	
	private static String getNewsTitle(Document doc){
		Elements titleElement = doc.select("title");
		String titleString = titleElement.text().replace("<title>", "").replace("</title>", "");
		titleString = "<b>" + titleString + "</b>";
		return titleString;
	}
	
	private static void writeHeader() throws IOException {
		bw.write( "<html>" );
		bw.newLine();
		bw.write( "<meta http-equiv=" + "\""+"Content-Type"+"\"" + " content=" + "\""+"text/html;charset=UTF-8"+"\"" + " />");
		bw.newLine();
		bw.write( "<body>");
		bw.newLine();
		bw.newLine();
		
	}
	
	private static Elements removePClassTag(Elements newsHeadlines) {
		Elements aux = new Elements();
		for(int i = 0; i < newsHeadlines.size(); i++){
			if(! newsHeadlines.get(i).toString().contains("<p class=") && 
					! newsHeadlines.get(i).toString().contains("published")){
				aux.add(newsHeadlines.get(i));
			}
		}
		return aux;
	}
	
	private static List<String> generateUrls(String baseUrl, int minPages, int max){
		List<String> linksList = new ArrayList<String>();
		linksList.add(baseUrl);
		
		for (int i = minPages; i <= max; i++) {
			String increment = "page/" + i + "/";
			String newLink = baseUrl + increment;
			linksList.add(newLink);
		}
		return linksList;
	}
	
	public static void main(String[] args) {
		//String baseUrl = "https://hashtag.blogfolha.uol.com.br/";
		
		try (BufferedReader br = new BufferedReader(new FileReader("folhaBlogs.txt"))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	parseNews(line, 2, 1000, line.replaceAll("https://", "")
		    			.replaceAll(".blogfolha.uol.com.br/", ""));
		    }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
