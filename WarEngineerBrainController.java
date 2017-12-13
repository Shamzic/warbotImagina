package pikashot;

import edu.warbot.agents.agents.WarEngineer;
import edu.warbot.agents.agents.WarLight;
import edu.warbot.brains.WarBrain;
import edu.warbot.brains.brains.WarEngineerBrain;

public abstract class WarEngineerBrainController extends WarEngineerBrain {
	WTask ctask;
	static WTask handleMsgs = new WTask() { 
		String exec(WarBrain bc){
			return "";
		} 
	};

    public WarEngineerBrainController() {
        super();
    }
    
    static WTask fightToTheEnd = new WTask(){
		String exec(WarBrain bc){
			WarEngineerBrainController me = (WarEngineerBrainController) bc;
			me.setDebugString("Ingenieur informaticieeeeeen !");	
            return ACTION_IDLE;
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
