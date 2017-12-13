package pikashot;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.jfree.data.statistics.MeanAndStandardDeviation;

import edu.warbot.agents.agents.WarExplorer;
import edu.warbot.agents.agents.WarHeavy;
import edu.warbot.agents.agents.WarLight;
import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.agents.percepts.WarAgentPercept;
import edu.warbot.agents.resources.WarFood;
import edu.warbot.brains.WarBrain;
import edu.warbot.brains.brains.WarLightBrain;
import edu.warbot.communications.WarMessage;
import jogamp.common.os.elf.SectionHeader;

@SuppressWarnings("unused")
public abstract class WarLightBrainController extends  WarLightBrain {
		
		private WarMessage wm = null;
		private WarAgentPercept target = null;
		private int angleSkirt;
		private boolean montee;
		WTask ctask;
		//private boolean m_etatRush =false;
		static WTask handleMsgs = new WTask() { 
			String exec(WarBrain bc){
				return "";
			} 
		};
			
	    public WarLightBrainController() {
	        super();
	        this.angleSkirt = 90;
	        this.montee=true;
	        ctask = waitForInstruction; 
	    }
	    
		@Override
		public String action() {
			String toReturn = ctask.exec(this);   // le run de la FSM
			if(toReturn == null)
			{
				if (isBlocked())
					setRandomHeading();
				setDebugString("Iron Maiden ...");
				
				return WarLight.ACTION_MOVE;
			} 
			else
				return toReturn;
		}
		
	    /*
	     * ETAT ATTENTE d'INSTRUCTION 
	     * => Des lors qu'il recoit l'ordre de rusher, l'agent  WarLight passe en etat rush 
	     */
		static WTask waitForInstruction = new WTask(){
			String exec(WarBrain bc){
				WarLightBrainController me = (WarLightBrainController) bc;
				listenMessages(me); 
				detectedEnnemi(me,WarAgentType.WarBase);
				detectedEnnemi(me,WarAgentType.WarHeavy);
				detectedEnnemi(me,WarAgentType.WarLight);
				detectedEnnemi(me,WarAgentType.WarTurret);
				detectedEnnemi(me,WarAgentType.WarExplorer);
				detectedEnnemi(me,WarAgentType.WarRocketLauncher);
				detectedEnnemi(me,WarAgentType.WarEngineer);
				me.setDebugString("Waiting for instructions");
				for(WarMessage m : me.getMessages()) {
					if(m.getMessage() == "I need help" && m.getDistance() < 150) {
						me.setHeading(CalculTrigo.angleObjMe(m.getDistance(),m.getAngle(),Double.parseDouble(m.getContent()[0]),Double.parseDouble(m.getContent()[1])));
					}
				}
				me.broadcastMessageToAgentType(WarAgentType.WarBase, "Where is the base ?", "");
				if(me.isBlocked()){me.setRandomHeading();}
				return WarLight.ACTION_MOVE;
			}
		};
		
	    /*
	     * ETAT RUSH
	     * => Des lors qu'il detecte une base ennemie, l'agent passe en etat d'attaque 
	     */
		static WTask rushToTheEnnemiBase = new WTask(){
			String exec(WarBrain bc){
				WarLightBrainController me = (WarLightBrainController) bc;
				me.broadcastMessageToAgentType(WarAgentType.WarBase, "Where is the base ?", "");
				for(WarMessage message : me.getMessages())
				{
					String[] list = message.getContent();
					if(message.getMessage().equals("Base here"))
					{
						double Tetac = CalculTrigo.angleObjMe(message.getDistance(), message.getAngle(), Double.parseDouble(list[0]), Double.parseDouble(list[1]));
						me.setDebugString("PIKA .... "/*+Tetac*/);
						me.setHeading(Tetac);
					}
				}
				if(detectedEnnemi(me,WarAgentType.WarTurret)) {
					return ACTION_FIRE;
				}
				if(detectedEnnemi(me,WarAgentType.WarBase)) {
					return ACTION_FIRE;
				}
				if(detectedEnnemi(me,WarAgentType.WarHeavy)) {
					return ACTION_FIRE;
				}
				if(detectedEnnemi(me,WarAgentType.WarLight)) {
					return ACTION_FIRE;
				}
				if(detectedEnnemi(me,WarAgentType.WarExplorer)) {
					return ACTION_FIRE;
				}
				if(detectedEnnemi(me,WarAgentType.WarRocketLauncher)) {
					return ACTION_FIRE;
				}
				if(me.isBlocked())
					me.setRandomHeading();
				return WarHeavy.ACTION_MOVE;
			}
		};
		
