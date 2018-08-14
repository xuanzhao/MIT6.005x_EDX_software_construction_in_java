/* Copyright (c) 2007-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 * TODO: Specification
 */
public class Board {
    private static final char UNTOUCHED = '-';
    private static final char FLAGGED = 'F';
    private static final char TOUCHED = ' ';
    private static final char MINE = 'B';

    private int boardXSize, boardYSize;
    private boolean[][] mines;
    private int[][] numbers;
    private boolean[][] open;
    private boolean[][] flags;

    // TODO: Abstraction function, rep invariant, rep exposure, thread safety
    // Abstraction function
    //    each cell could have or not have mine,
    //    each cell's state could be flagged, undigged, filled with number, blank.
    //    when dig a cell without mine, open it, and open its nearby cells which is satisfied nearby no any mine.
    //    when a cell is flagged, could unflagged it.
    // rep invariant
    //     “-” for squares with state untouched.
    //     “F” for squares with state flagged.
    //     “ ” (space) for squares with state dug and 0 neighbors that have a bomb.
    //     integer COUNT in range [1-8] for squares with state dug and COUNT neighbors that have a bomb.
    // thread safety
    //    any mutator be used sync lock to insure thread safety.
    // rep exposure
    //    board provide two get methods as observer, but do not offer any mutator method.

    // TODO: Specify, test, and implement in problem 2

