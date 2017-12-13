package pikashot;

public class CalculTrigo {
	public static double distanceObjMe(double DistanceExpeMe,double AngleExpeMe,double DistanceExpeObj,double AngleExpeObj){
		double xa = DistanceExpeMe*Math.cos(Math.toRadians(AngleExpeMe));
		double ya = DistanceExpeMe*Math.sin(Math.toRadians(AngleExpeMe));
		double xb = DistanceExpeObj*Math.cos(Math.toRadians(AngleExpeObj));
		double yb = DistanceExpeObj*Math.sin(Math.toRadians(AngleExpeObj));
		double xc = xa + xb;
		double yc = ya + yb;
		return Math.sqrt(Math.pow(xc,2) + Math.pow(yc,2));
		//return Math.pow(DistanceExpeMe,2) + Math.pow(DistanceExpeObj,2) - 2*DistanceExpeMe*DistanceExpeObj*Math.cos(Math.toRadians(AngleExpeObj-AngleExpeMe));
	}
	
	public static double angleObjMe(double DistanceExpeMe,double AngleExpeMe,double DistanceExpeObj,double AngleExpeObj){
		double xa = DistanceExpeMe*Math.cos(Math.toRadians(AngleExpeMe));
		double ya = DistanceExpeMe*Math.sin(Math.toRadians(AngleExpeMe));
		double xb = DistanceExpeObj*Math.cos(Math.toRadians(AngleExpeObj));
		double yb = DistanceExpeObj*Math.sin(Math.toRadians(AngleExpeObj));
		double xc = xa + xb;
		double yc = ya + yb;
		return LogicDegree(Math.toDegrees(Math.atan2(yc,xc)));
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
