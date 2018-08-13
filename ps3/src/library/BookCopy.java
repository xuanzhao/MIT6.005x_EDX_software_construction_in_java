package library;

import java.util.Objects;

/**
 * BookCopy is a mutable type representing a particular copy of a book that is held in a library's
 * collection.
 */
public class BookCopy {

    // TODO: rep
    private final Book book;
    private Condition condition;

    // TODO: rep invariant
    /* book title, authors, year must be consist with Book class.
     * book copy only two defined conditions.
     * thus we define BookCopy is a mutable type, so it should only have behavior equality.
     */
    // TODO: abstraction function
    /* represent a copy of book, attach the copy with condition and id.
     */

    // TODO: safety from rep exposure argument
    /* mutable reference has implement by defend copy, rep is private final.
     */

    public static enum Condition {
        GOOD, DAMAGED;
    }

    ;

    /**
     * Make a new BookCopy, initially in good condition.
     *
     * @param book the Book of which this is a copy
     */
    public BookCopy(Book book) {
        this.book = new Book(book);
        this.condition = Condition.GOOD;
        checkRep();
    }

    // assert the rep invariant
    private void checkRep() {
        assert condition == Condition.GOOD || condition == Condition.DAMAGED;
    }

    /**
     * @return the Book of which this is a copy
     */
    public Book getBook() {
        return new Book(book);
    }

    /**
     * @return the condition of this book copy
     */
    public Condition getCondition() {
        return condition;
    }

    /**
     * Set the condition of a book copy.  This typically happens when a book copy is returned and a librarian inspects it.
     *
     * @param condition the latest condition of the book copy
     */
    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    /**
     * @return human-readable representation of this book that includes book.toString()
     * and the words "good" or "damaged" depending on its condition
     */
    @Override
    public String toString() {
        return "BookCopy{" +
                "book=" + book +
                ", condition=" + condition.toString().toLowerCase() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookCopy bookCopy = (BookCopy) o;
        return Objects.equals(book, bookCopy.book) &&
                condition == bookCopy.condition;
    }


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}
