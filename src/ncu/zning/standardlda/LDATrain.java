package ncu.zning.standardlda;

import org.apache.log4j.Logger;

import ncu.zning.enty.Document;
import ncu.zning.enty.LdaModel;
import ncu.zning.enty.Params;

public class LDATrain {
	private LdaModel trainModel;
	private Params option;
	private static Logger logger=Logger.getLogger(LDATrain.class);
	private GibbsSampling sampler;
	
	
	public void init(){
		this.trainModel.init();
		sampler=new GibbsSampling(option, this.trainModel);
	}
	
	public void estimate(){
		logger.info("Start to simple "+this.option.getInters()+" iterations, using gibbs sampling");
		int iteCount=0;
		//actually this two variables can pre-calculated;
		double sumAlpha=this.trainModel.K*this.option.getAlpha();
		double sumBeta=this.trainModel.V*this.option.getBeta();
		
		for(iteCount=0;iteCount<this.option.getInters();iteCount++){
			logger.info("Iteration "+iteCount+"----->>>");
			int docIdx=0;
			
			for(Document doc : this.trainModel.getCorpus().getDocuments()){//each document
				//each word in each document
				for(int n=0; n<doc.getLength(); n++){
					int wordId=doc.getWords()[n];
					int newTopic=sampler.gibbsSampling(docIdx,n, wordId,sumAlpha,sumBeta);
					this.trainModel.Z[docIdx].set(n, newTopic);//update the word at n in doc
				}
				docIdx+=1;	
			}
			int realIter=iteCount+1;
			if(this.option.getSaveInteval()>0 && realIter>= this.option.getSaveStepStart() &&realIter%this.option.getSaveInteval()==0){
				logger.info("Start to save model at iteration:"+(iteCount+1));
				this.computeDocumentTheta(sumAlpha);
				this.computeTopicPhi(sumBeta);
				this.trainModel.saveModel(String.valueOf(iteCount+1));
			}
		}
		logger.info("Gibbs sampling iteration complete! Start to save File Model!");
		this.computeDocumentTheta(sumAlpha);
		this.computeTopicPhi(sumBeta);
		this.trainModel.saveModel("final");
		logger.info("----------TRAINNING FINISH----------");
	}
	
	/**
	 * compute the document each topic probability
	 * @param alphaSum
	 */
	private void computeDocumentTheta(double alphaSum){
		for(int m=0;m<this.trainModel.M;m++){
			for(int k=0;k<this.trainModel.K;k++){
				this.trainModel.theta[m][k]=(this.trainModel.nmk[m][k]+this.option.getAlpha())/(this.trainModel.nmkSum[m]+alphaSum);
			}
		}
	}
	
	private void computeTopicPhi(double betaSum){
		for(int k=0; k<this.trainModel.K; k++){
			for(int w=0; w<this.trainModel.V; w++){
				this.trainModel.phi[k][w]=(this.trainModel.nkv[k][w]+this.option.getBeta())/(this.trainModel.nkvSum[k]+betaSum);
			}
		}
	}
}
