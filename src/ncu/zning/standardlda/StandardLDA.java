package ncu.zning.standardlda;

import org.apache.log4j.PropertyConfigurator;

import ncu.zning.config.ConfigParser;
import ncu.zning.enty.Params;

public class StandardLDA {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PropertyConfigurator.configure("./conf/log4j.properties");
		
		String confPath=args[0];
		Params option=ConfigParser.parseConfig(confPath);
		GibbsSampling gibbsSampling=new GibbsSampling(option);
		gibbsSampling.init();
		gibbsSampling.estimate();
	}
}
