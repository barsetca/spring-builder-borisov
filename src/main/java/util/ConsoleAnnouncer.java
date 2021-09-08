package util;

public class ConsoleAnnouncer implements Announcer {

  @InjectByType
  private Recommender recommender;

  @Override()
  public void announce(String message) {
    System.out.println(message);
    recommender.recommend();
  }
}
