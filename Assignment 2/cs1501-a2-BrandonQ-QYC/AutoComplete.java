// CS 1501-Assignment 2
// Yichao Qiu
// yiq23@pitt.edu

import java.io.*;
import java.util.*;

public class AutoComplete{
 // private static final char SENTINEL = '^';
  private DLBNode root;
  //TO-DO: Add instance variable: you should have at least the tree root

  public AutoComplete(String dictFile) throws java.io.IOException {
    root = null;
    //TO-DO Initialize the instance variables
    Scanner fileScan = new Scanner(new FileInputStream(dictFile));
    while(fileScan.hasNextLine()){
      StringBuilder word = new StringBuilder(fileScan.nextLine());
      add(word);
      //TO-DO call the public add method or the private helper method if you have one
    }
    fileScan.close();
  }

  /**
   * Part 1: add, increment score, and get score
   */

  //add word to the tree
  public void add(StringBuilder word){
    if (word == null) throw new IllegalArgumentException("calls add() with a null key");
    //word = word.append(SENTINEL);
    root = add(root, word, 0);
    //TO-DO Implement this method
  }

  private DLBNode add(DLBNode x, StringBuilder key, int pos) {
    DLBNode result = x;
    if (x == null)
    {
      result = new DLBNode(key.charAt(pos),0);
      if(pos < key.length()-1)
      {
        result.child = add(result.child, key, pos + 1);
      } else{
        result.data = key.charAt(pos);
        result.isWord = true;
      }
    }
    else if(x.data == key.charAt(pos))
    {
      if(pos < key.length()-1)
      {
        result.child = add(result.child, key, pos + 1);
      } else{
        result.data = key.charAt(pos);
        result.isWord = true;
      }
    }
    else {
      result.sibling = add(result.sibling, key, pos);
    }
    return result;
  }

  //increment the score of word
  public void notifyWordSelected(StringBuilder word)
  {
    root = notifyWordSelected(root, word, 0);
    //TO-DO Implement this method
  }

  private DLBNode notifyWordSelected(DLBNode x, StringBuilder key, int pos)
  {
    if(x.data == key.charAt(pos))
    {
      if(pos < key.length()-1)
      {
        notifyWordSelected(x.child, key, pos + 1);
      } else{
        x.score = x.score + 1;
      }
    }
    else {
      notifyWordSelected(x.sibling, key, pos);
    }
    return x;
  }

  //get the score of word
  public int getScore(StringBuilder word)
  {
    //TO-DO Implement this method
    int score = getScore(root,word,0);
    return score;
  }

  private int getScore(DLBNode x, StringBuilder key, int pos )
  {
    int score = 0;
    if(x.data == key.charAt(pos))
    {
      if(pos < key.length()-1)
      {
        score = getScore(x.child, key, pos + 1);
      } else{
        score = x.score;
      }
    }
    else {
      score = getScore(x.sibling, key, pos);
    }
    return score;
  }
 
  /**
   * Part 2: retrieve word suggestions in sorted order.
   */
  
  //retrieve a sorted list of autocomplete words for word. The list should be sorted in descending order based on score.
  public ArrayList<Suggestion> retrieveWords(StringBuilder word){

    ArrayList<Suggestion> result = new ArrayList<Suggestion>();
    retrieveWords(root, word, result);
    //result.sort(Suggestion::compareTo);
    //TO-DO Implement this method
    return result;
  }

  private void retrieveWords (DLBNode x, StringBuilder current, ArrayList<Suggestion> queue)
  {
      if (x == null) return;
      DLBNode curr = x;
      while(curr != null){
          current.append(curr.data);
          if(curr.isWord == true)
          {
              Suggestion result = new Suggestion(current, 0);
              queue.add(result);
          }
          retrieveWords(curr.child, current, queue);
          current.deleteCharAt(current.length()-1);
          curr = curr.sibling;
      }
  }
  /**
   * Helper methods for debugging.
   */

  //Print the subtree after the start string
  public void printTree(String start){
    System.out.println("==================== START: DLB Tree Starting from "+ start + " ====================");
    DLBNode startNode = getNode(root, start, 0);
    if(startNode != null){
      printTree(startNode.child, 0);
    }
    System.out.println("==================== END: DLB Tree Starting from "+ start + " ====================");
  }

  //A helper method for printing the tree
  private void printTree(DLBNode node, int depth){
    if(node != null){
      for(int i=0; i<depth; i++){
        System.out.print(" ");
      }
      System.out.print(node.data);
      if(node.isWord){
        System.out.print(" *");
      }
        System.out.println(" (" + node.score + ")");
      printTree(node.child, depth+1);
      printTree(node.sibling, depth);
    }
  }

  //return a pointer to the node at the end of the start string. Called from printTree.
  private DLBNode getNode(DLBNode node, String start, int index){
    DLBNode result = node;
    if(node != null){
      if((index < start.length()-1) && (node.data.equals(start.charAt(index)))) {
          result = getNode(node.child, start, index+1);
      } else if((index == start.length()-1) && (node.data.equals(start.charAt(index)))) {
          result = node;
      } else {
          result = getNode(node.sibling, start, index);
      }
    }
    return result;
  }


  //A helper class to hold suggestions. Each suggestion is a (word, score) pair. 
  //This class should be Comparable to itself.
  public class Suggestion implements Comparable<Suggestion> {

    StringBuilder word;
    int score;
    //TO-DO Fill in the fields and methods for this class. Make sure to have them public as they will be accessed from the test program A2Test.java.

    private Suggestion(StringBuilder word, int score) {
      this.word = word;
      this.score = score;
    }

    @Override
    public int compareTo(Suggestion o) {
      return Integer.compare(this.score, o.score);
    }
  }

  //The node class.
  private class DLBNode{
    private Character data;
    private int score;
    private boolean isWord;
    private DLBNode sibling;
    private DLBNode child;

    private DLBNode(Character data, int score){
        this.data = data;
        this.score = score;
        isWord = false;
        sibling = child = null;
    }
  }
}
