package library;

import com.google.common.collect.Sets;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * SmallLibrary represents a small collection of books, like a single person's home collection.
 */
public class SmallLibrary implements Library {

    // This rep is required! 
    // Do not change the types of inLibrary or checkedOut, 
    // and don't add or remove any other fields.
    // (BigLibrary is where you can create your own rep for
    // a Library implementation.)

    // rep
    private final Set<BookCopy> inLibrary;
    private final Set<BookCopy> checkedOut;

    // rep invariant:
    //    the intersection of inLibrary and checkedOut is the empty set
    //    thus Library is defined as mutable type, so we should not override equals() and hashCode() methods.

    // abstraction function:
    //    represents the collection of books inLibrary union checkedOut,
    //      where if a book copy is in inLibrary then it is available,
    //      and if a copy is in checkedOut then it is checked out

    // TODO: safety from rep exposure argument
    /* mutable reference has implemented by defend copy.
     * rep is private.
     */

    public SmallLibrary() {
        inLibrary = Sets.newHashSet();
        checkedOut = Sets.newHashSet();
        checkRep();
    }

    // assert the rep invariant
    private void checkRep() {
        assert inLibrary != null;
        assert checkedOut != null;
        Sets.SetView<BookCopy> intersection = Sets.intersection(inLibrary, checkedOut);
        assert intersection.isEmpty();
    }

    public Set<BookCopy> getInLibrary() {
        return Sets.newHashSet(inLibrary);
    }

    public Set<BookCopy> getCheckedOut() {
        return Sets.newHashSet(checkedOut);
    }

    @Override
    public BookCopy buy(Book book) {
        BookCopy bookCopy = new BookCopy(book);
        inLibrary.add(bookCopy);
        return bookCopy;
    }

    @Override
    public void checkout(BookCopy copy) {
        synchronized (this) {
            if (isAvailable(copy)) {
                inLibrary.remove(copy);
                checkedOut.add(copy);
                checkRep();
            }
        }
    }

    @Override
    public void checkin(BookCopy copy) {
        synchronized (this) {
            if (checkedOut.contains(copy)) {
                checkedOut.remove(copy);
                inLibrary.add(copy);
                checkRep();
            }
        }
    }

    @Override
    public boolean isAvailable(BookCopy copy) {
        return inLibrary.contains(copy);
    }

    @Override
    public Set<BookCopy> allCopies(Book book) {
        return Sets.union(availableCopies(book), checkoutCopies(book));
    }

    @Override
    public Set<BookCopy> availableCopies(Book book) {
        return inLibrary.stream()
                .filter(bookCopy -> bookCopy.getBook().equals(book))
                .collect(Collectors.toSet());
    }

    public Set<BookCopy> checkoutCopies(Book book) {
        return checkedOut.stream()
                .filter(bookCopy -> bookCopy.getBook().equals(book))
                .collect(Collectors.toSet());
    }

    private Set<Book> allBooksInLibrary() {
        return Sets.union(inLibrary, checkedOut)
                .stream()
                .map(BookCopy::getBook)
                .collect(Collectors.toSet());
    }

    @Override
    public List<Book> find(String query) {
        return allBooksInLibrary().stream()
                .filter(book -> book.getTitle().equals(query) || book.getAuthors().contains(query))
                .sorted(Comparator.comparing(Book::getYear).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public void lose(BookCopy copy) {
        if (isAvailable(copy)) {
            inLibrary.remove(copy);
        }

        if (checkedOut.contains(copy)) {
            checkedOut.remove(copy);
        }

        checkRep();
    }

    // uncomment the following methods if you need to implement equals and hashCode,
    // or delete them if you don't
    // @Override
    // public boolean equals(Object that) {
    //     throw new RuntimeException("not implemented yet");
    // }
    // 
    // @Override
    // public int hashCode() {
    //     throw new RuntimeException("not implemented yet");
    // }


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
