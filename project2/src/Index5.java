/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.io.*;
import java.util.*;


public class Index5 {

    //--------------------------------------------
    int N = 0;
    public Map<Integer, SourceRecord> sources;  // store the doc_id and the file name.

    public HashMap<String, DictEntry> index; // THe inverted index
    //--------------------------------------------

    // Constructor
    public Index5() {
        sources = new HashMap<Integer, SourceRecord>();
        index = new HashMap<String, DictEntry>();
    }


    // Setter for total number of documents
    public void setN(int n) {
        N = n;
    }


    //---------------------------------------------

    // Method to print a posting list
    public void printPostingList(Posting p) {
    	   // Iterator<Integer> it2 = hset.iterator();
        System.out.print("[");
        while (p != null) {
        	 /// -4- **** complete here ****
            System.out.print("" + p.docId);
            p = p.next;
            if (p != null) {
                System.out.print(",");
            }
        }
        System.out.println("]");
    }


    //---------------------------------------------
    // Method to print the inverted index dictionary
//    public void printDictionary() {
//        Iterator it = index.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry pair = (Map.Entry) it.next();
//            DictEntry dd = (DictEntry) pair.getValue();
//            System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "]       =--> ");
//            printPostingList(dd.pList);
//        }
//        System.out.println("------------------------------------------------------");
//        System.out.println("*** Number of terms = " + index.size());
//    }


    // Method to print the inverted index dictionary
    public void printDictionary() {
        TreeMap<String, DictEntry> sortedIndex = new TreeMap<>(index);

        Iterator<Map.Entry<String, DictEntry>> it = sortedIndex.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, DictEntry> pair = it.next();
            DictEntry dd = pair.getValue();
            System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "]       =--> ");
            printPostingList(dd.pList);
        }

        System.out.println("------------------------------------------------------");
        System.out.println("*** Number of terms = " + sortedIndex.size());
    }



    //-----------------------------------------------
    // Method to build the inverted index from files on disk
    public void buildIndex(String[] files) {  // from disk not from the internet
        int fid = 0;      // file id
        for (String fileName : files) {
            try (BufferedReader file = new BufferedReader(new FileReader(fileName))) {
                if (!sources.containsKey(fileName)) {
                    sources.put(fid, new SourceRecord(fid, fileName, fileName, "notext"));
                }
                String ln;
                int flen = 0;
                
                while ((ln = file.readLine()) != null) {
                    /// -2- **** complete here ****
                    ///**** hint   flen +=  ________________(ln, fid);
                    flen += indexOneLine(ln, fid); // Call to indexOneLine to process each line
                }
                sources.get(fid).length = flen;

            } catch (IOException e) {
                System.out.println("File " + fileName + " not found. Skip it");
            }
            fid++;
        }
        //   printDictionary();
    }

  

    //----------------------------------------------------------------------------
    // Method to index one line of text
    public int indexOneLine(String ln, int fid) {
        int flen = 0;

        String[] words = ln.split("\\W+");
        flen += words.length;
        for (String word : words) {
            word = word.toLowerCase();
            if (stopWord(word)) {
                continue;
            }
            word = stemWord(word);
            // check to see if the word is not in the dictionary
            // if not add it
            if (!index.containsKey(word)) {
                index.put(word, new DictEntry());
            }
            // add document id to the posting list
            if (!index.get(word).postingListContains(fid)) {
                index.get(word).doc_freq += 1; //set doc freq to the number of doc that contain the term 
                if (index.get(word).pList == null) {
                    index.get(word).pList = new Posting(fid);
                    index.get(word).last = index.get(word).pList;
                } else {
                    index.get(word).last.next = new Posting(fid);
                    index.get(word).last = index.get(word).last.next;
                }
            } else {
                index.get(word).last.dtf += 1;
            }
            //set the term_fteq in the collection
            index.get(word).term_freq += 1;
            if (word.equalsIgnoreCase("lattice")) {

                System.out.println("  <<" + index.get(word).getPosting(1) + ">> " + ln);
            }

        }
        return flen;
    }

