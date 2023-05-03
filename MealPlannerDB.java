package mealplanner;
import java.sql.*;
import java.util.ArrayList;
public class MealPlannerDB {
    private final String DB_URL = "jdbc:postgresql:meals_db";
    private final String USER = "postgres";
    private final String PASS = "1111";
    private int meal_id = 1;
    private Connection connection;
    private Statement statement;
    private PreparedStatement pstmt;
    public MealPlannerDB() {
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            connection.setAutoCommit(true);
            statement = connection.createStatement();
            createTables();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void connect() {
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            connection.setAutoCommit(true);
            statement = connection.createStatement();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void disconnect() {
        try {
            if(pstmt!=null)pstmt.close();
            if(statement!=null)statement.close();
            if(connection!=null)connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void createTables() {
        try {
            statement.executeUpdate("create table if not exists meals(" +
                    "category varchar(1024)," +
                    "meal varchar(1024)," +
                    "meal_id integer" +
                    ")");

            statement.executeUpdate("create table if not exists ingredients(" +
                    "ingredient varchar(1024)," +
                    "ingredient_id integer," +
                    "meal_id integer" +
                    ")");
            statement.executeUpdate("create table if not exists plan(" +
                    "option varchar(1024)," +
                    "category varchar(1024)," +
                    "meal_id integer" +
                    ")");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void addMeal(String category, String name) {
        try {
            if(!isEmpty("meals")) {
                connect();
                ResultSet rs = statement.executeQuery("SELECT MAX(meal_id) as max_items\n" +
                        "FROM meals;");
                rs.next();
                meal_id = rs.getInt("max_items") + 1;
                disconnect();
            }
            connect();
            pstmt = connection.prepareStatement("INSERT INTO meals (category, meal, meal_id) VALUES (?, ?, ?) RETURNING meal_id", Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, category);
            pstmt.setString(2, name);
            pstmt.setInt(3, meal_id);
            pstmt.executeUpdate();
            disconnect();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void addIngredients(String ingredients) {
        try {
            String[] ingredientsArray=ingredients.split(",");
            connect();
            int ingredient_id=1;
            pstmt = connection.prepareStatement("INSERT INTO ingredients (ingredient, ingredient_id, meal_id) VALUES (?, ?, ?)");
            for (String ingredient : ingredientsArray) {
                pstmt.setString(1, ingredient);
                pstmt.setInt(2, ingredient_id++);
                pstmt.setInt(3, meal_id);
                pstmt.executeUpdate();
            }
            disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void deletePlan(){
        try {
            connect();
            statement=connection.createStatement();
            statement.executeUpdate("DELETE FROM plan");
            disconnect();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public boolean isEmpty(String table) {
        try {
            connect();
            int numberOfRow = 0;
            ResultSet checkIsEmpty = statement.executeQuery(String.format("SELECT COUNT(*) FROM %s",table));
            checkIsEmpty.next();
            numberOfRow = checkIsEmpty.getInt(1);
            disconnect();
            if (numberOfRow != 0) {
                return false;
            }
            else {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public ArrayList<String> importIngredientsForSave() {
        ArrayList<String> ingredientsList = null;
        ArrayList<Integer> meal_ids=new ArrayList<>();
        try {
            connect();
                connect();
                ingredientsList = new ArrayList<>();
                ResultSet mealId = statement.executeQuery("SELECT meal_id FROM plan");
                while (mealId.next()) {
                    meal_ids.add(mealId.getInt("meal_id"));
                }
                disconnect();
                connect();
                for(int id:meal_ids){
                    ResultSet ingredients =statement.executeQuery(String.format("SELECT ingredient FROM ingredients WHERE meal_id=%s",id));
                    while (ingredients.next()){
                        ingredientsList.add(ingredients.getString("ingredient"));

                    }
                }
                disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ingredientsList;
    }

    public void addPlan(String day,String category,String nameMeal){
        try {
            int mealIdFromMealName=nameToId(nameMeal);
            connect();
            pstmt = connection.prepareStatement("INSERT INTO plan (option, category, meal_id) VALUES (?, ?, ?)");
            pstmt.setString(1,day);
            pstmt.setString(2,category);
            pstmt.setInt(3,mealIdFromMealName);
            pstmt.executeUpdate();
            disconnect();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }


    public ArrayList<Meal> importMeals(String category) {
        ArrayList<Meal> listOfMeal=new ArrayList<>();
        try {
            connect();
            ResultSet rs = statement.executeQuery(String.format("SELECT * FROM meals WHERE category = '%s';",category));
            while (rs.next()) {
                int getIdToFetchIngredients = rs.getInt("meal_id");
                pstmt = connection.prepareStatement("SELECT * FROM ingredients WHERE meal_id = ?;");
                pstmt.setInt(1, getIdToFetchIngredients);
                ResultSet ingredients = pstmt.executeQuery();

                ArrayList<String> arrIngredients = new ArrayList<>();

                while (ingredients.next()) { // store ingredients in array
                    arrIngredients.add(ingredients.getString("ingredient"));
                }

                String name = rs.getString("meal");
                String[] ingredientList = arrIngredients.toArray(new String[arrIngredients.size()]);
                listOfMeal.add(new Meal(category,name,ingredientList));
            }
            disconnect();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return listOfMeal;
    }
    public void printPlan(String[] days,String[] availableCategory){
        try{
            connect();
            for(String day:days){
                System.out.println(day);
                for(String category:availableCategory){
                    ResultSet rs = statement.executeQuery(String.format("SELECT meal_id FROM plan WHERE category='%s' and option='%s'",category,day));
                    int mealId=0;
                    while (rs.next()){
                        mealId=rs.getInt("meal_id");
                    }
                    rs.close();
                    ResultSet mealName = statement.executeQuery(String.format("SELECT * FROM meals WHERE meal_id=%s",mealId));
                    String name=null;
                    while (mealName.next()){
                        name=mealName.getString("meal");
                    }
                    System.out.printf("%s: %s",category,name);
                }
            }
            disconnect();
        }catch (SQLException e){
            e.printStackTrace();
        }

    }


    public ArrayList<String> MealIngredient(String category){
        ArrayList<String> nameOfMeal=new ArrayList<>();
        try{
            connect();
            ResultSet rs = statement.executeQuery(String.format("SELECT * FROM meals WHERE category = '%s' ORDER BY meal;",category));
            while (rs.next()){
                nameOfMeal.add(rs.getString("meal"));
            }
            disconnect();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return nameOfMeal;
    }
    public int nameToId(String name){
        int mealId=0;
        try{
            connect();
            ResultSet rs = statement.executeQuery(String.format("SELECT * FROM meals WHERE meal = '%s';",name));
            while (rs.next()){
                mealId=rs.getInt("meal_id");
            }
            disconnect();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return mealId;
    }
}
