package org.engenomics.wernberg;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static String DIR = System.getProperty("user.dir");
    public static String REL_PATH = DIR + "../../data/genomes/chr1/";

    public static void main(String[] args) throws IOException {
        new Main().run();
    }

    public void run() throws IOException {
        String path = REL_PATH + "g1.fa";

        List<String> lines = Files.readAllLines(Paths.get(path), Charset.defaultCharset());


        int limit = 200000; //lines.size() (3578897)

        BufferedImage genome = new BufferedImage(70, limit, BufferedImage.TYPE_INT_RGB);

        int percentage = 0;

        for (int i = 0; i < limit; i++) {
            if (i % 100000 == 0) {
                System.out.println(i);
            }

            for (int j = 0; j < 70; j++) {
                if (j < lines.get(i).length()) {
                    genome.setRGB(j, i, toRGB(lines.get(i).charAt(j)));
                } else {
                    genome.setRGB(j, i, 0);
                }
            }
        }

        System.out.println("Completed. Writing image...");



        ImageIO.write(genome, "PNG", new File("genome.png"));

        System.out.println("Finished writing image!");
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
        if (k == 'N') { //Black
            return new Color(0, 0, 0).getRGB();
        }
        return -1;
    }
    public int toSRGB(char k) { //Stylized black and blue RGB
        if (k == 'A') { // Red
            return 255;
        }
        if (k == 'C') { // Green
            return 204;
        }
        if (k == 'T') { // Blue
            return 153;
        }
        if (k == 'G') { // Yellow
            return 102;
        }
        if (k == 'N') {
            return 51;
        }
        return -1;
    }
}
