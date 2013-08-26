package simple;

import xmlParsing.xml.DXmlParser;

/**
 * // TODO: Document this
 *
 * @author diego
 * @since 4.0
 */
public class ConfigBuilderTest {


   public static void main(String[] args) throws Exception{
      String fileName = "/Users/diego/Desktop/conf.xml";
      DummyOb ob = (DummyOb)new DXmlParser().parse(fileName);
      System.out.println(ob);
   }
}
