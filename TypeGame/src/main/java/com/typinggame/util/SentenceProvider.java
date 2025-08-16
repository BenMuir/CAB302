package com.typinggame.util;

/**
 * SentenceProvider is a utility class that supplies target sentences
 * for the typing game. It currently returns a single hardcoded sentence,
 * but it's designed to be easily expandable to support multiple sentences,
 * difficulty levels, or even external data sources. [Ben M - Aug 16 2025]
 */
public class SentenceProvider {

    /**
     * Returns a sentence for the typing challenge.
     * Currently, returns a fixed pangram (contains every letter of the alphabet).
     *
     * @return A sentence for the user to type. [Ben M - Aug 16 2025]
     */
    public static String getSentence() {
        return "The quick brown fox jumps over the lazy dog.";
    }


    // Future Expansions [Ben M - Aug 16 2025]

    // API Integration:
    //    - Fetch sentences from an online source or REST API.
    //    - Could support difficulty levels, categories, or daily challenges.
    //
    // Difficulty Modes:
    //    - Organize sentences by difficulty (easy, medium, hard).
    //    - Add a method like getSentence(String difficulty) to support this.
    //
    // Load from External File:
    //    - Read sentences from a text file (e.g. sentences.txt).
    //    - Useful for large sentence pools or user-generated content.


}