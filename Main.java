package mealplanner;
public class Main {
  public static void main(String[] args){
    MealPlannerDB db = new MealPlannerDB();
    Menu menu=new Menu(db);
    menu.generalMenu();
  }
}