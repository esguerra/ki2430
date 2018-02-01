package norman.baba.grids;

import java.awt.*;
import java.util.*;

/**
 * <p>Title: BABA</p>
 * <p>Description: Bioinformatique Research Project</p>
 * <p>Copyright: Copyright Norman Casagrande (c) 2003</p>
 * <p>Company: </p>
 * @author Norman Casagrande
 * @version 1.0
 */

public class FRTable extends NWTable {

   protected class BackgroundIndex {
      public String index;
      public Point currentBloc;
      BackgroundIndex(String index, Point currentBloc){
         this.index = index;
         this.currentBloc = currentBloc;
      }
   }

   protected static Color INDEX_FONT_COLOR = Color.white; //new Color(60,255,60);
   protected static Color PARAMS_COLOR = new Color(108, 255, 108);

   protected boolean m_showBkIdxs = true;
   protected LinkedList m_backgroundIdxs = new LinkedList();
   protected Font m_idxFont;
   protected int m_originalIdxFontSize = 16;

   protected int m_t = 1;

   public FRTable(int nHCells, int nVCells, int t) {
      super(nHCells, nVCells);

      m_t = t;
      m_idxFont = new Font(null,
                           Font.PLAIN,
                           (int)(m_originalIdxFontSize * m_zoomLevel));

   }

   public FRTable(int nHCells, int nVCells) {
      this(nHCells, nVCells, 1);
   }

   public void setBlocSize(int t) {
      m_t = t;
   }

   public void setGridSize(int nHCells, int nVCells, boolean updateAreaSize) {

     m_nHCells = nHCells;
     m_nVCells = nVCells;

     m_cells = new FRCellElement[m_nHCells][m_nVCells];
     int c, r;
     for (r = 0; r < m_nVCells; ++r) {
        for (c = 0; c < m_nHCells; ++c) {
           m_cells[c][r] = new FRCellElement(c, r,
                                             BasicGrid.GRID_BACKGROUND_COLOR);
        }
     }

     m_areaSize = null;

     if (updateAreaSize) {
        this.setDrawAreaSize();
     }
  }

  public void setZoomLevel(double zoom) {
     super.setZoomLevel(zoom);
     m_idxFont = new Font(null,
                          Font.PLAIN,
                          (int) (m_originalIdxFontSize * m_zoomLevel));

  }

  public void setShowBackgroundIndexes(boolean show) {
     m_showBkIdxs = show;
  }

  public void addBackgroundIndex(Point currentBloc, String index) {
     BackgroundIndex tmpIdx = new BackgroundIndex(index, currentBloc);
     m_backgroundIdxs.add(tmpIdx);
  }

  public void clearAllBkIndexes() {
     m_backgroundIdxs.clear();
  }

  public void clearLastBkIndex() {
     m_backgroundIdxs.removeLast();
  }

