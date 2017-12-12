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
import edu.warbot.brains.brains.WarHeavyBrain;
import edu.warbot.brains.brains.WarLightBrain;
import edu.warbot.communications.WarMessage;

@SuppressWarnings("unused")
public abstract class WarHeavyBrainController extends  WarHeavyBrain {
		
		private WarMessage wm = null;
		private WarAgentPercept target = null;
		WTask ctask;
		//private boolean m_etatRush =false;
		static WTask handleMsgs = new WTask() { 
			String exec(WarBrain bc){
				return "";
			} 
		};
			
	    public WarHeavyBrainController() {
	        super();
	        //ctask = waitForInstruction; 
	    }
	    
		@Override
		public String action() {
		//	String toReturn = ctask.exec(this);   // le run de la FSM
			if(null == null){
				if (isBlocked())
					setRandomHeading();
				setDebugString("OKTAMER");
				return WarHeavy.ACTION_MOVE;
			} else {
				return  WarHeavy.ACTION_IDLE;
			}
		}
//		
//	    /*
//	     * ETAT ATTENTE d'INSTRUCTION 
//	     * => Des lors qu'il recoit l'ordre de rusher, l'agent  warlight passe en etat rush 
//	     */
//		static WTask waitForInstruction = new WTask(){
//			String exec(WarBrain bc){
//				WarLightBrainController me = (WarLightBrainController) bc;
//				listenMessages(me); 
//				me.setDebugString("Waiting for instructions");
//				return WarLight.ACTION_IDLE;
//			}
//		};
//		
//	    /*
//	     * ETAT RUSH
//	     * => Des lors qu'il detecte une base ennemie, l'agent passe en etat d'attaque 
//	     */
//		static WTask rushToTheEnnemiBase = new WTask(){
//			String exec(WarBrain bc){
//				WarLightBrainController me = (WarLightBrainController) bc;
//				WarMessage message = me.getWarMessage();
//				String[] list = message.getContent();
//				if(message.getMessage().equals("goThere"))
//				{
//					//double previousangle = me.getHeading();
//					double Tetac = CalculTrigo.angleObjMe(message.getDistance(), message.getAngle(), Double.parseDouble(list[0]), Double.parseDouble(list[1]));
//					//double logic = CalculTrigo.LogicDegree(Tetac);
//					me.setDebugString("Etat rush - Angle cible : "+Tetac);
//					me.setHeading(Tetac);
//				}
//				detectedEnnemi(me,WarAgentType.WarBase);
//				listenMessages(me);
//				return WarLight.ACTION_MOVE;
//			}
//		};
//		
//		/*
//	     * ETAT D'ATTAQUE
//	     * => Attaque l'ennemi dans le champ de vision de l'agent warlight
//	     */	
//		static WTask attackEnnemiBase = new WTask(){
//			String exec(WarBrain bc){
//				WarLightBrainController me = (WarLightBrainController) bc;
//				detectedEnnemi(me,WarAgentType.WarBase);
//				if(me.getTarget()!=null)
//				{
//					me.setDebugString("FIRE !");	
//					me.setHeading(me.getTarget().getAngle());
//		             if (me.isReloaded())
//		                 return ACTION_FIRE;
//		             else if (me.isReloading())
//		                 return ACTION_IDLE;
//		             else
//		                 return ACTION_RELOAD;
//				}
//				else
//				{
//					me.ctask = waitForInstruction;
//				}
//				return ACTION_IDLE;
//			}
//		};
//	    
//		static void listenMessages(WarLightBrainController me) {
//			List<WarMessage> messages = me.getMessages();
//			for (WarMessage message : messages) 
//			{
//				if (message.getMessage().equals("goThere"))
//				{
//					me.setWarMessage(message);
//					me.ctask = rushToTheEnnemiBase;
//				}
//			}
//		}
//
//		static void detectedEnnemi(WarLightBrainController me, WarAgentType warAgentType) {
//			
//			for (WarAgentPercept wp : me.getPerceptsEnemies()) 
//			{
//	            if (me.isEnemy(wp) && wp.getType().equals(warAgentType))
//	            {
//	            	System.out.println("detected a base!");
//	            	me.setTarget(wp);
//					me.ctask = attackEnnemiBase;
//	            }
//			}
//		}
//
//		public void setWarMessage(WarMessage wm) {
//			this.wm= wm;
//		}
//		
//		public WarMessage getWarMessage() {
//			return wm;
//		}
//
//		public WarAgentPercept getTarget() {
//			return target;
//		}
//
//		public void setTarget(WarAgentPercept wb) {
//			this.target = wb;
//		}
}