package net.netdatacorp.jdbc;

import javax.naming.Context;
import javax.naming.InitialContext;
import com.ibm.as400.access.AS400;
import com.ibm.connector2.iseries.pgmcall.ISeriesPgmCallConnection;
import com.ibm.connector2.iseries.pgmcall.ISeriesPgmCallConnectionSpec;
import com.ibm.connector2.iseries.pgmcall.ISeriesPgmCallConnectionSpecImpl;

import net.netdatacorp.beans.LogonSpec;

/* 
 * <!-- begin-system-doc -->
 * plugin: com.ibm.etools.iseries.javatools
 * version: 9.0.0
 * timestamp: 03:20:53 PM 2013/10/03
 * crc32: 4092192853
 *   
 * <!-- end-system-doc -->
 * 
 * This class uses the JNDI name to lookup an ISeriesPgmCallConnection JCA connection.
 */
public class JcaAS400Connection implements IConnection {

	//JNDI name
	private String jndiName = null;

	//Logon info
	private LogonSpec logonSpec_ = new LogonSpec();
	
	//The connection
	private ISeriesPgmCallConnection jcaConnection_ = null;
	
    //Thread safety
	private boolean threadSafe_ = false;
	
	public JcaAS400Connection(String jndi) {
		jndiName = jndi;
	}
	
    public JcaAS400Connection(String jndi, LogonSpec logonSpec) {
		jndiName = jndi;
		logonSpec_ = logonSpec;
	}
	
    /*
     * (non-Javadoc)
     * @see iseries.programcall.base.IConnection#isThreadSafe()
     */
	public boolean isThreadSafe() {
		return threadSafe_;
	}

    /*
     * (non-Javadoc)
     * @see iseries.programcall.base.IConnection#setThreadSafe(boolean)
     */
	public void setThreadSafe(boolean threadSafe) {
		threadSafe_ = threadSafe;
	}

    
    /*
     * @return  The JNDI name.
     */
	public String getJndiName() {
		return jndiName;
	}

    /* Specifies the JNDI name that is used to lookup a JCA connection. 
     * @jndiName  The JNDI name.
     */
	public void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}

	/*
	 * @return The logon specifications 
	 */
	public LogonSpec getLogonSpec() {
		return logonSpec_;
	}
	
	/* Specifies the logon specs.
	 * @spec The logon specifications
	 */
	public void setLogonSpec(LogonSpec spec) {
		logonSpec_ = spec;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see iseries.programcall.base.IConnection#getAS400()
	 * 
	 * The AS400 object is extracted from the ISeriesPgmCallConnection JCA connection. 
	 */
    public AS400 getAS400() throws Exception {
		AS400 as400 = null;
		if (getJndiName() != null) {
				Context ic = new InitialContext();
				ConnectionFactory cf = (ConnectionFactory) ic.lookup(getJndiName());
				ISeriesPgmCallConnectionSpec jcaConnSpec = new ISeriesPgmCallConnectionSpecImpl();

				jcaConnSpec.setSessionID("1");
				
				// Overr-ride JCA logon default
				if (logonSpec_ != null) {
					if (logonSpec_.getHostName() != null && logonSpec_.getHostName().trim().length() > 0) {
						jcaConnSpec.setServerName(logonSpec_.getHostName());
					}
					if (logonSpec_.getUserName() != null && logonSpec_.getUserName().trim().length() > 0) {
						jcaConnSpec.setUserName(logonSpec_.getUserName());
						
						if (logonSpec_.getUserPw() != null && logonSpec_.getUserPw().trim().length() > 0) {
							jcaConnSpec.setPassword(logonSpec_.getUserPw());
						}
					}
					jcaConnSpec.setAS400LibList(logonSpec_.getLibList());
					jcaConnSpec.setAS400LibListPositions(logonSpec_.getLibListPos());
					jcaConnSpec.setCurrentLibrary(logonSpec_.getCurLib());
					jcaConnSpec.setInitialCommand(logonSpec_.getInitialCmd());
				}

				jcaConnection_ = (ISeriesPgmCallConnection)cf.getConnection(jcaConnSpec);
				as400 = jcaConnection_.getAS400Object();
		}
		return as400;
	}

	/* 
	 * (non-Javadoc)
	 * @see iseries.programcall.base.IConnection#releaseAS400()
	 * 
	 * The ISeriesPgmCallConnection JCA connection is closed. 
	 */
    public void releaseAS400() throws Exception {
        if (jcaConnection_ != null) {
            jcaConnection_.close();
        }
    }
}
