package org.jsweet.examples.blocksgame.util;

import def.dom.CanvasRenderingContext2D;

public abstract class AnimatedElement {

	public boolean invalidated;
	private int animationStepCount = 0;
	private int remainingAnimationSteps = -1;

	public boolean isVisible() {
		return true;
	};

	public void setVisible(boolean visible) {
	};

	public int getAnimationStep() {
		return this.animationStepCount - this.remainingAnimationSteps;
	}

	public void nextAnimationStep() {
		this.remainingAnimationSteps--;
		this.invalidated = this.remainingAnimationSteps >= 0;
	}

	public double getRemainingAnimationSteps() {
		return this.remainingAnimationSteps;
	}

	public double getAnimationStepCount() {
		return this.animationStepCount;
	}

	public void initAnimation(int stepCount) {
		this.animationStepCount = stepCount;
		this.remainingAnimationSteps = stepCount - 1;
		this.invalidated = true;
	}

	public void stopAnimation() {
		this.remainingAnimationSteps = -1;
		this.invalidated = true;
	}

	public void renderAnimation(CanvasRenderingContext2D animationCxt, CanvasRenderingContext2D areaCxt) {
	}

	public boolean isAnimating() {
		return this.remainingAnimationSteps >= 0;
	}

}
