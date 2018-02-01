package norman.baba.grids;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import norman.baba.utils.RBFParams;

/**
 * <p>Title: BABA</p>
 * <p>Description: Bioinformatique Research Project</p>
 * <p>Copyright: Copyright Norman Casagrande (c) 2003</p>
 * <p>Company: </p>
 * @author Norman Casagrande
 * @version 1.0
 */

public class PreprocessedGridList extends JPanel {

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
   public static int BORDER_GAP = 8;
   public static int CELL_SIZE = 40; // default cell size (pixels)
   public static int INFRAGRID_SIZE = 25; // (pixels)

   protected static final int TIP_POSITION_BOTTOM_LEFT = 0;
   protected static final int TIP_POSITION_UPPER_RIGHT = 1;

   /**
    * Start  (x1, y1) position of the image in the control. Used when
    * the control is bigger than the image.
    */
   protected Point m_centeredImgStartPos = new Point();

   public static int m_fontSize = 15;
   protected Font m_font = null;
   protected Font m_fontBold = null;
   protected Font m_tipFont = null;

   protected static Color GRIDS_COLOR = Color.lightGray;
   protected static Color GRID_BACKGROUND_COLOR = Color.white;

   protected RenderingHints m_rhAntiAliasOn;
   protected RenderingHints m_rhAntiAliasOff;

   protected boolean m_activeAntialias = true;

   /** -------------------------------------------
    *  Tables Section
    *  ------------------------------------------- */
   protected Hashtable m_ht = null;
   protected ArrayList m_keysList = null;
   protected int m_firstGrid = 0;
   protected int m_nGrids = 0;

   protected int m_nGridSideCells = 1;
   protected String[] m_encodedAlph;

   protected int m_vScroll = 0;

   int m_cellSizeDiv = CELL_SIZE / 2;

