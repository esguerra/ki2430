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

public class FRCellElement extends ScoredCellElement {

   // The alternative value stays always in the bottom
   protected String m_alternativeValue = null;

   // The Highlight colors of a divided cell
   protected Color m_dividedTopHLColor = null;
   protected Color m_dividedBottomHLColor = null;

   public FRCellElement(int column, int row, Color color,
                        String value,
                        String alternativeValue) {
     super(column, row, color, value);
     m_alternativeValue = value;
   }

  public FRCellElement(int column, int row, Color color, String value) {
    super(column, row, color, value);
  }

  public FRCellElement(int col, int row, Color color) {
    super(col, row, color);
  }

  public FRCellElement(int col, int row) {
    super(col, row);
  }

  public void setAlternativeVal(String alternativeValue) {
     this.m_alternativeValue = alternativeValue;
  }

  public String getVal() {

   try {
      int val = Integer.parseInt(m_value);
      if (val > 0) {
         return "+" + m_value;
      }
   }
   catch (NumberFormatException ex) {
      return m_value;
   }

   return m_value;

  }

  public String getAlternativeVal() {
     try {
        int val = Integer.parseInt(m_alternativeValue);
        if (val > 0) {
           return "+" + m_alternativeValue;
        }
     }
     catch (NumberFormatException ex) {
        return m_alternativeValue;
     }

     return m_alternativeValue;
  }

  public void setIntAlternativeVal(int alternativeValue) {
     this.m_alternativeValue = Integer.toString(alternativeValue);
  }

  public int getIntAlternativeVal() {
     return Integer.parseInt(this.m_alternativeValue);
  }

  public boolean hasAlternativeVal() {
     if (m_alternativeValue == null) {
        return false;
     }
     else {
        return true;
     }
  }

  public void setDividedHLColors(Color top, Color bottom) {
     m_dividedTopHLColor = top;
     m_dividedBottomHLColor = bottom;
  }

  public boolean existDividedColor() {
     if (m_dividedTopHLColor != null ||
         m_dividedBottomHLColor != null) {
        return true;
     }
     else {
        return false;
     }
  }

  public boolean areDividedColorsEqual() {
     if (m_dividedTopHLColor == m_dividedBottomHLColor) {
        return true;
     }
     else {
        return false;
     }
  }

  public Color getDividedTopHLColor() {
     if (m_dividedTopHLColor == null) {
        return this.getColor();
     }
     else {
        return m_dividedTopHLColor;
     }
  }

  public Color getDividedBottomHLColor() {
     if (m_dividedBottomHLColor == null) {
        return this.getColor();
     }
     else {
        return m_dividedBottomHLColor;
     }
  }

  public void clearHLColor() {
     super.clearHLColor();
     m_dividedTopHLColor = null;
     m_dividedBottomHLColor = null;
  }

  public void clearAll() {
     super.clearAll();
     m_alternativeValue = null;
     m_dividedTopHLColor = null;
     m_dividedBottomHLColor = null;
  }

}