package de.rabitem.main.player.instances;

import java.util.ArrayList;
import de.rabitem.main.HolsDerGeierUtil;
import de.rabitem.main.card.instances.PlayerCard;
import de.rabitem.main.player.Player;

public class GeierChe_2 extends Player {
	private ArrayList<Integer> myCards = new ArrayList<Integer>();
	private ArrayList<Integer> enemyCards = new ArrayList<Integer>();
	private ArrayList<Integer> gameCards = new ArrayList<Integer>();
	private static ArrayList<ArrayList<Integer>> enemyStrategy = new ArrayList<ArrayList<Integer>>();
	private ArrayList<Integer> Spiele = new ArrayList<Integer>();
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
		/*
		 * ab der zweiten Runde die letzte Karte des gegners zuweisen und aus seinen
		 * Karten entfernen
		 */
		if (countRounds > 1) {
			lastEnemyMove = getLastEnemyCard();
			enemyCards.remove(enemyCards.indexOf(lastEnemyMove));
		}
		if (countGames < 4) {
			getStrategy(pointCardValue, lastEnemyMove);
		}
		/*
		 * im ersten+zweiten Spiel wird eine Strategie,die unabhängig von der Stratgeie
		 * des Gegners ist danach wird entschieden, ob eine auf den gegner angepasste
		 * Strategie gespielt werden kann
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
		/* spezielle Kartenlegetechnik für die erste Runde */
		case 1:
			// ist die PunkteKarte 9 oder 10 lege ich 15
			if (pointCard == 10 || pointCard == 9) {
				card = 14;
			}
			// ist die Punktekarte -4,-5oder 8 lege ich 12
			else if (pointCard == -5 || pointCard == -4 || pointCard > 6) {
				card = 12;
			}
			// ist die PunkteKarte -2,-3,5,6 oder 7 lege ich 11
			else if (pointCard < -1 || pointCard > 4) {
				card = 11;
			}
			// ansonsten lege ich 5
			else {
				card = 5;

			}
			break;
		/* spezielle Kartenlegetechnik für die zweite Runde */
		case 2:
			/*
			 * ist die PunkteKarte 9 oder 10 und hat der Gegner die gleiche höchste Karte
			 * noch wie ich dann lege ich meine zweithöchste Karte wenn nicht, lege ich
			 * meine höchste Karte
			 */
			if (pointCard > 8) {
				if (enemyCards.get(enemyCards.size() - 1) == myCards.get(myCards.size() - 1)) {
					if (myCards.size()>1) {
						card = myCards.get(myCards.size() - 2);
					}
					else {
						card=playMiddleCard();
					}
					
				} else {
					card = myCards.get(myCards.size() - 1);
				}
			}
			/*
			 * ist die PunktKarte -4 oder -5 und der Gegner hat ind er Runde zuvor noch
			 * nicht die 12 gelegt oder ich habe keine 12 mehr dann lege ich meine höchste
			 * Karte hat er die 12 schon gespielt und ich kann die 12 noch spielen dann
			 * spiele ich die 12
			 */
			else if (pointCard < -3) {
				if (enemyCards.contains(12)) {
					card = myCards.get(myCards.size() - 1);
				} else {
					if (myCards.contains(12)) {
						card = 12;
					} else {
						card = myCards.get(myCards.size() - 1);
					}

				}
			}
			/* anosnten spiele ich eine Karte aus der Mitte */
			else {
				card = playMiddleCard();
			}
			break;
		/*
		 * aber der dritten Runde wird immer gleich entschieden, welche Karte zu spielen
		 * ist
		 */
		default:
			if (pointCard == 7 || pointCard == 8 || pointCard<0) {
				if (enemyCards.get(enemyCards.size() - 1) != myCards.get(myCards.size() - 1)) {
					card = myCards.get(myCards.size() - 1);
				} else {
					if (myCards.size()>2) {
						card = myCards.get(myCards.size() - 3);
					}
					else {
						card=playMiddleCard();
					}
					

				}
			} else if (pointCard > 8) {
				card = myCards.get(myCards.size() - 1);
			}

		}
		if (myCards.contains(card)) {
			return card;
		} else {
			return playMiddleCard();
		}

	}

	/* passt meine Karte auf die GegnerKarte an */
	private int strategyDynamic(int pointCard) {
		int card;
		int index = 0;
		int enemyValue;
		for (int i = 0; i < enemyStrategy.get(0).size(); i++) {
			if (enemyStrategy.get(0).get(i) == pointCard) {
				index = i;
				break;
			}
		}
		enemyValue = enemyStrategy.get(0).get(index + 1);
		/*
		 * meine Karte ist immer um 1 höher als die Karte, die der Gegner wahrscheinlich
		 * legt
		 */
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

	/*
	 * durchläuft die aktiven Player und gibt die letzte Karte des Players zurück,
	 * wenn er nicht meine ID hat wenn er also mein Gegner ist
	 */
	private int getLastEnemyCard() {
		for (Player p : HolsDerGeierUtil.getActivePlayers()) {
			if (p.getId() != this.getId()) {
				return p.getLastMove().getValue();
			}
		}
		return -99;

	}

	private int playMiddleCard() {
		return myCards.get(myCards.size()/2);
	}
	/*
	 * falls der Gegner immer die gleiche Karte zu einer bestimmten Punkte Karte
	 * legt, wird das erkannt PunkteKarte und entsprechende Karte, die der Gegner
	 * legt, werden hintereinander in Strategie-Array-List gespeichert und so
	 * einander zugeordnet
	 */
	private void getStrategy(int pointCardValue, int enemyCard) {
		/*
		 * in der ersten Runde wird die erste Dimension der Array-list mit den Karten
		 * des Gegners zu den jeweiligen PunkteKarten hintereinander gefüllt
		 */

		switch (countGames) {
		case 1:
			if (countRounds == 1) {
				enemyStrategy.add(Spiele);
				enemyStrategy.get(0).add(pointCardValue);
			} else {
				enemyStrategy.get(0).add(enemyCard);
				enemyStrategy.get(0).add(pointCardValue);

			}
			break;
		/*
		 * in der ersten Runde wird die erste Dimension der Array-list mit den Karten
		 * des Gegners zu den jeweiligen PunkteKarten hintereinander gefüllt
		 */
		case 2:
			/* zunächst noch hinzufügen der letzten GegnerKarte aus dem ersten Spiel */
			if (countRounds == 1) {
				enemyStrategy.get(0).add(enemyCard);
				enemyStrategy.add(Spiele);
				enemyStrategy.get(1).add(pointCardValue);
				/*
				 * dann befüllen der zweiten Dimesnion der ArrayList mit den Karten des Gegners
				 * zu den jeweiligen PunkteKarten hintereinander
				 */
			} else {
				enemyStrategy.get(1).add(enemyCard);
				enemyStrategy.get(1).add(pointCardValue);

			}
			break;
		case 3:
			if (countRounds == 1) {
				enemyStrategy.get(1).add(enemyCard);
			} else {

			}
			for (int i = 0; i < enemyStrategy.get(0).size(); i += 2) {
			int points = enemyStrategy.get(0).get(i);
			int enemy = enemyStrategy.get(0).get(i + 1);
			for (int j = 0; j < enemyStrategy.get(1).size(); j += 2) {
				if (enemyStrategy.get(1).get(j) == points) {
					if (enemyStrategy.get(1).get(j + 1) != enemy) {
						strategy = false;
					}
				}
			}
		}
		break;
		}

		/*
		 * dann wird überprüft ob die Einträge in der ersten und zweiten Dimension der
		 * List übereinstimmen wenn nein wird sttrategy auf false gesetzt --> der Gegner
		 * legt nicht jede Runde, die gleiche Karte auf eine PunkteKarte --> ich kann
		 * die Strategie nicht lernen
		 */
		
	}
}
