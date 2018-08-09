package library;

import java.util.Collections;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test suite for Book ADT.
 */
public class BookTest {

    /*
     * Testing strategy
     * ==================
     *
     * TODO: your testing strategy for this ADT should go here.
     * Make sure you have partitions.
     * 1. title = {empty, one space character, other characters}
     * 2. authors = {empty list, contain only one space in list, not contain only one space element in list}
     * 3. year = {empty, negative number, not a calender number, calender number}
     *
     * for each test Methods, test rep invariant, then test safety from rep exposure argument
     */

    // TODO: put JUnit @Test methods here that you developed from your testing strategy
    private static Book book1;
    private static Book book2;
    private static Book book3;
    private static Book book4;

    @Before
    public void setup() {
        String title1 = "ken";
        String title2 = "Ken";
        String title3 = "91";
        String title4 = "ken@91";
        List<String> authors1 = Lists.newArrayList("a");
        List<String> authors2 = Lists.newArrayList("A");
        List<String> authors3 = Lists.newArrayList("a", "b");
        List<String> authors4 = Lists.newArrayList("b", "a");
        int year1 = 2010;

        book1 = new Book(title1, authors1, year1);
        book2 = new Book(title2, authors2, year1);
        book3 = new Book(title3, authors3, year1);
        book4 = new Book(title4, authors4, year1);
    }

    @Test
    public void testExampleTest() {
        Book book = new Book("This Test Is Just An Example", Arrays.asList("You Should", "Replace It", "With Your Own Tests"), 1990);
        assertEquals("This Test Is Just An Example", book.getTitle());
    }

    @Test(expected = AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    @Test(expected = AssertionError.class)
    public void checkRepEmptyTitle() {
        Book book = new Book(" ", Lists.newArrayList("author1"), 2000);
    }

    @Test(expected = AssertionError.class)
    public void checkRepEmptyAuthors() {
        Book book = new Book("title", Lists.newArrayList("author1", " "), 2000);
    }

    @Test(expected = AssertionError.class)
    public void checkRepYear() {
        Book book = new Book("title", Lists.newArrayList("author1"), -1111111);
    }

    @Test
    public void testEqualBooks() {
        Book book1 = new Book("title", Lists.newArrayList("author"), 2000);
        Book book2 = new Book("title", Lists.newArrayList("author"), 2000);
        assertEquals(book1, book2);
    }

    @Test
    public void testNotEqualsBooksYear() {
        Book book1 = new Book("title", Lists.newArrayList("author"), 2000);
        Book book2 = new Book("title", Lists.newArrayList("author"), 2001);
        assertNotEquals(book1, book2);
    }

    @Test
    public void testNotEqualsBooksCaseSensitiveAuthor() {
        Book book1 = new Book("title", Lists.newArrayList("author"), 2000);
        Book book2 = new Book("title", Lists.newArrayList("authoR"), 2000);
        assertNotEquals(book1, book2);
    }

    @Test
    public void testNotEqualsBooksCaseSensitiveTitle() {
        Book book1 = new Book("TITLE", Lists.newArrayList("author"), 2000);
        Book book2 = new Book("title", Lists.newArrayList("author"), 2000);
        assertNotEquals(book1, book2);
    }

    @Test
    public void testNotEqualsBooksAuthorOrder() {
        Book book1 = new Book("title", Lists.newArrayList("author1", "author2"), 2000);
        Book book2 = new Book("title", Lists.newArrayList("author2", "author1"), 2000);
        assertNotEquals(book1, book2);
    }

    @Test
    public void testTitle() {
        assertEquals("the expected title is ", "ken", book1.getTitle());
        assertFalse("the Alphabetic case cause difference books", book1.getTitle().equals(book2.getTitle()));
        assertEquals("number is also legal title", "91", book3.getTitle());
        assertEquals("contain spec character is also legal title", "ken@91", book4.getTitle());
    }

    @Test
    public void testAuthors() {
        assertEquals("the expected author list is ", Lists.newArrayList("a"), book1.getAuthors());
        assertFalse("the Alphabetic case cause different books", book1.getAuthors().equals(book2.getAuthors()));
        assertFalse("the authors order cause different books", book3.getAuthors().equals(book4.getAuthors()));
    }

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}
