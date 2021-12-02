/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.fh.integration_tests;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;
import nl.fh.gamereport.GameReport;
import nl.fh.parser.PGN_Reader;
import nl.fh.parser.TolerantReader;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author frank
 */
public class PhilidorTest {
    
    public PhilidorTest() {
    }
      
    @Test
    public void testPhilidor(){
        // read a pgn file
        String filePath = "./pgn/Philidor.pgn";
        StringBuilder sb = new StringBuilder();

        try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8))
        {
                stream.forEach(s -> sb.append(s).append("\n"));
        }
        catch (IOException e){}
        String pgn = sb.toString();
        assertTrue(pgn.length() > 0);
                
        // process the pgn file
        PGN_Reader parser = new TolerantReader();
        List<GameReport> reports = parser.getGames(pgn);
        assertEquals(6, reports.size());
                
    }   
}