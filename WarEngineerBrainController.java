package pikashot;

import edu.warbot.agents.agents.WarEngineer;
import edu.warbot.agents.agents.WarExplorer;
import edu.warbot.agents.agents.WarLight;
import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.agents.percepts.WarAgentPercept;
import edu.warbot.agents.percepts.WarPercept;
import edu.warbot.agents.resources.WarFood;
import edu.warbot.brains.WarBrain;
import edu.warbot.brains.brains.WarEngineerBrain;
import edu.warbot.communications.WarMessage;

public abstract class WarEngineerBrainController extends WarEngineerBrain {
	WTask ctask;
	static WTask handleMsgs = new WTask() { 
		String exec(WarBrain bc){
			return "";
		} 
	};

    public WarEngineerBrainController() {
        super();
        ctask = IGonnaBuildATowerInFood;
    }
    
    static WTask IGonnaBuildATowerInFood = new WTask(){
		String exec(WarBrain bc){
			WarEngineerBrainController me = (WarEngineerBrainController) bc;
			me.setDebugString("Ingenieur informaticieeeeeen !");
			me.broadcastMessageToAgentType(WarAgentType.WarBase,"Where is the food ?","");
            for(WarMessage m : me.getMessages()) {
            	if(m.getMessage() == "Food here") {
					me.setHeading(CalculTrigo.angleObjMe(m.getDistance(),m.getAngle(),Double.parseDouble(m.getContent()[0]),Double.parseDouble(m.getContent()[1])));
					me.setNextBuildingToBuild(WarAgentType.WarTurret);
					if(CalculTrigo.distanceObjMe(m.getDistance(),m.getAngle(),Double.parseDouble(m.getContent()[0]),Double.parseDouble(m.getContent()[1])) < 2) {
						me.ctask = GoChill;
						return ACTION_BUILD;
					}
            	}
            }
            if(me.isBlocked())
				me.setRandomHeading();
            return ACTION_MOVE;
		}
	};
	
	static WTask GoChill = new WTask(){
		String exec(WarBrain bc){
			WarEngineerBrainController me = (WarEngineerBrainController) bc;
			me.setDebugString("siffler en travaillant");	
            if(me.getNbElementsInBag() > 0) {
            	me.setDebugString("manger c'est stylé");	
            	return ACTION_EAT;
            }
            if(me.getHealth() > 1250) {
				me.setNextBuildingToBuild(WarAgentType.WarTurret);
				me.setDebugString("Et une tourelle pour la 6");	
				return ACTION_BUILD;
            }
            for ( WarAgentPercept agtPcpt : me.getPercepts())
			{
				switch(agtPcpt.getType()) {
				case WarFood:
					if(!me.isBagFull())
					{
						me.setDebugString("MIAM");
						me.setHeading(agtPcpt.getAngle());
						if(agtPcpt.getDistance()<WarFood.MAX_DISTANCE_TAKE)	
							return WarExplorer.ACTION_TAKE;
						else
							return WarExplorer.ACTION_MOVE;
					}
				default:
					break;
				}
			}
            if(me.isBlocked()) {
            	me.broadcastMessageToAgentType(WarAgentType.WarBase,"Where is the food ?","");
            	for(WarMessage m : me.getMessages()) {
            		if(m.getMessage() == "Food here") {
            			me.setHeading(CalculTrigo.angleObjMe(m.getDistance(),m.getAngle(),Double.parseDouble(m.getContent()[0]),Double.parseDouble(m.getContent()[1])));
            		}
            	}
			}	
            return ACTION_MOVE;
		}
	};
    
    @Override
	public String action() {
		String toReturn = ctask.exec(this);   // le run de la FSM
		if(toReturn == null)
		{
			if (isBlocked())
				setRandomHeading();
			setDebugString("Heavy waitinggg ...");
			
			return WarLight.ACTION_MOVE;
		} 
		else
			return toReturn;
	}
}
