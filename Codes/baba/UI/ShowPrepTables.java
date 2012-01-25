package norman.baba.UI;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import java.util.*;

import norman.baba.grids.*;
import norman.baba.utils.RBFParams;

/**
 * <p>Title: BABA</p>
 * <p>Description: Bioinformatique Research Project</p>
 * <p>Copyright: Copyright Norman Casagrande (c) 2003</p>
 * <p>Company: </p>
 * @author Norman Casagrande
 * @version 1.0
 */

public class ShowPrepTables
    extends JFrame {

   /** -------------------------------------------
    *  Tables Section
    *  ------------------------------------------- */
   protected Hashtable m_ht = null;

   protected int m_nGridSideCells = 1;
   protected int m_totalVerticalSize = 0;
   protected int m_bottomSize = 6;

   protected PreprocessedGridList m_prGridsPanel;

   protected JPanel m_northPanel = new JPanel(/*new GridLayout(1,3)*/);
   protected JScrollBar m_vSBar = new JScrollBar(JScrollBar.VERTICAL);
   protected JTextFieldNumberOnly m_rangeFrom;
   protected JTextFieldNumberOnly m_rangeTo;

   public ShowPrepTables(Hashtable ht, ArrayList keysList, int firstGrid, int nGrids,
                         int encodedAlphSize) {

     super("Tot:" + ht.size());
     m_ht = ht;

     // Find the size of the (squared) grid
     if (keysList.size() > 0 ) {
        RBFParams tmpKey = (RBFParams) keysList.get(0);
        MinimalistMatrix m =
            (MinimalistMatrix) ht.get(tmpKey);
        m_nGridSideCells = m.mat.length + 1;
     }

     m_totalVerticalSize = (m_nGridSideCells *
                            PreprocessedGridList.CELL_SIZE +
                            PreprocessedGridList.INFRAGRID_SIZE) *
                            nGrids +
                            m_bottomSize;

     m_prGridsPanel = new PreprocessedGridList(ht, keysList,
                                               firstGrid, nGrids,
                                               m_nGridSideCells, encodedAlphSize);

     try {
        jbInit();
     }
     catch (Exception e) {
        e.printStackTrace();
     }

     this.setDialogDimension();
  }

  protected void setDialogDimension() {

     Insets ins = this.getInsets();
     int gridSideSize = m_nGridSideCells * PreprocessedGridList.CELL_SIZE;

     int width = ins.left + ins.right +
                 gridSideSize + m_vSBar.getWidth() +
                 PreprocessedGridList.BORDER_GAP*2 +
                 1;

     int height = ins.top + ins.bottom +
                  m_northPanel.getHeight() +
                  (gridSideSize + PreprocessedGridList.INFRAGRID_SIZE) * 2 +
                  m_bottomSize;

   //  int windowFixedSides = JFrame;
     Dimension newDim = new Dimension(width, height);

     this.setSize(newDim);

     Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
     Dimension frameSize = this.getSize();
     if (frameSize.height > screenSize.height) {
        frameSize.height = screenSize.height;
     }
     if (frameSize.width > screenSize.width) {
        frameSize.width = screenSize.width;
     }
     this.setLocation( (screenSize.width - frameSize.width) / 2,
                       (screenSize.height - frameSize.height) / 2);

  }

  private void jbInit() throws Exception {

     JPanel basePanel = (JPanel)this.getContentPane();

     JLabel range = new JLabel("Range:");
     m_northPanel.add(range);

     // Finds the number of digits, and adds a 1
     int fieldSize = (int)(Math.log(m_ht.size()) /
                           Math.log(10)) + 2;

     m_rangeFrom = new JTextFieldNumberOnly("0", fieldSize);
     m_rangeTo = new JTextFieldNumberOnly( Integer.toString(m_ht.size()-1) , fieldSize);

     m_northPanel.add(m_rangeFrom);
     m_northPanel.add(new JLabel("-"));
     m_northPanel.add(m_rangeTo);

     basePanel.add(m_prGridsPanel, BorderLayout.CENTER);
     basePanel.add(m_vSBar, BorderLayout.EAST);

     m_vSBar.setMinimum(0);
     m_vSBar.setMaximum(m_totalVerticalSize);

     m_vSBar.setUnitIncrement(m_nGridSideCells * PreprocessedGridList.CELL_SIZE / 5);

     m_vSBar.setBlockIncrement(m_nGridSideCells *
                               PreprocessedGridList.CELL_SIZE +
                               PreprocessedGridList.INFRAGRID_SIZE);


     basePanel.add(m_northPanel, BorderLayout.NORTH);
     pack();

     // Set Events
     this.addComponentListener(new ResizeListener());
     m_vSBar.addAdjustmentListener(new ScrollAdjustmentListener());
     m_rangeFrom.addActionListener(new FieldsEditListener());
     m_rangeTo.addActionListener(new FieldsEditListener());
  }


  class ScrollAdjustmentListener implements AdjustmentListener {

     public void adjustmentValueChanged(AdjustmentEvent e) {

        JScrollBar bar = (JScrollBar)e.getSource();

        if (bar == m_vSBar) {
           //System.out.println("V Value: " + e.getValue());
           m_prGridsPanel.setVScroll(e.getValue());
        }
     }
  }

  class ResizeListener implements ComponentListener {

     public void componentResized(ComponentEvent e) {

        int newHeight = m_prGridsPanel.getHeight();
        m_vSBar.setVisibleAmount(newHeight);

        m_prGridsPanel.setDrawAreaSize();
     }

     public void componentHidden(ComponentEvent e) {}
     public void componentMoved(ComponentEvent e) {}
     public void componentShown(ComponentEvent e) {}

  }

  public class FieldsEditListener implements ActionListener {

     public void actionPerformed(ActionEvent e) {
        String from = m_rangeFrom.getText();
        String to = m_rangeTo.getText();

        int fromVal = 0;
        int toVal = 0;
        int size;

        // Checks if the string is too big for parseint
        int strFromSize = m_rangeFrom.getText().length();
        int strToSize = m_rangeTo.getText().length();

        int intMaxSize = Integer.toString(Integer.MAX_VALUE).length() - 1;

        if (strFromSize > intMaxSize) {
           from = Integer.toString(Integer.MAX_VALUE);
        }
        if (strToSize > intMaxSize) {
           to = Integer.toString(Integer.MAX_VALUE);
        }

        // Checks if the string is empty
        if (strFromSize > 0) {
           fromVal = Integer.parseInt(from);
        }

        if (strToSize > 0) {
           toVal = Integer.parseInt(to);
        }

        if (fromVal > toVal) {
           fromVal = toVal;
           toVal = fromVal + 1;
           m_rangeFrom.setText( Integer.toString(fromVal) );
           m_rangeTo.setText( Integer.toString(toVal) );
        }

        if (toVal > m_ht.size()-1) {
           toVal = m_ht.size()-1;
           m_rangeTo.setText( Integer.toString(toVal) );
        }

        size = toVal-fromVal+1;

        m_totalVerticalSize = (m_nGridSideCells *
                               PreprocessedGridList.CELL_SIZE +
                               PreprocessedGridList.INFRAGRID_SIZE) *
                               size + m_bottomSize;

        m_vSBar.setMaximum(m_totalVerticalSize);
        m_prGridsPanel.setRange(fromVal, size);
     }

  }
}