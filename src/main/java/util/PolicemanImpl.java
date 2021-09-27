package util;

import javax.annotation.PostConstruct;

public class PolicemanImpl implements Policeman {

  @InjectByType
  private Recommender recommender;

  //конструктор не работает т.к. инжектятся поля, которые еще не готовы на момент отработки конструктора
  // по этому надо использовать метод init() - после того как объект настроенБ а не создан
//  public PolicemanImpl() {
//    System.out.println(recommender.getClass());
//  }
  // но init сам по себе не запустится, по этому надо использовать концепт SecondFaceConstructor
  // для этого исп-ся @PostConstruct и для того чтобы она заработала будем использовать
  // ObjectFactory, где в методе createObject после конфигурации объекта (configure()) запускаем метод
  //помеченный @PostConstruct у объекта если он у него есть.

  @PostConstruct
 public void init(){
    System.out.println(recommender.getClass());
  }

  @Override
  public void makePeopleLeaveRoom() {
    System.out.println("Быстро покинуть комнаты! У меня дубинка!!!");
  }
}
