package marvel;

import graph.CampusGraph;

import java.util.*;

/**
 * MarvelPaths helps finding connections between Marvel characters, using
 * the given input .tsv file. Two characters are connected if they appear
 * in the same comic book. It can find the shortest path between two characters.
 * When there is a tie, it will provides the path with highest lexicographic precedence.
 */
public class MarvelPaths {

    /**
     * Ask for user's input: two Marvel Hero names, and find the shortest path between them if possible.
     *
     * @param args is not used.
     */
    public static void main (String[] args) {
        CampusGraph<String, String> cg = MarvelParser.buildGraph("marvel.tsv");
        System.out.println("Hi! Welcome to Marvel Universe!");
        System.out.println();
        System.out.println("You can find the closest path between any two heroes in all marvel comic books!");
        System.out.println("Pleases type your heroes: ");
        Scanner input = new Scanner(System.in);

        boolean searched = false;
        while (!searched) {
            boolean firstSuccess = false;
            boolean secondSuccess = false;
            String hero1 = "";
            String hero2 = "";
            while (!firstSuccess) {
                System.out.println("Type your first hero: ");
                hero1 = input.nextLine();
                if (!cg.hasNode(hero1)) {
                    System.out.println("Oops! It seems like your first hero is not a Marvel Hero :( ");
                    System.out.println("Please type it again: ");
                } else {
                    firstSuccess = true;
                }
            }
            if (firstSuccess) {
                while (!secondSuccess) {
                    System.out.println("Type your second hero: ");
                    hero2 = input.nextLine();
                    if (!cg.hasNode(hero2)) {
                        System.out.println("Oops! It seems like your second hero is not a Marvel Hero :( ");
                        System.out.println("Please type it again: ");
                    } else {
                        secondSuccess = true;
                    }
                }
                if (secondSuccess) {
                    List<CampusGraph.Edges<String, String>> path = findPath(cg, hero1, hero2);
                    System.out.println("Correct Heroes!\n");
                    System.out.println("The shortest path from " + hero1 + " to " + hero2 + ":");
                    if (path == null) {
                        System.out.println("There is no connection between these two heroes.");
                    } else {
                        String lastStart = hero1;
                        for (CampusGraph.Edges<String, String> e : path) {
                            System.out.println(lastStart + " to " + e.getEndTag() + " via " + e.getEdgeLabel());
                            lastStart = e.getEndTag(); // update the last start each time
                        }
                    }
                    searched = true;
                }
            }
            System.out.println();
            System.out.println("END. Thank you.");
        }
    }

    /**
     * Find the shortest path between two given nodes in the given graph, if possible.
     *
     * @spec.requires cg != null, start != null, destination != null
     * @param cg the CampusGraph where the search will be done
     * @param start the starting node of the path
     * @param destination the destination node of the path
     * @return a list of Edges that bridge from the start to the destination
     */
    public static List<CampusGraph.Edges<String, String>> findPath(CampusGraph<String, String> cg, String start, String destination) {
        if (cg == null || start == null || destination == null) {
            throw new IllegalArgumentException();
        }
        Queue<String> visitedNodes = new LinkedList<>();
        Map<String, List<CampusGraph.Edges<String, String>>> paths = new HashMap<>();
        visitedNodes.add(start); // Add start to Q
        paths.put(start, new LinkedList<>()); // Add start->[] to M (start mapped to an empty list)

        while (!visitedNodes.isEmpty()) {
            String currentNode = visitedNodes.remove();
            if (currentNode.equals(destination)) { // found the path
                return paths.get(currentNode);
            } else {
                List<CampusGraph.Edges<String, String>> neighbors = cg.getAllChildren(currentNode);
                neighbors.sort(new Comparator<CampusGraph.Edges<String, String>>() {
                    @Override
                    public int compare(CampusGraph.Edges<String, String> o1, CampusGraph.Edges<String, String> o2) {
                        int cmpEndNode = o1.getEndTag().compareTo(o2.getEndTag());
                        if (cmpEndNode == 0) {
                            return o1.getEdgeLabel().compareTo(o2.getEdgeLabel());
                        }
                        return cmpEndNode;
                    }
                }); // ascending list. Sort characters and books names all at once??

                for (CampusGraph.Edges<String, String> e: neighbors) {
                    String neighbor = e.getEndTag();
                    if (!paths.containsKey(neighbor)) { // i.e. if the neighbor is unvisited
                        List<CampusGraph.Edges<String, String>> pathCopy = new LinkedList<>(paths.get(currentNode));
                        pathCopy.add(e);
                        paths.put(neighbor, pathCopy); // add the path exclusive to this neighbor
                        visitedNodes.add(neighbor); // then, mark the neighbor as visited
                    }
                }
            }
        }
        return null; // No destination found
    }
}
