/**
 * BitmapHacker is an Object that stores the image and also have more features on manipulating the image.
 *
 * @author Duc Tri Dang (Jin)
 * @version Feb 1 2024
 */

import java.io.*;

public class BitmapHacker2 {
    
    private int width;
    private int height;
    
    private int headerSize;
    private int[] header;
    
    private Pixel[][] pixels;

    private static final int BYTE_PER_PIXEL = 3;
    private static final int HEADERSIZE_ADDRESS = 0x000A;
    private static final int HEADER_ADDRESS = 0x0000;
    private static final int WIDTH_ADDRESS = 0x0012;
    private static final int HEIGHT_ADDRESS = 0x0016;

    /*
    * This constructor will take in a file then update the instance variable
    */
    public BitmapHacker2(File newFile) throws IllegalArgumentException
    {
        if (newFile == null){
            throw new IllegalArgumentException("The argument is null");
        }
        else if (!newFile.isFile()){
            throw new IllegalArgumentException("The argument is not a File");
        }
        else
        {
            try
            {
                RandomAccessFile raf = new RandomAccessFile(newFile,"r");
            
                headerSize = byteReading(HEADERSIZE_ADDRESS,raf);
                
                header = new int[headerSize];
                
                headerReading(HEADER_ADDRESS,raf);
                
                width = byteReading(WIDTH_ADDRESS,raf);
                // oriWidth = width;
                // if (width%4!=0)
                // {
                    // width = ((width/4)+1)*4;
                // }
                
                // 
                
                height = byteReading(HEIGHT_ADDRESS,raf);
                // oriHeight = height;
                // if (height%4!=0)
                // {
                    // height = ((height/4)+1)*4;
                // }
                
                pixels = new Pixel[height][width];
                
                pixelReading(headerSize,raf);
                
                raf.close();


            }
            catch (IOException e)
            {
                System.out.println("Input/Output error");
            }
            
        }
    }

    /*
    * This is a private method to read in every pixel after the reader and updating the pixels array
    */
    private void pixelReading(int address, RandomAccessFile raf)
    {
        
        try
        {
            raf.seek(address);
            int extraPadding = 0;
            if (width%4!=0)
            {
                extraPadding = 4 - (width*3)%4;
            }
            
            for (int i = height-1; i >=0 ; i--)
            {
                for (int j = 0;j<width; j++)
                {
                    int blue = raf.read();
                    int green = raf.read();
                    int red = raf.read();

                    pixels[i][j] = new Pixel(red, green, blue);
                }
                
                for (int k =0;k<extraPadding;k++)
                {
                    raf.read();
                }
            }

        }
        catch (IOException e)
        {
            System.out.println("Input/Output error");
        }
    }

    /*
    * This private method will read the header of the file and updating the reader.
    */
    private void headerReading(int address, RandomAccessFile raf)
    {
        try
        {
            raf.seek(address);
            for (int i = 0; i < headerSize; i++)
            {
                header[i] = raf.read();
            }
        }
        catch (IOException e)
        {
            System.out.println("Input/Output error");
        }
        
    }
    
    /*
    * This private method will read a specific byte.
    */
    private int byteReading(int address, RandomAccessFile raf)
    {
        try
        {
            
            raf.seek(address);
                
            int bitA = raf.read();
            
            int bitB = raf.read();

            int bitC = raf.read();

            int bitD = raf.read();

            int value = bitA + bitB*256 + bitC*256*256 + bitD*256*256*256;
            
            return value;
        }
        catch (IOException e)
        {
            System.out.println("Input/Output error");
        }
        
        return -1;
    }
    
