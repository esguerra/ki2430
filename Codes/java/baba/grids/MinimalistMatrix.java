package norman.baba.grids;

/**
 * <p>Title: BABA</p>
 * <p>Description: Bioinformatique Research Project</p>
 * <p>Copyright: Copyright Norman Casagrande (c) 2003</p>
 * <p>Company: </p>
 * @author Norman Casagrande
 * @version 1.0
 */

public class MinimalistMatrix {

   public MinimalistCellElement[][] mat;
   public int index = 0;

   public MinimalistMatrix(int t, int index) {
      this.mat = new MinimalistCellElement[t][t];
      this.index = index;
   }

}