package org.jsweet.examples.todomvc;

import static def.jquery.Globals.$;
import static def.underscore.Globals._;
import static jsweet.dom.Globals.clearTimeout;
import static jsweet.util.Globals.$apply;
import static jsweet.util.Globals.function;
import static jsweet.util.Globals.union;

import java.util.function.Consumer;
import java.util.function.Function;

import def.backbone.backbone.Collection;
import def.backbone.backbone.Model;
import def.backbone.backbone.ModelSaveOptions;
import def.backbone.backbone.ObjectHash;
import def.backbone.backbone.View;
import def.backbone.backbone.ViewOptions;
import def.jquery.JQuery;
import jsweet.dom.Element;
import jsweet.dom.Event;
import jsweet.dom.HTMLInputElement;
import jsweet.dom.KeyboardEvent;
import jsweet.lang.Ambient;

//Todo Model
//----------

class TodoStruct extends ObjectHash {
	public String content;
	public int order;
	public boolean done;
}

class StatsStruct extends ObjectHash {
	public int total;
	public int done;
	public int remaining;
}

// Our basic **Todo** model has `content`, `order`, and `done` attributes.
class Todo extends Model {

	// Default attributes for the todo.
	@Override
	public TodoStruct defaults() {
		return new TodoStruct() {
			{
				content = "empty todo...";
				done = false;
			}
		};
	}

	// Ensure that each todo created has `content`.
	@Override
	public void initialize() {
		if (this.get("content") == null) {
			this.set(new TodoStruct() {
				{
					content = defaults().content;
				}
			});
		}
	}

	// Toggle the `done` state of this todo item.
	public void toggle() {
		this.save(new TodoStruct() {
			{
				done = !(boolean) get("done");
			}
		});
	}

	// Remove this Todo from *localStorage* and delete its view.
	@Override
	public Object clear() {
		return this.destroy();
	}

}

@Ambient
class Store {
	public Store(String dbName) {
	}
}

// Todo Collection
// ---------------

// The collection of todos is backed by *localStorage* instead of a remote
// server.
class TodoList extends Collection<Todo> {

	// Reference to this collection's model.
	Class<Todo> model = Todo.class;

	// Save all of the todo items under the `"todos"` namespace.
	Store localStorage = new Store("todos-backbone");

	// Filter down the list of all todo items that are finished.
	Todo[] done() {
		return this.filter((todo, i) -> {
			return (Boolean) todo.get("done");
		});
	}

	// Filter down the list to only todo items that are still not finished.
	Todo[] remaining() {
		Todo[] done = this.done();
		return (Todo[]) $apply((Function<Todo, Todo[]>)
				this::without, done);
	}

	// We keep the Todos in sequential order, despite being saved by unordered
	// GUID in the database. This generates the next order number for new items.
	int nextOrder() {
		if (this.length == 0)
			return 1;
		return (Integer) this.last().get("order") + 1;
	}

	// Todos are sorted by their original insertion order.
	{
		comparator = union(function((Todo todo) -> {
			return (Integer) todo.get("order");
		}));
	}

}

// Todo Item View
// --------------

// The DOM element for a todo item...
class TodoView extends View<Todo> {

	// The TodoView listens for changes to its model, re-rendering. Since
	// there's
	// a one-to-one correspondence between a **Todo** and a **TodoView** in this
	// app, we set a direct reference on the model for convenience.
	Function<Object, String> template;

	// A TodoView model must be a Todo, redeclare with specific type
	Todo model;
	JQuery input;

	public TodoView(ViewOptions<Todo> options) {
		// ... is a list tag.
		this.tagName = "li";

		// The DOM events specific to an item.
		$set("events", new Object() {
			{
				$set("click .check", "toggleDone");
				$set("dblclick label.todo-content", "edit");
				$set("click span.todo-destroy", "clear");
				$set("keypress .todo-input", "updateOnEnter");
				$set("blur .todo-input", "close");
			}
		});

		$super(options);

		// Cache the template function for a single item.
		this.template = _.template($("#item-template").html());

		_.bindAll(this, "render", "close", "remove");
		this.model.bind("change", function(this::render));
		this.model.bind("destroy", function(this::remove));
	}

	// Re-render the contents of the todo item.
	public TodoView render() {
		this.$el.html(this.template.apply(this.model.toJSON()));
		this.input = this.$(".todo-input");
		return this;
	}

	// Toggle the `"done"` state of the model.
	void toggleDone() {
		this.model.toggle();
	}

	// Switch this view into `"editing"` mode, displaying the input field.
	void edit() {
		this.$el.addClass("editing");
		this.input.focus();
	}