    /*
    * This method will write the pixels in to a given file
    */
    public void writeImageToFile(File newFile)
    {
        if (newFile == null){
            throw new IllegalArgumentException("The argument is null");
        }
        else
        {
            if (newFile.exists())
                {
                    newFile.delete();
                }
            try
            {
            
                RandomAccessFile raf = new RandomAccessFile(newFile,"rw");
                
                raf.seek(0x0000);
                int extraPadding = 0;
                if (width%4!=0)
                {
                    extraPadding = 4 - (width*3)%4;
                }
                for (int i=0;i<headerSize;i++)
                {
                    raf.write(header[i]);
                }
                
                for (int i = height-1; i >=0 ; i--)//pixels.length-1
                {
                    for (int j = 0;j<width; j++)
                    {
                        raf.write(pixels[i][j].getBlue());
                        raf.write(pixels[i][j].getGreen());
                        raf.write(pixels[i][j].getRed());
                    }
                    
                    for (int k =0;k<extraPadding;k++)
                    {
                        raf.write(0);
                    }
                }
                
                raf.close();
            }
            catch (IOException e)
            {
                System.out.println("Input/Output error");
            }
        }
    }

    /*
    * The method will flip the entire pixels array, in other words, it will flip the picture upside down.
    */
    public void flip()
    {
        for (int i = 0; (int) i < height/2; i++)
        {

            Pixel[] tmp = pixels[i];
            pixels[i] = pixels[height - i-1];
            pixels[height - i-1] = tmp;
        }
    }
    
    /*
    * This method will blur the image by having for every pixel, update it to be the avarage value of its neighbors.
    */
    public void blur()
    {
        Pixel[][] blurring = new Pixel[height][width];

        for (int i = pixels.length-1; i >=0 ; i--)
        {
            for (int j = 0;j<pixels[i].length; j++)
            {
                int redAve = averageRed(i, j);
                int greenAve = averageGreen(i, j);
                int blueAve = averageBlue(i, j);

                Pixel newPixel = new Pixel(redAve, greenAve, blueAve);
                blurring[i][j] = newPixel;
            }
        }

        for (int i = pixels.length-1; i >=0 ; i--)
        {
            for (int j = 0;j<pixels[i].length; j++)
            {
                pixels[i][j] = blurring[i][j];
            }
        }
    }
    
    /*
    * This private method will update the average value for color Red.
    */
    private int averageRed(int i, int j)
    {
        int numberOfRed = 0;
        int numberOfNeighbor = 0;

        //pixels[i+1][j]
        if (i+1<=height-1)
        {
            numberOfNeighbor++;
            numberOfRed += pixels[i+1][j].getRed();
        }
        //pixels[i-1][j]
        if (i-1>=0)
        {
            numberOfNeighbor++;
            numberOfRed += pixels[i-1][j].getRed();
        }
        //pixels[i][j+1]
        if (j+1<=width-1)
        {
            numberOfNeighbor++;
            numberOfRed += pixels[i][j+1].getRed();
        }
        //pixels[i][j-1]
        if (j-1>=0)
        {
            numberOfNeighbor++;
            numberOfRed += pixels[i][j-1].getRed();
        }

        //pixels[i+1][j+1]
        if ((i+1<=height-1) && (j+1<=width-1))
        {
            numberOfNeighbor++;
            numberOfRed += pixels[i+1][j+1].getRed();
        }

        //pixels[i+1][j-1]
        if ((i+1<=height-1) && (j-1>=0))
        {
            numberOfNeighbor++;
            numberOfRed += pixels[i+1][j-1].getRed();
        }

        //pixels[i-1][j+1]
        if ((i-1>=0) && (j+1<=width-1))
        {
            numberOfNeighbor++;
            numberOfRed += pixels[i-1][j+1].getRed();
        }

        //pixels[i-1][j-1]
        if ((i-1>=0) && (j-1>=0))
        {
            numberOfNeighbor++;
            numberOfRed += pixels[i-1][j-1].getRed();
        }

        return (int) numberOfRed/numberOfNeighbor;
    }

