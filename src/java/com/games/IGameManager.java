package com.games;

/**
 * Contract for managing a particular game
 *
 * Has the smarts, whereas the IGameBoard is dumb.
 */
public interface IGameManager
{

   void introduceGameAndRules();

   /**
    *
    * @return a new board to play the game on
    */
   IGameBoard getNewGameBoard();

   /**
    *
    * @param fPlayer
    * @return the next move for the specified player
    */
   IGameMove getNextMove(PlayerEnum fPlayer, IGameBoard pGameBoard);

   /**
    *
    * @param pFinalPlayer
    * @param pGameBoard
    * @return a status value.  Will not return null
    */
   GameStatusInfo getGameStatusInfo(final PlayerEnum pFinalPlayer, IGameBoard pGameBoard);


   void renderStatusUpdate(GameStatusInfo pGameStatusInfo);

   /**
    * Render the game board to the user
    * @param pIGameBoard
    */
   void renderBoard(IGameBoard pIGameBoard);

   /**
    * Render the results to the user
    * @param pGameStatusInfo
    * @param pIGameBoard
    */
   void renderFinalResults(GameStatusInfo pGameStatusInfo, IGameBoard pIGameBoard);



   /**
    * Save the results of the game playing to a file
    */
   void saveResults(IGameBoard pGameBoard, final GameStatusInfo pGameStatusInfo);
}
