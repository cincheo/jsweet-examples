package org.jsweet.examples.markdown;

import jsweet.dom.HTMLElement;
import jsweet.dom.HTMLInputElement;

import static def.marked.Globals.marked;
import static jsweet.dom.Globals.document;

/**
 * Lets the user enter text and converts it from Markdown to HTML.
 * Created by Matthias Braun on 6/25/2016.
 */
public class MarkdownExample {

    public MarkdownExample() {
        // Get the input and output elements from markdown/index.hml
        HTMLInputElement markdownInput = (HTMLInputElement) document.querySelector("#markdownInput");
        HTMLElement htmlOutput = (HTMLElement) document.querySelector("#htmlOutput");

        // Markdown text in the input element appears as HTML in the output element
        connect(markdownInput, htmlOutput);

        // Show the default text of the input element in the HTML output
        htmlOutput.innerHTML = marked(markdownInput.value);
    }

    public static void main(String... args) {
        new MarkdownExample();
    }

    /**
     * When the user presses a key inside the {@code inputElement}, we convert the text inside it from Markdown to HTML
     * and put the HTML into the {@code htmlOutput}.
     *
     * @param inputElement the user can write Markdown in this {@link HTMLInputElement}
     * @param htmlOutput   this {@link HTMLElement} shows the text of the {@code inputElement} as HTML
     */
    private static void connect(HTMLInputElement inputElement, HTMLElement htmlOutput) {
        // Transform the content from markdown to HTML
        inputElement.addEventListener("keyup", (evt) -> {
            String inputText = inputElement.value;
            String html = marked(inputText);
            htmlOutput.innerHTML = html;
        });
    }
}
