package norman.baba.algorithms;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import javax.swing.text.DefaultCaret;

import norman.baba.grids.*;
import norman.baba.UI.*;
import norman.baba.utils.*;

/**
 * <p>Title: BABA</p>
 * <p>Description: Bioinformatique Research Project</p>
 * <p>Copyright: Copyright Norman Casagrande (c) 2003</p>
 * <p>Company: </p>
 * @author Norman Casagrande
 * @version 1.0
 *
 */

public class Nussinov
    extends SimpleDP
    implements CellInteractInterface {

   protected JButton m_btnScoreTable;
   protected ScoreHash m_scoreHash;
   protected JLabel m_l4Choiche;
   protected LinkedList m_bifuratedCells;
   protected int m_movement;

   protected final int BACKWARDS = 1;
   protected final int FORWARDS = 2;


   // Ridefine DrawablePanel
protected class FourChsDrawablePanel
    extends DrawablePanel {
   public void paintComponent(Graphics g) {

      int startY;
      int verticalPos = this.getSize().height / 2 - 6;
      int width = this.getSize().width - 6;

      // /
      startY = verticalPos - 5;
      g.drawLine(4, startY, width, startY - 15);

      // //
      startY = verticalPos - 1;
      g.drawLine(4, startY, width, startY);

      // \
      startY = verticalPos + 2;
      g.drawLine(4, startY, width, startY + 15);

      // \\
      startY = verticalPos + 6;
      g.drawLine(4, startY, width, startY + 22);

   }
}

   protected String m_alphabet;

   public Nussinov(JPanel contentPane,
                   String defaultString1) {
      super(contentPane, defaultString1, defaultString1);

      m_scoreHash = new ScoreHash();
      m_alphabet = "";
      m_bifuratedCells = new LinkedList();
   }

   public Nussinov(JPanel contentPane) {
      this(contentPane, "");
   }

   public String getAlgorithmName() {
      return "Nussinov";
   }

   /**
    * ********************** CENTER AREA *********************************
    */
   protected void setCenter() {
      m_dpTable = (NTable) new NTable(8,8);
      m_dpTable.setCellListener(this);

      m_gridScrollArea.add(m_dpTable);
   }

   /**
    * ********************** EAST AREA *********************************
    */

   protected void setEast(JPanel rightPanel) {

     /**
      * This panel is the only object in "rightPanel". I did this to
      * add some insets on every border.
      */
     JPanel rightInsPanel = new JPanel();
     rightInsPanel.setLayout(new GridBagLayout());

     /** In the "rightInsPanel" panel there is: */
     m_currStatusPanel.setLayout(new GridBagLayout());

     m_dwPanel = new FourChsDrawablePanel();

     JLabel stringOne_title = new JLabel("String S1:");

     ///////////////////////////////////////////////////////

     m_StringOne.setText(emptyStringMessage);
     m_StringTwo.setText(emptyStringMessage);
     m_StringOne.setEditable(false);

     m_StringOne.setBorder(m_defaultStringBorder);

     ///////////////////////////////////////////////////////

     m_l1Choiche.setOpaque(true);
     m_l2Choiche.setOpaque(true);
     m_l3Choiche.setOpaque(true);
     m_l4Choiche = new JLabel();
     m_l4Choiche.setOpaque(true);

     ///////////////////////////////////////////////////////

     m_btnBeginning.setEnabled(false);
     m_btnBeginning.setMargin(new Insets(2, 8, 2, 8));

     m_btnNext.setEnabled(false);

     m_btnPrev.setEnabled(false);
     m_btnPrev.setMargin(new Insets(2, 8, 2, 8));

     m_btnEnd.setEnabled(false);
     m_btnEnd.setMargin(new Insets(2, 8, 2, 8));

     m_clearPanel.setAlignmentX( (float) 0.0);
     m_clearPanel.setAlignmentY( (float) 0.0);
     m_btnClear.setEnabled(false);
     m_btnClear.setAlignmentX( (float) 0.5);
     m_btnClear.setMargin(new Insets(2, 15, 2, 14));

     /////////////////////////////////////////////////////////////////////////
     // The gridbag constraints mess
     /////////////////////////////////////////////////////////////////////////
     rightInsPanel.add(stringOne_title,
                       new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,
                                              GridBagConstraints.WEST,
                                              GridBagConstraints.NONE,
                                              new Insets(5, 0, 3, 0), 0, 0));
          ////////
     rightInsPanel.add(m_StringOne,
                       new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0,
                                              GridBagConstraints.WEST,
                                              GridBagConstraints.HORIZONTAL,
                                              new Insets(0, 0, 0, 0), 2, 0));

     rightInsPanel.add(m_btnSetOne,
                       new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
                                              GridBagConstraints.CENTER,
                                              GridBagConstraints.NONE,
                                              new Insets(0, 5, 0, 0), 0, -3));

     ////////////////////////////////////////////////////////////////////////
     rightInsPanel.add(m_currStatusPanel,
                       new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0,
                                              GridBagConstraints.WEST,
                                              GridBagConstraints.NONE,
                                              new Insets(5, 0, 5, 0), 0, 0));

     m_currStatusPanel.add(m_lDEqual,
                         new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                                                GridBagConstraints.WEST,
                                                GridBagConstraints.HORIZONTAL,
                                                new Insets(5, 0, 5, 0), 1, 0));
     m_currStatusPanel.add(m_dwPanel,
                         new GridBagConstraints(1, 0, 1, 4, 0.0, 0.0,
                                                GridBagConstraints.CENTER,
                                                GridBagConstraints.NONE,
                                                new Insets(0, 0, 0, 0), 15, 40));
     m_currStatusPanel.add(m_l1Choiche,
                         new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                                GridBagConstraints.WEST,
                                                GridBagConstraints.NONE,
                                                new Insets(0, 0, 0, 0), 0, 0));
     m_currStatusPanel.add(m_l2Choiche,
                         new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
                                                GridBagConstraints.WEST,
                                                GridBagConstraints.NONE,
                                                new Insets(0, 0, 0, 0), 0, 0));
     m_currStatusPanel.add(m_l3Choiche,
                         new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0,
                                                GridBagConstraints.WEST,
                                                GridBagConstraints.NONE,
                                                new Insets(0, 0, 0, 0), 0, 0));
     m_currStatusPanel.add(m_l4Choiche,
                           new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0,
                                                  GridBagConstraints.WEST,
                                                  GridBagConstraints.NONE,
                                                  new Insets(0, 0, 0, 0), 0, 0));

     ////////////////////////////////////////////////////////////////////////

     rightInsPanel.add(m_stepsButtonPanel,
                       new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0,
                                              GridBagConstraints.WEST,
                                              GridBagConstraints.NONE,
                                              new Insets(0, 0, 0, 0), 0, 0));

     rightInsPanel.add(m_clearPanel,
                       new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0,
                                              GridBagConstraints.WEST,
                                              GridBagConstraints.NONE,
                                              new Insets(0, 0, 0, 0), 0, 0));

     m_stepsButtonPanel.add(m_btnBeginning);
     m_stepsButtonPanel.add(m_btnPrev);
     m_stepsButtonPanel.add(m_btnNext);
     m_stepsButtonPanel.add(m_btnEnd);

     m_clearPanel.add(m_btnClear);

     rightPanel.add(rightInsPanel);

     m_btnSetOne.addActionListener(new SetButtonListener());

     m_btnBeginning.addActionListener(new MoveButtonListener());
     m_btnPrev.addActionListener(new MoveButtonListener());
     m_btnNext.addActionListener(new MoveButtonListener());
     m_btnEnd.addActionListener(new MoveButtonListener());
     m_btnClear.addActionListener(new MoveButtonListener());

      m_lDEqual.setText("D(i,j)= Max");

      m_l1Choiche.setText("D(i + 1, j)");
      m_l2Choiche.setText("D(i, j - 1)");
      m_l3Choiche.setText("D(i + 1, j - 1) + score");
      m_l4Choiche.setText("Max[D(i,k)+ D(k + 1,j)] (i<k<j)");
      m_l4Choiche.setOpaque(true);

      m_btnScoreTable = new JButton("Score Table");
      m_btnScoreTable.setEnabled(false);
      m_clearPanel.add(m_btnScoreTable);

      m_btnScoreTable.addActionListener(new ScoreButtonListener());
    }

