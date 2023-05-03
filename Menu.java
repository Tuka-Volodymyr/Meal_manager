package mealplanner;
import java.util.Scanner;

public class Menu {
    private MealPlannerDB db;
    private static final Scanner scanner=new Scanner(System.in);
    private final Actions actions;
    public Menu(MealPlannerDB db){
        this.db=db;
        actions=new Actions(db);
    }
    public void generalMenu(){
        boolean exit=true;
        while (exit){
            System.out.println("What would you like to do (add, show, plan, save, exit)?");
            String doing=scanner.nextLine();
            switch (doing){
                case "add"->actions.addMeal();
                case "show"->actions.printMeal();
                case "plan"->actions.addPlan();
                case "save"->actions.save();
                case "exit"->{
                    System.out.println("Bye!");
                    exit=false;
                }
            }
        }
    }
}