  public void highlightBloc(Point bloc) {

     // RICORDARSI DI METTERE UNA COLORAZIONE DIVERSA NEL CASO
     // DI UNA CELLA CON VALORE ALTERNATIVO!

     Point baseCell = new Point();
     baseCell.x = (m_t - 1) * bloc.x + 1;
     baseCell.y = (m_t - 1) * bloc.y + 1;

     int c, r;
     this.clearHighlightColors();
     FRCellElement tmpCell;

     for (c = 1; c < m_t; ++c) {
        for (r = 1; r < m_t; ++r) {

           tmpCell = (FRCellElement)this.getCell(baseCell.x + c,
                                  baseCell.y + r);
           tmpCell.setHLColor(Color.yellow);

        }
     }

     // Highlight the RBFunction parameters
     // (NOTE: not in the alternative value cell! Because it is handled
     // elsewhere)

     ///////////////// First the alphabet

     // Top alphabet
     for (c = 1; c < m_t; ++c) {
        tmpCell  = (FRCellElement)this.getCell(baseCell.x + c, 0);
        tmpCell.setHLColor(PARAMS_COLOR);
     }

     // Left alphabet
     for (r = 1; r < m_t; ++r) {
        tmpCell = (FRCellElement)this.getCell(0, baseCell.y + r);
        tmpCell.setHLColor(PARAMS_COLOR);
     }

     //////////////// Now the gaps

     // Top gap
     for (c = 1; c < m_t; ++c) {
        tmpCell  = (FRCellElement)this.getCell(baseCell.x + c, baseCell.y);

        if (!tmpCell.hasAlternativeVal()) {
           tmpCell.setHLColor(PARAMS_COLOR);
        }
        else {
           // Because it's top gap, the alternative one (bottom) is the
           // one that will be colored
           tmpCell.setDividedHLColors(Color.white, PARAMS_COLOR);
        }
     }

     // Left gap
     for (r = 1; r < m_t; ++r) {
        tmpCell = (FRCellElement)this.getCell(baseCell.x, baseCell.y + r);
        if (!tmpCell.hasAlternativeVal()) {
           tmpCell.setHLColor(PARAMS_COLOR);
        }
        else {
           // Because it's left gap, the normal one (top) is the
           // one that will be colored
           tmpCell.setDividedHLColors(PARAMS_COLOR, Color.white);
        }
     }

  }

  public void clearBloc(Point bloc) {

     Point baseCell = new Point();
     baseCell.x = (m_t - 1) * bloc.x + 1;
     baseCell.y = (m_t - 1) * bloc.y + 1;

     int c, r;
     FRCellElement tmpCell;

     // erasing everything of the bloc
     for (r = 1; r < m_t; ++r) {
        for (c = 1; c < m_t; ++c) {
           tmpCell = (FRCellElement)this.getCell(baseCell.x + c,
                                                 baseCell.y + r);
           tmpCell.clearAll();
           tmpCell.clearScore();
        }
     }
  }

  public void clearDPTableContent() {

     int c, r;
     FRCellElement tmpCell;
     for (r = 2; r < m_nVCells; ++r) {
        for (c = 2; c < m_nHCells; ++c) {
           tmpCell = (FRCellElement)this.getCell(c, r);
           tmpCell.clearAll();
           tmpCell.clearScore();
        }
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
     this.drawScores(m_offScreenGraphics);
     this.drawArrows(m_offScreenGraphics);
     this.drawIndexes(m_offScreenGraphics);

     g.drawImage(m_offScreenImage,
                 m_centeredImgStartPos.x,
                 m_centeredImgStartPos.y, this);
  }

  /**
   * This now draws also the alternative value and the divider
   * @param g
   */
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
     int cellSizeThird = m_cellSize / 3;
     int valWidth = 0;
     int alternativeValWidth = 0;
     String val;
     String alternativeVal;

     g.setFont(m_font);
     g.setColor(Color.black);

     FRCellElement tmpCell;

     for (r = 0; r < m_nVCells; ++r) {
        for (c = 0; c < m_nHCells; ++c) {

           tmpCell = (FRCellElement)m_cells[c][r];
           if (tmpCell.getVal() != null) {

              val = tmpCell.getVal();
              valWidth = fm.stringWidth(val);

              if (tmpCell.hasAlternativeVal()) {

                 // Divider
                 g.drawLine(c * m_cellSize, r * m_cellSize,
                            (c+1) * m_cellSize, (r+1) * m_cellSize);


                 alternativeVal = tmpCell.getAlternativeVal();
                 alternativeValWidth = fm.stringWidth(alternativeVal);
                 // Top Right
                 x = c * m_cellSize + cellSizeThird * 2 - (valWidth / 2);
                 y = r * m_cellSize + cellSizeThird + fontHeightDiv;
                 g.drawString(val, x, y);

                 // Bottom Left
                 x = c * m_cellSize + cellSizeThird - (alternativeValWidth / 2);
                 y = r * m_cellSize + cellSizeThird * 2 + fontHeightDiv;
                 g.drawString(alternativeVal, x, y);
              }
              else {
                 x = c * m_cellSize + cellSizeDiv - (valWidth / 2);
                 y = r * m_cellSize + cellSizeDiv + fontHeightDiv;
                 g.drawString(val, x, y);
              }

           }
        }
     }

     if (m_activeAntialias) {
        g2d.setRenderingHints(m_rhAntiAliasOff);
     }

  }

