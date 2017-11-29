package myteam;

import java.util.ArrayList;

import edu.warbot.agents.agents.WarExplorer;
import edu.warbot.agents.enums.WarAgentType;
import edu.warbot.agents.percepts.WarAgentPercept;
import edu.warbot.agents.resources.WarFood;
import edu.warbot.brains.WarBrain;
import edu.warbot.brains.brains.WarExplorerBrain;
import edu.warbot.communications.WarMessage;


/*
 * Notes : 
 * 
 * il faut s'inscrire avant
 * 
 * Le jar doit avoir le même nom que l'équipe
 * 
 * Un max de commentaire BIEN ECRITS ET UTILES
 * 
 * A rendre : sources + jar
 * 
 * Conditions initiales : 
 * 
 * 
 */



public abstract class WarExplorerBrainController extends WarExplorerBrain  {

	public WarExplorerBrainController() {
        super();
  

    }

    @Override
    public String action() {
    	ArrayList<WarAgentPercept> percepts = (ArrayList<WarAgentPercept>) getPercepts();
    	for ( WarAgentPercept agtPcpt : percepts)
    	{
    		switch(agtPcpt.getType()) {
    			case WarFood:
    				if(!this.isBagFull())
    				{
    					setDebugString("MAIM");
    					setHeading(agtPcpt.getAngle());
    					if(agtPcpt.getDistance()<WarFood.MAX_DISTANCE_TAKE)	
    						return take();
    				}
			default:
				break;
    		}
    	}
		if (this.isBagFull()) // retour � la base si le sac est plein
		{
			setDebugString("Bag full : je cherche ma maison");
			percepts = null;
			percepts = (ArrayList<WarAgentPercept>) getPerceptsAlliesByType(WarAgentType.WarBase);
			WarAgentPercept base = null;
			
			if((percepts.size()==0) || (percepts==null)) // on ne trouve pas la base
			{
				this.broadcastMessageToAgentType(WarAgentType.WarBase, "whereAreYou", ""); // on demande o� elle se trouve
				
				ArrayList<WarMessage> wm = (ArrayList<WarMessage>) this.getMessages();
				for (WarMessage m : wm)
				{
					if(m.getSenderType() == WarAgentType.WarBase)
					{
						setDebugString("OMG ma base! J'ARRIVE !");
						setHeading(m.getAngle());
					}
				}
			}
			else  // on a trouv� la base
			{
				base = percepts.get(0);
				if(base.getDistance() > WarBrain.MAX_DISTANCE_GIVE)
				{
					setDebugString("La base est dans mon rayon de perception... ");
	                setHeading(base.getAngle());
	                return WarExplorer.ACTION_MOVE;
				}
	            else {
	            	setDebugString("Et bim! Don de nourriture à la base");
	            	setIdNextAgentToGive(base.getID());  
	            	return WarExplorer.ACTION_GIVE;
	            }	            	
			}
		}
		else if (!this.isBagFull())
			setDebugString("Cherche miam (:");
    	
    	if (isBlocked()) // Obstacle imprévu
    		setRandomHeading();
    	
    	 for (WarAgentPercept w : getPerceptsEnemies()) 
    	 {
    		 if (w.getType().equals(WarAgentType.WarBase))
    				 {
    			 		double Xa = w.getDistance()*Math.cos(Math.toRadians(w.getAngle()));
    			 		double Ya = w.getDistance()*Math.sin(Math.toRadians(w.getAngle()));
    			 		this.broadcastMessageToAgentType(WarAgentType.WarLight, "goThere", String.valueOf(Xa), String.valueOf(Ya));
    			 		setDebugString("Angle cible : "+w.getAngle());
    			 		return WarExplorer.ACTION_IDLE;
    				 }
    		 if (w.getType().equals(WarAgentType.WarTurret))
			 {
		 		double Xa = w.getDistance()*Math.cos(Math.toRadians(w.getAngle()));
		 		double Ya = w.getDistance()*Math.sin(Math.toRadians(w.getAngle()));
		 		this.broadcastMessageToAgentType(WarAgentType.WarLight, "goThere", String.valueOf(Xa), String.valueOf(Ya));
		 		setDebugString("Angle cible : "+w.getAngle());
		 		setHeading(180);
		 		return WarExplorer.ACTION_MOVE;
			 }
    	 }
    	 
    	
        return WarExplorer.ACTION_MOVE;
    }

}
