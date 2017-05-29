//Michael Ly, cs380

import java.util.*;
import java.io.*;
import java.net.*;

public class TicTacToeClient {

    public static void main(String[] args)
    {
        try{
			
            Socket socket = new Socket("codebank.xyz", 38006);

            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            ObjectInputStream isob = new ObjectInputStream(is);
            ObjectOutputStream osob = new ObjectOutputStream(os);

            //Asking for a username
            Scanner kb = new Scanner(System.in);
            System.out.println("Please enter a username: ");
            String un = kb.nextLine();

            //Connecting to server
            ConnectMessage cm = new ConnectMessage(un);
            osob.writeObject(cm);

            //Starting a new game
            System.out.println("Starting tic tac toe game...\nYou are player 1 and use 'X' and go first, player 2 uses 'O' " );
            CommandMessage cmdm = new CommandMessage(CommandMessage.Command.NEW_GAME);
            osob.writeObject(cmdm);

            //Gets the board
            BoardMessage bm = (BoardMessage) isob.readObject();
            ErrorMessage er = null;

            //Loop until the game is over
            while(bm.getStatus() == BoardMessage.Status.IN_PROGRESS && er == null)
            {
                    System.out.println("Board layout is a 3x3 square with choices 0-2, starting from the top left and ending at the bottom right");

                    //Prints the board layouts
                    byte[][] temp = bm.getBoard();
                    getGameLayout(temp);
                    getBoardLayout(temp);

                    //gets user input
                    int r = 0, c = 0;
                    boolean valid = false;
                    String input = "";

                    while (!valid) {
                        System.out.println("Enter a position (p) or surrender (s) ?: ");
                        input = kb.nextLine();

                        if (input.equals("s")) {
                            System.out.println("You have surrendered, now exiting...");
                            CommandMessage cmer = new CommandMessage(CommandMessage.Command.SURRENDER);
                            osob.writeObject(cmer);
                            er = (ErrorMessage) isob.readObject();
                            valid = true;
                            continue;
                        }

                        System.out.println("Enter a position: ");
                        r = kb.nextByte();
                        System.out.println("Enter a position: ");
                        c = kb.nextByte();

                        kb.nextLine();

                        if ((r <= -1 || r >= 3) || (c <= -1 || c >= 3)) {
                            System.out.println("Invalid position(s) " + r + "," + c);
                        } else {
                            System.out.println(r + " " + c);
                            //Sends the move to server
                            osob.writeObject(new MoveMessage((byte) r, (byte) c));

                            //gets new board
                            bm = (BoardMessage) isob.readObject();
                            valid = true;
                        }
                    }

                    //checking board status
                if (bm.getStatus() != BoardMessage.Status.IN_PROGRESS) {
                        temp = bm.getBoard();
                        getGameLayout(temp);
                    if (bm.getStatus() == BoardMessage.Status.ERROR) {
                        System.out.println("error, exiting...");
                        continue;
                    } else if (bm.getStatus() == BoardMessage.Status.PLAYER1_VICTORY) {
                        System.out.println("Player 1 wins, exiting...");

                        continue;
                    } else if (bm.getStatus() == BoardMessage.Status.PLAYER2_VICTORY) {
                        System.out.println("Player 2 wins, exiting...");

                        continue;
                    } else if (bm.getStatus() == BoardMessage.Status.PLAYER2_SURRENDER) {
                        System.out.println("Player 2 surrenders, exiting...");

                        continue;
                    } else if (bm.getStatus() == BoardMessage.Status.STALEMATE) {
                        System.out.println("Stalemate, exiting...");

                        continue;
                    }
                }

            }

            socket.close();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void getBoardLayout(byte[][] b)
    {
        for(int i = 0; i < b.length; i++)
        {
            for(int j = 0; j < b[0].length; j++)
            {
                System.out.print("| ");
                if(b[i][j] == 0)
                {
                    System.out.print(i + " ");
                    System.out.print(j);
                }
                else if(b[i][j] == 1)
                {
                    System.out.print("n/a");
                }
                else if(b[i][j] == 2)
                {
                    System.out.print("n/a");
                }
                System.out.print(" |");
            }
            System.out.println("\n");
        }
    }

    public static void getGameLayout(byte[][] b)
    {
        for(int i = 0; i < b.length; i++)
        {
            for(int j = 0; j < b[0].length; j++)
            {
                System.out.print("| ");
                if(b[i][j] == 0)
                {
                    System.out.print(" ");
                }
                else if(b[i][j] == 1)
                {
                    System.out.print("X");
                }
                else if(b[i][j] == 2)
                {
                    System.out.print("O");
                }
                System.out.print(" |");
            }
            System.out.println("\n");
        }
    }
}
