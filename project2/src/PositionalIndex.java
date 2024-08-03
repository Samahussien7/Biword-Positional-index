import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class PositionalIndex {
    private Map<String, Map<Integer, List<Integer>>> index;

    public PositionalIndex() {
        index = new HashMap<>();
    }
    int N = 0;

    public void addTerm(String term, int documentId, List<Integer> positions) {
        // If the term is not already in the index, add it
        if (!index.containsKey(term)) {
            index.put(term, new HashMap<>());
        }

        // Get the positional postings for the term
        Map<Integer, List<Integer>> postings = index.get(term);

        // Add the documentId and positions to the postings
        if (postings.containsKey(documentId)) {
            // If documentId already exists, add positions to existing list
            List<Integer> existingPositions = postings.get(documentId);
            existingPositions.addAll(positions);
        } else {
            // If documentId doesn't exist, create a new list
            postings.put(documentId, new ArrayList<>(positions));
        }
    }

    public void buildIndex(String fileName, int documentId) {
        try (BufferedReader file = new BufferedReader(new FileReader(fileName))) {
            String ln;
            int position = 0;
            while ((ln = file.readLine()) != null) {
                String[] tokens = ln.toLowerCase().split("\\s+");
                for (String token : tokens) {
                    List<Integer> positions = new ArrayList<>();
                    positions.add(position++); // Add position of the token in the document
                    addTerm(token, documentId, positions);
                }
            }
        } catch (IOException e) {
            System.out.println("File " + fileName + " not found. Skip it");
        }
    }

    public void readFiles(String[] files) {
        for (int i = 0; i < files.length; i++) {
            buildIndex(files[i], i);
        }
    }
    String[] sort(String[] words) {  //bubble sort
        boolean sorted = false;
        String sTmp;
        //-------------------------------------------------------
        while (!sorted) {
            sorted = true;
            for (int i = 0; i < words.length - 1; i++) {
                int compare = words[i].compareTo(words[i + 1]);
                if (compare > 0) {
                    sTmp = words[i];
                    words[i] = words[i + 1];
                    words[i + 1] = sTmp;
                    sorted = false;
                }
            }
        }
        return words;
    }

    public void printIndex() {
        Map<String, Map<Integer, List<Integer>>> sortedIndex = new TreeMap<>(index);

        for (Map.Entry<String, Map<Integer, List<Integer>>> entry : sortedIndex.entrySet()) {
            String term = entry.getKey();
            Map<Integer, List<Integer>> postings = new TreeMap<>(entry.getValue());
            System.out.print(term + ": ");

            for (Map.Entry<Integer, List<Integer>> posting : postings.entrySet()) {
                int documentId = posting.getKey();
                List<Integer> positions = posting.getValue();
                System.out.print("doc" + documentId + ":");
                System.out.print("(");
                for (int i = 0; i < positions.size(); i++) {
                    System.out.print(positions.get(i));
                    if (i < positions.size() - 1) {
                        System.out.print(", ");
                    }
                }
                System.out.print(") ");
            }
            System.out.println();
        }
    }

    public List<Integer> searchPhrase(String phrase) {
        String[] queryTerms = phrase.toLowerCase().split("\\s+");
        return search(queryTerms);
    }

    private List<Integer> search(String[] queryTerms) {
        // Initialize result set to contain all document IDs
        Set<Integer> resultSet = new HashSet<>(index.values().iterator().next().keySet());

        // Iterate over each query term
        for (int i = 0; i < queryTerms.length; i++) {
            String term = queryTerms[i];
            Map<Integer, List<Integer>> postings = index.get(term);
            if (postings == null) {
                // If any query term does not exist in the index, return empty result set
                return new ArrayList<>();
            }

            // If it's the first query term, initialize the result set with its document IDs
            if (i == 0) {
                resultSet = new HashSet<>(postings.keySet());
            } else {
                // For subsequent terms, retain only the document IDs that appear in both result set and postings
                resultSet.retainAll(postings.keySet());
            }
        }

        // Check positional proximity of query terms within documents in the result set
        List<Integer> finalResult = new ArrayList<>();
        for (int docId : resultSet) {
            boolean found = false;
            List<Integer> positions = index.get(queryTerms[0]).get(docId);
            for (int pos : positions) {
                int currentPos = pos;
                for (int i = 1; i < queryTerms.length; i++) {
                    currentPos++;
                    if (index.get(queryTerms[i]).get(docId).contains(currentPos)) {
                        found = true;
                    } else {
                        found = false;
                        break;
                    }
                }
                if (found) {
                    finalResult.add(docId);
                    break;
                }
            }
        }
        return finalResult;
    }

    public static void main(String[] args) {
        PositionalIndex positionalIndex = new PositionalIndex();

        String files = "tmp11/rl/collection/";    // Change the path

        File file = new File(files);
        String[] fileList = file.list();

        fileList = positionalIndex.sort(fileList);
        positionalIndex.N = fileList.length;

        for (int i = 0; i < fileList.length; i++) {
            fileList[i] = files + fileList[i];
        }
        // Tokenize and index each document
        positionalIndex.readFiles(fileList);

        /* Print the index*/
        positionalIndex.printIndex();

        // Perform queries until the user enters "0"
        Scanner scanner = new Scanner(System.in);
        String query = "";
        while (!query.equals("0")) {
            System.out.print("Enter a phrase to search (enter 0 to quit): ");
            query = scanner.nextLine().trim().toLowerCase();
            if (!query.equals("0")) {
                List<Integer> result = positionalIndex.searchPhrase(query);
                if (!result.isEmpty()) {
                    System.out.println("Documents matching the query:");
                    for (int docId : result) {
                        System.out.print("Doc:" + docId  + "  ");
                        System.out.println();
                    }
                } else {
                    System.out.println("No documents match the query.");
                }
            }
        }
        System.out.println("Exiting...");
    }
}


