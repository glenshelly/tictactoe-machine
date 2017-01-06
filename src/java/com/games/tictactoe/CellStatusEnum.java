package com.games.tictactoe;

/**
 * Enum that gives the options for the status of a TicTacToe cell
 */
public enum CellStatusEnum
{


   // X corresponds to the application
   X_SELECTED("X"),

   // O corresponds to the user playing
   O_SELECTED("O"),
   UNSELECTED(" ");

   String fRenderingValue = null;

   /**
    *
    * @param pRenderingValue the value to display for a particular state
    */
   CellStatusEnum(final String pRenderingValue)
   {
      fRenderingValue = pRenderingValue;
   }


   /**
    *
    * @return the value to render for a particular state of the cell
    */
   public String getRenderingValue()
   {
      return fRenderingValue;
   }
}
