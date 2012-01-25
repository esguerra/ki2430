package norman.baba.grids;

import java.awt.*;
import java.util.Vector;
import java.util.ListIterator;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 *
 * @author Patrick Dekker
 * @version 1.0
 */
public class NTable extends NWTable {

    public NTable(int nHCells, int nVCells) {
        super(nHCells, nVCells);
    }

    public void setGridSize(int nHCells, int nVCells, boolean updateAreaSize) {

      m_nHCells = nHCells;
      m_nVCells = nVCells;

      m_cells = new NussinovCellElement[m_nHCells][m_nVCells];
      int c, r;
      for (r = 0; r < m_nVCells; ++r) {
         for (c = 0; c < m_nHCells; ++c) {
            m_cells[c][r] = new NussinovCellElement(c, r,
                                                  BasicGrid.GRID_BACKGROUND_COLOR);
         }
      }

      m_areaSize = null;

      if (updateAreaSize) {
         this.setDrawAreaSize();
      }
   }

    public void setTriArrows(CellElement current,
                           boolean showNotChoosen) {
     m_arrowList.clear();

     int cCol, cRow;
     cCol = current.getColumn();
     cRow = current.getRow();
     this.setGridCircle(cCol, cRow);

     if (((NussinovCellElement)current).isEndCell()) {
         return;
     }

     CellElement leftCell = this.getCell(cCol - 1, cRow);
     CellElement bottomCell = this.getCell(cCol, cRow + 1);
     CellElement bottomLeftCell = this.getCell(cCol - 1, cRow + 1);

     if (current.isPointer(leftCell)) {
        this.addArrow(current, leftCell, Color.black);
     }
     else {
        if (showNotChoosen) {
           this.addArrow(current, leftCell, Color.lightGray);
        }
     }

     if (current.isPointer(bottomCell)) {
         this.addArrow(current, bottomCell, Color.black);
     }
     else {
        if (showNotChoosen) {
           this.addArrow(current, bottomCell, Color.lightGray);
        }
     }

     if (current.isPointer(bottomLeftCell)) {
        this.addArrow(current, bottomLeftCell, Color.black);
     }
     else {
        if (showNotChoosen) {
           this.addArrow(current, bottomLeftCell, Color.lightGray);
        }
     }
     // bifuricated cell arrows
     NCellPair cellPair;
     ListIterator a = ((NussinovCellElement)current).getBifPointers().listIterator();

        while (a.hasNext()) {
            cellPair = (NCellPair)a.next();
            this.addArrow(current,cellPair.getCellOne(),Color.black);
            this.addArrow(current,cellPair.getCellTwo(),Color.black);
        }

  }

  public boolean addArrow(int cFrom, int rFrom, int cTo, int rTo, Color color) {
        if (!checkBounds(cFrom, rFrom, "addArrow") ||
            !checkBounds(cTo, rTo, "addArrow")) {
           return false;
        }

        DPArrow newArrow = new DPArrow();
        newArrow.color = color;

        newArrow.from.setLocation(cFrom, rFrom);
        newArrow.to.setLocation(cTo, rTo);

        defineArrow(newArrow);

        m_arrowList.add(newArrow);

        return true;
     }

     protected void defineArrow(DPArrow arrow) {

          int cFrom = arrow.from.x;
          int rFrom = arrow.from.y;
          int cTo = arrow.to.x;
          int rTo = arrow.to.y;

          arrow.arrowHead.reset();

          // Vertical or orizontal arrow
          int middleCell = m_cellSize / 2;

          if (cFrom == cTo || rFrom == rTo) {

             int endPoint;
             int endGap = m_cellSize / 4;

             if (cFrom == cTo) {
                // same row - VERTICAL

                arrow.tail.setBounds(m_cellSize * cFrom + middleCell,
                                     m_cellSize * (rFrom+1) - endGap,
                                     m_cellSize * cTo + middleCell,
                                     m_cellSize * rTo + endGap );

                endPoint = m_cellSize * rTo + middleCell - endGap;
                arrow.arrowHead.addPoint(arrow.tail.width, endPoint);
                arrow.arrowHead.addPoint(arrow.tail.width + endGap / 2,
                                         endPoint - endGap);
                arrow.arrowHead.addPoint(arrow.tail.width - endGap / 2,
                                         endPoint - endGap);
             }
             else {
                // same col - HORIZONTAL
                arrow.tail.setBounds(m_cellSize * cFrom + endGap,
                                     m_cellSize * rFrom + middleCell,
                                     m_cellSize * (cTo + 1),
                                     m_cellSize * rTo + middleCell);

                endPoint = m_cellSize * cTo + middleCell + endGap;
                arrow.arrowHead.addPoint(endPoint, arrow.tail.height);
                arrow.arrowHead.addPoint(endPoint + endGap,
                                         arrow.tail.height - endGap / 2);
                arrow.arrowHead.addPoint(endPoint + endGap,
                                         arrow.tail.height + endGap / 2);
             }

          }
          else {

             // Diagonal
             Point endPoint = new Point();

             int startGap = m_cellSize / 3;
             int endGap = m_cellSize / 8;
             arrow.tail.setBounds(m_cellSize * cFrom + startGap,
                                  m_cellSize * (rFrom + 1) - startGap,
                                  m_cellSize * (cTo + 1 ) - endGap,
                                  m_cellSize * rTo + endGap);

             endPoint.setLocation(m_cellSize * (cTo + 1)  - endGap ,
                                  m_cellSize * rTo  + endGap );

             arrow.arrowHead.addPoint(endPoint.x , endPoint.y );
             arrow.arrowHead.addPoint(endPoint.x + (int) (endGap / 1.5),
                                      endPoint.y - endGap * 2);
             arrow.arrowHead.addPoint(endPoint.x + endGap * 2,
                                      endPoint.y - (int) (endGap / 1.5));
          }
       }

   public void addInteractiveCell(NCellPair cellPair) {
           m_interactCells.addAll(cellPair.getCellPair());
   }

   public void clearDPTableContent() {

    for (int r = 1; r < m_nVCells; ++r) {
         for (int c = r+1 ; c < m_nHCells; ++c) {
               this.getCell(c , r ).clearAll();
             }
         }
     }
}




