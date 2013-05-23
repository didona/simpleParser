package configuration.test;

/**
 * // TODO: Document this
 *
 * @author diego
 * @since 4.0
 */
public class DummyGrandChild {
   private String father;

   public String getFather() {
      return father;
   }

   public void setFather(String father) {
      this.father = father;
   }

   @Override
   public String toString() {
      return "DummyGrandChild{" +
            "father='" + father + '\'' +
            '}';
   }
}
