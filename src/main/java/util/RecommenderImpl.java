package util;

//custom @
@Singleton
@Deprecated
public class RecommenderImpl implements Recommender {

  //custom @
  //аналог @Value в Spring
  @InjectProperty("wine")
  private String drink;

  public RecommenderImpl() {
    System.out.println("Recommender was crated");
  }

  @Override
  public void recommend() {
    System.out.println("to protect from virus, drink " + drink);

  }
}
