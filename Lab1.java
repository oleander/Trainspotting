import TSim.*;

public class Lab1 {

  public static void main(String[] a) {
    new Lab1();
  }

  public Lab1() {
    TSimInterface inter = TSimInterface.getInstance();

    try {
      inter.setSpeed(1,10);
    }
    catch (CommandException e) {
      e.printStackTrace();    // or only e.getMessage() for the error
      System.exit(1); // arash rocks
    }
  }
}

