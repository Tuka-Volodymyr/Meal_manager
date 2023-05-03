package mealplanner;
public class Checker {
    public boolean availableCategory(String category,String[] availableCategory){
        for(String c:availableCategory){
            if(c.equals(category)){
                return true;
            }
        }
        System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");
        return false;
    }
    public boolean availableInput(String[] ingredients){
        for(String s:ingredients){
                s=s.replaceAll("\\s","");
                if(!s.matches("[a-zA-Z]+")) {
                    System.out.println("Wrong format. Use letters only!");
                    return false;
                }
        }
        return true;
    }

}