    /*
    * This private method will update the average value for color Green.
    */
    private int averageGreen(int i, int j)
    {
        int numberOfGreen = 0;
        int numberOfNeighbor = 0;

        //pixels[i+1][j]
        if (i+1<=height-1)
        {
            numberOfNeighbor++;
            numberOfGreen += pixels[i+1][j].getGreen();
        }
        //pixels[i-1][j]
        if (i-1>=0)
        {
            numberOfNeighbor++;
            numberOfGreen += pixels[i-1][j].getGreen();
        }
        //pixels[i][j+1]
        if (j+1<=width-1)
        {
            numberOfNeighbor++;
            numberOfGreen += pixels[i][j+1].getGreen();
        }
        //pixels[i][j-1]
        if (j-1>=0)
        {
            numberOfNeighbor++;
            numberOfGreen += pixels[i][j-1].getGreen();
        }

        //pixels[i+1][j+1]
        if ((i+1<=height-1) && (j+1<=width-1))
        {
            numberOfNeighbor++;
            numberOfGreen += pixels[i+1][j+1].getGreen();
        }

        //pixels[i+1][j-1]
        if ((i+1<=height-1) && (j-1>=0))
        {
            numberOfNeighbor++;
            numberOfGreen += pixels[i+1][j-1].getGreen();
        }

        //pixels[i-1][j+1]
        if ((i-1>=0) && (j+1<=width-1))
        {
            numberOfNeighbor++;
            numberOfGreen += pixels[i-1][j+1].getGreen();
        }

        //pixels[i-1][j-1]
        if ((i-1>=0) && (j-1>=0))
        {
            numberOfNeighbor++;
            numberOfGreen += pixels[i-1][j-1].getGreen();
        }

        return (int) numberOfGreen/numberOfNeighbor;
    }

    /*
    * This private method will update the average value for color Blue.
    */
    private int averageBlue(int i, int j)
    {
        int numberOfBlue = 0;
        int numberOfNeighbor = 0;

        //pixels[i+1][j]
        if (i+1<=height-1)
        {
            numberOfNeighbor++;
            numberOfBlue += pixels[i+1][j].getBlue();
        }
        //pixels[i-1][j]
        if (i-1>=0)
        {
            numberOfNeighbor++;
            numberOfBlue += pixels[i-1][j].getBlue();
        }
        //pixels[i][j+1]
        if (j+1<=width-1)
        {
            numberOfNeighbor++;
            numberOfBlue += pixels[i][j+1].getBlue();
        }
        //pixels[i][j-1]
        if (j-1>=0)
        {
            numberOfNeighbor++;
            numberOfBlue += pixels[i][j-1].getBlue();
        }

        //pixels[i+1][j+1]
        if ((i+1<=height-1) && (j+1<=width-1))
        {
            numberOfNeighbor++;
            numberOfBlue += pixels[i+1][j+1].getBlue();
        }

        //pixels[i+1][j-1]
        if ((i+1<=height-1) && (j-1>=0))
        {
            numberOfNeighbor++;
            numberOfBlue += pixels[i+1][j-1].getBlue();
        }

        //pixels[i-1][j+1]
        if ((i-1>=0) && (j+1<=width-1))
        {
            numberOfNeighbor++;
            numberOfBlue += pixels[i-1][j+1].getBlue();
        }

        //pixels[i-1][j-1]
        if ((i-1>=0) && (j-1>=0))
        {
            numberOfNeighbor++;
            numberOfBlue += pixels[i-1][j-1].getBlue();
        }

        return (int) numberOfBlue/numberOfNeighbor;
    }

