package net.netdatacorp.jdbc;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400ConnectionPool;
import com.ibm.as400.access.CommandCall;
import com.ibm.as400.security.auth.AS400Credential;
import com.ibm.as400.security.auth.ProfileTokenCredential;

import net.netdatacorp.beans.Constants;
import net.netdatacorp.beans.LogonSpec;

/*
 * <!-- begin-system-doc -->
 * plugin: com.ibm.etools.iseries.javatools
 * version: 9.0.0
 * timestamp: 03:20:53 PM 2013/10/03
 * crc32: 3969559747
 *   
 * <!-- end-system-doc -->
 * 
 * The class represents a connection is that part of a connection pool. 
 */
public class PooledAS400Connection implements IConnection {

    //Logon info
    private LogonSpec logonSpec_ = new LogonSpec();
    
    //The connection
    private AS400 as400_ = null;
    
    //Use secure connections
    private boolean useSSL_ = false;
    
    //Credential before a swap
    private AS400Credential currentUserCredential_ = null;  
    
    // Credential for run-as user
    private ProfileTokenCredential runAsUserCredential_ = null;  
    
    //Tool box connection pool
    private AS400ConnectionPool toolboxConnectionPool_ = null;
    
    //Thread safety
	private boolean threadSafe_ = false;

    public PooledAS400Connection(AS400ConnectionPool pool, LogonSpec spec) {
        logonSpec_ = spec;
	    toolboxConnectionPool_ = pool;
    }

    public PooledAS400Connection(AS400ConnectionPool pool, LogonSpec spec, boolean useSSL) {
        logonSpec_ = spec;
        useSSL_ = useSSL;
	    toolboxConnectionPool_ = pool;
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
     * The AS400 object that is returned belongs to a pool of AS400 objects.  
     */
    public AS400 getAS400() throws Exception {
        if (as400_ == null && logonSpec_ != null) {
            try {
                synchronized(toolboxConnectionPool_) {
                    String hostName = logonSpec_.getHostName();
                    String userName = logonSpec_.getUserName();
                    String userPw = logonSpec_.getUserPw();
                    boolean doSwap = false;
                    if (hostName.compareToIgnoreCase(Constants.LOCAL_HOST) == 0 && userName.compareTo(Constants.SPC_CURRENT) != 0 && 
                        (userPw.length() == 0 || userPw.compareTo(Constants.SPC_CURRENT) == 0)) {
                        doSwap = true;
                        hostName = Constants.LOCAL_HOST;
                        userName = Constants.SPC_CURRENT;
                        userPw = Constants.SPC_CURRENT;
                    }
                    
                    if (useSSL_) {
                        as400_ = toolboxConnectionPool_.getSecureConnection(hostName, userName, userPw);
                    }
                    else {
                        as400_ = toolboxConnectionPool_.getConnection(hostName, userName, userPw);
                    }
                    
                    if (doSwap && as400_ != null) {
                        runAsUserCredential_ = new ProfileTokenCredential();
                        runAsUserCredential_.setSystem(as400_);
                        runAsUserCredential_.setTimeoutInterval(3600);
                        runAsUserCredential_.setTokenType(ProfileTokenCredential.TYPE_SINGLE_USE);
                        runAsUserCredential_.setToken(logonSpec_.getUserName(), ProfileTokenCredential.PW_NOPWDCHK);
                        currentUserCredential_ = runAsUserCredential_.swap(true);
                    }
                    
                    if (as400_ != null && !as400_.isConnected(AS400.COMMAND)) {
                        initJob(as400_);
                    }                    
                }
            } catch (Exception ex) {             
                try {
                    releaseAS400();
                }
                catch (Exception e) { }
                
                throw ex;
            }
        }
        return as400_;
    }

    /*
     * (non-Javadoc)
     * @see iseries.programcall.base.IConnection#releaseAS400()
     * 
     * The AS400 object is released back to the connection pool. 
     */
    public void releaseAS400() throws Exception {
        if (as400_ != null) {
            if (currentUserCredential_ != null) {
                try {
                    currentUserCredential_.swap();
                }
                catch (Exception e) {
                    try { discardCredentials(); } catch (Exception e1) { }
                    toolboxConnectionPool_.returnConnectionToPool(as400_);
                    as400_ = null;
                    throw e;                
                }
            }
        
            discardCredentials();
            toolboxConnectionPool_.returnConnectionToPool(as400_);
            as400_ = null;
        }
    }
    
    /*
     * Routine to clean up credentials.
     *
     */
     private void discardCredentials() throws Exception { 
        Exception exception = null;
            
        try {
             if (currentUserCredential_ != null) {
                 currentUserCredential_.destroy();
                 currentUserCredential_ = null;
             }
         }
         catch (Exception e) {
             exception = e;
         }
     
        try {
             if (runAsUserCredential_ != null) {
                 runAsUserCredential_.destroy();
                 runAsUserCredential_ = null;
             }
         }
         catch (Exception e) {
             if (exception == null) {
                 exception = e;
             }
         }    
         
         if (exception != null) {
             throw exception;
         } 
     }
    
    /*
     * Initialize the host job by setting up the library list and initial command.
     * as400  The AS400 object to initialize.
     */
    private void initJob(AS400 as400) throws Exception {
        CommandCall cmd = new CommandCall(as400);
        cmd.setThreadSafe(isThreadSafe());
        
        // Change inquiry message
        cmd.run("CHGJOB INQMSGRPY(*DFT)");

        //Set up the initial library list
        if (logonSpec_.getLibList() != null && logonSpec_.getLibList().length > 0) {
            if (logonSpec_.getLibListPos() != null
                    && logonSpec_.getLibListPos().length == logonSpec_.getLibList().length) {
                for (int i = 0; i < logonSpec_.getLibList().length; i++) {
                    StringBuffer sbuf = new StringBuffer();
                    sbuf.append("ADDLIBLE ");
                    sbuf.append(logonSpec_.getLibList()[i]);
                    sbuf.append(" ");
                    sbuf.append(logonSpec_.getLibListPos()[i]);
                    cmd.run(sbuf.toString());
                }
            } else {
                for (int i = 0; i < logonSpec_.getLibList().length; i++) {
                    StringBuffer sbuf = new StringBuffer();
                    sbuf.append("ADDLIBLE ");
                    sbuf.append(logonSpec_.getLibList()[i]);
                    cmd.run(sbuf.toString());
                }
            }
        }

        if (logonSpec_.getCurLib() != null
                && logonSpec_.getCurLib().length() > 0
                && logonSpec_.getCurLib().compareTo(Constants.SPC_USRPRF) !=0) {
            String cmdStr = "CHGCURLIB " + logonSpec_.getCurLib();
            cmd.run(cmdStr);
        }

        //Execute any initial commands
        if (logonSpec_.getInitialCmd() != null
                && logonSpec_.getInitialCmd().length() > 0) {
            cmd.run(logonSpec_.getInitialCmd());
        }
    }
}