//----------------------------------------------------------------------------  
    
    // Method to build the bi word index from files on disk
    public void buildIndex_bi_word(String[] files) {  // from disk not from the internet
        int fid = 0;      // file id
        for (String fileName : files) {
            try (BufferedReader file = new BufferedReader(new FileReader(fileName))) {
                if (!sources.containsKey(fileName)) {
                    sources.put(fid, new SourceRecord(fid, fileName, fileName, "notext"));
                }
                String ln;
                int flen = 0;
                
                while ((ln = file.readLine()) != null) {
                    /// -2- **** complete here ****
                    ///**** hint   flen +=  ________________(ln, fid);
                    flen += indexOneLine_bi_word(ln, fid); // Call to indexOneLine to process each line
                }
                sources.get(fid).length = flen;

            } catch (IOException e) {
                System.out.println("File " + fileName + " not found. Skip it");
            }
            fid++;
        }
        //   printDictionary();
    }

  

    //----------------------------------------------------------------------------
    // Method to index one line of text
    public int indexOneLine_bi_word(String ln, int fid) {
        int flen = 0;

        String[] words = ln.split("\\W+");
        flen += words.length;

        // Process individual words
        for (String word : words) {
            word = word.toLowerCase();
            if (stopWord(word)) {
                continue;
            }
            word = stemWord(word);
            indexWord(word, fid);
        }

        // Process BI_word combinations
        for (int i = 0; i < words.length - 1; i++) {
            String biword = words[i].toLowerCase() + "_" + words[i + 1].toLowerCase();
            indexWord(biword, fid);
        }

        return flen;
    }

    private void indexWord(String word, int fid) {
        if (!index.containsKey(word)) {
            index.put(word, new DictEntry());
        }

        if (!index.get(word).postingListContains(fid)) {
            index.get(word).doc_freq += 1;
            if (index.get(word).pList == null) {
                index.get(word).pList = new Posting(fid);
                index.get(word).last = index.get(word).pList;
            } else {
                index.get(word).last.next = new Posting(fid);
                index.get(word).last = index.get(word).last.next;
            }
        } else {
            index.get(word).last.dtf += 1;
        }

        index.get(word).term_freq += 1;
    }

  //----------------------------------------------------------------------------
    // Method to check if a word is a stop word
    boolean stopWord(String word) {
        if (word.equals("the") || word.equals("to") || word.equals("be") || word.equals("for") || word.equals("from") || word.equals("in")
                || word.equals("a") || word.equals("into") || word.equals("by") || word.equals("or") || word.equals("and") || word.equals("that")) {
            return true;
        }
        if (word.length() < 2) {
            return true;
        }
        return false;

    }


     //----------------------------------------------------------------------------
     // Method to stem a word (not implemented)
    String stemWord(String word) { //skip for now
        return word;
//        Stemmer s = new Stemmer();
//        s.addString(word);
//        s.stem();
//        return s.toString();
    }

    //----------------------------------------------------------------------------  
    // Method to intersect two posting lists
    public Posting intersect(Posting pL1, Posting pL2) {
        Posting answer = null;
        Posting last = null;

        while (pL1 != null && pL2 != null) {
            if (pL1.docId == pL2.docId) {
                if (answer == null) {
                    answer = new Posting(pL1.docId, pL1.dtf);
                    last = answer;
                } else {
                    last.next = new Posting(pL1.docId, pL1.dtf);
                    last = last.next;
                }
                pL1 = pL1.next;
                pL2 = pL2.next;
            } else if (pL1.docId < pL2.docId) {
                pL1 = pL1.next;
            } else {
                pL2 = pL2.next;
            }
        }

        return answer;
    }


    // Method to find documents containing a given phrase
    public String find_24_01(String phrase) { // any mumber of terms non-optimized search 
        String result = "";
        String[] words = phrase.split("\\W+");
        int len = words.length;
        
        //fix this if word is not in the hash table will crash...
        Posting posting = index.get(words[0].toLowerCase()).pList;
        int i = 1;
        while (i < len) {
            posting = intersect(posting, index.get(words[i].toLowerCase()).pList);
            i++;
        }
        while (posting != null) {
            //System.out.println("\t" + sources.get(num));
            result += "\t" + posting.docId + " - " + sources.get(posting.docId).title + " - " + sources.get(posting.docId).length + "\n";
            posting = posting.next;
        }
        return result;
    }


    // newww
    public String find_2(String phrase) {
        String result = "";
        List<String> terms = generateBiWords(phrase);
        List<Posting> postings = new ArrayList<>();

        // Retrieve postings for each term
        for (String term : terms) {
            if (index.containsKey(term.toLowerCase())) {
                postings.add(index.get(term.toLowerCase()).pList);
            }
        }

        // Intersect postings to find documents containing all terms
        Posting intersection = intersectPostings(postings);

        // Collect results
        while (intersection != null) {
            result += "\t" + intersection.docId + " - " + sources.get(intersection.docId).title + " - " + sources.get(intersection.docId).length + "\n";
            intersection = intersection.next;
        }
        return result;
    }

    // Function to generate bi-words from a query
    public List<String> generateBiWords(String phrase) {
        List<String> biwords = new ArrayList<>();
        String[] words = phrase.split("\\W+");
        for (int i = 0; i < words.length - 1; i++) {
            String biword = words[i].toLowerCase() + "_" + words[i + 1].toLowerCase();
            biwords.add(biword);
        }
        return biwords;
    }

    // Function to intersect multiple posting lists
    private Posting intersectPostings(List<Posting> postings) {
        if (postings.isEmpty()) {
            return null;
        }

        Posting result = null;
        Posting current = null;
        boolean allNull = false;

        // Iterate through postings until all are exhausted
        while (!allNull) {
            allNull = true;
            boolean first = true;

            for (Posting posting : postings) {
                if (posting == null) {
                    continue;
                }
                allNull = false;

                if (first) {
                    result = new Posting(posting.docId);
                    current = result;
                    first = false;
                } else {
                    if (posting.docId == current.docId) {
                        current.next = new Posting(posting.docId);
                        current = current.next;
                    }
                }
                posting = posting.next;
            }
        }

        return result;
    }





    //---------------------------------
    // Method to sort an array of words (bubble sort)
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

     //---------------------------------
    // Method to store the inverted index and document information to disk
    public void store(String storageName) {
        try {
            String pathToStorage = "C:\\Users\\nadae\\Desktop\\BiwordBonus\\tmp11\\rl\\collection\\"+storageName;   // change the path
            Writer wr = new FileWriter(pathToStorage);
            for (Map.Entry<Integer, SourceRecord> entry : sources.entrySet()) {
                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue().URL + ", Value = " + entry.getValue().title + ", Value = " + entry.getValue().text);
                wr.write(entry.getKey().toString() + ",");
                wr.write(entry.getValue().URL.toString() + ",");
                wr.write(entry.getValue().title.replace(',', '~') + ",");
                wr.write(entry.getValue().length + ","); //String formattedDouble = String.format("%.2f", fee );
                wr.write(String.format("%4.4f", entry.getValue().norm) + ",");
                wr.write(entry.getValue().text.toString().replace(',', '~') + "\n");
            }
            wr.write("section2" + "\n");

            Iterator it = index.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                DictEntry dd = (DictEntry) pair.getValue();
                //  System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "] <" + dd.term_freq + "> =--> ");
                wr.write(pair.getKey().toString() + "," + dd.doc_freq + "," + dd.term_freq + ";");
                Posting p = dd.pList;
                while (p != null) {
                    //    System.out.print( p.docId + "," + p.dtf + ":");
                    wr.write(p.docId + "," + p.dtf + ":");
                    p = p.next;
                }
                wr.write("\n");
            }
            wr.write("end" + "\n");
            wr.close();
            System.out.println("=============EBD STORE=============");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//=========================================

    public boolean storageFileExists(String storageName){
        File f = new File("/home/ehab/tmp11/rl/"+storageName);
        if (f.exists() && !f.isDirectory())
            return true;
        return false;
            
    }
