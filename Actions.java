package mealplanner;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class Actions {
    private MealPlannerDB db;
    private static Scanner scanner;
    private ArrayList<Meal> listOfMeal;
    private final Checker checker;
    private final String[] availableCategory;

    public Actions(MealPlannerDB db){
        scanner=new Scanner(System.in);
        listOfMeal=new ArrayList<>();
        checker=new Checker();
        availableCategory= new String[]{"breakfast", "lunch", "dinner"};
        this.db=db;
    }
    public void addMeal() {
        String category,name,ingredients;
        String[] ingredientsArray;
        System.out.println("Which meal do you want to add (breakfast, lunch, dinner)?");
        do {
            category = scanner.nextLine();
        } while (!checker.availableCategory(category,availableCategory));
        System.out.println("Input the meal's name:");
        do{
            name = scanner.nextLine();
        }while (!checker.availableInput(new String[]{name}));
        System.out.println("Input the ingredients:");
        do{
            ingredients = scanner.nextLine();
            ingredientsArray=ingredients.split(",");
            for(int i=1;i<ingredientsArray.length;i++){
                if(ingredientsArray[i].startsWith(" ")){
                    ingredientsArray[i]=ingredientsArray[i].substring(1);
                }
            }
        }while (!checker.availableInput(ingredientsArray));
        Meal meal = new Meal(category, name, ingredientsArray);
        listOfMeal.add(meal);
        db.addMeal(category,name);
        db.addIngredients(ingredients);
        System.out.println("The meal has been added!");
    }
    public void printMeal(){
        String category;
        System.out.println("Which category do you want to print (breakfast, lunch, dinner)?");
        do {
            category = scanner.nextLine();
        } while (!checker.availableCategory(category,availableCategory));
        listOfMeal=db.importMeals(category);
        if(listOfMeal.isEmpty()){
            System.out.println("No meals found.");
        }else {
            System.out.printf("Category: %s%n%n",category);
            for(Meal meal:listOfMeal){
                meal.printInfoAboutMeal();
            }
        }
    }
    public void save(){
        if(db.isEmpty("ingredients")){
            System.out.println("Unable to save. Plan your meals first.");
        }else {
            System.out.println("Input a filename:");
            String urlFile=scanner.nextLine();
            try{
                FileWriter myWriter = new FileWriter(urlFile);
                ArrayList<String> listOfIngredient=db.importIngredientsForSave();
                for(int i=0;i<listOfIngredient.size();i++){
                    int count=1;
                    for(int j=i+1;j<listOfIngredient.size();j++){
                        if(listOfIngredient.get(i).equals(listOfIngredient.get(j))){
                            count++;
                            listOfIngredient.remove(j);
                        }
                    }
                    if(count==1){
                        myWriter.write(String.format("%s\n",listOfIngredient.get(i)));
                    }else {
                        myWriter.write(String.format("%s x%s\n",listOfIngredient.get(i),count));
                    }
                }
                myWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Saved!");
        }
    }
    public void addPlan(){
        String[] dayOfWeak={"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
        String inputMeal;
        ArrayList<String> nameOfMeal;
        db.deletePlan();
        for(String day:dayOfWeak){
            System.out.println("\n"+day);
            for(String category:availableCategory){
                nameOfMeal=db.MealIngredient(category);
                for(String meal:nameOfMeal){
                    System.out.println(meal);
                }
                System.out.printf("Choose the %s for %s from the list above:%n",category,day);
                while (true){
                    inputMeal=scanner.nextLine();
                    if(nameOfMeal.contains(inputMeal))break;
                    System.out.println("This meal doesn’t exist. Choose a meal from the list above.");
                }
                db.addPlan(day,category,inputMeal);
            }
            System.out.printf("Yeah! We planned the meals for %s.",day);
        }
        db.printPlan(dayOfWeak,availableCategory);
    }
}
