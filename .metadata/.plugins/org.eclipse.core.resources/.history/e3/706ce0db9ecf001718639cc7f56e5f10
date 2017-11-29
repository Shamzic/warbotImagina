package myteam;

import java.util.List;

import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.agents.percepts.WarAgentPercept;
import edu.warbot.brains.brains.WarLightBrain;
import edu.warbot.communications.WarMessage;

public abstract class WarLightBrainController extends  WarLightBrain {

	private boolean m_etatRush;

    public WarLightBrainController() {
        super();
        this.setM_etatRush(false);
    }

    @Override
    public String action() {

//        for (WarAgentPercept wp : getPerceptsEnemies()) {
//
//            if (!wp.getType().equals(WarAgentType.WarBase) && !wp.getType().equals(WarAgentType.WarFood)) {
//
//                setHeading(wp.getAngle());
//                this.setDebugString("FIRE BITCH");
//                if (isReloaded())
//                    return ACTION_FIRE;
//                else if (isReloading())
//                    return ACTION_IDLE;
//                else
//                    return ACTION_RELOAD;
//            }
//        }
//
//        if (isBlocked())
//            setRandomHeading();
//
//        return ACTION_MOVE;

          List<WarMessage> messages = getMessages();
    	  for (WarMessage message : messages) {
              if (message.getMessage().equals("goThere"))
              {
            	  this.setM_etatRush(true);
            	  String[] list = message.getContent();
            	  this.setHeading(Double.parseDouble(list[0])+message.getAngle());
              }
          }
    	  
    	  if(this.isM_etatRush())
    	  {
            for (WarAgentPercept wp : getPerceptsEnemies()) {
    		  
    		              if (wp.getType().equals(WarAgentType.WarBase)) {
    		  
    		                  setHeading(wp.getAngle());
    		                  this.setDebugString("FIRE BITCH");
    		                  if (isReloaded())
    		                      return ACTION_FIRE;
    		                  else if (isReloading())
    		                      return ACTION_IDLE;
    		                  else
    		                      return ACTION_RELOAD;
    		              }
    		          }
    		  
    		          if (isBlocked())
    		              setRandomHeading();
    		  
    		  
    		  return ACTION_MOVE;
    	  }
    	  else  
    		  return ACTION_IDLE;
    }

	public boolean isM_etatRush() {
		return m_etatRush;
	}

	public void setM_etatRush(boolean m_etatRush) {
		this.m_etatRush = m_etatRush;
	}

}