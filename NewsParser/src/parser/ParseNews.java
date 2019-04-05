package parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

public class ParseNews {

	private BufferedWriter bw;
	
	private DAO dao;

	public void parseNews(String path) throws IOException {
		//Lendo arquivo com as fontes do rss
		File baseLinks = new File( path );
		FileReader fr = new FileReader(baseLinks);
		BufferedReader br = new BufferedReader(fr);
		
		String timeStamp = generateTimeStamp();
		String folderName = timeStamp.replace(":", "_");
		
		if (!new File(folderName).exists()) { // Verifica se o diret�rio existe.   
             (new File(folderName)).mkdirs();   // Cria o diret�rio   
         }  
		
		
		while (br.ready()) {
			String linha = br.readLine();
			Document docXml;
			try {
				docXml = Jsoup.connect(linha).get();
			} catch (IOException e1) {
				e1.printStackTrace();
				System.err.println("N�o foi poss�vel se conectar a URL");
				continue;
			}
//			writeHeaderGroup(linha);
			String s = docXml.toString();
			Document docXmlToParse = Jsoup.parse(s, "", Parser.xmlParser());

			Elements linksFromXml = docXmlToParse.select("link");
			List<String> listLinks = new ArrayList<String>();

			for(Element e : linksFromXml){
				String link = e.text();
				listLinks.add(link.split(" ")[0]);
			}
			//listLinks.removeAll(Arrays.asList("http://g1.globo.com"));
			listLinks.removeAll(Arrays.asList("http://globoesporte.globo.com"));
			for(String link : listLinks){
				
				String fileName = createFile(folderName);
				
				try {
					parseNewsText(link, linha, fileName, timeStamp);
				} catch (SQLException | IOException e1) {
					System.err.println("Erro ao adicionar novo registro.");
					e1.printStackTrace();
				}
				bw.close();
			}
			
		}
		br.close();
	}

	private String createFile(String folderName) throws IOException {
		Random r = new Random();
		int name = r.nextInt(100000000);
		
		String fileName = System.getProperty("user.dir") + "/" + folderName + "/" + name + ".html";
		File file = new File( fileName );
		file.createNewFile();

		bw = new BufferedWriter
				(new OutputStreamWriter(new FileOutputStream(fileName),"UTF-8"));
		return fileName;
	}

	private String generateTimeStamp() {
		String timeStamp = new Timestamp(new Date().getTime()).toString();
		return timeStamp;
	}

	private String extractUfFromRSSLink(String linha) {
		return linha.split("/")[4];
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

	private void parseNewsText(String link, String linha, String fileName, String timeStamp) throws SQLException, IOException{
		dao = new DAO();
		try {
			Document doc = getUrlData(link);
			String news = writeNewsOnFile(doc, link);
			insertNewsOnDB(linha, fileName, link, timeStamp, news, dao, doc);
			
		} catch (SQLException | IOException e) {
			if(dao.getStmt() != null && !dao.getStmt().isClosed()){
				dao.getStmt().close();
			}
			if(dao.getC() != null && !dao.getC().isClosed()){
				dao.getC().rollback();
				dao.getC().close();
			}
			deleteFile(fileName);
//			e.printStackTrace();
		}
		
		
	}

	private void insertNewsOnDB(String linha, String fileName, String link, String timeStamp, 
			String news, DAO dao, Document doc) throws SQLException {
		String uf = extractUfFromRSSLink(linha).toUpperCase();
		
		
		String[] array = fileName.split("/");
		
		String name = array[array.length - 1].split("\\.")[0];
		
		System.out.println(link);
		News newsObj = new News(timeStamp, link, getNewsTitle(doc), news, name, dao.getStateIdByUf(uf));
		
		dao.insert(newsObj);
		
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

	private Document getUrlData(String link) throws IOException {
		Document doc;
		doc = Jsoup.connect(link).get();
		return doc;
	}

	private void deleteFile(String fileName) throws IOException {
		File file = new File(fileName);
		if(file.exists()){
			bw.close();
			file.delete();
		}
		
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
		String titleString = titleElement.text().split(" - ")[1];
		return titleString;
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

}
