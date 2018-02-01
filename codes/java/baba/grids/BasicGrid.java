package norman.baba.grids;

import java.util.*;

import java.awt.*;
import java.awt.event.*;

//import javax.swing.*;

/**
 * <p>Title: BABA</p>
 * <p>Description: Bioinformatique Research Project</p>
 * <p>Copyright: Copyright Norman Casagrande (c) 2003</p>
 * @author Norman Casagrande
 * @version 1.0
 */

public class BasicGrid
    extends Panel
    implements ComponentListener, MouseListener, MouseMotionListener {

   /** -------------------------------------------
    *  Graphical section
    *  ------------------------------------------- */
   /** The image */
   protected Image m_offScreenImage = null;
   /** Graphic bitmap for double buffering */
   protected Graphics m_offScreenGraphics = null;

   /** Image size */
   protected Dimension m_areaSize = null;

   /** Gap space between the border and the grid */
   protected int m_borderGap = 8;

   /**
    * Start  (x1, y1) position of the image in the control. Used when
    * the control is bigger than the image.
    */
   protected Point m_centeredImgStartPos = new Point();

   protected static Color GRIDS_COLOR = Color.lightGray;
   protected static Color GRID_BACKGROUND_COLOR = Color.white;

   protected class GridRect {

      public Point from = new Point(); // Used when zooming
      public Point dim = new Point(); // Used when zooming

      public Rectangle coords = null;
      public int size = 1;
      public Color color = Color.black; // Default color = black

      public GridRect(int cFrom, int rFrom,
                      int cWidth, int rHeight,
                      int size, Color color) {

         this.coords = new Rectangle(cFrom * m_cellSize,
                                     rFrom * m_cellSize,
                                     (cWidth - 1) * m_cellSize + m_cellSize,
                                     (rHeight - 1) * m_cellSize + m_cellSize);

         this.from.setLocation(cFrom, rFrom);
         this.dim.setLocation(cWidth, rHeight);

         this.size = size;
         this.color = color;

      }


   }

   protected class GridCircle {
      Point pos = new Point(); // Used when zooming

      public Rectangle coords = null;
      public Color color = Color.red; // Default color = red
      int c = 0, r = 0;
   }

   protected class Highlight {

      public static final int HL_SIDEONLY = 0;
      public static final int HL_CENTERONLY = 1;
      public static final int HL_SIDEANDCENTER = 2;

      CellElement cell = null;
      int highlightType = HL_SIDEONLY;

      Highlight(CellElement cell, int highlightType) {
         this.cell = cell;
         this.highlightType = highlightType;
      }

   }

//   protected GridRect m_evRect = null;
   protected LinkedList m_evRects = null;
   protected GridCircle m_evCircle = null;
   protected int m_evCircleBounds = 7; // smaller value = bigger circle (in the cell)

   protected RenderingHints m_rhAntiAliasOn;
   protected RenderingHints m_rhAntiAliasOff;

   protected boolean m_activeAntialias = true;

   /** -------------------------------------------
    *  Normal Grid Section
    *  ------------------------------------------- */

   public static int EMPTY_TABLE_SIZE = 6; // default empty size (pixels)

   protected int m_cellSize = 40; // default cell size (pixels)

   /** Zoom Level */
   protected int m_originalCellSize = m_cellSize;
   protected int m_originalFontSize = 13;
   protected double m_zoomLevel = 1;

   protected Font m_font = null;

   protected int m_nHCells = EMPTY_TABLE_SIZE;
   protected int m_nVCells = EMPTY_TABLE_SIZE;

   protected CellElement m_cells[][];

   // ----------- Grid Interactivity --------------

   protected LinkedList m_interactCells = new LinkedList();
   protected CellElement m_currentInteractCell = null;

   protected CellInteractInterface m_cellInterface = null;

   // ---------------------------------------------

   /**
    * Default Constructor
    */

   public BasicGrid() {
      this(EMPTY_TABLE_SIZE, EMPTY_TABLE_SIZE);
   }

   public BasicGrid(int nHCells, int nVCells) {

      addComponentListener(this);
      addMouseMotionListener(this);
      addMouseListener(this);

      m_rhAntiAliasOn =
          new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);
      m_rhAntiAliasOff =
          new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_OFF);

      m_cellSize = (int) (m_originalCellSize * m_zoomLevel);
      m_font = new Font(null,
                        Font.PLAIN,
                        (int) (m_originalFontSize * m_zoomLevel));

      m_evRects = new LinkedList();

      this.setFont(m_font);
      this.setGridSize(nHCells, nVCells, false);

   }

   public void setCellListener(CellInteractInterface cellInterface) {
      this.m_cellInterface = cellInterface;
   }

   public void setGridSize(int nHCells, int nVCells, boolean updateAreaSize) {

      m_nHCells = nHCells;
      m_nVCells = nVCells;

      m_cells = new CellElement[m_nHCells][m_nVCells];
      int c, r;
      for (r = 0; r < m_nVCells; ++r) {
         for (c = 0; c < m_nHCells; ++c) {
            m_cells[c][r] = new CellElement(c, r,
                                            BasicGrid.GRID_BACKGROUND_COLOR);
         }
      }

      m_areaSize = null;

      if (updateAreaSize) {
         this.setDrawAreaSize();
      }
   }

   public int getHCellsCount() {
      return m_nHCells;
   }

   public int getVCellsCount() {
      return m_nVCells;
   }

   public void setZoomLevel(double zoom) {

      m_zoomLevel = zoom;
      m_cellSize = (int) (m_originalCellSize * m_zoomLevel);
      m_font = new Font(null,
                        Font.PLAIN,
                        (int) (m_originalFontSize * m_zoomLevel));
      this.setFont(m_font);
      m_areaSize = null;

      this.updateGridRects();

      if (m_evCircle != null) {
         int bounds = m_cellSize / m_evCircleBounds;

         m_evCircle.coords.setBounds(m_evCircle.pos.x * m_cellSize + bounds,
                                     m_evCircle.pos.y * m_cellSize + bounds,
                                     m_cellSize - bounds * 2,
                                     m_cellSize - bounds * 2);
      }

   }

   protected void updateGridRects() {

      ListIterator lIt = m_evRects.listIterator();
      GridRect tmpRect;

      while (lIt.hasNext()) {
         tmpRect = (GridRect)lIt.next();
         tmpRect.coords.setBounds(tmpRect.from.x * m_cellSize,
                                  tmpRect.from.y * m_cellSize,
                                  (tmpRect.dim.x - 1) * m_cellSize +
                                  m_cellSize,
                                  (tmpRect.dim.y - 1) * m_cellSize +
                                  m_cellSize);
      }

   }

   public double getZoomLevel() {
      return m_zoomLevel;
   }

   public void setAntiAlias(boolean activate) {
      m_activeAntialias = activate;
      m_areaSize = null;
   }

   public boolean getAntiAlias() {
      return m_activeAntialias;
   }

   public void setGridSize(int nHCells, int nVCells) {
      setGridSize(nHCells, nVCells, true);
   }

   public boolean setCellValue(int c, int r, int value) {
      return this.setCellValue(c, r, Integer.toString(value));
   }

   public boolean setCellValue(int c, int r, char value) {
      return this.setCellValue(c, r, "" + value);
   }

   public boolean setCellValue(int c, int r, String value) {

      if (!checkBounds(c, r, "setCellValue")) {
         return false;
      }

      m_cells[c][r].setVal(value);
      return true;
   }

   public String getCellValue(int c, int r) {
      if (!checkBounds(c, r, "getCellValue")) {
         return "";
      }
      return m_cells[c][r].getVal();
   }

   public CellElement getCell(int c, int r) {
      if (!checkBounds(c, r, "getCell")) {
         return null;
      }
      return m_cells[c][r];
   }

   public CellElement getLastCell() {
      return m_cells[m_nHCells - 1][m_nVCells - 1];
   }

   public int getCellValueInt(int c, int r) {
      if (!checkBounds(c, r, "getCellValueInt")) {
         return Integer.MAX_VALUE;
      }

      String val = m_cells[c][r].getVal();
      int retVal = 0;

      if (val == null ||
          val == "") {
         retVal = Integer.MAX_VALUE;
      }
      else {
         retVal = Integer.parseInt(val);
      }

      return retVal;
   }

   public boolean clearCellContent(int c, int r) {
      if (!checkBounds(c, r, "clearCellContent")) {
         return false;
      }

      m_cells[c][r].clearCell();
      return true;

   }

   public boolean setCellColor(int c, int r, Color newColor) {
      if (!checkBounds(c, r, "setCellColor")) {
         return false;
      }

      m_cells[c][r].setColor(newColor);
      return true;
   }

   public boolean clearCellColor(int c, int r) {
      if (!checkBounds(c, r, "clearCellColor")) {
         return false;
      }

      m_cells[c][r].setColor(BasicGrid.GRID_BACKGROUND_COLOR);
      return true;
   }

   public void setHighlight(Highlight hl,
                            Color centerColor, Color sideColor) {

      int c = hl.cell.getColumn();
      int r = hl.cell.getRow();

      // paint
      switch (hl.highlightType) {
         case Highlight.HL_CENTERONLY:

            hl.cell.setHLColor(centerColor);

            break;

         case Highlight.HL_SIDEONLY:

            m_cells[c]
                [0].setHLColor(sideColor);
            m_cells[0]
                [r].setHLColor(sideColor);

            break;

         case Highlight.HL_SIDEANDCENTER:

            hl.cell.setHLColor(centerColor);
            m_cells[c]
                [0].setHLColor(sideColor);
            m_cells[0]
                [r].setHLColor(sideColor);

            break;
      }

   }

   public boolean clearHighlight(Highlight hl) {

      int c = hl.cell.getColumn();
      int r = hl.cell.getRow();

      if (!checkBounds(c, r, "clearHighlight")) {
         return false;
      }

      switch (hl.highlightType) {
         case Highlight.HL_CENTERONLY:

            hl.cell.clearHLColor();

            break;

         case Highlight.HL_SIDEONLY:

            m_cells[c]
                [0].clearHLColor();
            m_cells[0]
                [r].clearHLColor();

            break;

         case Highlight.HL_SIDEANDCENTER:

            hl.cell.clearHLColor();
            m_cells[c]
                [0].clearHLColor();
            m_cells[0]
                [r].clearHLColor();

            break;
      }

      return true;
   }

   public void clearHighlightColors() {
      int c, r;
      CellElement tmpCell;

      for (r = 0; r < m_nVCells; ++r) {
         for (c = 0; c < m_nHCells; ++c) {
            tmpCell = m_cells[c][r];
            tmpCell.clearHLColor();
         }
      }
   }

   public boolean addGridRectangle(int cFrom, int rFrom,
                                   int cWidth, int rHeight,
                                   int size, Color color) {

      if (!checkBounds(cFrom, rFrom, "addGridRectangle") ||
          !checkBounds(cWidth, rHeight, "addGridRectangle")) {
         return false;
      }

      GridRect tmpRect = new GridRect(cFrom, rFrom,
                                      cWidth, rHeight,
                                      size, color);
      m_evRects.add(tmpRect);

      return true;
   }

   /** Setting just one grid rectangle */
   public boolean setGridRectangle(int cFrom, int rFrom,
                                   int cWidth, int rHeight,
                                   int size, Color color) {
      if (!checkBounds(cFrom, rFrom, "setGridRectangle") ||
          !checkBounds(cWidth, rHeight, "setGridRectangle")) {
         return false;
      }

      m_evRects.clear();

      this.addGridRectangle(cFrom, rFrom,
                            cWidth, rHeight,
                            size, color);

      return true;
   }

   public boolean setGridRectangle(int cFrom, int rFrom,
                                   int cWidth, int rHeight) {
      return this.setGridRectangle(cFrom, rFrom, cWidth, rHeight, 2, Color.black);
   }

   public boolean setGridCircle(int c, int r) {
      if (!checkBounds(c, r, "setGridCircle - Normal")) {
         return false;
      }

      if (m_evCircle == null) {
         m_evCircle = new GridCircle();
         m_evCircle.coords = new Rectangle();
      }

      setGridCircle(c, r, m_evCircle.color);
      return true;
   }

   public void clearGridCircle() {
      m_evCircle = null;
   }

   public void clearGridRectangles() {
      m_evRects.clear();
   }

   public boolean setGridCircle(int c, int r, Color color) {
      if (!checkBounds(c, r, "setGridCircle - Set Color")) {
         return false;
      }

      if (m_evCircle == null) {
         m_evCircle = new GridCircle();
         m_evCircle.coords = new Rectangle();
      }

      int bounds = m_cellSize / m_evCircleBounds;

      m_evCircle.coords.setBounds(c * m_cellSize + bounds,
                                  r * m_cellSize + bounds,
                                  m_cellSize - bounds * 2,
                                  m_cellSize - bounds * 2);

      m_evCircle.pos.setLocation(c, r);

      m_evCircle.color = color;
      m_evCircle.c = c;
      m_evCircle.r = r;

      return true;
   }

   public boolean checkBounds(int c, int r, String callingFunc) {
      if ( (r < 0 || r >= m_nVCells) ||
          (c < 0 || c >= m_nHCells)) {
         System.err.println(callingFunc + ": Out of bounds error!");
         return false;
      }
      else {
         return true;
      }
   }

   /**
    * This is an overriding of the {@link Panel#paint(Graphics g)} method
    * to draw the Panel.
    * <P>
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

      g.drawImage(m_offScreenImage,
                  m_centeredImgStartPos.x,
                  m_centeredImgStartPos.y, this);
   }

   protected void setDrawAreaSize() {
      m_areaSize = new Dimension();

      m_areaSize.setSize(m_nHCells * m_cellSize,
                         m_nVCells * m_cellSize);

      m_centeredImgStartPos.x = m_borderGap;
      m_centeredImgStartPos.y = m_borderGap;

      // Double buffering
      m_offScreenImage = createImage(m_areaSize.width + 1,
                                     m_areaSize.height + 1);
      m_offScreenGraphics = m_offScreenImage.getGraphics();

      // If no evidencing rectangle has been defined set
      // the default one
      if (m_evRects.isEmpty()) {

         GridRect tmpRect = new GridRect(0, 0,
                                         m_nHCells, m_nVCells,
                                         1, Color.black);

         m_evRects.add(tmpRect);
      }

   }

   /**
    * Overriding of the of the {@link Container} method
    * to get the dimensions of the icon.
    *
    * @return The preferred size of the icon, which means the picture size.
    */
   public Dimension getPreferredSize() {

      if (m_areaSize != null) {
         return new Dimension(m_areaSize.width + m_borderGap * 2,
                              m_areaSize.height + m_borderGap * 2);
      }
      else {
         return super.getPreferredSize();
      }
   }

   protected void drawGrid(Graphics g) {

      // Let's first clean everything
      g.setColor(BasicGrid.GRID_BACKGROUND_COLOR);
      g.fillRect(0, 0, m_areaSize.width + 1, m_areaSize.height + 1);

      // now draw the internal grid
      g.setColor(BasicGrid.GRIDS_COLOR);
      int c, r;
      for (c = 0; c < m_nHCells + 1; ++c) {
         g.drawLine(c * m_cellSize, 0, c * m_cellSize, m_areaSize.height);
      }

      for (r = 0; r < m_nVCells + 1; ++r) {
         g.drawLine(0, r * m_cellSize, m_areaSize.width, r * m_cellSize);
      }

      this.drawCellFillColors(g);
      this.drawInteractCell(g);

      this.drawEvCircle(g);

      // now draw a rectangle
      this.drawEvRectangle(g);
      this.drawValues(g);

    }

   protected void drawValues(Graphics g) {

      int x, y;
      int r, c;

      Graphics2D g2d = (Graphics2D) g;

      if (m_activeAntialias) {
         g2d.setRenderingHints(m_rhAntiAliasOn);
      }

      FontMetrics fm = getFontMetrics(m_font);
      int fontHeightDiv = fm.getAscent() / 2 - (int)m_zoomLevel; //fm.getHeight() / 2 - fm.getDescent();

      int cellSizeDiv = m_cellSize / 2;
      int fontWidth = 0;
      String val;

      g.setFont(m_font);
      g.setColor(Color.black);

      for (r = 0; r < m_nVCells; ++r) {
         for (c = 0; c < m_nHCells; ++c) {

            if (m_cells[c][r].getVal() != null) {
               val = m_cells[c][r].getVal();
               fontWidth = fm.stringWidth(val);
               x = c * m_cellSize + cellSizeDiv - (fontWidth / 2);
               y = r * m_cellSize + cellSizeDiv + fontHeightDiv;

               //g.drawRect(x,y,x+5,y+5);
               g.drawString(val, x, y);
            }
         }
      }

   }

   protected void drawCellFillColors(Graphics g) {

      int r, c;
      int xPos, yPos;

      for (r = 0; r < m_nVCells; ++r) {
         for (c = 0; c < m_nHCells; ++c) {

            if (m_cells[c][r].getColor() != BasicGrid.GRID_BACKGROUND_COLOR) {

               //System.out.println("Color found: " + m_matrixColor[c][r]);

               xPos = c * m_cellSize;
               yPos = r * m_cellSize;

               g.setColor(m_cells[c][r].getColor());
               g.fillRect(xPos + 1, yPos + 1,
                          m_cellSize - 1, m_cellSize - 1);

            }
         }
      }

   }

   protected void drawInteractCell(Graphics g) {

      if (m_currentInteractCell == null) {
         return;
      }

      //GridRect cellRect = new GridRect();
      //cellRect.coords = new Rectangle();

      int col = m_currentInteractCell.getColumn();
      int row = m_currentInteractCell.getRow();
      int xPos = m_currentInteractCell.getColumn() * m_cellSize;
      int yPos = m_currentInteractCell.getRow() * m_cellSize;

      GridRect cellRect = new GridRect(col, row, col+1, row+1, 1, Color.black);

      cellRect.coords.setBounds( (col) * m_cellSize,
                                (row) * m_cellSize,
                                m_cellSize,
                                m_cellSize);

      if (m_currentInteractCell.IsPressed()) {

         g.setColor(m_currentInteractCell.getMousePressColor());
         cellRect.size = m_currentInteractCell.getMousePressBorder();

      }
      else {

         g.setColor(m_currentInteractCell.getMouseOverColor());
         cellRect.size = m_currentInteractCell.getMouseOverBorder();

      }

      g.fillRect(xPos + 1, yPos + 1,
                 m_cellSize - 1, m_cellSize - 1);
      drawRectangle(g, cellRect);

   }

   protected void drawRectangle(Graphics g, GridRect rect) {
      g.setColor(rect.color);

      // size <= 1
      if (rect.size <= 1) {
         g.drawRect(rect.coords.x, rect.coords.y,
                    rect.coords.width, rect.coords.height);
         return;
      }

      // size > 1
      int i;
      int sizeDiv = rect.size / 2;
      for (i = -sizeDiv; i < sizeDiv + (rect.size % 2); ++i) {
         g.drawRect(rect.coords.x - i, rect.coords.y - i,
                    rect.coords.width + i * 2, rect.coords.height + i * 2);
      }

   }

   protected void drawEvRectangle(Graphics g) {
      ListIterator lIt = m_evRects.listIterator();

      while (lIt.hasNext()) {
         drawRectangle(g, (GridRect)lIt.next());
      }

   }

   protected void drawEvCircle(Graphics g) {

      if (m_evCircle == null) {
         return;
      }

      Graphics2D g2d = (Graphics2D) g;

      if (m_activeAntialias) {
         g2d.setRenderingHints(m_rhAntiAliasOn);
      }

      g2d.setColor(m_evCircle.color);
      g2d.fillOval(m_evCircle.coords.x, m_evCircle.coords.y,
                   m_evCircle.coords.width, m_evCircle.coords.height);

      int holeSize = m_evCircle.coords.width / 8; // smaller number is thicker

      g2d.setColor(m_cells[m_evCircle.c][m_evCircle.r].getColor());
      g2d.fillOval(m_evCircle.coords.x + holeSize,
                   m_evCircle.coords.y + holeSize,
                   m_evCircle.coords.width - holeSize * 2,
                   m_evCircle.coords.height - holeSize * 2);

      if (m_activeAntialias) {
         g2d.setRenderingHints(m_rhAntiAliasOff);
      }
   }

   // --------------- Interactivity grid section -------------------------

   public void clearInteractiveCells() {
      m_interactCells.clear();
   }

   public void addInteractiveCell(CellElement e) {
      m_interactCells.add(e);
   }

   public void mouseDragged(MouseEvent e) {
   }

   public void mouseMoved(MouseEvent e) {

      if (m_interactCells.size() == 0) {
         return;
      }

      CellElement prevInteract = m_currentInteractCell;
      m_currentInteractCell = getInteractOnCoord(e.getPoint());

      if (m_currentInteractCell != prevInteract) {
         paint(this.getGraphics());
      }

   }

   private CellElement getInteractOnCoord(Point coord) {

      coord.x -= m_borderGap;
      coord.y -= m_borderGap;

      Point cellCoord = new Point();
      cellCoord.x = coord.x / m_cellSize;
      cellCoord.y = coord.y / m_cellSize;

      ListIterator a = m_interactCells.listIterator();
      CellElement tmpCell;

      while (a.hasNext()) {
         tmpCell = (CellElement) a.next();

         if (tmpCell.getColumn() == cellCoord.x &&
             tmpCell.getRow() == cellCoord.y) {
            return tmpCell;
         }
      }

      return null;
   }

   /** Mouse Listeners */
   public void mouseClicked(MouseEvent e) {
   }

   public void mouseEntered(MouseEvent e) {
   }

   public void mouseExited(MouseEvent e) {
   }

   public void mousePressed(MouseEvent e) {
      if (m_currentInteractCell != null) {
         m_currentInteractCell.setPressed(true);
         paint(this.getGraphics());
      }
   }

   public void mouseReleased(MouseEvent e) {

      if (m_currentInteractCell == null) {
         return;
      }

      m_currentInteractCell.setPressed(false);
      CellElement prevInteract = m_currentInteractCell;
      m_currentInteractCell = getInteractOnCoord(e.getPoint());

      if (m_currentInteractCell == prevInteract) {
         m_currentInteractCell = null;
         m_cellInterface.onInteractPress(prevInteract);
      }
      else {
         paint(this.getGraphics());
      }

   }

   // --------------------------------------------------------------------

   /**
    * Invoked when the component (indirectly by the window) is resized.
    *
    * @param e The event occurred.
    */
   public void componentResized(ComponentEvent e) {
      //m_areaSize = null;
   }

   /**
    * Invoked when the component is hided.
    * <P>
    * It is here just because it's needed by the implementation of
    * the component listener.
    *
    * @param e The event occurred.
    */
   public void componentHidden(ComponentEvent e) {}

   /**
    * Invoked when the component is moved.
    * <P>
    * It is here just because it's needed by the implementation of
    * the component listener.
    *
    * @param e The event occurred.
    */
   public void componentMoved(ComponentEvent e) {}

   /**
    * Invoked when the component is showed.
    * <P>
    * It is here just because it's needed by the implementation of
    * the component listener.
    *
    * @param e The event occurred.
    */
   public void componentShown(ComponentEvent e) {}

}
