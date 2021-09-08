package util;

//custom @
@Singleton
public class RecommenderImpl implements Recommender {

  //custom @
  @InjectProperty("wine")
  private String drink;


  @Override
  public void recommend() {
    System.out.println("to protect from virus, drink " + drink);

  }
}
