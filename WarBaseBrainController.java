package pikashot;

import java.util.ArrayList;

import edu.warbot.agents.agents.WarBase;
import edu.warbot.agents.enums.WarAgentType;
//import edu.warbot.agents.agents.WarBase;
//import edu.warbot.agents.agents.WarExplorer;
//import edu.warbot.agents.enums.WarAgentCategory;
//import edu.warbot.agents.enums.WarAgentType;
//import edu.warbot.agents.percepts.WarAgentPercept;
import edu.warbot.brains.WarBrain;
import edu.warbot.brains.brains.WarBaseBrain;
//import edu.warbot.communications.WarMessage;
//
//import java.util.List;
import edu.warbot.communications.WarMessage;

public abstract class WarBaseBrainController extends WarBaseBrain {
	Double[] OppBasePos = {-1.0,-1.0};
	Double[] ActualFoodZone = {-1.0,-1.0};
	ArrayList<Double[]> FoodPos = new ArrayList<Double[]>();
	WTask ctask; // FSM
	
    public WarBaseBrainController() {
        super();
        ctask = listen;
    }
    
    
	static WTask listen = new WTask(){
		@SuppressWarnings("unused")
		String exec(WarBrain bc){
			WarBaseBrainController me = (WarBaseBrainController) bc;
			WarMessage m = me.getMessageFromExplorer();
			m = me.getMessageFromFighter();
			/*if(me.getNbElementsInBag() >= 0 && me.getHealth() >= 0.3 * me.getMaxHealth()){
				me.setNextAgentToCreate(WarAgentType.WarHeavy);
				me.setDebugString(String.valueOf(me.getNbElementsInBag()));
				return WarBase.ACTION_CREATE;
			}*/
			return ACTION_IDLE;
		}
		};
    
    static WTask handleMsgs = new WTask(){ String exec(WarBrain bc){return "";}};
    
    @Override
    public String action() {
    	
		String toReturn = ctask.exec(this);   // le run de la FSM
		
		if(toReturn == null){
			return WarBase.ACTION_IDLE;
		} else {
			return toReturn;
		}
	}
    
    private WarMessage getMessageFromFighter(){
    	for (WarMessage m : getMessages()) {
			//String[] listC = m.getContent();
			if(m.getMessage().equals("Where is the base ?") && OppBasePos[0] != -1){
				reply(m,"Base here",Double.toString(OppBasePos[0]),Double.toString(OppBasePos[1]));
				setDebugString("pos : " + OppBasePos[0] + " " + OppBasePos[1]);
			}
    	}
    	return null;
    }
    
	private WarMessage getMessageFromExplorer() {
		for (WarMessage m : getMessages()) {
			String[] listC = m.getContent();
			if(m.getMessage().equals("Food here") && m.getSenderType().equals(WarAgentType.WarExplorer)){
				Double[] lastFood = {0.0,0.0};
				lastFood[0] = CalculTrigo.distanceObjMe(m.getDistance(), m.getAngle(),
						Double.parseDouble(listC[0]), Double.parseDouble(listC[1]));
				lastFood[1] = CalculTrigo.angleObjMe(m.getDistance(), m.getAngle(),
						Double.parseDouble(listC[0]), Double.parseDouble(listC[1]));
				FoodPos.add(lastFood);
				int i;
				for(i = 0,ActualFoodZone[0] = 0.0,ActualFoodZone[1] = 0.0;i<FoodPos.size();i++){
					ActualFoodZone[0]+=FoodPos.get(i)[0];
					ActualFoodZone[1]+=FoodPos.get(i)[1];
				}
				ActualFoodZone[0] /= FoodPos.size();
				ActualFoodZone[1] /= FoodPos.size();
			}
			if(m.getMessage().equals("Base here") && m.getSenderType().equals(WarAgentType.WarExplorer)){
				OppBasePos[0] = CalculTrigo.distanceObjMe(m.getDistance(), m.getAngle(),
						Double.parseDouble(listC[0]), Double.parseDouble(listC[1]));
				OppBasePos[1] = CalculTrigo.angleObjMe(m.getDistance(), m.getAngle(),
						Double.parseDouble(listC[0]), Double.parseDouble(listC[1]));
			}
			if(m.getMessage().equals("Where are you ?") && m.getSenderType().equals(WarAgentType.WarExplorer)){
				reply(m,"I'm here","");
				setDebugString("I'm here");
			}
			if(m.getMessage().equals("Where is the food ?")){
				reply(m,"base here",Double.toString(ActualFoodZone[0]),Double.toString(ActualFoodZone[1]));
			}
		}
		return null;
	}
	
	
//    	
//    	
//    	if(this.isM_alertMessage())
//    		setDebugString("Base ennemie decouverte");
//    	if(isM_attacked())
//    		setDebugString("PLS GENERALE");
//
//        if (!_alreadyCreated) {
//            setNextAgentToCreate(WarAgentType.WarEngineer);
//            _alreadyCreated = true;
//            return WarBase.ACTION_CREATE;
//        }
//        
//        
//        
//        if(getNbElementsInBag() >= 0 && getHealth() >= 0.3 * getMaxHealth()){
//        	setNextAgentToCreate(WarAgentType.WarLight);
//        	setDebugString(String.valueOf(getNbElementsInBag()));
//            return WarBase.ACTION_CREATE;
//        }
//        
//        
//        if (getNbElementsInBag() >= 0 && getHealth() <= 0.8 * getMaxHealth())
//            return WarBase.ACTION_EAT;
//
//        if (getMaxHealth() == getHealth()) {
//            _alreadyCreated = true;
//        }
//
//        List<WarMessage> messages = getMessages();
//
//        for (WarMessage message : messages) {
//            if (message.getMessage().equals("whereAreYou"))
//            {
//            	setDebugString("I'm here ! ");
//                reply(message, "I'm here");
//            }
//            
//            else if (message.getMessage().equals("EnnemiBaseDiscovered"))
//            {
//            	this.setM_alertMessage(true);
//            	//String[] list = message.getContent();
//            	//this.broadcastMessageToAgentType(WarAgentType.WarLight, "goThere", list[0], list[1]);
//            }
//        }
//
//        for (WarAgentPercept percept : getPerceptsEnemies()) {
//            if (isEnemy(percept) && percept.getType().getCategory().equals(WarAgentCategory.Soldier))
//            {
//                broadcastMessageToAll("I'm under attack",
//                        String.valueOf(percept.getAngle()),
//                        String.valueOf(percept.getDistance()));
//                setM_attacked(true);
//            }
//        }
//
//        for (WarAgentPercept percept : getPerceptsResources()) {
//            if (percept.getType().equals(WarAgentType.WarFood))
//                broadcastMessageToAgentType(WarAgentType.WarExplorer, "I detected food",
//                        String.valueOf(percept.getAngle()),
//                        String.valueOf(percept.getDistance()));
//        }
//        return WarBase.ACTION_IDLE;
//    }
}