  protected void drawCellFillColors(Graphics g) {

     int r, c;
     int xPos, yPos;
     FRCellElement tmpCell;

     for (r = 0; r < m_nVCells; ++r) {
        for (c = 0; c < m_nHCells; ++c) {

           tmpCell = (FRCellElement)m_cells[c][r];
           if (tmpCell.getColor() != BasicGrid.GRID_BACKGROUND_COLOR ||
               tmpCell.hasAlternativeVal()) {

              //System.out.println("Color found: " + m_matrixColor[c][r]);

              if (tmpCell.existDividedColor()) {
                 this.drawDividedCell(g, tmpCell);
              }
              else {
                 xPos = c * m_cellSize;
                 yPos = r * m_cellSize;

                 g.setColor(m_cells[c][r].getColor());
                 g.fillRect(xPos + 1, yPos + 1,
                            m_cellSize - 1, m_cellSize - 1);
              }
           }
        }
     }

  }

  protected void drawDividedCell(Graphics g, FRCellElement cell) {

     Point pos = new Point(cell.getColumn() * m_cellSize,
                           cell.getRow() * m_cellSize);

     Polygon triangle = new Polygon();
     // Top triangle
     triangle.addPoint(pos.x + 1, pos.y + 1);
     triangle.addPoint(pos.x + 1 + m_cellSize, pos.y + 1);
     triangle.addPoint(pos.x + 1 + m_cellSize, pos.y + 1 + m_cellSize);

     g.setColor(cell.getDividedTopHLColor());
     g.fillPolygon(triangle);

     triangle.reset();

     // Bottom triangle
     triangle.addPoint(pos.x + 1, pos.y + 1);
     triangle.addPoint(pos.x + 1, pos.y + 1 + m_cellSize);
     triangle.addPoint(pos.x + m_cellSize, pos.y + 1 + m_cellSize);

     g.setColor(cell.getDividedBottomHLColor());
     g.fillPolygon(triangle);
  }

  protected void drawIndexes(Graphics g) {

     if (m_backgroundIdxs.isEmpty()) {
        return;
     }
     if (!m_showBkIdxs) {
        return;
     }

     Graphics2D g2d = (Graphics2D) g;

     if (m_activeAntialias) {
        g2d.setRenderingHints(m_rhAntiAliasOn);
     }

     Point pos = new Point();
     Point center = new Point();
     int width, height;

     FontMetrics fm = getFontMetrics(m_idxFont);
     height = fm.getAscent();

     Color bkRectColor = new Color(0,0,200, 150);
     g.setFont(m_idxFont);

     ListIterator lIt = m_backgroundIdxs.listIterator();
     BackgroundIndex tmpBk;

     while (lIt.hasNext()) {

        tmpBk = (BackgroundIndex)lIt.next();

        center.x = ((m_t - 1) * tmpBk.currentBloc.x + 1) * m_cellSize +
                   (m_t * m_cellSize / 2);
        center.y = ((m_t - 1) * tmpBk.currentBloc.y + 1) * m_cellSize +
                   (m_t * m_cellSize / 2);

        width = fm.stringWidth(tmpBk.index);
        pos.x = center.x - width / 2;
        pos.y = center.y + height / 2;

        g.setColor(bkRectColor);
        g.fillRoundRect(pos.x - 1,
                        pos.y - height + fm.getDescent() - 1,
                        width + 1, fm.getAscent(), 5, 5);

        g.setColor(INDEX_FONT_COLOR);
        g.drawString(tmpBk.index, pos.x, pos.y);
     }

     if (m_activeAntialias) {
        g2d.setRenderingHints(m_rhAntiAliasOff);
     }

  }

}