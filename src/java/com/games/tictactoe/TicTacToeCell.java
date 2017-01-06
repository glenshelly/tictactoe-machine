package com.games.tictactoe;

/**
 * Class that represents a single cell in a TicTacToe board
 */
public class TicTacToeCell
{


   private CellStatusEnum fCellStatus = CellStatusEnum.UNSELECTED;

   /**
    *
    * @return the status of this cell
    */
   public CellStatusEnum getCellStatus()
   {
      return fCellStatus;
   }

   /**
    *
    * @param pCellStatus
    */
   public void setCellStatus(CellStatusEnum pCellStatus)
   {
      fCellStatus = pCellStatus;
   }
}