    /*
    * This method will enhance the given colour by 100 value.
    */
    public void enhance(String color) throws IllegalArgumentException
    {
        if (color.equals("blue") || color.equals("red") || color.equals("green"))
        {
            for (int i = pixels.length-1; i >=0 ; i--)
            {
                for (int j = 0;j<pixels[i].length; j++)
                {
                    if (color.equals("blue"))
                    {       
                        pixels[i][j].setBlue(pixels[i][j].getBlue()+100);
                    }
                    if (color.equals("green"))
                    {       
                        pixels[i][j].setGreen(pixels[i][j].getGreen()+100);
                    }
                    if (color.equals("red"))
                    {       
                        pixels[i][j].setRed(pixels[i][j].getRed()+100);
                    }
                }
            }
            
        }
        else
        {
            throw new IllegalArgumentException("Color unidentified.");
        }
    }
    /*
     * This method is used to hide the given file into the the pixels
     */
    public boolean hide(File newFile) throws IllegalArgumentException
    {
        if (newFile == null){
            throw new IllegalArgumentException("The argument is null");
        }
        else if (!newFile.isFile()){
            throw new IllegalArgumentException("The argument is not a File");
        }
        else
        {
            try{
                RandomAccessFile raf = new RandomAccessFile(newFile,"r");

                //Distribute the length into first four element in this list 
                int len = (int) raf.length();

                int[] byteStegan = new int[len+4];

                byteStegan[0] = len%256;
                byteStegan[1] = (len/256)%256;
                
                byteStegan[2] = ((len/256)/256)%256;
                byteStegan[3] = (((len/256)/256)/256)%256;
                
                // Read the rest of the file
                for (int i = 0; i < len; i++) {
                    int currByte = raf.read();
                    byteStegan[i+4] =currByte;
                }

                // Return if there are less space to fill
                if ((height*width*3)<((len+4)*4))
                {
                    raf.close();
                    return false;
                }

                // Adjust the pixels array
                int count=0;
                int pos=0;
                for (int i = 0; i<pixels.length;i++)
                {
                    for (int j = 0;j<pixels[i].length; j++)
                    {
                        if (pos<byteStegan.length)
                        {            
                            //RED------------------------------------------------------------------------------------------
                            int redValue = pixels[i][j].getRed();

                            pixels[i][j].setRed((redValue & 0b11111100) | helperUpdatingColorHiding(count, byteStegan[pos]));
                            count+=1;
                            if (count>3)
                            {
                                count=0; 
                                pos+=1;
                            }
                            
                            if (pos>=byteStegan.length)
                            {
                                raf.close();
                                return true;
                            }
                            //----------------------------------------------------------------------------------------------
                    
                            //GREEN------------------------------------------------------------------------------------------
                            int greenValue = pixels[i][j].getGreen();

                            pixels[i][j].setGreen((greenValue & 0b11111100) | helperUpdatingColorHiding(count, byteStegan[pos]));
                            count+=1;
                            if (count>3)
                            {
                                count=0; 
                                pos+=1;
                            }

                            if (pos>=byteStegan.length)
                            {
                                raf.close();
                                return true;
                            }
                            //----------------------------------------------------------------------------------------------
                            
                            //BLUE------------------------------------------------------------------------------------------
                            int blueValue = pixels[i][j].getBlue();

                            pixels[i][j].setBlue((blueValue & 0b11111100) | helperUpdatingColorHiding(count, byteStegan[pos]));
                            count+=1;
                            if (count>3)
                            {
                                count=0; 
                                pos+=1;
                            }
                            if (pos>=byteStegan.length)
                            {
                                raf.close();
                                return true;
                            }
                            //----------------------------------------------------------------------------------------------

                        }
                        else
                        {
                            raf.close();
                            return true;
                        }
                    }
                }
                raf.close();
            }
            catch (IOException e)
            {
                System.out.println("Input/Output Error");
            }
            return false;
        }
        
    }

    /*
     * A helper method to get the bits of value based on the index of crumps.
     */

    private int helperUpdatingColorHiding(int count, int number)
    {
        // 7 6
        if (count==0)
        {
            return (number/64)%4;
        }

        //Next 2 bit - 5 4
        else if (count==1)
        {
            return (number/16)%4;
        }

        //Next 2 bit - 3 2
        else if (count==2)
        {
            return (number/4)%4;
        }

        //Least 2 significant bit - 1 0
        else 
        {
            return number%4;
        }
    }
    
