package org.jsweet.examples.markdown;

import def.dom.HTMLElement;
import def.dom.HTMLInputElement;

import static def.marked.Globals.marked;
import static def.dom.Globals.document;

/**
 * Lets the user enter text and converts it from Markdown to HTML.
 * Created by Matthias Braun on 6/25/2016.
 */
public class MarkdownExample {

    public MarkdownExample() {
        // Get the input and output elements from markdown/index.hml
        HTMLInputElement markdownInput = (HTMLInputElement) document.querySelector("#markdownInput");
        HTMLElement htmlOutput = (HTMLElement) document.querySelector("#htmlOutput");

        // Initially, show the default text of the input element in the HTML output
        convert(markdownInput, htmlOutput);

        // Markdown text in the input element appears as HTML in the output element when the user types
        markdownInput.addEventListener("keyup", evt -> convert(markdownInput, htmlOutput));
    }

    public static void main(String... args) {
        new MarkdownExample();
    }

    /**
     * Takes the text of the {@code inputElement}, converts it from Markdown to HTML, and puts the HTML into the
     * {@code outputElement}.
     *
     * @param inputElement  we convert the text of this {@link HTMLInputElement} from Markdown to HTML
     * @param outputElement this {@link HTMLElement} contains the text of the {@code inputElement} converted from Markdown to HTML
     */
    private static void convert(HTMLInputElement inputElement, HTMLElement outputElement) {
        String inputText = inputElement.value;
        String html = marked(inputText);
        outputElement.innerHTML = html;
    }
}
