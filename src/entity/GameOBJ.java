package entity;

import java.awt.*;

// Abstract base class representing any interactive entity/object in the game
public abstract class GameOBJ {

      // Position and dimension attributes
      public int x, y;              
      public int width, height;     
      public int speed;             // Movement speed

      // Collision detection box
      public Rectangle hitbox;

      // Abstract method to update entity state/logic must be implemented by subclasses
      public abstract void update();

      // Abstract method to render entity graphics onto the screen; must be implemented by subclasses
      public abstract void draw(Graphics g);    
}
