package norman.baba.grids;

import java.util.*;

import java.awt.*;

/**
 * <p>Title: BABA</p>
 * <p>Description: Bioinformatique Research Project</p>
 * <p>Copyright: Copyright Norman Casagrande (c) 2003</p>
 * @author Norman Casagrande
 * @version 1.0
 */

/** This class represent a single cell element.
 * In the basic version it contains informations about:
 * - Position in the grid (col, row)
 * - Value contained (a String)
 * - Cell color
 * - A vector of the backtracking pointers
 *
 * More could follow, but only as a basic data like font, color, etc, but
 * NOT algo related things. For this case extend the class!
 */

public class CellElement {

   protected int m_column = -1;
   protected int m_row = -1;

   protected String m_value = null;
   protected Color m_color = BasicGrid.GRID_BACKGROUND_COLOR;
   protected Color m_highlightColor = null;
   protected Color m_scoreColor = Color.red;

   protected Color m_mouseOverColor = new Color(160, 255, 160); // lightgreen
   protected Color m_mousePressColor = Color.green;
   protected int m_mouseOverBorder = 1;
   protected int m_mousePressBorder = 2;

   public static final int POINTER_POLICY_ANY = 0;
   public static final int POINTER_POLICY_CLOCKWISE = 1;
   public static final int POINTER_POLICY_COUNTERCLOCKWISE = 2;
   public static final int POINTER_POLICY_RANDOM = 3;

   public static final int POINTERPOS_UNDEF = 0;
   public static final int POINTERPOS_LEFT = 1;
   public static final int POINTERPOS_TOP = 2;
   public static final int POINTERPOS_DIAG = 3;

   protected boolean m_isPressed = false;

   protected class PointerCell {

      public int pos = POINTERPOS_UNDEF;
      public CellElement cell = null;

      PointerCell(CellElement cell, int pos) {
         this.pos = pos;
         this.cell = cell;
      }

      PointerCell(CellElement cell) {
         this.pos = POINTERPOS_UNDEF;
         this.cell = cell;
      }
   }

   protected Vector m_pointers;

   ////////////////////////////////////////////////////////////////////////////////

   public CellElement(int column, int row, Color color, String value) {

      color = color;
      this.m_column = column;
      this.m_row = row;
      this.m_value = value;

      m_pointers = new Vector();
   }

   public CellElement(int col, int row, Color color) {
      this(col, row, color, null);
   }

   public CellElement(int col, int row) {
      this(col, row, BasicGrid.GRID_BACKGROUND_COLOR, null);
   }

   public int getColumn() {
      return m_column;
   }

   public int getRow() {
      return m_row;
   }

   public Point getPos() {
      return new Point(m_column, m_row);
   }

   public int getIntVal() {
      return Integer.parseInt(this.m_value);
   }

   public void setIntVal(int val) {
      this.m_value = Integer.toString(val);
   }

   public String getVal() {
      return m_value;
   }

   public void setVal(String val) {
      m_value = val;
   }

   public void setColor(Color color) {
      m_color = color;
   }

   public Color getColor() {
      if (m_highlightColor == null) {
         return m_color;
      }
      else {
         return m_highlightColor;
      }
   }

   public Color getScoreColor() {
      return m_scoreColor;
   }

   /** highlight color has the priority on color */
   public void setHLColor(Color color) {
      m_highlightColor = color;
   }

   public void clearHLColor() {
      m_highlightColor = null;
   }

   public void clearColor() {
      m_color = BasicGrid.GRID_BACKGROUND_COLOR;
   }

   public void addPointer(CellElement e) {
      PointerCell pCell = new PointerCell(e);
      this.m_pointers.add(pCell);
   }

   public CellElement getPointer(int index) {

      if (index >= m_pointers.size()) {
         System.err.println("Invalid index while getting Pointer (CellData)");
         return null;
      }

      PointerCell tmp = (PointerCell) m_pointers.get(index);
      return tmp.cell;
   }

   public void addLeftPointer(CellElement e) {
      addPositionalPointer(e, POINTERPOS_LEFT);
   }

   public void addTopPointer(CellElement e) {
      addPositionalPointer(e, POINTERPOS_TOP);
   }

   public void addDiagPointer(CellElement e) {
      addPositionalPointer(e, POINTERPOS_DIAG);
   }

   protected void addPositionalPointer(CellElement e, int wantedPos) {
      PointerCell pCell = new PointerCell(e, wantedPos);
      this.m_pointers.add(pCell);
   }

