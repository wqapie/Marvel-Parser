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

package marvel;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvToBeanBuilder;
import graph.CampusGraph;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;


/**
 * Parser utility to load the Marvel Comics dataset.
 */
public class MarvelParser {

    /**
     * Reads the Marvel Universe dataset. Each line of the input file contains a character name and a
     * comic book the character appeared in, separated by a tab character
     *
     * @param filename the file that will be read
     * @return an Iterator for iteration over the dataset to build graphs.
     * @spec.requires filename is a valid file in the resources/data folder.
     */
    public static Iterator<UserModel> parseData(String filename) {
        // You can use this code as an example for getting a file from the resources folder
        // in a project like this. If you access TSV files elsewhere in your code, you'll need
        // to use similar code. If you use this code elsewhere, don't forget:
        //   - Replace 'MarvelParser' in `MarvelParser.class' with the name of the class you write this in
        //   - If the class is in src/main, it'll get resources from src/main/resources
        //   - If the class is in src/test, it'll get resources from src/test/resources
        //   - The "/" at the beginning of the path is important
        // Note: Most students won't re-write this code anywhere, this explanation is just for completeness.
        InputStream stream = MarvelParser.class.getResourceAsStream("/data/" + filename);
        if(stream == null) {
            // stream is null if the file doesn't exist.
            // You'll probably want to handle this case so you don't try to call
            // getPath and have a null pointer exception.
            // Technically, you'd be allowed to just have the NPE because of
            // the @spec.requires, but it's good to program defensively. :)
            throw new IllegalArgumentException("provided an invalid file name");
        }
        Reader reader = new BufferedReader(new InputStreamReader(stream)); // Read the file and then we will put it into a bunch of these beans.

        // Hint: You might want to create a new bean class to use with the OpenCSV Parser
        Iterator<UserModel> csvUserIterator =
                new CsvToBeanBuilder<UserModel>(reader) // set input
                        .withType(UserModel.class) // set entry type//
                        .withSeparator('\t') // for TSV
                        .withIgnoreLeadingWhiteSpace(true)
                        .build() // returns a CsvToBean<UserModel>
                        .iterator(); // give me an iterator to for-loop over csvUserIterator
        return csvUserIterator;
    }

    /**
     * Build a CampusGraph that contains specified nodes and edges in a file, given the correct fileName.
     *
     * @param fileName a String representation of a file to be build from.
     * @return a CampusGraph built from the information given in the file.
     * If the file does not exist, return an empty CampusGraph.
     */
    public static CampusGraph<String, String> buildGraph(String fileName) {

        Iterator<UserModel> iter = parseData(fileName);
        CampusGraph<String, String> cg = new CampusGraph<String, String>();
        Map<String, HashSet<String>> charactersInSameBook = new HashMap<>();
        Map<String, HashSet<String>> occurrenceBooks = new HashMap<>();

        while (iter.hasNext()) {
            UserModel line = iter.next();
            String hero = line.getHero();
            String book = line.getBook();
            cg.addNode(hero); // cg will automatically neglect the duplicates. All heroes will be in the keySet
            if (!charactersInSameBook.containsKey(book)) {
                charactersInSameBook.put(book, new HashSet<>());
            }
            charactersInSameBook.get(book).add(hero);

            if (!occurrenceBooks.containsKey(hero)) {
                occurrenceBooks.put(hero, new HashSet<>());
            }
            occurrenceBooks.get(hero).add(book);
        }

        List<String> allHeroes = cg.getAllNodes(); // return a list of all heroes
        for (String hero: allHeroes) {
            Set<String> books = occurrenceBooks.get(hero);
            if (!books.isEmpty()) { // to improve efficiency by skipping empty cases
                for (String book: books) {
                    Set<String> partners = charactersInSameBook.get(book);
                    if (!partners.isEmpty()) { // to improve efficiency by skipping empty cases
                        for (String partner : partners) {
                            if (!hero.equals(partner)) {
                                cg.addEdge(book, hero, partner); // No self-pointing edges?
                            }
                        }
                    }
                }
            }
        }
        return cg;
    }
}