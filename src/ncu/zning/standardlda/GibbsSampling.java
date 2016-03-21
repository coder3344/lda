package ncu.zning.standardlda;

import org.apache.log4j.Logger;

import ncu.zning.enty.Document;
import ncu.zning.enty.LdaModel;
import ncu.zning.enty.Params;

/**
 * 
 * Standard Lda estimator using gibbs sampling
 * @author zning
 *
 */
public class GibbsSampling {
	private LdaModel trainedModel;
	private Params option;
	private static Logger logger=Logger.getLogger(GibbsSampling.class);
	
	public GibbsSampling(Params p){
		this.option=p;
		this.trainedModel=new LdaModel(this.option);
	}
	
	public void init(){		
		this.trainedModel.init();
	}
	
	public void estimate(){
		logger.info("Start to simple "+this.option.getInters()+" iterations, using gibbs sampling");
		int iteCount=0;
		//actually this two variables can pre-calculated;
		double sumAlpha=this.trainedModel.K*this.option.getAlpha();
		double sumBeta=this.trainedModel.V*this.option.getBeta();
		
		for(iteCount=0;iteCount<this.option.getInters();iteCount++){
			logger.info("Iteration "+iteCount+"----->>>");
			int docIdx=0;
			
			for(Document doc : this.trainedModel.getCorpus().getDocuments()){//each document
				//each word in each document
				for(int n=0; n<doc.getLength(); n++){
					int wordId=doc.getWords()[n];
					int newTopic=gibbsSampling(docIdx,n, wordId,sumAlpha,sumBeta);
					this.trainedModel.Z[docIdx].set(n, newTopic);//update the word at n in doc
				}
				docIdx+=1;	
			}
			int realIter=iteCount+1;
			if(this.option.getSaveInteval()>0 && realIter>= this.option.getSaveStepStart() &&realIter%this.option.getSaveInteval()==0){
				logger.info("Start to save model at iteration:"+(iteCount+1));
				this.computeDocumentTheta(sumAlpha);
				this.computeTopicPhi(sumBeta);
				this.trainedModel.saveModel(String.valueOf(iteCount+1));
			}
		}
		logger.info("Gibbs sampling iteration complete! Start to save File Model!");
		this.computeDocumentTheta(sumAlpha);
		this.computeTopicPhi(sumBeta);
		this.trainedModel.saveModel("final");
		logger.info("----------TRAINNING FINISH----------");
	}
	/**
	 * use standard gibbs sampling algorithm to sample a new topic for word(wordId) in doc(docId)
	 * p(z_i=k|Z,W)=(n^t_k-i+β_t)/sum(n^t_k-i+β_t)*(n^k_m+α_t)/sum(n^k_m-i+α_t)
	 * @param docId
	 * @param wordId
	 * @return
	 */
	private int gibbsSampling(int docIdx,int wordIdx,int wordId,double sumAlpha,double sumBeta){
		int oldTopicId=this.trainedModel.Z[docIdx].get(wordIdx);
		this.trainedModel.nmk[docIdx][oldTopicId]-=1;
		
		this.trainedModel.nkv[oldTopicId][wordId]-=1;
		this.trainedModel.nkvSum[oldTopicId]-=1;
		this.trainedModel.nmkSum[docIdx]-=1;

		int k=0;
		for(; k<this.trainedModel.K; k++){
			this.trainedModel.p[k]=(this.trainedModel.nkv[k][wordId]+this.option.getBeta())/(this.trainedModel.nkvSum[k]+sumBeta)*
								(this.trainedModel.nmk[docIdx][k]+this.option.getAlpha())/(this.trainedModel.nmkSum[docIdx]+sumAlpha);
			if(k>0){
				this.trainedModel.p[k]+=this.trainedModel.p[k-1];
			}
		}
		double rand=Math.random();
		double u=rand*trainedModel.p[this.trainedModel.K-1];
		for(k=0; k<this.trainedModel.K; k++){
			if(this.trainedModel.p[k]>u)
				break;//get the new topic id: k
		}

		this.trainedModel.nmk[docIdx][k]+=1;
		this.trainedModel.nkv[k][wordId]+=1;
		this.trainedModel.nkvSum[k]+=1;
		this.trainedModel.nmkSum[docIdx]+=1;
		return k;
	}
	
	/**
	 * compute the document each topic probability
	 * @param alphaSum
	 */
	private void computeDocumentTheta(double alphaSum){
		for(int m=0;m<this.trainedModel.M;m++){
			for(int k=0;k<this.trainedModel.K;k++){
				this.trainedModel.theta[m][k]=(this.trainedModel.nmk[m][k]+this.option.getAlpha())/(this.trainedModel.nmkSum[m]+alphaSum);
			}
		}
	}
	
	private void computeTopicPhi(double betaSum){
		for(int k=0; k<this.trainedModel.K; k++){
			for(int w=0; w<this.trainedModel.V; w++){
				this.trainedModel.phi[k][w]=(this.trainedModel.nkv[k][w]+this.option.getBeta())/(this.trainedModel.nkvSum[k]+betaSum);
			}
		}
	}
}
