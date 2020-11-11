package ml.varpeti.jmavsim.obstacles;

import me.drton.jmavsim.KinematicObject;

import javax.vecmath.Vector3d;

public class Collidable extends KinematicObject {
    private long lastTime = -1;
    private Vector3d size;
    Obstacles obstacles;


    public Collidable(Vector3d position, Vector3d size, String model) {
        super(Obstacles.world, Obstacles.showGui);
        this.setPosition(position);
        this.modelFromFile(model);
        this.size = size;
    }

    private Collidable(Obstacles obstacles) //Vehicle
    {
        super(Obstacles.world, Obstacles.showGui);
        this.setPosition(Obstacles.vehicle.getPosition());
        this.size = Obstacles.vehicleSize;
        this.obstacles = obstacles;
    }

    public boolean checkCollision()
    {
        return this.checkCollisionWith(new Collidable(this.obstacles));
    }

    public boolean checkCollisionWith(Collidable other) {
        Vector3d aPos = this.getPosition();
        Vector3d bPos = other.getPosition();
        Vector3d aSize = this.size;
        Vector3d bSize = other.getSize();
        //AABB (Axis Aligned Bounding Box)
        if (Math.abs(aPos.x - bPos.x) < aSize.x + bSize.x) {
            if (Math.abs(aPos.y - bPos.y) < aSize.y + bSize.y) {
                if (Math.abs(aPos.z - bPos.z) < aSize.z + bSize.z) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setSize(Vector3d size)
    {
        this.size = size;
    }
    
    public Vector3d getSize()
    {
        return this.size;
    }

    @Override
    public void update(long t, boolean paused) {}
}

