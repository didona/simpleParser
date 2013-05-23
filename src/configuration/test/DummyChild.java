package configuration.test;

/**
 * // TODO: Document this
 *
 * @author diego
 * @since 4.0
 */
public class DummyChild {

   private long age;
   private String name;
   private DummyGrandChild dummyGrandChild;

   public long getAge() {
      return age;
   }

   public void setAge(long age) {
      this.age = age;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public DummyGrandChild getDummyGrandChild() {
      return dummyGrandChild;
   }

   public void setDummyGrandChild(DummyGrandChild dummyGrandChild) {
      this.dummyGrandChild = dummyGrandChild;
   }

   @Override
   public String toString() {
      return "DummyChild{" +
            "age=" + age +
            ", name='" + name + '\'' +
            ", dummyGrandChild=" + dummyGrandChild +
            '}';
   }
}
