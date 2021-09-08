import model.Room;
import util.Announcer;
import util.InjectByType;
import util.Policeman;

public class CoronaDesinfector {

// 7+ responsebility
//  private utile.Announcer announcer = new utile.ConsoleAnnouncer();
//  private utile.Policeman policeman = new utile.PolicemanImpl();

  //using Factory singleton
  //look up - не подходит для Init tests, меняем на @Singleton + cache
//  private Announcer announcer = ObjectFactory.getInstance().createObject(Announcer.class);
//  private Policeman policeman =ObjectFactory.getInstance().createObject(Policeman.class);
  // - >  Инверсия контроля = Do not call us, We call you - мы будем сами создавать твои объекты и их настраивать

  //custom @
  @InjectByType // аналог  @Autowired
  private Announcer announcer;
  @InjectByType
  private Policeman policeman;

  public void start(Room room) {
    announcer.announce("Начинаем дезинфекцию! Всем покинуть комнаты!!!!!!");
    policeman.makePeopleLeaveRoom();
    desinfect(room);
    announcer.announce("Дезинфекция закончена. Быстро в комнаты!!!");
  }

  private void desinfect(Room room) {
    System.out.println("Корона изыди! Корона ушла!!!!!");

  }

}
