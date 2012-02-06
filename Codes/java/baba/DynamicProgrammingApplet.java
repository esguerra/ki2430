package norman.baba;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.applet.*;

import norman.baba.algorithms.*;

/**
 * <p>Title: BABA</p>
 * <p>Description: Bioinformatique Research Project</p>
 * <p>Copyright: Copyright Norman Casagrande (c) 2003</p>
 * @author Norman Casagrande
 * @version 1.0
 */

public class DynamicProgrammingApplet
    extends Applet {

   private JButton m_btnSimpleDP = new JButton("Simple DP");
   private JButton m_btnNW = new JButton("Need.&Wunsch");
   private JButton m_btnSW = new JButton("Smith&Waterm.");
   private JButton m_btnFourRussians = new JButton("Four Russians");
   private JButton m_btnNussinov = new JButton("Nussinov");

   //Construct the application
   public DynamicProgrammingApplet() {
   }

   private void startNew(JFrame frame) {

      frame.setSize(new Dimension(700, 550));
      frame.validate();

      //Center the window
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension frameSize = frame.getSize();
      if (frameSize.height > screenSize.height) {
         frameSize.height = screenSize.height;
      }
      if (frameSize.width > screenSize.width) {
         frameSize.width = screenSize.width;
      }
      frame.setLocation( (screenSize.width - frameSize.width) / 2,
                        (screenSize.height - frameSize.height) / 2);
      frame.setVisible(true);

   }

   //Main method
   public void init() {
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
      catch (Exception e) {
         e.printStackTrace();
      }

      this.add(m_btnSimpleDP);
      this.add(m_btnNW);
      this.add(m_btnSW);
      this.add(m_btnFourRussians);
      this.add(m_btnNussinov);

      m_btnSimpleDP.addActionListener(new AlgoButtonListener());
      m_btnNW.addActionListener(new AlgoButtonListener());
      m_btnSW.addActionListener(new AlgoButtonListener());
      m_btnFourRussians.addActionListener(new AlgoButtonListener());
      m_btnNussinov.addActionListener(new AlgoButtonListener());

   }

   protected class AlgoButtonListener
       implements ActionListener {

      public void actionPerformed(ActionEvent event) {

         JButton selButton = (JButton)event.getSource();

         JFrame frame = new JFrame();
         JPanel contentPane = (JPanel)frame.getContentPane();
         SimpleDP algo = null;

         if (selButton == m_btnSimpleDP) {
            algo = new SimpleDP(contentPane, "GTACCT", "GGTGT");
         }
         else if (selButton == m_btnNW) {
            algo = new NeedlemanWunsch(contentPane, "HEAGAWGHEE", "PAWHEAE");
         }
         else if (selButton == m_btnSW) {
            algo = new SmithWaterman(contentPane, "HEAGAWGHEE", "PAWHEAE");
         }
         else if (selButton == m_btnFourRussians) {
            algo = new FourRussians(contentPane, "ATGTCA", "ATTAGTCA");
         }
         else if (selButton == m_btnNussinov) {
             algo = new Nussinov(contentPane,"GGGAAAUCC");
         }

         frame.setTitle("BABA: " + algo.getAlgorithmName());
         startNew(frame);
      }
   }
}
