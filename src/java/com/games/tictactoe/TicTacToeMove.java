package com.games.tictactoe;

import com.games.IGameMove;

/**
 * Class representing a particular move in TicTacToe
 */
public class TicTacToeMove
      implements IGameMove
{


   private static final int NULL_CELL_NUMBER = -1;
   private CellStatusEnum fCellStatus;
   private boolean fChooseToStop;
   private int fCellNumber;

   /**
    *
    * Meaning of cell numbers
    *
    *    1 2 3
    *    4 5 6
    *    7 8 9
    *
    * @param pCellNumber a number from 1 to 9, representing the cells in the grid
    * @param pCellStatus the new status to give a cell
    */
   public TicTacToeMove(int pCellNumber, CellStatusEnum pCellStatus)
   {
      if (pCellNumber < 1 || pCellNumber > 9)
      {
         throw new IllegalArgumentException("cellNumber is not valid. cellNumber=" + pCellNumber);
      }
      if (pCellStatus == null)
      {
         throw new IllegalArgumentException("cell status must not be null");
      }

      init(pCellNumber, pCellStatus, false);

   }


   public TicTacToeMove(final boolean pChooseToStop)
   {
      init(NULL_CELL_NUMBER, null, pChooseToStop);
   }

   private void init(final int pCellNumber, final CellStatusEnum pCellStatus, final boolean pChooseToStop)
   {
      fCellNumber = pCellNumber;
      fCellStatus = pCellStatus;
      fChooseToStop = pChooseToStop;
   }

   /**
    *
    * @return the status of the move
    */
   public CellStatusEnum getCellStatus()
   {
      return fCellStatus;
   }

   @Override
   public boolean isChooseToStop()
   {
      return fChooseToStop;
   }


   @Override
   public String toString()
   {
      return "TicTacToeMove{" +
            ", fCellStatus=" + fCellStatus +
            ", fChooseToStop=" + fChooseToStop +
            '}';
   }

   public int getCellNumber()
   {
      return fCellNumber;
   }
}
