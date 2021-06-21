// CS 1501-Assignment 1
// Yichao Qiu
// yiq23@pitt.edu

import java.io.*;
import java.util.*;


public class Crossword
{
    private DictInterface D;
    private char [][] theBoard;
    private char [][] currentBoard;
    private int boardsize;
    private final char [] characters = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};

    public static void main(String [] args) throws IOException
    {
        new Crossword();
    }

    public Crossword() throws IOException
    {

        //Read the dictionary
        //
        Scanner inScan1 = new Scanner(System.in);
        Scanner fileScan;
        File dictName;
        String dictString;

        while (true)
        {
            try
            {
                System.out.println("Please enter dictionary filename:");
                dictString = inScan1.nextLine();
                dictName = new File(dictString);
                fileScan = new Scanner(dictName);
                break;
            }
            catch (IOException e)
            {
                System.out.println("Problem: " + e);
            }
        }
        String st;
        D = new MyDictionary();

        while (fileScan.hasNext())
        {
            st = fileScan.nextLine();
            D.add(st);
        }
        fileScan.close();

        // Parse input file of the board to create k-d grid of characters

        Scanner inScan2 = new Scanner(System.in);
        Scanner fReader;
        File fName;
        String fString = "";

        // Make sure the file name for the board is valid
        while (true)
        {
            try
            {
                System.out.println("Please enter board filename:");
                fString = inScan2.nextLine();
                fName = new File(fString);
                fReader = new Scanner(fName);
                boardsize = Integer.parseInt(fReader.nextLine());
                theBoard = new char[boardsize][boardsize];
                currentBoard = new char[boardsize][boardsize];
                break;
            }
            catch (IOException e)
            {
                System.out.println("Problem: " + e);
            }
        }
        inScan2.close();

        for (int i = 0; i < boardsize; i++)
        {
            String rowStr = fReader.nextLine();
            for(int j = 0; j < boardsize; j++)
            {
                theBoard[i][j] = rowStr.charAt(j);
            }
        }
        fReader.close();

        // Solve

        if( solve(0,0) == false){
            System.out.println("This board has no solution.");
        }
        else {
            System.out.println("Solution Found:");
            printBoard(theBoard);
            int letterpoints = letterPoints(theBoard);
            System.out.println("Score: " + letterpoints );
        }
    }

    private boolean solve(int row, int col)
    {

        // check whether the board is full
        boolean isFull= true;
        for (int i = 0; i < boardsize;i++)
        {
            for (int j = 0; j < boardsize; j++)
            {
                if (theBoard[i][j] == '+')
                {
                    row = i;
                    col = j;
                    isFull = false;
                    break;
                }
            }
            if (!isFull) break;
        }
        if (isFull) return true;

        for (char letter : characters)
        {
            if (isValid(row, col, letter))
            {
                theBoard[row][col] = letter;
                // Recursion here, from the first '+' to the last one, if cannot find a solution
                // go to else and remain '+', which will be detected by the isFull.
                if (solve(row,col))
                {
                    return true;
                }
                else {
                    theBoard[row][col] = '+';
                }
            }
        }
        return false;
    }


    private boolean isValid(int row, int col, char nLetter){
        StringBuilder curRow = new StringBuilder(boardsize);
        StringBuilder curCol = new StringBuilder(boardsize);
        // Setup the current Board for checking
        //
        for (int i =0; i < boardsize;i++){
            currentBoard[i][col]= theBoard[i][col];
            currentBoard[row][i]= theBoard[row][i];
        }
        currentBoard[row][col] = nLetter;

        for (int i=0;i<boardsize;i++){
            if (currentBoard[i][col] != '+' ){
                if ( i > 0 ){
                    if (currentBoard[i-1][col] != '+') {
                        curCol.append(currentBoard[i][col]);
                    }
                } else {
                    curCol.append(currentBoard[i][col]);
                }
            }

            if (currentBoard[row][i] != '+'){
                if ( i > 0 ){
                    if (currentBoard[row][i - 1] != '+') {
                        curRow.append(currentBoard[row][i]);
                    }
                } else {
                    curRow.append(currentBoard[row][i]);
                }
            }
        }
        int resultR = 0;
        int resultC = 0;
        boolean blockR =false;
        boolean blockC =false;
        int sColIndex = curCol.lastIndexOf("-");
        int sRowIndex = curRow.lastIndexOf("-");

        // Check the Col
        // '-' is at END of string
        if (sColIndex == curCol.length()-1){
            blockC = true;
            resultC = D.searchPrefix(curCol, 0, sColIndex - 1);
            // '-' is at START of string
        } else if (sColIndex == 0){
            blockC = true;
            resultC = D.searchPrefix(curCol, 1, curCol.length()-1);
            // '-' is NOT in string
        } else if (sColIndex == -1){
            resultC = D.searchPrefix(curCol, 0, curCol.length()-1);
        } else if (sColIndex != 0){
            // '-' is at MIDDLE of string
            if (sColIndex > boardsize/2 ){
                resultC = D.searchPrefix(curCol, 0, sColIndex - 1);
            } else {
                resultC = D.searchPrefix(curCol, sColIndex+1, curCol.length()-1);
            }
        }

        // Check the Row
        // '-' is at END of string
        if (sRowIndex == curRow.length()-1){
            blockR = true;
            resultR = D.searchPrefix(curRow, 0, sRowIndex - 1);
            // '-' is at START of string
        } else if (sRowIndex == 0){
            blockR = true;
            resultR = D.searchPrefix(curRow, 1, curRow.length()-1);
            // '-' is NOT in string
        } else if (sRowIndex == -1){
            resultR = D.searchPrefix(curRow, 0, curRow.length()-1);
        } else if (sRowIndex != 0){
            // '-' is at MIDDLE of string
            if (sRowIndex > boardsize/2 ){
                resultR = D.searchPrefix(curRow, 0, sRowIndex - 1);
            } else {
                resultR = D.searchPrefix(curRow, sRowIndex + 1, curRow.length()-1);
            }
        }

        // have block
        if (blockC )
        {   // must be word or prefix and word
            if ( resultC != 2 && resultC != 3 ){
                return false;
            }
        }

        if(row < boardsize -1 )
        {
            if ( resultC != 1 && resultC != 3 )
            {
                return false;
            }
        }
        // at the edge
        else { // must be word or prefix and word
            if ( resultC != 2 && resultC != 3 )
            {
                return false;
            }
        }

        // have block
        if (blockR)
        {      // must be word or prefix and word
            if ( resultR != 2 && resultR != 3 ){
                return false;
            }
        }

        if(col < boardsize-1)  {
            if ( resultR != 1 && resultR != 3 ){
                return false;
            }
        }
        // at the edge
        else {  // must be word or prefix and word
            if ( resultR != 2 && resultR != 3 ){
                return false;
            }
        }
        return true;
    }


    private void printBoard(char[][] theboard)
    {
        for (int i = 0; i < boardsize; i++)
        {
            for(int j = 0; j < boardsize; j++)
            {
                System.out.print(theboard[i][j]);
            }
            System.out.println();
        }
    }

    private int letterPoints(char[][] theboard) throws IOException
    {
        int letterpoints = 0;
        char[][] point;
        point = new char[26][3];
        // import the letterpoints file to calculate the point
        Scanner pointScan = new Scanner(new FileInputStream("letterpoints.txt"));
        for (int i = 0; i < 26; i++)
        {
            String rowStr = pointScan.nextLine();
            for(int j = 0; j < 3; j++)
            {
                point[i][j] = rowStr.charAt(j);
            }
        }
        pointScan.close();

        for (int i = 0; i < boardsize; i++) {
            for(int j = 0; j < boardsize; j++) {
                for(int n = 0; n < 26; n++) {
                    if(theboard [i][j] == Character.toLowerCase(point[n][0])) {
                        letterpoints += Integer.parseInt(String.valueOf(point[n][2]));
                    }
                }
            }
        }

        return letterpoints;
    }

}
