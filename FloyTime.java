//Author: Armin Costa  ACosta@unibz.it
//----------------------------
// I coded this somewhat stupid game as an example for a freind of mine in order to lern programming
//
// So I hope it helps!! ;-)

import java.awt.*;
import java.applet.*;
import java.awt.event.*;

public class FloyTime extends Applet implements Runnable {
  int[][] grid_x = new int[120][120];
  int[] pos_y = new int[120];

  int y_ = 30;
  int x_ = 30;
  int pos_x_ = 20;

 boolean shoot = false;

 boolean up = false;    // booleans for faster/continous moving
 boolean down = false;
 boolean right = true;
 boolean left = false;

 boolean[] floyIsCaught = new boolean[10];

 boolean release_STATUS = false;


 //Window Events
 boolean STATUS_OPEN = false;  //open or closed Window
 boolean WIN = false;
 //Window Data
 int w_x = 10;
 int w_y = 40;
 int ww_x = 20;
 int ww_y = 90;

  Thread t;


  ////
  int[] bounds_x = new int[20];
  int[] bounds_y = new int[20];

  //////////
  //FloyZ variables
  int[] d_i = new int[10];   // dead_index  - dead floy are drawn in box
  int N_FLOYS = 1;
  int N_EATEN = 1;
  int start_x = 10;
  int start_y = 10;

  int[] n_x = new int[10]; //new x
  int[] n_y = new int[10]; // new y
  int xx;
  int yy;
  int[] c_x = new int[10]; //current x
  int[] c_y = new int[10];  // current y
  int[] posx = new int[10];   //those indexes mapps the floyz directions
  int[] posy = new int[10];

  int time_int = 600;  //used to keep track of the time - floy in box
  int t_int = 0;        //associated index


  int counter = 0;     //those variables keep track of the random direction variations
  int rand_counter = 20;

  public void init(){
    initArr();
    addKeyListener(new floyControl());
    initGrid();
    createRandom();


  }
  void initArr(){
    int pos = 10;
    for(int i = 0; i > d_i.length; i++){
      d_i[i] = pos;
      pos += 3;
    }
    for(int b = 0; b < 10; b++)  floyIsCaught[b] = false;

  }
  public void initGrid(){
    int posx = 0;
    int posy = 0;
    int y;
    int x;
   for(y = 0; y < 120; y++){
     for(x = 0; x < 60; x++){
       posx += 5;
       grid_x[y][x] = posx;
     }
     pos_y[y] = posy += 5;
     posx = 0;
   }

  }
  void shuttle(Graphics g, int y, int x){
    //ho
    g.setColor(Color.blue);
    g.drawLine(pos_y[y], grid_x[y][x]-1,pos_y[y]+15, grid_x[y+3][x]-1);
    g.drawLine(pos_y[y], grid_x[y][x]-2,pos_y[y]+15, grid_x[y+3][x]-2);

    g.setColor(Color.black);

    g.fillRect(pos_y[y], grid_x[y][x], 5, 5);
    g.fillRect(pos_y[y]+5, grid_x[y+1][x], 5, 5);
    g.fillRect(pos_y[y]+10, grid_x[y+2][x], 5, 5);
    g.fillRect(pos_y[y]+15, grid_x[y+3][x], 5, 5);
    ///vertical left
    g.fillRect(pos_y[y]-5, grid_x[y][x], 5, 5);
    g.fillRect(pos_y[y]-5, grid_x[y-1][x]-5, 5, 5);
    g.fillRect(pos_y[y]-5, grid_x[y-2][x]-5, 5, 5);
    ///vertical right
    g.fillRect(pos_y[y]+20, grid_x[y+3][x], 5, 5);
    g.fillRect(pos_y[y]+20, grid_x[y+4][x]-5, 5, 5);
    g.fillRect(pos_y[y]+20, grid_x[y+5][x]-5, 5, 5);

    drawCaughtFloy(g, y, x);  // draws the caught Floy - if any -- into the box

    missionCheck(y);    //checks if the conditions are met to release a floy


    ////define bounds
    ///////////////////

     //hor
   bounds_y[0] = pos_y[y];
   bounds_y[1] = pos_y[y]+5;
   bounds_y[2] = pos_y[y]+10;
   bounds_y[3] = pos_y[y] +15;
   //ver   l
   bounds_y[4] = pos_y[y] -5;
   bounds_y[5] = pos_y[y] - 5;
   bounds_y[6] = pos_y[y] - 5;
   //  ver  r
   bounds_y[7] = pos_y[y] +20;
   bounds_y[8] = pos_y[y] +20;
   bounds_y[9] = pos_y[y] +20;


   //horizzontal
   bounds_x[0] = grid_x[y][x];
   bounds_x[1] = grid_x[y+1][x];
   bounds_x[2] = grid_x[y+2][x];
   bounds_x[3] = grid_x[y+3][x];
     //vertical L
   bounds_x[4] = grid_x[y][x];
   bounds_x[5] = grid_x[y-1][x]-5;
   bounds_x[6] = grid_x[y-2][x]-5;
   //vertical  R
   bounds_x[7] = grid_x[y+3][x];
   bounds_x[8] = grid_x[y+4][x]-5;
   bounds_x[9] = grid_x[y+5][x]-5;




  }


