import processing.core.*; 
import processing.xml.*; 

import java.applet.*; 
import java.awt.Dimension; 
import java.awt.Frame; 
import java.awt.event.MouseEvent; 
import java.awt.event.KeyEvent; 
import java.awt.event.FocusEvent; 
import java.awt.Image; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class dpishik_ART131_processing_final extends PApplet {

///////////////////////////////////////
//  Programmer:   Dennis Pishik      //
//  Class:        ART131             //
//  Assignment:   Dynamic Project    //
///////////////////////////////////////



cubes[][] cubeArray;      // Creating an array of cubes.

int cols = 6;             // Number of columns and rows in the array.
int rows = 5;

//  Setting up the size of the screen and smoothness
public void setup()
{
  size(640,525);
  noStroke();
  smooth();
  
  cubeArray = new cubes[cols][rows];  // Initializing the array and filling it.
  
  for (int i = 0; i < cols; i++)
  {
    for (int j = 0; j < rows; j++)
    {
      cubeArray[i][j] = new cubes(i*123,j*125,25,25,0, 0);    // I picked numbers I thought would fit nicely on the screen.
    }
  }

  background(0,0,0);
}

// Here, I'm defining what my cubes are comprised of.
class cubes
{
  float x;          // An (X,Y0 coordinate, a Width, and a height.
  float y;
  float w;
  float h;
  float collision;  // whether there is a collision, and a timer from the collision.
  float timer;
  
  // Need to let the computer know what all I need to create my custom cubes as well as where those values go.
  cubes(float xpos, float ypos, float cubeWidth, float cubeHeight, float collided, float timeCollide)
  {
    x = xpos;
    y = ypos;
    w = cubeWidth;
    h = cubeHeight;
    collision = collided;
    timer = timeCollide;
  }
}

// Defining a bouncing ball class, and what all units will go into that class as well.
class ball
{
  float x;        // I will need an (X,Y) origin, a width and height because it's an ellipse...
  float y;        // ... a radius for making calculations easier, and velocity, for the bouncing.
  float w;
  float h;
  float rad;
  float vx;
  float vy;
  
  ball(float xpos, float ypos, float ballW, float ballH, float ballRad, float velX, float velY)
  {
    x = xpos;
    y = ypos;
    w = ballW;
    h = ballH;
    rad = ballRad;
    vx = velX;
    vy = velY;
  }
}

// Creating my bouncing ball!
ball bouncyBall = new ball(70,70,20,20,10,random(2,4),random(2,4));

float coll_L;          // variables to test collision from Left, Right, Top, and Bottom of my cubes.
float coll_R;
float coll_T;
float coll_B;
float timerBG = millis();

// Some variables to store colors, I can change the RGBs later in just one spot and it'll update everywhere I use the variables.
int ballColor = color(255,0,0,255);
int cubeNoCollide = color(0,0,255);
int cubeCollide = color(0,255,0);

public void draw()
{
  
  if (keyPressed)    // If the player presses the w,a,s,d keys, they can mess around with the ball's position...
  {                  // ... so really, you can have a unique experience every time!
    {
      if (key == 'w')
      {
        bouncyBall.vy -= .2f;
      }
      else if (key == 'a')
      {
        bouncyBall.vx -= .2f;
      }
      
      else if (key == 's')
      {
        bouncyBall.vy += .2f;
      }
      
      else if (key == 'd')
      {
        bouncyBall.vx += .2f;
      }
    }
    
    if(bouncyBall.vx < -6)
    {
       bouncyBall.vx = -6; 
    }
    else if(bouncyBall.vx > 6)
    {
       bouncyBall.vx = 6; 
    }
    else if(bouncyBall.vy < -6)
    {
      bouncyBall.vy = -6;
    }
    else if(bouncyBall.vy > 6)
    {
      bouncyBall.vy = 6;
    }
  }
  
  // Making sure that the ball stays within the screen bounds
  if ((bouncyBall.y < 1) || (bouncyBall.y > 524))
  {
    bouncyBall.vy *= -1;
  }
  
  if ((bouncyBall.x < 1) || (bouncyBall.x > 639))
  {
    bouncyBall.vx *= -1;
  }
  
  // Here's the big confusing part.  Checking for the collisions.  I tried to break it down easily, but it still looks scary.
  for (int i = 0; i < cols; i++)
  {
    for (int j = 0; j < rows; j++)
    { 
      // If the outside of the ball is greater than a cube's x position AND is less than the cube's width, it may be colliding...
      if ( ((bouncyBall.x + bouncyBall.rad) > cubeArray[i][j].x) &&
           ((bouncyBall.x - bouncyBall.rad) < (cubeArray[i][j].x + cubeArray[i][j].w)) )
           {
             // If, at the same time, the same is true of the y position and height, then we have a collision...
             // ... the ball is within the area of a cube.
             if (  ((bouncyBall.y + bouncyBall.rad) > cubeArray[i][j].y) &&
                   ((bouncyBall.y - bouncyBall.rad) < (cubeArray[i][j].y + cubeArray[i][j].h)) )
                   {
                     // From which side is the ball colliding with a cube?  Left, Right, Top, or Bottom?
                     
                     coll_L = abs((bouncyBall.x + bouncyBall.rad) - cubeArray[i][j].x);
                     coll_R = abs((bouncyBall.x - bouncyBall.rad) - (cubeArray[i][j].x + cubeArray[i][j].w));
                     coll_T = abs((bouncyBall.y + bouncyBall.rad) - cubeArray[i][j].y);
                     coll_B = abs((bouncyBall.y - bouncyBall.rad) - (cubeArray[i][j].y + cubeArray[i][j].h));
                     
                     // If the ball is colliding from the left or the right, we need to inverse the x velocity...
                     // ... so it changes direction and stops colliding.
                     // If the ball is colliding from the top or bottom, we need to inverse the y velocity instead.
                     
                     if ((coll_L < coll_R) && (coll_L < coll_T) && (coll_L < coll_B))
                         {
                           bouncyBall.vx *= -1;
                           bouncyBall.x += bouncyBall.vx;
                         }
                         
                         else if ((coll_R < coll_T) && (coll_R < coll_B))
                                 {
                                   bouncyBall.vx *= -1;
                                   bouncyBall.x += bouncyBall.vx;
                                 }
                         else if (coll_T < coll_B)
                                 {
                                   bouncyBall.vy *= -1;
                                   bouncyBall.y += bouncyBall.vy;
                                 }
                         else
                             {
                               bouncyBall.vy *= -1;
                               bouncyBall.y += bouncyBall.vy;
                             }
                         
                         // If we made it to the innermost IF statement, there must be a collision.  Toggle the flag.  Set the timer!
                         cubeArray[i][j].timer = millis();
                         cubeArray[i][j].collision = 1;
                   }
           }
 
           // After a second, reset the collision flag.  This will let us light up our cube, too!
           if((millis() - cubeArray[i][j].timer) > 1000)
             {
               cubeArray[i][j].collision = 0;
             }          
           
       }
    }
   
    bouncyBall.x += bouncyBall.vx;
    bouncyBall.y += bouncyBall.vy;
    
    fill(random(255),random(255),random(255),random(100,225));
    
    ellipse(bouncyBall.x, bouncyBall.y, (bouncyBall.w+(random(5,50))), (bouncyBall.h+(random(5,50))) );
    
    fill(ballColor);
    
    ellipse(bouncyBall.x, bouncyBall.y, bouncyBall.w, bouncyBall.h);
    
    for(int i = 0; i < cols; i++)
    {
      for(int j = 0; j < rows; j++)
      {
        // If there's a collision, cube is green, otherwise, cube is blue.  Draw it!
        if(cubeArray[i][j].collision ==1)
        {
          fill(cubeCollide);
        }
        else
        {
          fill(cubeNoCollide);
        }
        rect(cubeArray[i][j].x, cubeArray[i][j].y, cubeArray[i][j].w, cubeArray[i][j].h);
      }
    }
    
    // After 30 seconds, restart the whole thing!
    if((millis() - timerBG) > 30000)
    {
      background(0,0,0);
      timerBG = millis();
      bouncyBall.x = random(100,600);
      bouncyBall.y = random(50,100);
      bouncyBall.vx = random(2,4);
      bouncyBall.vy = random(2,4);
    }
}
  static public void main(String args[]) {
    PApplet.main(new String[] { "--present", "--bgcolor=#666666", "--hide-stop", "dpishik_ART131_processing_final" });
  }
}
