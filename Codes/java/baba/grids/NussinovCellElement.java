package norman.baba.grids;

import java.awt.Color;
import java.util.*;
/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author Patrick Dekker
 * @version 1.0
 */
public class NussinovCellElement extends ScoredCellElement {

    public static final int POINTERPOS_BOTTOM = 4;

    private Vector PairCellList = new Vector();

public NussinovCellElement(int column, int row, Color color, String value) {
     super(column, row, color, value);
  }

public NussinovCellElement(int col, int row, Color color) {
     super(col, row, color);
  }

public void addBottomPointer(CellElement e) {
        addPositionalPointer(e, POINTERPOS_BOTTOM);
  }

 public void addBifPointers(CellElement e1, CellElement e2) {
     PairCellList.add(new NCellPair(e1,e2));
  }

public void addBifPointers(NCellPair cellPair) {
       PairCellList.add(cellPair);
}

public NCellPair getFirstBifPointers() {
       if (PairCellList.isEmpty()){
        return null;
       } else {
           return (NCellPair) PairCellList.firstElement();
       }
   }

public boolean isBifCell(CellElement e) {
    ListIterator it = PairCellList.listIterator();

    NCellPair cp;

    while (it.hasNext()) {
        cp = (NCellPair) it.next();
        if (cp.getCellOne()==e || cp.getCellTwo()==e) return true;

    }
    return false;
}

// return cell pair if e is bif-pointer pair
public CellElement getNextCellFromPair(CellElement e) {
    ListIterator it = PairCellList.listIterator();
    NCellPair cp;

    while (it.hasNext()) {
        cp = (NCellPair) it.next();
        if (cp.getCellOne()==e) return cp.getCellTwo();
        if (cp.getCellTwo()==e) return cp.getCellOne();
    }
    return null;
}
 public boolean isEndCell() {
     return(this.getColumn()<=this.getRow());
 }

 public CellElement getBottomPointer() {
         return getPositionalPointer(POINTERPOS_BOTTOM);
}

 public Vector getBifPointers() {
     return PairCellList;
 }

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
              // 1. bottom
              // 2. diag
              // 3. left

              retCell = getPositionalPointer(POINTERPOS_BOTTOM);

              if (retCell == null) {
                 retCell = getPositionalPointer(POINTERPOS_DIAG);
              }

              if (retCell == null) {
                 retCell = getPositionalPointer(POINTERPOS_LEFT);
              }

              break;

           case POINTER_POLICY_COUNTERCLOCKWISE:

              // Clockwise:
              // 1. Left
              // 2. diag
              // 3. bottom
              retCell = getPositionalPointer(POINTERPOS_LEFT);

              if (retCell == null) {
                 retCell = getPositionalPointer(POINTERPOS_DIAG);
              }

              if (retCell == null) {
                 retCell = getPositionalPointer(POINTERPOS_BOTTOM);
              }

              break;

           case POINTER_POLICY_RANDOM:

              retCell = getPointer( (int) (Math.random() * m_pointers.size()));
        }

        return retCell;
   }
}
