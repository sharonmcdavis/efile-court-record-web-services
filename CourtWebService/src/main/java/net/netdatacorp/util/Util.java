package net.netdatacorp.util;

/*
 * <!-- begin-system-doc -->
 * plugin: com.ibm.etools.iseries.javatools
 * version: 9.0.0
 * timestamp: 03:20:53 PM 2013/10/03
 * crc32: 451204195
 *   
 * <!-- end-system-doc -->
 * 
 * Utility methods used by the program call runtime.
 */
public class Util {

   /* @rawBytes  The byte array to convert into a string
    * @return    The string representation of this byte array.
    */
   public static String byteArrayToString(byte[] rawBytes)  {
      String s = "";
      for (int i=0; i < rawBytes.length; i++) {
         s += Integer.toString((rawBytes[i] & 0xff) + 0x100, 16).substring(1);
      }
      return s;
   }
   
}
