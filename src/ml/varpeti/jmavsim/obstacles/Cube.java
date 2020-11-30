package ml.varpeti.jmavsim.obstacles;

import me.drton.jmavsim.MAVLinkHILSystem;

import javax.vecmath.Vector3d;

public class Cube extends Collidable {
    private boolean lastColl = false;
    private String name = "";
    public static MAVLinkHILSystem hilSystem;

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
                Vector3d vehiclePos = Obstacles.vehicle.getPosition();
                System.out.println(vehiclePos);

                //Find out where is the threat.
                double dx = position.x-vehiclePos.x;
                double dy = position.y-vehiclePos.y;
                double dz = position.z-vehiclePos.z;
                double minP = Math.abs(dx);
                byte msg = 0;
                if (dx<0) msg = 100; else msg = 101;
                if (minP<Math.abs(dy))
                {
                    if (dy<0) msg = 102; else msg = 103;
                    minP=Math.abs(dy);
                }
                if (minP<Math.abs(dz)) {
                     if (dz<0) msg = 104; else msg = 105;
                }
                hilSystem.sendCustomMessageHackedIntoGPS(msg);
            }
            lastColl = true;
        }
        else
        {
            if (lastColl) {
                System.out.print("Collision: end;   with: "+this.name+"; pos: ");
                System.out.println(Obstacles.vehicle.getPosition());
                hilSystem.sendCustomMessageHackedIntoGPS((byte)0);
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
