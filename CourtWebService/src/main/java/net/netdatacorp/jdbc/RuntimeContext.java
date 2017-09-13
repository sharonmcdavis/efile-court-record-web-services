package net.netdatacorp.jdbc;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import com.ibm.as400.access.AS400ConnectionPool;
import com.ibm.as400.access.Trace;
import com.ibm.connector.as400.Scrambler;

import net.netdatacorp.beans.Constants;
import net.netdatacorp.beans.LogonSpec;
import net.netdatacorp.beans.Messages;

/*
 * <!-- begin-system-doc -->
 * plugin: com.ibm.etools.iseries.javatools
 * version: 9.0.0
 * timestamp: 03:20:53 PM 2013/10/03
 * crc32: 1742782059
 *   
 * <!-- end-system-doc -->
 * 
 * A runtime context contains global runtime information used by all program call beans that
 * share a common configuration file. One instance of this class is created for each 
 * configuration file. 
 */
public class RuntimeContext {

	//The table containing all the runtime context of all program call beans.
	private static Hashtable<String, RuntimeContext> table_ = null;
	static {
		table_ = new Hashtable<String, RuntimeContext>();
	}

	//The properties loaded from the config file
	private Properties rtcfgProps_ = null;
	//The config file name
	private String configFileName_ = null;
	//The connection pool
	private AS400ConnectionPool toolboxConnectionPool_ = null;
	//Indicates if an exception is thrown if an error occurs during invoke
	private boolean throwExceptionOnError_ = false;
	//The connection factory
	private IConnectionFactory connectionFactory_ = null;
	//Indicates if SSL connections is used
	private boolean useSSL_ = false;
	//Indicates if iSeries program call J2C connections are used 
	private boolean useJCA_ = false;
	//The logon specifications. 
	private LogonSpec logonSpec_ = null;
	
	/*
	 * Create a new runtime context from a config file.
	 */
	private RuntimeContext(String configFileName) {
		configFileName_ = configFileName;
		
		//Load the config file
		rtcfgProps_ = loadConfigFile(configFileName_);
		if (rtcfgProps_ == null) {
			return;
		}
		
		//Process the throw exception on error property
        String propVal = getProperty(Constants.WDT_EXCEPTIONONERROR);
        if (propVal != null && propVal.trim().equalsIgnoreCase("true")) {
           	throwExceptionOnError_ = true;
        }
		
		//Process the connection factory property
		String conFactoryName = getProperty(Constants.WDT_CONNECTIONFACTORY);
		try {
			if (conFactoryName == null || conFactoryName.trim().length() < 1) {
				Trace.log(Trace.DIAGNOSTIC, Messages.getMsgText("ConnectionFactoryNotFound"));
			}
			else {
				Class<?> factoryClass = Class.forName(conFactoryName.trim());
				Object factoryObj = factoryClass.newInstance();
				if (factoryObj instanceof IConnectionFactory) {
					connectionFactory_ = (IConnectionFactory) factoryObj;
					connectionFactory_.setRuntimeContext(this);
				} else {
					Trace.log(Trace.DIAGNOSTIC, Messages.getMsgText("ConnectionFactoryNotFound"));
				}
			}
		}
		catch (Exception e) {
			Trace.log(Trace.ERROR, e);
		}
		
		//Process use SSL property
		propVal = getProperty(Constants.WDT_USESSL);
		if (propVal != null)
		    useSSL_ = (propVal.compareToIgnoreCase("true") == 0);
		
		//Process use JCA property
		propVal = getProperty(Constants.WDT_USEPGMCALLJCA);
		if (propVal != null)
		    useJCA_ = (propVal.compareToIgnoreCase("true") == 0);
		
		//Process trace properties
		try {
			String propTraceValue = getProperty(Constants.WDT_TRACE);
			boolean enableTrace = false;
			if (propTraceValue != null
					&& propTraceValue.trim().equalsIgnoreCase("error")) {
				Trace.setTraceErrorOn(true);
				enableTrace = true;
			} else if (propTraceValue != null
					&& propTraceValue.trim().equalsIgnoreCase("warning")) {
				Trace.setTraceWarningOn(true);
				Trace.setTraceDiagnosticOn(true);
				enableTrace = true;
			} else if (propTraceValue != null
					&& propTraceValue.trim().equalsIgnoreCase("all")) {
				Trace.setTraceAllOn(true);
				enableTrace = true;
			}
			if (enableTrace) {
				String propTraceFile = getProperty(Constants.WDT_TRACEFILE);
				if (propTraceFile != null && propTraceFile.trim().length() > 0) {
					Trace.setFileName(propTraceFile.trim());
				}
				Trace.setTraceOn(true);
				Trace.setTraceThreadOn(true);
			}
		} catch (Exception e) {
			Trace.log(Trace.ERROR, e);
		}
		
		//Process properties for LogonSpec
		try {
			logonSpec_ = createLogonSpec();
			toolboxConnectionPool_ = new AS400ConnectionPool();
			toolboxConnectionPool_.setThreadUsed(false);
		} catch (Exception e) {
			Trace.log(Trace.ERROR, e);
		}
	}
	
	/*
	 * Get the runtime context instance for the specified configuration file. If it 
	 * does not yet exist, a new one will be created.
	 * 
	 * @configFileName The runtime configuration file.
	 * @return  The runtime context instance for the specified configuration file.
	 */
	public synchronized static RuntimeContext getContext(String configFileName) {
		RuntimeContext rt = (RuntimeContext)table_.get(configFileName);
		if (rt == null) {
			rt = new RuntimeContext(configFileName);
			table_.put(configFileName, rt);
		}
		return rt;
	}
	
