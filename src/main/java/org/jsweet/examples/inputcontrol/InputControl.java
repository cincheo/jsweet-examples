package org.jsweet.examples.inputcontrol;

import static def.dom.Globals.console;
import static def.dom.Globals.alert;
import static def.dom.Globals.document;
import static def.dom.Globals.window;

import java.util.ArrayList;
import java.util.List;

import def.dom.Element;
import def.dom.Event;
import def.dom.HTMLAnchorElement;
import def.dom.HTMLBodyElement;
import def.dom.HTMLElement;
import def.dom.HTMLFormElement;
import def.dom.HTMLInputElement;
import def.dom.Node;
import def.dom.NodeList;
import def.dom.NodeListOf;
import def.js.Array;
import jsweet.util.StringTypes;

import static def.jquery.Globals.$;

public class InputControl {

	public static void main(String[] args) {
		new InputControl();
	}
	
	private HTMLFormElement form;

	public InputControl() {
		console.info("creating input control example");

		this.form = (HTMLFormElement) document.querySelector("form");
		this.form.onsubmit = this::onSubmit;

		// you can use regular Java API
		List<String> l = new ArrayList<>();
		l.add("Hello");
		l.add("world");
		// and you can also use regular JavaScript APIs
		Array<String> a = new Array<>();
		a.push("Hello", "world");
		// use of jQuery with the jQuery candy
		$("#target").text(l.toString());
		// use of the JavaScript DOM API
		alert(a.toString());
		
		Array<Element> inputs = Array.from(this.form.querySelectorAll(".form-control"));
		for (Node element : inputs) {
			addHitListener((HTMLElement) element);
		}

		this.form.querySelector("#reset").addEventListener("click", (event) -> {
			form.reset();
			for (Element element : inputs) {
				element.classList.remove("hit");
			}
		});
	}

	private Boolean onSubmit(Event event) {
		event.stopPropagation();
		console.log("form is valid");
		document.getElementById("form").classList.add("invisible");

		window.scroll(0, 0);

		document.getElementById("name").textContent = getInput("name").value;
		document.getElementById("age").textContent = getInput("age").value;
		document.getElementById("email").textContent = getInput("email").value;
		document.getElementById("tel").textContent = getInput("tel").value;

		String url = getInput("url").value;
		HTMLAnchorElement urlLink = (HTMLAnchorElement) document.getElementById("url");
		urlLink.href = url;
		urlLink.textContent = url;

		document.getElementById("range").textContent = getInput("range").value;
		document.getElementById("date").textContent = getInput("date").value;
		document.getElementById("continent").textContent = getInput("continent").value;
		document.getElementById("favcolor").style.backgroundColor = getInput("favcolor").value;

		document.getElementById("view").classList.remove("invisible");

		return false;
	}

	private HTMLInputElement getInput(String name) {
		return (HTMLInputElement) document.querySelector("[name='" + name + "']");
	}

	private void addHitListener(HTMLElement element) {
		element.addEventListener("keyup", (evt) -> {
			console.log("typing...");
			element.classList.add("hit");
		});
	}
}
