import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 *
 * @author ehab
 */



public class Test {

    public static void main(String args[]) throws IOException {
        
        Index5 index = new Index5();
        // Change the directory to your collection directory
        String filesDirectory = "tmp11/rl/collection/";
       
        File file = new File(filesDirectory);
        String[] fileList = file.list();

        fileList = index.sort(fileList);
        index.setN(fileList.length);

        for (int i = 0; i < fileList.length; i++) {
            fileList[i] = filesDirectory + fileList[i];
        }


        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Index Type\n1-inverted index\n2-bi_word index\n");
        int index_type = scanner.nextInt();
        if (index_type == 1) {
            index.buildIndex(fileList);

            // Test phrase for inverted index

            String test3 = "data  should plain greatest comif"; // data  should plain greatest comif
            System.out.println("Boo0lean Model result = \n" + index.find_24_01(test3));

            index.store("index");
            index.printDictionary();

            // Interactive search
            String phrase = "";
            String result = "";
            

            do {
                System.out.println("Enter search phrase: ");
                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                phrase = in.readLine();
                phrase = phrase.replaceAll("\"", "");

                //     System.out.println(result);

                if (!phrase.isEmpty()) {

                    System.out.println("Search Result:");
                    System.out.println(index.find_24_01(phrase));
                }
            } while (!phrase.isEmpty());

        } else {
            index.buildIndex_bi_word(fileList);

            index.store("index");
            index.printDictionary();

            // Interactive search
            String phrase = "";
            String result= "";

            do {
                System.out.println("Enter search phrase: ");
                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                phrase = in.readLine();
                if (phrase.startsWith("\"") && phrase.endsWith("\"")) {

                
                phrase = phrase.replaceAll("\"", "");

                // Split the input phrase into words
                String[] words = phrase.split("\\s+");
                StringBuilder biWordPhrase = new StringBuilder();
                    for (int i = 0; i < words.length -1 ; i++) {
                        biWordPhrase.append(words[i]).append("_").append(words[i+1]).append(" ");
                    }
                    // Trim any trailing space
                    biWordPhrase.setLength(biWordPhrase.length() - 1);

                    System.out.println("Search Result:");
                    System.out.println(index.find_24_01(biWordPhrase.toString()));

                }else if(!phrase.startsWith("\"") && phrase.endsWith("\"")){
                    phrase = phrase.replaceAll("\"", "");
                    String[] words = phrase.split("\\s+");
                    String res1=index.find_24_01(words[0]);
                    StringBuilder biWordPhrase = new StringBuilder();
                    for (int i = 1; i < words.length -1 ; i++) {
                        biWordPhrase.append(words[i]).append("_").append(words[i+1]).append(" ");
                    }
                    // Trim any trailing space
                    biWordPhrase.setLength(biWordPhrase.length() - 1);
                    String res2 =index.find_24_01(biWordPhrase.toString());
                    System.out.println("Search Result:");
                    System.out.println(index.getIntersection(res1,res2));
                    



                }
                else if(phrase.startsWith("\"") && (!phrase.endsWith("\""))){
                    phrase = phrase.replaceAll("\"", "");
                    String[] words = phrase.split("\\s+");
                    StringBuilder biWordPhrase = new StringBuilder();
                    for (int i = 0; i < words.length -2 ; i++) {
                        biWordPhrase.append(words[i]).append("_").append(words[i+1]).append(" ");
                    }
                    biWordPhrase.setLength(biWordPhrase.length() - 1);
                    String res1 =index.find_24_01(biWordPhrase.toString());
                    String res2=index.find_24_01(words[words.length -1]);
                    System.out.println("Search Result:");
                    System.out.println(index.getIntersection(res1,res2));

                }
                else {
                    String[] words = phrase.split("\\s+");
                    System.out.println("Search Result:");
                    System.out.println(index.find_24_01(words[0]));
                }
                // If there are more than one word, construct a BI_word phrase
                
        
            } while (!phrase.isEmpty());
        }


    }
    }
