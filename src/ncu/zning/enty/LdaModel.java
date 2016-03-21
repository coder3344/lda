package ncu.zning.enty;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * LDA model enty
 * @author zning
 * 
 */
public class LdaModel {
	
	static Logger logger=Logger.getLogger(LdaModel.class);
	private Params param;
	public Corpus corpus;
	/**
	 * document-topic matrix
	 */
	public double[][] theta;
	/**
	 * topic-word matrix
	 */
	public double[][] phi;
	
	/**
	 * each topic count for a specific document
	 */
	public int[][] nmk;
	/**
	 * each word count for a specific topic
	 */
	public int[][] nkv;
	/** 
	 * row sum of nkm
	 */
	public int[] nmkSum;
	/**
	 * row sum of nkv
	 */
	public int[] nkvSum;
	
	public int M;
	public int V;
	public int K;
	
	
	/**
	 * each word topic asign of each document<p>
	 * len(z)=M<p>
	 * len(z[i])=word count in document i<p>
	 */
	public Vector<Integer>[] Z;
	/**
	 * temp used for sample a new topic<p>
	 * store the new probability of each topic
	 */
	public double[] p;
	
	public LdaModel(Params config){
		this.param=config;		
	}
	/**
	 * Init the model parameters
	 */
	public void init(){
		this.corpus=new Corpus();
		this.corpus.loadCorpus(this.param.getCorpusPath());
		
		this.M=this.corpus.getDocuments().size();
		this.V=this.corpus.getId2wordMap().size();
		this.K=this.param.getK();
		
		logger.info("Document count:"+this.M+" vocabulary:"+this.V);
		int i=0,j=0;
		
		this.nkv=new int[this.K][this.V];		
		for(i=0; i<this.K; i++){
			for(j=0; j<this.V; j++){
				this.nkv[i][j]=0;
			}
		}
		this.nkvSum=new int[this.K];
		for(i=0; i<this.K; i++){
			this.nkvSum[i]=0;
		}
		
		this.nmk=new int[this.M][this.K];
		for(i=0; i<this.M; i++){
			for(j=0; j<this.K; j++){
				this.nmk[i][j]=0;
			}
		}
		this.nmkSum=new int[this.M];
		for(i=0; i<this.M; i++){
			this.nmkSum[i]=0;
		}
		
		this.Z=new Vector[this.M];
		i=0;//document index
		for(Document doc: this.corpus.getDocuments()){
			int docWordCount=doc.getLength();
			this.Z[i]=new Vector<Integer>();
			for(j=0; j<docWordCount; j++){//random a topic for each word in doc i
				int tmpTopic=(int)Math.floor(Math.random()*this.K);
				this.Z[i].add(tmpTopic);
				this.nmk[i][tmpTopic]+=1;
				this.nkv[tmpTopic][doc.getWords()[j]]+=1;
				this.nkvSum[tmpTopic]+=1;
			}
			this.nmkSum[i]+=docWordCount;
			i++;
		}
		this.p=new double[this.K];
		this.theta=new double[this.M][this.K];
		this.phi=new double[this.K][this.V];
	}
	
	
	public void saveModel(String statusMark){
		String saveDir=this.param.getSavePath();
		File dir=new File(saveDir);
		if(!dir.exists()){
			dir.mkdirs();
		}
		boolean r=this.saveTopWordInTopic(saveDir+File.separator+statusMark+".topicWords");
		r=this.saveDocumentTopicAssign(saveDir+File.separator+statusMark+".docTopicAssign");
		r=this.saveWordTopicAssign(saveDir+File.separator+statusMark+".wordTopicAssign");
	}
	private boolean saveWordTopicAssign(String path){
		try{
			FileOutputStream out=new FileOutputStream(new File(path));
			BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(out,"utf-8"));
			int docIdx=0;
			for(Document document: this.corpus.getDocuments()){
				StringBuffer sb=new StringBuffer();
				for(int j=0; j<document.getLength(); j++){
					String word=this.corpus.getId2wordMap().get(document.getWords()[j]);
					sb.append(word+":"+this.Z[docIdx].get(j)+" ");
				}
				writer.write(sb.toString().trim()+"\n");
				docIdx++;
			}
			writer.close();
		}catch(Exception ex){
			logger.error("Exception happend while save word-topic assign file!\n",ex);
			return false;
		}
		logger.info("Save save word-topic assign file finish.....<<<<<");
		return true;
	}
	
	private boolean saveTopWordInTopic(String path){
		try{
			FileOutputStream out=new FileOutputStream(new File(path));
			BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(out,"utf-8"));
			int top=this.param.getTopWords();
			if(top>this.V){
				top=this.V;
			}
			int tmp=0;
			for(int k=0; k<this.K; k++){
				tmp=top;
				List<TopicWordSort> sortList=new ArrayList<TopicWordSort>();
				for(int w=0; w<this.V; w++){
					TopicWordSort newEnty=new TopicWordSort(w, this.phi[k][w]);
					sortList.add(newEnty);
				}
				Collections.sort(sortList);
				writer.write("Topic "+k+"\n");
				StringBuffer sb=new StringBuffer();
				for(TopicWordSort enty: sortList){
					if(tmp>0){
						if(this.corpus.getId2wordMap().containsKey(enty.getWordId())){
							String word=this.corpus.getId2wordMap().get(enty.getWordId());
							sb.append("\t"+word+"\t"+enty.getProbability()+"\n");
							tmp-=1;
						}
					}else{
						break;
					}
				}
				writer.write("\t"+sb.toString().trim()+"\n");
			}
			writer.close();
		}catch(Exception ex){
			logger.error("Exception happend while save top topic words file!\n",ex);
			return false;
		}
		logger.info("Save save top topic words file finish.....<<<<<");
		return true;
	}
	private boolean saveDocumentTopicAssign(String path){
		try{
			FileOutputStream out=new FileOutputStream(new File(path));
			BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(out,"utf-8"));
			for(int m=0; m<this.M; m++){
				StringBuffer sb=new StringBuffer();
				List<TopicWordSort> sortList=new ArrayList<TopicWordSort>();
				for(int k=0; k<this.K; k++){
					TopicWordSort tmp=new TopicWordSort(k, this.theta[m][k]);
					sortList.add(tmp);
				}
				Collections.sort(sortList);
				
				for(TopicWordSort enty: sortList){
					sb.append(enty.getWordId()+":"+enty.getProbability()+" ");
				}
				writer.write(sb.toString().trim()+"\n");
			}
			writer.close();
		}catch(Exception ex){
			logger.error("Exception happend while save document topic assign file!\n",ex);
			return false;
		}
		logger.info("Save save document topic assign file finish.....<<<<<");
		return true;
	}
	
	
	
	
	
	
	
	public int[][] getNkv() {
		return nkv;
	}
	public void setNkv(int[][] nkv) {
		this.nkv = nkv;
	}
	public int[] getNmkSum() {
		return nmkSum;
	}
	public void setNmkSum(int[] nmkSum) {
		this.nmkSum = nmkSum;
	}
	
	public Params getParam() {
		return param;
	}
	public void setParam(Params param) {
		this.param = param;
	}
	public Corpus getCorpus() {
		return corpus;
	}
	public void setCorpus(Corpus corpus) {
		this.corpus = corpus;
	}
	public double[][] getTheta() {
		return theta;
	}
	public void setTheta(double[][] theta) {
		this.theta = theta;
	}
	public double[][] getPhi() {
		return phi;
	}
	public void setPhi(double[][] phi) {
		this.phi = phi;
	}
	public int[][] getNmk() {
		return nmk;
	}
	public void setNmk(int[][] nmk) {
		this.nmk = nmk;
	}
	
	public int[] getNkvSum() {
		return nkvSum;
	}
	public void setNkvSum(int[] nkvSum) {
		this.nkvSum = nkvSum;
	}
}
