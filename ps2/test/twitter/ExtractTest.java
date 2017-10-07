package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class ExtractTest {

    /*
     * TODO: your testing strategies for these methods should go here.
     * Make sure you have partitions.
     *
     * Test GetTimespanTwoTweets()
     * 1. input two tweets
     * 2. input three tweets
     * return {start=end, start < end}
     *
     * Test GetMentionedUsers()
     * 1. input has no mentionedUsers
     * 2. input has one mentionedUsers
     * 3. input has multi mentionedUsers
     * 4. input has include illegal mentionedUsers
     * return {null is true, Set<String> with one element, Set<String> with multi elements}
     *
     *
     * Each of these parts is covered by at least one test case below.
     */

    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T10:30:00Z");

    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "ken", "this is tweet 3 message@ken", d3);
    private static final Tweet tweet4 = new Tweet(3, "ken", "this is tweet 4 message@ken@91-akihiko", d3);

    private static final Tweet tweet5 = new Tweet(3, "ken", "this is tweet 5 message@ken.waseda.jp", d3);
    private static final Tweet tweet6 = new Tweet(3, "ken", "this is tweet 5 message@ken@KEN", d3);


    @Test(expected = AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    @Test
    public void testGetTimespanTwoTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2));

        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d2, timespan.getEnd());
    }

    @Test
    public void testGetTimespanOneTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1));

        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d1, timespan.getEnd());
    }

    @Test
    public void testGetTimespanThreeTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2, tweet3));

        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d2, timespan.getEnd());
    }

//    @Test
//    public void testGetTimespanZeroTweets() {
//        Timespan timespan = Extract.getTimespan(Arrays.asList());
//
//        assertEquals("expected start", null, timespan.getStart());
//        assertEquals("expected end", null, timespan.getEnd());
//    }

    @Test
    public void testGetMentionedUsersNoMention() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet1));

        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }

    @Test
    public void testGetMentionedUsersOneLegalMention() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet1, tweet2, tweet3));

        assertEquals("expected set", makeSet("ken"), mentionedUsers);
    }

    @Test
    public void testGetMentionedUsersMultiLegalMention() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet1, tweet2, tweet3, tweet4));

        assertEquals("expected set", makeSet("ken", "91-akihiko"), mentionedUsers);
    }

    @Test
    public void testGetMentionedUsersIncludeIllegalMention() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet1, tweet2, tweet3, tweet4, tweet5));

        assertEquals("expected set", makeSet("ken", "91-akihiko"), mentionedUsers);
    }

    @Test
    public void testGetMentionedUsersCaseInsensiveMention() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet1, tweet3, tweet6));
        assertEquals("expected set", makeSet("ken"), mentionedUsers);
    }

    private static Set<String> makeSet(String... elements) {
        Set<String> set = new HashSet<>();
        for (String x : elements) {
            set.add(x);
        }
        return set;
    }
    /*
     * Warning: all the tests you write here must be runnable against any
     * Extract class that follows the spec. It will be run against several staff
     * implementations of Extract, which will be done by overwriting
     * (temporarily) your version of Extract with the staff's version.
     * DO NOT strengthen the spec of Extract or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Extract, because that means you're testing a
     * stronger spec than Extract says. If you need such helper methods, define
     * them in a different class. If you only need them in this test class, then
     * keep them in this test class.
     */


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}
