package net.netdatacorp.jdbc;

import java.lang.reflect.Constructor;

import com.ibm.connector2.iseries.pgmcall.ISeriesPgmCallConnection;
import com.ibm.connector2.iseries.pgmcall.ISeriesPgmCallConnectionSpec;

import net.netdatacorp.beans.Constants;
import net.netdatacorp.beans.LogonSpec;

/*
 * <!-- begin-system-doc -->
 * plugin: com.ibm.etools.iseries.javatools
 * version: 9.0.0
 * timestamp: 03:20:53 PM 2013/10/03
 * crc32: 1922002563
 * 
 * <!-- end-system-doc -->
 * 
 * This connection factory can create the following types of IConnection:
 *   - JcaAS400Connection
 *   - PooledAS400Connection
 *     
 */
public class ConnectionFactory implements IConnectionFactory {
	//The runtime context of the program call bean
	private RuntimeContext rtContext = null;
	
	/*
	 * (non-Javadoc)
	 * @see iseries.programcall.base.IConnectionFactory#setRuntimeContext(RuntimeContext)
	 */
	public void setRuntimeContext(RuntimeContext ctx) {
		rtContext = ctx;
	}
	
	/*
	 * (non-Javadoc)
	 * @see iseries.programcall.base.IConnectionFactory#createConnection()
	 */
    public IConnection createConnection() throws Exception {
		if (rtContext != null) {
				if (rtContext.useJCA()) {
					Class<?> jcaConClass = Class.forName(Constants.JCA_CONNECTIONCLASSNAME);
					@SuppressWarnings("rawtypes")
					Class [] classParms = new Class[2];
					classParms[0] = String.class;
					classParms[1] = LogonSpec.class;
					Constructor<?> jcaConConstrutor = jcaConClass.getDeclaredConstructor(classParms);
					Object [] objParms = new Object[2];
					objParms[0] = rtContext.getProperty(Constants.WDT_PGMCALLJNDI);
					objParms[1] = rtContext.getLogonSpec();
					Object jcaConn = jcaConConstrutor.newInstance(objParms);
					return ((IConnection)jcaConn);
				} else {
					PooledAS400Connection pooledConn = new PooledAS400Connection(rtContext.getAS400ConnectionPool(), 
							rtContext.getLogonSpec(), rtContext.useSSL());
					return pooledConn;
				}
		}
		return null;
	}

	public ISeriesPgmCallConnection getConnection(ISeriesPgmCallConnectionSpec jcaConnSpec) {
		// TODO Auto-generated method stub
		return null;
	}
}
