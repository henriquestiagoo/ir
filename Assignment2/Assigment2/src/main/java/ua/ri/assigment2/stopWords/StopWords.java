/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.ri.assigment2.stopWords;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação 
 * @author Miguel Oliveira 72638, brasfilipe@ua.pt
 * @author Tiago Henriques 73046, henriquestiago@ua.pt
 */
public class StopWords {
    
    private List<String> stopWords;
    
    /**
     * Constructor da classe StopWords.
     * @param path 
     */
    public StopWords(Path path) {
        // Get stopWords from file
       stopWords = new ArrayList<>();
       
       try(Stream<String> lines = Files.lines(path)) {
            lines.map(line -> line.split(" "))
                 .filter(line -> line[0].length() > 0)
                 .filter(line -> !line[0].equals("|"))
                 .forEach(line -> stopWords.add(line[0]));
        } catch(IOException ex) {
            System.out.println(ex.toString());
        }
       Collections.sort(stopWords);
    }    
    
    /**
     * Função que verifica se uma palavra (token) é stop word.
     * @param word
     * @return se a palavra é stop word ou não
     */
    public boolean isStopWord(String word) {
        return stopWords.contains(word);
    }
    
    /**
     * Função que retorna o tamanho da lista de stop words
     * @return tamanho lista
     */
    public int getStopWordsSize() {
        return stopWords.size();
    }
    
    /**
     * Função que retorna a lista de stop words
     * @return stopWords
     */
    public List<String> getStopWords() {
        return stopWords;
    }
}
