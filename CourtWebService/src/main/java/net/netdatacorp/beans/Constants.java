package net.netdatacorp.beans;

/*
 * <!-- begin-system-doc -->
 * plugin: com.ibm.etools.iseries.javatools
 * version: 9.0.0
 * timestamp: 03:20:53 PM 2013/10/03
 * crc32: 1692001287
 *   
 * <!-- end-system-doc -->
 * 
 * Define constants used by the program call runtime.
 */
public class Constants {
	//Return codes for invokeReturnString method
	public static final String SUCCESS =  "success";
	public static final String FAILURE =  "failure";

	//Runtime config file property names
	public static final String WDT_USEPGMCALLJCA = "WDT_USEPGMCALLJCA";
	public static final String WDT_RUNTIMELIB = "WDT_RUNTIMELIB";
	public static final String WDT_USESSL = "WDT_USESSL";
	public static final String WDT_RUNTIMECURLIB = "WDT_RUNTIMECURLIB";
	public static final String WDT_RUNTIMELIBPOSITIONS = "WDT_RUNTIMELIBPOSITIONS";
	public static final String WDT_PGMCALLJNDI = "WDT_PGMCALLJNDI";
	public static final String WDT_CONNECTIONFACTORY = "WDT_CONNECTIONFACTORY";
	public static final String WDT_HOSTNAME = "WDT_HOSTNAME";
	public static final String WDT_RUNTIMEINITCMD = "WDT_RUNTIMEINITCMD";
	public static final String WDT_PASSWORDENCODING_ENABLE = "WDT_PASSWORDENCODING_ENABLE";
	public static final String WDT_PASSWORD = "WDT_PASSWORD";
	public static final String WDT_USERID = "WDT_USERID";
    public static final String WDT_TRACE = "WDT_TRACE";
    public static final String WDT_TRACEFILE = "WDT_TRACEFILE"; 
    public static final String WDT_EXCEPTIONONERROR = "WDT_EXCEPTIONONERROR";
	
	public static final String JCA_CONNECTIONCLASSNAME = "iseries.programcall.base.JcaAS400Connection";
	
	public static final String LOCAL_HOST = "localhost";
	public static final String SPC_CURRENT = "*CURRENT"; 
	public static final String SPC_USRPRF = "*USRPRF"; 	

}
