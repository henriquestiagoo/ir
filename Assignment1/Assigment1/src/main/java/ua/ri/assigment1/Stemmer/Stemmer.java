/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.ri.assigment1.Stemmer;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.tartarus.snowball.SnowballStemmer;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação 
 * @author Miguel Oliveira 72638, brasfilipe@ua.pt
 * @author Tiago Henriques 73046, henriquestiago@ua.pt
 */
public class Stemmer {
    
    /**
     * Stemmer algorithm.
     */
    private final String algorithm;
    
    /**
     * Construtor da classe Stemmer.
     * @param algorithm
     */
    public Stemmer(String algorithm) {
        this.algorithm = algorithm;
    }
    
    /*
     * Método que retorna uma palavra resultante do processo de Stemmização.
     * A partir da classe TestApp do libstemmer_java
     * http://snowball.tartarus.org/download.html
    */
    public String getSnowballStemmer(String token) {
        Class stemClass = null;
        try {
            stemClass = Class.forName("org.tartarus.snowball.ext." + algorithm + "Stemmer");
            SnowballStemmer stemmer = (SnowballStemmer) stemClass.newInstance();
            stemmer.setCurrent(token);
            if(stemmer.stem()){
                return stemmer.getCurrent();
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException  ex) {
            Logger.getLogger(Stemmer.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return token;
    }
    
     /**
     * metodo que retorna o algoritmo usada na "tokenizacao".
     * @return algorithm
     */
    public String getAlgorithm() {
        return algorithm;
    }
}