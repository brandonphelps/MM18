

package games.saloon;


public class HeatMap
{


    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static final String COLORS[] = {ANSI_BLACK, ANSI_PURPLE, ANSI_BLUE, ANSI_CYAN, ANSI_GREEN, ANSI_YELLOW, ANSI_RED, ANSI_WHITE};

    
    public static final int WIDTH = 40;
    public static final int HEIGHT = 20;
    public static final String asciiChart = "$@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft/\\|()1{}[]?-_+~<>i!lI;:,\"^`'. ";
    
    public float[][] intensity;


    
    public HeatMap()
    {
        intensity = new float[WIDTH][HEIGHT];
    }
    
    public HeatMap MultiplyBy(HeatMap other)
    {
        
        HeatMap result = new HeatMap();
        
        for(int x = 0; x < WIDTH; x++)
        {
            for(int y = 0; y < HEIGHT; y++)
            {
                result.SetValue(x, y, intensity[x][y] * other.GetValue(x, y));
            }
        }
        
        return(result);
        
    }
    
    public HeatMap MultiplyBy(double scalar)
    {
        
        HeatMap result = new HeatMap();
        
        for(int x = 0; x < WIDTH; x++)
        {
            for(int y = 0; y < HEIGHT; y++)
            {
                result.SetValue(x, y, intensity[x][y] * scalar);
            }
        }
        
        return(result);
        
    }
    
    public HeatMap Combine(HeatMap other)
    {
        return(this.Combine(other, 0.5f));
    }
    
    public HeatMap Combine(HeatMap other, double weighting)
    {
        
        HeatMap result = new HeatMap();
        
        if(weighting > 1) weighting = 1;
        else if(weighting < 0) weighting = 0;
        
        for(int x = 0; x < WIDTH; x++)
        {
            for(int y = 0; y < HEIGHT; y++)
            {
                result.SetValue(x, y, (weighting * intensity[x][y]) + ((1.0-weighting) *  other.GetValue(x, y)));
            }
        }
        
        return(result);
        
    }

    public HeatMap Maximum(HeatMap other)
    {
        
        HeatMap result = new HeatMap();
        
        for(int x = 0; x < WIDTH; x++)
        {
            for(int y = 0; y < HEIGHT; y++)
            {
                if(intensity[x][y] > other.GetValue(x, y))
                    result.SetValue(x, y, intensity[x][y]);
                else
                    result.SetValue(x, y, other.GetValue(x, y));
            }
        }
        
        return(result);
        
    }

    public HeatMap Inverse()
    {
        
        HeatMap result = new HeatMap();
        
        for(int x = 0; x < WIDTH; x++)
        {
            for(int y = 0; y < HEIGHT; y++)
            {
                result.SetValue(x, y, 1 - intensity[x][y]);
            }
        }
        
        return(result);
        
    }

    public HeatMap OrthoAverage()
    {
        
        HeatMap result = new HeatMap();
        
        for(int x = 0; x < WIDTH; x++)
        {
            for(int y = 0; y < HEIGHT; y++)
            {

                float value = 0.2f * intensity[x][y];
                if(x > 0) value += 0.2f * intensity[x-1][y];
                if(x < (WIDTH - 1)) value += 0.2f * intensity[x+1][y];
                if(y > 0) value += 0.2f * intensity[x][y-1];
                if(y < (HEIGHT - 1)) value += 0.2f * intensity[x][y+1];

                result.SetValue(x, y, value);
                
            }
        }
        
        return(result);
        
    }

    public void SetValue(int x, int y, double value)
    {
        SetValue(x, y, (float)value);
    }

    public void SetValue(int x, int y, float value)
    {
        if(value < 0)
            value = 0;
        else if(value > 1)
            value = 1;

        intensity[x][y] = value;
    }
    
    public float GetValue(int x, int y)
    {
        return(intensity[x][y]);
    }
    
    public String toString()
    {
        String result = new String();
        
        for(int y = 0; y < HEIGHT; y++)
        {
            
            for(int x = 0; x < WIDTH; x++)
            {
                int current = (int)(intensity[x][y] * 100);
                int colorIndex = (int)(intensity[x][y] * 7);

                result += COLORS[colorIndex];

                if(current == 100)
                    result += "XX";
                else
                    result += String.format("%02d", current);
                    
                result += ANSI_RESET;
            }
            result += "\n";
        }
        
        return(result);
        
    }
 
 
 
    public static HeatMap LinearRadiant(int centerX, int centerY, float maxIntensity, int radius)
    {
        
        HeatMap result = new HeatMap();
        
        for(int x = 0; x < WIDTH; x++)
        {
            for(int y = 0; y < HEIGHT; y++)
            {
                float distance = (float)Math.sqrt(((centerX - x)*(centerX - x)) + ((centerY - y)*(centerY - y)));
                if(distance < radius)
                {
                    
                     result.SetValue(x, y, ((float)(radius - distance) / (float)radius));
                    
                }
                // if it is outside the radius, intensity is zero
                else
                {
                    result.SetValue(x, y, 0);
                }

            }

            
        }
        
        return(result);
        
    }
    
    public static HeatMap OrthogonalRadiant(int centerX, int centerY, float maxIntensity, int radius)
    {
        
        HeatMap result = new HeatMap();
        
        for(int x = 0; x < WIDTH; x++)
        {
            result.SetValue(x, centerY, ((float)(radius - Math.abs(centerX - x)) / (float)radius));
        }
            
        for(int y = 0; y < HEIGHT; y++)
        {
            result.SetValue(centerX, y, ((float)(radius - Math.abs(centerY - y)) / (float)radius));
        }


        return(result);
        
    }

}