	// Close the `"editing"` mode, saving changes to the todo.
	void close() {
		this.model.save(new TodoStruct() {
			{
				content = (String) input.val();
			}
		});
		this.$el.removeClass("editing");
	}

	// If you hit `enter`, we're through editing the item.
	void updateOnEnter(KeyboardEvent e) {
		if (e.keyCode == 13)
			close();
	}

	// Remove the item, destroy the model.
	void clear() {
		this.model.clear();
	}

}

// The Application
// ---------------

// Our overall **AppView** is the top-level piece of UI.
class AppView extends View<Todo> {

	JQuery input;
	HTMLInputElement allCheckbox;
	Function<Object, String> statsTemplate;

	public AppView() {
		super();
		// Delegated events for creating new items, and clearing completed ones.
		$set("events", new Object() {
			{
				$set("keypress #new-todo", "createOnEnter");
				$set("keyup #new-todo", "showTooltip");
				$set("click .todo-clear a", "clearCompleted");
				$set("click .mark-all-done", "toggleAllComplete");
			}
		});

		// Instead of generating a new element, bind to the existing skeleton of
		// the App already present in the HTML.
		this.setElement($("#todoapp"), true);

		// At initialization we bind to the relevant events on the `Todos`
		// collection, when items are added or changed. Kick things off by
		// loading any preexisting todos that might be saved in *localStorage*.
		_.bindAll(this, "addOne", "addAll", "render", "toggleAllComplete");

		this.input = this.$("#new-todo");
		this.allCheckbox = (HTMLInputElement) this.$(".mark-all-done").$get(0);
		this.statsTemplate = _.template($("#stats-template").html());

		Globals.Todos.bind("add", function((Consumer<Todo>) this::addOne));
		Globals.Todos.bind("reset", function(this::addAll));
		Globals.Todos.bind("all", function(this::render));

		Globals.Todos.fetch();
	}

	// Re-rendering the App just means refreshing the statistics -- the rest
	// of the app doesn't change.
	public View<Todo> render() {

		this.$("#todo-stats").html(this.statsTemplate.apply(new StatsStruct() {
			{
				total = (int) Globals.Todos.length;
				done = Globals.Todos.done().length;
				remaining = Globals.Todos.remaining().length;
			}
		}));

		this.allCheckbox.checked = Globals.Todos.remaining().length == 0;
		return this;
	}

	// Add a single todo item to the list by creating a view for it, and
	// appending its element to the `<ul>`.
	public void addOne(Todo todo) {
		TodoView view = new TodoView(new ViewOptions<Todo>() {
			{
				model = todo;
			}
		});
		this.$("#todo-list").append((Element) view.render().el);
	}

	// Add all items in the **Todos** collection at once.
	void addAll() {
		Globals.Todos.each((todo, p, __) -> {
			this.addOne(todo);
		});
	}

	// Generate the attributes for a new Todo item.
	Object newAttributes() {
		return new TodoStruct() {
			{
				content = (String) input.val();
				order = Globals.Todos.nextOrder();
				done = false;
			}
		};
	}

	// If you hit return in the main input field, create new **Todo** model,
	// persisting it to *localStorage*.
	void createOnEnter(KeyboardEvent e) {
		if (e.keyCode != 13)
			return;
		Globals.Todos.create(this.newAttributes(), (ModelSaveOptions) null);
		this.input.val("");
	}

	// Clear all done todo items, destroying their models.
	boolean clearCompleted() {
		_.each(Globals.Todos.done(), (todo, i, a) -> {
			todo.clear();
			return null;
		});
		return false;
	}

	Integer tooltipTimeout = null;

	// Lazily show the tooltip that tells you to press `enter` to save
	// a new todo item, after one second.
	void showTooltip(Event e) {
		JQuery tooltip = $(".ui-tooltip-top");
		String val = (String) this.input.val();
		tooltip.fadeOut();
		if (this.tooltipTimeout != null)
			clearTimeout(this.tooltipTimeout);
		if (val == "" || val == this.input.attr("placeholder"))
			return;
		this.tooltipTimeout = (int) _.delay(function(() -> {
			tooltip.show().fadeIn();
		}), 1000, new Object[0]);
	}

	void toggleAllComplete() {
		boolean done = this.allCheckbox.checked;
		Globals.Todos.each((todo, index, __) -> {
			todo.save(new Object() {
				{
					$set("done", done);
				}
			});
		});
	}

}

class Globals {

	// Create our global collection of **Todos**.
	public static TodoList Todos = new TodoList();

	// Load the application once the DOM is ready, using `jQuery.ready`:
	public static void main() {
		$(function(() -> {
			// // Finally, we kick things off by creating the **App**.
			new AppView();
		}));
	}

}
