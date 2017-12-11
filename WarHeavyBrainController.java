package myteam;

import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.agents.percepts.WarAgentPercept;
import edu.warbot.brains.brains.WarHeavyBrain;

public abstract class WarHeavyBrainController extends  WarHeavyBrain {


    public WarHeavyBrainController() {
        super();
    }

    @Override
    public String action() {
    	
    	this.setDebugString("Balade");
        for (WarAgentPercept wp : getPerceptsEnemies()) {

            if (wp.getType().equals(WarAgentType.WarBase)/* || 
            		wp.getType().equals(WarAgentType.WarRocketLauncher) || 
            		wp.getType().equals(WarAgentType.WarHeavy) */ ) {

                setHeading(wp.getAngle());

                if (isReloaded())
                {
                	this.setDebugString("Feu !");
                	return ACTION_FIRE;
                }
                else if (isReloading())
                {
                    return ACTION_IDLE;
                }
                else
                    return ACTION_RELOAD;
            }
        }
    	

        if (isBlocked())
            setRandomHeading();

        return ACTION_MOVE;
    }

}