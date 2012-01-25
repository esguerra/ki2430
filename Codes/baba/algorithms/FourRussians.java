package norman.baba.algorithms;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.*;
import javax.swing.*;
import javax.swing.event.*;

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
 * Important! MAX t SIZE = 127!!!!
 */

public class FourRussians extends SimpleDP {

   public static final int PHASE_PREPROCESSING = 3;

   protected static final int MAX_T_SIZE = 6;
   protected static final int DEFAULT_T_SIZE = 3;
   protected int m_t = DEFAULT_T_SIZE;

   protected static final long PRECOMPILED_WARNING_LIMIT = 60000;

   protected String m_alphabet;
   protected Hashtable m_preprocTable;
   protected ArrayList m_orderedKeys;

   public JButton m_btnShowPreproc;
   public JSlider m_sldSetT;
   public JLabel m_tValue;

   private ChangeListener m_sliderChangeListener;

   protected byte[] m_stringsEncodedValues;
   protected int m_encodedAlphSize = 1;

   protected int m_nHBlocs = 0;
   protected int m_nVBlocs = 0;

   protected Vector m_validTValues = null;

   public FourRussians(JPanel contentPane,
                       String defaultString1, String defaultString2) {
      super(contentPane, defaultString1, defaultString2);

   }

   public FourRussians(JPanel contentPane) {
      this(contentPane, "", "");
   }

   public String getAlgorithmName() {
      return "Four Russians";
   }

   /**
    * ********************** CENTER AREA *********************************
    */
   protected void setCenter() {
      m_dpTable = new FRTable(10, 6, DEFAULT_T_SIZE);
      m_dpTable.setCellListener(this);

      m_gridScrollArea.add(m_dpTable);
   }

   /**
    * ********************** EAST AREA *********************************
    */
   protected void setEast(JPanel rightPanel) {

      super.setEast(rightPanel);

      m_clearPanel.remove(m_btnClear);
      m_stepsButtonPanel.add(m_btnClear);

      m_dwPanel.setVisible(false);
      m_lDEqual.setText("     ");
      m_l1Choiche.setText("      ");
      m_l2Choiche.setText("      ");
      m_l3Choiche.setText("      ");

      m_btnShowPreproc = new JButton("Show");
      m_btnShowPreproc.setEnabled(false);

      m_sldSetT = new JSlider(JSlider.HORIZONTAL, 2, 4, 3);
      m_tValue = new JLabel("t=3:");

      Dimension oldSize = m_sldSetT.getPreferredSize();
      m_sldSetT.setPreferredSize(new Dimension(80, oldSize.height));

      m_sldSetT.setSnapToTicks(true);
      m_sldSetT.setMinorTickSpacing(1);
      m_sldSetT.setPaintTicks(true);

      m_sldSetT.setEnabled(false);

      m_clearPanel.add(m_tValue);
      m_clearPanel.add(m_sldSetT);
      m_clearPanel.add(m_btnShowPreproc);

      m_sliderChangeListener = new SliderListener();
      m_sldSetT.addChangeListener(m_sliderChangeListener);
      m_btnShowPreproc.addActionListener(new ShowPrepListener());

   }

   protected boolean checkForBothStrings() {

      if (super.checkForBothStrings() == false) {
         return false;
      }

      m_validTValues = getValidTValues();
      if (m_validTValues.size() == 0) {
         System.err.println("NOT VALID!!");
      }
      else {
         m_sldSetT.setValueIsAdjusting(true);
         m_sldSetT.setMinimum(0);
         m_sldSetT.setMaximum(m_validTValues.size()-1);
         Integer tmpVal;
         m_sldSetT.setEnabled(true);

         // sets default value of 3 if present
         boolean found = false;
         for (int i = 0; i < m_validTValues.size(); ++i) {
            tmpVal = (Integer)m_validTValues.get(i);
            if (tmpVal.intValue() == 3) {
               m_sldSetT.setValue(i);
               found = true;
               break;
            }
         }

         if (!found) {
            m_sldSetT.setValue(0);
         }

         m_sldSetT.setValueIsAdjusting(false);
      }

      return true;
   }

