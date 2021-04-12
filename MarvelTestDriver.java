/*
 * Copyright (C) 2021 Hal Perkins.  All rights reserved.  Permission is
 * hereby granted to students registered for University of Washington
 * CSE 331 for use solely during Winter Quarter 2021 for purposes of
 * the course.  No other use, copying, distribution, or modification
 * is permitted without prior written consent. Copyrights for
 * third-party components of this work must be honored.  Instructors
 * interested in reusing these course materials should contact the
 * author.
 */

package marvel.scriptTestRunner;

import graph.CampusGraph;
import marvel.MarvelParser;
import marvel.MarvelPaths;

import java.io.*;
import java.util.*;

/**
 * This class implements a testing driver which reads test scripts from
 * files for testing Graph, the Marvel parser, and your BFS algorithm.
 */
public class MarvelTestDriver {

    public static void main(String[] args) {
        // You only need a main() method if you choose to implement
        // the 'interactive' test driver, as seen with GraphTestDriver's sample
        // code. You may also delete this method entirely if you don't want to
        // use the interactive test driver.
    }


    // ***************************
    // ***  JUnit Test Driver  ***
    // ***************************

    /**
     * String -> Graph: maps the names of graphs to the actual graph
     **/
    // TODO for the student: Uncomment and parameterize the next line correctly:
    private final Map<String, CampusGraph<String, String>> graphs = new HashMap<>();
    private final PrintWriter output;
    private final BufferedReader input;

    // Leave this constructor public
    public MarvelTestDriver(Reader r, Writer w) {
        // TODO: Implement this, reading commands from `r` and writing output to `w`.
        // See GraphTestDriver as an example.
        input = new BufferedReader(r);
        output = new PrintWriter(w);
    }

    /**
     * @throws IOException if the input or output sources encounter an IOException
     * @spec.effects Executes the commands read from the input and writes results to the output
     **/
    // Leave this method public
    public void runTests()
            throws IOException {
        String inputLine;
        while ((inputLine = input.readLine()) != null) {
            if ((inputLine.trim().length() == 0) ||
                    (inputLine.charAt(0) == '#')) {
                // echo blank and comment lines
                output.println(inputLine);
            } else {
                // separate the input line on white space
                StringTokenizer st = new StringTokenizer(inputLine);
                if (st.hasMoreTokens()) {
                    String command = st.nextToken();

                    List<String> arguments = new ArrayList<String>();
                    while (st.hasMoreTokens()) {
                        arguments.add(st.nextToken());
                    }

                    executeCommand(command, arguments);
                }
            }
            output.flush();
        }
    }

    private void executeCommand(String command, List<String> arguments) {
        try {
            switch (command) {
                case "CreateGraph":
                    createGraph(arguments);
                    break;
                case "AddNode":
                    addNode(arguments);
                    break;
                case "AddEdge":
                    addEdge(arguments);
                    break;
                case "ListNodes":
                    listNodes(arguments);
                    break;
                case "ListChildren":
                    listChildren(arguments);
                    break;
                case "LoadGraph":
                    loadGraph(arguments);
                    break;
                case "FindPath":
                    findPath(arguments);
                    break;
                default:
                    output.println("Unrecognized command: " + command);
                    break;
            }
        } catch (Exception e) {
            output.println("Exception: " + e.toString());
        }
    }

    private void createGraph(List<String> arguments) {
        if (arguments.size() != 1) {
            throw new CommandException("Bad arguments to CreateGraph: " + arguments);
        }

        String graphName = arguments.get(0);
        createGraph(graphName);
    }

    private void createGraph(String graphName) {
        // TODO Insert your code here.

        CampusGraph<String, String> newGraph = new CampusGraph<>();
        graphs.put(graphName, newGraph);
        output.println("created graph " + graphName);
    }

    private void addNode(List<String> arguments) {
        if (arguments.size() != 2) {
            throw new CommandException("Bad arguments to AddNode: " + arguments);
        }

        String graphName = arguments.get(0);
        String nodeName = arguments.get(1);

        addNode(graphName, nodeName);
    }

    private void addNode(String graphName, String nodeName) {
        // TODO Insert your code here.

        CampusGraph<String, String> graph = graphs.get(graphName);
        graph.addNode(nodeName);
        output.println("added node " + nodeName + " to " + graphName);
    }

    private void addEdge(List<String> arguments) {
        if (arguments.size() != 4) {
            throw new CommandException("Bad arguments to AddEdge: " + arguments);
        }

        String graphName = arguments.get(0);
        String parentName = arguments.get(1);
        String childName = arguments.get(2);
        String edgeLabel = arguments.get(3);

        addEdge(graphName, parentName, childName, edgeLabel);
    }

