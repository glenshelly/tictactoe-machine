package com.games.tictactoe;

import com.games.IGameBoard;
import com.games.IGameMove;
import com.games.RenderingHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A value object that simply contains board information
 */
public class TicTacToeBoard
      implements IGameBoard
{


   private static final int

         MAX_CELLS = 9;

   private final TicTacToeCell[] fBoardCells;

   private final List<TicTacToeMove> fMoveHistory;

   public TicTacToeBoard()
   {

      fMoveHistory = new ArrayList<>();

      fBoardCells = new TicTacToeCell[MAX_CELLS];
      for (int i = 0; i < MAX_CELLS; i++)
      {
         fBoardCells[i] = new TicTacToeCell();
      }
   }

   /**
    * @param pMove the move to make
    */
   @Override
   public void applyChosenMove(final IGameMove pMove)
   {
      Objects.requireNonNull(pMove, "move may not be null");
      if (pMove instanceof TicTacToeMove)
      {
         TicTacToeMove tttMove = (TicTacToeMove) pMove;

         //RenderingHelper.renderLoggingLine("applyChosenMove: given move=" + tttMove);
         final int oneBasedCellNumber = tttMove.getCellNumber();
         if (oneBasedCellNumber < 1 || oneBasedCellNumber > MAX_CELLS)
         {
            throw new IllegalArgumentException("invalid cell number.  value=" + oneBasedCellNumber);
         }
         if (tttMove.getCellStatus() == null)
         {
            throw new IllegalArgumentException("status may not be null");
         }


         // Apply the move and add it to the history
         setCellStatus(oneBasedCellNumber, tttMove.getCellStatus());
         addMoveToHistory(tttMove);
      }
      else
      {
         RenderingHelper.renderLoggingLine("applyChosenMove: given move is not a tttMove object. class=" + pMove.getClass().getName());
         // handle this situation according to the application norms
      }
   }

   /**
    * @param pCellStatus the status to check for
    * @return true if there is some TicTacToe-ish combination of the given cell status in this board
    */
   public boolean isGameWon(CellStatusEnum pCellStatus)
   {
      Objects.requireNonNull(pCellStatus, "cellStatusEnum may not be null");

      // There are 8 possible ways to win
      return
            // three rows
            (isMatch(1, pCellStatus) && isMatch(2, pCellStatus) && isMatch(3, pCellStatus))
                  || (isMatch(4, pCellStatus) && isMatch(5, pCellStatus) && isMatch(6, pCellStatus))
                  || (isMatch(7, pCellStatus) && isMatch(8, pCellStatus) && isMatch(9, pCellStatus))
                  // three columns
                  || (isMatch(1, pCellStatus) && isMatch(4, pCellStatus) && isMatch(7, pCellStatus))
                  || (isMatch(2, pCellStatus) && isMatch(5, pCellStatus) && isMatch(8, pCellStatus))
                  || (isMatch(3, pCellStatus) && isMatch(6, pCellStatus) && isMatch(9, pCellStatus))
                  // two diagonals
                  || (isMatch(1, pCellStatus) && isMatch(5, pCellStatus) && isMatch(9, pCellStatus))
                  || (isMatch(3, pCellStatus) && isMatch(5, pCellStatus) && isMatch(7, pCellStatus));

   }


   /**
    * @param pCellStatus the status to check for
    * @return a list of cells numbers that have the given status.  Will not return null.  May (of course) return an empty list
    */
   public List<Integer> getCellsOfGivenStatus(final CellStatusEnum pCellStatus)
   {
      Objects.requireNonNull(pCellStatus, "cellStatusEnum may not be null");

      List<Integer> returnValue = new ArrayList<>();
      for (int cellNumber = 1; cellNumber <= MAX_CELLS; cellNumber++)
      {
         CellStatusEnum thisCellStatus = getCellStatus(cellNumber);
         if (thisCellStatus == pCellStatus)
         {
            returnValue.add(cellNumber);
         }
      }
      return returnValue;

   }

   /**
    * @param pCellNumber  a value from 1 to 9
    * @param pGivenStatus the status to see if it matches
    * @return the calculation value of the status object at the given location
    */
   private boolean isMatch(final int pCellNumber, final CellStatusEnum pGivenStatus)
   {
      // convert cell  number to array pointer
      return getCellStatus(pCellNumber) == pGivenStatus;
   }


   /**
    * @param pCellNumber 1 based
    * @return the corresponding cell
    */
   public CellStatusEnum getCellStatus(final int pCellNumber)
   {
      if (pCellNumber < 1 || pCellNumber > 9)
      {
         throw new IllegalArgumentException("cell number must be from 1 to 9, but passed in value=" + pCellNumber);
      }
      // convert cell  number to array pointer
      return fBoardCells[pCellNumber - 1].getCellStatus();
   }

   /**
    *
    * @param pCellNumber a 1-based cell number
    * @param pCellStatus the new status for this cell
    */
   private void setCellStatus(final int pCellNumber, CellStatusEnum pCellStatus)
   {
      // convert 1-based cell number to 0-based array pointer
      fBoardCells[pCellNumber - 1].setCellStatus(pCellStatus);
   }


   /**
    * Will add all moves
    *
    * @param pMove the move to add
    */
   private void addMoveToHistory(TicTacToeMove pMove)
   {
      fMoveHistory.add(pMove);
   }

   @Override
   /**
    * @return the move history for current game
    */
   public List<? extends IGameMove> getMoveHistory()
   {
      return fMoveHistory;
   }
}
