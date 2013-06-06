package xmlParsing.test;

/**
 * // TODO: Document this
 *
 * @author diego
 * @since 4.0
 */
public class DummyOb {

   private DummyChild dummyChild;
   private DummyGrandChild dummyGrandChild;

   public DummyChild getDummyChild() {
      return dummyChild;
   }

   public void setDummyChild(DummyChild dummyChild) {
      this.dummyChild = dummyChild;
   }

   public DummyGrandChild getDummyGrandChild() {
      return dummyGrandChild;
   }

   public void setDummyGrandChild(DummyGrandChild dummyGrandChild) {
      this.dummyGrandChild = dummyGrandChild;
   }

   @Override
   public String toString() {
      return "DummyOb{" +
            "dummyChild=" + dummyChild +
            ", dummyGrandChild=" + dummyGrandChild +
            '}';
   }
}
