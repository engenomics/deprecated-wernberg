package org.engenomics.wernberg;

import ar.com.hjg.pngj.*;
import ar.com.hjg.pngj.chunks.ChunkCopyBehaviour;
import ar.com.hjg.pngj.chunks.PngChunkTextVar;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {
    public static String DIR = System.getProperty("user.dir");
    public static String REL_PATH = DIR + "../../data/genomes/chr1/";

    public static boolean isSRGB = false;

    public static void main(String[] args) throws IOException {
        new Main().run();
    }

    public void run() throws IOException {
        String path = REL_PATH + "g1.fa";

        List<String> lines = Files.readAllLines(Paths.get(path), Charset.defaultCharset());


        int limit = 2000000; //lines.size() (3578897)

        int complete = 0;
        long start = System.currentTimeMillis();
        int percentage = -1;

        ImageInfo info = new ImageInfo(70, limit, 8, true);
        PngWriter pngw = new PngWriter(new File("g.png"), info, true);
        for (int i = 0; i < limit; i++) {
            complete++;
            int newPercentage = i * 100 / limit;
            if (newPercentage > percentage) {
                //Get percentage
                percentage = newPercentage;
                newPercentage = 0;

                //Get estimated time
                long elapsedTime = System.currentTimeMillis() - start;
                String estimatedTimeRemaining = getRemainingTime(complete, limit, elapsedTime);

                System.out.println(percentage + "% complete. Estimated time remaining: " + estimatedTimeRemaining + ".");
            }

            int[] rgbs = new int[70];

            for (int j = 0; j < 70; j++) {
                if (j < lines.get(i).length()) {
                    if (isSRGB) {
                        rgbs[j] = toSRGB(lines.get(i).charAt(j));
                    } else {
                        rgbs[j] = toRGB(lines.get(i).charAt(j));
                    }
                } else {
                    rgbs[j] = new Color(0, 0, 0, 0).getRGB(); //Transparent
                }
            }

            ImageLineInt line = new ImageLineInt(info);
            for (int k = 0; k < rgbs.length; k++) {
                if (isSRGB) {
                    ImageLineHelper.setPixelRGB8(line, k, rgbs[k]);
                } else {
                    ImageLineHelper.setPixelRGBA8(line, k, rgbs[k]);
                }
            }

//            System.out.println("Writing row " + i + "...");
            pngw.writeRow(line, i);
//            System.out.println("Finished writing row " + i + "!");
        }
        pngw.end();

        System.out.println("Complete!");
    }

    public int toRGB(char k) { // TODO: Replace with map or switch
        if (k == 'A') { // Red
            return new Color(255, 0, 0).getRGB();
        }
        if (k == 'C') { // Green
            return new Color(0, 255, 0).getRGB();
        }
        if (k == 'T') { // Blue
            return new Color(0, 0, 255).getRGB();
        }
        if (k == 'G') { // Yellow
            return new Color(255, 255, 0).getRGB();
        }
        if (k == 'N') { // Black
            return new Color(0, 0, 0).getRGB();
        }
        if (k == '>') { // Purple: comment
            return new Color(127, 0, 127).getRGB();
        }
        return -1;
    }

    public int toSRGB(char k) { //Stylized black and blue RGB
        if (k == 'A') { // Red
            return new Color(0, 0, 255).getRGB();
        }
        if (k == 'C') { // Green
            return new Color(0, 0, 204).getRGB();
        }
        if (k == 'T') { // Blue
            return new Color(0, 0, 153).getRGB();
        }
        if (k == 'G') { // Yellow
            return new Color(0, 0, 102).getRGB();
        }
        if (k == 'N') {
            return new Color(0, 0, 51).getRGB();
        }
        return -1;
    }

    //From http://stackoverflow.com/q/9027317/2930268
    public String getRemainingTime(int complete, int limit, long msSpent) {
        long expectedMillisecondsRemaining = (long) ((double) limit * (double) msSpent / (double) complete - (double) msSpent);
        String time = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(expectedMillisecondsRemaining),
                TimeUnit.MILLISECONDS.toMinutes(expectedMillisecondsRemaining) -
                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(expectedMillisecondsRemaining)),
                TimeUnit.MILLISECONDS.toSeconds(expectedMillisecondsRemaining) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(expectedMillisecondsRemaining)));

        return "" + time + "";
    }
}
