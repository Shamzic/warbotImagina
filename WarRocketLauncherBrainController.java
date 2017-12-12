package pikashot;


import java.util.List;

import edu.warbot.agents.agents.WarRocketLauncher;
import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.agents.percepts.WarAgentPercept;
import edu.warbot.brains.WarBrain;
import edu.warbot.brains.brains.WarRocketLauncherBrain;
import edu.warbot.communications.WarMessage;


public abstract class WarRocketLauncherBrainController extends WarRocketLauncherBrain {

	@SuppressWarnings("unused")
	private double OppDistance = 0;
	private WarMessage wm = null;
	private WarAgentPercept target = null;
	WTask ctask;

	static WTask handleMsgs = new WTask() { 
		String exec(WarBrain bc){
			return "";
		} 
	};
	
	@Override
	public String action() {
		String toReturn = ctask.exec(this);   // le run de la FSM
		if(toReturn == null){
			if (isBlocked())
				setRandomHeading();
			return WarRocketLauncher.ACTION_MOVE;
		} else {
			return toReturn;
		}
	}
	
	public WarRocketLauncherBrainController() {
        super();
        ctask = waitForInstruction; 
    }
	
	static WTask waitForInstruction = new WTask(){
		String exec(WarBrain bc){
			WarRocketLauncherBrainController me = (WarRocketLauncherBrainController) bc;
			listenMessages(me); 
			me.setDebugString("Waiting for instructions");
			me.broadcastMessageToAgentType(WarAgentType.WarBase, "Where is the base ?", "");
			return WarRocketLauncher.ACTION_IDLE;
		}
	};
	
	static void listenMessages(WarRocketLauncherBrainController me) {
		List<WarMessage> messages = me.getMessages();
		for (WarMessage message : messages) 
		{
			if (message.getMessage().equals("Base here"))
			{
				me.setWarMessage(message);
				me.ctask = rushToTheEnnemiBase;
			}
		}
	}
	
	static WTask rushToTheEnnemiBase = new WTask(){
		String exec(WarBrain bc){
			WarRocketLauncherBrainController me = (WarRocketLauncherBrainController) bc;
			WarMessage message = me.getWarMessage();
			String[] list = message.getContent();
			me.setDebugString("OMW");
			me.broadcastMessageToAgentType(WarAgentType.WarBase, "Where is the base ?", "");
			if(message.getMessage().equals("Base here"))
			{	
				if(me.amIInRange(message.getDistance(), message.getAngle(), Double.parseDouble(list[0]), Double.parseDouble(list[1])))
					return ACTION_IDLE;
						
			}
			listenMessages(me);
			if(me.isBlocked())
				me.setRandomHeading();
			return WarRocketLauncher.ACTION_MOVE;
		}
	};
	
	static WTask fightToTheEnd = new WTask(){
		String exec(WarBrain bc){
			WarRocketLauncherBrainController me = (WarRocketLauncherBrainController) bc;
			me.setDebugString("FIRE !");	
             if (me.isReloaded()) {
            	 me.setTargetDistance(150);
                 return ACTION_FIRE;
             }
             else if (me.isReloading()) {      	 
            	 listenMessages(me);
                 return ACTION_IDLE;
             }
             else {
                 return ACTION_RELOAD;
             }
		}
	};
	
	public void setWarMessage(WarMessage wm) {
		this.wm= wm;
	}
	
	protected boolean amIInRange(double DistanceExpeMe,double AngleExpeMe,double DistanceExpeObj,double AngleExpeObj) {
		double Tetac = CalculTrigo.angleObjMe(DistanceExpeMe, AngleExpeMe, DistanceExpeObj, AngleExpeObj);
		setDebugString(Boolean.toString(CalculTrigo.distanceObjMe(DistanceExpeMe, AngleExpeMe, DistanceExpeObj, AngleExpeObj) < Range()));
		setHeading(Tetac);
		if(CalculTrigo.distanceObjMe(DistanceExpeMe, AngleExpeMe, DistanceExpeObj, AngleExpeObj) < Range()) {
			OppDistance = CalculTrigo.distanceObjMe(DistanceExpeMe, AngleExpeMe, DistanceExpeObj, AngleExpeObj);
			ctask = fightToTheEnd;
			return true;
		}
		return false;
	}

	private double Range() {
		return 150;
	}
	
	
	public WarMessage getWarMessage() {
		return wm;
	}

	public WarAgentPercept getTarget() {
		return target;
	}

	public void setTarget(WarAgentPercept wb) {
		this.target = wb;
	}
}
