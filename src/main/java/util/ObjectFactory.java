package util;

import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;

public class ObjectFactory {

  // private static ObjectFactory ourInstance = new ObjectFactory();
  // вместо предыдущей строчки используем раннер

  private final ApplicationContext context;
//  private Config config = new JavaConfig("util");
  //private Config config;

  private List<ObjectConfigurator> configurators = new ArrayList<>();

//  public static ObjectFactory getInstance() {
//    return ourInstance;
//  }

  @SneakyThrows
//  private ObjectFactory() {
  public ObjectFactory(ApplicationContext context) {
    this.context = context;
    // config = new JavaConfig("util", new HashMap<>(Map.of(Policeman.class, AngryPoliceman.class)));
    for (Class<? extends ObjectConfigurator> aClass : context.getConfig().getScanner()
        .getSubTypesOf(ObjectConfigurator.class)) {
      configurators.add(aClass.getDeclaredConstructor().newInstance());
    }


  }

  // in Spring -> getBean
  @SneakyThrows
  public <T> T createObject(Class<T> type) {
/*
Пернесли в контекст
    Class<? extends T> implClass = type;

    if (type.isInterface()){
      implClass = config.getImplClass(type);
    }
 */

    //T t = implClass.getDeclaredConstructor().newInstance();
    T t = type.getDeclaredConstructor().newInstance();
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
    configurators.forEach(objectConfigurator -> objectConfigurator.configure(t, context));

    return t;

  }
}
