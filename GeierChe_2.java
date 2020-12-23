package de.rabitem.main.player.instances;

import java.util.ArrayList;
import de.rabitem.main.HolsDerGeierUtil;
import de.rabitem.main.card.instances.PlayerCard;
import de.rabitem.main.player.Player;

public class GeierChe_2 extends Player {
	private ArrayList<Integer> myCards = new ArrayList<Integer>();
	private ArrayList<Integer> enemyCards = new ArrayList<Integer>();
	private ArrayList<Integer> gameCards = new ArrayList<Integer>();
	private static ArrayList<Integer> enemyStrategy_1 = new ArrayList<Integer>();
	private static ArrayList<Integer> enemyStrategy_2 = new ArrayList<Integer>();
	private int lastEnemyMove = -99;

	private int countRounds;
	private static int countGames;
	private int valueNextCard = -99;
	boolean strategy = true;

	// Konstruktor definieren --> Default-Kontruktor steht nicht mehr zur Verfügung
	public GeierChe_2() {
		super("Che"); /*
						 * expliziter Aufruf des Konstruktors der Vaterklasse, da kein default
						 * Konstruktor für einen impliziten Aufruf zur Verfügung steht
						 */
		customResets();
		System.out.println("Ich werde gewinnen!!");
	}

	// überladener Konstruktur
	public GeierChe_2(String playerinstance) {
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
		/*ab der zweiten Runde die letzte Karte des gegners zuweisen und 
		 *  aus seinen Karten entfernen
		 */
		if (countRounds > 1) {
			lastEnemyMove = getLastEnemyCard();
			enemyCards.remove(enemyCards.indexOf(lastEnemyMove));
		}
		getStrategy(pointCardValue, lastEnemyMove);

		/*
		 * im ersten+zweiten Spiel wird eine Strategie,die unabhängig von der Stratgeie
		 * des Gegners ist danach wird entschieden, ob eine auf den gegner angepasste
		 * Strategie gespeilt werden kann
		 */
		valueNextCard = (countGames < 3) ? strategyOne(pointCardValue)
				: ((strategy) ? strategyDynamic(pointCardValue) : strategyOne(pointCardValue));
		// gespielte karte aus meinen Karten entfernen
		myCards.remove(myCards.indexOf(valueNextCard));
		// Punkte Karten aus den Stapel Karten entfernen
		gameCards.remove(gameCards.indexOf(pointCardValue));

		System.out.println("Games:" + countGames + "Rounds" + countRounds + myCards.size() + " " + gameCards.size()
				+ " " + enemyCards.size());
		System.out.println(strategy);
		System.out.println("Punkte: " + pointCardValue + "Meine " + valueNextCard + " LetzteGegner" + lastEnemyMove);
		return new PlayerCard(valueNextCard);
	}

	private int strategyOne(int pointCard) {
		int card = 0;
		// System.out.println(countGames + " " + countRounds + "." + pointCard);
		switch (countRounds) {
		/* spiezielle Kartenlegetechnik für die erste Runde */
		case 1:
			if (pointCard == 10 || pointCard == 9) {
				card = 15;
			} else if (pointCard == -5 || pointCard == -4 || pointCard > 7) {
				card = 12;
			} else if (pointCard < -1 || pointCard > 4) {
				card = 11;
			} else {
				card = 5;
			}
			/* spezielle Kartenlegetechnik für die zweite Runde */
		case 2:
			if (pointCard > 13) {
				if (enemyCards.get(enemyCards.size() - 1) == myCards.get(myCards.size() - 1)) {
					card = 10;
				} else if (pointCard < -3) {
					card = myCards.get(myCards.size() - 1);
				}

			}
			/*
			 * aber der dritten Runde wird immer gleich entschieden, welche Karte zu spielen
			 * ist
			 */
		default:
			if (myCards.contains(card)) {
				return card;
			} else {
				return myCards.get(myCards.size() / 2);
			}

		}
	}

	private int strategyDynamic(int pointCard) {
		int card;
		int index = 0;
		int enemyValue;
		for (int i = 0; i < enemyStrategy_1.size(); i++) {
			if (enemyStrategy_1.get(i) == pointCard) {
				index = i;
				break;
			}
		}
		if (index == enemyStrategy_1.size() - 1) {
			enemyValue = 15;
		} else {
			enemyValue = enemyStrategy_1.get(index + 1);
		}

		if (enemyValue == 15) {
			card = 1;
		} else {
			card = ++enemyValue;
		}
		if (myCards.contains(card)) {
			return card;
		} else {
			return strategyOne(pointCard);
		}

	}

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
	private void getStrategy(int pointCardValue, int enemyCard) {
		/*
		 * in der ersten Runde wird die erste Array-list mit den Karten des Gegners zu
		 * den jeweiligen PunkteKarten hintereinander gefüllt
		 */
		if (countGames == 1) {
			enemyStrategy_1.add(enemyCard);
			enemyStrategy_1.add(pointCardValue);
		}
		
		else if (countGames == 2 & countRounds == 1) {
			enemyStrategy_1.add(enemyCard);
		} /*
		 * in der zweiten Runde wird die zweite Array-list mit den Karten des Gegners zu
		 * den jeweiligen PunkteKarten hintereinander gefüllt
		 */else if (countGames == 2 & countRounds > 1) {
			enemyStrategy_2.add(enemyCard);
			enemyStrategy_2.add(pointCardValue);
		}

		else if (countGames == 3 & countRounds == 1) {
			enemyStrategy_2.add(enemyCard);
			/*
			 * dann wird überprüft ob die Einträge in den beiden Array-lists übereinstimmen
			 * wenn nein wird fale ausgegebn --> der Gegner legt nicht jede Runde, die
			 * gleiche Karte auf eine PunkteKarte --> ich kann die Strategie nicht lernen
			 */
			for (int i = 1; i < enemyStrategy_1.size(); i += 2) {
				int points = enemyStrategy_1.get(i);
				int enemy = enemyStrategy_1.get(i + 1);
				for (int j = 1; j < enemyStrategy_2.size(); j += 2) {
					if (enemyStrategy_2.get(j) == points) {
						if (enemyStrategy_2.get(j + 1) != enemy) {
							strategy = false;
							break;
						}
					}
				}
			}
		}
	}

}