    private void addEdge(String graphName, String parentName, String childName,
                         String edgeLabel) {
        // TODO Insert your code here.

        CampusGraph<String, String> graph = graphs.get(graphName);
        graph.addNode(parentName);
        graph.addNode(childName);
        graph.addEdge(edgeLabel, parentName, childName);
        output.println("added edge " + edgeLabel + " from " + parentName + " to "
                + childName + " in " + graphName);
    }

    private void listNodes(List<String> arguments) {
        if (arguments.size() != 1) {
            throw new CommandException("Bad arguments to ListNodes: " + arguments);
        }

        String graphName = arguments.get(0);
        listNodes(graphName);
    }

    private void listNodes(String graphName) {
        // TODO Insert your code here.

        CampusGraph<String, String> graph = graphs.get(graphName);
        String str = graphName + " contains:";
        List<String> nodes = graph.getAllNodes();
        Collections.sort(nodes);
        for (String nodeStr: nodes) {
            str += " " + nodeStr;
        }
        output.println(str);
    }

    private void listChildren(List<String> arguments) {
        if (arguments.size() != 2) {
            throw new CommandException("Bad arguments to ListChildren: " + arguments);
        }

        String graphName = arguments.get(0);
        String parentName = arguments.get(1);
        listChildren(graphName, parentName);
    }

    private void listChildren(String graphName, String parentName) {
        // TODO Insert your code here.

        CampusGraph<String, String> graph = graphs.get(graphName);
        String str = "the children of " + parentName + " in " + graphName + " are:";
        List<CampusGraph.Edges<String, String>> children = graph.getAllChildren(parentName);
        children.sort(new Comparator<CampusGraph.Edges<String, String>>() {
            @Override
            public int compare(CampusGraph.Edges<String, String> o1, CampusGraph.Edges<String, String> o2) {
                int cmpEndNode = o1.getEndTag().compareTo(o2.getEndTag());
                if (cmpEndNode == 0) {
                    return o1.getEdgeLabel().compareTo(o2.getEdgeLabel());
                }
                return cmpEndNode;
            }
        }); // ascending list.

        for (CampusGraph.Edges<String, String> child: children) {
            str += (" " + child.getEndTag() + "(" + child.getEdgeLabel() + ")");
        }
        output.println(str);
    }

    private void loadGraph(List<String> arguments) {
        if (arguments.size() != 2) {
            throw new CommandException("Bad arguments to LoadGraph: " + arguments);
        }

        String graphName = arguments.get(0);
        String fileName = arguments.get(1);
        loadGraph(graphName, fileName);
    }

    private void loadGraph(String graphName, String fileName) {
        CampusGraph<String, String> cp = MarvelParser.buildGraph(fileName);
        graphs.put(graphName, cp);
        output.println("loaded graph " + graphName);
    }

    private void findPath(List<String> arguments) {
        if (arguments.size() != 3) {
            throw new CommandException("Bad arguments to FindPath: " + arguments);
        }

        String bookName = arguments.get(0);
        String hero1 = arguments.get(1);
        String hero2 = arguments.get(2);
        findPath(bookName, hero1, hero2);
    }

    private void findPath(String bookName, String hero1, String hero2) {
        CampusGraph<String, String> book = graphs.get(bookName);
        hero1 = hero1.replace("_", " ");
        hero2 = hero2.replace("_", " ");

        if (!book.hasNode(hero1) || !book.hasNode(hero2)) {
            if (!book.hasNode(hero1)) {
                output.println("unknown character " + hero1);
            }
            if (!book.hasNode(hero2)) {
                output.println("unknown character " + hero2);
            }
        } else { // both nodes are in the graph
            output.println("path from " + hero1 + " to " + hero2 + ":");
            List<CampusGraph.Edges<String, String>> path = MarvelPaths.findPath(book, hero1, hero2);
            if (path == null) {
                output.println("no path found");
            } else {
                String lastStart = hero1;
                for (CampusGraph.Edges<String, String> e: path) {
                    output.println(lastStart + " to " + e.getEndTag() + " via " + e.getEdgeLabel());
                    lastStart = e.getEndTag(); // update the last start each time
                }
            }
        }
    }
    /**
     * This exception results when the input file cannot be parsed properly
     **/
    static class CommandException extends RuntimeException {

        public CommandException() {
            super();
        }

        public CommandException(String s) {
            super(s);
        }

        public static final long serialVersionUID = 3495;
    }
}
