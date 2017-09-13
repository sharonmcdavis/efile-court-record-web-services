package net.netdatacorp.efile.util;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

public class HandlerUtil {
    public static String getXMLFromSource(Source source) throws Exception{
        String xml = null;
        
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            StreamResult sr = new StreamResult(bos);
            Transformer tf = TransformerFactory.newInstance().newTransformer();
            tf.transform(source, sr);
            xml = new String(bos.toString());
            bos.close();
        } catch (Exception e) {
            throw new Exception(e);            
        }
            
        return xml;        
    } 
    
    public static String outputMap(Map<?, ?> map){
    	StringBuffer output = new StringBuffer(" ");
    	Set<?> entries = map.entrySet();
    	Iterator<?> it = entries.iterator();
    	while (it.hasNext()){
    		Entry<?, ?> entry = (Entry<?, ?>) it.next();
    		output.append(entry.getKey().toString() + " : " + entry.getValue().toString() + "\n");
    	}
    	return output.toString();    	
    }
    
    public static String outputQName(QName name){
    	StringBuffer output = new StringBuffer(" ");
    	output.append("{" + name.getNamespaceURI()+ "} ");
    	if (name.getPrefix()!= null) output.append(name.getPrefix() + ":");
    	output.append(name.getLocalPart()); 
    	return output.toString();    	
    }
}
