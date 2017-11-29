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
		return Math.toDegrees(Math.atan2(yc,xc));
	}
	
	public static double abscisseDistanceObj(double distanceMeTarget,double angle) {
		return distanceMeTarget*Math.cos(Math.toRadians(angle));
	}
	
	public static double ordonneeDistanceObj(double distanceMeTarget,double angle) {
		return distanceMeTarget*Math.sin(Math.toRadians(angle));
	}
	

	public static double LogicDegree(double angleObjMe) {
		double temp = angleObjMe;
		for(;temp < 0;temp += 360){
			;
		}
		return temp;
	}
}
