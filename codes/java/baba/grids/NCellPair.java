package norman.baba.grids;

import java.util.Vector;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class NCellPair {

          private CellElement CellOne;
          private CellElement CellTwo;

          public NCellPair(CellElement e1,CellElement e2) {
              CellOne = e1;
              CellTwo = e2;
          }

          public Vector getCellPair() {
              Vector tmp = new Vector();
              tmp.add(CellOne);
              tmp.add(CellTwo);
              return tmp;
          }

          public CellElement getCellOne() {
              return CellOne;
          }

          public CellElement getCellTwo() {
              return CellTwo;
          }



    }