   protected void resetFRGrid() {

      ((FRTable)m_dpTable).setBlocSize(m_t);
      this.partitionGrid();
      m_alphabet = this.buildAlphabet(m_s1, m_s2);

      m_sldSetT.setEnabled(true);
      m_btnEnd.setEnabled(false);
      m_btnShowPreproc.setEnabled(false);
      this.buildEncodedAlphabet();

      setInfoMessage("Total blocs to precompute: " +
                     getNumPrecomputedBlocsString());
      m_currentPhase = PHASE_PREPROCESSING;
   }

   /**
    * Parition the grid into t size rectangles
    */
   protected void partitionGrid() {

      int r, c;
      m_dpTable.clearGridRectangles();

      m_nHBlocs = (m_dpTable.getHCellsCount()-2) / (m_t - 1);
      m_nVBlocs = (m_dpTable.getVCellsCount()-2) / (m_t - 1);

      int stC,stR = 1;

      for (r = 0; r < m_nVBlocs; ++r) {
         stC = 1;
         for (c = 0; c < m_nHBlocs; ++c) {
            m_dpTable.addGridRectangle(stC, stR,
                                       m_t, m_t,
                                       2, Color.black);
            stC += m_t-1;
         }
         stR += m_t-1;
      }

      m_dpTable.paint(m_dpTable.getGraphics());

   }

   protected Vector getValidTValues() {

      Vector validT = new Vector();
      int nHCells = m_dpTable.getHCellsCount()-2;
      int nVCells = m_dpTable.getVCellsCount()-2;

      // MAX size of t = MAX_T_SIZE!!!!
      // MIN size of t = 2!!!
      for (int i = 2; i <= MAX_T_SIZE; ++i) {
         if (nHCells % (i-1) == 0 &&
             nVCells % (i-1) == 0) {
            validT.add(new Integer(i));
         }
      }

      return validT;
   }

   /**
    * Ovverride of UpdateGapCells.
    * Now gaps are just the sums to the previous.
    */
   protected void updateGapCells() {
      int i;
      int diffVal = 0;

      FRCellElement prevCell = (FRCellElement)m_dpTable.getCell(1, 1); // Cell Zero
      // Cells zero has always the alternative value and the scoreVal
      // (original value) = 0
      //prevCell.setAlternativeVal("0");
      prevCell.setIntScoreVal(0);

      FRCellElement tmpCell;

      for (i = 0; i < m_gapPenaltyOne.length; ++i) {
         tmpCell = (FRCellElement)m_dpTable.getCell(1, i + 2);
         tmpCell.setIntScoreVal(m_gapPenaltyOne[i]);

         diffVal = m_gapPenaltyOne[i] - prevCell.getIntScoreVal();
         tmpCell.setIntVal(diffVal);

         tmpCell.addTopPointer(prevCell);
         prevCell = tmpCell;
      }

      prevCell = (FRCellElement)m_dpTable.getCell(1, 1); // Cell Zero
      diffVal = 0;
      for (i = 0; i < m_gapPenaltyTwo.length; ++i) {
         tmpCell = (FRCellElement)m_dpTable.getCell(i + 2, 1);
         tmpCell.setIntScoreVal(m_gapPenaltyTwo[i]);

         diffVal = m_gapPenaltyTwo[i] - prevCell.getIntScoreVal();
         tmpCell.setIntVal(diffVal);

         tmpCell.addLeftPointer(prevCell);
         prevCell = tmpCell;
      }
   }

