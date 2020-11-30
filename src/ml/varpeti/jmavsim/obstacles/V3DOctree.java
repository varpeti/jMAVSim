package ml.varpeti.jmavsim.obstacles;

import javax.vecmath.Vector3d;
import java.util.ArrayList;

import static java.lang.StrictMath.cbrt;

enum objectType {Air, Obstacle, Vehicle}

public class V3DOctree extends Octree<objectType> {

    public V3DOctree(Vector3d pos, Vector3d size, objectType value) {
        super(pos, size, value);
    }

    @Override
    protected void drawThis() {
        if (this.value == objectType.Obstacle) {
            synchronized (Obstacles.world) {
                Obstacles.world.addObject(new Cube(this.pos, this.size.x, this.pos.toString() + " " + this.size.x));
            }
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

    public int numOf(objectType type)
    {
        int ret = 0;
        if (this.leaf) {
            if (this.value == type)
                return 1;
            else
                return 0;
        }
        else for (int i = 0; i < numberOfChildren; i++) { ret += ((V3DOctree)children[i]).numOf(type);}

        return ret;
    }

     public ArrayList<V3DOctree> getLeavesInArea(Vector3d pos, Vector3d size)
     {
         ArrayList<V3DOctree> leaves = new ArrayList<>();
         if (isFullyOutside(pos, size)) return leaves;

         if (this.leaf) {
             leaves.add(this);
             return leaves;
         }

         for (int i = 0; i < numberOfChildren; i++)
         {
             ArrayList<V3DOctree> newLeaves = children[i].getLeavesInArea(pos, size);
             if (newLeaves.size()>0) leaves.addAll(newLeaves);
         }
         return leaves;
     }

     public ArrayList<V3DOctree> getNeighbours(Vector3d pos)
     {
         V3DOctree cur = getLeavesInArea(pos, new Vector3d(0.0d, 0.0d, 0.0d)).get(0);
         double minLength = cbrt(V3DOctree.minSize ) / 2.0d;
         ArrayList<V3DOctree> neighbours = new ArrayList<>();
         neighbours.addAll(getLeavesInArea(cur.pos, new Vector3d(cur.size.x+minLength, cur.size.y-minLength, cur.size.z-minLength)));
         neighbours.remove(cur);
         neighbours.addAll(getLeavesInArea(cur.pos, new Vector3d(cur.size.x-minLength, cur.size.y+minLength, cur.size.z-minLength)));
         neighbours.remove(cur);
         neighbours.addAll(getLeavesInArea(cur.pos, new Vector3d(cur.size.x-minLength, cur.size.y-minLength, cur.size.z+minLength)));
         neighbours.remove(cur);
         return neighbours;
     }

     static public ArrayList<Vector3d> cornerIt(ArrayList<V3DOctree> neighbours)
     {
         ArrayList<Vector3d> nodes = new ArrayList<>();
         double minLength = cbrt(V3DOctree.minSize);
         for (V3DOctree n : neighbours)
         {
             nodes.add(n.pos); if (0==0) continue;
             double thisLength = cbrt(n.volume);

             if (thisLength <= minLength) continue; //Too small to step in

             //Center
             nodes.add(n.pos);

             if (thisLength <= minLength*3) continue; //Too small to corner it

             Vector3d cs = new Vector3d(minLength*2.0d, minLength*2.0d, minLength*2.0d);

             //Corners
             nodes.add(new Vector3d(n.pos.x+n.size.x-cs.x, n.pos.y+n.size.y-cs.y, n.pos.z+n.size.z-cs.z));
             nodes.add(new Vector3d(n.pos.x-n.size.x+cs.x, n.pos.y+n.size.y-cs.y, n.pos.z+n.size.z-cs.z));
             nodes.add(new Vector3d(n.pos.x+n.size.x-cs.x, n.pos.y-n.size.y+cs.y, n.pos.z+n.size.z-cs.z));
             nodes.add(new Vector3d(n.pos.x-n.size.x+cs.x, n.pos.y-n.size.y+cs.y, n.pos.z+n.size.z-cs.z));
             nodes.add(new Vector3d(n.pos.x+n.size.x-cs.x, n.pos.y+n.size.y-cs.y, n.pos.z-n.size.z+cs.z));
             nodes.add(new Vector3d(n.pos.x-n.size.x+cs.x, n.pos.y+n.size.y-cs.y, n.pos.z-n.size.z+cs.z));
             nodes.add(new Vector3d(n.pos.x+n.size.x-cs.x, n.pos.y-n.size.y+cs.y, n.pos.z-n.size.z+cs.z));
             nodes.add(new Vector3d(n.pos.x-n.size.x+cs.x, n.pos.y-n.size.y+cs.y, n.pos.z-n.size.z+cs.z));


         }
         return nodes;
     }

}
