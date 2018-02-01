package norman.baba.UI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import norman.baba.algorithms.*;
import java.util.*;

import norman.baba.utils.*;

/**
 * <p>Title: BABA</p>
 * <p>Description: Bioinformatique Research Project</p>
 * <p>Copyright: Copyright Norman Casagrande (c) 2003</p>
 * <p>Company: </p>
 * @author Norman Casagrande
 * @version 1.0
 */

public class ScoreDialog
    extends JDialog {

   protected static final int SCOREMATRIX_ORIGINAL = 0;
   protected static final int SCOREMATRIX_PAM250 = 1;
   protected static final int SCOREMATRIX_BLOSUM62 = 2;
   protected static final int SCOREMATRIX_VTML160 = 3;
   protected static final int SCOREMATRIX_PENALIZEMISMATCH = 4;

   protected static int CELLS_WIDTH = 25;

   protected JDialog m_thisDialog;
   protected JTextField m_jTxtValues[];

   protected JButton m_btnOk = new JButton("Ok");
   protected JButton m_btnCancel = new JButton("Cancel");

   protected String[] m_matrixData = {
       "Original", "Pam250", "Blosum62", "Vtml160", "Penalize Mismatch"};
   protected JComboBox m_matrixSelection = new JComboBox(m_matrixData);

   protected int m_resulMatrix[][];
   protected String m_alphabet;

   protected ScoreHash m_passedHash;

   public ScoreDialog(Frame frame, String title, boolean modal,
                     String alphabet, ScoreHash passedHashtable) {
      super(frame, title, modal);

      this.m_thisDialog = this;
      this.m_passedHash = passedHashtable;
      this.m_alphabet = alphabet;

      this.m_matrixSelection.addActionListener(new ComboBoxListener());

      try {
         jbInit();
         pack();
      }
      catch (Exception ex) {
         ex.printStackTrace();
      }

      this.setDialogDimension();
      this.fillCells(passedHashtable);
   }

   protected void setDialogDimension() {

      int nHCells = m_alphabet.length() + 1;
      int calcWidth = CELLS_WIDTH * nHCells;

      Dimension prev = this.getSize();

      if (calcWidth > prev.width) {
         prev.width = calcWidth;
         this.setSize(prev);
      }

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

   public ScoreDialog(String alphabet, ScoreHash resultingHashtable) {
      this(null, "Score Table", true, alphabet, resultingHashtable);
   }

   private void jbInit() throws Exception {

      JPanel basePanel = new JPanel();
      JPanel scorePanel = new JPanel();

      int alphSize = m_alphabet.length();
      int i, j;

      basePanel.setLayout(new BorderLayout());

      GridLayout gridLayout = new GridLayout(alphSize+1, alphSize+1);
      scorePanel.setLayout(gridLayout);

      // CENTER
      Border insetsBorder;
      insetsBorder = BorderFactory.createEmptyBorder(0,0,0,5);
      scorePanel.setBorder(insetsBorder);

      JLabel jHTitles[] = new JLabel[alphSize];
      for (i = 0; i < jHTitles.length; ++i) {
         jHTitles[i] = new JLabel(m_alphabet.substring(i,i+1));
         jHTitles[i].setHorizontalAlignment(SwingConstants.CENTER);
      }

      JLabel jVTitles[] = new JLabel[alphSize];
      for (i = 0; i < jVTitles.length; ++i) {
         jVTitles[i] = new JLabel(m_alphabet.substring(i,i+1));
         jVTitles[i].setHorizontalAlignment(SwingConstants.CENTER);
      }

      // First line:
      // _ A G C T
      scorePanel.add(new JLabel());

      for (i = 0; i < alphSize; ++i) {
         scorePanel.add(jHTitles[i]);
      }

      int numEditableCells = (alphSize * alphSize + alphSize) / 2;

      m_jTxtValues = new JTextField[numEditableCells];
      int count = 0;

      for (j = 0; j < alphSize; ++j) {

         scorePanel.add(jVTitles[j]);

         for (i = 0; i < j+1; ++i) {
            m_jTxtValues[count] = new JTextField();
            m_jTxtValues[count].setHorizontalAlignment(SwingConstants.CENTER);
            scorePanel.add(m_jTxtValues[count]);
            count++;
         }

         for (i = j+1; i < alphSize; ++i) {
            scorePanel.add(new JLabel());
         }

      }

      // SOUTH
      JPanel southPanel = new JPanel();
      southPanel.add(m_btnOk);
      southPanel.add(m_btnCancel);

      // NORTH
      JPanel northPanel = new JPanel();
      northPanel.add(new JLabel("ScoreMatrix:"));
      northPanel.add(m_matrixSelection);

      // Add everything to the basic panel
      basePanel.add(northPanel, BorderLayout.NORTH);
      basePanel.add(scorePanel, BorderLayout.CENTER);
      basePanel.add(southPanel, BorderLayout.SOUTH);

      getContentPane().add(basePanel);

      // EVENTS
      m_btnOk.addActionListener(new ButtonListener());
      m_btnCancel.addActionListener(new ButtonListener());

   }

   protected void loadScoreMatrix(int scoreMatrix) {

      ScoreHash wholeMatrix = null;

      switch (scoreMatrix) {

         case SCOREMATRIX_ORIGINAL:
            wholeMatrix = m_passedHash;
            break;

         case SCOREMATRIX_PAM250:

            wholeMatrix = new ScoreHash();
            wholeMatrix.setScore('A', 'A', 2);
            wholeMatrix.setScore('R', 'A', -2);
            wholeMatrix.setScore('R', 'R', 6);
            wholeMatrix.setScore('N', 'A', 0);
            wholeMatrix.setScore('N', 'R', 0);
            wholeMatrix.setScore('N', 'N', 2);
            wholeMatrix.setScore('D', 'A', 0);
            wholeMatrix.setScore('D', 'R', -1);
            wholeMatrix.setScore('D', 'N', 2);
            wholeMatrix.setScore('D', 'D', 4);
            wholeMatrix.setScore('C', 'A', -2);
            wholeMatrix.setScore('C', 'R', -4);
            wholeMatrix.setScore('C', 'N', -4);
            wholeMatrix.setScore('C', 'D', -5);
            wholeMatrix.setScore('C', 'C', 12);
            wholeMatrix.setScore('Q', 'A', 0);
            wholeMatrix.setScore('Q', 'R', 1);
            wholeMatrix.setScore('Q', 'N', 1);
            wholeMatrix.setScore('Q', 'D', 2);
            wholeMatrix.setScore('Q', 'C', -5);
            wholeMatrix.setScore('Q', 'Q', 4);
            wholeMatrix.setScore('E', 'A', 0);
            wholeMatrix.setScore('E', 'R', -1);
            wholeMatrix.setScore('E', 'N', 1);
            wholeMatrix.setScore('E', 'D', 3);
            wholeMatrix.setScore('E', 'C', -5);
            wholeMatrix.setScore('E', 'Q', 2);
            wholeMatrix.setScore('E', 'E', 4);
            wholeMatrix.setScore('G', 'A', 1);
            wholeMatrix.setScore('G', 'R', -3);
            wholeMatrix.setScore('G', 'N', 0);
            wholeMatrix.setScore('G', 'D', 1);
            wholeMatrix.setScore('G', 'C', -3);
            wholeMatrix.setScore('G', 'Q', -1);
            wholeMatrix.setScore('G', 'E', 0);
            wholeMatrix.setScore('G', 'G', 5);
            wholeMatrix.setScore('H', 'A', -1);
            wholeMatrix.setScore('H', 'R', 2);
            wholeMatrix.setScore('H', 'N', 2);
            wholeMatrix.setScore('H', 'D', 1);
            wholeMatrix.setScore('H', 'C', -3);
            wholeMatrix.setScore('H', 'Q', 3);
            wholeMatrix.setScore('H', 'E', 1);
            wholeMatrix.setScore('H', 'G', -2);
            wholeMatrix.setScore('H', 'H', 6);
            wholeMatrix.setScore('I', 'A', -1);
            wholeMatrix.setScore('I', 'R', -2);
            wholeMatrix.setScore('I', 'N', -2);
            wholeMatrix.setScore('I', 'D', -2);
            wholeMatrix.setScore('I', 'C', -2);
            wholeMatrix.setScore('I', 'Q', -2);
            wholeMatrix.setScore('I', 'E', -2);
            wholeMatrix.setScore('I', 'G', -3);
            wholeMatrix.setScore('I', 'H', -2);
            wholeMatrix.setScore('I', 'I', 5);
            wholeMatrix.setScore('L', 'A', -2);
            wholeMatrix.setScore('L', 'R', -3);
            wholeMatrix.setScore('L', 'N', -3);
            wholeMatrix.setScore('L', 'D', -4);
            wholeMatrix.setScore('L', 'C', -6);
            wholeMatrix.setScore('L', 'Q', -2);
            wholeMatrix.setScore('L', 'E', -3);
            wholeMatrix.setScore('L', 'G', -4);
            wholeMatrix.setScore('L', 'H', -2);
            wholeMatrix.setScore('L', 'I', 2);
            wholeMatrix.setScore('L', 'L', 6);
            wholeMatrix.setScore('K', 'A', -1);
            wholeMatrix.setScore('K', 'R', 3);
            wholeMatrix.setScore('K', 'N', 1);
            wholeMatrix.setScore('K', 'D', 0);
            wholeMatrix.setScore('K', 'C', -5);
            wholeMatrix.setScore('K', 'Q', 1);
            wholeMatrix.setScore('K', 'E', 0);
            wholeMatrix.setScore('K', 'G', -2);
            wholeMatrix.setScore('K', 'H', 0);
            wholeMatrix.setScore('K', 'I', -2);
            wholeMatrix.setScore('K', 'L', -3);
            wholeMatrix.setScore('K', 'K', 5);
            wholeMatrix.setScore('M', 'A', -1);
            wholeMatrix.setScore('M', 'R', 0);
            wholeMatrix.setScore('M', 'N', -2);
            wholeMatrix.setScore('M', 'D', -3);
            wholeMatrix.setScore('M', 'C', -5);
            wholeMatrix.setScore('M', 'Q', -1);
            wholeMatrix.setScore('M', 'E', -2);
            wholeMatrix.setScore('M', 'G', -3);
            wholeMatrix.setScore('M', 'H', -2);
            wholeMatrix.setScore('M', 'I', 2);
            wholeMatrix.setScore('M', 'L', 4);
            wholeMatrix.setScore('M', 'K', 0);
            wholeMatrix.setScore('M', 'M', 6);
            wholeMatrix.setScore('F', 'A', -3);
            wholeMatrix.setScore('F', 'R', -4);
            wholeMatrix.setScore('F', 'N', -3);
            wholeMatrix.setScore('F', 'D', -6);
            wholeMatrix.setScore('F', 'C', -4);
            wholeMatrix.setScore('F', 'Q', -5);
            wholeMatrix.setScore('F', 'E', -5);
            wholeMatrix.setScore('F', 'G', -5);
            wholeMatrix.setScore('F', 'H', -2);
            wholeMatrix.setScore('F', 'I', 1);
            wholeMatrix.setScore('F', 'L', 2);
            wholeMatrix.setScore('F', 'K', -5);
            wholeMatrix.setScore('F', 'M', 0);
            wholeMatrix.setScore('F', 'F', 9);
            wholeMatrix.setScore('P', 'A', 1);
            wholeMatrix.setScore('P', 'R', 0);
            wholeMatrix.setScore('P', 'N', 0);
            wholeMatrix.setScore('P', 'D', -1);
            wholeMatrix.setScore('P', 'C', -3);
            wholeMatrix.setScore('P', 'Q', 0);
            wholeMatrix.setScore('P', 'E', -1);
            wholeMatrix.setScore('P', 'G', 0);
            wholeMatrix.setScore('P', 'H', 0);
            wholeMatrix.setScore('P', 'I', -2);
            wholeMatrix.setScore('P', 'L', -3);
            wholeMatrix.setScore('P', 'K', -1);
            wholeMatrix.setScore('P', 'M', -2);
            wholeMatrix.setScore('P', 'F', -5);
            wholeMatrix.setScore('P', 'P', 6);
            wholeMatrix.setScore('S', 'A', 1);
            wholeMatrix.setScore('S', 'R', 0);
            wholeMatrix.setScore('S', 'N', 1);
            wholeMatrix.setScore('S', 'D', 0);
            wholeMatrix.setScore('S', 'C', 0);
            wholeMatrix.setScore('S', 'Q', -1);
            wholeMatrix.setScore('S', 'E', 0);
            wholeMatrix.setScore('S', 'G', 1);
            wholeMatrix.setScore('S', 'H', -1);
            wholeMatrix.setScore('S', 'I', -1);
            wholeMatrix.setScore('S', 'L', -3);
            wholeMatrix.setScore('S', 'K', 0);
            wholeMatrix.setScore('S', 'M', -2);
            wholeMatrix.setScore('S', 'F', -3);
            wholeMatrix.setScore('S', 'P', 1);
            wholeMatrix.setScore('S', 'S', 2);
            wholeMatrix.setScore('T', 'A', 1);
            wholeMatrix.setScore('T', 'R', -1);
            wholeMatrix.setScore('T', 'N', 0);
            wholeMatrix.setScore('T', 'D', 0);
            wholeMatrix.setScore('T', 'C', -2);
            wholeMatrix.setScore('T', 'Q', -1);
            wholeMatrix.setScore('T', 'E', 0);
            wholeMatrix.setScore('T', 'G', 0);
            wholeMatrix.setScore('T', 'H', -1);
            wholeMatrix.setScore('T', 'I', 0);
            wholeMatrix.setScore('T', 'L', -2);
            wholeMatrix.setScore('T', 'K', 0);
            wholeMatrix.setScore('T', 'M', -1);
            wholeMatrix.setScore('T', 'F', -3);
            wholeMatrix.setScore('T', 'P', 0);
            wholeMatrix.setScore('T', 'S', 1);
            wholeMatrix.setScore('T', 'T', 3);
            wholeMatrix.setScore('W', 'A', -6);
            wholeMatrix.setScore('W', 'R', 2);
            wholeMatrix.setScore('W', 'N', -4);
            wholeMatrix.setScore('W', 'D', -7);
            wholeMatrix.setScore('W', 'C', -8);
            wholeMatrix.setScore('W', 'Q', -5);
            wholeMatrix.setScore('W', 'E', -7);
            wholeMatrix.setScore('W', 'G', -7);
            wholeMatrix.setScore('W', 'H', -3);
            wholeMatrix.setScore('W', 'I', -5);
            wholeMatrix.setScore('W', 'L', -2);
            wholeMatrix.setScore('W', 'K', -3);
            wholeMatrix.setScore('W', 'M', -4);
            wholeMatrix.setScore('W', 'F', 0);
            wholeMatrix.setScore('W', 'P', -6);
            wholeMatrix.setScore('W', 'S', -2);
            wholeMatrix.setScore('W', 'T', -5);
            wholeMatrix.setScore('W', 'W', 17);
            wholeMatrix.setScore('Y', 'A', -3);
            wholeMatrix.setScore('Y', 'R', -4);
            wholeMatrix.setScore('Y', 'N', -2);
            wholeMatrix.setScore('Y', 'D', -4);
            wholeMatrix.setScore('Y', 'C', 0);
            wholeMatrix.setScore('Y', 'Q', -4);
            wholeMatrix.setScore('Y', 'E', -4);
            wholeMatrix.setScore('Y', 'G', -5);
            wholeMatrix.setScore('Y', 'H', 0);
            wholeMatrix.setScore('Y', 'I', -1);
            wholeMatrix.setScore('Y', 'L', -1);
            wholeMatrix.setScore('Y', 'K', -4);
            wholeMatrix.setScore('Y', 'M', -2);
            wholeMatrix.setScore('Y', 'F', 7);
            wholeMatrix.setScore('Y', 'P', -5);
            wholeMatrix.setScore('Y', 'S', -3);
            wholeMatrix.setScore('Y', 'T', -3);
            wholeMatrix.setScore('Y', 'W', 0);
            wholeMatrix.setScore('Y', 'Y', 10);
            wholeMatrix.setScore('V', 'A', 0);
            wholeMatrix.setScore('V', 'R', -2);
            wholeMatrix.setScore('V', 'N', -2);
            wholeMatrix.setScore('V', 'D', -2);
            wholeMatrix.setScore('V', 'C', -2);
            wholeMatrix.setScore('V', 'Q', -2);
            wholeMatrix.setScore('V', 'E', -2);
            wholeMatrix.setScore('V', 'G', -1);
            wholeMatrix.setScore('V', 'H', -2);
            wholeMatrix.setScore('V', 'I', 4);
            wholeMatrix.setScore('V', 'L', 2);
            wholeMatrix.setScore('V', 'K', -2);
            wholeMatrix.setScore('V', 'M', 2);
            wholeMatrix.setScore('V', 'F', -1);
            wholeMatrix.setScore('V', 'P', -1);
            wholeMatrix.setScore('V', 'S', -1);
            wholeMatrix.setScore('V', 'T', 0);
            wholeMatrix.setScore('V', 'W', -6);
            wholeMatrix.setScore('V', 'Y', -2);
            wholeMatrix.setScore('V', 'V', 4);
            wholeMatrix.setScore('B', 'A', 0);
            wholeMatrix.setScore('B', 'R', -1);
            wholeMatrix.setScore('B', 'N', 2);
            wholeMatrix.setScore('B', 'D', 3);
            wholeMatrix.setScore('B', 'C', -4);
            wholeMatrix.setScore('B', 'Q', 1);
            wholeMatrix.setScore('B', 'E', 3);
            wholeMatrix.setScore('B', 'G', 0);
            wholeMatrix.setScore('B', 'H', 1);
            wholeMatrix.setScore('B', 'I', -2);
            wholeMatrix.setScore('B', 'L', -3);
            wholeMatrix.setScore('B', 'K', 1);
            wholeMatrix.setScore('B', 'M', -2);
            wholeMatrix.setScore('B', 'F', -4);
            wholeMatrix.setScore('B', 'P', -1);
            wholeMatrix.setScore('B', 'S', 0);
            wholeMatrix.setScore('B', 'T', 0);
            wholeMatrix.setScore('B', 'W', -5);
            wholeMatrix.setScore('B', 'Y', -3);
            wholeMatrix.setScore('B', 'V', -2);
            wholeMatrix.setScore('B', 'B', 3);
            wholeMatrix.setScore('Z', 'A', 0);
            wholeMatrix.setScore('Z', 'R', 0);
            wholeMatrix.setScore('Z', 'N', 1);
            wholeMatrix.setScore('Z', 'D', 3);
            wholeMatrix.setScore('Z', 'C', -5);
            wholeMatrix.setScore('Z', 'Q', 3);
            wholeMatrix.setScore('Z', 'E', 3);
            wholeMatrix.setScore('Z', 'G', 0);
            wholeMatrix.setScore('Z', 'H', 2);
            wholeMatrix.setScore('Z', 'I', -2);
            wholeMatrix.setScore('Z', 'L', -3);
            wholeMatrix.setScore('Z', 'K', 0);
            wholeMatrix.setScore('Z', 'M', -2);
            wholeMatrix.setScore('Z', 'F', -5);
            wholeMatrix.setScore('Z', 'P', 0);
            wholeMatrix.setScore('Z', 'S', 0);
            wholeMatrix.setScore('Z', 'T', -1);
            wholeMatrix.setScore('Z', 'W', -6);
            wholeMatrix.setScore('Z', 'Y', -4);
            wholeMatrix.setScore('Z', 'V', -2);
            wholeMatrix.setScore('Z', 'B', 2);
            wholeMatrix.setScore('Z', 'Z', 3);
            wholeMatrix.setScore('X', 'A', 0);
            wholeMatrix.setScore('X', 'R', -1);
            wholeMatrix.setScore('X', 'N', 0);
            wholeMatrix.setScore('X', 'D', -1);
            wholeMatrix.setScore('X', 'C', -3);
            wholeMatrix.setScore('X', 'Q', -1);
            wholeMatrix.setScore('X', 'E', -1);
            wholeMatrix.setScore('X', 'G', -1);
            wholeMatrix.setScore('X', 'H', -1);
            wholeMatrix.setScore('X', 'I', -1);
            wholeMatrix.setScore('X', 'L', -1);
            wholeMatrix.setScore('X', 'K', -1);
            wholeMatrix.setScore('X', 'M', -1);
            wholeMatrix.setScore('X', 'F', -2);
            wholeMatrix.setScore('X', 'P', -1);
            wholeMatrix.setScore('X', 'S', 0);
            wholeMatrix.setScore('X', 'T', 0);
            wholeMatrix.setScore('X', 'W', -4);
            wholeMatrix.setScore('X', 'Y', -2);
            wholeMatrix.setScore('X', 'V', -1);
            wholeMatrix.setScore('X', 'B', -1);
            wholeMatrix.setScore('X', 'Z', -1);
            wholeMatrix.setScore('X', 'X', -1);

            break;

         case SCOREMATRIX_BLOSUM62:

            wholeMatrix = new ScoreHash();
            wholeMatrix.setScore('A', 'A', 4);
            wholeMatrix.setScore('R', 'A', -1);
            wholeMatrix.setScore('R', 'R', 5);
            wholeMatrix.setScore('N', 'A', -2);
            wholeMatrix.setScore('N', 'R', 0);
            wholeMatrix.setScore('N', 'N', 6);
            wholeMatrix.setScore('D', 'A', -2);
            wholeMatrix.setScore('D', 'R', -2);
            wholeMatrix.setScore('D', 'N', 1);
            wholeMatrix.setScore('D', 'D', 6);
            wholeMatrix.setScore('C', 'A', 0);
            wholeMatrix.setScore('C', 'R', -3);
            wholeMatrix.setScore('C', 'N', -3);
            wholeMatrix.setScore('C', 'D', -3);
            wholeMatrix.setScore('C', 'C', 9);
            wholeMatrix.setScore('Q', 'A', -1);
            wholeMatrix.setScore('Q', 'R', 1);
            wholeMatrix.setScore('Q', 'N', 0);
            wholeMatrix.setScore('Q', 'D', 0);
            wholeMatrix.setScore('Q', 'C', -3);
            wholeMatrix.setScore('Q', 'Q', 5);
            wholeMatrix.setScore('E', 'A', -1);
            wholeMatrix.setScore('E', 'R', 0);
            wholeMatrix.setScore('E', 'N', 0);
            wholeMatrix.setScore('E', 'D', 2);
            wholeMatrix.setScore('E', 'C', -4);
            wholeMatrix.setScore('E', 'Q', 2);
            wholeMatrix.setScore('E', 'E', 5);
            wholeMatrix.setScore('G', 'A', 0);
            wholeMatrix.setScore('G', 'R', -2);
            wholeMatrix.setScore('G', 'N', 0);
            wholeMatrix.setScore('G', 'D', -1);
            wholeMatrix.setScore('G', 'C', -3);
            wholeMatrix.setScore('G', 'Q', -2);
            wholeMatrix.setScore('G', 'E', -2);
            wholeMatrix.setScore('G', 'G', 6);
            wholeMatrix.setScore('H', 'A', -2);
            wholeMatrix.setScore('H', 'R', 0);
            wholeMatrix.setScore('H', 'N', 1);
            wholeMatrix.setScore('H', 'D', -1);
            wholeMatrix.setScore('H', 'C', -3);
            wholeMatrix.setScore('H', 'Q', 0);
            wholeMatrix.setScore('H', 'E', 0);
            wholeMatrix.setScore('H', 'G', -2);
            wholeMatrix.setScore('H', 'H', 8);
            wholeMatrix.setScore('I', 'A', -1);
            wholeMatrix.setScore('I', 'R', -3);
            wholeMatrix.setScore('I', 'N', -3);
            wholeMatrix.setScore('I', 'D', -3);
            wholeMatrix.setScore('I', 'C', -1);
            wholeMatrix.setScore('I', 'Q', -3);
            wholeMatrix.setScore('I', 'E', -3);
            wholeMatrix.setScore('I', 'G', -4);
            wholeMatrix.setScore('I', 'H', -3);
            wholeMatrix.setScore('I', 'I', 4);
            wholeMatrix.setScore('L', 'A', -1);
            wholeMatrix.setScore('L', 'R', -2);
            wholeMatrix.setScore('L', 'N', -3);
            wholeMatrix.setScore('L', 'D', -4);
            wholeMatrix.setScore('L', 'C', -1);
            wholeMatrix.setScore('L', 'Q', -2);
            wholeMatrix.setScore('L', 'E', -3);
            wholeMatrix.setScore('L', 'G', -4);
            wholeMatrix.setScore('L', 'H', -3);
            wholeMatrix.setScore('L', 'I', 2);
            wholeMatrix.setScore('L', 'L', 4);
            wholeMatrix.setScore('K', 'A', -1);
            wholeMatrix.setScore('K', 'R', 2);
            wholeMatrix.setScore('K', 'N', 0);
            wholeMatrix.setScore('K', 'D', -1);
            wholeMatrix.setScore('K', 'C', -3);
            wholeMatrix.setScore('K', 'Q', 1);
            wholeMatrix.setScore('K', 'E', 1);
            wholeMatrix.setScore('K', 'G', -2);
            wholeMatrix.setScore('K', 'H', -1);
            wholeMatrix.setScore('K', 'I', -3);
            wholeMatrix.setScore('K', 'L', -2);
            wholeMatrix.setScore('K', 'K', 5);
            wholeMatrix.setScore('M', 'A', -1);
            wholeMatrix.setScore('M', 'R', -1);
            wholeMatrix.setScore('M', 'N', -2);
            wholeMatrix.setScore('M', 'D', -3);
            wholeMatrix.setScore('M', 'C', -1);
            wholeMatrix.setScore('M', 'Q', 0);
            wholeMatrix.setScore('M', 'E', -2);
            wholeMatrix.setScore('M', 'G', -3);
            wholeMatrix.setScore('M', 'H', -2);
            wholeMatrix.setScore('M', 'I', 1);
            wholeMatrix.setScore('M', 'L', 2);
            wholeMatrix.setScore('M', 'K', -1);
            wholeMatrix.setScore('M', 'M', 5);
            wholeMatrix.setScore('F', 'A', -2);
            wholeMatrix.setScore('F', 'R', -3);
            wholeMatrix.setScore('F', 'N', -3);
            wholeMatrix.setScore('F', 'D', -3);
            wholeMatrix.setScore('F', 'C', -2);
            wholeMatrix.setScore('F', 'Q', -3);
            wholeMatrix.setScore('F', 'E', -3);
            wholeMatrix.setScore('F', 'G', -3);
            wholeMatrix.setScore('F', 'H', -1);
            wholeMatrix.setScore('F', 'I', 0);
            wholeMatrix.setScore('F', 'L', 0);
            wholeMatrix.setScore('F', 'K', -3);
            wholeMatrix.setScore('F', 'M', 0);
            wholeMatrix.setScore('F', 'F', 6);
            wholeMatrix.setScore('P', 'A', -1);
            wholeMatrix.setScore('P', 'R', -2);
            wholeMatrix.setScore('P', 'N', -2);
            wholeMatrix.setScore('P', 'D', -1);
            wholeMatrix.setScore('P', 'C', -3);
            wholeMatrix.setScore('P', 'Q', -1);
            wholeMatrix.setScore('P', 'E', -1);
            wholeMatrix.setScore('P', 'G', -2);
            wholeMatrix.setScore('P', 'H', -2);
            wholeMatrix.setScore('P', 'I', -3);
            wholeMatrix.setScore('P', 'L', -3);
            wholeMatrix.setScore('P', 'K', -1);
            wholeMatrix.setScore('P', 'M', -2);
            wholeMatrix.setScore('P', 'F', -4);
            wholeMatrix.setScore('P', 'P', 7);
            wholeMatrix.setScore('S', 'A', 1);
            wholeMatrix.setScore('S', 'R', -1);
            wholeMatrix.setScore('S', 'N', 1);
            wholeMatrix.setScore('S', 'D', 0);
            wholeMatrix.setScore('S', 'C', -1);
            wholeMatrix.setScore('S', 'Q', 0);
            wholeMatrix.setScore('S', 'E', 0);
            wholeMatrix.setScore('S', 'G', 0);
            wholeMatrix.setScore('S', 'H', -1);
            wholeMatrix.setScore('S', 'I', -2);
            wholeMatrix.setScore('S', 'L', -2);
            wholeMatrix.setScore('S', 'K', 0);
            wholeMatrix.setScore('S', 'M', -1);
            wholeMatrix.setScore('S', 'F', -2);
            wholeMatrix.setScore('S', 'P', -1);
            wholeMatrix.setScore('S', 'S', 4);
            wholeMatrix.setScore('T', 'A', 0);
            wholeMatrix.setScore('T', 'R', -1);
            wholeMatrix.setScore('T', 'N', 0);
            wholeMatrix.setScore('T', 'D', -1);
            wholeMatrix.setScore('T', 'C', -1);
            wholeMatrix.setScore('T', 'Q', -1);
            wholeMatrix.setScore('T', 'E', -1);
            wholeMatrix.setScore('T', 'G', -2);
            wholeMatrix.setScore('T', 'H', -2);
            wholeMatrix.setScore('T', 'I', -1);
            wholeMatrix.setScore('T', 'L', -1);
            wholeMatrix.setScore('T', 'K', -1);
            wholeMatrix.setScore('T', 'M', -1);
            wholeMatrix.setScore('T', 'F', -2);
            wholeMatrix.setScore('T', 'P', -1);
            wholeMatrix.setScore('T', 'S', 1);
            wholeMatrix.setScore('T', 'T', 5);
            wholeMatrix.setScore('W', 'A', -3);
            wholeMatrix.setScore('W', 'R', -3);
            wholeMatrix.setScore('W', 'N', -4);
            wholeMatrix.setScore('W', 'D', -4);
            wholeMatrix.setScore('W', 'C', -2);
            wholeMatrix.setScore('W', 'Q', -2);
            wholeMatrix.setScore('W', 'E', -3);
            wholeMatrix.setScore('W', 'G', -2);
            wholeMatrix.setScore('W', 'H', -2);
            wholeMatrix.setScore('W', 'I', -3);
            wholeMatrix.setScore('W', 'L', -2);
            wholeMatrix.setScore('W', 'K', -3);
            wholeMatrix.setScore('W', 'M', -1);
            wholeMatrix.setScore('W', 'F', 1);
            wholeMatrix.setScore('W', 'P', -4);
            wholeMatrix.setScore('W', 'S', -3);
            wholeMatrix.setScore('W', 'T', -2);
            wholeMatrix.setScore('W', 'W', 11);
            wholeMatrix.setScore('Y', 'A', -2);
            wholeMatrix.setScore('Y', 'R', -2);
            wholeMatrix.setScore('Y', 'N', -2);
            wholeMatrix.setScore('Y', 'D', -3);
            wholeMatrix.setScore('Y', 'C', -2);
            wholeMatrix.setScore('Y', 'Q', -1);
            wholeMatrix.setScore('Y', 'E', -2);
            wholeMatrix.setScore('Y', 'G', -3);
            wholeMatrix.setScore('Y', 'H', 2);
            wholeMatrix.setScore('Y', 'I', -1);
            wholeMatrix.setScore('Y', 'L', -1);
            wholeMatrix.setScore('Y', 'K', -2);
            wholeMatrix.setScore('Y', 'M', -1);
            wholeMatrix.setScore('Y', 'F', 3);
            wholeMatrix.setScore('Y', 'P', -3);
            wholeMatrix.setScore('Y', 'S', -2);
            wholeMatrix.setScore('Y', 'T', -2);
            wholeMatrix.setScore('Y', 'W', 2);
            wholeMatrix.setScore('Y', 'Y', 7);
            wholeMatrix.setScore('V', 'A', 0);
            wholeMatrix.setScore('V', 'R', -3);
            wholeMatrix.setScore('V', 'N', -3);
            wholeMatrix.setScore('V', 'D', -3);
            wholeMatrix.setScore('V', 'C', -1);
            wholeMatrix.setScore('V', 'Q', -2);
            wholeMatrix.setScore('V', 'E', -2);
            wholeMatrix.setScore('V', 'G', -3);
            wholeMatrix.setScore('V', 'H', -3);
            wholeMatrix.setScore('V', 'I', 3);
            wholeMatrix.setScore('V', 'L', 1);
            wholeMatrix.setScore('V', 'K', -2);
            wholeMatrix.setScore('V', 'M', 1);
            wholeMatrix.setScore('V', 'F', -1);
            wholeMatrix.setScore('V', 'P', -2);
            wholeMatrix.setScore('V', 'S', -2);
            wholeMatrix.setScore('V', 'T', 0);
            wholeMatrix.setScore('V', 'W', -3);
            wholeMatrix.setScore('V', 'Y', -1);
            wholeMatrix.setScore('V', 'V', 4);

            break;

         case SCOREMATRIX_VTML160:

            wholeMatrix = new ScoreHash();
            wholeMatrix.setScore('A', 'A', 5);
            wholeMatrix.setScore('R', 'A', -2);
            wholeMatrix.setScore('R', 'R', 7);
            wholeMatrix.setScore('N', 'A', -1);
            wholeMatrix.setScore('N', 'R', 0);
            wholeMatrix.setScore('N', 'N', 7);
            wholeMatrix.setScore('D', 'A', -1);
            wholeMatrix.setScore('D', 'R', -3);
            wholeMatrix.setScore('D', 'N', 3);
            wholeMatrix.setScore('D', 'D', 7);
            wholeMatrix.setScore('C', 'A', 1);
            wholeMatrix.setScore('C', 'R', -3);
            wholeMatrix.setScore('C', 'N', -3);
            wholeMatrix.setScore('C', 'D', -5);
            wholeMatrix.setScore('C', 'C', 13);
            wholeMatrix.setScore('Q', 'A', -1);
            wholeMatrix.setScore('Q', 'R', 2);
            wholeMatrix.setScore('Q', 'N', 0);
            wholeMatrix.setScore('Q', 'D', 1);
            wholeMatrix.setScore('Q', 'C', -4);
            wholeMatrix.setScore('Q', 'Q', 6);
            wholeMatrix.setScore('E', 'A', -1);
            wholeMatrix.setScore('E', 'R', -1);
            wholeMatrix.setScore('E', 'N', 0);
            wholeMatrix.setScore('E', 'D', 3);
            wholeMatrix.setScore('E', 'C', -5);
            wholeMatrix.setScore('E', 'Q', 2);
            wholeMatrix.setScore('E', 'E', 6);
            wholeMatrix.setScore('G', 'A', 0);
            wholeMatrix.setScore('G', 'R', -3);
            wholeMatrix.setScore('G', 'N', 0);
            wholeMatrix.setScore('G', 'D', -1);
            wholeMatrix.setScore('G', 'C', -2);
            wholeMatrix.setScore('G', 'Q', -3);
            wholeMatrix.setScore('G', 'E', -2);
            wholeMatrix.setScore('G', 'G', 8);
            wholeMatrix.setScore('H', 'A', -2);
            wholeMatrix.setScore('H', 'R', 1);
            wholeMatrix.setScore('H', 'N', 1);
            wholeMatrix.setScore('H', 'D', 0);
            wholeMatrix.setScore('H', 'C', -2);
            wholeMatrix.setScore('H', 'Q', 2);
            wholeMatrix.setScore('H', 'E', -1);
            wholeMatrix.setScore('H', 'G', -3);
            wholeMatrix.setScore('H', 'H', 9);
            wholeMatrix.setScore('I', 'A', -1);
            wholeMatrix.setScore('I', 'R', -4);
            wholeMatrix.setScore('I', 'N', -4);
            wholeMatrix.setScore('I', 'D', -6);
            wholeMatrix.setScore('I', 'C', -1);
            wholeMatrix.setScore('I', 'Q', -4);
            wholeMatrix.setScore('I', 'E', -5);
            wholeMatrix.setScore('I', 'G', -7);
            wholeMatrix.setScore('I', 'H', -4);
            wholeMatrix.setScore('I', 'I', 6);
            wholeMatrix.setScore('L', 'A', -2);
            wholeMatrix.setScore('L', 'R', -3);
            wholeMatrix.setScore('L', 'N', -4);
            wholeMatrix.setScore('L', 'D', -6);
            wholeMatrix.setScore('L', 'C', -4);
            wholeMatrix.setScore('L', 'Q', -2);
            wholeMatrix.setScore('L', 'E', -4);
            wholeMatrix.setScore('L', 'G', -6);
            wholeMatrix.setScore('L', 'H', -3);
            wholeMatrix.setScore('L', 'I', 3);
            wholeMatrix.setScore('L', 'L', 6);
            wholeMatrix.setScore('K', 'A', -1);
            wholeMatrix.setScore('K', 'R', 4);
            wholeMatrix.setScore('K', 'N', 0);
            wholeMatrix.setScore('K', 'D', 0);
            wholeMatrix.setScore('K', 'C', -4);
            wholeMatrix.setScore('K', 'Q', 2);
            wholeMatrix.setScore('K', 'E', 1);
            wholeMatrix.setScore('K', 'G', -2);
            wholeMatrix.setScore('K', 'H', 0);
            wholeMatrix.setScore('K', 'I', -4);
            wholeMatrix.setScore('K', 'L', -3);
            wholeMatrix.setScore('K', 'K', 5);
            wholeMatrix.setScore('M', 'A', -1);
            wholeMatrix.setScore('M', 'R', -2);
            wholeMatrix.setScore('M', 'N', -3);
            wholeMatrix.setScore('M', 'D', -5);
            wholeMatrix.setScore('M', 'C', -1);
            wholeMatrix.setScore('M', 'Q', -1);
            wholeMatrix.setScore('M', 'E', -3);
            wholeMatrix.setScore('M', 'G', -5);
            wholeMatrix.setScore('M', 'H', -3);
            wholeMatrix.setScore('M', 'I', 2);
            wholeMatrix.setScore('M', 'L', 4);
            wholeMatrix.setScore('M', 'K', -2);
            wholeMatrix.setScore('M', 'M', 8);
            wholeMatrix.setScore('F', 'A', -3);
            wholeMatrix.setScore('F', 'R', -5);
            wholeMatrix.setScore('F', 'N', -5);
            wholeMatrix.setScore('F', 'D', -7);
            wholeMatrix.setScore('F', 'C', -4);
            wholeMatrix.setScore('F', 'Q', -4);
            wholeMatrix.setScore('F', 'E', -6);
            wholeMatrix.setScore('F', 'G', -6);
            wholeMatrix.setScore('F', 'H', 0);
            wholeMatrix.setScore('F', 'I', 0);
            wholeMatrix.setScore('F', 'L', 2);
            wholeMatrix.setScore('F', 'K', -5);
            wholeMatrix.setScore('F', 'M', 1);
            wholeMatrix.setScore('F', 'F', 9);
            wholeMatrix.setScore('P', 'A', 0);
            wholeMatrix.setScore('P', 'R', -2);
            wholeMatrix.setScore('P', 'N', -2);
            wholeMatrix.setScore('P', 'D', -1);
            wholeMatrix.setScore('P', 'C', -3);
            wholeMatrix.setScore('P', 'Q', -1);
            wholeMatrix.setScore('P', 'E', -1);
            wholeMatrix.setScore('P', 'G', -3);
            wholeMatrix.setScore('P', 'H', -2);
            wholeMatrix.setScore('P', 'I', -4);
            wholeMatrix.setScore('P', 'L', -3);
            wholeMatrix.setScore('P', 'K', -1);
            wholeMatrix.setScore('P', 'M', -4);
            wholeMatrix.setScore('P', 'F', -5);
            wholeMatrix.setScore('P', 'P', 9);
            wholeMatrix.setScore('S', 'A', 1);
            wholeMatrix.setScore('S', 'R', -1);
            wholeMatrix.setScore('S', 'N', 1);
            wholeMatrix.setScore('S', 'D', 0);
            wholeMatrix.setScore('S', 'C', 1);
            wholeMatrix.setScore('S', 'Q', 0);
            wholeMatrix.setScore('S', 'E', 0);
            wholeMatrix.setScore('S', 'G', 0);
            wholeMatrix.setScore('S', 'H', -1);
            wholeMatrix.setScore('S', 'I', -3);
            wholeMatrix.setScore('S', 'L', -3);
            wholeMatrix.setScore('S', 'K', -1);
            wholeMatrix.setScore('S', 'M', -3);
            wholeMatrix.setScore('S', 'F', -3);
            wholeMatrix.setScore('S', 'P', 0);
            wholeMatrix.setScore('S', 'S', 4);
            wholeMatrix.setScore('T', 'A', 1);
            wholeMatrix.setScore('T', 'R', -1);
            wholeMatrix.setScore('T', 'N', 0);
            wholeMatrix.setScore('T', 'D', -1);
            wholeMatrix.setScore('T', 'C', 0);
            wholeMatrix.setScore('T', 'Q', -1);
            wholeMatrix.setScore('T', 'E', -1);
            wholeMatrix.setScore('T', 'G', -2);
            wholeMatrix.setScore('T', 'H', -1);
            wholeMatrix.setScore('T', 'I', -1);
            wholeMatrix.setScore('T', 'L', -2);
            wholeMatrix.setScore('T', 'K', -1);
            wholeMatrix.setScore('T', 'M', -1);
            wholeMatrix.setScore('T', 'F', -3);
            wholeMatrix.setScore('T', 'P', -1);
            wholeMatrix.setScore('T', 'S', 2);
            wholeMatrix.setScore('T', 'T', 5);
            wholeMatrix.setScore('W', 'A', -5);
            wholeMatrix.setScore('W', 'R', -4);
            wholeMatrix.setScore('W', 'N', -5);
            wholeMatrix.setScore('W', 'D', -7);
            wholeMatrix.setScore('W', 'C', -7);
            wholeMatrix.setScore('W', 'Q', -6);
            wholeMatrix.setScore('W', 'E', -7);
            wholeMatrix.setScore('W', 'G', -5);
            wholeMatrix.setScore('W', 'H', -1);
            wholeMatrix.setScore('W', 'I', -2);
            wholeMatrix.setScore('W', 'L', -1);
            wholeMatrix.setScore('W', 'K', -5);
            wholeMatrix.setScore('W', 'M', -4);
            wholeMatrix.setScore('W', 'F', 3);
            wholeMatrix.setScore('W', 'P', -5);
            wholeMatrix.setScore('W', 'S', -4);
            wholeMatrix.setScore('W', 'T', -6);
            wholeMatrix.setScore('W', 'W', 16);
            wholeMatrix.setScore('Y', 'A', -3);
            wholeMatrix.setScore('Y', 'R', -3);
            wholeMatrix.setScore('Y', 'N', -2);
            wholeMatrix.setScore('Y', 'D', -5);
            wholeMatrix.setScore('Y', 'C', -1);
            wholeMatrix.setScore('Y', 'Q', -4);
            wholeMatrix.setScore('Y', 'E', -3);
            wholeMatrix.setScore('Y', 'G', -5);
            wholeMatrix.setScore('Y', 'H', 3);
            wholeMatrix.setScore('Y', 'I', -2);
            wholeMatrix.setScore('Y', 'L', -1);
            wholeMatrix.setScore('Y', 'K', -3);
            wholeMatrix.setScore('Y', 'M', -2);
            wholeMatrix.setScore('Y', 'F', 6);
            wholeMatrix.setScore('Y', 'P', -6);
            wholeMatrix.setScore('Y', 'S', -2);
            wholeMatrix.setScore('Y', 'T', -3);
            wholeMatrix.setScore('Y', 'W', 4);
            wholeMatrix.setScore('Y', 'Y', 10);
            wholeMatrix.setScore('V', 'A', 0);
            wholeMatrix.setScore('V', 'R', -4);
            wholeMatrix.setScore('V', 'N', -4);
            wholeMatrix.setScore('V', 'D', -4);
            wholeMatrix.setScore('V', 'C', 1);
            wholeMatrix.setScore('V', 'Q', -3);
            wholeMatrix.setScore('V', 'E', -3);
            wholeMatrix.setScore('V', 'G', -5);
            wholeMatrix.setScore('V', 'H', -3);
            wholeMatrix.setScore('V', 'I', 4);
            wholeMatrix.setScore('V', 'L', 2);
            wholeMatrix.setScore('V', 'K', -3);
            wholeMatrix.setScore('V', 'M', 1);
            wholeMatrix.setScore('V', 'F', -1);
            wholeMatrix.setScore('V', 'P', -3);
            wholeMatrix.setScore('V', 'S', -2);
            wholeMatrix.setScore('V', 'T', 0);
            wholeMatrix.setScore('V', 'W', -5);
            wholeMatrix.setScore('V', 'Y', -3);
            wholeMatrix.setScore('V', 'V', 5);
            wholeMatrix.setScore('B', 'A', -1);
            wholeMatrix.setScore('B', 'R', -2);
            wholeMatrix.setScore('B', 'N', 5);
            wholeMatrix.setScore('B', 'D', 6);
            wholeMatrix.setScore('B', 'C', -4);
            wholeMatrix.setScore('B', 'Q', 0);
            wholeMatrix.setScore('B', 'E', 2);
            wholeMatrix.setScore('B', 'G', -1);
            wholeMatrix.setScore('B', 'H', 0);
            wholeMatrix.setScore('B', 'I', -5);
            wholeMatrix.setScore('B', 'L', -5);
            wholeMatrix.setScore('B', 'K', 0);
            wholeMatrix.setScore('B', 'M', -4);
            wholeMatrix.setScore('B', 'F', -6);
            wholeMatrix.setScore('B', 'P', -2);
            wholeMatrix.setScore('B', 'S', 1);
            wholeMatrix.setScore('B', 'T', 0);
            wholeMatrix.setScore('B', 'W', -6);
            wholeMatrix.setScore('B', 'Y', -3);
            wholeMatrix.setScore('B', 'V', -4);
            wholeMatrix.setScore('B', 'B', 5);
            wholeMatrix.setScore('Z', 'A', -1);
            wholeMatrix.setScore('Z', 'R', 0);
            wholeMatrix.setScore('Z', 'N', 0);
            wholeMatrix.setScore('Z', 'D', 3);
            wholeMatrix.setScore('Z', 'C', -5);
            wholeMatrix.setScore('Z', 'Q', 4);
            wholeMatrix.setScore('Z', 'E', 5);
            wholeMatrix.setScore('Z', 'G', -2);
            wholeMatrix.setScore('Z', 'H', 0);
            wholeMatrix.setScore('Z', 'I', -4);
            wholeMatrix.setScore('Z', 'L', -3);
            wholeMatrix.setScore('Z', 'K', 2);
            wholeMatrix.setScore('Z', 'M', -3);
            wholeMatrix.setScore('Z', 'F', -5);
            wholeMatrix.setScore('Z', 'P', -1);
            wholeMatrix.setScore('Z', 'S', 0);
            wholeMatrix.setScore('Z', 'T', -1);
            wholeMatrix.setScore('Z', 'W', -7);
            wholeMatrix.setScore('Z', 'Y', -4);
            wholeMatrix.setScore('Z', 'V', -3);
            wholeMatrix.setScore('Z', 'B', 2);
            wholeMatrix.setScore('Z', 'Z', 5);
            wholeMatrix.setScore('X', 'A', 0);
            wholeMatrix.setScore('X', 'R', 0);
            wholeMatrix.setScore('X', 'N', 0);
            wholeMatrix.setScore('X', 'D', 0);
            wholeMatrix.setScore('X', 'C', 0);
            wholeMatrix.setScore('X', 'Q', 0);
            wholeMatrix.setScore('X', 'E', 0);
            wholeMatrix.setScore('X', 'G', 0);
            wholeMatrix.setScore('X', 'H', 0);
            wholeMatrix.setScore('X', 'I', 0);
            wholeMatrix.setScore('X', 'L', 0);
            wholeMatrix.setScore('X', 'K', 0);
            wholeMatrix.setScore('X', 'M', 0);
            wholeMatrix.setScore('X', 'F', 0);
            wholeMatrix.setScore('X', 'P', 0);
            wholeMatrix.setScore('X', 'S', 0);
            wholeMatrix.setScore('X', 'T', 0);
            wholeMatrix.setScore('X', 'W', 0);
            wholeMatrix.setScore('X', 'Y', 0);
            wholeMatrix.setScore('X', 'V', 0);
            wholeMatrix.setScore('X', 'B', 0);
            wholeMatrix.setScore('X', 'Z', 0);
            wholeMatrix.setScore('X', 'X', 0);

            break;

         case SCOREMATRIX_PENALIZEMISMATCH:

            wholeMatrix = new ScoreHash();
            int i, j;
            char a, b;
            for (i = 0; i < m_alphabet.length(); ++i) {
               for (j = 0; j < i+1; ++j) {
                  //System.out.println("A: " + m_alphabet.charAt(j) +
                  //                   " - B: " + m_alphabet.charAt(i));

                  a = m_alphabet.charAt(j);
                  b = m_alphabet.charAt(i);

                  if (a == b) {
                     wholeMatrix.setScore(a, b, 1);
                  }
                  else {
                     wholeMatrix.setScore(a, b, -2);
                  }
               }
            }

            break;

      }

      this.fillCells(wholeMatrix);

   }

   protected void fillCells(ScoreHash sh) {
      //"GACT"
      //"GACT"

      int i, j;
      int count = 0;
      for (i = 0; i < m_alphabet.length(); ++i) {
         for (j = 0; j < i+1; ++j) {
            //System.out.println("A: " + m_alphabet.charAt(j) +
            //                   " - B: " + m_alphabet.charAt(i));
            m_jTxtValues[count++].setText(
                sh.getScore(m_alphabet.charAt(j),
                            m_alphabet.charAt(i)));
         }
      }

   }

   protected class ButtonListener
       implements ActionListener {

      public void actionPerformed(ActionEvent event) {
         // Perform the action indicated by a mouse click on a button.

         Object b = event.getSource(); // Get the component that was clicked.

         Object[] message = new Object[2];
         String title;
         JTextField stringField;

         String[] options;

         if (b == m_btnOk) {

            m_passedHash.clearScores();
            int i, j, val;
            int count = 0;
            for (i = 0; i < m_alphabet.length(); ++i) {
               for (j = 0; j < i+1; ++j) {
                  //System.out.println("A: " + m_alphabet.charAt(j) +
                  //                   " - B: " + m_alphabet.charAt(i) +
                  //                   " - Score: " + m_jTxtValues[count].getText());

                  try {
                     val = Integer.parseInt(m_jTxtValues[count].getText());
                  }
                  catch (NumberFormatException ex) {
                     val = 0;
                  }

                  m_passedHash.setScore(m_alphabet.charAt(j),
                                        m_alphabet.charAt(i),
                                        val);

                  count++;
               }
            }

            m_thisDialog.dispose();
         }
         else if (b == m_btnCancel) {
            m_thisDialog.dispose();
         }
      }
   }

   protected class ComboBoxListener
       implements ActionListener {

      public void actionPerformed(ActionEvent e) {

         // will be m_matrixSelection
         JComboBox cb = (JComboBox) e.getSource();

         switch (cb.getSelectedIndex()) {

            //String[] m_matrixData = {
            //    "Original", "Pam250", "Blosum62", "vtml160"};

            case 0:
               loadScoreMatrix(ScoreDialog.SCOREMATRIX_ORIGINAL);
               break;

            // PAM250
            case 1:
               loadScoreMatrix(ScoreDialog.SCOREMATRIX_PAM250);
               break;

            case 2:
               loadScoreMatrix(ScoreDialog.SCOREMATRIX_BLOSUM62);
               break;

            case 3:
               loadScoreMatrix(ScoreDialog.SCOREMATRIX_VTML160);
               break;

            case 4:
               loadScoreMatrix(ScoreDialog.SCOREMATRIX_PENALIZEMISMATCH);
               break;
         }

      }
   }

}
