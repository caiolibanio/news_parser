package sensacionalista;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
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

public class ParseNewsSensacionalista {
	
	private BufferedWriter bw;
	
	private List<String> globalList = new ArrayList<String>();
	
	public List<String> extractAllLinks(String path, List<String> globalList) throws IOException {
		List<String> listLinks = new ArrayList<String>();
		
		Document doc =getUrlData(path);
		Elements body = doc.select("div[class=item-details").select("a");
		
		for (Element link: body) {
			String url = link.attr("href");
			if (!globalList.contains(url) && !listLinks.contains(url)) {
				listLinks.add(url);
			}
			
		}
		return listLinks;
		
		
	}
	
	private String createFolder(String caderno) {
		String timeStamp = generateTimeStamp();
		String folderName = "sensacinalista_" + caderno;
		
		if (!new File(folderName).exists()) { // Verifica se o diret�rio existe.   
             (new File(folderName)).mkdirs();   // Cria o diret�rio   
         }
		return folderName;
	}
	
	private String generateTimeStamp() {
		String timeStamp = new Timestamp(new Date().getTime()).toString();
		return timeStamp;
	}
	
	private File createFile(String folderName, String year, String caderno) throws IOException {
		Random r = new Random();
		int name = r.nextInt(100000000);
		
		String fileName = System.getProperty("user.dir") + "/" + folderName + "/" + name + "_" + caderno + "_" + year + ".html";
		File file = new File( fileName );
		file.createNewFile();

		bw = new BufferedWriter
				(new OutputStreamWriter(new FileOutputStream(fileName),"UTF-8"));
		return file;
	}
	
	public void parseNews(String baseLink, int minPages, int maxPages, String caderno) {
		List<String> all_pages = generateUrls(baseLink, minPages, maxPages);
		String folderName = createFolder(caderno);
		String actual_page = "";
		try {
			for(String page : all_pages) {
				actual_page = page;
				System.out.println("Processing page: " + actual_page);
				File file = null;
				List<String> links = null;
				try {
					links = extractAllLinks(page, globalList);
				} catch (Exception e) {
					System.err.println("Something goes wrong in page: " + page);
					System.out.println(e);
					Date date = new Date();
					System.out.println(date);
				}
				
				globalList.addAll(links);
				for (String link : links) {
					try {
						Document doc = getUrlData(link);
						String year = link.split("/")[3];
						file = createFile(folderName, year, caderno);
						String news = writeNewsOnFile(doc, link);
						bw.close();
					} catch (Exception e) {
						if(file.exists()) {
							file.delete();
						}
						System.err.println("Something goes wrong in link: " + link);
						System.out.println(e);
						Date date = new Date();
						System.out.println(date);
					}
					
				}
			}
		} catch (Exception e) {
			System.err.println("Something goes wrong in page: " + actual_page);
			System.out.println(e);
			Date date = new Date();
			System.out.println(date);
		}
		

	}
	
	private Document getUrlData(String link) throws IOException {
		Document doc;
		doc = Jsoup.connect(link).get();
		return doc;
	}
	
	private String writeNewsOnFile(Document doc, String link) throws IOException {
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
	
	private void writeHeaderNews(String link, Document doc) throws IOException {
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
	
	private void writeHeader() throws IOException {
		bw.write( "<html>" );
		bw.newLine();
		bw.write( "<meta http-equiv=" + "\""+"Content-Type"+"\"" + " content=" + "\""+"text/html;charset=UTF-8"+"\"" + " />");
		bw.newLine();
		bw.write( "<body>");
		bw.newLine();
		bw.newLine();
		
	}
	
	private Elements removePClassTag(Elements newsHeadlines) {
		Elements aux = new Elements();
		for(int i = 0; i < newsHeadlines.size(); i++){
			if(! newsHeadlines.get(i).toString().contains("<p class=") && 
					! newsHeadlines.get(i).toString().contains("published")){
				aux.add(newsHeadlines.get(i));
			}
		}
		return aux;
	}
	
	private List<String> generateUrls(String baseUrl, int minPages, int max){
		List<String> linksList = new ArrayList<String>();
		linksList.add(baseUrl);
		
		for (int i = minPages; i <= max; i++) {
			String increment = "page/" + i + "/";
			String newLink = baseUrl + increment;
			linksList.add(newLink);
		}
		return linksList;
	}
	
	public static void main(String args[]) throws IOException {
		
		ParseNewsSensacionalista c = new ParseNewsSensacionalista();
		//String baseUrl = "https://www.sensacionalista.com.br/pais/";
		
//		String baseUrl = "https://www.sensacionalista.com.br/entretenimento/";
//		String baseUrl = "https://www.sensacionalista.com.br/esportes/";
//		String baseUrl = "https://www.sensacionalista.com.br/mundo/";
		String baseUrl = "https://www.sensacionalista.com.br/comportamento-2/";
		
		c.parseNews(baseUrl, 2, 74, baseUrl.split("/")[3]);
		
		
		
		
	}

}
