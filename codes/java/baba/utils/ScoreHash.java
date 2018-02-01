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

public class ScoreHash {

   protected Hashtable m_hash;

   protected class ScorePair {
      char elA = '\0';
      char elB = '\0';

      ScorePair(char A, char B) {
         this.elA = A;
         this.elB = B;
      }

      public int hashCode() {

         // Gives a generic Hash: this is NOT guaranteed to be univoque
         return Character.getNumericValue(elA) *
                Character.getNumericValue(elB);
      }

      public boolean equals(Object object) {

         // Compares two objects: this IS guaranteed to be univoque
         ScorePair cmp = (ScorePair)object;

         if ( (cmp.elA == this.elA &&
               cmp.elB == this.elB) ||
              (cmp.elB == this.elA &&
               cmp.elA == this.elB) ) {

            return true;

         }
         else {
            return false;
         }
      }

   }

   public ScoreHash() {
      m_hash = new Hashtable();
   }

   public void clearScores() {
      m_hash.clear();
   }

   public int getSize() {
      return m_hash.size();
   }

   public boolean isEmpty() {
      return m_hash.isEmpty();
   }

   public void setScore(char A, char B, int score) {
      m_hash.remove(new ScorePair(A, B));
      m_hash.put(new ScorePair(A, B), new Integer(score));
   }

   public String getScore(char A, char B) {

      Integer val = null;

      val = (Integer)m_hash.get(new ScorePair(A, B));

      try {
         return val.toString();
      }
      catch (Exception ex) {
         //System.err.println("GetScore: No element found");
         return "?";
      }

   }

   public int getIntScore(char A, char B) {
      return Integer.parseInt(getScore(A, B));
   }
}