  public void floyCaught(){ //this method checks if the floy has been caught
    int e;
    for(e = 0; e < N_FLOYS; e++){
      if(((c_x[e] > bounds_y[6]) && (c_y[e] > bounds_x[6])) && ((c_x[e] < bounds_y[3]) && (c_y[e]  < bounds_x[3]))){
      floyIsCaught[e] = true;
      }
    }
  }

  void missionCheck(int y){
    for(int p = 0; p < N_FLOYS; p++){
      if((floyIsCaught[p] && STATUS_OPEN)){
        //if(((pos_y[y] > w_x)&&(pos_y[y] < ww_x))&& ((grid_x[y][x] >  w_y)&& (grid_x[y][x] < ww_x))){
        if((pos_y[y] > w_x)&&(pos_y[y] < ww_x)){
          showStatus("MISSION COMPLETED");
          release_STATUS = true;
        }else {
          release_STATUS = false;
        }
      }
    }

  }
  void drawCaughtFloy(Graphics g, int y, int x){
    int i = 0;

    for(int p = 0; p < N_FLOYS; p++){
      if(floyIsCaught[p]){
        g.fillOval(pos_y[y]+d_i[i],grid_x[y-1][x]-5, 5, 5);
        i += 1;
      }

    }

  }



  /////////////////////////////////////////////
  //////////Floy
  /////////////
    public void startingPoint(){
    start_x = 10;
    start_y = 10;

  }

    public void createRandom(){
      int i;
      for(i = 0; i < N_FLOYS; i++){
        posx[i] = (int)(Math.random()*600);
        posy[i] = (int)(Math.random()*300);
      }

  }
    public int randomCounter(){
    rand_counter = (int)((Math.random()*100)/ 2);
    return counter;


  }
    public void moving(){
      floyCaught();
    int z;
    for(z = 0; z < N_FLOYS; z++){
      if(posx[z] > c_x[z])   n_x[z] += directionX();
      else if(posx[z] < c_x[z])   n_x[z] -=  directionX();

      if(posy[z] > c_y[z])   n_y[z] += directionY();
      else if(posy[z] < c_y[z])          n_y[z] -= directionY();

      c_x[z] = n_x[z];
      c_y[z] = n_y[z];
    }

  }

  public int directionX(){
    xx = (int)(Math.random()*10);
    return xx;



  }
  public int directionY(){
    yy = (int)(Math.random()*10);
    return yy;

  }


  void releaseFloyz(){
    N_FLOYS -= 1;

  }

  ////////////////////////////////