//override simpleDP because there're no GAP-scores.
   protected boolean checkForBothStrings() {

         if (m_s1_size == 0){
            return false;
         }

         // NOTE: S1 = vertical, S2 = horizontal

         m_dpTable.setGridSize(m_s1_size + 1, m_s1_size + 1, true);
         int i;
         for (i = 0; i < m_s1_size; ++i) {
            m_dpTable.setCellValue(0, i + 1, m_s1.charAt(i));
            m_dpTable.setCellValue(i + 1, 0, m_s1.charAt(i));
         }

         updateInitCells();
         m_dpTable.setGridRectangle(1, 1, m_s1_size, m_s1_size);
         m_dpTable.setCellValue(0, 0, "D(i,j)");

         m_dpTable.repaint();
         m_gridScrollArea.doLayout();

         m_tableReady = true;
         m_btnNext.setEnabled(true);
         m_btnEnd.setEnabled(true);

         this.m_alphabet = this.buildAlphabet(m_s1);

         this.setDefaultScores();
         this.setCellScoresMatrix();

         m_btnScoreTable.setEnabled(true);

         return true;
   }

   protected void updateInitCells() {
       int i;

   CellElement tmpCell;

   for (i = 1; i< m_s1_size; ++i) {
      tmpCell = m_dpTable.getCell(i, i+1);
      tmpCell.setIntVal(0);
   }

   for (i = 0; i< m_s1_size ; ++i) {
      tmpCell = m_dpTable.getCell(i+1,i+1);
      tmpCell.setIntVal(0);
   }


   }

   protected String buildAlphabet(String s1) {
      HashSet hs = new HashSet();
      int i;

      String alphabet = "";

      for (i = 0; i < s1.length(); ++i) {
         hs.add(new Character(s1.charAt(i)));
      }

      Iterator it = hs.iterator();
      m_alphabet = "";
      while (it.hasNext()) {
         alphabet += ((Character)it.next()).charValue();
      }

      return alphabet;
        }

   // Default TABLE scores
   protected void setDefaultScores() {

      m_scoreHash.clearScores();

      int i, j;
      char a, b;
      for (i = 0; i < m_alphabet.length(); ++i) {
         for (j = 0; j < i+1; ++j) {

            a = m_alphabet.charAt(j);
            b = m_alphabet.charAt(i);

            if ((a == 'G' && b =='C' ) || (a == 'C' && b =='G' ) || (a == 'A' && b =='U' ) || (a == 'U' && b =='A' )) {
               m_scoreHash.setScore(a, b, 1);
            }
            else {
               m_scoreHash.setScore(a, b, 0);
            }
         }
      }
   }

   // There are no GAP scores
   protected void setDefaultGap(int whichString) {
      return;
   }

   public void setCellScoresMatrix() {

      int r, c, sc;
      char cS1, cS2;

      // NOTE: S1 = vertical, S2 = horizontal
       ScoredCellElement tmpCell;

      for (r = 0; r < m_s1_size; ++r) {
         for (c = 0; c < m_s1_size; ++c) {

             cS1 = m_s1.charAt(c);
             cS2 = m_s1.charAt(r);

          tmpCell = (ScoredCellElement)m_dpTable.getCell(c + 1, r + 1);

          if ( r <  c + 2 ) {
                 tmpCell.setScoreVal(m_scoreHash.getScore(cS1, cS2));
             } else {
                 tmpCell.setColor(Color.BLACK);
             }
         }

     }
   }

   protected void stepFWDCalc(boolean showSteps) {

      if (m_currentStep == 0) {
         m_btnSetOne.setEnabled(false);
         m_btnSetTwo.setEnabled(false);
         m_btnSetGapOne.setEnabled(false);
         m_btnSetGapTwo.setEnabled(false);
         m_btnScoreTable.setEnabled(false);

         m_btnPrev.setEnabled(true);
         m_btnBeginning.setEnabled(true);
      }

      Point realD = getCoordsByStep(m_currentStep);

      Point D = new Point(realD.x , realD.y );

      m_l1Choiche.setBackground(SystemColor.control);
      m_l2Choiche.setBackground(SystemColor.control);
      m_l3Choiche.setBackground(SystemColor.control);
      m_l4Choiche.setBackground(SystemColor.control);

      CellElement leftCell = m_dpTable.getCell(realD.x - 1, realD.y);
      CellElement bottomCell = m_dpTable.getCell(realD.x, realD.y + 1);
      CellElement bottomLeftCell = m_dpTable.getCell(realD.x - 1, realD.y + 1);

      NussinovCellElement currentCell = (NussinovCellElement)m_dpTable.getCell(realD.x, realD.y);

      int scoreCurrent = currentCell.getIntScoreVal();

      if (showSteps) {
         String DEqual = "D(" + (D.y) + ", " + (D.x) + ")= Max";

         String DLeft = "D(" + (D.y ) + ", " + (D.x - 1) + ") = " +
             leftCell.getVal() ;
         String DBottom = "D(" + (D.y + 1  ) + ", " + (D.x ) + ") = " +
             bottomCell.getVal();

         String DBottomLeft = "D(" + (D.y + 1) + ", " + (D.x - 1) + ") + sc.= " +
             bottomLeftCell.getVal() + " + " + scoreCurrent + " = " +
             (bottomLeftCell.getIntVal() + scoreCurrent);

         m_lDEqual.setText(DEqual);
         m_l1Choiche.setText(DLeft);
         m_l2Choiche.setText(DBottom);
         m_l3Choiche.setText(DBottomLeft);
      }

      int bifur,maxbifur;
      maxbifur = 0;
      CellElement k1Cell,k2Cell;

      int fromLeftVal = leftCell.getIntVal() ;
      int fromBottomVal = bottomCell.getIntVal()  ;
      int fromBottomLeftVal = bottomLeftCell.getIntVal() + scoreCurrent;

      Vector bifurCellVector = new Vector();

      // i < k < j
      for (int k = D.y+1; k < D.x; k++) {

          k1Cell = m_dpTable.getCell(k,D.y);
          k2Cell = m_dpTable.getCell(D.x,k+1);

          bifur = k1Cell.getIntVal()+k2Cell.getIntVal();
          if (bifur > maxbifur) {
              maxbifur = bifur;
              bifurCellVector.clear();
          }
          if (bifur == maxbifur) {
              bifurCellVector.add(new NCellPair(k1Cell,k2Cell));
          }
      }

      int max = Math.max(fromLeftVal, Math.max(fromBottomVal, Math.max(fromBottomLeftVal,maxbifur)));

      // Init choosen array
      LinkedList highlightList = new LinkedList();

      if (fromLeftVal == max) {
         m_l1Choiche.setBackground(Color.yellow);
         currentCell.addLeftPointer(leftCell);
         highlightList.add(leftCell);

      }
      if (fromBottomVal == max) {
         m_l2Choiche.setBackground(Color.yellow);
         currentCell.addBottomPointer(bottomCell);
         highlightList.add(bottomCell);
      }
      if (fromBottomLeftVal == max) {
         m_l3Choiche.setBackground(Color.yellow);
         currentCell.addDiagPointer(bottomLeftCell);
         highlightList.add(bottomLeftCell);
      }

      if (maxbifur == max) {
          m_l4Choiche.setBackground(Color.yellow);
          ListIterator it = bifurCellVector.listIterator();
          NCellPair pair;

          while (it.hasNext()) {
              pair = (NCellPair)it.next();
              currentCell.addBifPointers(pair);
              highlightList.addAll(pair.getCellPair());
          }
      }

      currentCell.setIntVal(max);

      if (showSteps) {
         m_dpTable.setSideHighlight(currentCell, new Color(0, 255, 255));

         m_dpTable.setTriArrows(currentCell, true);
         m_dpTable.setMultipleCellHighlight(highlightList);
         m_dpTable.paint(m_dpTable.getGraphics());
      }

      m_currentStep++;
   }

   protected void stepFWDBackTrack(boolean showSteps) {
       m_l1Choiche.setBackground(m_mainPane.getBackground());
       m_l2Choiche.setBackground(m_mainPane.getBackground());
       m_l3Choiche.setBackground(m_mainPane.getBackground());
       m_l4Choiche.setBackground(m_mainPane.getBackground());

       NussinovCellElement theOneBefore = null;

       if (!m_backTrackList.isEmpty()) {
           theOneBefore = (NussinovCellElement) m_backTrackList.getLast();
       }
       else {
           theOneBefore = null;
       }
        if (m_backtrackLastSel == null) {
           //user didn't select cell
           NussinovCellElement tmp = (NussinovCellElement) theOneBefore.getPointerWithPolicy(m_backtrackingPolicy);
           if (tmp==null) {
               NCellPair tmppair=theOneBefore.getFirstBifPointers();;
               m_backTrackList.add(tmppair.getCellOne());
               m_bifuratedCells.addLast(tmppair.getCellTwo());
           }
           else {
               m_backTrackList.add(tmp);
           }
       }
       else {
           // user did select cell!
           if (theOneBefore == null) {
               m_backTrackList.add(m_backtrackLastSel);
           }
           else {
               if (theOneBefore.isBifCell(m_backtrackLastSel)) { //user select a bif-cell pair
                   m_backTrackList.add(m_backtrackLastSel);
                   m_bifuratedCells.addLast(theOneBefore.getNextCellFromPair(
                           m_backtrackLastSel));
               } else {
                   m_backTrackList.add(m_backtrackLastSel);
               }
           }
       }

       NussinovCellElement currentCell = (NussinovCellElement)
                                             m_backTrackList.getLast();

       if (m_backTrackList.size() > 1 ) {
           setResultString(theOneBefore, currentCell);
       } else {
           initResultString();
       }


       Point D = new Point(currentCell.getColumn(), currentCell.getRow());

       CellElement leftCell = currentCell.getLeftPointer();
       CellElement BottomCell = currentCell.getBottomPointer();
       CellElement BottomLeftCell = currentCell.getDiagPointer();

       String DEqual = "D(" + (D.y) + ", " + (D.x) + ") = Select";
       String DLeft = "";
       String DBottom = "";
       String DBottomLeft = "";
       String DBifuricate = "";

       m_dpTable.clearInteractiveCells();

       // Init choosen array
       LinkedList highlightList = new LinkedList();

       if (leftCell == null) {
           DLeft = "No Pointer";
       } else {
           DLeft = "D(" + leftCell.getRow() + ", " + leftCell.getColumn() + ")";
           m_dpTable.addInteractiveCell(leftCell);
           highlightList.add(leftCell);
       }

       if (BottomCell == null) {
           DBottom = "No Pointer";
       } else {
           DBottom = "D(" + BottomCell.getRow() + ", " + BottomCell.getColumn() +
                     ")";
           m_dpTable.addInteractiveCell(BottomCell);
           highlightList.add(BottomCell);
       }

       if (BottomLeftCell == null) {
           DBottomLeft = "No Pointer";
       } else {
           DBottomLeft = "D(" + BottomLeftCell.getRow() + ", " +
                         BottomLeftCell.getColumn() + ")";
           m_dpTable.addInteractiveCell(BottomLeftCell);
           highlightList.add(BottomLeftCell);
       }

       int n = 0;
       NCellPair cellPair;
       ListIterator a = currentCell.getBifPointers().listIterator();

        while (a.hasNext()) {
            cellPair = (NCellPair)a.next();
            n++;
            highlightList.addAll(cellPair.getCellPair());
            ((NTable)m_dpTable).addInteractiveCell(cellPair);
        }

        if (n==0) {
            DBifuricate = "No Pointers";
        }
        else {
            DBifuricate = n + " Pointer Pair" + (n>1?"s":"");
        }

        m_lDEqual.setText(DEqual);
        m_l1Choiche.setText(DLeft);
        m_l2Choiche.setText(DBottom);
        m_l3Choiche.setText(DBottomLeft);
        m_l4Choiche.setText(DBifuricate);

        m_dpTable.setTriArrows(currentCell, false);
        m_dpTable.setMultipleCellHighlight(highlightList);

        currentCell.setColor(Color.green);

        if (currentCell.isEndCell() && m_bifuratedCells.isEmpty()) {
           m_btnNext.setEnabled(false);
           m_btnEnd.setEnabled(false);
           m_dpTable.clearAllArrows();
           m_dpTable.clearGridCircle();
        }
        else {
           m_btnNext.setEnabled(true);
           m_btnEnd.setEnabled(true);
        }

        if (showSteps) {
           m_dpTable.paint(m_dpTable.getGraphics());
        }

        if (currentCell.isEndCell() && !m_bifuratedCells.isEmpty()) {
            m_backtrackLastSel = (CellElement)this.m_bifuratedCells.removeLast();
        }
        else {
            m_backtrackLastSel = null;
        }
   }

   protected void stepZero() {

   switch (m_currentPhase) {
      case PHASE_BACKTRACK:
         ListIterator lIt = m_backTrackList.listIterator();
         while (lIt.hasNext()) {
            m_backtrackLastSel = (CellElement) lIt.next();
            lIt.remove();
            m_backtrackLastSel.clearColor();
         }

         m_backTrackList.add(m_backtrackLastSel);

         m_dpTable.clearDPHighlights();
         m_dpTable.clearAllArrows();
         m_btnNext.setEnabled(true);
         m_btnEnd.setEnabled(true);

         for (int i = 0; i < 3; i++) {
            m_resLine[i] = "";
         }

         stepBackward();
         break;

      case PHASE_CALC_GRID:
         m_btnScoreTable.setEnabled(true);
         m_currentStep = 0;

         m_dpTable.clearDPTableContent();
         m_dpTable.clearDPHighlights();

         m_dpTable.clearAllArrows();
         m_dpTable.clearGridCircle();

         m_backTrackList.clear();

         m_l1Choiche.setBackground(m_mainPane.getBackground());
         m_l2Choiche.setBackground(m_mainPane.getBackground());
         m_l3Choiche.setBackground(m_mainPane.getBackground());
         m_l4Choiche.setBackground(m_mainPane.getBackground());

         m_lDEqual.setText("D(i,j)= Max");
         m_l1Choiche.setText("D(i + 1, j)");
         m_l2Choiche.setText("D(i, j - 1)");
         m_l3Choiche.setText("D(i + 1, j - 1) + score");
         m_l4Choiche.setText("Max[D(i,k)+ D(k + 1,j)] (i<k<j)");

         setInfoMessage("Waiting..");

         m_btnPrev.setEnabled(false);
         m_btnBeginning.setEnabled(false);

         m_btnSetOne.setEnabled(true);
         m_btnSetTwo.setEnabled(true);
         m_btnSetGapOne.setEnabled(true);
         m_btnSetGapTwo.setEnabled(true);

         m_currentPhase = PHASE_CALC_GRID;
         m_bottomResultArea.setText("");

         m_dpTable.paint(m_dpTable.getGraphics());

         break;
   }

}

   protected class ScoreButtonListener
           implements ActionListener {

      public void actionPerformed(ActionEvent event) {
         // Perform the action indicated by a mouse click on a button.

         Object b = event.getSource(); // Get the component that was
         // clicked.

         if (b == m_btnScoreTable) {
            //String alphabet = "AGUC";
            ScoreDialog a = new ScoreDialog(m_alphabet, m_scoreHash);
            a.show();

            setCellScoresMatrix();
            m_dpTable.paint(m_dpTable.getGraphics());
         }
      }
   }

   protected class SetButtonListener
           implements ActionListener {

        public void actionPerformed(ActionEvent event) {
           // Perform the action indicated by a mouse click on a button.

           Object b = event.getSource(); // Get the component that was clicked.

           Object[] message = new Object[2];
           String title;
           JTextField stringField;

           String[] options;


           title = "Setting string";
           message[0] = "Input the String S1 (i.e. GGGAAAUCC):";

           stringField = new JTextFieldUC();
           stringField.setText(m_defS1);
           stringField.selectAll();

           options = new String[2];
           options[0] = "Ok";
           options[1] = "Cancel";

           message[1] = stringField;

// Options
           int result = JOptionPane.showOptionDialog(
               new JPanel(), // the parent that the dialog blocks
               message, // the dialog message array
               title, // the title of the dialog window
               JOptionPane.DEFAULT_OPTION, // option type
               JOptionPane.QUESTION_MESSAGE, // message type
               null, // optional icon, use null to use the default icon
               options, // options string array, will be made into buttons
               message[0] // option that should be made into a default button
               );

            if (result == 0) {
                m_defS1 = stringField.getText();
                m_defS2 = m_defS1;
                setString(m_defS1, STRING_ONE);
                setString(m_defS2, STRING_TWO);
            }

        }
     }

  protected void stepForward(boolean showSteps) {
        m_movement = FORWARDS;
        int l  = m_dpTable.getHCellsCount() -1;

        switch (m_currentPhase) {

           case PHASE_CALC_GRID:

              if (m_currentStep >=
                  ((l*l - l) / 2) )  {

                 // Entering backtrack phase
                 m_currentPhase = PHASE_BACKTRACK;

                 m_backtrackLastSel = m_dpTable.getCell( l,1 );
                 m_bifuratedCells.clear();
                 m_backTrackList.clear();

                 m_dpTable.clearDPHighlights();
                 m_dpTable.clearAllArrows();

                 // reset result string
                 initResultString();


                 m_bottomResultArea.setText(m_resLine[0] + "\n" +
                                m_resLine[1] + "\n");

                 setInfoMessage("Backtracking Pointers. Policy used: " +
                                 CellElement.getPolicyName(
                                 m_backtrackingPolicy) + ".");
                 stepFWDBackTrack(showSteps);
              }
              else {
                 setInfoMessage("Calculating DP Table. Step: " +
                                m_currentStep);
                 stepFWDCalc(showSteps);
              }

              break;

           case PHASE_BACKTRACK:

              stepFWDBackTrack(showSteps);
              break;
        }

     }

     protected void initResultString() {
         m_resLine[0] = this.m_s1;

         StringBuffer s=new StringBuffer();
         for (int i=0 ; i < m_s1.length(); i++){
             s.append('.');
         }

         m_resLine[1] = s.toString();
         m_resLine[2] = "";
         this.m_bottomResultArea.setText(m_resLine[0] + '\n' + m_resLine[1] + '\n' );

     }

     protected void setResultString(ScoredCellElement prevCell,
                               ScoredCellElement selectedBackTrackingCell) {

    StringBuffer sb = new StringBuffer(m_resLine[1]);

    if (m_movement==FORWARDS) {
         if (prevCell.getDiagPointer() == selectedBackTrackingCell &&
             Integer.parseInt(prevCell.getScoreVal())>0 ) {

             sb.setCharAt(prevCell.getColumn()-1, ')');
             sb.setCharAt(prevCell.getRow()-1, '(');
         }
         m_resLine[1] = sb.toString();
     } else {
            sb.setCharAt(selectedBackTrackingCell.getColumn()-1, '.');
            sb.setCharAt(selectedBackTrackingCell.getRow()-1, '.');
            m_resLine[1] = sb.toString();
        }

         m_bottomResultArea.setText(m_resLine[0] + "\n" +
                                    m_resLine[1] + "\n");
  }

     protected void stepEnd() {

           int i;
           int totSize = (m_s1_size * m_s1_size - m_s1_size) /2 ;
           m_dpTable.clearDPHighlights();
           m_dpTable.clearAllArrows();
           m_dpTable.clearGridCircle();

           switch (m_currentPhase) {

              case PHASE_CALC_GRID:
                 for (i = m_currentStep; i < totSize; ++i) {
                    stepForward(false);
                 }

                 stepForward(true);
                 break;

              case PHASE_BACKTRACK:

                 while (m_btnEnd.isEnabled()) {
                    stepForward(false);
                 }
                 m_dpTable.paint(m_dpTable.getGraphics());
           }
        }

     // our steps are diagonal
     protected Point getCoordsByStep(int step) {
           Point ret = new Point();

           int i = m_s1_size - 1;
           int n = 1;

           while (step - i  >= 0) {
               step-=i;
               n ++;
               i --;
           }

           ret.x = step + n + 1;
           ret.y = step + 1;

           return ret;
   }

   protected void stepBackward() {

         CellElement currentCell;
         m_movement = BACKWARDS;
         switch (m_currentPhase) {

            case PHASE_CALC_GRID:
                super.stepBackward();
                break;
            case PHASE_BACKTRACK:
               currentCell = (CellElement) m_backTrackList.getLast();
               currentCell.clearColor();

               if (m_backTrackList.size() <= 1) {
                  m_currentStep--;
                  m_currentPhase = PHASE_CALC_GRID;
                  m_backTrackList.clear();
                  m_dpTable.clearInteractiveCells();
                  stepForward(true);
               }
               else {

                  m_backTrackList.removeLast();
                  currentCell = (CellElement) m_backTrackList.getLast();
                  currentCell.clearColor();

                  if (m_backTrackList.size() == 0) {
                     m_backtrackLastSel = m_dpTable.getLastCell();
                     m_bottomResultArea.setText("");
                  }
                  else {
                     m_backtrackLastSel = (CellElement) m_backTrackList.getLast();
                     m_backTrackList.removeLast();
                  }

                  stepFWDBackTrack(true);
               }

               break;
         }

      }


}
