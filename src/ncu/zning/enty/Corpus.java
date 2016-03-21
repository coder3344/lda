package ncu.zning.enty;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.log4j.Logger;

/**
 * <p>Load the input documents of corpus<p>
 * <p>Note: Regard each line of the file in input corpus as a document
 * @author zning
 */
public class Corpus {
	private static Logger logger=Logger.getLogger(Corpus.class);
	
	private Map<String,Integer> word2idMap;
	private Map<Integer,String> id2wordMap;
	private List<Document> documents;
	public Corpus(){
		this.word2idMap=new HashMap<String,Integer>();
		this.id2wordMap=new HashMap<Integer,String>();
		this.documents=new ArrayList<Document>();
	}
	
	/**
	 * Load the input corpus
	 * @param filePath input file path or directory path
	 */
	public void loadCorpus(String filePath){
		File input=new File(filePath);		
		if(input.isDirectory()){
			Stack<File> dirStack=new Stack<File>();
			dirStack.add(input);
			while(!dirStack.isEmpty()){
				File tmp=dirStack.pop();
				File[] files=tmp.listFiles();
				for( File f : files){
					if(f.isDirectory()){
						dirStack.add(f);
					}else{
						this.loadFile(f);
					}
				}
			}
		}else{
			this.loadFile(input);
		}
	}
	private void loadFile(File file){
		if(file==null){
			logger.info("Null file can't not be loadded!");
			return;
		}
		try{
			FileInputStream in=new FileInputStream(file);
			BufferedReader reader=new BufferedReader(new InputStreamReader(in,"utf-8"));
			String line=null;
			while((line=reader.readLine())!=null){
				line=line.trim();
				String[] words=line.split("[ \\t]");
				if(words.length==0)
					continue;
				Document document=new Document(words.length);
				int curIdx=0;
				for(String word : words){
					if(word.trim().length()==0)   continue;
					Integer wordIdx=this.word2idMap.get(word);
					if(wordIdx==null){
						wordIdx=this.word2idMap.size();
						this.word2idMap.put(word, wordIdx);
						this.id2wordMap.put(wordIdx, word);						
					}
					document.addWord(curIdx,wordIdx);
					curIdx+=1;
				}
				document.scaleLength(curIdx);
				this.documents.add(document);
			}
		}catch(IOException ex){
			logger.error("ERROR while load file:"+file.getAbsolutePath()+"\n",ex);
		}
	}
	
	public Map<String, Integer> getWord2idMap() {
		return word2idMap;
	}

	public void setWord2idMap(Map<String, Integer> word2idMap) {
		this.word2idMap = word2idMap;
	}

	public Map<Integer, String> getId2wordMap() {
		return id2wordMap;
	}

	public void setId2wordMap(Map<Integer, String> id2wordMap) {
		this.id2wordMap = id2wordMap;
	}

	public List<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}
}
