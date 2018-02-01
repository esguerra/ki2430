package norman.baba.UI;

import java.awt.event.*;
import javax.swing.*;

/**
 * <p>Title: BABA</p>
 * <p>Description: Bioinformatique Research Project</p>
 * <p>Copyright: Copyright Norman Casagrande (c) 2003</p>
 * @author Norman Casagrande
 * @version 1.0
 */

/** JTextField Uppercase */
public class JTextFieldUC
    extends JTextField {

   public JTextFieldUC(String s, int col) {
      super(s, col);

      //enableEvents (AWTEvent.ACTION_EVENT_MASK);
      //enableEvents (AWTEvent.KEY_EVENT_MASK);
   }

   public JTextFieldUC() {
      this( (String)null, 0);
   }

   public JTextFieldUC(String s) {
      this(s, s.length());
   }

   public JTextFieldUC(int col) {
      this( (String)null, col);
   }

   protected void processKeyEvent(KeyEvent e) {

      char ch = e.getKeyChar();
      //System.out.println("Ch: " + ch);

      if (Character.isLetter(ch)) {
         e.setKeyChar(Character.toUpperCase(ch));
      }

//      e.setKeyChar(Character.toUpperCase(e.getKeyChar()));
      super.processKeyEvent(e);
   }
}
