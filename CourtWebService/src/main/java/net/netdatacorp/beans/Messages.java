package net.netdatacorp.beans;

import java.io.InputStream;
import java.util.Properties;
import com.ibm.as400.access.Trace;

/*
 * <!-- begin-system-doc -->
 * plugin: com.ibm.etools.iseries.javatools
 * version: 9.0.0
 * timestamp: 03:20:53 PM 2013/10/03
 * crc32: 2939997367
 *   
 * <!-- end-system-doc -->
 * 
 * This class loads messages from the Messages.properties files.
 */
public class Messages {

	private static Properties msgProps = null;
	static {msgProps = loadMessageFile("Messages.properties");}

	/*  @key    The message key.
	 *  @return The message string associated with the specified key.
	 */
	public static String getMsgText(String key) {
		return getMsgText(key, new String[0]);
	}
	
	/*  @key    The message key.
	 *  @return The message string associated with the specified key.
	 */
	public static String getMsgText(String key, String parm) {
		String [] parms = new String[1];
		parms[0] = parm;
		return getMsgText(key, parms);
	}
	
	/*  @key    The message key.
	 *  @parms  A list of substitution parameters
	 *  @return The message string associated with the specified key.
	 */
	public static String getMsgText(String key, String[] parms) {
		String msgText = "";
		if (msgProps != null) {
			msgText = msgProps.getProperty(key);
		}
		if (parms == null || parms.length < 1) {
			return msgText;
		}

		// Fill in message substitution variables
		StringBuffer buf = new StringBuffer(msgText);
		int pos = 0;
		while (pos < msgText.length()) {
			int ind1 = msgText.indexOf('{', pos);
			int ind2 = msgText.indexOf('}', pos);
			if (ind1 > -1 && ind2 > -1 && ind2 > ind1) {
				int varnum = -1;
				String varStr = "";
				varStr = msgText.substring(ind1 + 1, ind2);
				varnum = Integer.parseInt(varStr);
				if (varnum < parms.length) {
					String placeHolder = "{" + varnum + "}";
					buf.replace(ind1, ind1 + placeHolder.length(),
							parms[varnum]);
					msgText = buf.toString();
					pos = ind1 + parms[varnum].length();
				} else {
					pos = ind2 + 1;
				}
			} else if (ind1 > -1) {
				pos = ind1 + 1;
			} else {
				pos = msgText.length();
			}
		}

		return buf.toString();
	}
	
	
	
	
	/*
	 * Load the message file.
	 */
	private static Properties loadMessageFile(String messageFileName) {
  		InputStream is = null;
		try {
			is = Messages.class.getResourceAsStream(messageFileName);
			if (is != null) {
				Properties props = new Properties();
				props.load(is);
				return props;
			}
			else {
				Trace.log(Trace.DIAGNOSTIC, "The message file could not be found: " + messageFileName);
			}
		} catch (Exception e) {
			Trace.log(Trace.ERROR, e);
			e.printStackTrace();
		}
		finally {
			if (is != null) {
				try {
					is.close();
				}
				catch (Exception e) {
					Trace.log(Trace.ERROR, e);
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}
