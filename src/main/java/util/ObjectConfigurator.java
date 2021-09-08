package util;

 // in Spring => BeanPostProcessor
public interface ObjectConfigurator {

  void configure(Object t, ApplicationContext context);
}
