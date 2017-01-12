package com.games.tictactoe;


import com.games.GameStatusEnum;
import com.games.GameStatusInfo;
import com.games.IGameBoard;
import com.games.IGameManager;
import com.games.IGameMove;
import com.games.PlayerEnum;
import com.games.RenderingHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * An implementation of the GameManager contract.
 * <p>
 * Some notes on the current TicTacToe implementation
 * <p>
 * - This implementation assumes two players - the Application and a human
 * - Each cell on the board is represented by a single digit, from 1 to 9, where 1 is NW corner and 9 is SE corner
 * - A particular play combination, then, can be written as a combination of numbers; a complete game
 * will always be from 5 to 9.
 * - If I know what 13489 is a losing combination, I'd avoid trying it again, up to the last move that the
 * application made
 */
@SuppressWarnings("JavaDoc")
public class TicTacToeManager
      implements IGameManager
{


   // These file names are hard-coded here; in a real project, they'd probably be specified in some more
   // flexible way (e.g., a properties file of some sort
   private static final String FILE_NAME_VERBOSE = "tttVerbose.log";
   private static final String FILE_NAME_CONCISE = "tttConcise.log";

   private static final int TOTAL_CELL_COUNT = 9;
   private static final int BOARD_DIMENSION = 3;

   // Pithy phrases to render to the user in certain circumstances
   private static final String[] LOSING_PHRASES = {"I gotta get better at this...", "You must be pretty good!", "Did you study this in college?",
         "Are you, like, a TicTacToe Grand Wizard?", "Pretty slick, you are.", "How do you do it?",
         "I am duly impressed.", "Good job!", "Nice!", "Give me another chance?",
         "Again.  Sigh...", "Again!  Arg!"
   };
   private static final String[] WINNING_PHRASES = {"Nice try, though.", "Nevertheless, thou art a truly worthy opponent.", "Better luck next time!", "Keep up the swell effort!", "Keep on trying, though!", "And I promise, I didn't cheat!", "And you thought this game was easy!"};
   private static final String[] DRAW_PHRASES = {"Looks like we're even-steven.", "Neck and neck!", "Shall we give it another shot?"};


   // Set to hold the moves that Should Be Avoided
   private Set<String> fMovesToAvoid;

   /**
    * Public constructor for this manager class.  At this point, there's no particular advantage to creating a
    * static factory method - getInstance() - rather than this constructor
    */
   public TicTacToeManager()
   {
      // load up the old lost-games from a file
      initializeMovesToAvoidSet();
   }

   /**
    * Get the next game move for the specified player
    *
    * @param pPlayer    the play who we want to get a move for
    * @param pGameBoard the game board to use
    * @return the next move to be played.  It may indicate "I quit" by either the human or the application, or
    * be a real move.
    */
   public IGameMove getNextMove(final PlayerEnum pPlayer, IGameBoard pGameBoard)
   {
      if (null == pGameBoard)
      {
         throw new IllegalArgumentException("game board may not be null");
      }
      if (null == pPlayer)
      {
         throw new IllegalArgumentException("player may not be null");
      }
      final IGameMove gameMove;
      if (pPlayer == PlayerEnum.APPLICATION)
      {
         gameMove = getGameMoveFromApplication(pGameBoard);
      }
      else
      {
         // Show the board to the human, before soliciting a move
         renderBoard(pGameBoard);
         gameMove = getGameMoveFromHuman(pGameBoard);
      }
      return gameMove;
   }


   /**
    * Depending on how the opponent played, it may be that all the remaining paths have already been shown to
    * be all losers.  In this case, the application will quit.
    *
    * @param pGameBoard
    * @return a move made by the application
    */
   @SuppressWarnings("JavaDoc")
   private IGameMove getGameMoveFromApplication(final IGameBoard pGameBoard)
   {
      /*
      Assumptions
      - There are three unique initial moves: corner, side, center.  All corner moves are, essentially, rotations
        of each other.  Therefore, always start in the same corner or side cell (when not starting in the center):
        Opening cells are: NW corner (cell 1), N side (cell 2), and Center (cell 5)

       */
      final TicTacToeBoard tttBoard = getTttBoard(pGameBoard);

      boolean isNewBoard = tttBoard.getCellsOfGivenStatus(CellStatusEnum.UNSELECTED).size() == TOTAL_CELL_COUNT;
      final CellStatusEnum machineSelection = CellStatusEnum.X_SELECTED;
      final IGameMove returnVal;
      if (isNewBoard)
      {
         int initialCellSelection = getInitialCellApplicationMove();
         returnVal = new TicTacToeMove(initialCellSelection, CellStatusEnum.X_SELECTED);
      }
      else
      {

         // Look for an acceptable next move
         final List<Integer> emptyCells = tttBoard.getCellsOfGivenStatus(CellStatusEnum.UNSELECTED);
         Integer acceptedMove = null;
         for (Integer possibleNewCell : emptyCells)
         {
            Optional<Integer> optionalCell = Optional.of(possibleNewCell);
            final String proposedMoveSet = getMoveSummaryConcise(tttBoard.getMoveHistory(), optionalCell);
            boolean isFoundInBadList = getMovesToAvoidSet().contains(proposedMoveSet);
            if (!isFoundInBadList)
            {
               acceptedMove = possibleNewCell;
               break;
            }
         }

         if (acceptedMove == null)
         {
            // Give up the game - no acceptable moves found
            returnVal = new TicTacToeMove(true);
         }
         else
         {
            returnVal = new TicTacToeMove(acceptedMove, machineSelection);
         }
      }
      return returnVal;
   }


   /**
    * @return a set with Moves to avoid.  Will never return null. The values of the set are strings of concatenated single digits,
    * each digit representing a single move by a player on a particular TicTacToe cell.
    */
   private Set<String> getMovesToAvoidSet()
   {
      if (fMovesToAvoid == null)
      {
         // it should never happen that fMovesToAvoid is null here, since we initialize it in the constructor
         // Still, as a matter of good form, we'll check for null here.
         initializeMovesToAvoidSet();
      }
      return fMovesToAvoid;
   }

   private void initializeMovesToAvoidSet()
   {
      fMovesToAvoid = getMovesToAvoidSetFromConciseLog();
   }


   /**
    * @return a cell number
    */
   private int getInitialCellApplicationMove()
   {
      /*
         There are, essentially, only three unique cells: corner, side, and center.  As such, we could
         restrict the moves to cells 1, 2 and 5 (NW Corner, North side, and center), and effectively play all
          the possibilities.

          However, always starting in one of these 3 cells, rather than anywhere on the board, would appear to
          the human player to be more... machine-like.  And since starting anywhere on the board will not affect
          the movesToAvoid that we calculate (since we're getting all rotational and mirror variants), we'll go
          ahead and start in any cell.
       */
      return getRandomInt(TOTAL_CELL_COUNT);
   }

   private int getRandomInt(final int pRandomRange)
   {
      return new Random().nextInt(pRandomRange) + 1;
   }

   /**
    * @param pGameBoard the game board being used
    * @return a move by this human.  Might possibly be a "Quit" move.
    */
   private IGameMove getGameMoveFromHuman(final IGameBoard pGameBoard)
   {

      IGameMove returnValue = null;
      int cellNumber;


      final Scanner scanner = new Scanner(System.in);
      RenderingHelper.renderOutputLine("");
      RenderingHelper.renderOutputLine("Your move!\n");
      while (returnValue == null)
      {
         boolean isNeedsExtraInstruction = false;
         RenderingHelper.renderOutputLine("Please enter the row and column of your next move, or 'q' to quit: ");
         final String inputLine = scanner.nextLine(); // does not appear that this will ever return null
         RenderingHelper.renderOutputLine("");
         //RenderingHelper.renderLoggingLine("getNextMove: input=" + inputLine);
         final boolean isQuit = "q".equalsIgnoreCase(inputLine.trim());
         if (isQuit)
         {
            returnValue = new TicTacToeMove(true);
            break;
         }
         else
         {
            // We'll actually accept a value separated by a commas or by a space, since 13% of our users will
            // use a comma anyways, despite our clear instructions.  There's no good reason to slap them on the wrist
            // for something so natural and acceptable.  Double check this with our UI folks, in case they disagree.
            String[] parts = inputLine.split(",");
            if (parts.length < 2)
            {
               //RenderingHelper.renderLoggingLine("getNextMove: split by comma, parts.length=" + parts.length);
               parts = inputLine.split(" ");
               if (parts.length < 2)
               {
                  //RenderingHelper.renderLoggingLine("getNextMove: split by space, parts.length=" + parts.length);
                  RenderingHelper.renderOutputLine("It doesn't seem that you've entered two numbers separated by a space.");
                  isNeedsExtraInstruction = true;
               }
            }

            if (parts.length >= 2)
            {
               try
               {
                  //RenderingHelper.renderLoggingLine("getNextMove: parts[0]=" + parts[0] + ".");
                  //RenderingHelper.renderLoggingLine("getNextMove: parts[1]=" + parts[1]+ ".");
                  int row = Integer.valueOf(parts[0].trim());
                  int column = Integer.valueOf(parts[1].trim());
                  if (row < 1 || row > BOARD_DIMENSION)
                  {
                     RenderingHelper.renderOutputLine("Sorry, " + row + "  is not a valid row number.");
                     isNeedsExtraInstruction = true;
                  }
                  else if (column < 1 || column > BOARD_DIMENSION)
                  {
                     RenderingHelper.renderOutputLine("Sorry, " + column + "  is not a valid column number.");
                     isNeedsExtraInstruction = true;
                  }
                  else
                  {
                     final TicTacToeBoard tttBoard = getTttBoard(pGameBoard);
                     cellNumber = getCellNumber(row, column);
                     if (tttBoard.getCellStatus(cellNumber) != CellStatusEnum.UNSELECTED)
                     {
                        RenderingHelper.renderOutputLine("Sorry, that spot's already taken - you'll have to select another one.");
                     }
                     else
                     {
                        returnValue = new TicTacToeMove(cellNumber, CellStatusEnum.O_SELECTED);
                     }
                  }
               }
               catch (Exception e)
               {
                  RenderingHelper.renderOutputLine("Sorry, that doesn't seem to be a valid value\n");
                  isNeedsExtraInstruction = true;
               }
            }

            // If we don't have something yet, give some more help to the user
            if (isNeedsExtraInstruction)
            {
               RenderingHelper.renderOutputLine("Please enter two numbers, each from 1 to 3, separated by a space.\n");
               RenderingHelper.renderOutputLine("The first number is for the row, and the second number is for the column.  \n");
               RenderingHelper.renderOutputLine("For example, to specify the center spot on the board:\n");
               RenderingHelper.renderOutputLine("  2 2\n");
            }
         }
      }

      return returnValue;
   }


   @Override
   public IGameBoard getNewGameBoard()
   {
      // If performance or memory becomes an issue, we could simply reset the existing game board, rather than create
      // a new object
      return new TicTacToeBoard();
   }


   /**
    * get the game status; for certain results
    *
    * @param pMostRecentPlayer the most recent player to make a move.  This parameter is not particularly used
    *                          by this method, but seems good to have in the contract, since for other games, unlike with TicTacToe, the most
    *                          recent player to have played *might* have done something to lose the game.
    * @param pGameBoard
    * @return the current game status
    */
   public GameStatusInfo getGameStatusInfo(
         final PlayerEnum pMostRecentPlayer, IGameBoard pGameBoard
   )
   {
      if (null == pGameBoard)
      {
         throw new IllegalArgumentException("game board may not be null");
      }
      if (null == pMostRecentPlayer)
      {
         throw new IllegalArgumentException("player may not be null");
      }

      final TicTacToeBoard tttBoard = getTttBoard(pGameBoard);
      GameStatusEnum gameStatus;
      if (tttBoard.isGameWon(CellStatusEnum.O_SELECTED))
      {
         gameStatus = GameStatusEnum.WON;
      }
      else if (tttBoard.isGameWon(CellStatusEnum.X_SELECTED))
      {
         gameStatus = GameStatusEnum.WON;
      }
      else if (tttBoard.getCellsOfGivenStatus(CellStatusEnum.UNSELECTED).size() == 0)
      {
         gameStatus = GameStatusEnum.TIE;
      }
      else
      {
         gameStatus = GameStatusEnum.ONGOING;
      }
      //
      return new GameStatusInfo(pMostRecentPlayer, gameStatus);
   }


   @Override
   public void renderFinalResults(final GameStatusInfo pGameStatusInfo, IGameBoard pGameBoard)
   {
      if (null == pGameBoard)
      {
         throw new IllegalArgumentException("game board may not be null");
      }
      if (null == pGameStatusInfo)
      {
         throw new IllegalArgumentException("GameStatusInfo may not be null");
      }

      GameStatusEnum gameStatusEnum = pGameStatusInfo.getGameStatus();
      if (gameStatusEnum == null)
      {
         throw new IllegalArgumentException("gameStatusEnum may not be null");
      }
      PlayerEnum finalPlayer = pGameStatusInfo.getFinalPlayer();
      if (finalPlayer == null)
      {
         throw new IllegalArgumentException("finalPlayer may not be null");
      }

      // Get the text to render
      final String gameOverLead = "************* GAME OVER: ";
      final String outputLine;
      if (gameStatusEnum == GameStatusEnum.WON)
      {
         if (finalPlayer == PlayerEnum.APPLICATION)
         {
            String pithyPhrase = WINNING_PHRASES[getRandomInt(WINNING_PHRASES.length) - 1];
            outputLine = gameOverLead + "I won this time! " + pithyPhrase;
         }
         else
         {
            String pithyPhrase = LOSING_PHRASES[getRandomInt(LOSING_PHRASES.length) - 1];
            outputLine = gameOverLead + "You won! " + pithyPhrase;
         }
      }
      else if (gameStatusEnum == GameStatusEnum.TIE)
      {
         String pithyPhrase = DRAW_PHRASES[getRandomInt(DRAW_PHRASES.length) - 1];
         outputLine = gameOverLead + "It's a Tie!  " + pithyPhrase;
      }
      else if (gameStatusEnum == GameStatusEnum.QUIT)
      {
         //
         if (finalPlayer == PlayerEnum.APPLICATION)
         {
            outputLine = gameOverLead + "I quit - in the past, when I've come to this point and tried all the available options, I lost every time.";
         }
         else
         {
            outputLine = gameOverLead + "Ok, we'll stop this particular game.";
         }
      }
      else if (gameStatusEnum == GameStatusEnum.ONGOING)
      {
         throw new IllegalStateException("Should not be rendering final status if the game is still ongoing");
      }
      else
      {
         // should never get here
         throw new IllegalStateException("current gameStatus not handled - gameStatusEnum=" + gameStatusEnum);
      }


      RenderingHelper.renderOutputLine(outputLine);
      RenderingHelper.renderOutputLine("");
      RenderingHelper.renderOutputLine("Here's how the game ended: ");
      renderBoard(pGameBoard);
   }


   @Override
   public void renderBoard(final IGameBoard pGameBoard)
   {
      if (null == pGameBoard)
      {
         throw new IllegalArgumentException("game board may not be null");
      }

      final TicTacToeBoard tttBoard = getTttBoard(pGameBoard);
      // In a real project, I'd probably use apache StringUtils.repeat() to do this
      final String horizontalLine = "------------";
      final String indent = "    ";
      RenderingHelper.renderOutputLine("\n");
      for (int cellNumber = 1; cellNumber <= 9; cellNumber++)
      {

         final boolean isNeedsLine = cellNumber == 4 || cellNumber == 7;
         if (isNeedsLine)
         {
            RenderingHelper.renderOutputLine("");
            RenderingHelper.renderOutputLine(horizontalLine);
         }
         final boolean isNewLineOfBoard = (cellNumber - 1) % 3 == 0;
         if (isNewLineOfBoard)
         {
            RenderingHelper.renderOutputFragment(indent);
         }
         else
         {
            RenderingHelper.renderOutputFragment("|");
         }
         RenderingHelper.renderOutputFragment(" " + tttBoard.getCellStatus(cellNumber).getRenderingValue() + " ");
      }
      RenderingHelper.renderOutputLine("\n");
   }

   private TicTacToeBoard getTttBoard(final IGameBoard pGameBoard)
   {
      if (!(pGameBoard instanceof TicTacToeBoard))
      {
         throw new IllegalArgumentException("given game board is not a ticTacToe board");
      }
      return (TicTacToeBoard) pGameBoard;
   }


   @Override
   public void introduceGameAndRules()
   {
      RenderingHelper.renderOutputLine("");
      RenderingHelper.renderOutputLine("Welcome to TicTacToe.  You'll be playing the computer (me!) today, so... good luck!");
      RenderingHelper.renderOutputLine("");
      RenderingHelper.renderOutputLine("When entering your moves, please enter two numbers separated by a space, representing the row and column of your selection. ");
      RenderingHelper.renderOutputLine("We're playing on a standard TicTacToe board, so your row and column numbers must be from 1 to 3.");
      RenderingHelper.renderOutputLine("For example, to specify the center space, enter:   2 2");
      RenderingHelper.renderOutputLine("");
      RenderingHelper.renderOutputLine("I'll be going first - I'm 'X', and you'll be 'O'.");
      RenderingHelper.renderOutputLine("");
   }

   /**
    * Save results if it's a loss
    *
    * @param pGameBoard      the game board
    * @param pGameStatusInfo the status of hte game
    */
   public void saveResults(IGameBoard pGameBoard, final GameStatusInfo pGameStatusInfo)
   {

      if (null == pGameBoard)
      {
         throw new IllegalArgumentException("game board may not be null");
      }
      if (null == pGameStatusInfo)
      {
         throw new IllegalArgumentException("GameStatusInfo may not be null");
      }
      final PlayerEnum finalPlayer = pGameStatusInfo.getFinalPlayer();
      if (finalPlayer == null)
      {
         throw new IllegalArgumentException("the final play may not be null here");
      }
      final GameStatusEnum gameStatus = pGameStatusInfo.getGameStatus();
      if (gameStatus == null)
      {
         throw new IllegalArgumentException("the game status may not be null");
      }

      final boolean isApplicationLost = finalPlayer == PlayerEnum.APPLICATION && (gameStatus == GameStatusEnum.QUIT);
      final boolean isHumanWon = finalPlayer == PlayerEnum.HUMAN && (gameStatus == GameStatusEnum.WON);
      boolean isRecordInfo = isApplicationLost || isHumanWon;
      if (isRecordInfo)
      {
         final TicTacToeBoard tttBoard = getTttBoard(pGameBoard);
         Optional<Integer> nullCell = Optional.ofNullable(null);

         final String moveSummary = getMoveSummaryConcise(tttBoard.getMoveHistory(), nullCell);
         // Remove the last move, which would've been from the Human, since that won't come into play when
         // we're actually using the data to decide what move to make (and what moves to avoid)
         final String moveSummaryWithoutFinalHumanMove = moveSummary.substring(0, moveSummary.length() - 1);

         /*
            When writing out a loss, write out all the possible combinations - 4 possible rotations, and flips
            on 4 axes.  That way, we'll avoid playing essentially similar games
         */
         final Set<String> moveSummaryVariants = getMoveSummaryVariants(moveSummaryWithoutFinalHumanMove);

         // Add the variants to the current set, and write them out to the file
         final Set<String> movesToAvoid = getMovesToAvoidSet();
         movesToAvoid.addAll(moveSummaryVariants);

         // Note: for now, we write out the entire file; it's quick to do so, since we never go beyond a few hundred
         // lines.  If performance became an issue, we could change this to append new lines rather than write the entire
         // file.
         writeLogFiles(movesToAvoid);
      }
   }

   /**
    * Get the corresponding rotational summaries, and the corresponding mirror summaries (reflected on various axes),
    * as well as the original
    *
    * @param pMoveSummary the original moveSummary
    * @return all variants of the moveSummary, including itself.
    */
   private Set<String> getMoveSummaryVariants(final String pMoveSummary)
   {
      Set<String> returnVal = new HashSet<>();
      returnVal.add(pMoveSummary);

      /*
         1 2 3
         4 5 6
         7 8 9
       */

      // Rotate
      int[] transform90 = {3, 6, 9, 2, 5, 8, 1, 4, 7};
      returnVal.add(transformString(pMoveSummary, transform90));
      int[] transform180 = {9, 8, 7, 6, 5, 4, 3, 2, 1};
      returnVal.add(transformString(pMoveSummary, transform180));
      int[] transform270 = {7, 4, 1, 8, 5, 2, 9, 6, 3};
      returnVal.add(transformString(pMoveSummary, transform270));

      // Flip on an axis
      int[] flipHorizontal = {7, 8, 9, 4, 5, 6, 1, 2, 3};
      returnVal.add(transformString(pMoveSummary, flipHorizontal));
      int[] flipVertical = {3, 2, 1, 6, 5, 4, 9, 8, 7};
      returnVal.add(transformString(pMoveSummary, flipVertical));
      int[] flipForwardDiagonal = {9, 6, 3, 8, 5, 2, 7, 4, 1};
      returnVal.add(transformString(pMoveSummary, flipForwardDiagonal));
      int[] flipBackwardsDiagonal = {1, 4, 7, 2, 5, 8, 3, 6, 9};
      returnVal.add(transformString(pMoveSummary, flipBackwardsDiagonal));

      return returnVal;
   }

   /**
    * Transform the given String, according to the given array.
    *
    * @param pMoveSummary    a String summary of the moves, in the form "3578"
    * @param pTransformArray an array that directs how to transform this summary
    * @return a transformed array
    */
   private String transformString(final String pMoveSummary, final int[] pTransformArray)
   {
      List<Integer> tempList = new ArrayList<>();
      for (int i = 0; i < pMoveSummary.length(); i++)
      {
         int oneBasedDigit = Integer.valueOf(pMoveSummary.substring(i, i + 1));
         final int zeroBasedArrayPointer = oneBasedDigit - 1;
         final int result = pTransformArray[(zeroBasedArrayPointer)];
         tempList.add(result);
      }
      return getStringFromList(tempList);
   }


   /**
    * Write the concise move summaries to a a log.  Overwrites the existing log files.
    * <p>
    * The concise summary of moves is what we really use; we write out the concise summary to the concise file, in order
    * to read it back in again the next time we start up.
    * <p>
    * The verbose file is never read back in by this current application; it's written out to satisfy the requirements
    * (perhaps some other application needs the data in that format?)
    *
    * @param pMoveSummaries
    */
   private void writeLogFiles(Set<String> pMoveSummaries)
   {
      try (
            Writer conciseWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FILE_NAME_CONCISE), "utf-8"));
            Writer verboseWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FILE_NAME_VERBOSE), "utf-8")))
      {
         for (String moveSummary : pMoveSummaries)
         {
            // Write out verbose entry to verbose file
            final String verboseRepresentation = getVerboseRepresentation(moveSummary);
            verboseWriter.write(verboseRepresentation);
            verboseWriter.write("\n");

            // Write out concise entry to concise file
            conciseWriter.write(moveSummary);
            conciseWriter.write("\n");
         }
      }
      catch (IOException e)
      {
         RenderingHelper.renderLoggingLine("writeLogFiles: problem writing. e=" + e);
      }
   }

   private String getVerboseRepresentation(final String moveSummary)
   {
      final StringBuilder sb = new StringBuilder();
      for (int i = 0; i < moveSummary.length(); i++)
      {
         int oneBasedDigit = Integer.valueOf(moveSummary.substring(i, i + 1));
         int row = getRow(oneBasedDigit);
         int column = getColumn(oneBasedDigit);
         //  line like this: (2,2)-(2,3)-(1,1)-(3,3)-(3,1)-(3,2)
         final String verboseSummary = "(" + row + "," + column + ")";
         if (sb.length() > 0)
         {
            sb.append("-");
         }
         sb.append(verboseSummary);
      }
      return sb.toString();
   }


   /**
    * @param pCellNumber
    * @return the row that corresponds to the given cell number
    */
   private int getRow(int pCellNumber)
   {
      return (pCellNumber / 3) + 1;
   }

   /**
    * @param pCellNumber
    * @return the column that corresponds to the given cell number
    */
   private int getColumn(int pCellNumber)
   {
      return pCellNumber % 3;
   }


   /**
    * @param pMoves                   the moves so far
    * @param pPossibleAdditionalValue Possible (optional) value
    * @return a String of digits, in order
    */
   private String getMoveSummaryConcise(final List<? extends IGameMove> pMoves,
                                        Optional<Integer> pPossibleAdditionalValue)
   {
      //RenderingHelper.renderLoggingLine("getMoveSummary: try with additionalValue=" + pPossibleAdditionalValue);
      List<Integer> cellList = pMoves.stream().map(gm -> ((TicTacToeMove) gm).getCellNumber())
/*
            .peek(num -> System.out.println("got in list num=" + num))
*/
            .collect(Collectors.toList());

      pPossibleAdditionalValue.ifPresent(cellList::add);

      return getStringFromList(cellList);
   }


   /**
    * Concatenate the given integers in a String
    *
    * @param pCellList
    * @return the String built from the given integers
    */
   private String getStringFromList(final List<Integer> pCellList)
   {
      StringBuilder sb = new StringBuilder();
      pCellList.forEach(sb::append);
      return sb.toString();
   }

   /**
    * @return the rows in the concise log, each as a separate String in the returned Set
    */
   private Set<String> getMovesToAvoidSetFromConciseLog()
   {
      Set<String> returnVal = new TreeSet<>();
      File conciseFile = new File(FILE_NAME_CONCISE);
      boolean isFileExists = conciseFile.exists();
      if (isFileExists)
      {
         try (
               final FileInputStream in = new FileInputStream(FILE_NAME_CONCISE);
               BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8")))
         {
            String thisLine;
            while ((thisLine = br.readLine()) != null)
            {
               returnVal.add(thisLine);
            }
         }
         catch (IOException e)
         {
            RenderingHelper.renderLoggingLine("error e=" + e);
         }
      }
      return returnVal;
   }


   /**
    * Get the cell number that corresponds to the given row and column
    *
    * @param pRow    a row number, from 1 to 3
    * @param pColumn a column number, from 1 to 3
    * @return a corresponding cell number
    */
   private int getCellNumber(final int pRow, final int pColumn)
   {
      return (pRow - 1) * 3 + pColumn;
   }

}
