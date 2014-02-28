package com.dynet.kjanssen;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        final int SIZE = 5;
        final int WIN = 4;
        FlipForX FFX = new FlipForX(SIZE, WIN);
        int player = 1;
        int winner = 0;
        boolean done = false;
        Scanner in = new Scanner(System.in);

        System.out.println(FFX);
        while (!done && winner == 0)
        {
            String colStr, playStr;
            int column = 0;
            char play = ' ';
            do
            {
                System.out.print("Player " + player + ", enter the column for your play: ");
                colStr = in.next();
                //System.out.println(colStr);
                if (colStr.charAt(0) == 'q' || colStr.charAt(0) == 'Q')
                    done = true;
                else
                    column = Integer.parseInt(colStr);
            } while (!done && (column < 1 || column > SIZE));
            if (done)
                break;
            do
            {
                System.out.print("Enter the type of play - F for flip - D for drop: ");
                playStr = in.next();
                //System.out.println(playStr);
                if (playStr.charAt(0) == 'q' || playStr.charAt(0) == 'Q')
                    done = true;
                else
                    play = playStr.toUpperCase().charAt(0);
            } while (!done && play != 'F' && play != 'D');
            if (done)
                break;

            // System.out.println("Col: " + column + ", play: " + play + ", player: " + player);
            if (FFX.Play (column, play, player))
            {
                System.out.print(FFX);
                winner = FFX.Test();
                player = player == 1 ? 2 : 1;
            }
            else
                System.out.print("Invalid move - try again.\n");
        }
        if (winner == 1)
            System.out.print("Player 1!! You win!!\n");
        else if (winner == 2)
            System.out.print("Player 2!! You win!!\n");
        else if (winner == 3)
            System.out.print("Sorry, the cat wins!\n");
        else
            System.out.print("Sorry, there is no winner :(\n");
    }
}
