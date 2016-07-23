package parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Timer;
import java.util.TimerTask;


public class Main {
	Timer timer;

	private ParseNews parseNews;
	
	/**
	 * Numero de vezes que o parser deverá analisar os links em busca de novas mensagens
	 */
	private int countCicles = 1;

	public Main(int seconds) {
		timer = new Timer();
		timer.schedule(new RemindTask(),
				0,        //initial delay
				seconds*1000);  //subsequent rate
	}

	class RemindTask extends TimerTask {
		public void run() {
			System.out.println("Current count is: " + countCicles);
			if(countCicles > 0){
				parseNews = new ParseNews();
				try {
					parseNews.parseNews("base_links.txt");
					countCicles--;
				} catch (IOException e) {
					System.err.println("Critical Error!");
					e.printStackTrace();
				}
			}else{
				timer.cancel(); //Terminate the timer thread
			}

		}
	}

	public static void main(String args[]) {
		new Main(5); //Time between executions (in seconds)
		System.out.println("Task scheduled.");
	}
}