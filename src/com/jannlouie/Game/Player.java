package com.jannlouie.Game;

import java.util.Scanner;

public class Player {
    private String name = "";
    private String guess = "";
    private int points = 0;

    private Scanner scanner = new Scanner(System.in);

    Player() {}

    Player(String n) {
        this.name = n;
    }

    public void makeGuess() {
        char playerGuess;
        boolean onLoop = false;

        System.out.println("\nPress [1] for \"Even\" or press [2] for \"Odd\".");

        do {
            System.out.print("Enter your guess, " + this.name + ": ");
            playerGuess = scanner.next().charAt(0);

            if (playerGuess == '1') {
                this.guess = "Even";
            }
            else if (playerGuess == '2') {
                this.guess = "Odd";
            }
            else {
                System.out.println("You have entered an invalid input. Please try again\n");
            }
        } while (onLoop);
    }

    public void addPoints(int playerSetPoints) {
        this.points += playerSetPoints;
    }

    public String getName() {
        return this.name;
    }

    public String getGuess() {
        return this.guess;
    }

    public int getPoints() {
        return this.points;
    }
}
