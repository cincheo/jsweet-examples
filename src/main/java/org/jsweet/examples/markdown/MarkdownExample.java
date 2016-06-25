package org.jsweet.examples.markdown;

import def.showdown.showdown.Converter;
import jsweet.dom.HTMLFormElement;
import jsweet.dom.HTMLInputElement;

import static jsweet.dom.Globals.console;
import static jsweet.dom.Globals.document;

/**
 * Lets the user enter text and converts it from Markdown to HTML.
 * Created by Matthias Braun on 6/25/2016.
 */
public class MarkdownExample {

    private final Converter markdownConverter = new Converter();

    public MarkdownExample() {

        HTMLFormElement form = (HTMLFormElement) document.querySelector("form");
        HTMLInputElement markdownInput = (HTMLInputElement) form.querySelector("#markdownInput");
        addHitListener(markdownInput);
    }

    public static void main(String... args) {
        new MarkdownExample();
    }

    private void addHitListener(HTMLInputElement element) {
        //  Transform the content from markdown to HTML
        element.addEventListener("keyup", (evt) -> {
            String text = element.value;
            console.info("Text: " + text);
            String html = markdownConverter.makeHtml(text);
            console.info("Text as HTML: " + html);
        });
    }
}
