package util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.SneakyThrows;

public class ObjectFactory {

  // private static ObjectFactory ourInstance = new ObjectFactory();
  // вместо предыдущей строчки используем раннер ApplicationRun

  private final ApplicationContext context;
//  private Config config = new JavaConfig("util");
  //private Config config;

  private List<ObjectConfigurator> configurators = new ArrayList<>();
  private List<ProxyConfigurator> proxyConfigurators = new ArrayList<>();

//  public static ObjectFactory getInstance() {
//    return ourInstance;
//  }

  @SneakyThrows
//  private ObjectFactory() {
  public ObjectFactory(ApplicationContext context) {
    this.context = context;
    // config = new JavaConfig("util", new HashMap<>(Map.of(Policeman.class, AngryPoliceman.class)));
    //теперь конфиг создаем в раннере ApplicationRun
    for (Class<? extends ObjectConfigurator> aClass : context.getConfig().getScanner()
        .getSubTypesOf(ObjectConfigurator.class)) {
      configurators.add(aClass.getDeclaredConstructor().newInstance());
    }

    for (Class<? extends ProxyConfigurator> aClass : context.getConfig().getScanner()
        .getSubTypesOf(ProxyConfigurator.class)) {
      proxyConfigurators.add(aClass.getDeclaredConstructor().newInstance());
    }


  }

  // in Spring -> getBean
  @SneakyThrows
  public <T> T createObject(Class<T> implClass) {
/*
Перенесли в контекст
    Class<? extends T> implClass = implClass;

    if (implClass.isInterface()){
      implClass = config.getImplClass(implClass);
    }
 */

    //T t = implClass.getDeclaredConstructor().newInstance();
    T t = create(implClass);
    // здесь можем настроить объект t  как мы хотим, для примера

//    for (Field field : implClass.getDeclaredFields()) {
//      InjectProperty annotation = field.getAnnotation(InjectProperty.class);
//      String path = ClassLoader.getSystemClassLoader().getResource("application.properties").getPath();
//      Stream<String> lines = new BufferedReader(new FileReader(path)).lines();
//      Map<String, String> propertiesMap = lines.map(line -> line.split("="))
//          .collect(toMap(arr -> arr[0], arr -> arr[1]));
//      if (annotation != null) {
//        String value = annotation.value().isEmpty() ? propertiesMap.get(field.getName())
//            : propertiesMap.get(annotation.value());
//        field.setAccessible(true);
//        field.set(t, value);
//      }
//    }
    // -> перенесен в отдельный класс public class InjectPropertyAnnotationObjectConfiguratorImpl implements
    // так как если здесь нарушается принципы Single Responsibility и Open Close of SOLID

    //analog pattern ChainOfResponsibility (many Handlers = objectConfigurator)

    // configurators.forEach(objectConfigurator -> objectConfigurator.configure(t));
    configure(t);

    invokeInit(implClass, t);

    t = wrapWithProxyIfNeeded(implClass, t);

    return t;

  }

  private <T> T wrapWithProxyIfNeeded(Class<T> implClass, T t) {
    for (ProxyConfigurator configurator : proxyConfigurators) {
      t = (T) configurator.replaceWithProxyIfNeeded(t, implClass);
    }
    return t;
  }

  private <T> void invokeInit(Class<T> type, T t)
      throws IllegalAccessException, InvocationTargetException {
    for (Method method : type.getMethods()) {
      if (method.isAnnotationPresent(PostConstruct.class)){
        method.invoke(t);
      }
    }
  }

  private <T> void configure(T t) {
    configurators.forEach(objectConfigurator -> objectConfigurator.configure(t, context));
  }

  private <T> T create(Class<T> type)
      throws InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException, NoSuchMethodException {
    return type.getDeclaredConstructor().newInstance();
  }
}
