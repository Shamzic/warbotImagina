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
	ArrayList<Double[]> TurretPos = new ArrayList<Double[]>();
	WTask ctask; // FSM
	ArrayList<WarAgentType> agentListInit = new ArrayList<WarAgentType>();
	boolean ingenieur = false;
	int time;
	
    public WarBaseBrainController() {
        super();
        ctask = listen;
        time = 0;
        agentListInit.add(WarAgentType.WarExplorer);
        agentListInit.add(WarAgentType.WarExplorer);
        agentListInit.add(WarAgentType.WarLight);
        agentListInit.add(WarAgentType.WarLight);
        agentListInit.add(WarAgentType.WarLight);
        agentListInit.add(WarAgentType.WarHeavy);
        agentListInit.add(WarAgentType.WarHeavy);
    }
    
    
	static WTask listen = new WTask(){
		@SuppressWarnings("unused")
		String exec(WarBrain bc){
			WarBaseBrainController me = (WarBaseBrainController) bc;
			WarMessage m = me.getMessageFromExplorer();
			m = me.getMessageFromFighter();
			while(me.agentListInit.size() != 0 && me.getNbElementsInBag() >= 0 && me.getHealth() >= 0.3 * me.getMaxHealth())
			{
				me.setNextAgentToCreate(me.agentListInit.get(0));
				me.agentListInit.remove(0);
				return WarBase.ACTION_CREATE;
			}
			if(!me.ingenieur && me.FoodPos.size() > 3) {
				me.ingenieur = true;
				addAgent(me,WarAgentType.WarEngineer,1);
			}
			if(me.OppBasePos[0] != -1) {
				addAgent(me,WarAgentType.WarRocketLauncher,1);
				addAgent(me,WarAgentType.WarHeavy,2);
			}
			if(me.time % 500 == 0 && me.time != 0) {
				addAgent(me,WarAgentType.WarHeavy,2);
				addAgent(me,WarAgentType.WarLight,1);
			}
			if(me.getNbElementsInBag() >= 0 && me.getHealth() < 0.8 * me.getMaxHealth())
				return ACTION_EAT;
			return ACTION_IDLE;
		}
	};
	
	static void addAgent(WarBaseBrainController me, WarAgentType type, int number) {
		for(int i=0; i<number; i++)
			me.agentListInit.add(type);
	};
    
    static WTask handleMsgs = new WTask(){ String exec(WarBrain bc){return "";}};
    
    @Override
    public String action() {
    	time++;
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
		int nbExp = 0;
		for (WarMessage m : getMessages()) {
			String[] listC = m.getContent();
			if(m.getMessage().equals("Food here") && m.getSenderType().equals(WarAgentType.WarExplorer))
			{
				Double[] lastFood = {0.0,0.0};
				lastFood[0] = CalculTrigo.distanceObjMe(m.getDistance(), m.getAngle(),
						Double.parseDouble(listC[0]), Double.parseDouble(listC[1]));
				lastFood[1] = CalculTrigo.angleObjMe(m.getDistance(), m.getAngle(),
						Double.parseDouble(listC[0]), Double.parseDouble(listC[1]));
				FoodPos.add(lastFood);
				int i;
				for(i = 0,ActualFoodZone[0] = 0.0,ActualFoodZone[1] = 0.0;i<FoodPos.size();i++)
				{
					ActualFoodZone[0]+=FoodPos.get(i)[0];
					ActualFoodZone[1]+=FoodPos.get(i)[1];
				}
				ActualFoodZone[0] /= FoodPos.size();
				ActualFoodZone[1] /= FoodPos.size();
				setDebugString("pos " + ActualFoodZone[0] +" " + ActualFoodZone[1]);
			}
			if(m.getMessage().equals("Turret here") && m.getSenderType().equals(WarAgentType.WarExplorer)){
				Double[] TurretFood = {0.0,0.0};
				TurretFood[0] = CalculTrigo.distanceObjMe(m.getDistance(), m.getAngle(),
						Double.parseDouble(listC[0]), Double.parseDouble(listC[1]));
				TurretFood[1] = CalculTrigo.angleObjMe(m.getDistance(), m.getAngle(),
						Double.parseDouble(listC[0]), Double.parseDouble(listC[1]));
				TurretPos.add(TurretFood);
			}
			if(m.getMessage().equals("Base here")){
				OppBasePos[0] = CalculTrigo.distanceObjMe(m.getDistance(), m.getAngle(),
						Double.parseDouble(listC[0]), Double.parseDouble(listC[1]));
				OppBasePos[1] = CalculTrigo.angleObjMe(m.getDistance(), m.getAngle(),
						Double.parseDouble(listC[0]), Double.parseDouble(listC[1]));
			}
			if(m.getMessage().equals("Where are you ?") && m.getSenderType().equals(WarAgentType.WarExplorer)){
				reply(m,"I'm here","");
				setDebugString("I'm here");
			}
			if(m.getMessage().equals("He is alive") && m.getSenderType().equals(WarAgentType.WarExplorer)){
				nbExp++;
			}
			if(m.getMessage().equals("Where is the food ?")){
				if(ActualFoodZone[0] != -1)
					reply(m,"Food here",Double.toString(ActualFoodZone[0]),Double.toString(ActualFoodZone[1]));
					setDebugString("pos " + ActualFoodZone[0] +" " + ActualFoodZone[1]);
			}
		}
		if(nbExp < 5)
			agentListInit.add(WarAgentType.WarExplorer);
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