package norman.baba.algorithms;

import java.util.*;
import java.awt.*;
import javax.swing.*;

import norman.baba.grids.*;

/**
 * <p>Title: BABA</p>
 * <p>Description: Bioinformatique Research Project</p>
 * <p>Copyright: Copyright Norman Casagrande (c) 2003</p>
 * <p>Company: </p>
 * @author Norman Casagrande
 * @version 1.0
 */

public class SmithWaterman extends NeedlemanWunsch {

   protected static final int PHASE_SELECT_LOCAL = 2;

   protected JLabel m_l4Choiche;

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

   public SmithWaterman(JPanel contentPane,
                        String defaultString1, String defaultString2) {
      super(contentPane, defaultString1, defaultString2);
   }

   public SmithWaterman(JPanel contentPane) {
      this(contentPane, "", "");
   }

   public String getAlgorithmName() {
      return "Smith & Waterman Local Search";
   }

   protected void setEast(JPanel rightPanel) {
      super.setEast(rightPanel);

      m_l4Choiche = new JLabel("0");
      m_l4Choiche.setOpaque(true);

      putFourDrawablePanel();

      m_currStatusPanel.add(m_l4Choiche,
                            new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0,
                            GridBagConstraints.WEST,
                            GridBagConstraints.NONE,
                            new Insets(0, 0, 0, 0), 0, 0));

   }

   protected void putFourDrawablePanel() {
      m_currStatusPanel.remove(m_dwPanel);
      m_dwPanel = new FourChsDrawablePanel();
      m_currStatusPanel.add(m_dwPanel,
                            new GridBagConstraints(1, 0, 1, 4, 0.0, 0.0,
          GridBagConstraints.CENTER,
          GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 15, 50));
      m_l4Choiche.setVisible(true);
   }

   protected void putOriginalDrawablePanel() {
      m_currStatusPanel.remove(m_dwPanel);
      m_dwPanel = new DrawablePanel();
      m_currStatusPanel.add(m_dwPanel,
                            new GridBagConstraints(1, 0, 1, 4, 0.0, 0.0,
          GridBagConstraints.CENTER,
          GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 15, 50));
      m_l4Choiche.setVisible(false);
   }

   // Default GAP scores
   protected void setDefaultGap(int whichString) {
      switch (whichString) {
         case STRING_ONE:
            this.setGapSequence(this.getGapKSequence(GAP_ONE, -1), GAP_ONE);
            break;
         case STRING_TWO:
            this.setGapSequence(this.getGapKSequence(GAP_TWO, -1), GAP_TWO);
            break;
      }
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
               m_scoreHash.setScore(a, b, 2);
            }
            else {
               m_scoreHash.setScore(a, b, -1);
            }
         }
      }
   }

   protected void stepForward(boolean showSteps) {

     switch (m_currentPhase) {

        case PHASE_CALC_GRID:

           if (m_currentStep >=
               (m_dpTable.getHCellsCount() - 2) *
               (m_dpTable.getVCellsCount() - 2)) {

              // Entering backtrack phase
              m_currentPhase = PHASE_SELECT_LOCAL;

              m_dpTable.clearDPHighlights();
              m_dpTable.clearAllArrows();
              m_dpTable.clearGridCircle();
              m_lDEqual.setText("D(x, x) = Select");
              m_l1Choiche.setText("No Pointer");
              m_l2Choiche.setText("No Pointer");
              m_l3Choiche.setText("No Pointer");
              m_l1Choiche.setBackground(m_mainPane.getBackground());
              m_l2Choiche.setBackground(m_mainPane.getBackground());
              m_l3Choiche.setBackground(m_mainPane.getBackground());

              putOriginalDrawablePanel();

              this.findMaxValues();
              setInfoMessage("Select Starting local!");

              //stepFWDBackTrack(showSteps);
           }
           else {
              setInfoMessage("Calculating DP Table. Step: " +
                                  m_currentStep);
              stepFWDCalc(showSteps);
           }

           break;

        case PHASE_SELECT_LOCAL:

           this.initBackward();

        case PHASE_BACKTRACK:

           stepFWDBackTrack(showSteps);
           break;
     }

  }

  protected void stepBackward() {

     CellElement currentCell;

     switch (m_currentPhase) {

        case PHASE_CALC_GRID:

           m_currentStep--;

           if (m_currentStep <= 0) {
              stepZero();
              return;
           }

           Point realD = getCoordsByStep(m_currentStep);
           currentCell = m_dpTable.getCell(realD.x, realD.y);

           // erasing pointers
           currentCell.clearAll();
           m_currentStep--;

           stepForward(true);

           break;

        case PHASE_SELECT_LOCAL:

          putFourDrawablePanel();

           m_currentStep--;
           m_currentPhase = PHASE_CALC_GRID;
           m_dpTable.clearHighlightColors();
           m_dpTable.clearInteractiveCells();
           stepForward(true);
           break;

        case PHASE_BACKTRACK:

           currentCell = (CellElement) m_backTrackList.getLast();
           currentCell.clearColor();

           if (m_backTrackList.size() <= 1) {
              m_currentPhase = PHASE_SELECT_LOCAL;
              m_backTrackList.clear();
              m_dpTable.clearAllArrows();
              m_dpTable.clearGridCircle();
              m_dpTable.clearHighlightColors();
              m_dpTable.clearInteractiveCells();
              setInfoMessage("Select Starting local!");

              m_lDEqual.setText("D(x, x) = Select");
              m_l1Choiche.setText("No Pointer");
              m_l2Choiche.setText("No Pointer");
              m_l3Choiche.setText("No Pointer");

              this.findMaxValues();
           }
           else {

              m_backTrackList.removeLast();
              currentCell = (CellElement) m_backTrackList.getLast();
              currentCell.clearColor();

              if (m_backTrackList.size() == 0) {
                 m_backtrackLastSel = m_dpTable.getLastCell();
              }
              else {
                 m_backtrackLastSel = (CellElement) m_backTrackList.getLast();
                 m_backTrackList.removeLast();
              }

              // cut result string
              boolean toErase = false;
              for (int i = 0; i < 3; i++) {
                 if (m_resLine[i].length() > 1) {
                    m_resLine[i] = m_resLine[i].substring(2,
                        m_resLine[i].length());
                 }
                 else {
                    m_resLine[i] = "";
                    toErase = true;
                 }
              }

              if (toErase) {
                 m_bottomResultArea.setText("");
              }

              stepFWDBackTrack(true);
           }

           break;
     }

  }

  /**
   * Ovveride of stepFWDCalc.
   * Now uses scores and consider the fourth possibility 0
   * when computing D(i,j)
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
     m_l4Choiche.setBackground(SystemColor.control);

     CellElement leftCell = m_dpTable.getCell(realD.x - 1, realD.y);
     CellElement topCell = m_dpTable.getCell(realD.x, realD.y - 1);
     CellElement topLeftCell = m_dpTable.getCell(realD.x - 1, realD.y - 1);

     ScoredCellElement currentCell = (ScoredCellElement)m_dpTable.getCell(realD.x, realD.y);

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

//// DUMB BUG
//        String DLeft = "D(" + (D.y - 1) + ", " + (D.x) + ") + sc.= " +
//            leftCell.getVal() + " + " + scoreGapLeft + " = " + (leftCell.getIntVal() + scoreGapLeft);
//        String DTop = "D(" + (D.y) + ", " + (D.x - 1) + ") + sc.= " +
//            topCell.getVal() + " + " + scoreGapTop + " = " + (topCell.getIntVal() + scoreGapTop);

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
     int zero = 0;

     int max = Math.max(
                        Math.max(fromLeftVal,
                                 Math.max(fromTopVal, fromTopLeftVal)), 0);

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
     if (max == 0) {
        m_l4Choiche.setBackground(Color.yellow);
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

  /**
   * Ovveride of stepFWDBackTrack: we need to change the behaviour
   * when in the local search it finds a zero.
   * @param showSteps
   */
  protected void stepFWDBackTrack(boolean showSteps) {

     // Global alignment: start from D(m,n)

     // TODO: Not very elegant. To be changed!
     CellElement theOneBefore = null;
     if (!m_backTrackList.isEmpty()) {
        theOneBefore = (CellElement) m_backTrackList.getLast();
     }

     if (m_backtrackLastSel == null) {
        // Policy for automatic pointer selection!!
        m_backTrackList.add(theOneBefore.getPointerWithPolicy(
            m_backtrackingPolicy));
     }
     else {
        m_backTrackList.add(m_backtrackLastSel);
     }

     CellElement currentCell = (CellElement) m_backTrackList.getLast();

     if (m_backTrackList.size() > 1) {
        setResultString(theOneBefore, currentCell);
     }

     Point D = new Point(currentCell.getColumn() - 1, currentCell.getRow() - 1);

     m_l1Choiche.setBackground(m_mainPane.getBackground());
     m_l2Choiche.setBackground(m_mainPane.getBackground());
     m_l3Choiche.setBackground(m_mainPane.getBackground());
     m_l4Choiche.setBackground(m_mainPane.getBackground());

     CellElement leftCell = currentCell.getLeftPointer();
     CellElement topCell = currentCell.getTopPointer();
     CellElement topLeftCell = currentCell.getDiagPointer();

     String DEqual = "D(" + (D.y) + ", " + (D.x) + ") = Select";
     String DLeft = "";
     String DTop = "";
     String DTopLeft = "";

     m_dpTable.clearInteractiveCells();

     // Init choosen array
     LinkedList highlightList = new LinkedList();

     if (leftCell == null) {
        DLeft = "No Pointer";
     }
     else {
        DLeft = "D(" + (leftCell.getRow() - 1) + ", " +
            (leftCell.getColumn() - 1) + ")";
        m_dpTable.addInteractiveCell(leftCell);
        highlightList.add(leftCell);
     }

     if (topCell == null) {
        DTop = "No Pointer";
     }
     else {
        DTop = "D(" + (topCell.getRow() - 1) + ", " +
            (topCell.getColumn() - 1) + ")";
        m_dpTable.addInteractiveCell(topCell);
        highlightList.add(topCell);
     }

     if (topLeftCell == null) {
        DTopLeft = "No Pointer";
     }
     else {
        DTopLeft = "D(" + (topLeftCell.getRow() - 1) + ", " +
            (topLeftCell.getColumn() - 1) + ")";
        m_dpTable.addInteractiveCell(topLeftCell);
        highlightList.add(topLeftCell);
     }

     m_lDEqual.setText(DEqual);
     m_l1Choiche.setText(DLeft);
     m_l2Choiche.setText(DTop);
     m_l3Choiche.setText(DTopLeft);

     m_dpTable.setTriArrows(currentCell, false);
     m_dpTable.setMultipleCellHighlight(highlightList);

     currentCell.setColor(Color.green);

     if (currentCell.getIntVal() == 0) {

        m_btnNext.setEnabled(false);
        m_btnEnd.setEnabled(false);
        m_dpTable.clearAllArrows();
        m_dpTable.clearGridCircle();

        m_dpTable.clearInteractiveCells();
        m_dpTable.clearDPHighlights();
     }
     else {
        // TODO: Not elegant. To be changed
        m_btnNext.setEnabled(true);
        m_btnEnd.setEnabled(true);
     }

     if (showSteps) {
        m_dpTable.paint(m_dpTable.getGraphics());
     }

     m_backtrackLastSel = null;
  }

  protected void stepZero() {

     switch (m_currentPhase) {
        case PHASE_SELECT_LOCAL:
           stepBackward();
           break;

        case PHASE_BACKTRACK:
           super.stepZero();
           m_lDEqual.setText("D(x, x) = Select");
           m_l1Choiche.setText("No Pointer");
           m_l2Choiche.setText("No Pointer");
           m_l3Choiche.setText("No Pointer");
           break;

        case PHASE_CALC_GRID:
           super.stepZero();
           this.putFourDrawablePanel();

           break;
     }
     m_l4Choiche.setBackground(m_mainPane.getBackground());
 }

  protected void stepEnd() {
     switch (m_currentPhase) {
        case PHASE_SELECT_LOCAL:
           stepForward(true);
           break;

        default:
           super.stepEnd();
     }
  }

  protected void findMaxValues() {

     m_dpTable.clearInteractiveCells();

     int c, r;
     CellElement tmpCell;

     int max = Integer.MIN_VALUE;
     LinkedList maxEls = new LinkedList();

     for (r = 2; r < m_dpTable.getVCellsCount(); ++r) {
        for (c = 2; c < m_dpTable.getHCellsCount(); ++c) {

           tmpCell = m_dpTable.getCell(c, r);

           if (tmpCell.getIntVal() > max) {
              maxEls.clear();
              maxEls.add(tmpCell);
              max = tmpCell.getIntVal();
           }
           else if (tmpCell.getIntVal() == max) {
              maxEls.add(tmpCell);
           }

        }
     }

     Iterator lIt = maxEls.iterator();

     while (lIt.hasNext()) {
        tmpCell = (CellElement)lIt.next();
        // highlight of the starting local point
        tmpCell.setHLColor(Color.blue);
        m_dpTable.addInteractiveCell(tmpCell);
     }

     // Selects by default the last max element:
     m_backtrackLastSel = (CellElement)maxEls.getLast();

     m_dpTable.paint(m_dpTable.getGraphics());
  }

  public void onInteractPress(CellElement cellPressed) {

     if (m_currentPhase == PHASE_SELECT_LOCAL) {
        this.initBackward();
     }

     super.onInteractPress(cellPressed);

  }

  protected void initBackward() {

     m_currentPhase = PHASE_BACKTRACK;
     setInfoMessage("Backtracking Pointers. Policy used: " +
                    CellElement.getPolicyName(
                    m_backtrackingPolicy) + ".");

    // Erase the highlights of the starting local point
     m_dpTable.clearHighlightColors();

     // reset result string
     for (int i = 0; i < 3; i++) {
        m_resLine[i] = "";
     }
  }

}