    /* 
     * A method that extract the file that hidden in pixels.
     */
    public boolean unhide(File newFile)
    {
    
        if (newFile == null){
            throw new IllegalArgumentException("The argument is null");
        }            
        else
        {
            if (newFile.isFile()) 
            {
                newFile.delete();
            }

            //finding the length of the hidden file

            int byteA = (pixels[0][0].getRed() % 4)*64+ (pixels[0][0].getGreen() % 4)*16+ (pixels[0][0].getBlue() % 4)*4 +(pixels[0][1].getRed() % 4) ;
            
            int byteB = (pixels[0][1].getGreen() % 4)*64+ (pixels[0][1].getBlue() % 4)*16+ (pixels[0][2].getRed() % 4)*4 +(pixels[0][2].getGreen() % 4) ;
            
            int byteC = (pixels[0][2].getBlue() % 4)*64+ (pixels[0][3].getRed() % 4)*16+ (pixels[0][3].getGreen() % 4)*4 +(pixels[0][3].getBlue() % 4) ;
            
            int byteD = (pixels[0][4].getRed() % 4)*64+ (pixels[0][4].getGreen() % 4)*16+ (pixels[0][4].getBlue() % 4)*4 +(pixels[0][5].getRed() % 4) ;
            
            int len = (byteA + byteB*256 + byteC*256*256 + byteD*256*256*256)+4;

            
            if (len < 4)
            {
                return false;
            }

            // Extracting the file
            int[] resultByteStegan = new int[len];

            int count=0;
            int pos=0;

            for (int i = 0; i<pixels.length;i++)
            {
                for (int j = 0;j<pixels[i].length; j++)
                {
                    if (pos<resultByteStegan.length)
                    {            
                        //RED------------------------------------------------------------------------------------------
                        int redValue = pixels[i][j].getRed();

                        resultByteStegan[pos] += helperUpdatingColorUnHiding(count,redValue);

                        count+=1;
                        if (count>3)
                        {
                            count=0; 
                            pos+=1;
                        }
                        
                        if (pos>=resultByteStegan.length)
                        {
                            writeHiddenFile(newFile,resultByteStegan);
                            return true;
                        }
                        //----------------------------------------------------------------------------------------------
                
                        //GREEN------------------------------------------------------------------------------------------
                        int greenValue = pixels[i][j].getGreen();

                        resultByteStegan[pos] += helperUpdatingColorUnHiding(count,greenValue);
                        count+=1;
                        if (count>3)
                        {
                            count=0; 
                            pos+=1;
                        }

                        if (pos>=resultByteStegan.length)
                        {
                            writeHiddenFile(newFile,resultByteStegan);
                            return true;
                        }
                        //----------------------------------------------------------------------------------------------
                        
                        //BLUE------------------------------------------------------------------------------------------
                        int blueValue = pixels[i][j].getBlue();

                        resultByteStegan[pos] += helperUpdatingColorUnHiding(count,blueValue);
                        count+=1;
                        if (count>3)
                        {
                            count=0; 
                            pos+=1;
                        }
                        if (pos>=resultByteStegan.length)
                        {
                            writeHiddenFile(newFile,resultByteStegan);
                            return true;
                        }
                        //----------------------------------------------------------------------------------------------

                    }
                    else
                    {
                        //raf.close();
                        writeHiddenFile(newFile,resultByteStegan);
                        return true;
                    }
                }
            }
            return true;
        }
    }

    /*
     * A helper method that return an appropriate value that updating the original byte based on the index of the crumps.
     */
    private int helperUpdatingColorUnHiding(int count, int colorValue)
    {
        // 7 6
        if (count==0)
        {
            return (colorValue%4)*64;
        }

        //Next 2 bit - 5 4
        else if (count==1)
        {
            return (colorValue%4)*16;
        }

        //Next 2 bit - 3 2
        else if (count==2)
        {
            return (colorValue%4)*4;
        }

        //Least 2 significant bit - 1 0
        else 
        {
            return (colorValue%4)*1;
        }
    }

    private void writeHiddenFile(File newFile, int[] resultByteStegan)
    {
        try
        {
            RandomAccessFile raf = new RandomAccessFile(newFile,"rw");
            for (int i = 4; i<resultByteStegan.length;i++)
            {
                raf.write(resultByteStegan[i]);
            }
            raf.close();
        }
        catch (IOException e)
        {
            System.out.println("Input/ Output Error");
        }
    }
    
    public static void main(String[] args) {
        File givenFile = new File("result1.bmp");

        File resFile = new File("result2");
        BitmapHacker2 bm = new BitmapHacker2(givenFile);
        bm.unhide(resFile);
    }
}
 