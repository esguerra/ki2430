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

public class DPTable
    extends BasicGrid {

   protected class DPArrow {

      Point from = new Point(); // Used when zooming
      Point to = new Point(); // Used when zooming

      public Rectangle tail = new Rectangle();
      Polygon arrowHead = new Polygon();
      Color color = Color.black;
   }

   protected Vector m_arrowList;

   protected Highlight m_currentHLPoint = null;
   protected LinkedList m_choosenHLPoint = null;

   public DPTable() {
      this(EMPTY_TABLE_SIZE, EMPTY_TABLE_SIZE);
   }

   public DPTable(int nHCells, int nVCells) {

      // calling super constructor
      super(nHCells, nVCells);

      m_arrowList = new Vector();

   }

   public boolean setArrow(int cFrom, int rFrom, int cTo, int rTo) {
      m_arrowList.clear();
      return this.addArrow(cFrom, rFrom, cTo, rTo);
   }

   public boolean setArrowColor(int index, Color color) {
      if (index >= m_arrowList.size()) {
         System.err.println("Out of bounds of the m_arrowList array!");
         return false;
      }

      ( (DPArrow) m_arrowList.get(index)).color = color;
      return true;
   }

   public boolean addArrow(CellElement from, CellElement to, Color color) {
      return this.addArrow(from.getColumn(), from.getRow(),
                           to.getColumn(), to.getRow(), color);
   }

   public boolean addArrow(int cFrom, int rFrom, int cTo, int rTo) {
      return this.addArrow(cFrom, rFrom, cTo, rTo, Color.black);
   }

   public boolean addArrow(int cFrom, int rFrom, int cTo, int rTo, Color color) {
      if (!checkBounds(cFrom, rFrom, "addArrow") ||
          !checkBounds(cTo, rTo, "addArrow")) {
         return false;
      }

      if ( (cFrom + rFrom) == (cTo + rTo)) {
         System.err.println("Cannot point to the same cell!");
         return false;
      }

      if ( (cFrom < cTo) || (rFrom < rTo)) {
         System.err.println("Forward arrow non permitted for the moment!");
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
                                 m_cellSize * rFrom + endGap,
                                 m_cellSize * cTo + middleCell,
                                 m_cellSize * (rTo + 1));

            endPoint = m_cellSize * rTo + middleCell + endGap;
            arrow.arrowHead.addPoint(arrow.tail.width, endPoint);
            arrow.arrowHead.addPoint(arrow.tail.width + endGap / 2,
                                     endPoint + endGap);
            arrow.arrowHead.addPoint(arrow.tail.width - endGap / 2,
                                     endPoint + endGap);
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
                              m_cellSize * rFrom + startGap,
                              m_cellSize * (cTo + 1),
                              m_cellSize * (rTo + 1));

         endPoint.setLocation(m_cellSize * (cTo + 1) - endGap,
                              m_cellSize * (rTo + 1) - endGap);

         arrow.arrowHead.addPoint(endPoint.x, endPoint.y);
         arrow.arrowHead.addPoint(endPoint.x + endGap * 2,
                                  endPoint.y + (int) (endGap / 1.5));
         arrow.arrowHead.addPoint(endPoint.x + (int) (endGap / 1.5),
                                  endPoint.y + endGap * 2);

      }

   }

   public void setTriArrows(CellElement current,
                            boolean showNotChoosen) {
      m_arrowList.clear();

      int cCol, cRow;
      cCol = current.getColumn();
      cRow = current.getRow();

      CellElement leftCell = this.getCell(cCol - 1, cRow);
      CellElement topCell = this.getCell(cCol, cRow - 1);
      CellElement topLeftCell = this.getCell(cCol - 1, cRow - 1);

      if (current.isPointer(leftCell)) {
         this.addArrow(current, leftCell, Color.black);
      }
      else {
         if (showNotChoosen) {
            this.addArrow(current, leftCell, Color.lightGray);
         }
      }

      if (current.isPointer(topCell)) {
         this.addArrow(current, topCell, Color.black);
      }
      else {
         if (showNotChoosen) {
            this.addArrow(current, topCell, Color.lightGray);
         }
      }

      if (current.isPointer(topLeftCell)) {
         this.addArrow(current, topLeftCell, Color.black);
      }
      else {
         if (showNotChoosen) {
            this.addArrow(current, topLeftCell, Color.lightGray);
         }
      }

      this.setGridCircle(cCol, cRow);
   }

   public void clearAllArrows() {
      m_arrowList.clear();
   }

   public void setSideHighlight(CellElement cell, Color sideColor) {

      if (m_currentHLPoint == null) {
         m_currentHLPoint = new Highlight(cell, Highlight.HL_SIDEONLY);
      }
      else {
         this.clearHighlight(m_currentHLPoint);
         m_currentHLPoint.cell = cell;
      }
      this.setHighlight(m_currentHLPoint,
                        null, sideColor);
   }

   /** Highlight Choosen Points
    *
    */
   public void setMultipleCellHighlight(LinkedList cells) {

      int i;
      ListIterator lIt;

      if (m_choosenHLPoint == null) {
         m_choosenHLPoint = new LinkedList();
      }
      else {
         lIt = m_choosenHLPoint.listIterator();

         // Clearing prev Highlight
         while (lIt.hasNext()) {
            ( (Highlight) lIt.next()).cell.clearHLColor();
         }
         m_choosenHLPoint.clear();

      }

      lIt = cells.listIterator();
      CellElement tmpCell;

      // adding new highlight
      while (lIt.hasNext()) {
         tmpCell = (CellElement) lIt.next();
         // DEBUG
         if (tmpCell == null) {
            System.out.println("AAAAAHHHH");
         }
         m_choosenHLPoint.add(new Highlight(tmpCell, Highlight.HL_CENTERONLY));
      }

      lIt = m_choosenHLPoint.listIterator();
      Highlight tmpHighlight;

      while (lIt.hasNext()) {
         tmpHighlight = (Highlight) lIt.next();
         this.setHighlight(tmpHighlight, Color.yellow, null);
      }
   }

   public void clearDPHighlights() {

      if (m_currentHLPoint != null) {
         this.clearHighlight(m_currentHLPoint);
         m_currentHLPoint = null;
      }

      if (m_choosenHLPoint != null) {
         ListIterator lIt = m_choosenHLPoint.listIterator();
         Highlight tmpHighlight;
         while (lIt.hasNext()) {
            tmpHighlight = (Highlight) lIt.next();
            this.clearHighlight(tmpHighlight);
         }
         m_choosenHLPoint.clear();
      }

   }

   public void clearDPTableContent() {

      int c, r;
      for (r = 2; r < m_nVCells; ++r) {
         for (c = 2; c < m_nHCells; ++c) {
            this.getCell(c, r).clearAll();
         }
      }
   }

   public void setZoomLevel(double zoom) {
      super.setZoomLevel(zoom);

      ListIterator lIt = m_arrowList.listIterator();

      while (lIt.hasNext()) {
         defineArrow( (DPArrow) lIt.next());
      }

   }

   /**
    * In this method the offscreen image for double buffering is created
    * (if doesn't exists).
    *
    * @param g The Graphics class where to paint.
    */
   public void paint(Graphics g) {
      if (m_areaSize == null) {
         this.setDrawAreaSize();
      }

      this.drawGrid(m_offScreenGraphics);
      this.drawArrows(m_offScreenGraphics);

      g.drawImage(m_offScreenImage,
                  m_centeredImgStartPos.x,
                  m_centeredImgStartPos.y, this);
   }

   protected void drawArrows(Graphics g) {

      Graphics2D g2d = (Graphics2D) g;

      if (m_activeAntialias) {
         g2d.setRenderingHints(m_rhAntiAliasOn);
      }

      int i;
      DPArrow tmpArrow;
      for (i = 0; i < m_arrowList.size(); ++i) {
         tmpArrow = (DPArrow) m_arrowList.get(i);

         g2d.setColor(tmpArrow.color);

         g2d.drawLine(tmpArrow.tail.x, tmpArrow.tail.y,
                      tmpArrow.tail.width, tmpArrow.tail.height);
         g2d.fillPolygon(tmpArrow.arrowHead);
      }

      if (m_activeAntialias) {
         g2d.setRenderingHints(m_rhAntiAliasOff);
      }

   }

}