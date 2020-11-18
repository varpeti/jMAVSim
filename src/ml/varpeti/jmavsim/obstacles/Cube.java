package ml.varpeti.jmavsim.obstacles;

import javax.vecmath.Vector3d;

public class Cube extends Collidable {
    private boolean lastColl = false;
    private String name = "";

    public Cube(Vector3d position,double size) {
        super(position, new Vector3d(size,size,size), "models/cube"+size+".obj");
    }

     public Cube(Vector3d position, double size, String name) {
        super(position, new Vector3d(size,size,size), "models/cube"+size+".obj");
        //System.out.println(size);
        this.name = name;
    }

    @Override
    public void update(long t, boolean paused)
    {
        if (this.checkCollision())
        {
            if (!lastColl) {
                System.out.print("Collision: start; with: "+this.name+"; pos: ");
                System.out.println(Obstacles.vehicle.getPosition());
            }
            lastColl = true;
        }
        else
        {
            if (lastColl) {
                System.out.print("Collision: end;   with: "+this.name+"; pos: ");
                System.out.println(Obstacles.vehicle.getPosition());
            }
            lastColl = false;
        }
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }
}
