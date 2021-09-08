package util;

import java.lang.reflect.Field;
import lombok.SneakyThrows;

public class InjectByTypeAnnotationObjectConfigurator implements ObjectConfigurator {

  @Override
  @SneakyThrows
  public void configure(Object t, ApplicationContext context) {
    for (Field field : t.getClass().getDeclaredFields()) {
      if (field.isAnnotationPresent(InjectByType.class)){
        field.setAccessible(true);
        //Object object = ObjectFactory.getInstance().createObject(field.getType());
        Object object = context.getObject(field.getType());
           // ObjectFactory.getInstance().createObject(field.getType());
        field.set(t, object);
      }
    }
    ;

  }
}
