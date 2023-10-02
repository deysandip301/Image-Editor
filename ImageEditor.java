import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.SocketPermission;
import java.util.*;
import java.awt.*;


public class ImageEditor {

    public static BufferedImage convertToGrayScale(BufferedImage inputImage) {
        int height = inputImage.getHeight();
        int width = inputImage.getWidth();
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for (int i=0; i<height; i++) {
            for (int j =0 ; j<width; j++) {
                outputImage.setRGB(j, i, inputImage.getRGB(j, i));
            }
        }
        return outputImage;
    }

    

    

    public static int truncateBrightness(int color) {
        if (color<0) return 0;
        if (color>255) return 255;
        return color;
    }
    

    public static BufferedImage increaseBrightness(BufferedImage inputImage, int percent) {
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i=0; i<height ; i++) {
            for (int j =0; j<width; j++) {
                Color pixel = new Color(inputImage.getRGB(j, i));
                
                int red = pixel.getRed();
                red = (int)(red*(1+percent/100d));
                red = truncateBrightness(red);
                int green = pixel.getGreen();
                green = (int)(green*(1+percent/100d));
                green = truncateBrightness(green);
                int blue = pixel.getBlue();
                blue = (int)(blue*(1+percent/100d));
                blue = truncateBrightness(blue);

                outputImage.setRGB(j, i, ((red << 16) | (green << 8) | blue));
            }
        }
        return outputImage;
    }

    public static BufferedImage mirrorImage(BufferedImage inputImage) {
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        for (int i=0; i<height; i++) {
            for (int j=0; j<width; j++) {
                outputImage.setRGB(width-j-1, i, inputImage.getRGB(j, i));
            }
        }
        return outputImage;
    }

    public static BufferedImage flipVertically(BufferedImage inputImage) {
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();
        BufferedImage outputImage = new BufferedImage(width , height, BufferedImage.TYPE_INT_RGB );
        for (int i=0; i<height ; i++) {
            for (int j =0; j<width; j++) {
                outputImage.setRGB(j, height-i-1, inputImage.getRGB(j, i));
            }
        }
        return outputImage;
    }

    public static BufferedImage transposeImage(BufferedImage inputImage) {
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();
        BufferedImage outputImage = new BufferedImage(height, width, BufferedImage.TYPE_INT_RGB);
        
        for (int i=0; i<height; i++) {
            for (int j=0; j<width; j++) {
                outputImage.setRGB(i, j, inputImage.getRGB(j, i));
            }
        }
        return outputImage;
    }

    public static BufferedImage rotateClockwise(BufferedImage inputImage) {
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();
        BufferedImage outputImage = new BufferedImage(height, width, BufferedImage.TYPE_INT_RGB);
        outputImage = mirrorImage(transposeImage(inputImage));
        return outputImage;
    }

    static BufferedImage rotateAntiClockwise(BufferedImage image){
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage outputImage = new BufferedImage(height, width, BufferedImage.TYPE_INT_RGB);

        for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                outputImage.setRGB(i, j, image.getRGB(width-j-1, i));
            }
        }
        return outputImage;
    }

    
    public static BufferedImage pixelete(BufferedImage image,int value){
        int width = image.getWidth();
        int height = image.getHeight();

        int avg_array[][] = new int[height+1][width+1];

        for(int row=0; row<height-(height%value); row+=value){
            for(int column=0; column<width-(width%value); column+=value){
                float color_avg [] = new float[3];
                for(int row_pool=row; row_pool<row+value; row_pool++){
                    for(int col_pool=column; col_pool<column+value; col_pool++){
                        Color pixel = new Color (image.getRGB(col_pool,row_pool));
                        color_avg[0] += pixel.getRed()/(float)(value*value);
                        color_avg[1] += pixel.getGreen()/(float)(value*value);
                        color_avg[2] += pixel.getBlue()/(float)(value*value);
                    }
                }
                Color newPixel = new Color((int)color_avg[0], (int)color_avg[1], (int)color_avg[2]);
                avg_array[(row/value)][(column/value)] = newPixel.getRGB();
            }
        }

        if(height%value != 0){
            for(int column=0; column<width-(width%value); column+=value){
                float color_avg [] = new float[3];
                for(int row_pool=height-(value); row_pool<height; row_pool++){
                    for(int col_pool=column; col_pool<column+value; col_pool++){
                        Color pixel = new Color (image.getRGB(col_pool,row_pool));
                        color_avg[0] += pixel.getRed()/(float)(value*value);
                        color_avg[1] += pixel.getGreen()/(float)(value*value);
                        color_avg[2] += pixel.getBlue()/(float)(value*value);
                    }
                }
                Color newPixel = new Color((int)color_avg[0], (int)color_avg[1], (int)color_avg[2]);
                avg_array[(height/value)][(column/value)] = newPixel.getRGB();
            }
        }

        if(width%value!=0){
            for(int row=0; row<height-(height%value); row+=value){
                float color_avg [] = new float[3];
                for(int row_pool=row; row_pool<row+value; row_pool++){
                    for(int col_pool=width-(value); col_pool<width; col_pool++){
                        Color pixel = new Color (image.getRGB(col_pool,row_pool));
                        color_avg[0] += pixel.getRed()/(float)(value*value);
                        color_avg[1] += pixel.getGreen()/(float)(value*value);
                        color_avg[2] += pixel.getBlue()/(float)(value*value);
                    }
                }
                Color newPixel = new Color((int)color_avg[0], (int)color_avg[1], (int)color_avg[2]);
                avg_array[(row/value)][(width/value)] = newPixel.getRGB();
            }
        }

        float color_avg [] = new float[3];
        

        for(int row_pool = height-value; row_pool<height; row_pool++){
            for(int col_pool = width-value; col_pool<width; col_pool++){
                Color pixel = new Color (image.getRGB(col_pool,row_pool));
                color_avg[0] += pixel.getRed()/(float)((value)*(value));
                color_avg[1] += pixel.getGreen()/(float)((value)*(value));
                color_avg[2] += pixel.getBlue()/(float)((value)*(value));
            }
        }

        Color newPixel = new Color((int)color_avg[0], (int)color_avg[1], (int)color_avg[2]);
        avg_array[(height/value)][(width/value)] = newPixel.getRGB();

        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for(int row=0; row<height; row++){
            for(int column=0; column<width; column++){
                newImage.setRGB(column, row, avg_array[row/value][column/value]);
            }
        }
        return newImage;
    }
    
    public static void printChoices() {
        System.out.println("Enter 1 to convert to grayscale : ");
        System.out.println("Enter 2 to increase brightness : ");
        System.out.println("Enter 3 to flip the image horizontally : ");
        System.out.println("Enter 4 to flip the image vertically : ");
        System.out.println("Enter 5 to convert to transpose : ");
        System.out.println("Enter 6 to rotate the image clockwise : ");
        System.out.println("Enter 7 to rotate the image Anti-clockwise : ");
        System.out.println("Enter 8 to pixelete the image : ");
        System.out.println("Enter 9 to exit  : ");
    }

    public static void main(String args[]) {

        Scanner sc = new Scanner(System.in);
        int choice=-1;
        printChoices();
        
        
        
        
        while (choice!=9) {
            
            System.out.println("Enter the path of the image:");
            String path = sc.nextLine();
            
        try{
            File inputFile = new File(path);
            BufferedImage image = ImageIO.read(inputFile);
            BufferedImage outputImage = null;
            System.out.print("Enter the choice : ");
            choice = sc.nextInt();
            sc.nextLine();
            switch(choice){
                case 1:
                    outputImage = convertToGrayScale(image);
                    System.out.println("Grayscale image created as outputImage.jpg");
                    break;
                case 2:
                    System.out.println("Enter the value of brightness :");
                    int value = sc.nextInt();
                    sc.nextLine();
                    outputImage = increaseBrightness(image, value);
                    System.out.println("Brightness adjusted image created as outputImage.jpg");
                    break;
                case 3:
                    outputImage = mirrorImage(image);
                    System.out.println("Horizontally Flipped image created as outputImage.jpg");
                    break;
                case 4:
                    outputImage = flipVertically(image);
                    System.out.println("Vertically Flipped image created as outputImage.jpg");
                    break;
                case 5 :
                    outputImage = transposeImage(image);
                    System.out.println("transposed image created as outputImage.jpg");
                    break;
                case 6:
                    outputImage = rotateClockwise(image);
                    System.out.println("clockwise Rotated image created as outputImage.jpg");
                    break;
                case 7:
                    outputImage = rotateAntiClockwise(image);
                    System.out.println("Anti-clockwise Rotated image created as outputImage.jpg");
                    break;
                case 8:
                    System.out.println("Enter the value of blurr (1 to 100):");
                    int value_pixel = sc.nextInt();
                    sc.nextLine();
                    if (value_pixel < 1 || value_pixel > 100){
                        System.out.println("Invalid value");
                    }
                    else {
                        outputImage = pixelete(image, value_pixel);
                        System.out.println("Blurred image created as outputImage.jpg");
                    }
                    break;
                case 9 :
                    break;
                default:
                    System.out.println("Invalid input");
            }
            if  (choice == 9) {
                break;
            }


            File outputFile = new File("outputImage.jpg");
            ImageIO.write(outputImage, "jpg", outputFile);
        }
        catch(IOException e){
            
            System.out.println("Error: "+e);
        }
        

        }
    }
}
            


        