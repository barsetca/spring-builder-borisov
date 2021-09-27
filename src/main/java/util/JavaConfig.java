package util;

import java.util.Map;
import java.util.Set;
import lombok.Getter;
import org.reflections.Reflections;

public class JavaConfig implements Config {

  @Getter // эта @ по сути делает @Override метода интерфейса Reflections getScanner();
  private Reflections scanner;
  private Map<Class, Class> ifc2ImplClass;

  public JavaConfig(String packageToScan, Map<Class, Class> ifc2ImplClass ) {
    this.scanner = new Reflections(packageToScan);
    this.ifc2ImplClass = ifc2ImplClass;
  }

  @Override
  public <T> Class<? extends T> getImplClass(Class<T> interfc) {
    return  ifc2ImplClass.computeIfAbsent(interfc, aClass -> {
      Set<Class<? extends T>> classes = scanner.getSubTypesOf(interfc);
      if (classes.size() != 1){
        throw new RuntimeException(interfc + " has 0 or more than one impl please update your config");
      }
      return classes.iterator().next();
    });
    }
}
