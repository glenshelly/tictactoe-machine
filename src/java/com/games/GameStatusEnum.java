package com.games;

/**
 * Enum that represents the status of a particular game
 */
public enum GameStatusEnum
{

    // This doesn't declare *who* won the game; just that it was won by someone
    WON(true),
    ONGOING(false),
    TIE(true),
    QUIT(true);

    final boolean fGameOver;

    GameStatusEnum(final boolean pGameOver)
    {
        fGameOver = pGameOver;
    }

    public boolean isGameOver()
    {
        return fGameOver;
    }

}