   /**
    * Overriding of the original setGapSequence. Now it checks if
    * the gaps have an offset of {-1, 0, 1}.
    * @param newGapSequence String with the new gap sequence in the format:
    * {x1, x2, x3, x4, ...} (where x is a number).
    * @param whichGapSequence The identifier of the gap sequence (first string
    * or second string).
    */
   protected void setGapSequence(String newGapSequence, int whichGapSequence) {

      int sLen = 0;

      switch (whichGapSequence) {
         case GAP_ONE:
            sLen = m_s1_size;
            break;
         case GAP_TWO:
            sLen = m_s2_size;
            break;
         default:
            System.err.println("Not a valid gap sequence ID!");
            return;
      }

      int tmpGapPenality[] = new int[sLen];
      String filledGapSequence = "{";

      StringTokenizer sTok = new StringTokenizer(newGapSequence, "{}, ");

      int val = 0;
      int prevVal = 0;

//      filledGapSequence += prevVal + ", ";
//      tmpGapPenality[0] = prevVal;

      for (int i = 0; i < sLen; ++i) {

         if (sTok.hasMoreTokens()) {
            try {
               val = Integer.parseInt(sTok.nextToken());
            }
            catch (NumberFormatException ex) {
               val = 0;
            }
         }
         else {
            val = 0;
         }

         if (Math.abs(val-prevVal) > 1) {
            // Error
            JOptionPane.showMessageDialog(null,
                                          "Invalid gap values!\nThey must have " +
                                          "an offset of {-1, 0, +1}!",
                                          "Invalid gaps",
                                          JOptionPane.ERROR_MESSAGE);
            return;
         }

         tmpGapPenality[i] = val;
         prevVal = val;
         filledGapSequence += val + ", ";
      }

      filledGapSequence = filledGapSequence.substring(0,
          filledGapSequence.length() - 2) + "}";

      switch (whichGapSequence) {
         case GAP_ONE:
            m_gapPenaltyOne = tmpGapPenality;
            m_gapOne.setText(filledGapSequence);
            break;
         case GAP_TWO:
            m_gapPenaltyTwo = tmpGapPenality;
            m_gapTwo.setText(filledGapSequence);
            break;
      }

      if (m_tableReady) {
         updateGapCells();
         //m_dpTable.repaint();
         m_dpTable.paint(m_dpTable.getGraphics());
      }

   }

   protected void stepForward(boolean showSteps) {

      switch (m_currentPhase) {
         case PHASE_PREPROCESSING:

            long precSize = getNumPrecomputedBlocs();

            if (precSize > Integer.MAX_VALUE) {
               JOptionPane.showMessageDialog(null,
                                             "Number of precompiled matrix too large!",
                                             "Error",
                                             JOptionPane.ERROR_MESSAGE);
               return;
            }

            // it's about 550 bytes for every matrix..
            int megs = (int)((double)(550 * precSize) / 1000000.0);

            NumberFormat nf = NumberFormat.getInstance();
            String precSizeStr = nf.format(precSize);

            if (precSize > PRECOMPILED_WARNING_LIMIT) {

               int retVal = JOptionPane.showConfirmDialog(null,
                   "Warning! The number of blocs to compute will be very large (" +
                   precSizeStr +
                   ").\n You will need a huge amount of memory (~" +
                   megs +
                   " MB).\n Do you still want to continue?", "Warning!",
                   JOptionPane.YES_NO_OPTION);

               if (retVal != JOptionPane.YES_OPTION) {
                  return;
               }
            }

            try {
               m_preprocTable = new Hashtable( (int) precSize);
               m_orderedKeys = new ArrayList( (int) precSize);
            }
            catch (java.lang.OutOfMemoryError e) {
               m_preprocTable = null;
               m_orderedKeys = null;
               Runtime.getRuntime().gc();
               JOptionPane.showMessageDialog(null,
                                             "Out of memory!\n (Please give " +
                                             "a second to the garbage collector " +
                                             "to clean this mess..).\n" +
                                             "Note: Il could be unstable anyway",
                                             "Didn't I tell you? :)",
                                             JOptionPane.ERROR_MESSAGE);

               Runtime.getRuntime().gc();
               Runtime.getRuntime().gc();
               return;

            }

            PreprocessingFrame prFrm =
                new PreprocessingFrame(m_preprocTable, m_orderedKeys,
                                       m_t, m_encodedAlphSize,
                                       this);

            prFrm.show();

            m_currentStep = 0;
            m_currentPhase = PHASE_CALC_GRID;

            break;

         case PHASE_CALC_GRID:

            if (m_currentStep >= m_nHBlocs * m_nVBlocs) {

               m_backtrackLastSel = m_dpTable.getLastCell();

               // reset result string
               for (int i = 0; i < 3; i++) {
                  m_resLine[i] = "";
               }

               setInfoMessage("Backtracking Pointers. Policy used: " +
                               CellElement.getPolicyName(
                               m_backtrackingPolicy) + ".");

               m_currentPhase = PHASE_BACKTRACK;
               ((FRTable)m_dpTable).setShowBackgroundIndexes(false);
               m_dpTable.clearHighlightColors();

               m_dwPanel.setVisible(true);
               m_l1Choiche.setVisible(true);
               m_l2Choiche.setVisible(true);
               m_l3Choiche.setVisible(true);

               stepFWDBackTrack(showSteps);

            }
            else {
               setInfoMessage("Applying preprocessed blocs. Step: " +
                              m_currentStep);
               this.stepFWDCalc(showSteps);
            }

            break;

         case PHASE_BACKTRACK:

            stepFWDBackTrack(showSteps);

            break;
      }
   }

