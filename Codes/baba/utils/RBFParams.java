package norman.baba.utils;

import java.util.*;

/**
 * <p>Title: BABA</p>
 * <p>Description: Bioinformatique Research Project</p>
 * <p>Copyright: Copyright Norman Casagrande (c) 2003</p>
 * <p>Company: </p>
 * @author Norman Casagrande
 * @version 1.0
 */


/**
 * Restricted Bloc Function (RBF) Parameters class.
 * NOTE: A is not used (always = 0) due to the encoding.
 */

public class RBFParams {

   public byte[] B, C, D, E;

   /**
    *        S2    j          j+k
    *     0        |     E     |        n
    *     +--------+-----------+-------+
    * S1  |                            |
    *  i -+        +---+-------+       |
    *     |        | A |   B   |       |
    *     |        +---+-------+       |
    *   D |        |   |       |       |
    *     |        | C |   F   |       |
    *     |        |   |       |       |
    * i+k-+        +---+-------+       |
    *     |                            |
    *     +----------------------------+
    *
    */
   public RBFParams(byte[] D_s1, byte[] E_s2,
                    byte[] B_Upper,
                    byte[] C_Left) {
      B = B_Upper;
      C = C_Left;
      D = D_s1;
      E = E_s2;
   }

   public int hashCode() {
      String b = new String(B);
      String c = new String(C);
      String d = new String(D);
      String e = new String(E);

      String resString = b + c + d + e;
      return resString.hashCode();
   }

   public boolean equals(Object object) {

      // Compares two objects: this IS guaranteed to be univoque
      RBFParams cmp = (RBFParams)object;

      if (Arrays.equals(cmp.B, this.B) &&
          Arrays.equals(cmp.C, this.C) &&
          Arrays.equals(cmp.D, this.D) &&
          Arrays.equals(cmp.E, this.E)) {

         return true;

      }
      else {
         return false;
      }
   }

}