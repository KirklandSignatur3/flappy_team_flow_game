package com.kirkland.game.sprites;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Log {
    private FileWriter fileWriter;
    private CSVPrinter csvPrinter;
    public static final int P1_JUMP = 1;
    public static final int P2_JUMP = 2;
    public static final int P1_OFF_TIME_PRESS = 3;
    public static final int P2_OFF_TIME_PRESS = 4;
    public static final int HIT_COIN = 5;
    public static final int MISS_COIN = 6;
    public static final int START_GAME = 7;
    public static final int END_GAME = 8;

    public Log(String filepath, String gametype){
        LocalDateTime date = java.time.LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd_H.m.s");
        String formatted_date = date.format(formatter);
        String outputFile = filepath +gametype+ formatted_date +".csv";
        // def9j
        try {
            fileWriter = new FileWriter(outputFile);
            csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT.withHeader("time","p1 jump", "p2 jump", "p1 off time press", "p2 off time press",
                    "hit coin", "miss coin", "start game", "end game"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void log_event(float time, int event){
        /**
         * logs an event to the CSV file.
         * @param:
         * 1. p1 jump
         * 2. p2 jump
         * 3. p1 off time press
         * 4. p2 off time press
         * 5. collide with coin
         * 6. miss coin
         * 7. Start Game
         * 8. End game
         */

        try {
            switch(event){
                case 1: // p1 jump
                    csvPrinter.printRecord(time, 1, 0, 0, 0, 0, 0, 0, 0);
                    break;
                case 2: // p2 jump
                    csvPrinter.printRecord(time, 0, 1, 0, 0, 0, 0, 0, 0);
                    break;
                case 3: // p1 jump
                    csvPrinter.printRecord(time, 0, 0, 1, 0, 0, 0, 0, 0);
                    break;
                case 4: // p2 jump
                    csvPrinter.printRecord(time, 0, 0, 0, 1, 0, 0, 0, 0);
                    break;
                case 5:
                    csvPrinter.printRecord(time, 0, 0, 0, 0, 1, 0, 0, 0);
                    break;
                case 6:
                    csvPrinter.printRecord(time, 0, 0, 0, 0, 0, 1, 0, 0);
                    break;
                case 7:
                    csvPrinter.printRecord(time, 0, 0, 0, 0, 0, 0, 1, 0);
                    break;
                case 8:
                    csvPrinter.printRecord(time, 0, 0, 0, 0, 0, 0, 0, 1);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        // closes the filse
        try {
            fileWriter.close();
            csvPrinter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

