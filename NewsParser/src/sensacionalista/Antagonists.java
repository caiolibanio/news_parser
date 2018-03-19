package sensacionalista;

public class Antagonists {
	
	private String fakeText;
	
	private String realText;
	
	private int matches;
	
	

	public Antagonists(String fakeText, String realText, int matches) {
		super();
		this.fakeText = fakeText;
		this.realText = realText;
		this.matches = matches;
	}

	public String getFakeText() {
		return fakeText;
	}

	public void setFakeText(String fakeText) {
		this.fakeText = fakeText;
	}

	public String getRealText() {
		return realText;
	}

	public void setRealText(String realText) {
		this.realText = realText;
	}

	public int getMatches() {
		return matches;
	}

	public void setMatches(int matches) {
		this.matches = matches;
	}
	
	

}
