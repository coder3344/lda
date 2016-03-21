package ncu.zning.config;





import java.io.File;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ncu.zning.enty.Params;

/**
 * parse the lda configuration file
 * @author FanstyCoder
 *
 */
public class ConfigParser {
	private static Logger logger=Logger.getLogger(ConfigParser.class);
	public static Params parseConfig(String confFilePath){
		Params params=new Params();
		try{
			 DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance(); 
			 DocumentBuilder builder=builderFactory.newDocumentBuilder();
			 Document doc=builder.parse("E:\\workspace\\ldatoolkit\\conf\\lda.config");
			 Element root=doc.getDocumentElement();
			 
			 NodeList nodes=root.getElementsByTagName("config").item(0).getChildNodes();
			 System.out.println(nodes.getLength());
			 if(nodes!=null){
				 for(int i=0; i<nodes.getLength(); i++){
					 try{
						 Element element=(Element)nodes.item(i);
						 String nodeName=element.getNodeName().trim().toLowerCase();
						 String nodeValue=element.getTextContent().trim();
						 switch (nodeName) {
						case "k":
							params.setK(Integer.parseInt(nodeValue));
							break;
						case "iters":
							params.setInters(Integer.parseInt(nodeValue));
							break;
						case "top":
							params.setTopWords(Integer.parseInt(nodeValue));
							break;
						case "savestart":
							params.setSaveStepStart(Integer.parseInt(nodeValue));
							break;
						case "saveinterval":
							params.setSaveInteval(Integer.parseInt(nodeValue));
							break;
						case "alpha":
							params.setAlpha(Double.parseDouble(nodeValue));
							break;
						case "beta":
							params.setBeta(Double.parseDouble(nodeValue));
							break;
						case "corpuspath":
							params.setCorpusPath(nodeValue);
							break;
						case "outputdir":
							params.setSavePath(nodeValue);
							break;
						default:
							logger.info("No this configuration");
							break;
						}
					 }catch(Exception ex){
						 
					 }
				 }
			 }			 
		}catch(Exception ex){
			logger.error("ERROR while parse config file!",ex);
			return null;
		}
		return params;
	}
}
