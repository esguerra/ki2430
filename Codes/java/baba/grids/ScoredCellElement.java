package norman.baba.grids;

import java.awt.Color;

/**
 * <p>Title: BABA</p>
 * <p>Description: Bioinformatique Research Project</p>
 * <p>Copyright: Copyright Norman Casagrande (c) 2003</p>
 * <p>Company: </p>
 * @author Norman Casagrande
 * @version 1.0
 */

public class ScoredCellElement extends CellElement {

   protected String m_scoreValue = null;

   public ScoredCellElement(int column, int row, Color color, String value) {
      super(column, row, color, value);
   }

   public ScoredCellElement(int col, int row, Color color) {
      super(col, row, color);
   }

   public ScoredCellElement(int col, int row) {
      super(col, row);
   }

   public int getIntScoreVal() {
      return Integer.parseInt(this.m_scoreValue);
   }

   public void setIntScoreVal(int scoreVal) {
      this.m_scoreValue = Integer.toString(scoreVal);
   }

   public String getScoreVal() {
      return m_scoreValue;
   }

   public void setScoreVal(String scoreVal) {
      m_scoreValue = scoreVal;
   }

   public void clearScore() {
      m_scoreValue = null;
   }
}