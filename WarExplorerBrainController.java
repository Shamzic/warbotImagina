package pikashot;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import edu.warbot.agents.MovableWarAgent;
import edu.warbot.agents.WarResource;
import edu.warbot.agents.agents.WarExplorer;
import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.agents.percepts.WarAgentPercept;
import edu.warbot.agents.resources.WarFood;
import edu.warbot.brains.WarBrain;
import edu.warbot.brains.brains.WarExplorerBrain;
import edu.warbot.communications.WarMessage;
import myteam.CalculTrigo;


/*
 * Notes : 
 * 
 * Il faut s'inscrire avant
 * Le jar doit avoir le même nom que l'équipe
 * Un max de commentaire BIEN ECRITS ET UTILES
 * A rendre : sources + jar
 * 
 * Comportements à implémenter :
 * - Récupérer liste de bases ennemies
 * - Explorateur esquive tous les agents ennemis autres que explorateur
 * - 
 * 
 */

@SuppressWarnings("unused")
public abstract class WarExplorerBrainController extends WarExplorerBrain  {

	
	WTask ctask;
	private WarMessage wm = null;
	private WarAgentPercept target = null;
	private int angleSkirt;
	private boolean montee;
	private WTask previousState =  new WTask(){ 
		String exec(WarBrain bc){
			return "";
		}
	};

	static WTask handleMsgs = new WTask(){ 
		String exec(WarBrain bc){
			return "";
		}
	};


	public WarExplorerBrainController() {
		super();
        this.angleSkirt = 90;
        this.montee=true;
		ctask = searchFoodTask; // initialisation de la FSM
	}

	@Override
	public String action() {
		String toReturn = ctask.exec(this);   // le run de la FSM
		if(toReturn == null)
		{
			if (isBlocked())
				setRandomHeading();
			return WarExplorer.ACTION_MOVE;
		} 
		else
			return toReturn;
	}

	static WTask returnFoodTask = new WTask(){
		String exec(WarBrain bc){
			WarExplorerBrainController me = (WarExplorerBrainController) bc;
			if(me.isBagEmpty()){
				me.setHeading(me.getHeading() + 180); // Once the food is given, let's go back searching food !
				me.ctask = searchFoodTask;
				return(null);
			}

			me.setDebugStringColor(Color.green.darker());
			me.setDebugString("Returning Food");

			if(me.isBlocked())
				me.setRandomHeading();

			ArrayList<WarAgentPercept> basePercepts = (ArrayList<WarAgentPercept>) me.getPerceptsAlliesByType(WarAgentType.WarBase);

			//Si je ne vois pas de base
			if(basePercepts == null | basePercepts.size() == 0)
			{
				//j'envoie un message aux bases alli�es pour savoir ou elles sont..
				me.setDebugString("Where are you?");
				me.broadcastMessageToAgentType(WarAgentType.WarBase, "Where are you?", "");
				
				WarMessage m = me.getMessageFromBase();
				//Si j'ai un message de la base je vais vers elle
				if(m != null)
				{
					me.setDebugString("direction vers la base");
					me.setHeading(m.getAngle());
				}

				return(MovableWarAgent.ACTION_MOVE);

			}else{//si je vois une base alli�e
				WarAgentPercept base = basePercepts.get(0);

				if(base.getDistance() > MovableWarAgent.MAX_DISTANCE_GIVE){
					me.setHeading(base.getAngle());
					return(MovableWarAgent.ACTION_MOVE);
				}
				else{
					me.setIdNextAgentToGive(base.getID());
					return(MovableWarAgent.ACTION_GIVE);
				}
			}
		}
	};

