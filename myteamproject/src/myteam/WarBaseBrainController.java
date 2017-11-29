package myteam;

import edu.warbot.agents.agents.WarBase;
import edu.warbot.agents.enums.WarAgentCategory;
import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.agents.percepts.WarAgentPercept;
import edu.warbot.brains.brains.WarBaseBrain;
import edu.warbot.communications.WarMessage;

import java.util.List;

public abstract class WarBaseBrainController extends WarBaseBrain {

    private boolean _alreadyCreated;
    private boolean _inDanger;
    private boolean m_alertMessage;
    private boolean m_attacked;

    public WarBaseBrainController() {
        super();

        _alreadyCreated = false;
        set_inDanger(false);
        setM_alertMessage(false);
        setM_attacked(false);
    }


    @Override
    public String action() {
    	
    	if(this.isM_alertMessage())
    		setDebugString("Base ennemie decouverte");
    	if(isM_attacked())
    		setDebugString("PLS GENERALE");

        if (!_alreadyCreated) {
            setNextAgentToCreate(WarAgentType.WarEngineer);
            _alreadyCreated = true;
            return WarBase.ACTION_CREATE;
        }

        if (getNbElementsInBag() >= 0 && getHealth() <= 0.8 * getMaxHealth())
            return WarBase.ACTION_EAT;

        if (getMaxHealth() == getHealth()) {
            _alreadyCreated = true;
        }

        List<WarMessage> messages = getMessages();

        for (WarMessage message : messages) {
            if (message.getMessage().equals("whereAreYou"))
            {
            	setDebugString("I'm here ! ");
                reply(message, "I'm here");
            }
            
            else if (message.getMessage().equals("EnnemiBaseDiscovered"))
            {
            	this.setM_alertMessage(true);
            	//String[] list = message.getContent();
            	//this.broadcastMessageToAgentType(WarAgentType.WarLight, "goThere", list[0], list[1]);
            }
        }

        for (WarAgentPercept percept : getPerceptsEnemies()) {
            if (isEnemy(percept) && percept.getType().getCategory().equals(WarAgentCategory.Soldier))
            {
                broadcastMessageToAll("I'm under attack",
                        String.valueOf(percept.getAngle()),
                        String.valueOf(percept.getDistance()));
                setM_attacked(true);
            }
        }

        for (WarAgentPercept percept : getPerceptsResources()) {
            if (percept.getType().equals(WarAgentType.WarFood))
                broadcastMessageToAgentType(WarAgentType.WarExplorer, "I detected food",
                        String.valueOf(percept.getAngle()),
                        String.valueOf(percept.getDistance()));
        }

        return WarBase.ACTION_IDLE;
    }


	public boolean is_inDanger() {
		return _inDanger;
	}


	public void set_inDanger(boolean _inDanger) {
		this._inDanger = _inDanger;
	}


	public boolean isM_alertMessage() {
		return m_alertMessage;
	}


	public void setM_alertMessage(boolean m_alertMessage) {
		this.m_alertMessage = m_alertMessage;
	}


	public boolean isM_attacked() {
		return m_attacked;
	}


	public void setM_attacked(boolean m_attacked) {
		this.m_attacked = m_attacked;
	}

}
