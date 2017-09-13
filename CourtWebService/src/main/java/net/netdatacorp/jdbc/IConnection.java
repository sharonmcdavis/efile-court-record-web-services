package net.netdatacorp.jdbc;

import com.ibm.as400.access.AS400;

/*
 * <!-- begin-system-doc -->
 * plugin: com.ibm.etools.iseries.javatools
 * version: 9.0.0
 * timestamp: 03:20:53 PM 2013/10/03
 * crc32: 342917432
 *   
 * <!-- end-system-doc -->
 * 
 * This class represents a connection to the system.  
 */
public interface IConnection {
	
	/*
	 * @return The AS400 object associated with the connection. 
	 */
    public AS400 getAS400() throws Exception;
	
	/*
	 * Release the AS400 object associated with this connection. 
	 */
    public void releaseAS400() throws Exception;
    
  /* 
   * 
   * @return true/false indicating thread safety of the connection.
   */
	 public boolean isThreadSafe();

  /*
   * Set thread safe property.
   */
	 public void setThreadSafe(boolean threadSafe);    

}
