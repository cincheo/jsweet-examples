package org.jsweet.examples.promises;

import static def.dom.Globals.alert;
import static def.dom.Globals.console;
import static def.dom.Globals.document;
import static def.dom.Globals.setTimeout;
import static def.dom.Globals.window;
import static def.js.Globals.parseInt;
import static jsweet.util.Lang.array;
import static jsweet.util.Lang.function;
import static jsweet.util.StringTypes.click;
import static jsweet.util.StringTypes.div;

import java.util.function.Consumer;

import def.es6_promise.Promise;
import def.es6_promise.Promise.CallbackBiConsumer;
import def.dom.HTMLDivElement;
import def.dom.HTMLElement;
import def.js.Array;
import def.js.Date;
import def.js.Math;

class ConcurrentSpinner {

	public static void main(String[] args) {
		window.onload = (e) -> {
			return new ConcurrentSpinner((HTMLDivElement) document.getElementById("spinner"));
		};
	}

	private HTMLDivElement spinner;

	public ConcurrentSpinner(HTMLDivElement spinner) {
		this.spinner = spinner;

		startRace() //
				.thenOnFulfilledFunction(this::onSuccess) //
				.catchOnRejectedFunction(this::onError);
	}

	private Void onSuccess(Double[] times) {
		onComplete();
		document.getElementById("end-overlay").classList.add("visible");
		return null;
	}

	private Void onError(Object error) {
		onComplete();
		alert("An error occurred: " + error);
		return null;
	}

	private void onComplete() {
		console.log("example completed");
	}

	/**
	 * Create and start all progress bars
	 * 
	 * @return a promise of the end of all progresses
	 */
	@SuppressWarnings("unchecked")
	private Promise<Double[]> startRace() {
		Array<Promise<Double>> promises = new Array<Promise<Double>>();
		for (int i = 0; i < 5; i++) {
			promises.push(this.spawnProgressBar(i));
		}

		return Promise.all(array(promises));
	}

	/**
	 * Create a progress bar and start its race process
	 * 
	 * @param index
	 *            Progress bar's index
	 * @return A promise of progress end, whose result is the total time to
	 *         completion, in millis
	 */
	private Promise<Double> spawnProgressBar(int index) {

		HTMLDivElement progressBackground = document.createElement(div);
		progressBackground.classList.add("background");

		HTMLDivElement progressText = document.createElement(div);
		progressText.classList.add("text");

		HTMLDivElement progress = document.createElement(div);
		progress.classList.add("progress");
		progress.appendChild(progressBackground);
		progress.appendChild(progressText);

		HTMLDivElement bar = document.createElement(div);
		bar.classList.add("bar");
		bar.id = "bar" + index;
		bar.dataset.$set("progress", "0");
		bar.addEventListener(click, (event) -> {
			double newProgress = Math.round((100 * (bar.clientHeight - event.clientY) / bar.clientHeight));
			console.log("clicked on " + event.offsetY + " percent=" + newProgress + " height=" + bar.clientHeight);
			bar.dataset.$set("progress", "" + newProgress);
			return null;
		});
		bar.appendChild(progress);
		this.spinner.appendChild(bar);

		double startTime = new Date().getTime();
		return new Promise<Double>((CallbackBiConsumer<Consumer<Double>, Consumer<Object>>) //
		(resolve, reject) -> {
			this.onProgress(bar, resolve, reject, startTime);
		});
	}

	private void onProgress(HTMLDivElement progressBar, Consumer<Double> resolve, Consumer<Object> reject,
			double startTime) {
		double progress = parseInt(progressBar.dataset.$get("progress"));

		// console.log(progressBar.id + ": " + progress + "%");
		HTMLElement progressElement = (HTMLElement) progressBar.querySelector(".progress");
		progressElement.style.height = progress + "%"; // increase bar's height

		HTMLElement text = (HTMLElement) progressBar.querySelector(".text");
		text.textContent = progress + "%";
		text.style.color = "rgb(" + (40 + (((100 - progress) / 100) * 215)) + ", " + (2 * progress / 100) * 255
				+ ", 0)";
		text.style.fontSize = ((progress + 20) / 55) + "em";

		if (progress < 100) {
			// task is not finished, let's continue
			progressBar.dataset.$set("progress", "" + (progress + 1));
			double nextTick = 100 * Math.random();
			setTimeout(function(() -> {
				onProgress(progressBar, resolve, reject, startTime);
			}), nextTick);
		} else {

			// progress reached 100%, display animation & resolve promise
			text.style.fontSize = "";
			progressBar.classList.add("loaded");
			progressBar.classList.add("animation");
			setTimeout(function(() -> {
				progressBar.classList.remove("animation");
			}), 350);

			resolve.accept(new Date().getTime() - startTime);
		}
	}
}
