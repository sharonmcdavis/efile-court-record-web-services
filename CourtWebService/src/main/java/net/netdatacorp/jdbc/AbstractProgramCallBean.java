package net.netdatacorp.jdbc;

import com.ibm.as400.access.Trace;

/*
 * <!-- begin-system-doc -->
 * plugin: com.ibm.etools.iseries.javatools
 * version: 9.0.0
 * timestamp: 03:20:53 PM 2013/10/03
 * crc32: 2162558938
 *   
 * <!-- end-system-doc -->
 * 
 * This is the base class for all program call beans. 
 */
public abstract class AbstractProgramCallBean {
	
	//Exception if the program call failed. 
	protected Exception programException = null;
	
	// Program location
	protected String programPath = null;

	// The connection that will be used to invoke the program
	protected IConnection connection = null;

	/* This constructor will initialize a connection for the program call bean
	 * from the connection factory specified in the config file. 
	 *  
	 * @rtContext The runtime context. 
	 */
	protected AbstractProgramCallBean(RuntimeContext rtContext) {
		if (rtContext != null && rtContext.getConnectionFactory() != null) {
			try {
				//Ask connection factory for a new connection.
				connection = rtContext.getConnectionFactory().createConnection();
			}
			catch (Exception e) {
				Trace.log(Trace.ERROR, e);
			}
		}
	}

	/* Trim leading whitespaces.
	 * 
	 * @s source string
	 */
	protected String trimLeft(String s) {
		int startIndex = 0;
		int len = s.length();
		for (startIndex=0; startIndex < len; startIndex++) {
			char c = s.charAt(startIndex);
			if (c != ' ' && c != '\0') {
				break;
			}
		}
		return s.substring(startIndex);
	}

	/* Trim leading and trailing whitespaces.
	 * 
	 * @s source string
	 */
	protected String trimBoth(String s) {
		String ls = trimLeft(s);
		return trimRight(ls);
	}
	
	/* Trim trailing whitespaces.
	 * 
	 * @s source string
	 */
	protected String trimRight(String s) {
		int endIndex = 0;
		int len = s.length();
		for (endIndex=len -1; endIndex > -1; endIndex--) {
			char c = s.charAt(endIndex);
			if (c != ' ' && c != '\0') {
				break;
			}
		}
		return s.substring(0, endIndex +1);
	}

	/* Invoke the program. If the invoke failed, the invokeException 
	 * field contain the exception.
	 * 
	 * @return  true if program call is successful. False otherwise.  
	 */
	public abstract boolean invoke();
	
	/* Initialize the connection that will be used to perform the program call.
	 * 
	 * @connection  The connection.
	 */
	public void initConnection(IConnection connection) {
		this.connection = connection;
	}
	
	/* Specifies the path to the program that will be invoked. 
	 * 
	 * @path The path to he program.
	 */
	public void initPath(String path) {
		programPath = path;
	}

    /*
     * @return  The exception if any that caused the program call to fail.
     */
    public Exception retrieveProgramCallException() {
        return programException;
    }
    
}
