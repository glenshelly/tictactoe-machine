package com.games;

/**
 * Helper class for rendering information to the user or to the log files (for debugging)
 *
 * Abstracts the details of rendering away from other particular classes
 *
 * For now, will simply render to System.out
 *
 */
public class RenderingHelper
{


    /**
     * Render the line to the user.  At present, simply renders the text to System.out.
     *
     * @param pString a String to render to the user
     */
    public static void renderOutputLine(final String pString) {
        // Refactor this, depending on how we wish to render the data to the user
        System.out.println("    " + pString);
    }

    /**
     * Render a fragment of text, without a line feed
     * @param pString
     */
    public static void renderOutputFragment(final String pString) {
        // Refactor this, depending on how we wish to render the data to the user
        System.out.print(pString);
    }

    /**
     * Render the give message to an internal log.  At present, simply renders the text to System.out.
     *
     * @param pLoggingString a String to render to our logging mechanism
     */
    public static void renderLoggingLine(final String pLoggingString) {
        // Refactor this, depending on how we render logging messages
        System.out.println("LOGGING: " + pLoggingString);
    }



}
