package parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.CopyOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.io.FileUtils;

/**
 * Simple demo that uses java.util.Timer to schedule a task 
 * to execute once 5 seconds have passed.
 */

public class Teste {
	public static void main(String args[]) throws IOException{
		
		
//		//2015-04-21 15_05_36.926
//		//2015-04-21 17_37_16.971
//		//2015-05-18 10_35_50.55
		String pathGeosen = "C:\\Users\\Caio\\Desktop\\geosen_novos_exp\\1000\\STAnalizerStat.txt\\";
		String pathTrained = "C:\\Users\\Caio\\Desktop\\geosen_novos_exp\\10000\\STAnalizerStat.txt\\";
		String destFile = "C:\\Users\\Caio\\Desktop\\geosen_novos_exp\\result_p1_p1_advance.txt";
		
		String geosenCol = "C:\\Users\\Caio\\Desktop\\geosen_novos_exp\\1000\\crawl\\colored_texts\\";
		String geosenColTrained = "C:\\Users\\Caio\\Desktop\\geosen_novos_exp\\10000\\crawl\\colored_texts\\";

		File geosen = new File(pathGeosen);
		File geosenTrained = new File(pathTrained);
		File dest = new File(destFile);

		dest.createNewFile();

		String lineGeo = "";
		String lineTrained = "";
		String content = "";

		BufferedReader brGeo = new BufferedReader(new InputStreamReader(
				new FileInputStream(geosen), "UTF-8"));



		for(int i = 0; i < 1000; i++){
			
			BufferedReader brTrained = new BufferedReader(new InputStreamReader(
					new FileInputStream(geosenTrained), "UTF-8"));
			
			if((lineGeo = brGeo.readLine()) != null){
				for(int j = 0; j < 1000; j++){
					if((lineTrained = brTrained.readLine()) != null){
						String [] lineGeoArray = lineGeo.split(" ");
						String [] lineTrainedArray = lineTrained.split(" ");

						if(lineGeoArray.length == 10 && lineTrainedArray.length == 10){
							if((lineGeoArray[1].equals(lineTrainedArray[1]))){
								
								if(! lineGeoArray[5].equals(lineTrainedArray[5]) ||
									! lineGeoArray[9].equals(lineTrainedArray[9])){
									
									String[] array = getLinkColored(lineGeoArray[1], geosenCol, geosenColTrained);
									content += "LINK 1000: " + array[0] + " | " + "Accepted: " + lineGeoArray[5] + " | Ignored: " + lineGeoArray[9] + System.lineSeparator() + "LINK 10000: " + array[1] + 
											" | Accepted: " + lineTrainedArray[5] + " | Ignored: " + lineTrainedArray[9] + System.lineSeparator() + "------------------------------------------------------------" + 
											System.lineSeparator() + System.lineSeparator();
									break;
								}

								
							}
						}


					}	
				}
			}
		}



		File fileToWrite = new File(dest.getAbsolutePath());
		fileToWrite.createNewFile();

		FileWriter fw = new FileWriter(fileToWrite.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(content);
		bw.close();

	}

	private static String[] getLinkColored(String name, String geosenCol, String geosenColTrained) {
		
		name = name.split("/")[ name.split("/").length - 1];
		File fileGeosenCol = new File(geosenCol);
		File fileGeosenTrained = new File(geosenColTrained);
		
		File[] geoCol = fileGeosenCol.listFiles();
		File[] geoColTrained = fileGeosenTrained.listFiles();
		
		File geoOut = null;
		File geoTrainedOut = null;
		
		String[] array = new String[2];
		
		for(File file : geoCol){
			if(file.getName().equals(name)){
				geoOut = file;
				break;
			}
		}
		
		for(File file : geoColTrained){
			if(file.getName().equals(name)){
				geoTrainedOut = file;
				break;
			}
		}
		
		array[0] = geoOut.getAbsolutePath();
		array[1] = geoTrainedOut.getAbsolutePath();
		return array;
		
	}

	private static void copyFile(String source, String destination){
		File sourceFile = new File(source);
		File dest = new File(destination);
		try {
			FileUtils.copyDirectory(sourceFile, dest);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//	public static void main(String args[]){
	//		System.out.println(isCep("58400-490"));
	//	}
	//	
	//	private static boolean isTelefone(String numeroTelefone) {
	//        return numeroTelefone.matches(".((10)|([1-9][1-9]).)\\s9?[6-9][0-9]{3}-?[0-9]{4}") ||
	//                numeroTelefone.matches(".((10)|([1-9][1-9]).)\\s[2-5][0-9]{3}-?[0-9]{4}") ||
	//                numeroTelefone.matches(".((10)|([1-9][1-9]).)9?[6-9][0-9]{3}-?[0-9]{4}") ||
	//                numeroTelefone.matches(".((10)|([1-9][1-9]).)[2-5][0-9]{3}-?[0-9]{4}") ||
	//                numeroTelefone.matches("\\d{4,4}-\\d{4,4}") ||
	//                numeroTelefone.matches("\\d{8,8}") ||
	//                numeroTelefone.matches("\\d{9,9}");
	//    }
	//	
	//	private static boolean isCep(String cep){
	//		return cep.matches("\\d{5,5}-\\d{3,3}");
	//	}


}