public class Pixel {
    private int red;
    private int green;
    private int blue;

    public Pixel(int newRed, int newGreen, int newBlue)
    {
        setRed(newRed);
        setGreen(newGreen);
        setBlue(newBlue);
    }

    public int getRed()
    {
        return red;
    }

    public int getGreen()
    {
        return green;
    }

    public int getBlue()
    {
        return blue;
    }

    public void setRed(int newRed)
    {
        if (newRed>255) red = 255;

        if (newRed<0) red = 0;

        if ((newRed>=0) && (newRed<=255)) red = newRed;
    }

    public void setGreen(int newGreen)
    {
        if (newGreen>255) green = 255;

        if (newGreen<0) green = 0;

        if ((newGreen>=0) && (newGreen<=255)) green = newGreen;
    }

    public void setBlue(int newBlue)
    {
        if (newBlue>255) blue = 255;

        if (newBlue<0) blue = 0;

        if ((newBlue>=0) && (newBlue<=255)) blue = newBlue;
    }
}
