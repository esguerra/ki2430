package norman.baba.UI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import java.util.*;
import java.text.*;

// DEBUG ONLY
import java.io.*;

import norman.baba.utils.*;
import norman.baba.grids.*;
//import norman.baba.swingUtilities.*;
import norman.baba.algorithms.*;

/**
 * <p>Title: BABA</p>
 * <p>Description: Bioinformatique Research Project</p>
 * <p>Copyright: Copyright Norman Casagrande (c) 2003</p>
 * <p>Company: </p>
 * @author Norman Casagrande
 * @version 1.0
 */

public class PreprocessingFrame
    extends JFrame {

   protected int MSEC_TIME = 125;

   protected JLabel m_infoLabel = new JLabel();
   protected JProgressBar m_progressBar;

   protected int m_t = 0;
   protected Hashtable m_preprocTable;
   protected ArrayList m_ordKeys;
   protected FourRussians m_owner;

   // Explicit because also java.utils has Timer
   protected javax.swing.Timer m_timer;
   protected int m_pbarValue = 1;

   protected int m_encodedAlphSize = 1;

   protected int m_index = 0;
   protected boolean m_stopped = false;

   public PreprocessingFrame(Hashtable pHt, ArrayList lOrdKeys,
                             int t,
                             int encodedAlphSize,
                             FourRussians owner) {

      m_preprocTable = pHt;
      m_ordKeys = lOrdKeys;
      m_encodedAlphSize = encodedAlphSize;

      m_t = t;
      m_owner = owner;

      try {
         jbInit();
         this.pack();
         this.setDialogPosition();
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }

   protected void jbInit() throws Exception {

      m_progressBar = new JProgressBar();
      m_progressBar.setStringPainted(true);

      JPanel basePanel = (JPanel)this.getContentPane();

      Border insetsBorder;
      insetsBorder = BorderFactory.createEmptyBorder(5, 10, 5, 10);
      basePanel.setBorder(insetsBorder);

      m_infoLabel.setHorizontalAlignment(SwingConstants.CENTER);

      basePanel.setLayout(new GridLayout(2, 1));
      basePanel.add(m_infoLabel);
      basePanel.add(m_progressBar);

      this.setTitle("Doin' the dirty job..");

   }

   protected void setDialogPosition() {

      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      this.setSize(this.getWidth() * 2, this.getHeight());
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

   public void show() {
      //Create a timer.
      m_timer = new javax.swing.Timer(MSEC_TIME, new ActionListener() {
         public void actionPerformed(ActionEvent evt) {
            m_progressBar.setValue(m_pbarValue);

            if (!m_stopped) {
               toFront();
            }
         }
      });

      super.show();
      this.toFront();
      this.m_timer.start();
      this.go();
   }

   protected void go() {
      final SwingWorker worker = new SwingWorker() {
		@Override
		protected Object doInBackground() throws Exception {
			return new Preprocessing();
		}
      };
      worker.execute();
   }

   protected class Preprocessing {

      public Preprocessing() {

         try {

            NumberFormat nf = NumberFormat.getInstance();
            m_index = 0;

            /////////// Deactivate buttons /////////
            m_owner.m_btnClear.setEnabled(false);
            m_owner.m_btnNext.setEnabled(false);
            m_owner.m_btnEnd.setEnabled(false);
            m_owner.setStringsButtonEnabled(false);
            ////////////////////////////////////////

            m_owner.setInfoMessage("Preprocessing..");

            byte[] stringsEncodedValues = new byte[m_encodedAlphSize];
            for (byte i = 0; i < m_encodedAlphSize; ++i) {
               stringsEncodedValues[i] = i;
            }

            byte offsetValues[] = {
                -1, 0, 1};

            LinkedList alphComb = permuteArray(stringsEncodedValues,
                                               m_t - 1);
            LinkedList offsetComb = permuteArray(offsetValues, m_t - 1);

            ListIterator D_s1_lIt = alphComb.listIterator();
            ListIterator E_s2_lIt;

            ListIterator B_offs1_lIt;
            ListIterator C_offs2_lIt;

            byte[] D_s1;
            byte[] E_s2;

            byte[] B_offs1;
            byte[] C_offs2;

            // it should be:
            // stringsEncodedValues^(m_t-1)^2 * offsetValues^(m_t-1)^2
            // but already do have the permutations!
            int tot = alphComb.size() * alphComb.size() *
                offsetComb.size() * offsetComb.size();

            m_progressBar.setMinimum(0);
            m_progressBar.setMaximum(tot);
            m_pbarValue = 0;

            m_infoLabel.setText("Total combinations: " + nf.format(tot));
            Runtime.getRuntime().gc();

            while (D_s1_lIt.hasNext()) {

               D_s1 = (byte[]) D_s1_lIt.next();
               E_s2_lIt = alphComb.listIterator();

               //System.out.println("-> " + D_s1[0] + D_s1[1] + D_s1[2] + D_s1[3]);

               // ================> Loop on D_s1_lIt..\n;

               while (E_s2_lIt.hasNext()) {

                  E_s2 = (byte[]) E_s2_lIt.next();
                  B_offs1_lIt = offsetComb.listIterator();

                  // ==========> Loop on E_s2_lIt..

                  while (B_offs1_lIt.hasNext()) {

                     B_offs1 = (byte[]) B_offs1_lIt.next();
                     C_offs2_lIt = offsetComb.listIterator();

                     // ====> Loop on B_offs1_lIt..\n");

                     while (C_offs2_lIt.hasNext()) {

                        C_offs2 = (byte[]) C_offs2_lIt.next();
                        this.computeDPMatrix(m_preprocTable, D_s1, E_s2,
                                             B_offs1,
                                             C_offs2);

                        m_pbarValue++;

                     }
                  }

               }

            }

            m_stopped = true;
            JOptionPane.showMessageDialog(null,
                                          "Total Preprocessed Blocs: " +
                                          nf.format(m_preprocTable.size()),
                                          "Done!",
                                          JOptionPane.INFORMATION_MESSAGE);

            m_owner.m_btnEnd.setEnabled(true);
         }
         catch (java.lang.OutOfMemoryError e) {
            m_preprocTable = null;
            m_ordKeys = null;
            Runtime.getRuntime().gc();
            m_stopped = true;
            JOptionPane.showMessageDialog(null,
                                          "Out of memory!\n(Please give " +
                                          "a second to the garbage collector " +
                                          "to clean this mess..).\n" +
                                          "Note: Il could be unstable anyway",
                                          "Didn't I tell you? :)",
                                          JOptionPane.ERROR_MESSAGE);

            Runtime.getRuntime().gc();
            Runtime.getRuntime().gc();

            m_owner.m_currentPhase = FourRussians.PHASE_PREPROCESSING;
         }
         finally {

            m_owner.setInfoMessage("Waiting");
            /////////// Reactivate buttons /////////
            m_owner.m_btnClear.setEnabled(true);
            m_owner.m_btnNext.setEnabled(true);
            m_owner.setStringsButtonEnabled(true);

            m_owner.m_btnShowPreproc.setEnabled(true);
            ////////////////////////////////////////

            dispose();

         }

      }

      // NOTE: s1 = string vertical   (= rows)
      //       s2 = string horizontal (= cols)
      protected void computeDPMatrix(Hashtable ht,
                                     byte[] D_s1, byte[] E_s2,
                                     byte[] B_offs1, byte[] C_offs2) throws
          java.lang.OutOfMemoryError {

         byte i;
         MinimalistMatrix dpMatrix = new MinimalistMatrix(m_t, m_index);

         byte CVal = 0;
         byte BVal = 0;

         // Index: dpMatrix[columns][rows]

         // upper left is always 0
         dpMatrix.mat[0][0] = new MinimalistCellElement( (byte) 0, (byte) 0,
             (byte) 0);

         for (i = 1; i < m_t; ++i) {

            // rows - first column
            CVal += C_offs2[i - 1];
            dpMatrix.mat[0][i] = new MinimalistCellElement( (byte) 0, i, CVal);

            // columns - first row
            BVal += B_offs1[i - 1];
            dpMatrix.mat[i][0] = new MinimalistCellElement(i, (byte) 0, BVal);

         }

         // Calculating Dynamic Programming
         byte c, r;
         byte minVal;
         byte leftVal, topVal, topLeftVal;

         for (r = 1; r < m_t; ++r) {
            for (c = 1; c < m_t; ++c) {

               minVal = Byte.MAX_VALUE;

               leftVal = (byte) (dpMatrix.mat[c - 1][r].value + 1);
               topVal = (byte) (dpMatrix.mat[c][r - 1].value + 1);

               if (D_s1[r - 1] == E_s2[c - 1]) {
                  topLeftVal = dpMatrix.mat[c - 1][r - 1].value;
               }
               else {
                  topLeftVal = (byte) (dpMatrix.mat[c - 1][r - 1].value + 1);
               }

               minVal = triMin(leftVal, topVal, topLeftVal);
               dpMatrix.mat[c][r] = new MinimalistCellElement(c, r, minVal);

               if (minVal == leftVal) {
                  dpMatrix.mat[c][r].pLeft = dpMatrix.mat[c - 1][r];
               }
               if (minVal == topVal) {
                  dpMatrix.mat[c][r].pTop = dpMatrix.mat[c][r - 1];
               }
               if (minVal == topLeftVal) {
                  dpMatrix.mat[c][r].pTopLeft = dpMatrix.mat[c - 1][r - 1];
               }
            }
         }

         // put the matrix in the hashtable
         RBFParams key = new RBFParams(D_s1, E_s2, B_offs1, C_offs2);

         // This index will be the same as OrdKeys index.
         // i need it to show the user the "index" of the choosen bloc
         m_index++;

         // Each 3x3 matrix is ~424 bytes
         // Each 4x4 matrix is ~656 bytes (for 2985984 elements with an alphabet)
         ht.put(key, dpMatrix);
         m_ordKeys.add(key);
      }

//////////////////////
      protected byte triMin(byte a, byte b, byte c) {

         if (a < b) {
            if (a < c) {
               return a;
            }
            else {
               return c;
            }
         }
         else {
            if (b < c) {
               return b;
            }
            else {
               return c;
            }
         }
      }

      /////////////////////////////////////////////////////////////////////////
      // Permute offset
      /////////////////////////////////////////////////////////////////////////

      protected LinkedList permuteArray(byte array[], int size) {

         LinkedList retList = new LinkedList();

         int i, j;
         String tmpStr;

         byte buildArray[] = new byte[size];

         for (i = 0; i < array.length; ++i) {
            recursivePerm(retList, buildArray, 0,
                          array[i], array);
         }

         return (retList);
      }

      protected void recursivePerm(LinkedList list, byte[] buildingArray,
                                   int index, byte ch, byte array[]) {

         int i;
         buildingArray[index] = ch;

         if (index >= buildingArray.length - 1) {
            byte tmpArray[] = new byte[buildingArray.length];

//         System.out.print("-> ");
            for (i = 0; i < buildingArray.length; ++i) {
               tmpArray[i] = buildingArray[i];

//            System.out.print(tmpArray[i]);
            }
//         System.out.println();

            list.add(tmpArray);
            return;
         }

         ++index;
         for (i = 0; i < array.length; ++i) {

            recursivePerm(list, buildingArray, index,
                          array[i], array);

         }

      }

   }

}