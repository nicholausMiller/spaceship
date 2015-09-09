
package spaceship;

import java.io.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;

public class Spaceship extends JFrame implements Runnable {
    static final int WINDOW_WIDTH = 420;
    static final int WINDOW_HEIGHT = 445;
    final int XBORDER = 20;
    final int YBORDER = 20;
    final int YTITLE = 25;
    boolean animateFirstTime = true;
    int xsize = -1;
    int ysize = -1;
    Image image;
    Graphics2D g;

    sound zsound = null;
    sound bgSound = null;
    Image outerSpaceImage;

//variables for rocket.
    Image rocketImage;
    int rocketXPos;
    int rocketYPos;
    boolean rocketRight;
    int rocketXSpeed;
    int rocketYSpeed;
    int numStars = 5;
    int starXPos[] = new int[numStars];
    int starYPos[] = new int[numStars];
    boolean starHit[] = new boolean[numStars];
    boolean starVisible[] = new boolean[numStars];
   
    int numMissile = 10;
    int currentMissileIndex;
    int missileXPos[]=new int[numMissile];
    int missileYPos[]=new int[numMissile];
    boolean missileVisible[]=new boolean[numMissile];
    boolean missileRight[]=new boolean[numMissile];
   
    
    static Spaceship frame;
    public static void main(String[] args) {
        frame = new Spaceship();
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public Spaceship() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.BUTTON1 == e.getButton()) {
                    //left button

// location of the cursor.
                    int xpos = e.getX();
                    int ypos = e.getY();

                }
                if (e.BUTTON3 == e.getButton()) {
                    //right button
                    reset();
                }
                repaint();
            }
        });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        repaint();
      }
    });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent e) {

        repaint();
      }
    });

        addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                if (e.VK_UP == e.getKeyCode()) {
                    rocketYSpeed++;
                } else if (e.VK_DOWN == e.getKeyCode()) {
                    rocketYSpeed--;
                } else if (e.VK_LEFT == e.getKeyCode()) {
                    rocketXSpeed--;
                } else if (e.VK_RIGHT == e.getKeyCode()) {
                    rocketXSpeed++;
                }
                else if (e.VK_SPACE == e.getKeyCode()) {
                    missileVisible[currentMissileIndex] = true;
                    missileXPos[currentMissileIndex] = rocketXPos;
                    missileYPos[currentMissileIndex] = rocketYPos;
                    missileRight[currentMissileIndex] = rocketRight;
                   currentMissileIndex++;
                    if (currentMissileIndex >= numMissile)
                        currentMissileIndex = 0;
                }
                repaint();
            }
        });
        init();
        start();
    }
    Thread relaxer;
////////////////////////////////////////////////////////////////////////////
    public void init() {
        requestFocus();
    }
////////////////////////////////////////////////////////////////////////////
    public void destroy() {
    }



////////////////////////////////////////////////////////////////////////////
    public void paint(Graphics gOld) {
        if (image == null || xsize != getSize().width || ysize != getSize().height) {
            xsize = getSize().width;
            ysize = getSize().height;
            image = createImage(xsize, ysize);
            g = (Graphics2D) image.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }
//fill background
        g.setColor(Color.cyan);
        g.fillRect(0, 0, xsize, ysize);

        int x[] = {getX(0), getX(getWidth2()), getX(getWidth2()), getX(0), getX(0)};
        int y[] = {getY(0), getY(0), getY(getHeight2()), getY(getHeight2()), getY(0)};
//fill border
        g.setColor(Color.black);
        g.fillPolygon(x, y, 4);
// draw border
        g.setColor(Color.red);
        g.drawPolyline(x, y, 5);

        if (animateFirstTime) {
            gOld.drawImage(image, 0, 0, null);
            return;
        }
        
        g.drawImage(outerSpaceImage,getX(0),getY(0),
                getWidth2(),getHeight2(),this);
//draw the rocket
        if (rocketRight){
        drawRocket(rocketImage,getX(rocketXPos),getYNormal(rocketYPos),0.0,1.0,1.0 );
        }
        else if (rocketRight == false){
          drawRocket(rocketImage,getX(rocketXPos),getYNormal(rocketYPos),0.0,-1.0,1.0 );  
        }
        
//draw the star
      //  drawStar(getX(starXPos),getYNormal(starYPos),0.0,1.0,1.0 );
        for (int index=0;index<numStars;index++)
        {
            if (starVisible[index]){
                g.setColor(Color.yellow);
               drawStar(getX(starXPos[index]),getYNormal(starYPos[index]),0,1,1);
            }
        }
        for (int index=0;index<numMissile;index++)
        {
            if (missileVisible[index])
            {
                g.setColor(Color.black);
                drawCircle(getX(missileXPos[index]),getYNormal(missileYPos[index]),0,1,1);
               
            
                if (missileRight[index]){
                    missileXPos[index]++;
                }
                else{
                    missileXPos[index]--;                    
                }
            }
        
        }
        gOld.drawImage(image, 0, 0, null);
    }