   protected void stepBackward() {

      CellElement currentCell;
      FRTable dpTable = (FRTable)m_dpTable;
      Point blocPos;

      switch (m_currentPhase) {

         case PHASE_CALC_GRID:

            m_currentStep--;

            blocPos = getCoordsByStep(m_currentStep);
            dpTable.clearBloc(blocPos);

            if (m_currentStep <= 0) {
               stepZero();
               return;
            }

            // Twice because the current will be added by the stepForward!
            dpTable.clearLastBkIndex();
            dpTable.clearLastBkIndex();

            m_currentStep--;

            stepForward(true);

            break;

         case PHASE_BACKTRACK:

            if (m_backTrackList.size() <= 1) {
               currentCell = (CellElement) m_backTrackList.getLast();
               currentCell.clearColor();

               m_currentStep--;

               m_currentPhase = PHASE_CALC_GRID;

               m_backTrackList.clear();
               m_dpTable.clearInteractiveCells();
               m_dpTable.clearHighlightColors();
               m_dpTable.clearGridCircle();
               m_dpTable.clearAllArrows();

               dpTable.setShowBackgroundIndexes(true);
               dpTable.clearLastBkIndex();

               m_lDEqual.setText("     ");
               m_l1Choiche.setText("      ");
               m_l2Choiche.setText("      ");
               m_l3Choiche.setText("      ");
               m_dwPanel.setVisible(false);

               stepForward(true);
            }
            else {
               super.stepBackward();
            }

            break;
      }

   }

