 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.ri.assigment4;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.logging.Level;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação 
 * @author Miguel Oliveira 72638, brasfilipe@ua.pt
 * @author Tiago Henriques 73046, henriquestiago@ua.pt
 */

/**
 * Word2VecGenerator.
 */
public class Word2VecGenerator {

    private static String filePath;
    private static SentenceIterator iter;
    private static Word2Vec vec;
    private static final String filename = "./data/aerodynamics_sentences.txt";
    
    /**
     * Word2VecGenerator Constructor.
     */
    public Word2VecGenerator(){
        filePath = filename;
        initialize();
    }
    
    /**
     * Created by agibsonccc on 10/9/14.
     *
     * Neural net that processes text into wordvectors. See below url for an in-depth explanation.
     * https://deeplearning4j.org/word2vec.html
     */
    private void initialize() {
        
        try {        
            iter = new BasicLineIterator(filePath);
        } catch (FileNotFoundException ex) {
            java.util.logging.Logger.getLogger(Word2VecGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }

       TokenizerFactory t = new DefaultTokenizerFactory();
        
        /*
            CommonPreprocessor will apply the following regex to each token: [\d\.:,"'\(\)\[\]|/?!;]+
            So, effectively all numbers, punctuation symbols and some special symbols are stripped off.
            Additionally it forces lower case for all tokens. OLD BUT GOLD
         */
        t.setTokenPreProcessor(new CommonPreprocessor());

        vec = new Word2Vec.Builder()
                .minWordFrequency(5)
                .iterations(1)
                .layerSize(100)
                .seed(42)
                .windowSize(5)
                .iterate(iter)
                .tokenizerFactory(t)
                .build();

        vec.fit();
    }
    
    /**
     * Método que retorna as 4 palavras mais similares do termo passado como argumento.
     * @param term
     * @param numWords
     * @return 
     */
    public Collection<String> generateNearestWords(String term, int numWords) {   
        // Prints out the closest 10 words to "day". An example on what to do with these Word Vectors.
        Collection<String> lst = vec.wordsNearest(term, numWords);
        return lst;
    }  
}