  public void paint(Graphics g){
    g.setColor(Color.black);
    g.drawRect(0, 0, 600, 320);
    g.drawString("YOUR MISSION: " , 10, 340);
    g.drawString("catch the floy into the box containing water, open the window and when close enough release the FLOY " , 10, 350);
    g.drawString("If you take to much time to get close to the window, the floy will fly away", 10, 360);
    g.drawString("-------------- ", 10, 380);
    g.drawString("COMMANDS: ",10, 400);
    g.drawString("press 'S' to open the window ", 10, 410);
    g.drawString("press 'D' to close the window ", 10, 420);
    g.drawString("press 'A' to release the floy when you get next to the window ", 10, 430);
    g.drawString("ATTENTION:  opening the window may let in some other floyz :-) ", 10, 450);
    g.drawString("--------------------------------------------------------------", 10, 470);
    g.drawString("Autor: Armin Costa             ACosta@unibz.it", 10, 490);





  }
  void youWon(Graphics g){
     g.setColor(Color.blue);
     g.drawString("YOU WON!!!!!!!", 10, 470);
  }
  public void update(Graphics g){
    if(WIN)   youWon(g);
   g.clearRect(1, 1, 599, 319);
   int i;
   for(i = 0; i < N_FLOYS; i++){
     if(!floyIsCaught[i]){
       g.fillOval(c_x[i],c_y[i], 6, 3);
     }
   }

    g.drawString("WINDOW", 0, 30);
    g.fillRect(10, 40, 10, 50);  //window
    if(STATUS_OPEN)  g.drawRect(20, 40, 50, 50);

    shuttle(g, y_, x_);

    //paint(g);

  }
  public void run(){
    Graphics g2 = getGraphics();


    while(true){
      try{
        /*
        if(up) up();    // method calls for speedy- moving
        if (down) down();
        if (right) right();
        if (left)  left();
        */


        if(counter > rand_counter){
          createRandom();
          rand_counter = randomCounter();
          counter = 0;
        }
        //repaint();
        moving();
        update(g2);
        counter++;
        for(int i = 0; i < N_FLOYS; i++){
          if(t_int > time_int){
            floyIsCaught[i] = false;
          //floyAway = true;
            t_int = 0;
          }
        }
        t_int++;
        Thread.sleep(10);
      }catch(InterruptedException e){}

    }

  }
    public void start()
        {
                if (t == null)
                {
                        t= new Thread(this);
                        t.start();
                }
  }

  public void stop()
        {
                if (t!=null)
                {
                        t.stop();
                        t=null;
                }
   }
   void right(){
         y_ += 1;
        x_ = x_;

   }
   void left(){
       y_ -= 1;
        x_ = x_;

   }
   void up(){
     y_ = y_;
    x_ -= 1;

   }
   void down(){
     y_ = y_;
      x_ += 1;

   }
  public class floyControl extends KeyAdapter{
    public void keyPressed(KeyEvent e){
      int code = e.getKeyCode();
      if(code == KeyEvent.VK_RIGHT){
        y_ += 1;
        x_ = x_;
        up = false;
        down = false;
        right = true;
        left = false;

      }if(code == KeyEvent.VK_LEFT){
        up = false;
        down = false;
        right = false;
        left = true;
        y_ -= 1;
        x_ = x_;
        //pos_x_ -= 5;

      }if(code == KeyEvent.VK_UP){
        y_ = y_;
        x_ -= 1;
        up = true;
        down = false;
        right = false;
        left = false;

      }if(code == KeyEvent.VK_DOWN){
        up = false;
        down = true;
        right = false;
        left = false;
        y_ = y_;
        x_ += 1;
      }
      if(code == KeyEvent.VK_S){
        double r = Math.random();
        STATUS_OPEN = true;
        if(r > 0.5){
          N_FLOYS++;         //opens the Window
        }

      }
      if(code == KeyEvent.VK_D){
        STATUS_OPEN = false;
      }
      if(code == KeyEvent.VK_A){
        if(release_STATUS){
          releaseFloyz();
          if(N_FLOYS == 0)  WIN = true;
        }

      }
    }
  }
}

