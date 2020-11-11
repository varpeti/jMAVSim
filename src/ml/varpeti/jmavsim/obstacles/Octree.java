package ml.varpeti.jmavsim.obstacles;

import javax.vecmath.Vector3d;

public abstract class Octree<V> {
    protected Vector3d pos;
    protected Vector3d size;
    protected V value;

    protected boolean leaf;
    protected double area;

    public static double minSize;
    protected static final int numberOfChildren = 8;

    /*    4------5
         /|     /|
        0------1 |
        | |    | |
        | 6----|-7
        |/     |/
        2------3

    */
    protected Octree<V>[] children = new Octree[numberOfChildren];

    public Octree(Vector3d pos, Vector3d size, V value)
    {
        this.pos = pos; this.size = size; this.value = value;
        this.leaf = true; this.area = size.x*size.y*size.z*8;
        for (int i = 0; i < numberOfChildren; i++) {
            children[i] = null;
        }
    }

    /*public Octree(@NotNull final Octree<V> ori)
    {
        this.pos = ori.pos; this.size = ori.size; this.value = ori.value;
        this.leaf = ori.leaf; this.area = ori.area;

        if (this.leaf) for (int i = 0; i < numberOfChildren; i++) { this.children[i] = null; }
        else for (int i = 0; i < numberOfChildren; i++) { this.children[i] = new Octree<>(ori.children[i]); }
    }*/

    /* This method change the value of an area in the tree */
    public final void setValue(Vector3d pos, Vector3d size, V value)
    {
        //System.out.println(this.pos.toString()+" "+this.size.toString());
        //System.out.println(pos.toString()+" "+size.toString());
        if (isFullyContained(pos,size)) {
            //System.out.println("FullyContained");
            if (!this.leaf) this.merge();
            this.value = value;
            this.leaf = true;
        } else if (!isFullyOutside(pos,size)) {
            //System.out.println("PartiallyContained "+this.area+" "+minSize);
            if (this.area <= minSize) return;
            if (this.leaf) split();
            setChildValues(pos,size,value);
        }/*else
        {
          System.out.println("FullyOutside");
        }*/
    }

    public final V getValue(Vector3d pos)
    {
        if (isFullyOutside(pos, new Vector3d(0.0d,0.0d,0.0d))) return null;

        if (this.leaf) return this.value;

        for (int i = 0; i < numberOfChildren; i++) {
            V value = children[i].getValue(pos);
            if (value != null) return value;
        }
        return null; // Should never get here
    }

    protected abstract void drawThis();
    /*{
       System.out.println(pos.x+" "+pos.y+" "+pos.z+" | "+size.x+" "+size.y+" "+size.z+" : "+value.toString());
    }*/

    public final void draw()
    {
        if (this.leaf) drawThis();
        else for (int i = 0; i < numberOfChildren; i++) {children[i].draw();}
    }

    @Override
    public String toString()
    {
        StringBuilder ret = new StringBuilder();
        if (this.leaf) return pos.x+" "+pos.y+" "+pos.z+" | "+size.x+" "+size.y+" "+size.z+" : "+value.toString()+"\n";
        else for (int i = 0; i < numberOfChildren; i++) { ret.append(children[i].toString());}
        return ret.toString();
    }

    private boolean isFullyContained(Vector3d pos, Vector3d size)
    {
        return (pos.x - size.x <= this.pos.x - this.size.x &&
                pos.x + size.x >= this.pos.x + this.size.x &&
                pos.y - size.y <= this.pos.y - this.size.y &&
                pos.y + size.y >= this.pos.y + this.size.y &&
                pos.z - size.z <= this.pos.z - this.size.z &&
                pos.z + size.z >= this.pos.z + this.size.z );
    }

     private boolean isFullyOutside(Vector3d pos, Vector3d size)
     {
         return (this.pos.x + this.size.x <= pos.x - size.x  ||
                 this.pos.y + this.size.y <= pos.y - size.y  ||
                 this.pos.z + this.size.z <= pos.z - size.z  ||
                 this.pos.x - this.size.x >= pos.x + size.x  ||
                 this.pos.y - this.size.y >= pos.y + size.y  ||
                 this.pos.z - this.size.z >= pos.z + size.z  );
     }

     private void setChildValues(Vector3d pos, Vector3d size, V value)
     {
         for (int i = 0; i < numberOfChildren; i++) {
             this.children[i].setValue(pos, size, value);
         }

         //Any of them is not leaf, we don't merge
         for (int i = 0; i < numberOfChildren; i++) {
             if (!children[i].leaf) return;
         }

         //Any of them has a different value, we don't merge
         for (int i = 1; i < numberOfChildren; i++)
         {
             if (children[0].value != children[i].value) return;
         }

         //They are same values, they are all leaves
         merge();
     }

     /*   4------5
         /|     /|
        0------1 |
        | |    | |
        | 6----|-7
        |/     |/
        2------3
     */
     protected abstract void split();
     /*{
         this.leaf = false;
         Vector3d childSize = new Vector3d(this.size.x/2.0d,this.size.y/2.0d,this.size.z/2.0d);

         children[0] = new Octree<>(new Vector3d(pos.x-childSize.x,pos.y-childSize.y,pos.z-childSize.z), childSize, this.value);
         children[1] = new Octree<>(new Vector3d(pos.x+childSize.x,pos.y-childSize.y,pos.z-childSize.z), childSize, this.value);
         children[2] = new Octree<>(new Vector3d(pos.x-childSize.x,pos.y+childSize.y,pos.z-childSize.z), childSize, this.value);
         children[3] = new Octree<>(new Vector3d(pos.x+childSize.x,pos.y+childSize.y,pos.z-childSize.z), childSize, this.value);
         children[4] = new Octree<>(new Vector3d(pos.x-childSize.x,pos.y-childSize.y,pos.z+childSize.z), childSize, this.value);
         children[5] = new Octree<>(new Vector3d(pos.x+childSize.x,pos.y-childSize.y,pos.z+childSize.z), childSize, this.value);
         children[6] = new Octree<>(new Vector3d(pos.x-childSize.x,pos.y+childSize.y,pos.z+childSize.z), childSize, this.value);
         children[7] = new Octree<>(new Vector3d(pos.x+childSize.x,pos.y+childSize.y,pos.z+childSize.z), childSize, this.value);

     }*/

     private void merge()
     {
         this.leaf = true;
         this.value = children[0].value;
         for (int i = 0; i < numberOfChildren; i++)
         {
             children[i] = null;
         }
     }

}
