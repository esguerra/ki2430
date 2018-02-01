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

public class JTextFieldGaps extends JTextField {

   public JTextFieldGaps(String s, int col) {
      super(s, col);
   }

   public JTextFieldGaps() {
      this( (String)null, 0);
   }

   public JTextFieldGaps(String s) {
      this(s, s.length());
   }

   public JTextFieldGaps(int col) {
      this( (String)null, col);
   }

   protected void processKeyEvent(KeyEvent e) {
      char ch = e.getKeyChar();

      switch (Character.getType(ch)) {
         case Character.DECIMAL_DIGIT_NUMBER:
         case Character.CONTROL:
         case Character.UNASSIGNED:
         case Character.OTHER_PUNCTUATION:
         case Character.DASH_PUNCTUATION:
         case Character.START_PUNCTUATION:
         case Character.END_PUNCTUATION:

            super.processKeyEvent(e);
      }

   }
}