package pikashot;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.jfree.data.statistics.MeanAndStandardDeviation;

import edu.warbot.agents.agents.WarExplorer;
import edu.warbot.agents.agents.WarHeavy;
import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.agents.percepts.WarAgentPercept;
import edu.warbot.agents.resources.WarFood;
import edu.warbot.brains.WarBrain;
import edu.warbot.brains.brains.WarHeavyBrain;
import edu.warbot.communications.WarMessage;

@SuppressWarnings("unused")
public abstract class WarHeavyBrainController extends  WarHeavyBrain {
		
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
			
	    public WarHeavyBrainController() {
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
				setDebugString("Heavy waitinggg ...");
				
				return WarHeavy.ACTION_MOVE;
			} 
			else
				return toReturn;
		}
		
	    /*
	     * ETAT ATTENTE d'INSTRUCTION 
	     * => Des lors qu'il recoit l'ordre de rusher, l'agent  warheavy passe en etat rush 
	     */
		static WTask waitForInstruction = new WTask(){
			String exec(WarBrain bc){
				WarHeavyBrainController me = (WarHeavyBrainController) bc;
				listenMessages(me); 
				detectedEnnemi(me,WarAgentType.WarBase);
				detectedEnnemi(me,WarAgentType.WarHeavy);
				me.setDebugString("Waiting for instructions");
				if(me.isBlocked()){me.setRandomHeading();}
				return WarHeavy.ACTION_MOVE;
			}
		};
		
	    /*
	     * ETAT RUSH
	     * => Des lors qu'il detecte une base ennemie, l'agent passe en etat d'attaque 
	     */
		static WTask rushToTheEnnemiBase = new WTask(){
			String exec(WarBrain bc){
				WarHeavyBrainController me = (WarHeavyBrainController) bc;
				WarMessage message = me.getWarMessage();
				String[] list = message.getContent();
				me.broadcastMessageToAgentType(WarAgentType.WarBase, "Where is the base ?", "");
				if(message.getMessage().equals("goThere"))
				{
					double Tetac = CalculTrigo.angleObjMe(message.getDistance(), message.getAngle(), Double.parseDouble(list[0]), Double.parseDouble(list[1]));
					me.setDebugString("PIKA .... "/*+Tetac*/);
					me.setHeading(Tetac);
				}
				if(detectedEnnemi(me,WarAgentType.WarTurret)) {
					return ACTION_FIRE;
				}
				if(detectedEnnemi(me,WarAgentType.WarBase)) {
					return ACTION_FIRE;
				}
				listenMessages(me);
				if(me.isBlocked())
					me.setRandomHeading();
				return WarHeavy.ACTION_MOVE;
			}
		};
		
		/*
	     * ETAT D'ATTAQUE
	     * => Attaque l'ennemi dans le champ de vision de l'agent warheavy
	     */	
		static WTask attackEnnemiBase = new WTask(){
			String exec(WarBrain bc){
				WarHeavyBrainController me = (WarHeavyBrainController) bc;
				detectedEnnemi(me,WarAgentType.WarBase);
				if(me.getTarget()!=null)
				{
					me.setDebugString("SHOT !");	
					me.setHeading(me.getTarget().getAngle());
		             if (me.isReloaded())
		                 return ACTION_FIRE;
		             else if (me.isReloading())
		                 return ACTION_IDLE;
		             else
		                 return ACTION_RELOAD;
				}
				else
				{
					me.ctask = waitForInstruction;
				}
				return ACTION_IDLE;
			}
		};
	    
		static void listenMessages(WarHeavyBrainController me) {
			List<WarMessage> messages = me.getMessages();
			for (WarMessage message : messages) 
			{
				if (message.getMessage().equals("goThere"))
				{
					me.setWarMessage(message);
					me.ctask = rushToTheEnnemiBase;
				}
			}
		}

		static boolean detectedEnnemi(WarHeavyBrainController me, WarAgentType warAgentType) {
			
			for (WarAgentPercept wp : me.getPerceptsEnemies()) 
			{
	            if (me.isEnemy(wp) && wp.getType().equals(warAgentType))
	            {
	            	me.setDebugStringColor(Color.orange.darker());
	            	if(wp.getType() == WarAgentType.WarBase || wp.getType() == WarAgentType.WarBase)
	            	{
	            		me.setDebugString("Detected ennemi base");
		            	me.setTarget(wp);
						me.ctask = attackEnnemiBase;
						return true;
	            	}
	            	else if(wp.getType() == WarAgentType.WarHeavy || wp.getType() == WarAgentType.WarLight)
	            	{
	            		me.setDebugString("Detected ennemi heavy");
	            		me.setTarget(wp);
	            		me.ctask = attackEnnemiBase;
	            		return true;
	            	}
	            }
			}
			return false;
		}
		
		static WTask skirtEnnemiInCircle = new WTask(){
			String exec(WarBrain bc){
				WarHeavyBrainController me = (WarHeavyBrainController) bc;
				WarAgentPercept cibleToskirt = me.getTarget();
				me.setHeading(CalculTrigo.LogicDegree(me.angleSkirt+cibleToskirt.getAngle()));
				if(me.montee)
				{
					me.angleSkirt--;
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
		
		public static void skirt(WarHeavyBrainController me, WarAgentPercept wp) {
			wp.getDistance();
			me.setHeading(wp.getAngle());
			me.ctask = attackEnnemiBase;
		}

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