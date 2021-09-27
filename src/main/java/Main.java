import java.util.HashMap;
import java.util.Map;
import model.Room;
import util.AngryPoliceman;
import util.ApplicationContext;
import util.ApplicationRun;
import util.ObjectFactory;
import util.Policeman;
import util.PolicemanImpl;

public class Main {

  public static void main(String[] args) {
    //CoronaDesinfector desinfector = new CoronaDesinfector();
    // после реализации инверси контроля создаем объекты так
    //CoronaDesinfector desinfector = ObjectFactory.getInstance().createObject(CoronaDesinfector.class);
    ApplicationContext context = ApplicationRun
        .run("util", new HashMap<>(Map.of(Policeman.class, PolicemanImpl.class)));
    CoronaDesinfector desinfector = context.getObject(CoronaDesinfector.class);
    desinfector.start(new Room());
  }

}
