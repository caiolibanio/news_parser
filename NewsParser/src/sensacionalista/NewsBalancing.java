package sensacionalista;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NewsBalancing {
	
	private int threshold = 1;
	
	private List<RealFakeAntagonistCandidates> listAntagonists = new ArrayList<RealFakeAntagonistCandidates>();
	
	public void newsAntagonistGenerator(List<String> listReal, List<String> listFake) {
		
		for(int i = 0; i < listFake.size(); i++){
			String title = listFake.get(i).split("----")[0];
			String[] array_title = title.split(" ");
			List<String> listCapWordsTitle = removeNonCapsWords(array_title);
			RealFakeAntagonistCandidates antagonist = null;
			for(int j = 0; j < listReal.size(); j++) {
				String body = listReal.get(j).split("----")[0];
				String[] array_body = body.split(" ");
				List<String> listCapWordsBody = removeNonCapsWords(array_body);
				int occurrences = 0;
				for(int k = 0; k < listCapWordsTitle.size(); k++){
					occurrences += Collections.frequency(listCapWordsBody, listCapWordsTitle.get(k));
				}
//				if(listCapWordsTitle.contains("Xbox") && listCapWordsBody.contains("Atibaia")) {
//					System.out.println();
//				}
				
				if(occurrences >= threshold) {
					if(antagonist == null) {
						antagonist = new RealFakeAntagonistCandidates();
						antagonist.setFakeText(listFake.get(i));
						antagonist.getRealNewsCandidates().add(listReal.get(j));
					}else {
						antagonist.getRealNewsCandidates().add(listReal.get(j));
					}
					
				}
			}
			if(antagonist != null) {
				listAntagonists.add(antagonist);
			}
		}
	}
	
	private List<String> removeNonCapsWords(String[] listOfWords) {
		List<String> listProcessed = new ArrayList<String>();
		for(String word: listOfWords) {
			String proc_word = word.replaceAll("\"", "").replaceAll("“", "").replaceAll("”", "").replaceAll(":", "");
			if(proc_word.length() > 2 && Character.isUpperCase(word.charAt(0)) == true) {
				listProcessed.add(word);
			}
		}
		return listProcessed;
	}
	
	public List<Antagonists> balanceAntagonistList() {
		List<Antagonists> antagonists = new ArrayList<Antagonists>();
		
		for(RealFakeAntagonistCandidates ant: listAntagonists) {
			String titleFake = ant.getFakeText().split("----")[0];
			String[] arrayTitleFake = titleFake.split(" ");
			List<String> listCapWordsTitle = removeNonCapsWords(arrayTitleFake);
			int occurrencesOfTopindex = 0;
			String topNews = null;
			for(String fullNews: ant.getRealNewsCandidates()) {
				String body = fullNews.split("----")[1];
				String[] array_body = body.split(" ");
				List<String> listCapWordsBody = removeNonCapsWords(array_body);
				int occurrences = 0;
				for(String titleWord: listCapWordsTitle) {
					occurrences += Collections.frequency(listCapWordsBody, titleWord);
				}
				if(occurrences > occurrencesOfTopindex) {
					occurrencesOfTopindex = occurrences;
					topNews = fullNews;
				}
				
			}
			if(topNews != null) {
				Antagonists newAnt = new Antagonists(ant.getFakeText(), topNews, occurrencesOfTopindex);
				antagonists.add(newAnt);
			}
		}
		List<Antagonists> bestAntagonists = removingAmbiguities(antagonists);
		return bestAntagonists;
	}
	
	private List<Antagonists> removingAmbiguities(List<Antagonists> antagonists){
		List<Antagonists> antListProcessed = new ArrayList<Antagonists>();
		List<Antagonists> antListTemp = new ArrayList<Antagonists>();
		for(Antagonists ant: antagonists) {
			List<Antagonists> antListAmbiguities = findAntagonists(antagonists, ant.getFakeText(), ant.getRealText());
			antListAmbiguities.removeAll(antListTemp);
			int topMatch = 0;
			Antagonists bestAnt = null;
			for(Antagonists antMatched: antListAmbiguities) {
				if(antMatched.getMatches() > topMatch) {
					topMatch = antMatched.getMatches();
					bestAnt = antMatched;
				}
			}
			
			if(bestAnt != null) {
				antListProcessed.add(bestAnt);
				antListTemp.addAll(antListAmbiguities);
			}
			
		}
		return antListProcessed;
	}
	
	private List<Antagonists> findAntagonists(List<Antagonists> antagonists, String textFake, String textReal){
		List<Antagonists> antList = new ArrayList<Antagonists>();
		for(Antagonists ant: antagonists) {
			if(ant.getFakeText().equals(textFake) || ant.getRealText().equals(textReal)) {
				antList.add(ant);
			}
		}
		return antList;
	}

	public int getThreshold() {
		return threshold;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

	public List<RealFakeAntagonistCandidates> getListAntagonists() {
		return listAntagonists;
	}

	public void setListAntagonists(List<RealFakeAntagonistCandidates> listAntagonists) {
		this.listAntagonists = listAntagonists;
	}

}
