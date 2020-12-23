package de.rabitem.main.player.instances;

import java.util.ArrayList;
import de.rabitem.main.HolsDerGeierUtil;
import de.rabitem.main.card.instances.PlayerCard;
import de.rabitem.main.card.instances.PointsCard;
import de.rabitem.main.player.Player;

public class GeierChe extends Player {
	private ArrayList<Integer> myCards = new ArrayList<Integer>();
	private ArrayList<Integer> enemyCards = new ArrayList<Integer>();
	private ArrayList<Integer> gameCards = new ArrayList<Integer>();
	private static int[] enemyStrategy = new int[30];
	private int lastEnemyMove=-99;

	private int countRounds;
	private static int countGames;
	private int valueNextCard = -99;
	boolean strategy;

	// Konstruktor definieren --> Default-Kontruktor steht nicht mehr zur Verfügung
	public GeierChe() {
		super("Che"); /*
						 * expliziter Aufruf des Konstruktors der Vaterklasse, da kein default
						 * Konstruktor für einen impliziten Aufruf zur Verfügung steht
						 */
		System.out.println("Ich werde gewinnen!!");
	}

	// überladener Konstruktur
	public GeierChe(String playerinstance) {
		/*
		 * Aufruf des Konstruktors der eigenen Klasse ohne Paramter
		 */
		this();
		System.out.println("The Winner is: " + playerinstance);// stimmt natürlich nicht, nur eine Standrad-Aussage
	}

	@Override
	public void customResets() {
		countRounds = 0;
		myCards.clear();
		enemyCards.clear();
		gameCards.clear();
		for (int i = 0; i < enemyStrategy.length; i += 2) {
			for (int j = -5; j < 11; j++) {
				if (j == 0)
					continue;
				enemyStrategy[i] = j;
			}
		}

		// Befüllen der Karten Arrays
		for (int i = 1; i < 16; i++) {
			myCards.add(i);
			enemyCards.add(i);
		}
		for (int i = -5; i < 11; i++) {
			if (i == 0)
				continue;
			gameCards.add(i);
		}
	}

	// überschreiben der Methode für die nächste Karte
	@Override
	public PlayerCard getNextCardFromPlayer(final int pointCardValue) {
		// Rundenanzahl hochzählen
		countRounds++;
		// Anzahl der Spiele hochzählen
		if (countRounds % 15 == 1) {
			countGames++;
		}
		lastEnemyMove=getLastEnemyCard();
		if(lastEnemyMove!=-99) {
			strategy = getStrategy(pointCardValue, lastEnemyMove);
		}

		/*
		 * im ersten+zweiten Spiel wird eine Strategie,die unabhängig von der Stratgeie
		 * des Gegners ist danach wird entschieden, ob eine auf den gegner angepasste
		 * Strategie gespeilt werden kann
		 */
		valueNextCard = (countGames < 3) ? strategyOne(pointCardValue)
				: ((strategy) ? strategyDynamic(pointCardValue) : strategyOne(pointCardValue));
		myCards.remove(myCards.indexOf(valueNextCard));
		gameCards.remove(pointCardValue);
		enemyCards.remove(enemyCards.indexOf(lastEnemyMove));
		return new PlayerCard(valueNextCard);
	}

	private int strategyOne(int pointCard) {

		System.out.println(countGames + " " + countRounds + "." + pointCard);
		switch (countRounds) {
		/*
		 * case 1: if (pointCard>8) { valueNextCard=new PlayerCard (5); } else if
		 * (pointCard<0){ valueNextCard= new PlayerCard(1); } return valueNextCard;
		 */
		case 1:
			if (pointCard == 10 || pointCard == 9) {
				valueNextCard = 15;
			}

		case 2:
			default:
			return valueNextCard;
		}
	}

	private int strategyDynamic(int pointCard) {
		int index = 0;
		for (int i = 0; i < enemyStrategy.length; i++) {
			if (enemyStrategy[i] == pointCard) {
				index = i;
				break;
			}
		}
		int enemyValue = enemyStrategy[index + 1];
		if (enemyValue == 15) {
			valueNextCard = 1;
		} else {
			valueNextCard = ++enemyValue;
		}
		System.out.println(countGames + pointCard + valueNextCard);
		if (myCards.contains(valueNextCard)) {
			return valueNextCard;
		} else {
			return strategyOne(pointCard);
		}

	}

	private int highestEnemyCard() {
		return enemyCards.get(enemyCards.size() - 1);
	}

	/*
	 * private int pointsStillIn() { int punkte=0; for(PlayerCard p:gameCards) {
	 * punkte+=p.getValue(); } return punkte; }
	 */


	private int getLastEnemyCard() {
		for (Player p : HolsDerGeierUtil.getActivePlayers()) {
			if (p.getId() != this.getId()) {
				return p.getLastMove().getValue();
			}
		}
		return -99;
			
	}


	/*
	 * falls der Gegner immer die gleiche Karte zu einer bestimmten Punkte Karte
	 * legt, wird das erkannt PunkteKarte und entsprechende Karte, die der Gegner
	 * legt, werden hinter einander im Strategie-Array gespeichert und so einander
	 * zugeordnet
	 */
	private boolean getStrategy(int pointCardValue, int enemyCard) {
		int index = 0;
		for (int i = 0; i < enemyStrategy.length; i++) {
			if (enemyStrategy[i] == pointCardValue) {
				index = i;
				break;
			}
			if (countGames == 1 && countRounds > 1) {
				enemyStrategy[index + 1] = enemyCard;
			}
			if (countGames == 2 && countRounds > 1) {
				if (enemyStrategy[index + 1] != enemyCard) {
					return false;
				}

			}
		}
		return true;

	}

}
