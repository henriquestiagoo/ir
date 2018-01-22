/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.ri.assigment1.tokenizer;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import ua.ri.assigment1.Stemmer.Stemmer;
import ua.ri.assigment1.stopWords.StopWords;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação 
 * @author Miguel Oliveira 72638, brasfilipe@ua.pt
 * @author Tiago Henriques 73046, henriquestiago@ua.pt
 */

/*
  * Create a tokenizer class that returns the tokens of an input text. In doing this, pay particular
    attention to characters that need special handling ('.'; ','; '-'; etc.).
    Integrate the Porter stemmer (http://snowball.tartarus.org/download.html) and a stopword
    filter. Use this list as default: http://snowball.tartarus.org/algorithms/english/stop.txt
*/
public class Tokenizer extends SimpleTokenizer{   
    
    private final Stemmer stemmer;
    private final StopWords sw;    
    
    /**
     * Construtor do Tokenizer.
     * @param dir_stopWords
     */
    public Tokenizer(Path dir_stopWords) {
        sw = new StopWords(dir_stopWords);
        stemmer = new Stemmer("porter");
    }
    
    
    /**
     * Método que faz o processamento do texto proveniente de um documento.
     * @param text
     * @return list
     */
    @Override
    public List textProcessor(String text) {
        // Remove all non-alphabetic characters and pontuation marks
        List<String> list = new ArrayList<>(Arrays.asList(text.split("[^A-Za-z]")));
       
        list = list.stream()
                .filter(s-> minTerm(s))
                .filter(s-> !sw.isStopWord(s))
                .map(s-> stemmer.getSnowballStemmer(s))
                .map(s-> s.toLowerCase())
                .collect(Collectors.toList());
        return list;
    }
}