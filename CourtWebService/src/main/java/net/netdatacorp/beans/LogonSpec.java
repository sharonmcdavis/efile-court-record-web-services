package net.netdatacorp.beans;

/*
 * <!-- begin-system-doc -->
 * plugin: com.ibm.etools.iseries.javatools
 * version: 9.0.0
 * timestamp: 03:20:53 PM 2013/10/03
 * crc32: 2681977817
 *   
 * <!-- end-system-doc -->
 * 
 * This class contains the specifications to logon onto the system. The mininum requirement for
 * a successful logon is a valid host name, user ID, and password. 
 * 
 * If library list information is supplied, it will be used to setup the initial library list.
 * If an initial command is supplied, it will be executed the first time the connection is made.  
 */
public class LogonSpec {
	//host name
	private String hostName = null;

	//User ID
	private String userName = null;

	//User ID password
	private String userPw = null;

	//Library list
	private String[] libList = null;

	//The positions of each library. Valid strings are "*FIRST" and "*LAST"
	private String[] libListPos = null;

	//The current library
	private String curLib = null;

	//The initial command
	private String initialCmd = null;

	/*
	 * Default constructor
	 */
	public LogonSpec() {
	}
	
	/*
	 * Constuctor to create a new LogonSpec by copying an existing one.
	 */
	public LogonSpec(LogonSpec ls) {
		hostName = ls.getHostName();
		userName = ls.getUserName();
		userPw = ls.getUserPw();
		libList = ls.getLibList();
		libListPos = ls.getLibListPos();
		curLib = ls.getCurLib();
		initialCmd = ls.getInitialCmd();
	}

     /*
     * @return  The current library.
     */
	public String getCurLib() {
		return curLib;
	}

    /* Specifies the current library that is set the first time the connection
     * is established. 
     * @curLib The current library.
     */
	public void setCurLib(String curLib) {
		this.curLib = curLib;
	}

    /*
     * @return  The system name.
     */
	public String getHostName() {
		return hostName;
	}

    /* Specifies the system that the connection is establised with. 
     * @hostName The system name.
     */
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

    /* Specifies the inital command that is executed the first time the connection
     * is establised. 
     * @return  The initial command.
     */
	public String getInitialCmd() {
		return initialCmd;
	}

    /* 
     * @initialCmd  The initial command.
     */
	public void setInitialCmd(String initialCmd) {
		this.initialCmd = initialCmd;
	}

    /* 
     * @return  The library list.
     */
	public String[] getLibList() {
		return libList;
	}

    /* Specifies the library list that is set the first time the connection
     * is establised. 
     * @libList  The library list.
     */
	public void setLibList(String[] libList) {
		this.libList = libList;
	}

    /*
     * @return  The library list positions.
     */
	public String[] getLibListPos() {
		return libListPos;
	}

    /* Used in conjunction with the library list to determine the positions of library.
     * @libListPos  The library list positions.
     */
	public void setLibListPos(String[] libListPos) {
		this.libListPos = libListPos;
	}

    /*
     * @return  The user ID.
     */
	public String getUserName() {
		return userName;
	}

    /* Specifies the user that will be connected to the system.
     * @userName  The user ID.
     */
	public void setUserName(String userName) {
		this.userName = userName;
	}

    /*
     * @return  The user password.
     */
	public String getUserPw() {
		return userPw;
	}

    /* Specifes the pass word of the user that will be connected to the system.
     * @userPw  The user password.
     */
	public void setUserPw(String userPw) {
		this.userPw = userPw;
	}
}
