package myteam;

import edu.warbot.brains.WarBrain;
//import edu.warbot.agents.resources.WarFood;
import edu.warbot.brains.brains.WarLightBrain;

public abstract class WarLightBrainController extends  WarLightBrain {
	WTask ctask;
	static WTask handleMsgs = new WTask(){ 
		String exec(WarBrain bc){
			return "";
		}
	};
		
    /*public WarLightBrainController() {
        super();
    }

    /*@Override
    public String action() {

          List<WarMessage> messages = getMessages();
    	  for (WarMessage message : messages) {
              if (message.getMessage().equals("goThere"))
              {          	  
            	  this.setM_etatRush(true);
            	  String[] list = message.getContent();
            	  double Tetac = CalculTrigo.angleObjMe(message.getDistance(), message.getAngle(), Double.parseDouble(list[0]), Double.parseDouble(list[1]));
            	  setDebugString("Angle cible : "+Tetac);
            	  this.setHeading(Tetac);
            	  setM_etatRush(true);
              }
          }
    	  
    	  if(this.isM_etatRush())
    	  {this.setDebugString("RUSH");
            for (WarAgentPercept wp : getPerceptsEnemies()) {
            				
    		              if (isEnemy(wp) && !wp.getType().equals(WarAgentType.WarFood)) {
    					 		this.broadcastMessageToAgentType(WarAgentType.WarLight, "goThere",
    					 				String.valueOf(wp.getDistance()), String.valueOf(wp.getAngle()));
    			             
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
    	  else {
    		  for (WarAgentPercept wp : getPerceptsEnemies()) {
    	       if (isEnemy(wp) && !wp.getType().equals(WarAgentType.WarFood)) {
    	    	   this.broadcastMessageToAgentType(WarAgentType.WarLight, "goThere",
			 				String.valueOf(wp.getDistance()), String.valueOf(wp.getAngle()));
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
    		  
    }

	public boolean isM_etatRush() {
		return m_etatRush;
	}

	public void setM_etatRush(boolean m_etatRush) {
		this.m_etatRush = m_etatRush;
	}*/

}