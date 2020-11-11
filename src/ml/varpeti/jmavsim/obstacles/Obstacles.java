package ml.varpeti.jmavsim.obstacles;

import me.drton.jmavsim.KinematicObject;
import me.drton.jmavsim.World;

import javax.vecmath.Vector3d;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Obstacles {
    public static World world;
    public static boolean showGui;
    public static KinematicObject vehicle;
    public static Vector3d vehicleSize;
    public static V3DOctree myOctree;

    public static boolean initialized = false;

    public static void newObstacles(World world, boolean showGui, KinematicObject vehicle, Vector3d vehicleSize) {

        if (initialized) return; //It makes it singleton :?

        final String map = "obstacles/obstacles.map";
        final double minSize = 0.25*0.25*0.25;
        final Vector3d rootPos = new Vector3d(0.0d,0.0d,-32.0d);
        final Vector3d rootSize = new Vector3d(32.0d,32.0d,32.0d); //Maxsize

        Obstacles.world = world;
        Obstacles.showGui = showGui;
        Obstacles.vehicle = vehicle;
        Obstacles.vehicleSize = vehicleSize;
        Obstacles.myOctree = new V3DOctree(rootPos,rootSize,objectType.Air);
        Octree.minSize = minSize;

        //Add cubes from file //TODO or generate from seed

        try {
            Scanner myReader = new Scanner(new File(map));

            while (myReader.hasNextLine()) {
                Vector3d a = mine(myReader.nextLine());
                //synchronized (Obstacles.world) {world.addObject(new Cube(a, a.toString()));}
                Obstacles.myOctree.setValue(a,new Vector3d(0.125,0.125,0.125), objectType.Obstacle);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.err.println("File not found: "+System.getProperty("user.dir")+"/"+ map);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Obstacles.myOctree.draw();
        System.out.println(Obstacles.myOctree);
    }

    private static Vector3d mine(String data) throws Exception {
        Pattern p = Pattern.compile("([\\d.-]*) ([\\d.-]*) ([\\d.-]*)");
        Matcher m = p.matcher(data);
        if (m.find()) {
            return new Vector3d(Double.parseDouble(m.group(1)),Double.parseDouble(m.group(2)),Double.parseDouble(m.group(3)));
        }
        throw new Exception("Invalid file");
    }
}
