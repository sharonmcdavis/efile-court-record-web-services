package net.netdatacorp.jdbc;

/*
 * <!-- begin-system-doc -->
 * plugin: com.ibm.etools.iseries.javatools
 * version: 9.0.0
 * timestamp: 03:20:53 PM 2013/10/03
 * crc32: 2945798701
 * 
 * <!-- end-system-doc -->
 * 
 * This interface represents a connection factory. Implement this interface to 
 * provide custom connections for the program call beans. 
 *    
 */
public interface IConnectionFactory {

	/*
	 * @ctx The runtime context  
	 */
	public void setRuntimeContext(RuntimeContext ctx );
	
	/*
	 * @return Returns a connection that can be use by the program call bean. 
	 */
    public IConnection createConnection() throws Exception;
}
