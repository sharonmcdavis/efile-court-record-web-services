package net.netdatacorp.jdbc;

import com.ibm.as400.access.AS400;

/*
 * <!-- begin-system-doc -->
 * plugin: com.ibm.etools.iseries.javatools
 * version: 9.0.0
 * timestamp: 03:20:53 PM 2013/10/03
 * crc32: 1323963339
 *   
 * <!-- end-system-doc -->
 * 
 * A simple wrapper around the AS400 object. 
 */
public class AS400Connection implements IConnection {
	private AS400 as400_ = null;
	
    //Thread safety
	private boolean threadSafe_ = false;
	
	public AS400Connection(AS400 as400) {
		as400_ = as400;
	}
	
	/*
	 * (non-Javadoc)
	 * @see iseries.programcall.base.IConnection#getAS400()
	 */
    public AS400 getAS400() throws Exception {
		return as400_;
	}
	
	/*
	 * (non-Javadoc)
	 * @see iseries.programcall.base.IConnection#releaseAS400()
	 */
    public void releaseAS400() throws Exception  {
		
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


}
