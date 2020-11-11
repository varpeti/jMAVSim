package ml.varpeti.jmavsim.obstacles;

import javax.vecmath.Vector3d;

enum objectType {Air, Obstacle, Vehicle}

public class V3DOctree extends Octree<objectType> {

    public V3DOctree(Vector3d pos, Vector3d size, objectType value) {
        super(pos, size, value);
    }

    @Override
    protected void drawThis() {
        if (this.value == objectType.Obstacle)
            synchronized (Obstacles.world) {
                Obstacles.world.addObject(new Cube(this.pos, this.size.x, this.pos.toString()+" "+this.size.x ));
            }
    }

    /*    4------5
         /|     /|
        0------1 |
        | |    | |
        | 6----|-7
        |/     |/
        2------3
     */
    @Override
    protected void split()
    {
        this.leaf = false;
        Vector3d childSize = new Vector3d(this.size.x / 2.0d, this.size.y / 2.0d, this.size.z / 2.0d);

        children[0] = new V3DOctree(new Vector3d(pos.x - childSize.x, pos.y - childSize.y, pos.z - childSize.z), childSize, this.value);
        children[1] = new V3DOctree(new Vector3d(pos.x + childSize.x, pos.y - childSize.y, pos.z - childSize.z), childSize, this.value);
        children[2] = new V3DOctree(new Vector3d(pos.x - childSize.x, pos.y + childSize.y, pos.z - childSize.z), childSize, this.value);
        children[3] = new V3DOctree(new Vector3d(pos.x + childSize.x, pos.y + childSize.y, pos.z - childSize.z), childSize, this.value);
        children[4] = new V3DOctree(new Vector3d(pos.x - childSize.x, pos.y - childSize.y, pos.z + childSize.z), childSize, this.value);
        children[5] = new V3DOctree(new Vector3d(pos.x + childSize.x, pos.y - childSize.y, pos.z + childSize.z), childSize, this.value);
        children[6] = new V3DOctree(new Vector3d(pos.x - childSize.x, pos.y + childSize.y, pos.z + childSize.z), childSize, this.value);
        children[7] = new V3DOctree(new Vector3d(pos.x + childSize.x, pos.y + childSize.y, pos.z + childSize.z), childSize, this.value);
    }

    @Override
    public String toString()
    {
        StringBuilder ret = new StringBuilder();
        if (this.leaf) {
            if (this.value == objectType.Obstacle)
                return pos.x + " " + pos.y + " " + pos.z + " | " + size.x + " " + size.y + " " + size.z + " : " + value.toString() + "\n";
            else
                return "";
        }
        else for (int i = 0; i < numberOfChildren; i++) { ret.append(children[i].toString());}

        return ret.toString();
    }

}