   public CellElement getLeftPointer() {
      return getPositionalPointer(POINTERPOS_LEFT);
   }

   public CellElement getTopPointer() {
      return getPositionalPointer(POINTERPOS_TOP);
   }

   public CellElement getDiagPointer() {
      return getPositionalPointer(POINTERPOS_DIAG);
   }

   /**
    *    Selects the next pointer according to the given policy
    */
   public CellElement getPointerWithPolicy(int policy) {

      if (m_pointers.size() == 0) {
         return null;
      }

      CellElement retCell = null;

      switch (policy) {
         case POINTER_POLICY_ANY:

            retCell = (CellElement) m_pointers.firstElement();
            break;

         case POINTER_POLICY_CLOCKWISE:

            // Clockwise:
            // 1. left
            // 2. diag
            // 3. top

            retCell = getPositionalPointer(POINTERPOS_LEFT);

            if (retCell == null) {
               retCell = getPositionalPointer(POINTERPOS_DIAG);
            }

            if (retCell == null) {
               retCell = getPositionalPointer(POINTERPOS_TOP);
            }

            break;

         case POINTER_POLICY_COUNTERCLOCKWISE:

            // Clockwise:
            // 1. top
            // 2. diag
            // 3. left

            retCell = getPositionalPointer(POINTERPOS_TOP);

            if (retCell == null) {
               retCell = getPositionalPointer(POINTERPOS_DIAG);
            }

            if (retCell == null) {
               retCell = getPositionalPointer(POINTERPOS_LEFT);
            }

            break;

         case POINTER_POLICY_RANDOM:

            retCell = getPointer( (int) (Math.random() * m_pointers.size()));
      }

      return retCell;
   }

   protected CellElement getPositionalPointer(int wantedPos) {

      PointerCell tmp = null;
      CellElement retCell = null;
      ListIterator a = m_pointers.listIterator();

      while (a.hasNext()) {

         tmp = (PointerCell) a.next();

         // wantedPos can be:
         // POINTERPOS_UNDEF, POINTERPOS_LEFT, POINTERPOS_TOP, POINTERPOS_DIAG
         if (tmp.pos == wantedPos) {
            retCell = tmp.cell;
            break;
         }

      }

      return retCell;
   }

   public int countPointers() {
      return m_pointers.size();
   }

   public void clearPointers() {
      m_pointers.clear();
   }

   public boolean isPointer(CellElement cell) {
      ListIterator a = m_pointers.listIterator();
      PointerCell tmp = null;

      while (a.hasNext()) {
         tmp = (PointerCell) a.next();

         if (tmp.cell == cell) {

            return true;
         }
      }

      return false;
   }

   public int getPointerPos(CellElement cell) {

      ListIterator a = m_pointers.listIterator();
      PointerCell tmp = null;

      while (a.hasNext()) {
         tmp = (PointerCell) a.next();

         if (tmp.cell == cell) {
            return tmp.pos;
         }
      }

      return POINTERPOS_UNDEF;

   }

   public static String getPolicyName(int policy) {

      String retPolicy = "Policy not valid";

      switch (policy) {
         case POINTER_POLICY_ANY:
            retPolicy = "Any Pointer";
            break;

         case POINTER_POLICY_CLOCKWISE:
            retPolicy = "Clockwise selection";
            break;

         case POINTER_POLICY_COUNTERCLOCKWISE:
            retPolicy = "Counterclockwise selection";
            break;

         case POINTER_POLICY_RANDOM:
            retPolicy = "Random selection";
            break;

      }

      return retPolicy;
   }

   public void clearCell() {
      this.m_value = null;
      this.clearColor();
   }

   public void clearAll() {
      this.clearCell();
      this.clearPointers();
   }

   public boolean IsPressed() {
      return m_isPressed;
   }

   public void setPressed(boolean state) {
      m_isPressed = state;
   }

   //////////////////////////////////////////////////////////

   public Color getMouseOverColor() {
      return m_mouseOverColor;
   }

   public void setMouseOverColor(Color color) {
      m_mouseOverColor = color;
   }

   public Color getMousePressColor() {
      return m_mousePressColor;
   }

   public void setMousePressColor(Color color) {
      m_mousePressColor = color;
   }

   public int getMouseOverBorder() {
      return m_mouseOverBorder;
   }

   public void setMouseOverBorder(int borderSize) {
      m_mouseOverBorder = borderSize;
   }

   public int getMousePressBorder() {
      return m_mousePressBorder;
   }

   public void setMousePressBorder(int borderSize) {
      m_mousePressBorder = borderSize;
   }

}
