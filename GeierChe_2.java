package de.rabitem.main.player.instances;

import java.util.ArrayList;
import de.rabitem.main.HolsDerGeierUtil;
import de.rabitem.main.card.instances.PlayerCard;
import de.rabitem.main.player.Player;

public class GeierChe_2 extends Player {
	private ArrayList<Integer> myCards = new ArrayList<Integer>();
	private ArrayList<Integer> enemyCards = new ArrayList<Integer>();
	private ArrayList<Integer> gameCards = new ArrayList<Integer>();
	private ArrayList<ArrayList<Integer>> enemyStrategy = new ArrayList<ArrayList<Integer>>();
	private ArrayList<Integer> Spiele = new ArrayList<Integer>();
	private ArrayList<Integer> Spiele2 = new ArrayList<Integer>();
	private int lastEnemyMove = -99;

	private int countRounds;
	private int countGames = 0;
	private int valueNextCard = -99;
	boolean strategy = true;

	// Konstruktor definieren --> Default-Kontruktor steht nicht mehr zur Verf�gung
	public GeierChe_2() {
		super("Che"); /*
						 * expliziter Aufruf des Konstruktors der Vaterklasse, da kein default
						 * Konstruktor f�r einen impliziten Aufruf zur Verf�gung steht
						 */
		customResets();
		System.out.println("Ich werde gewinnen!!");
	}

	// �berladener Konstruktur
	public GeierChe_2(String playerinstance) {
		/*
		 * Aufruf des Konstruktors der eigenen Klasse ohne Paramter
		 */
		this();
		System.out.println("The Winner is: " + playerinstance);// stimmt nat�rlich nicht, nur eine Standrad-Aussage
	}