   protected void stepFWDCalc(boolean showSteps) {

      if (m_currentStep == 0) {
         m_btnSetOne.setEnabled(false);
         m_btnSetTwo.setEnabled(false);
         m_btnSetGapOne.setEnabled(false);
         m_btnSetGapTwo.setEnabled(false);
         m_sldSetT.setEnabled(false);

         m_btnPrev.setEnabled(true);
         m_btnBeginning.setEnabled(true);
      }

      int i, x, y;
      FRCellElement tmpCell;
      Point currBloc;

      currBloc = getCoordsByStep(m_currentStep);
      x = (m_t - 1) * currBloc.x + 1;
      y = (m_t - 1) * currBloc.y + 1;

      //////////////////////////////////////////////////////
      ///// First encode the two alphabets
      //////////////////////////////////////////////////////
      int start, end;
      start = (m_t-1) * currBloc.y;
      end = (m_t-1) * (currBloc.y + 1);

      // Substring will be of size m_t!!
      String sub_s1 = m_s1.substring(start, end);

      start = (m_t-1) * currBloc.x;
      end = (m_t-1) * (currBloc.x + 1);
      String sub_s2 = m_s2.substring(start, end);

      // this local alphabet is guaranteed to be <= m_t
      String localAlphabet = buildAlphabet(sub_s1, sub_s2);

      Hashtable encodedAlphabet = new Hashtable(sub_s1.length());
      byte count = 0;
      Character ch;

      // Assign to each unique value of the two strings a value
      for (i = 0; i < sub_s1.length(); ++i) {
         ch = new Character(sub_s2.charAt(i));
         if (!encodedAlphabet.containsKey(ch)) {
            encodedAlphabet.put(ch, new Byte(count++));
         }
         ch = new Character(sub_s1.charAt(i));
         if (!encodedAlphabet.containsKey(ch)) {
            encodedAlphabet.put(ch, new Byte(count++));
         }
      }

      /////////////////////////////////////////
      // The two encoded alphabets
      byte D_encodedAlphaS1[] = new byte[m_t-1];
      byte E_encodedAlphaS2[] = new byte[m_t-1];

      Byte tmpEl;

      for (i = 0; i < sub_s1.length(); ++i) {
         ch =  new Character(sub_s1.charAt(i));
         tmpEl = (Byte) encodedAlphabet.get(ch);
         D_encodedAlphaS1[i] = tmpEl.byteValue();
      }

      for (i = 0; i < sub_s2.length(); ++i) {
         ch =  new Character(sub_s2.charAt(i));
         tmpEl = (Byte)encodedAlphabet.get(ch);
         E_encodedAlphaS2[i] = tmpEl.byteValue();
      }

      //////////////////////////////////////////////////////
      ///// Now gets the gaps
      //////////////////////////////////////////////////////
      byte B_topGap[] = new byte[m_t-1];
      byte C_leftGap[] = new byte[m_t-1];

      ////// Top gap
      start = (m_t-1) * currBloc.x + 2;
      for (i = 0; i < m_t-1; i++) {
         tmpCell = (FRCellElement)m_dpTable.getCell(start + i, y);
         if (tmpCell.hasAlternativeVal()) {
            B_topGap[i] = (byte) tmpCell.getIntAlternativeVal();
         }
         else {
            B_topGap[i] = (byte) tmpCell.getIntVal();
         }
      }

      ////// Left gap
      start = (m_t-1) * currBloc.y + 2;
      for (i = 0; i < m_t-1; i++) {
         tmpCell = (FRCellElement)m_dpTable.getCell(x, start + i);

         C_leftGap[i] = (byte)tmpCell.getIntVal();
      }

      //////////////////////////////////////////////////////
      ///// Now I have everything. Let's ask the hashtable!
      //////////////////////////////////////////////////////

      RBFParams param = new RBFParams(D_encodedAlphaS1, E_encodedAlphaS2,
                                      B_topGap, C_leftGap);

      MinimalistMatrix foundMat = (MinimalistMatrix)m_preprocTable.get(param);

      m_lDEqual.setText("Found Bloc = " + foundMat.index);

      ///////////////////////////////////////////////////////
      /// Now update the table
      ///////////////////////////////////////////////////////

      tmpCell = (FRCellElement)m_dpTable.getCell(x + m_t-1, y);
      int newOffsetVal = 0;
      int newRealVal = tmpCell.getIntScoreVal();
      MinimalistCellElement prevPrecompCell;
      MinimalistCellElement currPrecompCell;

      ///// the right side ////////
      byte c, r;
      for (r = 1; r < m_t; ++r) {
         tmpCell = (FRCellElement)m_dpTable.getCell(x + m_t-1, y + r);

         prevPrecompCell = foundMat.mat[m_t-1][r-1];
         currPrecompCell = foundMat.mat[m_t-1][r];

         newOffsetVal = currPrecompCell.value - prevPrecompCell.value;
         newRealVal += newOffsetVal;
         tmpCell.setIntVal(newOffsetVal);
         tmpCell.setIntScoreVal(newRealVal);
      }

      ///// the bottom side ///////
      tmpCell = (FRCellElement)m_dpTable.getCell(x, y + m_t-1);
      newRealVal = tmpCell.getIntScoreVal();
      for (c = 1; c < m_t; ++c) {
         tmpCell = (FRCellElement)m_dpTable.getCell(x + c, y + m_t-1);

         prevPrecompCell = foundMat.mat[c-1][m_t-1];
         currPrecompCell = foundMat.mat[c][m_t-1];

         newOffsetVal = currPrecompCell.value - prevPrecompCell.value;
         newRealVal += newOffsetVal;

         if (c == m_t - 1) {
            tmpCell.setIntAlternativeVal(newOffsetVal);
         }
         else {
            tmpCell.setIntVal(newOffsetVal);
            tmpCell.setIntScoreVal(newRealVal);
         }
      }

      // Now sets the pointers
      FRCellElement pointed;
      for (r = 1; r < m_t; ++r) {
         for (c = 1; c < m_t; ++c) {
            tmpCell = (FRCellElement)m_dpTable.getCell(x + c,
                                                       y + r);
            currPrecompCell = foundMat.mat[c][r];

            if (currPrecompCell.pLeft != null) {
               pointed = (FRCellElement)m_dpTable.getCell(tmpCell.getColumn()-1,
                                                          tmpCell.getRow());
               tmpCell.addLeftPointer(pointed);
            }
            if (currPrecompCell.pTop != null) {
               pointed = (FRCellElement)m_dpTable.getCell(tmpCell.getColumn(),
                                                          tmpCell.getRow()-1);
               tmpCell.addTopPointer(pointed);
            }
            if (currPrecompCell.pTopLeft != null) {
               pointed = (FRCellElement)m_dpTable.getCell(tmpCell.getColumn()-1,
                                                          tmpCell.getRow()-1);
               tmpCell.addDiagPointer(pointed);
            }

         }
      }

      /// Update the grid colors
      ((FRTable)m_dpTable).addBackgroundIndex(currBloc,
                                              Integer.toString(foundMat.index));
      ((FRTable)m_dpTable).highlightBloc(currBloc);

      m_currentStep++;
      if (showSteps) {
         m_dpTable.paint(m_dpTable.getGraphics());
      }

   }

