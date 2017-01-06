package com.games;

import java.util.List;

/**
 * public contract for game boards
 */
public interface IGameBoard
{


   /**
    *
    * @param pMove the move to apply to the board
    */
   void applyChosenMove(IGameMove pMove);

   /**
    *
    * @return the history of moves
    */
   List<? extends IGameMove> getMoveHistory();
}