    public Board(String filename) {
        try (Stream<String> linesStream = Files.lines(Paths.get(filename))) {
            Iterator<String> iterator = linesStream.iterator();
            String firstLine = iterator.next();
            String[] nm = firstLine.trim().split("\\s+");
            boardYSize = Integer.parseInt(nm[0]);
            boardXSize = Integer.parseInt(nm[1]);

            mines = new boolean[boardYSize][boardXSize];
            open = new boolean[boardYSize][boardXSize];
            flags = new boolean[boardYSize][boardXSize];
            numbers = new int[boardYSize][boardXSize];

            for (int i = 0; i < boardYSize; i++) {
                String line = iterator.next().trim().replaceAll("\\s+", "");

                if (line.length() != boardXSize) {
                    System.out.println(line.length());
                    throw new IllegalArgumentException("mine file " + filename + "less than " + boardXSize + " char");
                }
                for (int j = 0; j < boardXSize; j++) {
                    char c = line.charAt(j);
                    mines[i][j] = c != '0';
                    open[i][j] = false;
                    flags[i][j] = false;
                    numbers[i][j] = 0;
                }
            }
            calculateNumbers();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Board(int boardXSize, int boardYSize, Optional<Integer> mineNumber) {
        if (boardYSize <= 0 || boardXSize <= 0) {
            throw new IllegalArgumentException("Mine sweeper size is invalid!");
        }

        mineNumber = Optional.of(mineNumber.orElseGet(() -> (int) (boardXSize * boardYSize * 0.25)));
        if (mineNumber.get() < 0 || mineNumber.get() > boardXSize * boardYSize) {
            throw new IllegalArgumentException("Mine number is larger than the size of mine sweeper board!");
        }

        this.boardXSize = boardXSize;
        this.boardYSize = boardYSize;
        mines = new boolean[boardYSize][boardXSize];
        open = new boolean[boardYSize][boardXSize];
        flags = new boolean[boardYSize][boardXSize];
        numbers = new int[boardYSize][boardXSize];

        for (int i = 0; i < boardYSize; i++) {
            for (int j = 0; j < boardXSize; j++) {
                mines[i][j] = false;
                open[i][j] = false;
                flags[i][j] = false;
                numbers[i][j] = 0;
            }
        }

        buildMinesFromNumber(mineNumber.get());
        calculateNumbers();
    }

    public Board(int boardXSize, int boardYSize) {
        this(boardYSize, boardXSize, Optional.empty());
    }

    private void checkRep() {
    }


    private void calculateNumbers() {
        for (int i = 0; i < boardYSize; i++) {
            for (int j = 0; j < boardXSize; j++) {
                numbers[i][j] = 0;
                for (int ii = i - 1; ii <= i + 1; ii++) {
                    for (int jj = j - 1; jj <= j + 1; jj++) {
                        if (inArea(ii, jj) && mines[ii][jj]) {
                            numbers[i][j]++;
                        }
                    }
                }
            }
        }
    }

    private boolean inArea(int y, int x) {
        return y >= 0 && y < boardYSize && x >= 0 && x < boardXSize;
    }


    private void buildMinesFromNumber(int mineNumber) {

        Stream.iterate(0, x -> x + 1).limit(mineNumber).forEach(i -> {
            int y = i / boardXSize;
            int x = i % boardXSize;
            mines[y][x] = true;
        });

        for (int i = boardXSize * boardYSize - 1; i >= 0; i--) {
            int iY = i / boardXSize;
            int iX = i % boardXSize;

            int randNumber = (int) (Math.random() * (i + 1));

            int randY = randNumber / boardXSize;
            int randX = randNumber % boardXSize;

            swap(iX, iY, randX, randY);
        }
    }

    private void swap(int iX, int iY, int randX, int randY) {
        boolean b = mines[iX][iY];
        mines[iX][iY] = mines[randX][randY];
        mines[randX][randY] = b;
    }

    public synchronized void open(int y, int x) {
        if (!inArea(y, x)) {
            throw new IllegalArgumentException("Out of index in open funcion!");
        }

        if (isMine(y, x)) {
            throw new IllegalArgumentException("Cannot open an mine cell in open function!");
        }

        open[y][x] = true;

        if (numbers[y][x] > 0) {
            return;
        }

        for (int i = y - 1; i < y + 1; i++) {
            for (int j = x - 1; j < x + 1; j++) {
                if (inArea(i, j) && !open[i][j] && !mines[i][j]) {
                    open(i, j);
                }
            }
        }
    }

    private boolean isMine(int y, int x) {
        if (!inArea(y, x)) {
            throw new IllegalArgumentException("Out of index in isMine function!");
        }
        return mines[y][x];
    }

    public void print() {
        for (int i = 0; i < boardYSize; i++) {
            for (int j = 0; j < boardXSize; j++) {
                System.out.print(mines[i][j] ? MINE : String.valueOf(numbers[i][j]));
            }
            System.out.println();
        }
    }

    public String showBoardState() {
        StringBuilder boardState = new StringBuilder(boardXSize * boardYSize);
        for (int i = 0; i < boardYSize; i++) {
            for (int j = 0; j < boardXSize; j++) {
                if (flags[i][j]) {
                    boardState.append(FLAGGED);
                } else if (open[i][j] && mines[i][j]) {
                    boardState.append(MINE);
                } else if (open[i][j] && numbers[i][j] == 0) {
                    boardState.append(TOUCHED);
                } else if (open[i][j] && numbers[i][j] > 0) {
                    boardState.append(String.valueOf(numbers[i][j]));
                } else {
                    boardState.append(UNTOUCHED);
                }
            }
            boardState.append('\n');
        }
        return boardState.toString();
    }

    public synchronized void flag(int y, int x) {
        if (inArea(y, x)) {
            flags[y][x] = true;
        }
    }


    public synchronized void deflag(int y, int x) {
        if (inArea(y, x)) {
            flags[y][x] = false;
        }
    }

    public synchronized boolean dig(int y, int x) {
        if (inArea(y, x)) {
            if (isMine(y, x)) {
                System.out.println("Game over");
                open[y][x] = true;
                return true;
            } else {
                open(y, x);
            }
        }
        return false;
    }

    public int getBoardXSize() {
        return boardXSize;
    }

    public int getBoardYSize() {
        return boardYSize;
    }

    public static void main(String[] args) {
        Board board = new Board(5, 5);
//        Board board = new Board("/Users/ken/github/course_study/MIT6.005x_EDX_software_construction_in_java/ps5/src/resources/mine_4_3_1.txt");
        board.print();
    }


}