   public PreprocessedGridList(Hashtable ht, ArrayList keysList,
                               int firstGrid, int nGrids, int numGridSideCells,
                               int encodedAlphSize) {
      this.m_ht = ht;
      this.m_keysList = keysList;
      this.m_firstGrid = firstGrid;
      this.m_nGrids = nGrids;

      this.m_nGridSideCells = numGridSideCells;

      m_encodedAlph = new String[encodedAlphSize];
      //System.out.println("Alph Size: " + encodedAlphSize);

      for (int i = 0; i < m_encodedAlph.length; ++i) {
         m_encodedAlph[i] = "X" + i;
      }

      m_rhAntiAliasOn =
          new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);
      m_rhAntiAliasOff =
          new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_OFF);

      m_font = new Font(null,
                        Font.PLAIN, m_fontSize);
      m_fontBold = new Font(null,
                        Font.BOLD, m_fontSize);
      double tipFontSize = m_fontSize / 1.3;
      m_tipFont = new Font(null, Font.PLAIN, (int)tipFontSize);

      this.setFont(m_font);

   }

   public void setVScroll(int val) {
      m_vScroll = val;
      repaint();
   }

   public void setRange(int firstGrid, int nGrids) {
      m_firstGrid = firstGrid;
      m_nGrids = nGrids;
      repaint();
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

      g.setColor(this.getBackground());
      g.fillRect(0,0, m_areaSize.width, m_areaSize.height);
      this.drawAllGrids(m_offScreenGraphics);

      g.drawImage(m_offScreenImage,
                  m_centeredImgStartPos.x,
                  m_centeredImgStartPos.y, this);
   }

   public void setDrawAreaSize() {

      m_areaSize = this.getSize();

      m_centeredImgStartPos.x = BORDER_GAP;
      m_centeredImgStartPos.y = 0;

      // Double buffering
      m_offScreenImage = createImage(m_areaSize.width + 1,
                                     m_areaSize.height + 1);
      m_offScreenGraphics = m_offScreenImage.getGraphics();

   }

   protected void drawAllGrids(Graphics g) {

      g.setColor(this.getBackground());
      g.fillRect(0,0, m_areaSize.width, m_areaSize.height);
      // Find the size of the (squared) grid
//      ListIterator tmpLstIt = m_keysList.listIterator(m_firstGrid);

      int verticalBlocSize = m_nGridSideCells * CELL_SIZE + INFRAGRID_SIZE;

      int startBloc = m_vScroll / verticalBlocSize;
      ListIterator tmpLstIt = m_keysList.listIterator(m_firstGrid + startBloc);

      int verticalPos = -m_vScroll % verticalBlocSize +
                        INFRAGRID_SIZE;

      int endBloc = m_areaSize.height / verticalBlocSize + 2;

      for (int i = 0; (i < endBloc) && (tmpLstIt.hasNext()); ++i) {
         RBFParams tmpKey = (RBFParams)tmpLstIt.next();
         MinimalistMatrix m = (MinimalistMatrix)m_ht.get(tmpKey);
         this.drawGrid(g, m, verticalPos);
         this.drawValues(g, m, tmpKey, verticalPos, "Bloc: " + (m_firstGrid +  startBloc + i));
         this.drawTitleStrings(g, tmpKey, verticalPos);

         verticalPos += verticalBlocSize;
      }

    }

    protected void drawGrid(Graphics g, MinimalistMatrix grid,
                            int verticalPos) {

       int sideLenght = m_nGridSideCells * CELL_SIZE;

       Graphics2D g2d = (Graphics2D) g;
       if (m_activeAntialias) {
          g2d.setRenderingHints(m_rhAntiAliasOff);
       }
       // now draw the internal grid
       // Let's first clean everything
       g.setColor(PreprocessedGridList.GRID_BACKGROUND_COLOR);
       g.fillRect(0, verticalPos, sideLenght,
                  sideLenght);

       g.setColor(PreprocessedGridList.GRIDS_COLOR);

       int c, r;
       for (c = 0; c < m_nGridSideCells + 1; ++c) {
          g.drawLine(c * CELL_SIZE, verticalPos,
                     c * CELL_SIZE, sideLenght + verticalPos);
       }

       for (r = 0; r < m_nGridSideCells + 1; ++r) {
          g.drawLine(0, r * CELL_SIZE + verticalPos,
                     sideLenght, r * CELL_SIZE + verticalPos);
       }

       g.setColor(Color.black);
       g.drawRect(0, verticalPos,
                  sideLenght, sideLenght);
       g.drawLine(CELL_SIZE*2, verticalPos + CELL_SIZE*2,
                  sideLenght, verticalPos + CELL_SIZE*2);
       g.drawLine(CELL_SIZE*2, verticalPos + CELL_SIZE*2+1,
                  sideLenght, verticalPos + CELL_SIZE*2+1);

       g.drawLine(CELL_SIZE*2, verticalPos + CELL_SIZE*2,
                  CELL_SIZE*2, verticalPos + sideLenght);
       g.drawLine(CELL_SIZE*2+1, verticalPos + CELL_SIZE*2,
                  CELL_SIZE*2+1, verticalPos + sideLenght);

    }

    protected void drawValues(Graphics g,
                              MinimalistMatrix grid,
                              RBFParams RBFunc,
                              int verticalPos, String name) {

       int x, y;
       byte r, c;

       int t = m_nGridSideCells - 1;

       Graphics2D g2d = (Graphics2D) g;
       if (m_activeAntialias) {
          g2d.setRenderingHints(m_rhAntiAliasOn);
       }

       FontMetrics fmVal = getFontMetrics(m_font);
       FontMetrics fmTip = getFontMetrics(m_tipFont);

       g.setFont(m_font);

       // First draw the table cell name
       int fontWidth = fmVal.stringWidth(name);
       x = (m_nGridSideCells * CELL_SIZE) / 2 - (fontWidth / 2);
       g.setColor(this.getBackground());
       g.fillRect(x, verticalPos - 4 - fmVal.getHeight(), fontWidth, fmVal.getHeight());

       g.setColor(Color.black);
       g.drawString(name, x, verticalPos - 4);

       // Now draw cells values

       int fontHeightDiv = fmVal.getAscent() / 2;
       byte val = 0;

       for (r = 0; r < t; ++r) {
          for (c = 0; c < t; ++c) {

             DrawVal(g, verticalPos,
                     c, r,
                     grid.mat[c][r].value, fmVal);

          }
       }

       // Now draw the tip

       byte valA = 0;
       byte valB = grid.mat[0][t-1].value;
       byte currVal;

       // Zero
       //DrawTip(g, verticalPos, (byte)0, (byte)0, valA, TIP_POSITION_UPPER_RIGHT, fmTip);

       int tipPosition = TIP_POSITION_UPPER_RIGHT;

       // Horizontal
       for (c = 1; c < t; ++c) {

          // Top
          currVal = (byte)(grid.mat[c][0].value - valA);
          valA = grid.mat[c][0].value;
          DrawTip(g, verticalPos,
                  c, (byte)0,
                  currVal, TIP_POSITION_UPPER_RIGHT, fmTip);

          // Bottom
          currVal = (byte)(grid.mat[c][t-1].value - valB);
          valB = grid.mat[c][t-1].value;

          if (c == t-1) {
             tipPosition = TIP_POSITION_BOTTOM_LEFT;
          }
          else {
             tipPosition = TIP_POSITION_UPPER_RIGHT;
          }

          DrawTip(g, verticalPos,
                  c, (byte)(t-1),
                  currVal, tipPosition, fmTip);
       }

       // Vertical
       valA = 0;
       valB = grid.mat[t-1][0].value;
       for (r = 1; r < t; ++r) {

          // Left
          currVal = (byte)(grid.mat[0][r].value - valA);
          valA = grid.mat[0][r].value;
          DrawTip(g, verticalPos,
                  (byte)0, r,
                  currVal, TIP_POSITION_UPPER_RIGHT, fmTip);

          // Right
          currVal = (byte)(grid.mat[t-1][r].value - valB);
          valB = grid.mat[t-1][r].value;
          DrawTip(g, verticalPos,
                  (byte)(t-1), r,
                  currVal, TIP_POSITION_UPPER_RIGHT, fmTip);
       }

    }

    protected void DrawVal(Graphics g, int verticalPos,
                           byte col, byte row,
                           byte val, FontMetrics fmVal) {

       int fontWidth;
       int x, y;
       String strVal;
       int fontHeightDiv = fmVal.getAscent() / 2;

       // Draw Actual value
       g.setColor(Color.black);
       g.setFont(m_font);
       strVal = Byte.toString(val);
       fontWidth = fmVal.stringWidth(strVal);
       x = (col + 1) * CELL_SIZE + m_cellSizeDiv - (fontWidth / 2);
       y = (row + 1) * CELL_SIZE + m_cellSizeDiv + fontHeightDiv;

       //g.drawRect(x,y,x+5,y+5);
       g.drawString(strVal, x, y + verticalPos);


    }

    protected void DrawTip(Graphics g, int verticalPos,
                           byte col, byte row,
                           byte tip, int tipPosition,
                           FontMetrics fmTip) {
       int fontWidth;
       int x = 0, y = 0;
       String strVal;
       strVal = Byte.toString(tip);
       if (tip > 0) {
          strVal = "+" + strVal;
       }

       switch (tipPosition) {
          case TIP_POSITION_UPPER_RIGHT:
             // Draw Tip value
             g.setFont(m_tipFont);
             g.setColor(Color.red);
             fontWidth = fmTip.stringWidth(strVal);
             x = (col + 2) * CELL_SIZE - 1 - fontWidth;
             y = (row + 1) * CELL_SIZE + fmTip.getAscent();

             //g.drawRect(x,y,x+5,y+5);
             break;

          case TIP_POSITION_BOTTOM_LEFT:
             x = (col + 1) * CELL_SIZE + 3;
             y = (row + 2) * CELL_SIZE - 1;

             break;
       }
       g.drawString(strVal, x, y + verticalPos);

    }

    protected void drawTitleStrings(Graphics g, RBFParams key, int verticalPos) {

       int x, y;
       int r, c;

       Graphics2D g2d = (Graphics2D) g;
       if (m_activeAntialias) {
          g2d.setRenderingHints(m_rhAntiAliasOn);
       }

       FontMetrics fm = getFontMetrics(m_font);

       int fontHeightDiv = fm.getAscent() / 2; //fm.getHeight() / 2 - fm.getDescent();
       int cellSizeDiv = CELL_SIZE / 2;
       String val;

       g.setFont(m_fontBold);
       g.setColor(Color.black);
       int fontWidth;

       for (c = 0; c < key.E.length; ++c) {

          val = m_encodedAlph[key.E[c]];
          fontWidth = fm.stringWidth(val);
          x = (c+2) * CELL_SIZE + cellSizeDiv - (fontWidth / 2);
          y = cellSizeDiv + fontHeightDiv;

          //g.drawRect(x,y,x+5,y+5);
          g.drawString(val, x, y+verticalPos);

       }

       for (r = 0; r < key.D.length; ++r) {

          val = m_encodedAlph[key.D[r]];
          fontWidth = fm.stringWidth(val);
          x = cellSizeDiv - (fontWidth / 2);
          y = (r+2) * CELL_SIZE + cellSizeDiv + fontHeightDiv;

          //g.drawRect(x,y,x+5,y+5);
          g.drawString(val, x, y+verticalPos);

       }

    }

}