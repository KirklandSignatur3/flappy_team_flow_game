package com.kirkland.game.sprites;

import com.labjack.LJUD;
import com.labjack.LJUDException;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.IntByReference;

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
    public static final int PLAYER_Y = 9;
    public static final int COIN_Y = 10;
    public static final int BG_CHANGE_WHITE = 11;
    public static final int BG_CHANGE_BLACK = 12;

    private int intHandle = 0;
//    private int posChannel = 1;
//    private DoubleByReference refVoltage =
//            new DoubleByReference(0.0);

    public Log(String filepath, String gametype){
        LocalDateTime date = java.time.LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd_H.m.s");
        String formatted_date = date.format(formatter);
        String outputFile = filepath +gametype+ " " + formatted_date +".csv";
        // def9j

        try {
            fileWriter = new FileWriter(outputFile);
            csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT.withHeader("time","p1 jump", "p2 jump", "p1 off time press", "p2 off time press",
                    "hit coin", "miss coin", "start game", "end game", "player Y", "coin Y", "BG_CHANGE_WHITE", "BG_CHANGE_BLACK"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // LABJACK
        try {
//            int intHandle = 0;
            IntByReference refHandle =
                    new IntByReference(0);


            //Positive channel = 1 (AIN1)
//            int posChannel = 1;
            //Negative channel = 199 (single-ended)
            int negChannel = 199;
            //Range = +/- 10V
            int range = LJUD.Constants.rgBIP10V;
            //Resolution Index = 8 (Effective resolution with
            //                      +/- 10V range = 16 bits)
            int resolutionIndex = 1;
            //Settling = 0 (Auto)
            int settling = 0;

            //Open the first found LabJack U3.
            LJUD.openLabJack(LJUD.Constants.dtU3,
                    LJUD.Constants.ctUSB, "1", 1, refHandle);
            intHandle = refHandle.getValue();

            /////////////// FOR TESTING ALL THECHANNELS

//            for  (int j=1; j<11; j++){
//                Thread.sleep(1000);
//                for (int i=0; i<8; i++) {
//                    LJUD.eDO(intHandle, i, j >> i & 1);
//                }
//                for (int i=0; i<8; i++) {
//                    LJUD.eDO(intHandle, i, 0);
//                }
//            }
            /////////////// FOR TESTING ALL THECHANNELS

        }
        catch(LJUDException le) {
            le.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }



    }

    public void log_event(float time, int event, float pos){
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
//        LJUD.eDO(intHandle, 0, 1);
//        LJUD.eDO(intHandle, 0, 0);
//
//        for  (int j=1; j<11; j++){
//            for (int i=0; i<8; i++) {
//                LJUD.eDO(intHandle, i, j >> i & 1);
//            }
//            for (int i=0; i<8; i++) {
//                LJUD.eDO(intHandle, i, 0);
//            }
//        } remove



        // checking when last bit is sent
//        if (event != 9 & event != 10){
//            for (int i=0; i<7; i++) {
//                LJUD.eDO(intHandle, i, (event >> i) & 1);
//            }
//            LJUD.eDO(intHandle, 7, 1); // adds 128 to final, we'd just filter
//            // add sleep(ms)?
//            LJUD.eDO(intHandle, 7, 0); // scuffed? can't we just send to them atll at the same time
//            // the channels already convert to a base 10 number based on the chanel that the trigger was recieved
//            for (int i=0; i<7; i++) {
//                LJUD.eDO(intHandle, i, 0);
//            }
//        }


//        System.out.println("AIN" + posChannel + " = " +
//                refVoltage.getValue() + " volts");

        try {
            switch(event){
                case 1: // p1 jump
                    csvPrinter.printRecord(time, 1, 0, 0, 0, 0, 0, 0, 0, pos, 0,0 ,0 );

                    System.out.println("p1 jump");
                    break;
                case 2: // p2 jump
                    csvPrinter.printRecord(time, 0, 1, 0, 0, 0, 0, 0, 0, pos, 0,0 ,0 );
                    System.out.println("p2 jump");
                    break;
                case 3: // p1 jump
                    csvPrinter.printRecord(time, 0, 0, 1, 0, 0, 0, 0, 0, pos, 0,0 ,0 );
                    System.out.println("p1 off time press");
                    break;
                case 4: // p2 jump
                    csvPrinter.printRecord(time, 0, 0, 0, 1, 0, 0, 0, 0, pos, 0,0 ,0 );
                    System.out.println("p2 off time press");
                    break;
                case 5:
                    csvPrinter.printRecord(time, 0, 0, 0, 0, 1, 0, 0, 0, 0, pos,0 ,0 );
                    System.out.println("hit coin");
                    break;
                case 6:
                    csvPrinter.printRecord(time, 0, 0, 0, 0, 0, 1, 0, 0, 0, pos,0 ,0 );
                    System.out.println("miss coin");

                    break;
                case 7:
                    csvPrinter.printRecord(time, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0,0 ,0 );
                    System.out.println("Start Game");
                    break;
                case 8:
                    csvPrinter.printRecord(time, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0,0 ,0 );
                    System.out.println("End Game");
                    break;
                case 9: // player Y
                    csvPrinter.printRecord(time, 0, 0, 0, 0, 0, 0, 0, 0, pos, 0,0 ,0 );
                    System.out.println("Player Y pos");
                    break;
                case 10: // Coin Y
                    csvPrinter.printRecord(time, 0, 0, 0, 0, 0, 0, 0, 0, 0, pos,0 ,0 );
                    System.out.println("Coin Y pos");
                    break;
                case 11: // BG_CHANGE_WHITE
                    csvPrinter.printRecord(time, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,1 ,0 );
                    System.out.println("BG_CHANGE_WHITE LOG");
                    break;
                case 12: // BG_CHANGE_BLACK
                    csvPrinter.printRecord(time, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,0 ,1 );
                    System.out.println("BG_CHANGE_WHITE LOG");
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

