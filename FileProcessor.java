/**
 * Name: Duc Tri Dang
 * Date: Jan 18 2024
 * Course: COMP 3621
 */
import java.io.*;

public class FileProcessor {
    
    int len;
    int[] byteStore;
    
    private static final int NULL_CONSTANT_INT = -1;
    
    /*
     * The constructor of FileProcessor
     * Read the file and throw IllegalArgumentException if the file is not a valid file with smaller size than an integer.
     */
    public FileProcessor(File newFile) throws IllegalArgumentException{
        if (newFile == null){
            throw new IllegalArgumentException("The argument is null");
        }
        else if (!newFile.isFile()){
            throw new IllegalArgumentException("The argument is not a File");
        }
        else if (newFile.length()>Integer.MAX_VALUE){
            throw new IllegalArgumentException(" The size of this file is larger to be stored in an integer");
        }
        else{
            try{
                RandomAccessFile raf = new RandomAccessFile(newFile,"r");
                len = (int) raf.length();
                byteStore = new int[len];
                for (int i = 0; i < len; i++) {
                    int currByte = raf.read();
                    byteStore[i] = currByte;
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
     * The clear() method reset the length and the byteStore array to -1 and null, respectively.
     */
    public void clear()
    {
        len=NULL_CONSTANT_INT;
        byteStore = null;
    }

    /*
     * The method getBytes() willreturn a copy of the array of byte or null if there is no file stored in the object
     */
    public int[] getBytes()
    {
        if (byteStore==null)
        {
            return null;
        }
        else
        {
            return byteStore.clone();
        }
    }

    /*
     * The method printHex() will take in a written file, throw exception if it is not a valid file, then print hex value of the current file on that written file.
     */
    public void printHex(File writtenFile)
    {
        if (writtenFile == null)
        {
            throw new IllegalArgumentException("The argument is null");
        }
        else
        {
            int[] byteArray = getBytes();
            if (byteArray == null)
            {
                throw new IllegalStateException("File Processor does not store any file");
            }
            else
            {
                try
                {
                    int count=0;
                    PrintWriter pWriter = new PrintWriter(writtenFile);
                    if (byteArray.length==0)
                    {
                        pWriter.print(0);
                    }
                    else
                    {
                        for (int i=0;i<byteArray.length;i++)
                        {
                            count+=1;
                            String output = Integer.toString(byteArray[i], 16);
                            if (output.length()==1)
                            {
                                output="0"+output;
                            }
                            
                            if (count==10)
                            {
                                pWriter.println(output);
                                count=0;
                            }
                            else if (i==byteArray.length-1)
                            {
                                pWriter.println(output);
                            }
                            else
                            {
                                pWriter.print(output + " ");
                            }
                        }
                    }
                    pWriter.close();
                }
                catch (FileNotFoundException e)
                {
                    System.out.println("Input/Output error");
                }
                
            }
        }
    }

    /*
     * The compare() method will compare the current FileProcessor with another FileProcessor and throw IllegalArgumentException if the argument is null
     */
    public boolean compare(FileProcessor fp)
    {
        if (fp==null)
        {
            throw new IllegalArgumentException("The argument is null");
        }
        else if ((fp.getBytes() != null) && (byteStore!=null))
        {
            if (len==fp.len)
            {
                boolean check =true;
                for (int i=0;i<byteStore.length;i++)
                {
                    if (byteStore[i]!=fp.getBytes()[i])
                    {
                        System.out.println(i+" "+ byteStore[i]+" "+fp.getBytes()[i]);
                        check = false;
                    }
                }
                return check;
            }
            else
            {
                return false;
            }
        }
        return false;
    }
}
