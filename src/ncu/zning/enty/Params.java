package ncu.zning.enty;

/**
 * @author zning
 *
 */
public class Params {
	private double alpha;
	private double beta;
	/**
	 * counts of topics
	 */
	private int K;
	/**
	 * counts of words in document
	 */
	private int V;
	/**
	 * counts of documents in corpus
	 */
	private int M;
	/**
	 * Gibbs iteration count
	 */
	private int inters;
	private int saveStepStart;
	private int saveInteval;
	/**
	 * the top words count in each topic to be saved
	 */
	private int topWords;
	
	private String corpusPath;
	private String savePath;
	
	public Params(){
		this.alpha=0;
		this.beta=0;
		this.K=0;
		this.V=-1;
		this.M=-1;
		this.inters=-1;
		this.saveInteval=-1;
		this.saveStepStart=-1;
		this.topWords=-1;
		this.savePath=null;
		this.corpusPath=null;
	}
	
	public double getAlpha() {
		return alpha;
	}
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}
	public double getBeta() {
		return beta;
	}
	public void setBeta(double beta) {
		this.beta = beta;
	}
	public int getK() {
		return K;
	}
	public void setK(int k) {
		K = k;
	}
	public int getV() {
		return V;
	}
	public void setV(int v) {
		V = v;
	}
	public int getM() {
		return M;
	}
	public void setM(int m) {
		M = m;
	}
	public int getInters() {
		return inters;
	}
	public void setInters(int inters) {
		this.inters = inters;
	}
	public int getSaveStepStart() {
		return saveStepStart;
	}
	public void setSaveStepStart(int saveStepStart) {
		this.saveStepStart = saveStepStart;
	}
	public int getSaveInteval() {
		return saveInteval;
	}
	public void setSaveInteval(int saveInteval) {
		this.saveInteval = saveInteval;
	}
	public int getTopWords() {
		return topWords;
	}
	public void setTopWords(int topWords) {
		this.topWords = topWords;
	}
	public String getCorpusPath() {
		return corpusPath;
	}
	public void setCorpusPath(String corpusPath) {
		this.corpusPath = corpusPath;
	}
	public String getSavePath() {
		return savePath;
	}
	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}
	
}
