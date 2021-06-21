// Yichao Qiu/yiq23/Assignment 4

import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.*;
import java.awt.Color;
import javax.swing.UIManager;

@SuppressWarnings("unchecked")
public class AirlineSystem {

    // declare variables
    private String[] cityNames = null;
    private Digraph G = null;
    private String fileName;
    private static int[] r;
    static AirlineSystem airline = new AirlineSystem();
    static ImageIcon icon = new ImageIcon("icon.jpg", "airplane");

    // taken from lab 9 and 10
    public static void main(String[] args) throws Exception {

        UIManager UI = new UIManager();
        UIManager.put("OptionPane.background", new Color(78, 87, 212));
        UIManager.put("Panel.background", new Color(78, 212, 96));

        JOptionPane.showMessageDialog(null, "Welcome to CS1501 Airlines! ", "CS1501 Airlines",
                0, icon);

        airline.readGraph();

        while (true) {
            switch (airline.menu()) {
                case 1:
                    airline.printGraph();
                    break;
                case 2:
                    airline.primMST();
                    break;
                case 3:
                    airline.shortPath();
                    break;
                case 4:
                    r = airline.getUserCity();
                    airline.addRoute(r);
                    break;
                case 5:
                    r = airline.getUserCity();
                    airline.removeRoute(r);
                    break;
                case 6:
                    airline.findVia();
                    break;
                case 7:
                    airline.updateFile();
                    System.out.println("We're sad to see you go! Come back soon!");
                    System.exit(0);

                default:
                    System.out.println("Incorrect option.");
            }
        }
    }

    private void shortPath() throws Exception {
        switch (airline.shortPathMenu()) {
            case 1:
                r = airline.getUserCity();
                airline.shortestHops(r);
                break;
            case 2:
                r = airline.getUserCity();
                airline.shortestDistance(r);
                break;
            case 3:
                r = airline.getUserCity();
                airline.lowestCost(r);
                break;
            default:
                break;
        }
    }

    private int menu() {

        String[] options = { "Click to expand", "Display all routes",
                "Display a miniumum spanning tree (MST) based on distance", "Shortest path menu", "Add a new route.",
                "Remove an existing route.", "Find a flight via a different city.", "Exit and Save." };

        String s = (String) JOptionPane.showInputDialog(null, "Choose one of the menu options below:\n",
                "Customized Dialog", JOptionPane.PLAIN_MESSAGE, icon, options, "Choose");

        int choice = 0;

        for (int i = 1; i < options.length; i++) {
            if (s.equals(options[i])) {
                choice = i;
            }

        }
        return choice;
    }

    private int shortPathMenu() {

        String[] shortOptions = { "Shortest Path based on: ", "Shortest Path based on number of hops.",
                "Shortest Path based on total miles.", "Shortest Path based on price.", "Exit." };

        String s = (String) JOptionPane.showInputDialog(null, "Choose one of the menu options below:\n",
                "Customized Dialog", JOptionPane.PLAIN_MESSAGE, icon,shortOptions, "Choose");

        int choice = 0;

        for (int i = 1; i < shortOptions.length; i++) {
            if (s.equals(shortOptions[i])) {
                choice = i;
            }

        }
        return choice;
    }

    private void readGraph() throws IOException {
        fileName = JOptionPane.showInputDialog("Please enter a filename to start:");
        Scanner fileScan = new Scanner(new FileInputStream(fileName));
        int v = Integer.parseInt(fileScan.nextLine());
        G = new Digraph(v);

        cityNames = new String[v];
        for (int i = 0; i < v; i++) {
            cityNames[i] = fileScan.nextLine();
        }

        while (fileScan.hasNext()) {
            int from = fileScan.nextInt();
            int to = fileScan.nextInt();
            int distance = fileScan.nextInt();
            double cost = fileScan.nextDouble();
            G.addEdge(new DirectedEdge(from - 1, to - 1, distance, cost));
            G.addEdge(new DirectedEdge(to - 1, from - 1, distance, cost));
        }
        fileScan.close();
        JOptionPane.showMessageDialog(null, "Data accepted!");
    }

