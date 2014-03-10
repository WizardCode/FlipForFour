package com.dynet.kjanssen;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class FlipForFourPanel extends JPanel implements KeyListener {

    private FlipForX fff;
    private int player;
    private int winner;
    private boolean waiting;
    private boolean onePlayer;
    private JFrame frame;
    private Font font;
    private JLabel title;
    private DisplayPanel displayPanel;
    private JPanel colLabels;
    private Image imageR, imageB;
    private Color redHighlight, blueHighlight;

    public FlipForFourPanel (JFrame parent)
    {
        fff = new FlipForX(5, 4);
        player = 1;
        winner = 0;
        waiting = false;
        frame = parent;
        frame.addKeyListener(this);
        setBackground(Color.WHITE);
        font = new Font ("Arial", Font.PLAIN, 30);
        title = new JLabel ("Flip For Four");
        title.setFont(new Font("Arial", Font.PLAIN, 40));
        add(title);
        displayPanel = new DisplayPanel();
        add(displayPanel);
        colLabels = new JPanel(new GridLayout(1, 5));
        colLabels.setBackground(Color.WHITE);
        for (int i = 1; i <= 5; i++) {
            JLabel colLabel = new JLabel("     " + i);
            colLabel.setFont(font);
            colLabel.setBackground(Color.WHITE);
            colLabel.setPreferredSize(new Dimension(104, 40));
            colLabels.add(colLabel);
        }
        add(colLabels);
        JLabel info = new JLabel("Left-Click: Drop     Right-Click: Flip     F1: Help");
        info.setFont(new Font("Arial", Font.PLAIN, 20));
        add(info);
        imageR = new ImageIcon(getClass().getResource("blue-ball.png")).getImage();
        imageB = new ImageIcon(getClass().getResource("red-ball.png")).getImage();
        redHighlight = new Color(255, 170, 170);
        blueHighlight = new Color(150, 130, 255);
        DecidePlayers();
    }

    void DecidePlayers ()
    {
        //Custom button text
        Object[] options = {"1 Player", "2 Player"};
        onePlayer = JOptionPane.showOptionDialog(frame,
                "Would you like to play 1 Player (against computer)\n" +
                        "or 2 Player (hotseat)? ",
                "How would you like to play?",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]) == 0;
    }

    void UpdatePanel()
    {
        int playAgain = JOptionPane.showConfirmDialog (null, "Would you like to play again?",
                                (winner == 2 ? "Red " : "Blue ") + "Player Wins!", JOptionPane.YES_NO_OPTION);

        if (playAgain == 0) {
            fff = new FlipForX(5, 4);
            displayPanel.repaint();
            DecidePlayers();
        } else {
            WindowEvent wev = new WindowEvent(frame, WindowEvent.WINDOW_CLOSING);
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
        }

    }

    public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {
        System.out.println("Key Released: " + e.getKeyCode() + "F1: " + KeyEvent.VK_F1);
        if (e.getKeyCode() == KeyEvent.VK_F1)
            JOptionPane.showMessageDialog(null,
                    "The goal of Flip For Four is to get four pieces\n" +
                    "of the same color in a row or a diagonal. One\n" +
                    "turn consists of either dropping a piece into\n" +
                    "column or flipping the contents of a column\n" +
                    "upside down.",
                    "Flip For Four Help",
                    JOptionPane.PLAIN_MESSAGE);
    }

    class DisplayPanel extends JPanel
    {
        FFFSquare [] squares;

        DisplayPanel () {
            setLayout(new GridLayout(5, 5, 4, 0));
            setBackground(Color.BLACK);
            setBorder(new EmptyBorder(4, 4, 4, 4));
            squares = new FFFSquare[25];
            for (int i = 0; i < 25;)
                for (int r = 5; r >= 1; r--)
                    for (int c = 1; c <= 5; c++, i++) {
                        squares[i] = new FFFSquare(r, c);
                        add(squares[i]);
                    }
        }

        public void paintComponent (Graphics g)
        {
            super.paintComponent (g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint (RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            for (int s = 0; s < 9; s++)
                squares[s].repaint();
        }

        class FFFSquare extends JPanel implements MouseListener
        {
            int row, col;
            FFFSquare (int R, int C)
            {
                row = R;
                col = C;
                setPreferredSize (new Dimension(100, 100));
                setBackground (Color.WHITE);
                addMouseListener (this);
            }
            public void paintComponent (Graphics g)
            {
                super.paintComponent (g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint (RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setFont (font);
                char who = fff.GetPosition (row, col);
                //String owner = new String();
                if (who == '1')
                    //owner = "1";
                    g2.drawImage(imageR, 0, 0, 100, 100, null);
                else if (who == '2')
                    //owner = "2";
                    g2.drawImage(imageB, 0, 0, 100, 100, null);
                //g2.drawString(owner, 35, 60);
            }

            public void mouseClicked (MouseEvent e)
            {
                // System.out.println("Clicked: (" + row + ", " + col + ")");

                boolean goodMove = false;
                if (!waiting) goodMove = fff.Play(col, e.getButton() == MouseEvent.BUTTON3 ? 'F' : 'D', player);
                System.out.println("Player: " + player + " goes " + goodMove);
                if (goodMove && !waiting)
                {
                    player = player == 1 ? 2 : 1;
                    displayPanel.repaint();
                    if (!onePlayer) {
                        mouseExited(e);
                        mouseEntered(e);
                    }
                    winner = fff.Test();
                    if (winner > 0)
                        UpdatePanel ();
                    else if (onePlayer) {
                        waiting = true;
                        player = player == 1 ? 2 : 1;
                        int delay = 500; //milliseconds
                        ActionListener taskPerformer = new ActionListener()
                        {
                            public void actionPerformed(ActionEvent evt)
                            {
                                player = player == 1 ? 2 : 1;
                                boolean goodMove = fff.MakeBestPlay(player);
                                System.out.println("Player: " + player + " goes " + goodMove);

                                if (goodMove)
                                {
                                    player = player == 1 ? 2 : 1;

                                    displayPanel.repaint();
                                    winner = fff.Test();
                                    if (winner > 0)
                                        UpdatePanel ();

                                    waiting = false;
                                }
                            }
                        };
                        Timer timer = new Timer(delay, taskPerformer);
                        timer.setRepeats(false);
                        timer.start();
                    }
                }
            }

            public void mouseEntered (MouseEvent e)
            {
                // System.out.println("Entered: (" + row + ", " + col + ")");

                for (int i = 0; i < squares.length; i++)
                    if (squares[i].col == col)
                        squares[i].setBackground(player == 2 ? redHighlight : blueHighlight);
            }

            public void mouseExited (MouseEvent e)
            {
                // System.out.println("Exited: (" + row + ", " + col + ")");

                for (int i = 0; i < squares.length; i++)
                    if (squares[i].col == col)
                        squares[i].setBackground(Color.WHITE);
            }

            public void mousePressed (MouseEvent event) {}
            public void mouseReleased (MouseEvent event) {}
        }
    }

    public static void main (String [] args)
    {
        JFrame frame = new JFrame ("Flip For Four");
        FlipForFourPanel fffPanel = new FlipForFourPanel (frame);
        frame.getContentPane().add (fffPanel);
        frame.setSize (600, 675);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        frame.setResizable (false);
        frame.setVisible (true);
    }
}
