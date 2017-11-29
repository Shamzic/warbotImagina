package myteam;

public class CalculTrigo {
	public static double distanceObjMe(double DistanceExpeMe,double AngleExpeMe,double DistanceExpeObj,double AngleExpeObj){
		double xa = DistanceExpeMe*Math.toRadians(Math.cos(AngleExpeMe));
		double ya = DistanceExpeMe*Math.toRadians(Math.sin(AngleExpeMe));
		double xb = DistanceExpeObj*Math.toRadians(Math.cos(AngleExpeObj));
		double yb = DistanceExpeObj*Math.toRadians(Math.sin(AngleExpeObj));
		double xc = xa + xb;
		double yc = ya + yb;
		return Math.sqrt((xc*xc)/(yc*yc));
	}
	
	public static double angleObjMe(double DistanceExpeMe,double AngleExpeMe,double DistanceExpeObj,double AngleExpeObj){
		double xa = DistanceExpeMe*Math.toRadians(Math.cos(AngleExpeMe));
		double ya = DistanceExpeMe*Math.toRadians(Math.sin(AngleExpeMe));
		double xb = DistanceExpeObj*Math.toRadians(Math.cos(AngleExpeObj));
		double yb = DistanceExpeObj*Math.toRadians(Math.sin(AngleExpeObj));
		double xc = xa + xb;
		double yc = ya + yb;
		return Math.toDegrees(Math.atan(yc/xc));
	}
}
