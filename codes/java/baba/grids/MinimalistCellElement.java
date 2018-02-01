package norman.baba.grids;

/**
 * <p>Title: BABA</p>
 * <p>Description: Bioinformatique Research Project</p>
 * <p>Copyright: Copyright Norman Casagrande (c) 2003</p>
 * <p>Company: </p>
 * @author Norman Casagrande
 * @version 1.0
 */

public class MinimalistCellElement {

   public byte column = -1;
   public byte row = -1;

   public byte value = 0;

   // I don't use a list because it would also bring a lot of complexity
   // to handle it. There are just three pointers: i can check them all
   public MinimalistCellElement pLeft = null;
   public MinimalistCellElement pTop = null;
   public MinimalistCellElement pTopLeft = null;

   public MinimalistCellElement(byte column, byte row, byte value) {
      this.column = column;
      this.row = row;
      this.value = value;
   }

}