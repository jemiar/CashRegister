package dev.hoangminh;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CashRegister {

    //String to display welcome text or when user enters help in the command
    private static String HELP;
    //String to display when user accidentally enters nothing or only white spaces
    private static final String EMPTY_COMMAND = "Please enter your command. Enter help for list of commands.";
    //String to display when user exits the app
    private static final String SALUT = "Thank you for using our app. See you again soon.";
    //String to display if user enters invalid command
    private static final String INVALID_COMMAND = "Your command is invalid. Enter help for list of commands";
    //String to display if user enters negative value(s)
    private static final String NEG_VAL = "Please do not use negative value for the amount of bank note";
    //String to display if user wants to take out more banknotes than they have in the register
    private static final String NOT_ENOUGH = "You try to take more than you have in the register. Please try again";
    //Operate like a hash table to translate the index and value of the banknote
    private final int[] dic = {20, 10, 5, 2, 1};
    //Used to store the number of types of banknote, in case new banknote will be used in the future
    private final int NUM_DENOMINATOR = 5;
    //instance var: array to store the number of each type of banknote
    private int[] banknotes;

    //Constructor: initialize banknotes array
    public CashRegister(){
        banknotes = new int[NUM_DENOMINATOR];
    }

    //Initialize HELP string by loading it from text file
    static {
        try {
            Path currentRelativePath = Paths.get("");
            String pwd = currentRelativePath.toAbsolutePath().toString();
            System.out.println("Current absolute path is: " + pwd);
            String path = "resources/help.txt";
            HELP = new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            System.err.println("File not existed. Please check file path");
            e.printStackTrace();
        }
    }

    //Method to calculate the sum of money in the register
    int sum() {
        int s = 0;
        for(int i = 0; i < NUM_DENOMINATOR; i++)
            s += banknotes[i] * dic[i];
        return s;
    }

    //Method to get the banknote array in the register
    int[] getBanknotes() {
        return banknotes;
    }

    //Method to show the total amount of money as well as the number of each type of banknote in the register
    void show() {
        int sum = sum();
        StringBuilder output = new StringBuilder();
        output.append("$" + sum + " ");
        for(int b: banknotes)
            output.append(Integer.valueOf(b) + " ");
        System.out.println(output);
    }

    //Method to put money into the register
    void put(String[] command) {
        //If clause to make sure the command is valid with command + number for each type of banknote
        if (command.length != (NUM_DENOMINATOR + 1))
            System.out.println(INVALID_COMMAND);
        else{
            //Temp to store the original array. In case user enters a negative value,
            // restore the banknotes array to its original state
            int[] temp = banknotes.clone();
            for(int i = 1; i < NUM_DENOMINATOR + 1; i++){
                //Try to parse int in the command
                try{
                    int val = Integer.parseInt(command[i]);
                    //If user enters negative value
                    if (val < 0){
                        System.out.println(NEG_VAL);
                        banknotes = temp.clone();
                        return;
                    }else
                        banknotes[i-1] += val;
                }catch (NumberFormatException e) {
                    System.out.println(INVALID_COMMAND);
                    return;
                }
            }
            show();
        }
    }

    //Method to take out money from the register
    void take(String[] command) {
        //If clause to make sure the command is valid
        if (command.length != (NUM_DENOMINATOR + 1))
            System.out.println(INVALID_COMMAND);
        else{
            //Used to restore banknote array if user enters negative value
            //or they want to take out more money than they have
            int[] temp = banknotes.clone();
            for(int i = 1; i < NUM_DENOMINATOR + 1; i++){
                //Try to parse int from the command
                try{
                    int val = Integer.parseInt(command[i]);
                    //If user enters negative value
                    if (val < 0){
                        System.out.println(NEG_VAL);
                        banknotes = temp.clone();
                        return;
                    //If user tries to take out more money than they have
                    }else if (val > banknotes[i-1]){
                        System.out.println(NOT_ENOUGH);
                        banknotes = temp.clone();
                        return;
                    }else
                        banknotes[i-1] -= val;
                }catch (NumberFormatException e) {
                    System.out.println(INVALID_COMMAND);
                    return;
                }
            }
            show();
        }
    }

    //Method to change money
    void change(String[] command) {
        //Make sure the command is valid
        if (command.length != 2)
            System.out.println(INVALID_COMMAND);
        else{
            //Try to parse int in the command
            try{
                int val = Integer.parseInt(command[1]);
                //Make sure the value is not negative
                if (val < 0){
                    System.out.println(NEG_VAL);
                    return;
                }else {
                    //Call the helper method to do the change task
                    changeHelper(val);
                }
            }catch (NumberFormatException e) {
                System.out.println(INVALID_COMMAND);
                return;
            }
        }
    }

    //Method to do the actual change
    private void changeHelper(int val) {
        //used to store the banknote array
        int[] tempArray = banknotes.clone();
        //store the result
        List<int[]> result = new ArrayList<>();
        //used to store the immediate changes while looking for appropriate change
        int[] changes = new int[NUM_DENOMINATOR];
        //call the backtracking function
        backtrack(changes, val, 0, tempArray, result);
        //if the money is changable, result will have size = 1
        if (result.size() > 0) {
            //Update the banknote array and display the change
            int[] result_array = result.get(0);
            for(int i = 0; i < NUM_DENOMINATOR; i++)
                banknotes[i] -= result_array[i];
            StringBuilder output = new StringBuilder();
            for(int b: result_array)
                output.append(Integer.valueOf(b) + " ");
            System.out.println(output);
        } else
            //The amount is unchangable
            System.out.println("Sorry");
    }

    private void backtrack(int[] changes, int val, int index, int[] candidates, List<int[]> res){
        //during the backtracking process, if the sum amount we look for become < 0, stop the search
        if(val < 0)
            return;
        //if we find the proper change, add that to the result and stop the search
        else if (val == 0){
            res.add(changes.clone());
            return;
        } else {
            //For all the candidates that we have in the register
            for(int i = index; i < NUM_DENOMINATOR; i++){
                //Only look for banknote that we have in the register and only when
                //A solution has not been found
                if(candidates[i] > 0 && res.size() < 1){
                    //increase the number of this banknote by 1
                    changes[i] += 1;
                    //reduce the number of this banknote in the register by 1
                    candidates[i] -= 1;
                    //continue the search with new immediate changes and updated value and candidates
                    backtrack(changes, val-dic[i], i, candidates, res);
                    //Backtracking -> Restore changes and candidates to their original states
                    changes[i] -= 1;
                    candidates[i] += 1;
                }
            }
        }
    }

    public static void main(String[] args) {
        CashRegister app = new CashRegister();
        Scanner input = new Scanner(System.in);
        //Welcome
        System.out.println(HELP);
        //Run the app in an indefinite loop until the user enters quit
        while(true){
            System.out.println("Please enter your command");
            System.out.print("> ");
            String command = input.nextLine();
            //Split the command by whitespace
            String[] commandList = command.split(" ");
            //Make sure users don't enter only whitespaces and hit enter accidentally
            if (commandList.length < 0)
                System.out.println(EMPTY_COMMAND);
            else {
                //Depend on the first element of the command, call the proper method to do the task
                switch (commandList[0]) {
                    case "show":
                        app.show();
                        break;
                    case "put":
                        app.put(commandList);
                        break;
                    case "take":
                        app.take(commandList);
                        break;
                    case "change":
                        app.change(commandList);
                        break;
                    case "help":
                        System.out.println(HELP);
                        break;
                    case "quit":
                        System.out.println(SALUT);
                        return;
                    default:
                        System.out.println(INVALID_COMMAND);
                        break;
                }
            }
        }
    }
}
