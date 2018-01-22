/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.ri.assigment3.corpusReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Universidade de Aveiro, DETI, Recuperação de Informação 
 * @author Miguel Oliveira 72638, brasfilipe@ua.pt
 * @author Tiago Henriques 73046, henriquestiago@ua.pt
 */

/**
 * Create a corpus reader class that returns, in turn, the contents of each document in a
    collection (corpus). The document should be processed to ignore any irrelevant sections
    and clean any existing tags (e.g. xml markup).
    Your implementation should be modular and easily extended/adapted to other corpora
    structures. 
 */
public class CorpusReader {
    
    private List<Path> pathFiles;
    
    /**
     * Constructor do CorpusReader que recebe o caminho onde os ficheiros a serem lidos se situa.
     * @param path 
     */
    public CorpusReader(Path path){
        // Get Files from directory
        pathFiles = new ArrayList<>(); 
        try (Stream<Path> lines = Files.list(path)) {
            lines.forEach(file -> pathFiles.add(file));
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
    }
    
    
    /**
     * Função usada para obter o texto de cada ficheiro. 
     * Recebe o tipo de ficheiros a serem lidos e invoca a função readXml.
     * @param fileType
     * @param position
     * @param xmlTags
     * @return tmp
     */
    public HashMap<Integer, String> getCorpusText(String fileType, int position, List<String> xmlTags) {
        HashMap<Integer, String> tmp = null;
        switch(fileType){
            case "xml":
                tmp = readXml(position, xmlTags);
        }
        return tmp;
    }
 
    /**
     * Método usado para ficheiros do tipo xml que retorna uma HashMap em que a chave é o identificador do
     * documento e o valor é o texto desse documento.
     * @param position
     * @param xmlTags
     * @return 
     */
    public HashMap<Integer, String> readXml(int position, List<String> xmlTags)  {
        String text = "";
        int docId = 0;
        HashMap<Integer, String> docAndText = new HashMap<>();
        try {
            //Create a DOM Builder Factory
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            
            //Get the DOM Builder
            DocumentBuilder builder = factory.newDocumentBuilder();
            
            //Use path to get file
            Path xmlFilePath = Paths
                    .get(pathFiles.get(position).toString())
                    .toAbsolutePath();
            
            //Parse the XML document
            Document document = builder.parse(xmlFilePath.toFile());
            document.getDocumentElement().normalize();
            
            NodeList nodeList = document.getElementsByTagName(xmlTags.get(0));
            Node nodeDocId = nodeList.item(0);
            docId = Integer.parseInt(nodeDocId.getTextContent().split("\n")[1]);
            
            nodeList = document.getElementsByTagName(xmlTags.get(1));
            
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node nodeTitle = nodeList.item(i);
                text += nodeTitle.getTextContent();
            }
            
            nodeList = document.getElementsByTagName(xmlTags.get(2));
            
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node nodeText = nodeList.item(i);
                text += nodeText.getTextContent();
            }
            docAndText.put(docId, text);
                  
        } catch (SAXException | IOException | ParserConfigurationException ex) {
            Logger.getLogger(CorpusReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return docAndText;
    }
    
    
    /**
     * Retorna o tamanho do corpus.
     * @return pathFiles.size()
     */
    public int getCorpusSize() {
        return pathFiles.size();
    }
    
    /**
     * Retorna o caminho para cada ficheiro em forma de String
     * @param filePosition
     * @return pathFiles.get(filePosition).toString(); 
     */
    public String getFilePath(int filePosition) {
        return pathFiles.get(filePosition).toString(); 
    }
}