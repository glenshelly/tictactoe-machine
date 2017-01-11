package com.games;

/**
 * Contract for a particular move
 */
public interface IGameMove
{
   /**
    *
    * @return true if the player's move is, in effect, to decide to stop playing
    */
   boolean isChooseToStop();

}