	static WTask searchFoodTask = new WTask(){
		String exec(WarBrain bc){
			WarExplorerBrainController me = (WarExplorerBrainController) bc;
			detectedEnnemi(me);
			if(me.isBagFull())
			{
				me.ctask = returnFoodTask;
				return(null);
			}

			if(me.isBlocked())
				me.setRandomHeading();
			/* Ajout 12/12/17 */
			
			ArrayList<WarAgentPercept> basePercepts = (ArrayList<WarAgentPercept>) me.getPerceptsAlliesByType(WarAgentType.WarBase);
			if(basePercepts != null && basePercepts.size() !=0) {
				me.broadcastMessageToAgentType(WarAgentType.WarBase, "Where is the food ?", "");
			}
			
			WarMessage message = me.getMessageAboutFood();
			if(message != null) {
				String[] list = message.getContent();
				double Tetac = CalculTrigo.angleObjMe(message.getDistance(), message.getAngle(), Double.parseDouble(list[0]), Double.parseDouble(list[1]));
				me.setHeading(Tetac);
			}
			
			/* fin ajout  */

			me.setDebugStringColor(Color.BLACK);
			me.setDebugString("Searching food ... :( ");


			ArrayList<WarAgentPercept> percepts = (ArrayList<WarAgentPercept>) me.getPercepts();

			for ( WarAgentPercept agtPcpt : percepts)
			{
				switch(agtPcpt.getType()) {
				case WarFood:
					if(!me.isBagFull())
					{
						me.setDebugString("MIAM");
						me.setHeading(agtPcpt.getAngle());
						if(agtPcpt.getDistance()<WarFood.MAX_DISTANCE_TAKE)	
							return WarExplorer.ACTION_TAKE;
						else
							return WarExplorer.ACTION_MOVE;
					}

				default:
					break;
				}
			}
			return WarExplorer.ACTION_MOVE;
		}
	};


	static WTask runnawayTask = new WTask(){
		String exec(WarBrain bc){
			WarExplorerBrainController me = (WarExplorerBrainController) bc;
			me.setDebugStringColor(Color.PINK);
			me.setDebugString("Mamamia this is an ennemi !!!");
			me.setHeading(180+me.getHeading()); // strategic retreat !!!
			me.ctask = searchFoodTask;
			return null;
		}
	};
	
	
	static boolean detectedEnnemi(WarExplorerBrainController me) {
		me.setDebugString("fucking ennemi detected");	
		
		for (WarAgentPercept w :  me.getPerceptsEnemies()) 
		{
			if (w.getType().equals(WarAgentType.WarBase))
			{
				me.broadcastMessageToAgentType(WarAgentType.WarBase, "Base here",String.valueOf(w.getDistance()), String.valueOf(w.getAngle()));
				me.setDebugStringColor(Color.RED);
				me.setDebugString("BASE FOUNDED ! Target angle : "+w.getAngle());
				return true;
			}
			if (w.getType().equals(WarAgentType.WarLight) || w.getType().equals(WarAgentType.WarTurret) || w.getType().equals(WarAgentType.WarHeavy))
			{
				me.setTarget(w);
				me.setDebugString("skirt1");
				me.ctask= skirtEnnemiInCircle; 
			}
		}
		return false;
	}
	
	static WTask skirtEnnemiInCircle = new WTask(){
		String exec(WarBrain bc){
			WarExplorerBrainController me = (WarExplorerBrainController) bc;
			me.setDebugString("skirt");	
			WarAgentPercept cibleToskirt = me.getTarget();
			me.setHeading(CalculTrigo.LogicDegree(me.angleSkirt+cibleToskirt.getAngle()));
			me.setDebugString("angle : "+CalculTrigo.LogicDegree(me.angleSkirt+cibleToskirt.getAngle()));	
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
				me.ctask= searchFoodTask; 
			}
		return ACTION_MOVE;
		}
	};	

	private WarMessage getMessageAboutFood() {
		for (WarMessage m : getMessages()) {
			if(m.getMessage().equals("Food here"))
				return m;
		}
		return null;
	}

	private WarMessage getMessageFromBase() {
		for (WarMessage m : getMessages()) {
			if(m.getSenderType().equals(WarAgentType.WarBase))
				return m;
		}

		broadcastMessageToAgentType(WarAgentType.WarBase, "Where are you?", "");
		return null;
	}
	

	public void setTarget(WarAgentPercept wb) {
		this.target = wb;
	}
	public WarAgentPercept getTarget() {
		return target;
	}
}