    private void printGraph() {

        String print = "";
        for (int i = 0; i < G.v; i++) {
            print += cityNames[i] + " to \n";
            for (DirectedEdge e : G.adj(i)) {
                print += ">> " + cityNames[e.to()] + "  (" + e.distance() + " miles)  $" + e.cost() + "\n";
            }
        }

        JTextArea textArea = new JTextArea(15, 25);
        textArea.setText(print);
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);

        JOptionPane.showMessageDialog(null, scrollPane);

    }

    private void primMST() {
        PrimMSTTrace mst = new PrimMSTTrace(G);
        mst.prim(G, 0);

        String print = "";
        Iterable<DirectedEdge> edges = mst.edges();
        // find a way to print the tree
        for (DirectedEdge e : edges) {
            String start = cityNames[e.either()];
            String end = cityNames[e.other(e.either())];
            print += (start + " -> " + end + "\n Price: $" + e.cost() + " Distance: " + e.distance() + " miles \n");
        }

        print += "The total distance is: " + mst.weight();

        JTextArea textArea = new JTextArea(15, 25);
        textArea.setText(print);
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);

        JOptionPane.showMessageDialog(null, scrollPane);

    }

    // Lab 10 as reference
    private void shortestHops(int[] r) {

        int source = r[0];
        int destination = r[1];

        G.bfs(source);
        String print = "";
        if (!G.marked[destination]) {
            print += ("There is no route from " + cityNames[source] + " to " + cityNames[destination]);
        } else {
            Stack<Integer> path = new Stack<>();
            for (int x = destination; x != source; x = G.edgeTo[x])
                path.push(x);

            path.push(source);
            print += "FEWEST HOPS from " + cityNames[source] + " to " + cityNames[destination];
            print += "\nThe fewest hops from " + cityNames[source] + " to " + cityNames[destination] + " is "
                    + G.distTo[destination];

            print += ("\nPath (in reverse order): \n");
            while (!path.empty()) {
                print += cityNames[path.pop()] + " -> ";
            }
            print += "\n";
        }

        JTextArea textArea = new JTextArea(8, 25);
        textArea.setText(print);
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);

        JOptionPane.showMessageDialog(null, scrollPane);

    }

    private void shortestDistance(int[] r) {
        String print = "";

        int source = r[0];
        int destination = r[1];
        G.dijkstras(source, destination, 0);
        if (!G.marked[destination]) {
            print += ("There is no route from " + cityNames[source] + " to " + cityNames[destination]) + "\n";
        } else {
            Stack<Integer> path = new Stack<>();
            for (int x = destination; x != source; x = G.edgeTo[x]) {
                path.push(x);
            }

            print += ("SHORTEST DISTANCE from " + cityNames[source] + " to " + cityNames[destination]);
            print += ("\nThe shortest distance from " + cityNames[source] + " to " + cityNames[destination] + " is "
                    + G.distTo[destination] + "miles\n");

            print += ("Path with edges: \n");
            int prevVertex = source;
            print += (cityNames[source] + " ");
            while (!path.empty()) {
                int v = path.pop();
                print += (G.distTo[v] - G.distTo[prevVertex] + " miles " + cityNames[v]);
                prevVertex = v;
            }
        }

        JTextArea textArea = new JTextArea(8, 28);
        textArea.setText(print);
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);

        JOptionPane.showMessageDialog(null, scrollPane);
    }

    private void lowestCost(int[] r) {
        String print = "";
        int source = r[0];
        int destination = r[1];

        G.dijkstras(source, destination, 1);
        if (!G.marked[destination]) {
            print += ("There is no route from " + cityNames[source] + " to " + cityNames[destination] + "\n");
        } else {
            Stack<Integer> path = new Stack<>();
            for (int x = destination; x != source; x = G.edgeTo[x]) {
                path.push(x);
            }
            print += ("LOWEST COST from " + cityNames[source] + " to " + cityNames[destination]);
            print += ("\nThe lowest cost from " + cityNames[source] + " to " + cityNames[destination] + " is $"
                    + G.distTo[destination]);

            print += ("\nPath with edges: \n");
            int prevVertex = source;
            print += (cityNames[source] + " ");
            while (!path.empty()) {
                int v = path.pop();
                print += ("$" + (G.distTo[v] - G.distTo[prevVertex]) + " " + cityNames[v]);
                prevVertex = v;
            }
        }

        JTextArea textArea = new JTextArea(8, 25);
        textArea.setText(print);
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);

        JOptionPane.showMessageDialog(null, scrollPane);
    }

    private int findCity(String name) {
        int i;
        for (i = 0; i < cityNames.length; i++) {
            if (cityNames[i].equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

    private int[] getUserCity() throws Exception {

        String s = JOptionPane.showInputDialog("Enter starting city:");
        String end = JOptionPane.showInputDialog("Enter destination city:");

        int start = findCity(s);
        int destination = findCity(end);

        int[] ret = { start, destination };
        if (start < 0 || destination < 0) {
            throw new Exception("This city does not exist. Please check your spelling and try again");
        }

        return ret;
    }

    private void addRoute(int[] r) {

        String cost = JOptionPane.showInputDialog("Enter cost:");
        String miles = JOptionPane.showInputDialog("Enter distance:");

        double c = Double.parseDouble(cost);
        int dist = Integer.parseInt(miles);

        G.addEdge(new DirectedEdge(r[0], r[1], dist, c));
        G.addEdge(new DirectedEdge(r[1], r[1], dist, c));

        JOptionPane.showMessageDialog(null, "New route added successfully!");

    }

    private void removeRoute(int[] r) {

        boolean removed = false;

        for (DirectedEdge e : G.adj(r[0])) {

            if (e.either() == r[0] && e.other(e.either()) == r[1]) {
                Iterator it = G.getadg(r[0]).iterator();

                while (it.hasNext()) {

                    if (e == it.next()) {
                        it.remove();
                        removed = true;
                    }

                }
            }

        }

        if (removed == true) {
            JOptionPane.showMessageDialog(null, "Route removed successfully!");

        } else {
            JOptionPane.showMessageDialog(null,
                    "We were not able to remove the route specified. Please check if a route exists.");
        }

    }

    private void updateFile() throws FileNotFoundException {

        File original = new File(fileName);

        PrintWriter myWriter = new PrintWriter(original);

        myWriter.write(cityNames.length + "\n");
        for (String name : cityNames) {
            myWriter.write(name + "\n");
        }

        boolean[][] done = new boolean[G.adj.length][G.adj.length];
        for (int i = 0; i < G.adj.length; i++) {
            for (DirectedEdge E : G.adj(i)) {
                int start = E.from() + 1;
                int end = E.to() + 1;
                if (done[start - 1][end - 1] == false) {
                    myWriter.write(start + " " + end + " " + E.distance() + " " + E.cost() + "\n");
                    done[start - 1][end - 1] = true;
                    done[end - 1][start - 1] = true;
                }

            }
        }

        myWriter.close();

    }

    private void findVia() throws Exception {

        String s = JOptionPane.showInputDialog("Enter starting city:");
        String m = JOptionPane.showInputDialog("Enter layover city:");
        String end = JOptionPane.showInputDialog("Enter destination city:");

        int start = findCity(s);
        int layover = findCity(m);
        int destination = findCity(end);

        int[] ret = { start, destination };
        if (start < 0 || destination < 0) {
            throw new Exception("This city does not exist. Please check your spelling and try again");
        }

        if (start < 0 || destination < 0) {
            throw new Exception("This city does not exist. Please check your spelling and try again");
        }

        String print = "";

        for (DirectedEdge e : G.adj(start)) {
            if (e.to() == layover) {
                for (DirectedEdge d : G.adj(layover)) {
                    if (d.to() == destination) {
                        print += ("Layover found!\n");
                        print += ("The route from " + cityNames[start] + " to " + cityNames[destination]
                                + "\n with a layover in " + cityNames[layover] + " is " + (e.distance() + d.distance())
                                + " miles \n and will cost $" + (e.cost() + d.cost()));
                    }
                }
            }
        }

        JTextArea textArea = new JTextArea(8, 28);
        textArea.setText(print);
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);

        JOptionPane.showMessageDialog(null, scrollPane);

    }
    /////////////////////// OTHER CLASSES///////////////////////////////////

    // Digraph class: updated from older version
    public class Digraph {
        public final int v;
        public int e;
        public LinkedList<DirectedEdge>[] adj;
        public boolean[] marked; // marked[v] = is there an s-v path
        public int[] edgeTo; // edgeTo[v] = previous edge on shortest s-v path
        public int[] distTo; // distTo[v] = number of edges shortest s-v path
        private static final int INFINITY = Integer.MAX_VALUE;

        /**
         * Create an empty digraph with v vertices.
         */
        public Digraph(int v) {
            if (v < 0)
                throw new RuntimeException("Number of vertices must be nonnegative");
            this.v = v;
            this.e = 0;
            @SuppressWarnings("unchecked")
            LinkedList<DirectedEdge>[] temp = (LinkedList<DirectedEdge>[]) new LinkedList[v];
            adj = temp;
            for (int i = 0; i < v; i++)
                adj[i] = new LinkedList<DirectedEdge>();
        }

        /**
         * Add the edge e to this digraph.
         */
        public void addEdge(DirectedEdge edge) {
            int from = edge.from();
            adj[from].add(edge);
            e++;
        }

        public int v() {
            return this.v;
        }

        public LinkedList<DirectedEdge> getadg(int v) {
            return adj[v];
        }

        /**
         * Return the edges leaving vertex v as an Iterable. To iterate over the edges
         * leaving vertex v, use foreach notation:
         * <tt>for (DirectedEdge e : graph.adj(v))</tt>.
         */
        public Iterable<DirectedEdge> adj(int v) {
            return adj[v];
        }

        public void dijkstras(int source, int destination, int num) {
            marked = new boolean[this.v];
            distTo = new int[this.v];
            edgeTo = new int[this.v];

            for (int i = 0; i < v; i++) {
                distTo[i] = INFINITY;
                marked[i] = false;
            }
            distTo[source] = 0;
            marked[source] = true;
            int nMarked = 1;

            if (num == 0) // this means need to calculate based on Distance
            {
                int current = source;
                while (nMarked < this.v) {
                    for (DirectedEdge w : adj(current)) {
                        if (distTo[current] + w.distance() < distTo[w.to()]) {
                            edgeTo[w.to()] = current;
                            distTo[w.to()] = distTo[current] + w.distance;

                        }
                    }
                    // Find the vertex with minimim path distance
                    // This can be done more effiently using a priority queue!
                    int min = INFINITY;
                    current = -1;

                    for (int i = 0; i < distTo.length; i++) {
                        if (marked[i])
                            continue;
                        if (distTo[i] < min) {
                            min = distTo[i];
                            current = i;
                        }
                    }

                    if (current >= 0) {
                        marked[current] = true;
                        nMarked++;

                    } else {
                        break;
                    }
                }
            } else {
                int current = source;
                while (nMarked < this.v) {
                    for (DirectedEdge w : adj(current)) {
                        if (distTo[current] + w.cost() < distTo[w.to()]) {
                            edgeTo[w.to()] = current;
                            distTo[w.to()] = distTo[current] + (int) w.cost();

                        }
                    }
                    // Find the vertex with minimim path distance
                    // This can be done more effiently using a priority queue!
                    int min = INFINITY;
                    current = -1;

                    for (int i = 0; i < distTo.length; i++) {
                        if (marked[i])
                            continue;
                        if (distTo[i] < min) {
                            min = distTo[i];
                            current = i;
                        }
                    }

                    if (current >= 0) {
                        marked[current] = true;
                        nMarked++;

                    } else {
                        break;
                    }
                }
            }

        }

        public void bfs(int source) {
            marked = new boolean[this.v];
            distTo = new int[this.v];
            edgeTo = new int[this.v];

            Queue<Integer> q = new LinkedList<Integer>();
            for (int i = 0; i < v; i++) {
                distTo[i] = INFINITY;
                marked[i] = false;
            }
            distTo[source] = 0;
            marked[source] = true;
            q.add(source);

            while (!q.isEmpty()) {
                int v = q.remove();
                for (DirectedEdge w : adj(v)) {
                    if (!marked[w.to()]) {
                        edgeTo[w.to()] = v; // reverse
                        distTo[w.to()] = distTo[v] + 1;
                        marked[w.to()] = true;
                        q.add(w.to());
                    }
                }
            }
        }

    }

    // Directed Edge CLASS : updated from given
    public class DirectedEdge {
        private int v;
        private int w;
        private double cost;
        private int distance;

        public DirectedEdge(int v, int w, int distance, double cost) {
            if (v < 0)
                throw new IndexOutOfBoundsException("Must be postive integers!");
            if (w < 0)
                throw new IndexOutOfBoundsException("Must be postive integers!");
            if (Double.isNaN(cost))
                throw new IllegalArgumentException("Weight is NaN");
            this.v = v;
            this.w = w;
            this.setCost(cost);
            this.setDistance(distance);
        }

        public int from() {
            return v;
        }

        public int to() {
            return w;
        }

        public void setCost(double c) {
            this.cost = c;
        }

        public double cost() {
            return cost;
        }

        public void setDistance(int d) {
            this.distance = d;
        }

        public int distance() {
            return distance;
        }

        public int other(int vertex) {
            if (vertex == v)
                return w;
            else if (vertex == w)
                return v;
            else
                throw new RuntimeException("Illegal endpoint");
        }

        public int either() {
            return v;
        }
    }

    // PrimMST class: edited for Directed edge and digraph
    public class PrimMSTTrace {
        private DirectedEdge[] edgeTo; // edgeTo[v] = shortest edge from tree vertex to non-tree vertex
        private double[] distTo; // distTo[v] = weight of shortest such edge
        private boolean[] marked; // marked[v] = true if v on tree, false otherwise
        private IndexMinPQ<Double> pq;

        public PrimMSTTrace(Digraph G) {
            edgeTo = new DirectedEdge[G.v()];
            distTo = new double[G.v()];
            marked = new boolean[G.v()];
            pq = new IndexMinPQ<Double>(G.v());
            for (int v = 0; v < G.v(); v++)
                distTo[v] = Double.POSITIVE_INFINITY;

            for (int v = 0; v < G.v(); v++) // run from each vertex to find
                if (!marked[v])
                    prim(G, v); // minimum spanning forest

            // check optimality conditions

        }

        // run Prim's algorithm in graph G, starting from vertex s
        private void prim(Digraph G, int s) {
            distTo[s] = 0.0;
            pq.insert(s, distTo[s]);
            while (!pq.isEmpty()) {
                int v = pq.delMin();
                scan(G, v);
            }
        }

        // scan vertex v
        private void scan(Digraph G, int v) {
            marked[v] = true;
            for (DirectedEdge e : G.adj(v)) {
                int w = e.other(v);
                if (marked[w]) {
                    continue;
                }
                if (e.distance() < distTo[w]) {
                    distTo[w] = e.distance();
                    edgeTo[w] = e;
                    if (pq.contains(w)) {
                        pq.change(w, distTo[w]);
                    } else {
                        pq.insert(w, distTo[w]);
                    }
                }
            }
        }

        // return iterator of edges in MST
        public Iterable<DirectedEdge> edges() {
            ArrayList<DirectedEdge> mst = new ArrayList<DirectedEdge>();
            for (int v = 0; v < edgeTo.length; v++) {
                DirectedEdge e = edgeTo[v];
                if (e != null) {
                    mst.add(e);
                }
            }
            return mst;
        }

        // return weight of MST
        public double weight() {
            double weight = 0.0;
            for (DirectedEdge e : edges())
                weight += e.distance();
            return weight;
        }
    }

    // IndexMINPQ:Given
    public class IndexMinPQ<Key extends Comparable<Key>> implements Iterable<Integer> {
        private int N; // number of elements on PQ
        private int[] pq; // binary heap using 1-based indexing
        private int[] qp; // inverse of pq - qp[pq[i]] = pq[qp[i]] = i
        private Key[] keys; // keys[i] = priority of i

        @SuppressWarnings("unchecked")
        public IndexMinPQ(int NMAX) {
            keys = (Key[]) new Comparable[NMAX + 1]; // make this of length NMAX??
            pq = new int[NMAX + 1];
            qp = new int[NMAX + 1]; // make this of length NMAX??
            for (int i = 0; i <= NMAX; i++)
                qp[i] = -1;
        }

        // is the priority queue empty?
        public boolean isEmpty() {
            return N == 0;
        }

        // is k an index on the priority queue?
        public boolean contains(int k) {
            return qp[k] != -1;
        }


        // associate key with index k
        public void insert(int k, Key key) {
            if (contains(k))
                throw new RuntimeException("item is already in pq");
            N++;
            qp[k] = N;
            pq[N] = k;
            keys[k] = key;
            swim(N);
        }


        // delete a minimal key and returns its associated index
        public int delMin() {
            if (N == 0)
                throw new RuntimeException("Priority queue underflow");
            int min = pq[1];
            exch(1, N--);
            sink(1);
            qp[min] = -1; // delete
            keys[pq[N + 1]] = null; // to help with garbage collection
            pq[N + 1] = -1; // not needed
            return min;
        }

        /*
         * // change key associated with index k; insert if index k is not present
         * public void put(int k, Key key) { if (!contains(k)) insert(k, key); else
         * changeKey(k, key); }
         * 
         * // return key associated with index k public Key get(int k) { if
         * (!contains(k)) throw new RuntimeException("item is not in pq"); else return
         * keys[pq[k]]; }
         */

        // change the key associated with index k
        public void change(int k, Key key) {
            if (!contains(k))
                throw new RuntimeException("item is not in pq");
            keys[k] = key;
            swim(qp[k]);
            sink(qp[k]);
        }

        /**************************************************************
         * General helper functions
         **************************************************************/
        private boolean greater(int i, int j) {
            return keys[pq[i]].compareTo(keys[pq[j]]) > 0;
        }

        private void exch(int i, int j) {
            int swap = pq[i];
            pq[i] = pq[j];
            pq[j] = swap;
            qp[pq[i]] = i;
            qp[pq[j]] = j;
        }

        /**************************************************************
         * Heap helper functions
         **************************************************************/
        private void swim(int k) {
            while (k > 1 && greater(k / 2, k)) {
                exch(k, k / 2);
                k = k / 2;
            }
        }

        private void sink(int k) {
            while (2 * k <= N) {
                int j = 2 * k;
                if (j < N && greater(j, j + 1))
                    j++;
                if (!greater(k, j))
                    break;
                exch(k, j);
                k = j;
            }
        }

        /***********************************************************************
         * Iterators
         **********************************************************************/

        /**
         * Return an iterator that iterates over all of the elements on the priority
         * queue in ascending order.
         * <p>
         * The iterator doesn't implement <tt>remove()</tt> since it's optional.
         */
        public Iterator<Integer> iterator() {
            return new HeapIterator();
        }

        private class HeapIterator implements Iterator<Integer> {
            // create a new pq
            private IndexMinPQ<Key> copy;

            // add all elements to copy of heap
            // takes linear time since already in heap order so no keys move
            public HeapIterator() {
                copy = new IndexMinPQ<Key>(pq.length - 1);
                for (int i = 1; i <= N; i++)
                    copy.insert(pq[i], keys[pq[i]]);
            }

            public boolean hasNext() {
                return !copy.isEmpty();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }

            public Integer next() {
                if (!hasNext())
                    throw new NoSuchElementException();
                return copy.delMin();
            }
        }

    }

}