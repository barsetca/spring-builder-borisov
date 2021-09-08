package util;

import java.util.Map;

public class ApplicationRun {

  public static ApplicationContext run(String packageToScan, Map<Class, Class> ifc2ImplClass){
    JavaConfig config = new JavaConfig(packageToScan, ifc2ImplClass);
    ApplicationContext context = new ApplicationContext(config);
    ObjectFactory factory = new ObjectFactory(context);
    // todo - init all singletons which are not lazy
    context.setFactory(factory);
    return context;

  }

}
