package com.dynet.kjanssen;

import java.util.Random;

public class FlipForX {

    int size;
    int toWin;
    char [][] positions;
    int [] counts;

    public FlipForX (int S, int W)
    {
        size = S;
        toWin = W;

        positions = new char[size + 2][size + 2];
        for (int i = 0; i < size + 2; i++)
            for (int j = 0; j < size + 2; j++)
                positions[i][j] = ' ';

        counts = new int[size + 2];
        for (int i = 0; i < size + 2; i++)
            counts[i] = 0;
    }

    public char GetPosition (int R, int C)
    {
        return positions[R][C];
    }

    public String toString ()
    {
        String outs = "\n";

        for (int j = 0; j < size; j++)
            outs += "+-------";
        outs += "+\n";
        for (int i = size; i > 0; i--)
        {
            for (int j = 0; j < size; j++)
                outs += "|       ";
            outs += "|\n";
            for (int j = 1; j <= size; j++)
                outs += "|   " + positions[i][j] + "   ";
            outs += "|\n";
            for (int j = 0; j < size; j++)
                outs += "|       ";
            outs += "|\n";
            for (int j = 0; j < size; j++)
                outs += "+-------";
            outs += "+\n";
        }
        for (int j = 0; j < size; j++)
            outs += "    " + (j+1) + "   ";
        outs += "\n";

        return outs;
    }

    public boolean Play (int col, char play, int who)
    {
        if (play == 'D' && counts[col] < size) {
            counts[col]++;
            positions[counts[col]][col] = Character.toChars(who + 48)[0];
            return true;
        } else if (play == 'F' && counts[col] > 0) {
            for (int i = 1, j = counts[col]; i < j; i++, j--) {
                char temp = positions[i][col];
                positions[i][col] = positions[j][col];
                positions[j][col] = temp;
            }
            return true;
        }

        return false;
    }

    private boolean UnPlay (int col, char play)
    {
        if (play == 'D' && counts[col] > 0) {
            positions[counts[col]][col] = ' ';
            counts[col]--;
            return true;
        } else if (play == 'F' && counts[col] > 0) {
            for (int i = 1, j = counts[col]; i < j; i++, j--) {
                char temp = positions[i][col];
                positions[i][col] = positions[j][col];
                positions[j][col] = temp;
            }
            return true;
        }

        return false;
    }

    public int Test ()
    {
        int first = 1, last = size - toWin + 1;
        for (int r = 1; r <= size; r++)
            for (int c = first; c <= last; c++)
                if (positions[r][c] != ' ')
                {
                    int count = 1;
                    for (int i = c+1; positions[r][i] == positions[r][c]; i++)
                        count++;
                    if (count >= toWin)
                        return positions[r][c] - '0';
                }
        for (int c = 1; c <= size; c++)
            for (int r = first; r <= last; r++)
                if (positions[r][c] != ' ')
                {
                    int count = 1;
                    for (int i = r+1; positions[i][c] == positions[r][c]; i++)
                        count++;
                    if (count >= toWin)
                        return positions[r][c] - '0';
                }
        for (int r = first; r <= last; r++)
        {
            for (int c = first; c <= last; c++)
                if (positions[r][c] != ' ')
                {
                    int count = 1;
                    for (int i = r+1, j = c+1; positions[i][j] == positions[r][c]; i++, j++)
                        count++;
                    if (count >= toWin)
                        return positions[r][c] - '0';
                }
            for (int c = size; c >= toWin; c--)
                if (positions[r][c] != ' ')
                {
                    int count = 1;
                    for (int i = r+1, j = c-1; positions[i][j] == positions[r][c]; i++, j--)
                        count++;
                    if (count >= toWin)
                        return positions[r][c] - '0';
                }
        }
        return 0;
    }

    public boolean MakeBestPlay (int who)
    {
        // check for winning moves
        for (int col = 1; col <= size; col++) {
            Play(col, 'D', who);
            if (Test() == who)
                return true;
            UnPlay(col, 'D');
            Play(col, 'F', who);
            if (Test() == who)
                return true;
            UnPlay(col, 'F');
        }

        // check for blocking drop moves
        for (int col = 1; col <= size; col++) {
            int other = who == 1 ? 2 : 1;

            Play(col, 'D', other);
            if (Test() == other) {
                UnPlay(col, 'D');
                Play(col, 'D', who);
                return true;
            }
            UnPlay(col, 'D');
        }

        // check for blocking flip moves
        for (int col = 1; col <= size; col++) {
            int other = who == 1 ? 2 : 1;

            Play(col, 'F', other);
            if (Test() == other) {
                UnPlay(col, 'F');
                if (counts[col] < size) {
                    Play(col, 'D', who);
                    return true;
                }
            }
            UnPlay(col, 'F');
        }

        // else make random move
        Random random = new Random();
        int n = random.nextInt(size) + 1;

        while (!Play(n, 'D', who)) {
            n = random.nextInt(size) + 1;
        }

        return true;
    }
}
