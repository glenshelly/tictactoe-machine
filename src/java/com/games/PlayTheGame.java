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
 * - Depending on what positions the human plays in a game, the machine may or may not be able to play every last
 * combination.  However, being ever hopeful, it will continue to try.
 * For example, let's say that the application has already lost a game with a certain 5-move combination. It will
 * nevertheless select the same first 4 of those 5, being hopeful that, in this new game, the human will choose
 * something different on the human's 4th (last) move, therefore allowing the application to make a different choice on
 * it's final move of the game.  Of course, humans being humans, this may not happen.  However, our optimistic application
 * will continue to try.
 * - The game has not been optimized for winning; rather, it simply tries not to lose.  Improvements could quickly
 * be made so that it seizes the opportunity to win, based on prior experience.
 * <p>
 * Assumptions made during the implementation
 * - English only interface: the interface has not be localized for multilingual support
 * - Performance: At present, readability and simplicity are more important than raw speed.  If performance targets
 * are identified that this implementation does not satisfy, additional effort could be made to improve performance.
 * Until then, such effort is not warranted, especially if it sacrifices readability.
 * - JDK version: The application supports Java 8
 * - Testing: This code does not include assertions or junit tests; such checks could be added per project standards
 * - Results are saved in simple text files
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
         PlayerEnum currentPlayer = PlayerEnum.APPLICATION;
         GameStatusInfo gameStatusInfo = new GameStatusInfo(currentPlayer, GameStatusEnum.ONGOING);
         IGameBoard gameBoard = gameManager.getNewGameBoard();

         // Loop through the moves in this particular game
         boolean isThisGameStillOn = true;
         while (isThisGameStillOn)
         {
            IGameMove gameMove = gameManager.getNextMove(currentPlayer, gameBoard);
            if (gameMove.isChooseToStop())
            {
               // it could be that the human stopped, or that the application has given up
               gameStatusInfo = new GameStatusInfo(currentPlayer, GameStatusEnum.QUIT);
            }
            else
            {
               // Make the specified move, and check the subsequent status
               gameBoard.applyChosenMove(gameMove);
               gameStatusInfo = gameManager.getGameStatusInfo(currentPlayer, gameBoard);
            }

            // Give an update for the current status of the game
            gameManager.renderStatusUpdate(gameStatusInfo);

            isThisGameStillOn = !gameStatusInfo.getGameStatus().isGameOver();

            // Change to the next player, in case we loop around to play another round, though we may not actually do so
            currentPlayer = getNextPlayer(currentPlayer);
         }


         // Save the results of this game, and render the final status to the user
         gameManager.saveResults(gameBoard, gameStatusInfo);
         gameManager.renderFinalResults(gameStatusInfo, gameBoard);


         // Check if we should play another game
         isStillPlayingAdditionalGames = inviteAnotherGame();
         if (isStillPlayingAdditionalGames)
         {
            RenderingHelper.renderOutputLine("\n");
            RenderingHelper.renderOutputLine("OK, let's play again!  I'll start...");
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
