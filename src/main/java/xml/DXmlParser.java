package xml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Diego Didona, didona@gsd.inesc-id.pt Date: 11/10/12
 */

public class DXmlParser<O> {


   private static final Log log = LogFactory.getLog(DXmlParser.class);
   private static final String PACKAGE_SEPARATOR = "\\.";
   private static final String customSetterString = "setWith";


   static {
      //PropertyConfigurator.configure("conf/log4j.properties");
   }

   public final O parse(String fileName) {
      try {
         DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
         Document doc = docBuilder.parse(new File(fileName));
         Node root = doc.getFirstChild();
         return recursiveParse(root);
      } catch (Throwable t) {
         t.printStackTrace();
         System.exit(-1);
      }
      return null;   //unreachable
   }


   private O recursiveParse(Node root) {
      try {
         return (O) (recursiveParseElement(root).targetObject);
      } catch (Exception e) {
         e.printStackTrace();
         System.exit(-1);
      }
      return null; //unreachable statement
   }


   private ObjectAndSetter recursiveParseElement(Node element) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, ClassNotFoundException, InstantiationException {
      log.trace("Parsing " + element.getNodeName());
      ObjectAndSetter objectAndSetter = parseElementNode(element);
      Object thisObject = objectAndSetter.targetObject;
      NodeList childNodes = element.getChildNodes();
      int size = childNodes.getLength();
      //Base case
      if (size == 0) {
         return objectAndSetter;
      }
      //Class cls = Class.forName(this.packageName() + element.getNodeName());
      // Class cls = Class.forName(element.getNodeName());
      Class cls = ClassLoader.getSystemClassLoader().loadClass(element.getNodeName());
      for (int i = 0; i < size; i++) {
         Node elem = childNodes.item(i);
         if (elem.getNodeType() == Node.ELEMENT_NODE) {
            ObjectAndSetter oAs = recursiveParseElement(elem);
            java.lang.Object o = oAs.targetObject;
            if (oAs.setterName == null) {
               String nodeName = elem.getNodeName();
               this.invokeSet(thisObject, cls, nodeName, o);
            } else {
               this.invokeCustomSet(thisObject, cls, oAs.setterName, o);
            }
         }
      }
      return objectAndSetter;
   }

   private void typeAwareInvokeSet(java.lang.Object newInstance, Method m, java.lang.Object param, Class paramType) throws InvocationTargetException, IllegalAccessException {

      if (param.getClass() == String.class) {
         log.trace("Invoking method " + m.getName() + " with parameter of type " + paramType.getName() + " value " + param);
         String paramm = (String) param;
         if (paramType.getName().equals("int")) {
            m.invoke(newInstance, Integer.parseInt(paramm));
         } else if (paramType.getName().equals("double")) {
            m.invoke(newInstance, Double.parseDouble(paramm));
         } else if (paramType.getName().equals("long")) {
            m.invoke(newInstance, Long.parseLong(paramm));
         } else if (paramType.getName().equals("java.lang.String")) {
            m.invoke(newInstance, (String) (paramm));
         } else if (paramType.getName().equals("boolean")) {
            boolean b = param.equals("true");
            m.invoke(newInstance, b);
         }
      } else {
         log.trace("Invoking method " + m.getName() + " with parameter of class " + paramType.getName());
         m.invoke(newInstance, param);
      }
   }

   //This does not support nested Element  yet (I should only put the cycle I use to create the root xmlParsing in a recursive method)
   private ObjectAndSetter parseElementNode(Node element) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException, InstantiationException {

      //Class clazz = Class.forName(packageName() + element.getNodeName());
      Class clazz = Class.forName(element.getNodeName());
      Object newInstance = clazz.newInstance();
      String setWith = null;
      if (element.hasAttributes()) {
         for (int k = 0; k < element.getAttributes().getLength(); k++) {
            Node attribute = element.getAttributes().item(k);
            String nodeName = attribute.getNodeName();
            String param = attribute.getNodeValue();
            if (nodeName.equals(DXmlParser.customSetterString)) {
               setWith = param;
            } else {
               this.invokeSet(newInstance, clazz, nodeName, param);
            }
         }
      }

      return new ObjectAndSetter(newInstance, setWith);
   }

   private void invokeSet(Object o, Class clazz, String nodeName, Object param) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
      String simpleName = ObjectFromFullQualifiedName(nodeName);
      String nameMethod = "set" + simpleName.substring(0, 1).toUpperCase() + simpleName.substring(1);//nodeName.substring(0, 1).toUpperCase() + nodeName.substring(1);
      log.trace("Going to invoke " + nameMethod + " on Object of " + clazz);
      Class returnType = this.getClassMethodIfExists(clazz, nameMethod);
      if (returnType == null) {
         throw new NoSuchMethodException("Could not find method " + nameMethod);
      }
      Method m = clazz.getMethod(nameMethod, returnType);
      this.typeAwareInvokeSet(o, m, param, returnType);

   }

   private void invokeCustomSet(Object o, Class clazz, String customSet, Object param) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
      String nameMethod = "set" + customSet.substring(0, 1).toUpperCase() + customSet.substring(1);//nodeName.substring(0, 1).toUpperCase() + nodeName.substring(1);
      log.trace("Going to invoke " + nameMethod + " on Object of " + clazz);
      Class returnType = this.getClassMethodIfExists(clazz, nameMethod);
      if (returnType == null) {
         throw new NoSuchMethodException("Could not find method " + nameMethod);
      }
      Method m = clazz.getMethod(nameMethod, returnType);
      this.typeAwareInvokeSet(o, m, param, returnType);

   }

   private String ObjectFromFullQualifiedName(String s) {
      String ret[] = s.split(PACKAGE_SEPARATOR);
      return ret.length == 0 ? s : ret[ret.length - 1];
   }

   private Class getClassMethodIfExists(Class c, String method) {
      Method[] declared = c.getDeclaredMethods();
      for (Method aDeclared : declared) {
         if (aDeclared.getName().equals(method)) {
            Class[] array = aDeclared.getParameterTypes();
            if (array.length > 1) {
               throw new RuntimeException("Method " + method + " found, but it is not a one-parameter setter");
            } else
               return array[0];
         }
      }
      //Method not found or...it is in a superclass
      if (c == Object.class)
         return null;
      return getClassMethodIfExists(c.getSuperclass(), method);
   }


   private class ObjectAndSetter {
      private Object targetObject;
      private String setterName;

      public ObjectAndSetter(Object targetObject, String setterName) {
         this.targetObject = targetObject;
         this.setterName = setterName;
      }
   }


}