	@Override
	public void customResets() {
		// zur�cksetzen aller Nicht-statischen Attribute
		countRounds = 0;
		myCards.clear();
		enemyCards.clear();
		gameCards.clear();

		// Bef�llen der Karten Arrays
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

	// �berschreiben der Methode f�r die n�chste Karte
	@Override
	public PlayerCard getNextCardFromPlayer(final int pointCardValue) {
		// Rundenanzahl hochz�hlen

		countRounds++;
		// Anzahl der Spiele hochz�hlen
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
		// in den ersten 3 Spielen wird versucht die Strategie des Gegners
		// herauszufinden
		if (countGames < 4) {
			getStrategy(pointCardValue, lastEnemyMove);
		}
		/*
		 * im ersten+zweiten Spiel wird eine Strategie,die unabh�ngig von der Stratgeie
		 * des Gegners ist gespielt, danach wird entschieden, ob eine auf den gegner
		 * angepasste Strategie gespielt werden kann
		 */
		valueNextCard = (countGames < 3) ? strategyOne(pointCardValue)
				: ((strategy) ? strategyDynamic(pointCardValue) : strategyOne(pointCardValue));

		// gespielte karte aus meinen Karten entfernen
		myCards.remove(myCards.indexOf(valueNextCard));
		// Punkte Karten aus den Stapel Karten entfernen
		gameCards.remove(gameCards.indexOf(pointCardValue));
		return new PlayerCard(valueNextCard);
	}

	/*
	 * Strategie, die in den ersten beiden Spielen aufjedenfall gespielt wird und
	 * danach nur noch, wenn die Strategie des Gegners nicht erkannt werden konnte
	 */
	private int strategyOne(int pointCard) {
		int card = 0;
		if (countRounds==1){
		/* spezielle Kartenlegetechnik f�r die erste Runde */
			card = roundOne(pointCard);
		}
		else {
		/*
		 * ab der zweiten Runde wird immer gleich entschieden, welche Karte zu spielen
		 * ist
		 */card=this.roundElse(pointCard);	
		}
		/*
		 * zum Schluss wird noch �berpr�ft ob meine gew�hlte Karte noch in meinen
		 * Handkarten vorhanden ist wenn ja wird sie gespielt wenn nein wird eine Karte
		 * aus der Mitte gespielt
		 */
		if (this.canUse(new PlayerCard(card))) {
			return card;
		} else {
			return playMiddleCard();
		}

	}
	
	private int roundOne(int pointCard) {
		// ist die PunkteKarte 9 oder 10 lege ich 14
		if (pointCard == 10 || pointCard == 9) {
			return 1;
		}
		// ist die PunkteKarte 6,7,8 lege ich 13
		else if (pointCard>5)
			return 13;
		// ist die Punktekarte -4,-5,4oder 5 lege ich 12
		else if (pointCard == -5 || pointCard == -4 || pointCard == 4 || pointCard == 5) {
			return 12;
		}
		// ist die PunkteKarte -1,-2,-3 lege ich 10
		else if (pointCard < 0) {
			return 10;

		}
		// ansonsten lege ich eine Karte mit dem gleichen Wert wie die PunkteKarte
		else {
			return pointCard;
		}
	}

	private int roundElse(int pointCard) {
		int card = 0;

		switch (pointCard) {
		/*
		 * ist die PunkteKarte 9 oder 10 und hat der Gegner die gleiche h�chste Karte
		 * noch wie ich dann lege ich meine niedrigste Karte wenn nicht, lege ich meine
		 * h�chste Karte
		 */
		case 10:
		case 9:
			if (enemyCards.get(enemyCards.size() - 1) != myCards.get(myCards.size() - 1)) {
				card = myCards.get(myCards.size() - 1);

			} else {
				card = myCards.get(0);
			}
			break;
			/* ist die 9 oder 10 nicht mehr im Spiel, spiele ich meine h�chste Karte ansonsten wird die
			 * Punkte Karte +5 gelegt
			 */
		case 8:
		case 7:
		case 6:
			if (gameCards.contains(10)|| gameCards.contains(9)) {
				card=pointCard+5;
			}
			else {
				card = myCards.get(myCards.size()-1);
			}
			break;
			// der Wert der Punkte Karte wird gelegt
		case 5:
		case 4:
		case 3:
		case 2:
		case 1:
			card=pointCard;
			break;
			//eine Karte aus der Mitte meiner noch vorhanden Karten wird gelegt
		case -1:
		case -2:
		case -3:
			card= this.playMiddleCard();
			break;
			/*
			 * ist die PunktKarte -4 oder -5 und der Gegner hat in der Runde zuvor noch
			 * nicht die 12 gelegt oder ich habe keine 12 mehr dann lege ich meine h�chste
			 * Karte hat er die 12 schon gespielt und ich kann die 12 noch spielen dann
			 * spiele ich die 12
			 */

		case -4:
		case -5:
			if (enemyCards.contains(12)) {
				card= myCards.get(myCards.size() - 1);
			} else {
				if (myCards.contains(12)) {
					return 12;
				} else {
					card= myCards.get(myCards.size() - 1);
				}

			}
			break;
			default:
				card = this.playMiddleCard();
		}
		return card;
	}

	/*
	 * passt meine Karte auf die GegnerKarte an kann fr�hestens ab der dritten Runde
	 * gespielt werden falls die Strategie des Gegners erkennbar war
	 */
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
		 * meine Karte ist immer um 1 h�her als die Karte, die der Gegner wahrscheinlich
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
	 * durchl�uft die aktiven Player und gibt die letzte Karte des Players zur�ck,
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

	/* spielt eine Karte aus der Mitte */
	private int playMiddleCard() {
		return myCards.get(myCards.size() / 2);
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
		 * des Gegners zu den jeweiligen PunkteKarten hintereinander gef�llt
		 */
		switch (countGames) {
		case 1:
			this.caseOne(pointCardValue, enemyCard);
			break;
		case 2:
			this.caseTwo(pointCardValue, enemyCard);
			break;
		case 3:
			this.caseThree(enemyCard);
			break;
		default:
			System.out.println(strategy);
		}

	}

	private void caseOne(int pointCardValue, int enemyCard) {
		if (countRounds == 1) {
			enemyStrategy.add(Spiele);
			enemyStrategy.get(0).add(pointCardValue);
		} /*
			 * else if (countRounds==15){ enemyStrategy.get(0).add(pointCardValue); }
			 */ else {
			enemyStrategy.get(0).add(enemyCard);
			enemyStrategy.get(0).add(pointCardValue);
		}
	}

	private void caseTwo(int pointCardValue, int enemyCard) {
		/* zun�chst noch hinzuf�gen der letzten GegnerKarte aus dem ersten Spiel */
		if (countRounds == 1) {
			enemyStrategy.get(0).add(enemyCard);
			enemyStrategy.add(Spiele2);
			enemyStrategy.get(1).add(pointCardValue);
			/*
			 * dann bef�llen der zweiten Dimesnion der ArrayList mit den Karten des Gegners
			 * zu den jeweiligen PunkteKarten hintereinander
			 */
		} else {

			enemyStrategy.get(1).add(enemyCard);
			enemyStrategy.get(1).add(pointCardValue);
		}
	}

	private void caseThree(int enemyCard) {
		if (countRounds == 2) {
			enemyStrategy.get(1).add(enemyCard);
		} else if (countRounds == 3) {
			/*
			 * �berpr�fen ob die Zuordnung der erten Dimension der List zur Zuordnung der
			 * zweiten Dimension der List passt wenn nein wird strategy auf false gesetzt
			 * --> der Gegner legt nicht jede Runde, die gleiche Karte auf eine PunkteKarte
			 * --> ich kann die Strategie nicht lernen
			 */

			int points;
			int enemy;
			for (int i = 0; i < enemyStrategy.get(0).size()-2; i += 2) {
				points = enemyStrategy.get(0).get(i);
				enemy = enemyStrategy.get(0).get(i + 1);

				for (int j = 0; j < enemyStrategy.get(1).size()-2; j += 2) {
					if (enemyStrategy.get(1).get(j) == points && enemyStrategy.get(1).get(j + 1) != enemy) {
						strategy = false;
						break;
					}
				}
			}
		}
	}
}