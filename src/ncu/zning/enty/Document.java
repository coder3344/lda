package ncu.zning.enty;


/**
 * @author zning
 * <p>Note: Each word is represented by its global id 
 */
public class Document {
	private int[] words;
	/**
	 * real length of the document
	 */
	private int length;
	public Document(int length){
		this.words=new int[length];
		this.length=length;
	}
	
	public Document(){
		this.words=null;
		this.length=-1;
	}
	
	public void addWord(int idx,int word){
		if(idx<this.length-1){
			this.words[idx]=word;
		}
	}
	
	public void scaleLength(int len){
		this.length=len;
	}

	public int[] getWords() {
		return words;
	}

	public void setWords(int[] words) {
		this.words = words;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}
}
