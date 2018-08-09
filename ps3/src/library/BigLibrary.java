package library;

import com.google.common.collect.Sets;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * BigLibrary represents a large collection of books that might be held by a city or
 * university library system -- millions of books.
 * <p>
 * In particular, every operation needs to run faster than linear time (as a function of the number of books
 * in the library).
 */
public class BigLibrary implements Library {

    // TODO: rep
    private final Set<BookCopy> inLibrary;
    private final Set<BookCopy> checkOut;
    private final Set<Book> allBooks;

    // TODO: rep invariant
    // the intersection of inLibrary and checkedOut is the empty set, inLibrary and checkOut should be some ordered.
    // thus Library is defined as mutable type, so we should not override equals() and hashCode() methods.

    // TODO: abstraction function
    //     represents the collection of books inLibrary union checkedOut,
    //       where if a book copy is in inLibrary then it is available,
    //       and if a copy is in checkedOut then it is checked out.

    // TODO: safety from rep exposure argument
    // mutable reference has implemented by defend copy.
    // rep is private.

    public BigLibrary() {
        inLibrary = Sets.newConcurrentHashSet();
        checkOut = Sets.newConcurrentHashSet();
        allBooks = Sets.newConcurrentHashSet();
    }

    // assert the rep invariant
    private void checkRep() {
        assert inLibrary != null;
        assert checkOut != null;
        assert allBooks != null;
        assert Sets.intersection(inLibrary, checkOut).isEmpty();
    }

    public Set<BookCopy> getInLibrary() {
        return Sets.newHashSet(inLibrary);
    }

    public Set<BookCopy> getCheckOut() {
        return Sets.newHashSet(checkOut);
    }

    public Set<Book> getAllBooks() {
        return Sets.newHashSet(allBooks);
    }

    @Override
    public BookCopy buy(Book book) {
        BookCopy bookCopy = new BookCopy(book);
        inLibrary.add(bookCopy);
        allBooks.add(book);

        return bookCopy;
    }

    @Override
    public void checkout(BookCopy copy) {
        if (isAvailable(copy)) {
            inLibrary.remove(copy);
            checkOut.add(copy);
            checkRep();
        }
    }

    @Override
    public void checkin(BookCopy copy) {
        if (checkOut.contains(copy)) {
            checkOut.remove(copy);
            inLibrary.add(copy);
            checkRep();
        }
    }

    @Override
    public Set<BookCopy> allCopies(Book book) {
        return Sets.union(availableCopies(book), checkOutCopies(book));
    }

    @Override
    public Set<BookCopy> availableCopies(Book book) {
        return inLibrary.parallelStream()
                .filter(bookCopy -> bookCopy.getBook().equals(book))
                .collect(Collectors.toSet());
    }

    private Set<BookCopy> checkOutCopies(Book book) {
        return checkOut.parallelStream()
                .filter(bookCopy -> bookCopy.getBook().equals(book))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isAvailable(BookCopy copy) {
        return inLibrary.contains(copy);
    }

    @Override
    public List<Book> find(String query) {
        return allBooks.parallelStream()
                .filter(book -> book.getTitle().equals(query) || book.getAuthors().contains(query))
                .sorted(Comparator.comparing(Book::getYear).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public void lose(BookCopy copy) {
        if (inLibrary.contains(copy)) {
            inLibrary.remove(copy);
        }
        if (checkOut.contains(copy)) {
            checkOut.remove(copy);
        }
        if (allCopies(copy.getBook()).isEmpty()) {
            allBooks.remove(copy.getBook());
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
