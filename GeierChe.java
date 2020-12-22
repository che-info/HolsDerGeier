package de.rabitem.main.player.instances;

import java.util.ArrayList;

import de.rabitem.main.HolsDerGeierUtil;
import de.rabitem.main.card.instances.PlayerCard;
import de.rabitem.main.player.Player;

public class GeierChe extends Player {
	private ArrayList<PlayerCard> myCards=new ArrayList <PlayerCard>();
	private ArrayList<PlayerCard> enemyCards=new ArrayList <PlayerCard>();
	private ArrayList<PlayerCard> gameCards=new ArrayList <PlayerCard>();
	private static int []enemyStrategy=new int [30];

	private int countRounds;
	private static int countGames;
	private PlayerCard valueNextCard= new PlayerCard(-99);
	
	//Konstruktor definieren --> Default-Kontruktor steht nicht mehr zur Verfügung
	public GeierChe() {
		super("Che"); /*expliziter Aufruf des Konstruktors der Vaterklasse,
		da kein default Konstruktor für einen impliziten Aufruf zur Verfügung steht*/
		System.out.println("Ich werde gewinnen!!");
	}
	
	//überladener Konstruktur
	public GeierChe(String playerinstance) {
		/*Aufruf des Konstruktors der eigenen Klasse ohne Paramter 
		*/
		this();
		System.out.println("The Winner is: "+ playerinstance);// stimmt natürlich nicht, nur eine Standrad-Aussage
	}
	
	@Override
	public void customResets() {
		countRounds=0;
		myCards.clear();
		enemyCards.clear();
		gameCards.clear();
		for (int i=0; i<enemyStrategy.length;i+=2) {
			for (int j=-5; j<11; j++) {
				if (j==0)
					continue;
				enemyStrategy[i]=j;
			}
		}
		
		//Befüllen der Karten Arrays
		for (int i=1; i<16;i++) {
			myCards.add(new PlayerCard(i));
			enemyCards.add(new PlayerCard(i));
		}
		for (int i=-5;i<11;i++) {
			if(i==0)
				continue;
			gameCards.add(new PlayerCard (i));			
		}
	}
	
	
	
	//überschreiben der Methode für die nächste Karte
	@Override 
	public PlayerCard getNextCardFromPlayer(final int pointCardValue) {
		//Rundenanzahl hochzählen
		countRounds++;
		//Anzahl der Spiele hochzählen
		if (countRounds%15==0) {
			countGames++;
		}
		
       
    
	/*im ersten Spiel wird einmalig eine Strategie gespielt,die nicht lernt
	 * danach wird eine Strategie gespielt,die vom Gegner lernt
	 */
      		valueNextCard= (countGames<3)? strategyOne(pointCardValue): strategyDynamic(pointCardValue);

      return valueNextCard ;
    }

	private PlayerCard strategyOne (int pointCard) {
		
		System.out.print(countGames+countRounds+pointCard);
		switch (countRounds) {
		/*case 1:
			if (pointCard>8) {
			valueNextCard=new PlayerCard (5);
			}
			else if (pointCard<0){
			valueNextCard= new PlayerCard(1);
			}
			return valueNextCard;*/
		case 1:
			valueNextCard=new PlayerCard(15);
		case 2:
			PlayerCard enemyCard=getLastEnemyCard();
			//getStrategy(pointCard, enemyCard.getValue());
			//System.out.println(enemyCard.getValue());
			//removeCard(getEnemy(), enemyCard);
			valueNextCard=new PlayerCard(1);
		case 3:
			valueNextCard=new PlayerCard(2);
		case 4:
			valueNextCard=new PlayerCard(3);
		case 5:
			valueNextCard=new PlayerCard(4);
		case 6:
			valueNextCard=new PlayerCard(5);
		case 7:
			valueNextCard=new PlayerCard(6);
		case 8:
			valueNextCard=new PlayerCard(7);
		case 9:
			valueNextCard=new PlayerCard(8);
		case 10:
			valueNextCard=new PlayerCard(9);
		case 11:
			valueNextCard=new PlayerCard(10);
		case 12:
			valueNextCard=new PlayerCard(11);
		case 13:
			valueNextCard=new PlayerCard(12);
		case 14:
			valueNextCard=new PlayerCard(13);
		case 15:
			valueNextCard=new PlayerCard(14);
		default:
			return valueNextCard;
		}
	}
	private PlayerCard strategyDynamic (int pointCard) {
		int index=0;
		for (int i=0; i<enemyStrategy.length;i++) {
			if (enemyStrategy[i]==pointCard) {
				index=i;
				break;
			}
		}
		int enemyValue=enemyStrategy[index+1];
		if (enemyValue==15) {
			valueNextCard=new PlayerCard (1);
		}
		else {
					valueNextCard=new PlayerCard (enemyValue+1);
		}
		System.out.println(countGames+pointCard+valueNextCard.getValue());
		if (myCards.contains(valueNextCard)) {
			return valueNextCard;
		}
		else {
			return strategyOne(pointCard);
		}
		

	}
	private PlayerCard highestEnemyCard() {
		return enemyCards.get(enemyCards.size()-1);
	}
	
	/*private int pointsStillIn() {
		int punkte=0;
		for(PlayerCard p:gameCards) {
			punkte+=p.getValue();
		}
		return punkte;
	}*/
	
	private PlayerCard getLastEnemyCard () {
		return getEnemy().getLastMove();
	}
	
	private Player getEnemy () {
		for (Player p:HolsDerGeierUtil.getActivePlayers()) {
			if (p instanceof GeierChe) {
				
			}
			else
				return p;
		} return null;
	}
	
	private void removeCard(Player p, PlayerCard card) {
		if(p==getEnemy()) {
					enemyCards.remove(card);
		}
		if (p instanceof GeierChe) {
			myCards.remove(card);
		}
	}
	
	/*falls der Gegner immer die gleiche Karte zu einer bestimmten Punkte Karte legt,
	 * wird das erkannt
	 * PunkteKarte und entsprechende Karte, die der Gegner legt, werden hinter einander 
	 * im Strategie-Array gespeichert und so einander zugeordnet
	 */
	private void getStrategy (int pointCardValue, int enemyCard) {
		if (countRounds>1) {
			int index=0;
		for (int i=0; i<enemyStrategy.length;i++) {
			if (enemyStrategy[i]==pointCardValue) {
				index=i;
				break;
			}
		}enemyStrategy[index+1]=enemyCard;
		}
		
		
	}

}