	/*
	 * Get the configuration file for this runtime context instance. 
	 * 
	 * @return The configuration file name.
	 */
	public String getConfigFileName() {
		return configFileName_;
	}
	
	/* 
	 * Get a property in the runtime configuration file.
	 * 
	 * @key     The property key.
	 * @return  The property value. 
	 */
	public String getProperty(String key) {
		if (rtcfgProps_ == null) {
			return null;
		}
		return rtcfgProps_.getProperty(key);
	}
	
	/*
	 * Indicates if an exception is thrown if an errors during program call. 
	 * 
	 * @return  True if an exception should be thrown for errors that occur during program call.
	 *          False otherwise.
	 */
	public boolean throwExceptionOnError() {
		return throwExceptionOnError_;
	}
	
	/*
	 * Get the connection pool. 
	 * 
	 * @return  The connection pool
	 */
	public AS400ConnectionPool getAS400ConnectionPool() {
		return toolboxConnectionPool_;
	}
	
	/*
	 * Get the connection factory.
	 * 
	 * @return The connection factory.
	 */
	public IConnectionFactory getConnectionFactory() {
		return connectionFactory_;
	}
	
	/*
	 * Indicates if Secure Sockets Layer is used when new connections are created in the connection pool.
	 * 
	 * @return  True if SSL is used. False otherwise.
	 */
	public boolean useSSL() {
		return useSSL_;
	}
	
	/*
	 * Indicates if program call J2C connections are used to connect to the system.
	 * 
	 * @return  True if program call J2C connections are used. False otherwise. 
	 */
	public boolean useJCA() {
		return useJCA_;
	}
	
	/*
	 * Get the logon specifcatoins. This includes, the host, name, user ID, and library list information.
	 * 
	 * @return The logon specifications.  
	 */
	public LogonSpec getLogonSpec() {
		return logonSpec_;
	}
	
	
	/* 
	 * Initialize the logon specifications from the values specified in the configuration file.
	 * 
	 * @return  A logon specification.
	 */
    @SuppressWarnings("unused")
	private LogonSpec createLogonSpec() throws Exception {
			LogonSpec logonSpec = new LogonSpec();
			logonSpec.setHostName(getProperty(Constants.WDT_HOSTNAME));
			logonSpec.setUserName(getProperty(Constants.WDT_USERID));
			logonSpec.setUserPw(getProperty(Constants.WDT_PASSWORD));
			String encode = getProperty(Constants.WDT_PASSWORDENCODING_ENABLE);
			if (encode.compareToIgnoreCase("true") == 0 && logonSpec.getUserPw().compareToIgnoreCase(Constants.SPC_CURRENT) != 0) {
				logonSpec.setUserPw(Scrambler.unScramble(getProperty(Constants.WDT_PASSWORD)));
			}
			logonSpec.setCurLib(getProperty(Constants.WDT_RUNTIMECURLIB));

			logonSpec.setInitialCmd(getProperty(Constants.WDT_RUNTIMEINITCMD));

			String llist = getProperty(Constants.WDT_RUNTIMELIB);
			if ((llist != null) && (llist.length() > 0)) {
				StringTokenizer token = new StringTokenizer(llist, ";");
				int j = 0;
				Vector<String> libraries = new Vector<String>(1);
				while (token.hasMoreTokens()) {
					libraries.addElement(token.nextToken());
					j++;
				}
				if (libraries.size() > 0) {
					String[] libl = new String[libraries.size()];
					int sz = libraries.size();
					for (int i = 0; i < sz; i++) {					
						libl[i] = (String) libraries.elementAt(i);
					}
					logonSpec.setLibList(libl);
				}
			}

			String llistpos = getProperty(Constants.WDT_RUNTIMELIBPOSITIONS);
			if (llistpos != null && llistpos.length() > 0) {
				StringTokenizer token = new StringTokenizer(llistpos, ";");
				int i = 0;
				ArrayList<String> libpos = new ArrayList<String>(1);
				while (token.hasMoreElements()) {
					libpos.add(token.nextToken());
					i++;
				}
				if (libpos.size() > 0) {
					int count = libpos.size();
					String[] libListPos = new String[count];
					for (int j = 0; j < count; j++) {
						libListPos[j] = (String) libpos.get(j);
					}
					logonSpec.setLibListPos(libListPos);
				}
			}
				
			return logonSpec;
	}
	
	/* 
	 * Load the configuration file.
	 * 
	 * @configFileName  The configuration file name
	 * @return          The Properties object containing the key/value pairs of the configuration file. 
	 */
	private Properties loadConfigFile(String configFileName) {
  		InputStream is = null;
		try {
			is = AbstractProgramCallBean.class.getResourceAsStream(configFileName);
			if (is != null) {
				Properties rtcfgProps = new Properties();
				rtcfgProps.load(is);
				
				return rtcfgProps;
			}
			else {
				Trace.log(Trace.DIAGNOSTIC, Messages.getMsgText("ConfigFileNotFound", configFileName));
			}
		} catch (Exception e) {
			Trace.log(Trace.ERROR, e);
		}
		finally {
			if (is != null) {
				try {
					is.close();
				}
				catch (Exception e) {
					Trace.log(Trace.ERROR, e);
				}
			}
		}
		
		return null;
	}
}
