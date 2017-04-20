package org.jsweet.examples.knockoutjs;

import static def.knockout.Globals.ko;
import static jsweet.util.Lang.number;

import def.knockout.KnockoutComputed;
import def.knockout.KnockoutObservable;
import def.knockout.KnockoutObservableArray;
import jsweet.lang.Interface;

public class ReservationsViewModel {

	Meal[] availableMeals;

	KnockoutObservableArray<SeatReservation> seats;

	public ReservationsViewModel() {
		availableMeals = new Meal[] { new Meal() {
			{
				mealName = "Standard (crouton salad)";
				price = 0;
			}
		}, new Meal() {
			{
				mealName = "Premium (foie gras)";
				price = 34.95;
			}
		}, new Meal() {
			{
				mealName = "Ultimate (caviar)";
				price = 290;
			}
		}, };
		seats = ko.observableArray(new SeatReservation[] { new SeatReservation("Renaud", availableMeals[0]),
				new SeatReservation("Eve", availableMeals[2]), new SeatReservation("Domitille", availableMeals[0]),
				new SeatReservation("Domitille", availableMeals[1]) });
	}

	public void addSeat() {
		seats.push(new SeatReservation("", availableMeals[0]));
	}

	public static void main(String[] args) {
		ko.applyBindings(new ReservationsViewModel());
	}

}

@Interface
abstract class Meal {
	String mealName;
	double price;
}

class SeatReservation {
	String name;
	KnockoutObservable<Meal> meal;
	KnockoutComputed<String> formattedPrice;

	public SeatReservation(String name, Meal initialMeal) {
		this.name = name;
		this.meal = ko.observable(initialMeal);
		this.formattedPrice = ko.computed(() -> {
			Double price = meal.apply().price;
			return price != null ? "$" + number(price).toFixed(2) : "None";
		});
	}

}
