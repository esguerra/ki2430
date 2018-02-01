package norman.baba.UI;

import javax.swing.JTextField;
import java.awt.event.*;

/**
 * <p>Title: BABA</p>
 * <p>Description: Bioinformatique Research Project</p>
 * <p>Copyright: Copyright Norman Casagrande (c) 2003</p>
 * <p>Company: </p>
 * @author Norman Casagrande
 * @version 1.0
 */

public class JTextFieldNumberOnly
    extends JTextField {

   public JTextFieldNumberOnly(String s, int col) {
      super(s, col);

      //enableEvents (AWTEvent.ACTION_EVENT_MASK);
      //enableEvents (AWTEvent.KEY_EVENT_MASK);
   }

   public JTextFieldNumberOnly() {
      this( (String)null, 0);
   }

   public JTextFieldNumberOnly(String s) {
      this(s, s.length());
   }

   public JTextFieldNumberOnly(int col) {
      this( (String)null, col);
   }

   protected void processKeyEvent(KeyEvent e) {
      char ch = e.getKeyChar();

      switch (Character.getType(ch)) {
         case Character.DECIMAL_DIGIT_NUMBER:
         case Character.CONTROL:
         case Character.UNASSIGNED:

            super.processKeyEvent(e);
      }

   }
}