package library;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test suite for Library ADT.
 */
@RunWith(Parameterized.class)
public class LibraryTest {

    /*
     * Note: all the tests you write here must be runnable against any
     * Library class that follows the spec.  JUnit will automatically
     * run these tests against both SmallLibrary and BigLibrary.
     */

    /**
     * Implementation classes for the Library ADT.
     * JUnit runs this test suite once for each class name in the returned array.
     *
     * @return array of Java class names, including their full package prefix
     */
    @Parameters(name = "{0}")
    public static Object[] allImplementationClassNames() {
        return new Object[]{
                "library.SmallLibrary",
                "library.BigLibrary"
        };
    }

    /**
     * Implementation class being tested on this run of the test suite.
     * JUnit sets this variable automatically as it iterates through the array returned
     * by allImplementationClassNames.
     */
    @Parameter
    public String implementationClassName;

    /**
     * @return a fresh instance of a Library, constructed from the implementation class specified
     * by implementationClassName.
     */
    public Library makeLibrary() {
        try {
            Class<?> cls = Class.forName(implementationClassName);
            return (Library) cls.newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    /*
     * Testing strategy
     * ==================
     *
     * TODO: your testing strategy for this ADT should go here.
     * Make sure you have partitions.
     *
     * 1. initial no books.
     * 2. add a book.
     * 3. add multi same book copy
     * 4. add different books
     * 5. checkout book
     * 6. check-in book
     * 7. lose book
     * 8. find exact title
     * 9. find exact author
     */

    private static Book book1;
    private static Book book2;
    private static Book book3;
    private static Book book4;
    private static Book book5;

    private static Library library;
    private static List<Book> booksToBuy;
    private static List<BookCopy> bookCopies;

    @Before
    public void setup() {
        book1 = new Book("title1", Lists.newArrayList("author1"), 2000);
        book2 = new Book("title1", Lists.newArrayList("author11"), 1900);  // the same as book1 except year
        book3 = new Book("title3", Lists.newArrayList("author1"), 1950);  // different from book1 but the same author
        book4 = new Book("title4", Lists.newArrayList("author1", "author4"), 1998);  // 2 authors including author1
        book5 = new Book("title: title5", Lists.newArrayList("author5"), 1999); // long title

        library = makeLibrary();
        booksToBuy = Lists.newArrayList(book1, book1, book1, book2, book3, book3, book4, book5);
        bookCopies = Lists.newArrayList();
    }

    private void buySomeBooks() {
        booksToBuy.forEach(book -> bookCopies.add(library.buy(book)));
    }

    // TODO: put JUnit @Test methods here that you developed from your testing strategy
    @Test
    public void testExampleTest() {
        Library library = makeLibrary();
        Book book = new Book("This Test Is Just An Example", Arrays.asList("You Should", "Replace It", "With Your Own Tests"), 1990);
        assertEquals(Collections.emptySet(), library.availableCopies(book));
    }


    @Test(expected = AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }


    @Test
    public void testBuyFirstCopy() {
        BookCopy bookCopy = library.buy(book1);
        assertTrue(library.isAvailable(bookCopy));
    }

    @Test
    public void testBuyMultiSameCopy() {
        BookCopy bookCopy1 = library.buy(book1);
        BookCopy bookCopy2 = library.buy(book1);

        assertFalse(bookCopy1 == bookCopy2);
        assertTrue(library.isAvailable(bookCopy1) && library.isAvailable(bookCopy2));
    }

    @Test
    public void testBuyDifferentCopy() {
        BookCopy bookCopy1 = library.buy(book1);
        BookCopy bookCopy2 = library.buy(book2);

        assertTrue(library.isAvailable(bookCopy1) && library.isAvailable(bookCopy2));
    }

    @Test
    public void testBuyManyCopys() {
        buySomeBooks();
        bookCopies.forEach(bookCopy -> assertTrue(library.isAvailable(bookCopy)));
        assertTrue(library.availableCopies(book1).size() == 3);
        assertTrue(library.availableCopies(book2).size() == 1);
        assertTrue(library.availableCopies(book3).size() == 2);
    }

    @Test
    public void testCheckOut() {
        buySomeBooks();

        BookCopy bookCopy1 = bookCopies.get(0);
        BookCopy bookCopy2 = bookCopies.get(1);
        BookCopy bookCopy3 = bookCopies.get(2);

        library.checkout(bookCopy1);
        assertFalse(library.isAvailable(bookCopy1));
        assertTrue(library.availableCopies(book1).size() == 2);
        assertTrue(library.availableCopies(book1).containsAll(Lists.newArrayList(bookCopy2, bookCopy3)));

        library.checkout(bookCopy2);
        assertFalse(library.isAvailable(bookCopy2));
        assertTrue(library.availableCopies(book1).size() == 1);
        assertTrue(library.availableCopies(book1).containsAll(Lists.newArrayList(bookCopy3)));

        library.checkout(bookCopy3);
        assertFalse(library.isAvailable(bookCopy3));
        assertTrue(library.availableCopies(book1).isEmpty());
    }

    @Test
    public void testCheckOutCheckIn() {
        buySomeBooks();

        BookCopy bookCopy1 = bookCopies.get(0);
        BookCopy bookCopy2 = bookCopies.get(1);
        BookCopy bookCopy3 = bookCopies.get(2);

        library.checkout(bookCopy1);
        library.checkout(bookCopy2);
        library.checkout(bookCopy3);

        assertTrue(library.availableCopies(book1).isEmpty());

        library.checkin(bookCopy1);
        assertTrue(library.isAvailable(bookCopy1));
        assertTrue(library.availableCopies(book1).size() == 1);

        library.checkin(bookCopy1);
        assertTrue(library.isAvailable(bookCopy1));
        assertTrue(library.availableCopies(book1).size() == 1);

        library.checkout(bookCopy1);
        library.checkin(bookCopy2);
        assertFalse(library.isAvailable(bookCopy1));
        assertTrue(library.isAvailable(bookCopy2));
        assertTrue(library.availableCopies(book1).size() == 1);
    }

    @Test
    public void testFindExactTitle() {
        buySomeBooks();

        List<Book> books1 = library.find(book1.getTitle());
        // newest year first
        assertEquals(books1, Lists.newArrayList(book1, book2));

        List<Book> books2 = library.find(book5.getTitle());
        assertEquals(books2, Lists.newArrayList(book5));
    }

    @Test
    public void testFindExactAuthor() {
        buySomeBooks();

        List<Book> books1 = library.find(book1.getAuthors().get(0));
//         newest year first
        assertEquals(books1, Lists.newArrayList(book1, book4, book3));

        List<Book> books2 = library.find(book4.getAuthors().get(1));
        assertEquals(books2, Lists.newArrayList(book4));
    }

    @Test
    public void testLoseBooks() {
        buySomeBooks();

        BookCopy bookCopy1 = bookCopies.get(0);
        BookCopy bookCopy2 = bookCopies.get(1);
        BookCopy bookCopy3 = bookCopies.get(2);

        assertTrue(library.isAvailable(bookCopy1));
        library.checkout(bookCopy1);
        assertFalse(library.isAvailable(bookCopy1));
        assertEquals(library.allCopies(book1).size(), 3);
        library.lose(bookCopy1);
        assertEquals(library.allCopies(book1).size(), 2);
        library.lose(bookCopy2);
        assertEquals(library.allCopies(book1).size(), 1);
    }

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}
