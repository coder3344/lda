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
	private LdaModel model;
	private Params option;
	private static Logger logger=Logger.getLogger(GibbsSampling.class);
	
	public GibbsSampling(Params p,LdaModel m){
		this.option=p;
		this.model=m;
	}
	
	
	/**
	 * use standard gibbs sampling algorithm to sample a new topic for word(wordId) in doc(docId)
	 * p(z_i=k|Z,W)=(n^t_k-i+β_t)/sum(n^t_k-i+β_t)*(n^k_m+α_t)/sum(n^k_m-i+α_t)
	 * @param docId
	 * @param wordId
	 * @return
	 */
	public int gibbsSampling(int docIdx,int wordIdx,int wordId,double sumAlpha,double sumBeta){
		int oldTopicId=this.model.Z[docIdx].get(wordIdx);
		this.model.nmk[docIdx][oldTopicId]-=1;
		
		this.model.nkv[oldTopicId][wordId]-=1;
		this.model.nkvSum[oldTopicId]-=1;
		this.model.nmkSum[docIdx]-=1;

		int k=0;
		for(; k<this.model.K; k++){
			this.model.p[k]=(this.model.nkv[k][wordId]+this.option.getBeta())/(this.model.nkvSum[k]+sumBeta)*
								(this.model.nmk[docIdx][k]+this.option.getAlpha())/(this.model.nmkSum[docIdx]+sumAlpha);
			if(k>0){
				this.model.p[k]+=this.model.p[k-1];
			}
		}
		double rand=Math.random();
		double u=rand*this.model.p[this.model.K-1];
		for(k=0; k<this.model.K; k++){
			if(this.model.p[k]>u)
				break;//get the new topic id: k
		}

		this.model.nmk[docIdx][k]+=1;
		this.model.nkv[k][wordId]+=1;
		this.model.nkvSum[k]+=1;
		this.model.nmkSum[docIdx]+=1;
		return k;
	}
	
	
	
	
}
