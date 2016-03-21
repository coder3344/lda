package ncu.zning.enty;

/**
 * used for sorting top probability words in each topic
 * @author zning
 *
 */
public class TopicWordSort implements Comparable<TopicWordSort> {
	private int wordId;
	private double probability;
	public TopicWordSort(int id,double p){
		this.wordId=id;
		this.probability=p;
	}
	
	@Override
	public int compareTo(TopicWordSort arg0) {
		// TODO Auto-generated method stub
		if(this.probability>arg0.getProbability()){
			return 1;
		}else if(this.probability==arg0.getProbability()){
			return 0;
		}else{
			return -1;
		}
	}

	public int getWordId() {
		return wordId;
	}
	public void setWordId(int wordId) {
		this.wordId = wordId;
	}
	public double getProbability() {
		return probability;
	}
	public void setProbability(double probability) {
		this.probability = probability;
	}
}
