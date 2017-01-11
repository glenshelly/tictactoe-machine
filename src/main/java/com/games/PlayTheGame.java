package com.games;


import com.games.tictactoe.TicTacToeManager;

import java.util.Scanner;

/**
 * This class provides static methods to satisfy the requirements of the following exercise:
 * <p>
 * "Please write a program that lets a human player to play “Tic Tac Toe” game with a computer.
 * The computer initially plays by randomly choosing an  empty cell in a grid. However, it keeps track of all
 * previous loosing combinations and never loses by following the same sequence of moves. (you can also
 * consider that one loosing combination can be converted into 4 if you rotate the grid by 90, 180, and 270 degrees)
 * For simplicity, make it a console game and use text file  for storing loosing combinations. To simplify debugging,
 * please write each loosing combination on a separate line like this: (2,2)-(2,3)-(1,1)-(3,3)-(3,1)-(3,2) where
 * the last move need to be changed the next time machine plays."
 * <p>
 * Assumptions about the assignment:
 * - The computer will always go first
 * - The game has not been optimized for winning; rather, it simply avoids losing in the same way more than once
 *   Improvements could quickly be made so that it seizes the opportunity to win, based on prior experience.
 * <p>
 * Assumptions made during the implementation
 * - English only interface: the interface has not been localized for multilingual support
 * - Performance: At present, readability of code is prioritized over raw speed.  If performance targets
 * are later identified that this implementation does not satisfy, additional effort could be made to improve performance.
 * - JDK version: The application supports Java 8
 * - Testing: This code does not include assertions or junit tests; such checks could be added per project standards
 * - Results are saved in simple text files in the directory where the application is run.
 */
public class PlayTheGame
{

   public static void main(String[] pArgs)
   {
      play();
   }


   /**
    * Wrapper method for running the exercise
    */
   private static void play()
   {

      /*
         This method could support other games, besides TicTacToe.
         For now, tic-tac-toe is is the only supported implementation of IGameManager
       */
      IGameManager gameManager = getGameToPlay();
      gameManager.introduceGameAndRules();

      // Loop through (potentially) multiple games
      boolean isStillPlayingAdditionalGames = true;
      while (isStillPlayingAdditionalGames)
      {

         // Start a new game, with the application being the first player
         // We'll build a new board each game; we could simply reset the existing board, if we become
         // concerned about memory or performance in the future
         PlayerEnum currentPlayer = PlayerEnum.APPLICATION;
         IGameBoard gameBoard = gameManager.getNewGameBoard();


         // Loop through the moves in this particular game, until the game is no longer on
         boolean isThisGameStillOn = true;
         while (isThisGameStillOn)
         {

            // 1. Get the next move
            IGameMove gameMove = gameManager.getNextMove(currentPlayer, gameBoard);


            // 2. Apply the move (maybe) and get the resulting status
            GameStatusInfo gameStatusInfo;
            if (gameMove.isChooseToStop())
            {
               // It could be that the human quit, or that the application has given up
               gameStatusInfo = new GameStatusInfo(currentPlayer, GameStatusEnum.QUIT);
            }
            else
            {
               gameBoard.applyChosenMove(gameMove);
               gameStatusInfo = gameManager.getGameStatusInfo(currentPlayer, gameBoard);
            }


            // 3. Figure out what to do next, and do it
            isThisGameStillOn = !gameStatusInfo.getGameStatus().isGameOver();
            if (isThisGameStillOn)
            {
               // Change to the next player, in preparation of the next time through the loop
               currentPlayer = getNextPlayer(currentPlayer);
            }
            else
            {
               // Save the results of this game, and render the final status to the user
               gameManager.saveResults(gameBoard, gameStatusInfo);
               gameManager.renderFinalResults(gameStatusInfo, gameBoard);
            }

         }




         // Ask to play another game
         isStillPlayingAdditionalGames = inviteAnotherGame();
         if (isStillPlayingAdditionalGames)
         {
            RenderingHelper.renderOutputLine("\n");
            RenderingHelper.renderOutputLine("OK, let's play again!  I'll start...");
         }
         else
         {
            RenderingHelper.renderOutputLine("\n");
            RenderingHelper.renderOutputLine("OK, so long, let's play again soon!");
            RenderingHelper.renderOutputLine("\n");
         }
      }
   }

   /**
    *
    * @param pCurrentPlayer
    * @return the next player.  We could move this down to the IGameManager implementation, if different games will
    * offer different movements of players (e.g., a player might go twice in a row in some games)
    */
   private static PlayerEnum getNextPlayer(PlayerEnum pCurrentPlayer)
   {
      pCurrentPlayer = pCurrentPlayer == PlayerEnum.APPLICATION ? PlayerEnum.HUMAN : PlayerEnum.APPLICATION;
      return pCurrentPlayer;
   }

   /**
    * @return true if the user wants to play another game
    */
   private static boolean inviteAnotherGame()
   {
      RenderingHelper.renderOutputLine("");
      RenderingHelper.renderOutputLine("");
      RenderingHelper.renderOutputLine("");
      RenderingHelper.renderOutputLine("*********************************************************");
      RenderingHelper.renderOutputLine("Would you like to play another game?  Enter 'y' for Yes: ");
      final Scanner scanner = new Scanner(System.in);
      final String nextWord = scanner.next();
      return "y".equalsIgnoreCase(nextWord.trim()) || "yes".equalsIgnoreCase(nextWord.trim());
   }


   /**
    * At present, this method will hard-code a return value of the TicTacToeManager, but
    * it could be later enhanced to return another game that satisfies the GameManager contract
    *
    * @return a particular game to play.
    */
   private static IGameManager getGameToPlay()
   {
      return new TicTacToeManager();
   }


}
