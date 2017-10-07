package twitter;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

/**
 * Filter consists of methods that filter a list of tweets for those matching a
 * condition.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class Filter {

    /**
     * Find tweets written by a particular user.
     * 
     * @param tweets
     *            a list of tweets with distinct ids, not modified by this method.
     * @param username
     *            Twitter username, required to be a valid Twitter username as
     *            defined by Tweet.getAuthor()'s spec.
     * @return all and only the tweets in the list whose author is username,
     *         in the same order as in the input list.
     */
    public static List<Tweet> writtenBy(List<Tweet> tweets, String username) {
        List<Tweet> ts = new LinkedList<>();
        if (username.contains(".")) {
            return ts;
        }
        if (! username.matches("[\\w_-]+")) {
            return ts;
        }

        for (Tweet t : tweets) {
            String author = t.getAuthor();
            if (author.toLowerCase().equals(username.toLowerCase())) {
                ts.add(t);
            }
        }
        return ts;

//        throw new RuntimeException("not implemented");

    }

    /**
     * Find tweets that were sent during a particular timespan.
     * 
     * @param tweets
     *            a list of tweets with distinct ids, not modified by this method.
     * @param timespan
     *            timespan
     * @return all and only the tweets in the list that were sent during the timespan,
     *         in the same order as in the input list.
     */
    public static List<Tweet> inTimespan(List<Tweet> tweets, Timespan timespan) {
        List<Tweet> ts = new LinkedList<>();
        Instant startTime = timespan.getStart();
        Instant endTime = timespan.getEnd();
        if (startTime.equals(endTime)) {
            return ts;
        }

        for (Tweet t : tweets) {
            Instant timestamp = t.getTimestamp();
            if ((timestamp.equals(startTime) | timestamp.isAfter(startTime)) &
                    (timestamp.equals(endTime) | timestamp.isBefore(endTime))) {
                ts.add(t);
            }
        }
        return ts;
//        throw new RuntimeException("not implemented");
    }

    /**
     * Find tweets that contain certain words.
     * 
     * @param tweets
     *            a list of tweets with distinct ids, not modified by this method.
     * @param words
     *            a list of words to search for in the tweets. 
     *            A word is a nonempty sequence of nonspace characters.
     * @return all and only the tweets in the list such that the tweet text (when 
     *         represented as a sequence of nonempty words bounded by space characters 
     *         and the ends of the string) includes *at least one* of the words 
     *         found in the words list. Word comparison is not case-sensitive,
     *         so "Obama" is the same as "obama".  The returned tweets are in the
     *         same order as in the input list.
     */
    public static List<Tweet> containing(List<Tweet> tweets, List<String> words) {
        List<Tweet> ts = new LinkedList<>();
        if (words.isEmpty()) {
            return ts;
        }

        for (Tweet t : tweets) {
            String text = t.getText().toLowerCase();
            if (text.isEmpty()) break;
            for (String word : words) {
                if (!word.equals(" ") & !word.equals("") & text.contains(word.toLowerCase())) {
                    ts.add(t);
                    break;
                }
            }
        }

        return ts;
//        throw new RuntimeException("not implemented");
    }

    /* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