		/*
	     * ETAT D'ATTAQUE
	     * => Attaque l'ennemi dans le champ de vision de l'agent WarLight
	     */	
		static WTask attackEnnemiBase = new WTask(){
			String exec(WarBrain bc){
				WarLightBrainController me = (WarLightBrainController) bc;
				detectedEnnemi(me,WarAgentType.WarBase);
				for (WarAgentPercept wp : me.getPerceptsEnemies()) 
				{	if(wp.getType() != WarAgentType.WarFood) 
					{
					me.setDebugString("SHOT !");	
					me.setHeading(wp.getAngle());
		             if (me.isReloaded())
		                 return ACTION_FIRE;
		             else if (me.isReloading())
		                 return ACTION_IDLE;
		             else
		                 return ACTION_RELOAD;
					}
				}
				me.ctask = waitForInstruction;
				return ACTION_IDLE;
			}
		};
	    
		static void listenMessages(WarLightBrainController me) {
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

		static boolean detectedEnnemi(WarLightBrainController me, WarAgentType warAgentType) {
			
			for (WarAgentPercept wp : me.getPerceptsEnemies()) 
			{
	            if (me.isEnemy(wp) && wp.getType().equals(warAgentType))
	            {
	            	me.setDebugStringColor(Color.orange.darker());
	            	if(wp.getType() == WarAgentType.WarBase)
	            	{
	            		me.setDebugString("Detected ennemi base");
	            		me.broadcastMessageToAgentType(WarAgentType.WarBase,"Base here",Double.toString(wp.getDistance()),Double.toString(wp.getAngle()));
		            	me.setTarget(wp);
						me.ctask = attackEnnemiBase;
						return true;
	            	}
	            	else if(wp.getType() == WarAgentType.WarLight || wp.getType() == WarAgentType.WarExplorer || wp.getType() == WarAgentType.WarRocketLauncher)
	            	{
	            		me.setDebugString("Detected ennemi heavy");
	            		me.setTarget(wp);
	            		me.ctask = attackEnnemiBase;
	            		return true;
	            	}else if(wp.getType() == WarAgentType.WarHeavy || wp.getType() == WarAgentType.WarTurret) 
	            	{
	            		me.setDebugString("Detected ennemi heavy");
	            		me.setTarget(wp);
	            		me.ctask = skirtEnnemiInCircle;
	            		return true;
	            	}
	            }
			}
			return false;
		}
		
		static WTask skirtEnnemiInCircle = new WTask(){
			String exec(WarBrain bc){
				WarLightBrainController me = (WarLightBrainController) bc;
				WarAgentPercept cibleToskirt = me.getTarget();
				me.setHeading(CalculTrigo.LogicDegree(me.angleSkirt+cibleToskirt.getAngle()));
				if(me.montee)
				{
					me.angleSkirt-=0.5;
					if(me.angleSkirt==0)
						me.montee=false;
				}
				else	
				{
					me.angleSkirt=90;
					me.montee=true;
					me.ctask= waitForInstruction; 
				}
			return ACTION_MOVE;
			}
		};
		

		public void setWarMessage(WarMessage wm) {
			this.wm= wm;
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