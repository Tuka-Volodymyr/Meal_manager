package mealplanner;

public class Meal {
    private String category;
    private String name;
    private String[] ingredients;
    public Meal(String category,String name,String[] ingredients) {
        this.category = category;
        this.name = name;
        this.ingredients = ingredients;
    }
    public void printInfoAboutMeal(){
        System.out.printf("""
                Name: %s
                Ingredients:%n""",name);
        for(String ingredient:ingredients){
            if(ingredient.startsWith(" ")) {
                ingredient = ingredient.substring(1);
            }
            System.out.println(ingredient);
        }
        System.out.println();
    }
    public String getName() {
        return name;
    }
    public String getCategory() {
        return category;
    }

    public String[] getIngredients() {
        return ingredients;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public void setIngredients(String[] ingredients) {
        this.ingredients = ingredients;
    }
}
