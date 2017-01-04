package org.jsweet.examples.blocksgame.util;

import def.js.Math;

public class Collisions {

	// see http://williamecraver.wix.com/elastic-equations
	public static void sphericCollision(MobileElement refMobile, MobileElement targetMobile) {
		double th1 = refMobile.speedVector.angle();
		double th2 = targetMobile.speedVector.angle();
		double v1 = refMobile.speedVector.length();
		double v2 = targetMobile.speedVector.length();
		double m1 = refMobile.weight;
		double m2 = targetMobile.weight;

		Vector r_per = targetMobile.getPosition().to(refMobile.getPosition()); 
		double phi = r_per.angle();

		double a2 = (v2 * Math.cos(th2 - phi) * (m2 - m1) + 2 * m1 * v1 * Math.cos(th1 - phi)) / (m1 + m2);
		double b2 = v2 * Math.sin(th2 - phi);

		targetMobile.speedVector.x = a2 * Math.cos(phi) + b2 * Math.cos(phi + Math.PI / 2);
		targetMobile.speedVector.y = a2 * Math.sin(phi) + b2 * Math.sin(phi + Math.PI / 2);
	}

}
