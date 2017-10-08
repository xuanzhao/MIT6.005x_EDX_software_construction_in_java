package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.*;

import org.junit.Test;

public class SocialNetworkTest {

    /*
     * TODO: your testing strategies for these methods should go here.
     * Make sure you have partitions.
     *
     * Test GuessFollowsGraph()
     * 1. input empty List, get empty map
     * 2. input one list with no mention, get one key with empty value
     * 3. input one list with one mention, get one key map
     * 4. input one list with multi mentions, get multi keys map
     * 5. input multi list with multi mentions, get multi keys map
     *
     *
     * Test Influencers()
     * 1. input empty followsGraph, get empty influencers list
     * 2. input one key with one value followsGraph, get ordered influencers list by count
     * 3. input multi key followsGraph, get ordered influencers list by count
     */


    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T10:30:00Z");


    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "alySSA", "is it reasonable @ken so much?", d3);
    private static final Tweet tweet4 = new Tweet(3, "alySSA", "is it reasonable  @ken@91 so much?", d3);

    private static final Tweet tweet5 = new Tweet(3, "ken", "this is tweet 4 message@alyssa@91-akihiko", d3);

    private static final Tweet tweet6 = new Tweet(3, "ken", "this is tweet 5 message@alyssa@91", d3);
    private static final Tweet tweet7 = new Tweet(3, "91", "this is tweet 5 message@alyssa", d3);


    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testGuessFollowsGraphEmpty() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>());
        
        assertTrue("expected empty graph", followsGraph.isEmpty());
    }


    @Test
    public void testGuessFollowsGraphNoMention() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet1));

        assertEquals("expected result map key", makeSet("alyssa"), followsGraph.keySet());
        assertEquals("expected result map value", makeSet(), followsGraph.get("alyssa"));
    }

    @Test
    public void testGuessFollowsGraphOneMention() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet3));

        assertEquals("expected one key", 1, followsGraph.keySet().size());
        assertEquals("expected result map key", makeSet("alyssa"), followsGraph.keySet());
        assertEquals("expected result map value", makeSet("ken"), followsGraph.get("alyssa"));
    }


    @Test
    public void testGuessFollowsGraphMultiMentionsInOneTweet() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet4));

        assertEquals("expected one key", 1, followsGraph.keySet().size());
        assertEquals("expected result map key", makeSet("alyssa"), followsGraph.keySet());
        assertEquals("expected result map value", makeSet("ken", "91"), followsGraph.get("alyssa"));
    }


    @Test
    public void testGuessFollowsGraphMultiMentionsInMultiTweets() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet4, tweet5));

        assertEquals("expected one key", 2, followsGraph.keySet().size());
        assertEquals("expected result map key", makeSet("alyssa", "ken"), followsGraph.keySet());
        assertEquals("expected result map key alyssa's value", makeSet("ken", "91"), followsGraph.get("alyssa"));
        assertEquals("expected result map key ken's value", makeSet("91-akihiko"), followsGraph.get("ken"));
    }


    @Test
    public void testInfluencersEmpty() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("expected empty list", influencers.isEmpty());
    }

    @Test
    public void testInfluencersOneKey() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet5));
        List<String> influencers = SocialNetwork.influencers(followsGraph);

        System.out.println(followsGraph.values());
        assertEquals("expected list", Arrays.asList("alyssa", "91-akihiko"), influencers);
    }

    @Test
    public void testInfluencersMultiKeys() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet6, tweet7));
        List<String> influencers = SocialNetwork.influencers(followsGraph);

        assertEquals("expected list", Arrays.asList("alyssa", "91"), influencers);
    }

    /*
     * Warning: all the tests you write here must be runnable against any
     * SocialNetwork class that follows the spec. It will be run against several
     * staff implementations of SocialNetwork, which will be done by overwriting
     * (temporarily) your version of SocialNetwork with the staff's version.
     * DO NOT strengthen the spec of SocialNetwork or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in SocialNetwork, because that means you're testing a
     * stronger spec than SocialNetwork says. If you need such helper methods,
     * define them in a different class. If you only need them in this test
     * class, then keep them in this test class.
     */


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

    private static Set<String> makeSet(String... elements) {
        Set<String> set = new HashSet<>();
        for (String x : elements) {
            set.add(x);
        }
        return set;
    }
}