   protected void stepZero() {
      FRTable dpTable = ( (FRTable) m_dpTable);

      switch (m_currentPhase) {
         case PHASE_CALC_GRID:
            dpTable.clearAllBkIndexes();
            m_dpTable.clearHighlightColors();
            m_sldSetT.setEnabled(true);
            setInfoMessage("Total blocs to precompute: " +
                           getNumPrecomputedBlocsString());
            super.stepZero();

            m_lDEqual.setText("     ");
            m_l1Choiche.setText("      ");
            m_l2Choiche.setText("      ");
            m_l3Choiche.setText("      ");

           break;

         case PHASE_BACKTRACK:
            dpTable.setShowBackgroundIndexes(true);
            super.stepZero();
            break;
      }

   }

   protected void stepEnd() {

      int i;
      int totSize = m_nHBlocs * m_nVBlocs;

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

   protected void algoClear() {
      super.algoClear();
      m_btnShowPreproc.setEnabled(false);
      m_dwPanel.setVisible(false);
      m_sldSetT.setEnabled(false);

      m_currentPhase = PHASE_PREPROCESSING;
   }

   ///////////////////////////////////////////////////////////////////////

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
      alphabet = "";
      while (it.hasNext()) {
         alphabet += ((Character)it.next()).charValue();
      }

      return alphabet;
      //System.out.println("Alphabet: " + alphabet);
   }

   protected void buildEncodedAlphabet() {

      // NOTE: MAX t SIZE = 256!!!!
      // the dimension of the encoded alphabet values are
      // min(size(alphabet), (t-1)*2)
      m_encodedAlphSize = (Math.min(m_alphabet.length(), (m_t - 1) * 2));

      m_stringsEncodedValues = new byte[m_encodedAlphSize];
      for (byte i = 0; i < m_encodedAlphSize; ++i) {
         m_stringsEncodedValues[i] = i;
      }

   }

   /**
    * Returns the number of forecasted precomputed blocs.
    */
   protected long getNumPrecomputedBlocs() {

      long totAlpha = (long)Math.pow(m_encodedAlphSize, m_t - 1);
      // offset is always 3: {-1, 0, +1}
      long totOffset = (long)Math.pow(3, m_t - 1);

      return totAlpha * totAlpha *
             totOffset * totOffset;
   }

   protected String getNumPrecomputedBlocsString() {
      NumberFormat nf = NumberFormat.getInstance();
      long nPrec = getNumPrecomputedBlocs();

      return nf.format(nPrec);
   }

   /**
    * Now returns the coordinate of the BLOC!!
    *
    * @param step The current Step
    * @return The coordinates of the current bloc.
    * Ie. 2,1 = second bloc on right and first on top
    */
   protected Point getCoordsByStep(int step) {
      Point ret = new Point();

      // Column
      int cols = m_nHBlocs;

      ret.x = step % cols;
      ret.y = step / cols;

      return ret;
   }

   ///////////////// EVENTS ////////////////////////////////

   class SliderListener implements ChangeListener {

       public void stateChanged(ChangeEvent e) {
           JSlider source = (JSlider)e.getSource();

           if (!source.getValueIsAdjusting()) {

              int a = source.getValue();
              Integer mappedT = (Integer)m_validTValues.get(source.getValue());
              m_t = mappedT.intValue();
              m_tValue.setText("t=" + m_t + ":");
              resetFRGrid();

           }
       }

   }

   protected class ShowPrepListener
       implements ActionListener {

      public void actionPerformed(ActionEvent event) {
         // Perform the action indicated by a mouse click on a button.

         Object b = event.getSource(); // Get the component that was
         // clicked.

         if (b == m_btnShowPreproc) {
            ShowPrepTables spt = new ShowPrepTables(m_preprocTable, m_orderedKeys,
                0, m_preprocTable.size(), m_encodedAlphSize);
            spt.show();
         }
      }
   }

}
