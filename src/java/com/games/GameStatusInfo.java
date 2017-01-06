package com.games;

/**
 * A simple wrapper to the games's status and final player (if any)
 */
public class GameStatusInfo
{


    private final PlayerEnum fFinalPlayer;
    private final GameStatusEnum fGameStatus;

    public GameStatusInfo(final PlayerEnum pFinalPlayer, final GameStatusEnum pGameStatus)
    {
        fFinalPlayer = pFinalPlayer;
        fGameStatus = pGameStatus;
    }

    /**
     *
     * @return null if no one is winning
     */
    public PlayerEnum getFinalPlayer()
    {
        return fFinalPlayer;
    }

    public GameStatusEnum getGameStatus()
    {
        return fGameStatus;
    }

    @Override
    public String toString()
    {
        return "GameStatusInfo{" +
              "fFinalPlayer=" + fFinalPlayer +
              ", fGameStatus=" + fGameStatus +
              '}';
    }
}
