package sensacionalista;

import java.util.ArrayList;
import java.util.List;

public class RealFakeAntagonistCandidates {

	private String fakeText;
	
	private List<String> realNewsCandidates = new ArrayList<String>();

	private int matches;
	
	public RealFakeAntagonistCandidates() {
		super();
		this.fakeText = fakeText;
		this.realNewsCandidates = realNewsCandidates;
		this.matches = matches;
	}

	public String getFakeText() {
		return fakeText;
	}

	public void setFakeText(String fakeText) {
		this.fakeText = fakeText;
	}

	public int getMatches() {
		return matches;
	}

	public void setMatches(int matches) {
		this.matches = matches;
	}

	public List<String> getRealNewsCandidates() {
		return realNewsCandidates;
	}

	public void setRealNewsCandidates(List<String> realNewsCandidates) {
		this.realNewsCandidates = realNewsCandidates;
	}

	
	
	
}
