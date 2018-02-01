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

public class NeedlemanWunsch
    extends SimpleDP
    implements CellInteractInterface {

   protected JButton m_btnScoreTable;
   protected ScoreHash m_scoreHash;

   protected String m_alphabet;

   public NeedlemanWunsch(JPanel contentPane,
                   String defaultString1, String defaultString2) {
      super(contentPane, defaultString1, defaultString2);

      m_scoreHash = new ScoreHash();
      m_alphabet = "";
   }

   public NeedlemanWunsch(JPanel contentPane) {
      this(contentPane, "", "");
   }

   public String getAlgorithmName() {
      return "Needleman & Wunsch Dynamic Programming";
   }

   /**
    * ********************** CENTER AREA *********************************
    */
   protected void setCenter() {
      m_dpTable = new NWTable(10, 6);
      m_dpTable.setCellListener(this);

      m_gridScrollArea.add(m_dpTable);
   }

   /**
    * ********************** EAST AREA *********************************
    */
   protected void setEast(JPanel rightPanel) {

      super.setEast(rightPanel);

      m_gapOne_title.setText("S1 Score Gap Penalty Array:");
      m_gapTwo_title.setText("S2 Score Gap Penalty Array:");

      m_lDEqual.setText("D(S1,S2)= Max");

      m_l1Choiche.setText("D(S1-1, S2) + score");
      m_l2Choiche.setText("D(S1, S2-1) + score");
      m_l3Choiche.setText("D(S1 - 1, S2 - 1) + score");

      m_btnScoreTable = new JButton("Score Table");
      m_btnScoreTable.setEnabled(false);
      m_clearPanel.add(m_btnScoreTable);

      m_btnScoreTable.addActionListener(new ScoreButtonListener());

   }

   protected boolean checkForBothStrings() {
      if (super.checkForBothStrings() == false) {
         return false;
      }

      this.m_alphabet = this.buildAlphabet(m_s1, m_s2);

      this.setDefaultScores();
      this.setCellScoresMatrix();

      m_btnScoreTable.setEnabled(true);

      return true;
   }

   protected String buildAlphabet(String s1, String s2) {
      HashSet hs = new HashSet();
      int i;

      String alphabet = "";

      for (i = 0; i < s1.length(); ++i) {
         hs.add(new Character(s1.charAt(i)));
      }

      for (i = 0; i < s2.length(); ++i) {
         hs.add(new Character(s2.charAt(i)));
      }

      Iterator it = hs.iterator();
      m_alphabet = "";
      while (it.hasNext()) {
         alphabet += ((Character)it.next()).charValue();
      }

      return alphabet;
      //System.out.println("Alphabet: " + alphabet);
   }

   // Default TABLE scores
   protected void setDefaultScores() {

      m_scoreHash.clearScores();

      int i, j;
      char a, b;
      for (i = 0; i < m_alphabet.length(); ++i) {
         for (j = 0; j < i+1; ++j) {
            //System.out.println("A: " + m_alphabet.charAt(j) +
            //                   " - B: " + m_alphabet.charAt(i));

            a = m_alphabet.charAt(j);
            b = m_alphabet.charAt(i);

            if (a == b) {
               m_scoreHash.setScore(a, b, 1);
            }
            else {
               m_scoreHash.setScore(a, b, 0);
            }
         }
      }
   }

   // Default GAP scores
   protected void setDefaultGap(int whichString) {
      switch (whichString) {
         case STRING_ONE:
            this.setGapSequence(this.getGapKSequence(GAP_ONE, -6), GAP_ONE);
            break;
         case STRING_TWO:
            this.setGapSequence(this.getGapKSequence(GAP_TWO, -6), GAP_TWO);
            break;
      }
   }

   public void setCellScoresMatrix() {

      int r, c;
      char cS1, cS2;

      // NOTE: S1 = vertical, S2 = horizontal
      ScoredCellElement tmpCell;

      for (r = 0; r < m_s1_size; ++r) {
         for (c = 0; c < m_s2_size; ++c) {

            cS1 = m_s2.charAt(c);
            cS2 = m_s1.charAt(r);

            tmpCell = (ScoredCellElement)m_dpTable.getCell(c+2, r+2);
            tmpCell.setScoreVal(m_scoreHash.getScore(cS1, cS2));
         }
      }

   }

   /**
    * Ovveride of UpdateGapCells.
    * Now gaps are sums of the scores of the gaps cells.
    */
   protected void updateGapCells() {
      int i;
      int totScore = 0;

      ScoredCellElement prevCell = (ScoredCellElement)m_dpTable.getCell(1, 1); // Cell Zero
      prevCell.setIntScoreVal(0);
      ScoredCellElement tmpCell;

      for (i = 0; i < m_gapPenaltyOne.length; ++i) {
         tmpCell = (ScoredCellElement)m_dpTable.getCell(1, i + 2);
         tmpCell.setIntScoreVal(m_gapPenaltyOne[i]);
         totScore += m_gapPenaltyOne[i];
         tmpCell.setIntVal(totScore);

         tmpCell.addTopPointer(prevCell);
         prevCell = tmpCell;
      }

      prevCell = (ScoredCellElement)m_dpTable.getCell(1, 1); // Cell Zero
      totScore = 0;
      for (i = 0; i < m_gapPenaltyTwo.length; ++i) {
         tmpCell = (ScoredCellElement)m_dpTable.getCell(i + 2, 1);
         tmpCell.setIntScoreVal(m_gapPenaltyTwo[i]);
         totScore += m_gapPenaltyTwo[i];
         tmpCell.setIntVal(totScore);

         tmpCell.addLeftPointer(prevCell);
         prevCell = tmpCell;
      }
   }

   /**
    * Ovverride of stepFWDCalc.
    * Now uses scores
    */
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

      Point D = new Point(realD.x - 1, realD.y - 1);

      m_l1Choiche.setBackground(SystemColor.control);
      m_l2Choiche.setBackground(SystemColor.control);
      m_l3Choiche.setBackground(SystemColor.control);

      CellElement leftCell = m_dpTable.getCell(realD.x - 1, realD.y);
      CellElement topCell = m_dpTable.getCell(realD.x, realD.y - 1);
      CellElement topLeftCell = m_dpTable.getCell(realD.x - 1, realD.y - 1);

      ScoredCellElement currentCell = (ScoredCellElement)m_dpTable.getCell(realD.x, realD.y);

//      char cS1, cS2;
//      cS1 = m_s1.charAt(realD.y - 2);
//      cS2 = m_s2.charAt(realD.x - 2);

      ScoredCellElement gapLeftCell = (ScoredCellElement)m_dpTable.getCell(1, realD.y);
      ScoredCellElement gapTopCell = (ScoredCellElement)m_dpTable.getCell(realD.x, 1);

      int scoreCurrent = Integer.parseInt(currentCell.getScoreVal());
      int scoreGapLeft = Integer.parseInt(gapLeftCell.getScoreVal());
      int scoreGapTop = Integer.parseInt(gapTopCell.getScoreVal());

      if (showSteps) {
         String DEqual = "D(" + (D.y) + ", " + (D.x) + ")= Max";

         String DLeft = "D(" + (D.y) + ", " + (D.x-1) + ") + sc.= " +
             leftCell.getVal() + " + " + scoreGapLeft + " = " + (leftCell.getIntVal() + scoreGapLeft);
         String DTop = "D(" + (D.y-1) + ", " + (D.x) + ") + sc.= " +
             topCell.getVal() + " + " + scoreGapTop + " = " + (topCell.getIntVal() + scoreGapTop);

         String DTopLeft = "D(" + (D.y - 1) + ", " + (D.x - 1) + ") + sc.= " +
             topLeftCell.getVal() + " + " + scoreCurrent + " = " +
             (topLeftCell.getIntVal() + scoreCurrent);

         m_lDEqual.setText(DEqual);
         m_l1Choiche.setText(DLeft);
         m_l2Choiche.setText(DTop);
         m_l3Choiche.setText(DTopLeft);
      }

      int fromLeftVal = leftCell.getIntVal() + scoreGapLeft;
      int fromTopVal = topCell.getIntVal() + scoreGapTop;
      int fromTopLeftVal = topLeftCell.getIntVal() + scoreCurrent;

      int max = Math.max(fromLeftVal, Math.max(fromTopVal, fromTopLeftVal));

      // Init choosen array
      LinkedList highlightList = new LinkedList();

      if (fromLeftVal == max) {
         m_l1Choiche.setBackground(Color.yellow);
         currentCell.addLeftPointer(leftCell);
         highlightList.add(leftCell);
         highlightList.add(gapLeftCell);

      }
      if (fromTopVal == max) {
         m_l2Choiche.setBackground(Color.yellow);
         currentCell.addTopPointer(topCell);
         highlightList.add(topCell);
         highlightList.add(gapTopCell);
      }
      if (fromTopLeftVal == max) {
         m_l3Choiche.setBackground(Color.yellow);
         currentCell.addDiagPointer(topLeftCell);
         highlightList.add(topLeftCell);
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

   protected void stepZero() {
      if (m_currentPhase == PHASE_CALC_GRID) {
         m_btnScoreTable.setEnabled(true);
      }
      super.stepZero();
      m_lDEqual.setText("D(S1,S2)= Max");

      m_l1Choiche.setText("D(S1-1, S2) + score");
      m_l2Choiche.setText("D(S1, S2-1) + score");
      m_l3Choiche.setText("D(S1 - 1, S2 - 1) + score");
   }

   ////////////////

   protected class ScoreButtonListener
       implements ActionListener {

      public void actionPerformed(ActionEvent event) {
         // Perform the action indicated by a mouse click on a button.

         Object b = event.getSource(); // Get the component that was
         // clicked.

         if (b == m_btnScoreTable) {
            //String alphabet = "AGTC";
            ScoreDialog a = new ScoreDialog(m_alphabet, m_scoreHash);
            a.show();

            setCellScoresMatrix();
            m_dpTable.paint(m_dpTable.getGraphics());
         }
      }
   }

}
