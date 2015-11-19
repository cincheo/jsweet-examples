package org.jsweet.examples.blocksgame;

import static jsweet.dom.Globals.window;

import jsweet.dom.Event;
import jsweet.lang.Date;

public class Globals {

	static GameManager gm;

	public static void animate() {
		GameArea area = gm.getCurrentLevel();
		if (!gm.isPaused()) {
			if (!area.finished) {
				area.currentDate = new Date();
				area.render();
				area.calculateNextPositions();
				window.requestAnimationFrame((time) -> {
					animate();
				});
			} else {
				gm.onLevelEnded();
			}
		}
	}

	public static Object start(Event event) {
		gm = new GameManager();
		gm.init();
		gm.startGame();
		return event;
	}

	public static void main(String[] args) {
		window.onload = Globals::start;
	}
	
}