////////////////////////////////////////////////////////////////////////////
    public void drawStar(int xpos,int ypos,double rot,double xscale,double yscale)
    {
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.setColor(Color.yellow);
        g.fillOval(-10,-10,20,20);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
    //////////////////////////////////////////////////////////////////////
     public void drawCircle(int xpos,int ypos,double rot,double xscale,double yscale)
    {
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.setColor(Color.red);
        g.fillOval(-10,-10,20,20);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
////////////////////////////////////////////////////////////////////////////
    public void drawRocket(Image image,int xpos,int ypos,double rot,double xscale,
            double yscale) {
        int width = rocketImage.getWidth(this);
        int height = rocketImage.getHeight(this);
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.drawImage(image,-width/2,-height/2,
        width,height,this);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
////////////////////////////////////////////////////////////////////////////
// needed for     implement runnable
    public void run() {
        while (true) {
            animate();
            repaint();
            double seconds = 0.04;    //time that 1 frame takes.
            int miliseconds = (int) (1000.0 * seconds);
            try {
                Thread.sleep(miliseconds);
            } catch (InterruptedException e) {
            }
        }
    }
/////////////////////////////////////////////////////////////////////////
    public void reset() {

//init the location of the rocket to the center.
        rocketXPos = getWidth2()/2;
        rocketYPos = getHeight2()/2;
        rocketRight = true;
        rocketXSpeed = 0;
        rocketYSpeed = 0;
      for (int index=0;index<numStars;index++){ 
        starVisible[index] = true;
        starYPos[index] = (int)(Math.random() * getHeight2());
        starXPos[index] = (int)(Math.random() * getWidth2()/2 + getWidth2()/2);
        starHit[index] = false;
       currentMissileIndex = 0;
       missileRight[index] = false; 
      
      } 
for (int index=0;index<numMissile;index++)
            missileVisible[index] = false;
    }
/////////////////////////////////////////////////////////////////////////
    public void animate() {
        if (animateFirstTime) {
            animateFirstTime = false;
            if (xsize != getSize().width || ysize != getSize().height) {
                xsize = getSize().width;
                ysize = getSize().height;
            }
           outerSpaceImage = Toolkit.getDefaultToolkit().getImage("./outerSpace.jpg");
            rocketImage = Toolkit.getDefaultToolkit().getImage("./rocket.GIF");
            reset();
            readFile();
            bgSound = new sound("starwars.wav");
        }
        if (bgSound.donePlaying)
        {
           bgSound = new sound("starwars.wav"); 
        }
        for (int index=0;index<numStars;index++){
        starXPos[index] = starXPos[index] + rocketXSpeed;
        }
        rocketYPos = rocketYPos + rocketYSpeed; 
    
          if (rocketYPos <0){
           rocketYPos = 0; 
           rocketYSpeed = 0;
        }
                    
        if (rocketYPos > getHeight2()){
           rocketYPos = getHeight2();
           rocketYSpeed = 0;
        }  
        for (int index=0;index<numStars;index++){ 
      
            if (starXPos[index] <= 0){
            starYPos[index] = (int)(Math.random() * getHeight2());
            starXPos[index] = (int)(Math.random() * getWidth2()/2 + getWidth2()/2); 
            }
            else if (starXPos[index] > getWidth2()){
            starXPos[index] = 0; 
            
        }
            
      }
        for (int count=0;count<numMissile;count++)
        {
            if (missileVisible[count])
            {
                missileXPos[count] += 8;
                if (missileYPos[count] >= getWidth2())
                    missileVisible[count] = false;
            }                  
        }
         for (int count=0;count<numMissile;count++)
        {
            for (int index=0;index<numStars;index++)
            {
                if (starVisible[index] && starXPos[index]-20 < missileXPos[count] && 
                    starXPos[index]+20 > missileXPos[count] &&
                    starYPos[index]-20 < missileYPos[count] &&
                    starYPos[index]+20 > missileYPos[count])
                {
                    starVisible[index] = false;
                    
                }

            }
        }
        if(rocketXSpeed > 0){
            rocketRight = true;
        }
        else if(rocketXSpeed < 0){
            rocketRight = false;
        }
        
            
            
            
            for (int count=0;count<numStars;count++)
        {
            for (int index=0;index<numStars;index++)
            {
                if (starHit[index] && starXPos[index]-20 < rocketXPos && 
                    starXPos[index]+20 > rocketXPos &&
                    starYPos[index]-20 < rocketYPos &&
                    starYPos[index]+20 > rocketYPos)
                {
                    starHit[index] = true;
                    zsound = new sound("ouch.wav");
                }

            }
        }
    }

////////////////////////////////////////////////////////////////////////////
    public void start() {
        if (relaxer == null) {
            relaxer = new Thread(this);
            relaxer.start();
        }
    }
////////////////////////////////////////////////////////////////////////////
    public void stop() {
        if (relaxer.isAlive()) {
            relaxer.stop();
        }
        relaxer = null;
    }
/////////////////////////////////////////////////////////////////////////
    public int getX(int x) {
        return (x + XBORDER);
    }

    public int getY(int y) {
        return (y + YBORDER + YTITLE);
    }

    public int getYNormal(int y) {
        return (-y + YBORDER + YTITLE + getHeight2());
    }
    
    
    public int getWidth2() {
        return (xsize - getX(0) - XBORDER);
    }

    public int getHeight2() {
        return (ysize - getY(0) - YBORDER);
    }
    public void readFile() {
        try {
            String inputfile = "info.txt";
            BufferedReader in = new BufferedReader(new FileReader(inputfile));
            String line = in.readLine();
            while (line != null) {
                String newLine = line.toLowerCase();
                if (newLine.startsWith("numstars"))
                {
                    String numStarsString = newLine.substring(9);
                    numStars = Integer.parseInt(numStarsString.trim());
                }
                line = in.readLine();
            }
            in.close();
        } catch (IOException ioe) {
        }
    }
}

class sound implements Runnable {
    Thread myThread;
    File soundFile;
    public boolean donePlaying = false;
    sound(String _name)
    {
        soundFile = new File(_name);
        myThread = new Thread(this);
        myThread.start();
    }
    public void run()
    {
        try {
        AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
        AudioFormat format = ais.getFormat();
    //    System.out.println("Format: " + format);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine source = (SourceDataLine) AudioSystem.getLine(info);
        source.open(format);
        source.start();
        int read = 0;
        byte[] audioData = new byte[16384];
        while (read > -1){
            read = ais.read(audioData,0,audioData.length);
            if (read >= 0) {
                source.write(audioData,0,read);
            }
        }
        donePlaying = true;

        source.drain();
        source.close();
        }
        catch (Exception exc) {
            System.out.println("error: " + exc.getMessage());
            exc.printStackTrace();
        }
    }

}
class Missile{
    public static int currentMissileIndex = 0;
    public final static  int numMissile = 10;
   
  
    public int missileXPos[]=new int[numMissile];
    public  int missileYPos[]=new int[numMissile];
    public boolean missileVisible[]=new boolean[numMissile];
    public boolean missileRight[]=new boolean[numMissile];
    Missile(){
       
        
      
    }
    
    
}
      
