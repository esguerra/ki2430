package norman.baba.grids;

import java.util.*;
import java.awt.*;
import norman.baba.utils.*;

/**
 * <p>Title: BABA</p>
 * <p>Description: Bioinformatique Research Project</p>
 * <p>Copyright: Copyright Norman Casagrande (c) 2003</p>
 * <p>Company: </p>
 * @author Norman Casagrande
 * @version 1.0
 */

public class NWTable
    extends DPTable {

   public NWTable(int nHCells, int nVCells) {
      super(nHCells, nVCells);
   }

   public void setGridSize(int nHCells, int nVCells, boolean updateAreaSize) {

      m_nHCells = nHCells;
      m_nVCells = nVCells;

      m_cells = new ScoredCellElement[m_nHCells][m_nVCells];
      int c, r;
      for (r = 0; r < m_nVCells; ++r) {
         for (c = 0; c < m_nHCells; ++c) {
            m_cells[c][r] = new ScoredCellElement(c, r,
                                                  BasicGrid.GRID_BACKGROUND_COLOR);
         }
      }

      m_areaSize = null;

      if (updateAreaSize) {
         this.setDrawAreaSize();
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

      g.drawImage(m_offScreenImage,
                  m_centeredImgStartPos.x,
                  m_centeredImgStartPos.y, this);
   }

   public void drawScores(Graphics g) {

      int x, y;
      int r, c;

      Graphics2D g2d = (Graphics2D) g;

      if (m_activeAntialias) {
         g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                              RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      }

      double scoreFontSize = m_originalFontSize / 1.3;

      Font scoreFont = new Font(null,
                                Font.PLAIN,
                                (int) (scoreFontSize * m_zoomLevel));

      FontMetrics fm = getFontMetrics(scoreFont);
      int fontWidth = 0;
      String val;

      g.setFont(scoreFont);
      g.setColor(Color.red);

      ScoredCellElement tmpCell;

      for (r = 1; r < m_nVCells; ++r) {
         for (c = 1; c < m_nHCells; ++c) {

            tmpCell = (ScoredCellElement)m_cells[c][r];

            if (tmpCell.getScoreVal() != null) {
               val = tmpCell.getScoreVal();
               fontWidth = fm.stringWidth(val);

               x = (c+1) * m_cellSize - 4 - fontWidth;
               y = r * m_cellSize + 1 + fm.getAscent(); ;

               g.drawString(val, x, y);
            }
         }
      }
   }
}