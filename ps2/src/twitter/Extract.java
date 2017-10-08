package twitter;


import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extract consists of methods that extract information from a list of tweets.
 *
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class Extract {

    /**
     * Get the time period spanned by tweets.
     *
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return a minimum-length time interval that contains the timestamp of
     *         every tweet in the list.
     */
    public static Timespan getTimespan(List<Tweet> tweets) {
//        throw new RuntimeException("not implemented");

        Instant minTimestamps = tweets.get(0).getTimestamp();
        Instant maxTimestamps = tweets.get(0).getTimestamp();

        for (Tweet t : tweets) {
            Instant curTweetTimestamp = t.getTimestamp();
            if (curTweetTimestamp.isBefore(minTimestamps)) {
                minTimestamps = curTweetTimestamp;
            } else if (curTweetTimestamp.isAfter(maxTimestamps)) {
                maxTimestamps = curTweetTimestamp;
            }
        }
        return new Timespan(minTimestamps, maxTimestamps);
    }

    /**
     * Get usernames mentioned in a list of tweets.
     *
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return the set of usernames who are mentioned in the text of the tweets.
     *         A username-mention is "@" followed by a Twitter username (as
     *         defined by Tweet.getAuthor()'s spec).
     *         The username-mention cannot be immediately preceded or followed by any
     *         character valid in a Twitter username.
     *         For this reason, an email address like bitdiddle@mit.edu does NOT
     *         contain a mention of the username mit.
     *         Twitter usernames are case-insensitive, and the returned set may
     *         include a username at most once.
     */
    public static Set<String> getMentionedUsers(List<Tweet> tweets) {
        Set<String> mentionedUsers = new HashSet<>();

        Pattern pattern = Pattern.compile("@([\\w_-]+[.]*)*", Pattern.CASE_INSENSITIVE);
        for (Tweet t : tweets) {
            String text = t.getText();
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                String result = matcher.group();
                if (!result.contains(".")) {
                    for (String s : result.split("@")) {
                        if (! mentionedUsers.contains(s.toLowerCase()))
                            mentionedUsers.add(s.toLowerCase());
                    }
                }
            }
        }
        mentionedUsers.remove("");
        return  mentionedUsers;

//        throw new RuntimeException("not implemented");
    }

    /* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */


    public static void main(String[] args) {
        Instant now = Instant.now();
        System.out.println(now);
        Instant truncated = now.truncatedTo(ChronoUnit.HOURS);
        System.out.println(truncated);
        System.out.println(now.getEpochSecond());
        System.out.println(now.compareTo(truncated));
        System.out.println(now.compareTo(now));
        System.out.println(truncated.compareTo(now));
        System.out.println(now.isAfter(truncated));

        Instant d3 = Instant.parse("2016-02-17T10:30:00Z");
        Tweet tweet3 = new Tweet(3, "ken", "this is tweet 3 message@ken", d3);
        Tweet tweet4 = new Tweet(3, "ken", "this is tweet 4 message@ken@91-akihiko", d3);
        Tweet tweet5 = new Tweet(3, "ken", "this is tweet 5 message@ken.waseda.jp", d3);
        String text3 = tweet3.getText();
        String text4 = tweet4.getText();
        String text5 = tweet5.getText();
        System.out.println(text4);
        System.out.println(text5);
        HashSet<String> mentionedUsers = new HashSet<>();
        Pattern pattern = Pattern.compile("@([\\w_-]+[.]*)*",  Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text4);

        while (matcher.find()) {
            System.out.print("Start index:" + matcher.start());
            System.out.print(" End index:" + matcher.end() + " ");
            System.out.println("group count:" + matcher.groupCount());
            System.out.println(matcher.group());
            String result = matcher.group();
            if (result.contains(".")) {
                System.out.println("not illegal user " + result);
            } else {
                for (String s : result.split("@")) {
                    System.out.println(s);
                    mentionedUsers.add(s);
                }
            }
            System.out.println(mentionedUsers);
//            System.out.println(result.contains("."));
//            System.out.println(result.split("@").toString());
        }
        mentionedUsers.remove("");
        System.out.println(mentionedUsers);

        System.out.println("abd ".contains(""));
    }

}