//----------------------------------------------------    
    public void createStore(String storageName) {
        try {
            String pathToStorage = "/home/ehab/tmp11/"+storageName;
            Writer wr = new FileWriter(pathToStorage);
            wr.write("end" + "\n");
            wr.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//----------------------------------------------------      
     //load index from hard disk into memory
    public HashMap<String, DictEntry> load(String storageName) {
        try {
            String pathToStorage = "/home/ehab/tmp11/rl/"+storageName;         
            sources = new HashMap<Integer, SourceRecord>();
            index = new HashMap<String, DictEntry>();
            BufferedReader file = new BufferedReader(new FileReader(pathToStorage));
            String ln = "";
            int flen = 0;
            while ((ln = file.readLine()) != null) {
                if (ln.equalsIgnoreCase("section2")) {
                    break;
                }
                String[] ss = ln.split(",");
                int fid = Integer.parseInt(ss[0]);
                try {
                    System.out.println("**>>" + fid + " " + ss[1] + " " + ss[2].replace('~', ',') + " " + ss[3] + " [" + ss[4] + "]   " + ss[5].replace('~', ','));

                    SourceRecord sr = new SourceRecord(fid, ss[1], ss[2].replace('~', ','), Integer.parseInt(ss[3]), Double.parseDouble(ss[4]), ss[5].replace('~', ','));
                    //   System.out.println("**>>"+fid+" "+ ss[1]+" "+ ss[2]+" "+ ss[3]+" ["+ Double.parseDouble(ss[4])+ "]  \n"+ ss[5]);
                    sources.put(fid, sr);
                } catch (Exception e) {

                    System.out.println(fid + "  ERROR  " + e.getMessage());
                    e.printStackTrace();
                }
            }
            while ((ln = file.readLine()) != null) {
                //     System.out.println(ln);
                if (ln.equalsIgnoreCase("end")) {
                    break;
                }
                String[] ss1 = ln.split(";");
                String[] ss1a = ss1[0].split(",");
                String[] ss1b = ss1[1].split(":");
                index.put(ss1a[0], new DictEntry(Integer.parseInt(ss1a[1]), Integer.parseInt(ss1a[2])));
                String[] ss1bx;   //posting
                for (int i = 0; i < ss1b.length; i++) {
                    ss1bx = ss1b[i].split(",");
                    if (index.get(ss1a[0]).pList == null) {
                        index.get(ss1a[0]).pList = new Posting(Integer.parseInt(ss1bx[0]), Integer.parseInt(ss1bx[1]));
                        index.get(ss1a[0]).last = index.get(ss1a[0]).pList;
                    } else {
                        index.get(ss1a[0]).last.next = new Posting(Integer.parseInt(ss1bx[0]), Integer.parseInt(ss1bx[1]));
                        index.get(ss1a[0]).last = index.get(ss1a[0]).last.next;
                    }
                }
            }
            System.out.println("============= END LOAD =============");
            //    printDictionary();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return index;
    }
    public static Set<String> getIntersection(String str1, String str2) {
        // Split strings into lines
        String[] lines1 = str1.split("\n");
        String[] lines2 = str2.split("\n");
        
        // Create a set to store the common lines
        Set<String> intersection = new HashSet<>();
        
        // Convert lines2 to a set for faster lookup
        Set<String> set2 = new HashSet<>();
        for (String line : lines2) {
            set2.add(line);
        }
        
        // Check each line of lines1 for intersection with lines2
        for (String line : lines1) {
            if (set2.contains(line)) {
                intersection.add(line);
            }
        }
        
        return intersection;
    }
}

//=====================================================================

