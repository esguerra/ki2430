package norman.baba.algorithms;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import javax.swing.text.DefaultCaret;

import norman.baba.UI.*;
import norman.baba.grids.*;

/**
 * <p>Title: BABA</p>
 * <p>Description: Bioinformatique Research Project</p>
 * <p>Copyright: Copyright Norman Casagrande (c) 2003</p>
 * <p>Company: </p>
 * @author Norman Casagrande
 * @version 1.0
 */

public class SimpleDP
    implements CellInteractInterface {

   protected String m_defS1 = "";
   protected String m_defS2 = "";

   /**
    * **********************************************************************
    * User Interface Stuffs
    * **********************************************************************
    */

   /**
    * ****************** Useful things *********************************
    */
   protected Border m_defaultStringBorder = BorderFactory.createCompoundBorder(
       BorderFactory.
       createEtchedBorder(Color.white, new Color(165, 163, 151)),
       BorderFactory.createEmptyBorder(1, 2, 3, 2));

   protected Font m_resultAreaFont = new Font("Monospaced", 0, 11);

   /**
    * ************************* Panels *********************************
    */

   /** The main panel. Everything is attached here */
   protected JPanel m_mainPane = null;

   /**
    * The Scroll Area where the grid is painted.
    * NOTE: It is a ScrollPane and NOT a JScrollPane because this is way
    * faster. Double buffering is controlled by the grid class.
    */
   protected ScrollPane m_gridScrollArea = new ScrollPane();

   /** The grid class - CENTER */
   protected DPTable m_dpTable;

   /**
    * ********************** NORTH AREA *********************************
    */
   protected JTextField m_infoLabel = new JTextField(" Waiting..", 40);

   /**
    * ********************** SOUTH AREA *********************************
    */

   protected JTextArea m_bottomResultArea = new JTextArea(3, 50);

   /**
    * ********************** EAST AREA *********************************
    */

   protected JPanel m_clearPanel = new JPanel();
   protected JPanel m_currStatusPanel = new JPanel();
   protected JPanel m_stepsButtonPanel = new JPanel();

   protected JLabel m_gapOne_title = new JLabel("S1 Gap Penalty Array:");
   protected JLabel m_gapTwo_title = new JLabel("S2 Gap Penalty Array:");

   // These elements are public
   public JTextField m_StringOne = new JTextField(22);
   public JTextField m_StringTwo = new JTextField(22);

   public JTextField m_gapOne = new JTextField(22);
   public JTextField m_gapTwo = new JTextField(22);

   ////////
   public JButton m_btnSetOne = new JButton("Set");
   public JButton m_btnSetTwo = new JButton("Set");
   public JButton m_btnSetGapOne = new JButton("Set");
   public JButton m_btnSetGapTwo = new JButton("Set");

   ////////
   public JLabel m_lDEqual = new JLabel("D(S1,S2) = Min");

   public JLabel m_l1Choiche = new JLabel("D(S1-1, S2) + 1");
   public JLabel m_l2Choiche = new JLabel("D(S1, S2-1) + 1");
   public JLabel m_l3Choiche = new JLabel("D(S1 - 1, S2 - 1) + [1|0]");

   ////////
   public JButton m_btnBeginning = new JButton("|<");
   public JButton m_btnNext = new JButton(">");
   public JButton m_btnPrev = new JButton("<");
   public JButton m_btnEnd = new JButton(">|");

   ////////
   public JButton m_btnClear = new JButton("Clear");

   protected static String emptyStringMessage = "[Please Set String]";
   protected static String emptyGapMessage = "[Not Set]";

   /////////////////////////////////////////////////////////////////////

   /**
    * This class draws the three lines a the center of the right panel.
    * (From D(S1,S2) to the choiches.
    */
   protected class DrawablePanel
       extends JPanel {
      public void paintComponent(Graphics g) {
         //super.paintComponent(g);

         int startY;
         int width = this.getSize().width - 6;

         // /
         startY = this.getSize().height / 2 - 4;
         g.drawLine(4, startY, width, startY - 15);

         // --
         startY = this.getSize().height / 2;
         g.drawLine(4, startY, width, startY);

         // \
         startY = this.getSize().height / 2 + 4;
         g.drawLine(4, startY, width, startY + 15);

      }
   }

   DrawablePanel m_dwPanel = new DrawablePanel();

   /**
    * **********************************************************************
    * Dynamic Programming Stuffs
    * **********************************************************************
    */
   protected String m_s1 = null;
   protected int m_s1_size = 0;
   protected String m_s2 = null;
   protected int m_s2_size = 0;

   protected String[] m_resLine = new String[3];

   public static final int STRING_ONE = 0; // VERTICAL string
   public static final int STRING_TWO = 1; // HORIZONTAL string

   protected boolean m_tableReady = false;

   protected int m_currentStep = 0;

   protected int m_gapPenaltyOne[];
   protected int m_gapPenaltyTwo[];

   protected static final int GAP_ONE = 0;
   protected static final int GAP_TWO = 1;

   /** In which state is the current calculus: phase calculating the grid */
   protected static final int PHASE_CALC_GRID = 0;
   /** In which state is the current calculus: phase backtracking the pointers */
   protected static final int PHASE_BACKTRACK = 1;

   /** The current calculating phase. At the beginning is calculating the grid */
   public int m_currentPhase = PHASE_CALC_GRID;

   // Linked list of the backtrack cells
   protected LinkedList m_backTrackList = new LinkedList();
   protected CellElement m_backtrackLastSel = null;
   protected int m_backtrackingPolicy = CellElement.
       POINTER_POLICY_COUNTERCLOCKWISE;

   /**
    * **********************************************************************
    * The class
    * **********************************************************************
    */

   /** Constructor */
   public SimpleDP(JPanel contentPane,
                   String defaultString1, String defaultString2) {

      m_mainPane = contentPane;
      m_mainPane.setLayout(new BorderLayout());

      m_defS1 = defaultString1;
      m_defS2 = defaultString2;

      try {
         jbInit();
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }

   public SimpleDP(JPanel contentPane) {
      this(contentPane, "", "");
   }

   public String getAlgorithmName() {
      return "Simple Dynamic Programming";
   }

   //Component initialization
   protected void jbInit() throws Exception {

      /**
       * ************************* Panels *********************************
       */

      /** The top panel - NORTH */
      JPanel topPanel = new JPanel();
      /** The bottom panel - SOUTH */
      JPanel bottomPanel = new JPanel();
      /** The right panel - EAST */
      JPanel rightPanel = new JPanel();

      /**
       * ********************** CENTER AREA *********************************
       */
      this.setCenter();
      /**
       * ********************** NORTH AREA ********************************
       */
      this.setNorth(topPanel);
      /**
       * ********************** SOUTH AREA ********************************
       */
      this.setSouth(bottomPanel);
      /**
       * ********************** EAST AREA *********************************
       */
      this.setEast(rightPanel);

      /////////////////////////////////////////////////////////////////////////
      /////////////////////////////////////////////////////////////////////////

      // Now adds everything to the main panel
      m_mainPane.add(topPanel, BorderLayout.NORTH);
      m_mainPane.add(m_gridScrollArea, BorderLayout.CENTER);
      m_mainPane.add(rightPanel, BorderLayout.EAST);
      m_mainPane.add(bottomPanel, BorderLayout.SOUTH);

   }

   /**
    * ********************** CENTER AREA *********************************
    */
   protected void setCenter() {
      m_dpTable = new DPTable(10, 6);
      m_dpTable.setCellListener(this);

      m_gridScrollArea.add(m_dpTable);
      // This is in the case of JScrollArea instead of ScrollArea
      //m_gridScrollArea.setDoubleBuffered(true);
      //m_gridScrollArea.getViewport().setView(m_dpTable);
   }

   /**
    * ********************** NORTH AREA *********************************
    */
   protected void setNorth(JPanel topPanel) {

//      JComboBox aliasingComboBox = new JComboBox(aliasingData);
      int i;
      String[] aliasingData = {
          "On", "Off"};
      Choice aliasingComboBox = new Choice();
      for (i = 0; i < aliasingData.length; ++i) {
         aliasingComboBox.add(aliasingData[i]);
      }

//      JComboBox zoomComboBox = new JComboBox(zoomData);
      String[] zoomData = {
          "-2", "-1", "Normal", "+1", "+2", "+3"};
      Choice zoomComboBox = new Choice();
      for (i = 0; i < zoomData.length; ++i) {
         zoomComboBox.add(zoomData[i]);
      }

      /*      Dimension prevDim = zoomComboBox.getPreferredSize();
            prevDim.width += 2;
            zoomComboBox.setPreferredSize(prevDim);
       */
      zoomComboBox.select(2); // Selecting Normal Zoom

      m_infoLabel.setBorder(m_defaultStringBorder);
      m_infoLabel.setEditable(false);

      topPanel.add(new JLabel("AntiAliasing:"));
      topPanel.add(aliasingComboBox);

      topPanel.add(new JLabel("Zoom:"));
      topPanel.add(zoomComboBox);
      topPanel.add(new JLabel("Info:"));
      topPanel.add(m_infoLabel);

      // Listeners
      aliasingComboBox.addItemListener(new AliasingComboListener());
      zoomComboBox.addItemListener(new ZoomComboListener());

   }

   /**
    * ********************** SOUTH AREA *********************************
    */
   protected void setSouth(JPanel bottomPanel) {

      JTextArea bottomLabelArea = new JTextArea("S1:\n\nS2:", 3, 4);
      m_bottomResultArea.setFont(m_resultAreaFont);
      bottomLabelArea.setFont(m_resultAreaFont);

      m_bottomResultArea.setBorder(m_defaultStringBorder);
      bottomLabelArea.setBorder(m_defaultStringBorder);

      m_bottomResultArea.setEditable(false);
      bottomLabelArea.setEditable(false);

      bottomLabelArea.setBackground(UIManager.getColor("Label.background"));

      // Redefining height to be 3 rows: the default was not correct.
      FontMetrics fm = m_mainPane.getFontMetrics(m_resultAreaFont);
      int areasHeight = fm.getHeight() * 3 + fm.getDescent() * 3 - 2;

      m_bottomResultArea.setPreferredSize(new Dimension(400, areasHeight));
      bottomLabelArea.setPreferredSize(new Dimension(bottomLabelArea.
          getPreferredSize().width,
          areasHeight));

      bottomPanel.add(bottomLabelArea);
      bottomPanel.add(m_bottomResultArea);

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

      JLabel stringOne_title = new JLabel("String S1:");
      JLabel stringTwo_title = new JLabel("String S2:");

      ///////////////////////////////////////////////////////

      m_StringOne.setText(emptyStringMessage);
      m_StringTwo.setText(emptyStringMessage);
      m_gapOne.setText(emptyGapMessage);
      m_gapTwo.setText(emptyGapMessage);

      m_StringOne.setEditable(false);
      m_StringTwo.setEditable(false);
      m_gapOne.setEditable(false);
      m_gapTwo.setEditable(false);

      m_btnSetGapOne.setEnabled(false);
      m_btnSetGapTwo.setEnabled(false);

      m_StringOne.setBorder(m_defaultStringBorder);
      m_StringTwo.setBorder(m_defaultStringBorder);
      m_gapOne.setBorder(m_defaultStringBorder);
      m_gapTwo.setBorder(m_defaultStringBorder);

      ///////////////////////////////////////////////////////

      m_l1Choiche.setOpaque(true);
      m_l2Choiche.setOpaque(true);
      m_l3Choiche.setOpaque(true);

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
      rightInsPanel.add(stringTwo_title,
                        new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0,
                                               GridBagConstraints.WEST,
                                               GridBagConstraints.NONE,
                                               new Insets(5, 0, 3, 0), 0, 0));
      ////////
      rightInsPanel.add(m_StringOne,
                        new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0,
                                               GridBagConstraints.WEST,
                                               GridBagConstraints.HORIZONTAL,
                                               new Insets(0, 0, 0, 0), 2, 0));
      rightInsPanel.add(m_StringTwo,
                        new GridBagConstraints(0, 5, 2, 1, 1.0, 0.0,
                                               GridBagConstraints.CENTER,
                                               GridBagConstraints.HORIZONTAL,
                                               new Insets(0, 0, 0, 0), 2, 0));
      ////////
      rightInsPanel.add(m_gapOne_title,
                        new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0,
                                               GridBagConstraints.WEST,
                                               GridBagConstraints.NONE,
                                               new Insets(5, 0, 3, 0), 0, 0));
      rightInsPanel.add(m_gapTwo_title,
                        new GridBagConstraints(0, 6, 2, 1, 0.0, 0.0,
                                               GridBagConstraints.WEST,
                                               GridBagConstraints.NONE,
                                               new Insets(5, 0, 3, 0), 0, 0));
      ////////
      rightInsPanel.add(m_gapOne,
                        new GridBagConstraints(0, 3, 2, 1, 1.0, 0.0,
                                               GridBagConstraints.WEST,
                                               GridBagConstraints.HORIZONTAL,
                                               new Insets(0, 0, 0, 0), 0, 0));
      rightInsPanel.add(m_gapTwo,
                        new GridBagConstraints(0, 7, 2, 1, 1.0, 0.0,
                                               GridBagConstraints.WEST,
                                               GridBagConstraints.HORIZONTAL,
                                               new Insets(0, 0, 0, 0), 2, 0));
      ////////
      rightInsPanel.add(m_btnSetGapOne,
                        new GridBagConstraints(2, 2, 1, 3, 0.0, 0.0,
                                               GridBagConstraints.CENTER,
                                               GridBagConstraints.NONE,
                                               new Insets(0, 5, 0, 0), 0, -3));
      rightInsPanel.add(m_btnSetGapTwo,
                        new GridBagConstraints(2, 7, 1, 2, 0.0, 0.0,
                                               GridBagConstraints.CENTER,
                                               GridBagConstraints.NONE,
                                               new Insets(0, 5, 0, 0), 0, -3));

      rightInsPanel.add(m_btnSetOne,
                        new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
                                               GridBagConstraints.CENTER,
                                               GridBagConstraints.NONE,
                                               new Insets(0, 5, 0, 0), 0, -3));
      rightInsPanel.add(m_btnSetTwo,
                        new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0,
                                               GridBagConstraints.CENTER,
                                               GridBagConstraints.NONE,
                                               new Insets(0, 5, 0, 0), 0, -3));

      ////////////////////////////////////////////////////////////////////////
      rightInsPanel.add(m_currStatusPanel,
                        new GridBagConstraints(0, 9, 3, 1, 0.0, 0.0,
                                               GridBagConstraints.WEST,
                                               GridBagConstraints.NONE,
                                               new Insets(5, 0, 5, 0), 0, 0));

      m_currStatusPanel.add(m_lDEqual,
                          new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                                                 GridBagConstraints.WEST,
                                                 GridBagConstraints.HORIZONTAL,
                                                 new Insets(5, 0, 5, 0), 1, 0));
      m_currStatusPanel.add(m_dwPanel,
                          new GridBagConstraints(1, 0, 1, 3, 0.0, 0.0,
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

      ////////////////////////////////////////////////////////////////////////
      ////////////////////////////////////////////////////////////////////////

      rightInsPanel.add(m_stepsButtonPanel,
                        new GridBagConstraints(0, 10, 3, 1, 0.0, 0.0,
                                               GridBagConstraints.WEST,
                                               GridBagConstraints.NONE,
                                               new Insets(0, 0, 0, 0), 0, 0));

      rightInsPanel.add(m_clearPanel,
                        new GridBagConstraints(1, 11, 1, 1, 0.0, 0.0,
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
      m_btnSetTwo.addActionListener(new SetButtonListener());
      m_btnSetGapOne.addActionListener(new SetButtonListener());
      m_btnSetGapTwo.addActionListener(new SetButtonListener());

      m_btnBeginning.addActionListener(new MoveButtonListener());
      m_btnPrev.addActionListener(new MoveButtonListener());
      m_btnNext.addActionListener(new MoveButtonListener());
      m_btnEnd.addActionListener(new MoveButtonListener());
      m_btnClear.addActionListener(new MoveButtonListener());
   }

   public void setInfoMessage(String message) {
      m_infoLabel.setText(" " + message);
   }

   public void setBottomText(String text) {
      m_bottomResultArea.setText(text);
   }

   public void setStringsButtonEnabled(boolean enabled) {
      m_btnSetOne.setEnabled(enabled);
      m_btnSetTwo.setEnabled(enabled);
      m_btnSetGapOne.setEnabled(enabled);
      m_btnSetGapTwo.setEnabled(enabled);
   }

   ////////////////////////////////////////////////////////////////////////////
   ////////////////////////////////////////////////////////////////////////////
   ////////////////////////////////////////////////////////////////////////////
   ////////////////////////////////////////////////////////////////////////////

   /**
    * Sets a string for the algorithm.<P>
    * If both strings are inserted the table il filled with the two values
    * and the default gaps.
    * @param str The string value to be set.
    * @param whichString Wich string should be set. Possible Values:<BR>
    * <CODE>STRING_ONE<\CODE> = the first string.<BR>
    * <CODE>STRING_TWO<\CODE> = the second string.
    */
   public void setString(String str, int whichString) {

      JTextField tmpStringTF;
      JButton tmpGapButton;
      int tmpStrLen = 0;
      m_tableReady = false;

      switch (whichString) {
         case STRING_ONE:
            tmpStringTF = m_StringOne;
            tmpGapButton = m_btnSetGapOne;
            break;
         case STRING_TWO:
            tmpStringTF = m_StringTwo;
            tmpGapButton = m_btnSetGapTwo;
            break;
         default:
            System.err.println("Not a valid string sequence ID!");
            return;
      }

      if (str == null || str.length() == 0) {
         tmpStrLen = 0;
         tmpStringTF.setText(emptyStringMessage);
         tmpGapButton.setEnabled(false);
         return;
      }
      else {
         tmpStrLen = str.length();
         tmpGapButton.setEnabled(true);
      }

      switch (whichString) {
         case STRING_ONE:
            m_s1 = str;
            m_s1_size = tmpStrLen;
            m_StringOne.setText(str);
            break;
         case STRING_TWO:
            m_s2 = str;
            m_s2_size = tmpStrLen;
            m_StringTwo.setText(str);
            break;
      }

      this.setDefaultGap(whichString);
      m_btnClear.setEnabled(true);

      checkForBothStrings();
   }

   protected void setDefaultGap(int whichString) {
      switch (whichString) {
         case STRING_ONE:
            this.setGapSequence(this.getGapIncreasing(GAP_ONE), GAP_ONE);
            break;
         case STRING_TWO:
            this.setGapSequence(this.getGapIncreasing(GAP_TWO), GAP_TWO);
            break;
      }

   }

   protected boolean checkForBothStrings() {

      if (m_s1_size == 0 ||
          m_s2_size == 0) {
         return false;
      }

      // NOTE: S1 = vertical, S2 = horizontal

      m_dpTable.setGridSize(m_s2_size + 2, m_s1_size + 2, true);
      int i;
      for (i = 0; i < m_s1_size; ++i) {
         m_dpTable.setCellValue(0, i + 2, m_s1.charAt(i));
      }
      for (i = 0; i < m_s2_size; ++i) {
         m_dpTable.setCellValue(i + 2, 0, m_s2.charAt(i));
      }

      updateGapCells();

      m_dpTable.setGridRectangle(2, 2, m_s2_size, m_s1_size);
      m_dpTable.setCellValue(0, 0, "D(i,j)");
      m_dpTable.setCellValue(1, 1, "0");

      m_dpTable.repaint();
      m_gridScrollArea.doLayout();

      m_tableReady = true;
      m_btnNext.setEnabled(true);
      m_btnEnd.setEnabled(true);

      return true;
   }

   // ex. newGapSequence = "{1, 2, 3, 4, 5}"
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

         tmpGapPenality[i] = val;
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

   /**
    * Fill gap values and sets the gap pointers
    */
   protected void updateGapCells() {
      int i;

      CellElement prevCell = m_dpTable.getCell(1, 1); // Cell Zero
      CellElement tmpCell;

      for (i = 0; i < m_gapPenaltyOne.length; ++i) {
         tmpCell = m_dpTable.getCell(1, i + 2);
         tmpCell.setIntVal(m_gapPenaltyOne[i]);

         tmpCell.addTopPointer(prevCell);
         prevCell = tmpCell;
      }

      prevCell = m_dpTable.getCell(1, 1); // Cell Zero
      for (i = 0; i < m_gapPenaltyTwo.length; ++i) {
         tmpCell = m_dpTable.getCell(i + 2, 1);
         tmpCell.setIntVal(m_gapPenaltyTwo[i]);

         tmpCell.addLeftPointer(prevCell);
         prevCell = tmpCell;
      }
   }

   protected String getGapIncreasing(int whichGapSequence) {

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
            return "";
      }

      String ret = "{";

      for (int i = 0; i < sLen; ++i) {
         ret += (i + 1) + ", ";
      }

      ret = ret.substring(0, ret.length() - 2) + "}";

      return ret;
   }

   protected String getGapKSequence(int whichGapSequence, int kValue) {

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
            return "";
      }

      String ret = "{";

      for (int i = 0; i < sLen; ++i) {
         ret += kValue + ", ";
      }

      ret = ret.substring(0, ret.length() - 2) + "}";

      return ret;
   }

   protected Point getCoordsByStep(int step) {
      Point ret = new Point();

      // Column
      int cols = m_s2_size;

      ret.x = step % cols + 2;
      ret.y = step / cols + 2;

      return ret;
   }

   protected void stepForward(boolean showSteps) {

      switch (m_currentPhase) {

         case PHASE_CALC_GRID:

            if (m_currentStep >=
                (m_dpTable.getHCellsCount() - 2) *
                (m_dpTable.getVCellsCount() - 2)) {

               // Entering backtrack phase
               m_currentPhase = PHASE_BACKTRACK;

               m_backtrackLastSel = m_dpTable.getLastCell();

               m_dpTable.clearDPHighlights();
               m_dpTable.clearAllArrows();

               // reset result string
               for (int i = 0; i < 3; i++) {
                  m_resLine[i] = "";
               }

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

            // erasing pointers,
            // values, etc
            currentCell.clearAll();
            m_currentStep--;

            stepForward(true);

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

      if (currentCell.getColumn() == 1 &&
          currentCell.getRow() == 1) {

         m_btnNext.setEnabled(false);
         m_btnEnd.setEnabled(false);
         m_dpTable.clearAllArrows();
         m_dpTable.clearGridCircle();
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

   protected void setResultString(CellElement prevCell,
                                CellElement selectedBackTrackingCell) {

      Point prevPos = new Point(prevCell.getColumn() - 2,
                                prevCell.getRow() - 2);

      /** I.e.
       *  S1 = CATAGTG
       *  S2 = GTCAGGT
       *
       *  Possible Aligment:
       *  --CATAGTG
       *    ||  ||
       *  GTCAG-GT-
       */

      switch (prevCell.getPointerPos(selectedBackTrackingCell)) {

         // LEFT or TOP alighment are Insertion/Suppression

         case CellElement.POINTERPOS_LEFT:

            m_resLine[0] = "-" + m_resLine[0];
            m_resLine[1] = " " + m_resLine[1];
            m_resLine[2] = m_s2.charAt(prevPos.x) + m_resLine[2];

            break;

         case CellElement.POINTERPOS_TOP:

            m_resLine[0] = m_s1.charAt(prevPos.y) + m_resLine[0];
            m_resLine[1] = " " + m_resLine[1];
            m_resLine[2] = "-" + m_resLine[2];
            break;

            // DIAG is substitution (if the two chars are different)
            //      or identity (if the two chars are equal)
         case CellElement.POINTERPOS_DIAG:

            m_resLine[0] = m_s1.charAt(prevPos.y) + m_resLine[0];

            if (m_s1.charAt(prevPos.y) == m_s2.charAt(prevPos.x)) {
               m_resLine[1] = "|" + m_resLine[1];
            }
            else {
               m_resLine[1] = " " + m_resLine[1];
            }

            m_resLine[2] = m_s2.charAt(prevPos.x) + m_resLine[2];

            break;
      }

      m_bottomResultArea.setText(m_resLine[0] + "\n" +
                                 m_resLine[1] + "\n" +
                                 m_resLine[2] + "\n");
   }

   public void onInteractPress(CellElement cellPressed) {
      // do the interact things
      m_backtrackLastSel = cellPressed;
      stepFWDBackTrack(true);
   }

   protected void stepFWDCalc(boolean showSteps) {

      if (m_currentStep == 0) {
         m_btnSetOne.setEnabled(false);
         m_btnSetTwo.setEnabled(false);
         m_btnSetGapOne.setEnabled(false);
         m_btnSetGapTwo.setEnabled(false);

         m_btnPrev.setEnabled(true);
         m_btnBeginning.setEnabled(true);
      }

      Point realD = getCoordsByStep(m_currentStep);

      Point D = new Point(realD.x - 1, realD.y - 1);
      int p = 1;

      m_l1Choiche.setBackground(m_mainPane.getBackground());
      m_l2Choiche.setBackground(m_mainPane.getBackground());
      m_l3Choiche.setBackground(m_mainPane.getBackground());

      CellElement leftCell = m_dpTable.getCell(realD.x - 1, realD.y);
      CellElement topCell = m_dpTable.getCell(realD.x, realD.y - 1);
      CellElement topLeftCell = m_dpTable.getCell(realD.x - 1, realD.y - 1);

      CellElement currentCell = m_dpTable.getCell(realD.x, realD.y);

      if (m_s2.charAt(realD.x - 2) == m_s1.charAt(realD.y - 2)) {
         p = 0;
      }

      if (showSteps) {
         String DEqual = "D(" + (D.y) + ", " + (D.x) + ") = Min";

         String DLeft = "D(" + (D.y) + ", " + (D.x-1) + ") + 1 = " +
         	leftCell.getVal() + " + 1 = " + (leftCell.getIntVal() + 1);
         String DTop = "D(" + (D.y-1) + ", " + (D.x) + ") + 1 = " +
         	topCell.getVal() + " + 1 = " + (topCell.getIntVal() + 1);
         
////DUMB BUG

//         String DLeft = "D(" + (D.y - 1) + ", " + (D.x) + ") + 1 = " +
//             leftCell.getVal() + " + 1 = " + (leftCell.getIntVal() + 1);
//         String DTop = "D(" + (D.y) + ", " + (D.x - 1) + ") + 1 = " +
//             topCell.getVal() + " + 1 = " + (topCell.getIntVal() + 1);

         String DTopLeft = "D(" + (D.y - 1) + ", " + (D.x - 1) + ") + " + p +
             " = " +
             topLeftCell.getVal() + " + " + p + " = " +
             (topLeftCell.getIntVal() + p);

         m_lDEqual.setText(DEqual);
         m_l1Choiche.setText(DLeft);
         m_l2Choiche.setText(DTop);
         m_l3Choiche.setText(DTopLeft);
      }

      int fromLeftVal = leftCell.getIntVal() + 1;
      int fromTopVal = topCell.getIntVal() + 1;
      int fromTopLeftVal = topLeftCell.getIntVal() + p;

      int min = Math.min(fromLeftVal, Math.min(fromTopVal, fromTopLeftVal));

      // Init choosen array
      LinkedList highlightList = new LinkedList();

      if (fromLeftVal == min) {
         m_l1Choiche.setBackground(Color.yellow);
         currentCell.addLeftPointer(leftCell);
         highlightList.add(leftCell);
      }
      if (fromTopVal == min) {
         m_l2Choiche.setBackground(Color.yellow);
         currentCell.addTopPointer(topCell);
         highlightList.add(topCell);
      }
      if (fromTopLeftVal == min) {
         m_l3Choiche.setBackground(Color.yellow);
         currentCell.addDiagPointer(topLeftCell);
         highlightList.add(topLeftCell);
      }

      currentCell.setIntVal(min);

      if (showSteps) {
         if (p == 0) {
            // the two characters are equal: RED
            m_dpTable.setSideHighlight(currentCell, Color.red);
         }
         else {
            // the two characters are not equal:
            m_dpTable.setSideHighlight(currentCell, new Color(0, 255, 255));
         }

         m_dpTable.setTriArrows(currentCell, true);
         m_dpTable.setMultipleCellHighlight(highlightList);
         m_dpTable.paint(m_dpTable.getGraphics());
      }

      m_currentStep++;
   }

   protected void stepZero() {

      // just in case:
      m_dpTable.getCell(1, 1).clearColor();

      switch (m_currentPhase) {
         case PHASE_BACKTRACK:

            //m_currentPhase = PHASE_BACKTRACK;

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

            m_currentStep = 0;

            m_dpTable.clearDPTableContent();
            m_dpTable.clearDPHighlights();

            m_dpTable.clearAllArrows();
            m_dpTable.clearGridCircle();

            m_backTrackList.clear();

            m_l1Choiche.setBackground(m_mainPane.getBackground());
            m_l2Choiche.setBackground(m_mainPane.getBackground());
            m_l3Choiche.setBackground(m_mainPane.getBackground());
            m_lDEqual.setText("D(S1,S2) = Min");
            m_l1Choiche.setText("D(S1-1, S2) + 1");
            m_l2Choiche.setText("D(S1, S2-1) + 1");
            m_l3Choiche.setText("D(S1 - 1, S2 - 1) + [1|0]");

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

   protected void stepEnd() {

      int i;
      int totSize = m_s1_size * m_s2_size;

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

      int c, r;
      for (c = 0; c < m_dpTable.getHCellsCount(); ++c) {
         m_dpTable.getCell(c, 0).clearCell();
         m_dpTable.getCell(c, 1).clearCell();
      }

      for (r = 0; r < m_dpTable.getVCellsCount(); ++r) {
         m_dpTable.getCell(0, r).clearCell();
         m_dpTable.getCell(1, r).clearCell();
      }

      m_StringOne.setText(emptyStringMessage);
      m_StringTwo.setText(emptyStringMessage);
      m_gapOne.setText(emptyGapMessage);
      m_gapTwo.setText(emptyGapMessage);

      m_s1 = null;
      m_s2 = null;
      m_s1_size = 0;
      m_s2_size = 0;

      m_currentPhase = PHASE_CALC_GRID;
      stepZero(); // warning: do not put this after setGridSize!

      m_dpTable.clearGridRectangles();
      m_dpTable.clearInteractiveCells();
      m_dpTable.setGridSize(10, 6);  // TODO: make something for this

      m_gridScrollArea.doLayout();

      m_btnSetGapOne.setEnabled(false);
      m_btnSetGapTwo.setEnabled(false);
      m_btnNext.setEnabled(false);
      m_btnEnd.setEnabled(false);

      m_dpTable.repaint();
   }

   ////////////////////////////////////////////////////////////////////
   ///////// Listeners ////////////////////////////////////////////////
   ////////////////////////////////////////////////////////////////////

   protected class AliasingComboListener
       implements ItemListener {

      public void itemStateChanged(ItemEvent event) {
         Choice ch = (Choice) event.getSource();

         boolean currentAntiAlias = m_dpTable.getAntiAlias();
         boolean newAntiAlias = false;

         switch (ch.getSelectedIndex()) {
            // Anti alias ON
            case 0:
               newAntiAlias = true;
               break;

               // Anti alias OFF
            case 1:
               newAntiAlias = false;
               break;
         }

         if (newAntiAlias != currentAntiAlias) {
            m_dpTable.setAntiAlias(newAntiAlias);
            m_dpTable.paint(m_dpTable.getGraphics());
         }

      }
   }

   protected class ZoomComboListener
       implements ItemListener {

      public void itemStateChanged(ItemEvent event) {
         Choice cb = (Choice) event.getSource();

         double currentZoom = m_dpTable.getZoomLevel();
         double newZoom = 1.0;

         switch (cb.getSelectedIndex()) {
            // Zoom -2
            case 0:

               newZoom = 0.6;
               break;

               // Zoom -1
            case 1:

               newZoom = 0.8;
               break;

               // Zoom Normal
            case 2:

               newZoom = 1.0;
               break;

               // Zoom +1
            case 3:

               newZoom = 1.4;
               break;

               // Zoom +2
            case 4:

               newZoom = 1.8;
               break;

               // Zoom +3
            case 5:

               newZoom = 2.3;
               break;

         }

         if (newZoom != currentZoom) {
            m_dpTable.setZoomLevel(newZoom);
            m_dpTable.paint(m_dpTable.getGraphics());
            m_gridScrollArea.doLayout();
         }

      }
   }

   protected class MoveButtonListener
       implements ActionListener {

      public void actionPerformed(ActionEvent event) {
         // Perform the action indicated by a mouse click on a button.

         Object b = event.getSource(); // Get the component that was
         // clicked.

         if (b == m_btnBeginning) {
            stepZero();
         }
         else if (b == m_btnNext) {
            stepForward(true);
         }
         else if (b == m_btnPrev) {
            stepBackward();
         }
         else if (b == m_btnEnd) {
            stepEnd();
         }
         else if (b == m_btnClear) {
            algoClear();
         }
      } // end actionPerformed ---------------------------

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

         if (b == m_btnSetOne) {
            title = "Setting string";
            message[0] = "Input the String S1 (i.e. GCCACCGT):";
            stringField = new JTextFieldUC();
            stringField.setText(m_defS1);
            stringField.selectAll();

            options = new String[2];
            options[0] = "Ok";
            options[1] = "Cancel";
         }
         else if (b == m_btnSetTwo) {
            title = "Setting string";
            message[0] = "Input the String S2 (i.e. TTTACGT):";
            stringField = new JTextFieldUC();
            stringField.setText(m_defS2);
            stringField.selectAll();

            options = new String[2];
            options[0] = "Ok";
            options[1] = "Cancel";
         }
         else if (b == m_btnSetGapOne ||
                  b == m_btnSetGapTwo) {
            title = "Setting gap penalty";
            message[0] = "Input the gap penalty sequence (i.e. 1,2,3,4):";
            stringField = new JTextFieldGaps();

            if (b == m_btnSetGapOne) {
               stringField.setText(m_gapOne.getText());
            }
            else {
               stringField.setText(m_gapTwo.getText());
            }

            options = new String[5];
            options[0] = "Ok";
            options[1] = "Increasing";
            options[2] = "All Zero";
            options[3] = "All k-value";
            options[4] = "Cancel";
         }
         else {
            return;
         }

         message[1] = stringField;

         //JOptionPane confirmPane = new JOptionPane();
         //confirmPane.setWantsInput(true);

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

         if (b == m_btnSetOne) {
            if (result == 0) {
               m_defS1 = stringField.getText();
               setString(m_defS1, STRING_ONE);
            }
         }
         else if (b == m_btnSetTwo) {
            if (result == 0) {
               m_defS2 = stringField.getText();
               setString(m_defS2, STRING_TWO);
            }
         }
         else if (b == m_btnSetGapOne ||
                  b == m_btnSetGapTwo) {

            int gapType;
            if (b == m_btnSetGapOne) {
               gapType = GAP_ONE;
            }
            else {
               gapType = GAP_TWO;
            }

            switch (result) {

               case 0: // OK
                  setGapSequence(stringField.getText(), gapType);
                  break;
               case 1: // Increasing
                  setGapSequence(getGapIncreasing(gapType), gapType);
                  break;
               case 2: // Zero Sequence
                  setGapSequence(getGapKSequence(gapType, 0), gapType);
                  break;
               case 3: // All k Sequence
                  String strKVal = JOptionPane.showInputDialog(null,
                      "Please enter the k-value:",
                      "0");

                  int kVal;

                  try {
                     kVal = Integer.parseInt(strKVal);
                  }
                  catch (NumberFormatException ex) {
                     kVal = 0;
                  }

                  setGapSequence(getGapKSequence(gapType, kVal), gapType);
                  break;
            }
         }
      }
   }

}
