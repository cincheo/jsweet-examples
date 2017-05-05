package org.jsweet.examples.angularjs;

import static def.angularjs.Globals.angular;
import static def.dom.Globals.confirm;
import static def.dom.Globals.console;
import static def.dom.Globals.setTimeout;
import static def.js.Globals.parseInt;
import static jsweet.util.Lang.function;
import static jsweet.util.Lang.string;
import static jsweet.util.Lang.union;
import static jsweet.util.Lang.object;
import static org.jsweet.examples.angularjs.Globals.toTitleCase;

import java.util.function.Consumer;

import def.angularjs.ng.IDeferred;
import def.angularjs.ng.ILocationService;
import def.angularjs.ng.IModule;
import def.angularjs.ng.IPromise;
import def.angularjs.ng.IQService;
import def.angularjs.ng.IScope;
import def.angularjs.ng.route.IRoute;
import def.angularjs.ng.route.IRouteParamsService;
import def.angularjs.ng.route.IRouteProvider;
import def.js.Array;
import def.js.Date;
import jsweet.lang.Interface;
import def.js.RegExp;

enum InvitationStatus {
	NEW, SUBMITTED, ACCEPTED;
}

class Invitation {

	private static int counter = 0;

	Number id = counter++;
	String firstName;
	String lastName;
	String email;

	public Date creationDate;
	public InvitationStatus status;

	public Invitation() {
		this.status = InvitationStatus.NEW;
		this.creationDate = new Date();
	}

	public String getFullName() {
		String firstName = this.firstName == null ? "" : this.firstName;
		String lastName = this.lastName == null ? "" : this.lastName;
		return toTitleCase(firstName + " " + lastName);
	}

	public boolean isSubmitted() {
		return status == InvitationStatus.SUBMITTED;
	}

	public String getStatusLabel() {
		String label = object(InvitationStatus.class).$get("" + status);
		return toTitleCase(label);
	}
}

/**
 * fake data api for invitations
 */
class InvitationRepository {
	public static InvitationRepository instance = new InvitationRepository();

	private Array<Invitation> invitations = new Array<Invitation>();

	public InvitationRepository() {
		add(new Invitation() {
			{
				firstName = "Bill";
				lastName = "Gates";
				email = "bill.gates@microsoft.com";
				status = InvitationStatus.ACCEPTED;
			}
		});

		add(new Invitation() {
			{
				firstName = "Barack";
				lastName = "Obama";
				email = "barack.obama@whitehouse.gov";
				status = InvitationStatus.SUBMITTED;
			}
		});

		add(new Invitation() {
			{
				firstName = "Louis";
				lastName = "Grignon";
				email = "louis.grignon@gmail.com";
				status = InvitationStatus.SUBMITTED;
			}
		});
	}

	public void add(Invitation invitation) {
		invitations.push(invitation);
	}

	public IPromise<Array<Invitation>> list(IQService $q) {
		IDeferred<Array<Invitation>> deferred = $q.defer();

		// mock invitation query
		setTimeout(function(() -> {
			deferred.resolve(invitations);
		}), 100);

		return deferred.promise;
	}

	public Invitation get(Number id) {
		return invitations.filter(person -> {
			return person.id == id;
		}).$get(0);
	}

	public void remove(Invitation invitation) {
		console.log("remove invitation", invitation);
		int index = invitations.indexOf(invitation);
		if (index != -1) {
			invitations.splice(index, 1);
		}
	}
}

@Interface
abstract class GlobalScope extends IScope {
	String greeting;
}

class GlobalController {
	public GlobalController(GlobalScope $scope) {
		if (new Date().getHours() >= 12) {
			$scope.greeting = "Hello";
		} else {
			$scope.greeting = "Good morning";
		}
	}
}

@Interface
abstract class InvitationViewScope extends IScope {
	Invitation invitation;
}

class InvitationViewController {
	public InvitationViewController(InvitationViewScope $scope, IRouteParamsService $routeParams) {
		double id = parseInt($routeParams.$get("id").toString());
		console.log("view invitation: " + id);

		Invitation invitation = InvitationRepository.instance.get(id);
		$scope.invitation = invitation;
	}
}

@Interface
abstract class InvitationEditScope extends IScope {
	Runnable submit;
	Runnable cancel;

	Invitation invitation;

	String firstName;
	String lastName;
	String email;
}

class InvitationEditController {
	private Invitation invitation;
	private InvitationEditScope $scope;
	private ILocationService $location;

	public InvitationEditController(InvitationEditScope $scope, IRouteParamsService $routeParams,
			ILocationService $location) {
		this.$scope = $scope;
		this.$location = $location;
		this.invitation = InvitationRepository.instance.get(parseInt($routeParams.$get("id").toString()));

		$scope.firstName = invitation.firstName;
		$scope.lastName = invitation.lastName;
		$scope.email = invitation.email;
		$scope.invitation = invitation;
		$scope.submit = this::submit;
		$scope.cancel = this::cancel;
	}

	public void submit() {
		console.log("saving invitation modifications");
		invitation.firstName = $scope.firstName;
		invitation.lastName = $scope.lastName;
		invitation.email = $scope.email;
		invitation.status = InvitationStatus.SUBMITTED;
		$location.path("#/list");
	}

	public void cancel() {
		if (confirm("Are you sure you want to delete " + invitation.getFullName() + "'s invitation")) {
			InvitationRepository.instance.remove(invitation);
			$location.path("#/list");
		}
	}
}

class InvitationRequestController {
	private Invitation invitation;
	private InvitationEditScope $scope;
	private ILocationService $location;

	public InvitationRequestController(InvitationEditScope $scope, ILocationService $location) {
		$scope.invitation = invitation = new Invitation();
		$scope.submit = this::submit;
		this.$scope = $scope;
		this.$location = $location;
	}

	private void submit() {
		console.log("submitting invitation");
		invitation.firstName = $scope.firstName;
		invitation.lastName = $scope.lastName;
		invitation.email = $scope.email;
		invitation.status = InvitationStatus.SUBMITTED;
		InvitationRepository.instance.add(invitation);
		$location.path("#/list");
	}
}

@Interface
abstract class InvitationListScope extends IScope {
	Array<Invitation> invitations;
	Consumer<Invitation> viewInvitation;
}

class InvitationListController {
	private Array<Invitation> invitations;
	private ILocationService $location;

	public InvitationListController(InvitationListScope $scope, ILocationService $location, IQService $q) {
		this.$location = $location;

		console.log("display invitations list");

		invitations = new Array<Invitation>();

		$scope.invitations = invitations;

		$scope.viewInvitation = this::viewInvitation;
		InvitationRepository.instance.list($q).thenSuccessCallbackFunction(existingInvitations -> {
			console.log("got invitations", existingInvitations);
			for (Invitation invitation : existingInvitations) {
				invitations.push(invitation);
			}
			return null;
		});
	}

	public void viewInvitation(Invitation invited) {
		console.log("invitation " + invited);
		$location.path("/view/" + invited.id);
	}
}

class Globals {
	public static void main(String[] args) {
		IModule module = angular.module("angularExample", new String[] { "ngRoute" });
		module.controller("GlobalController", function(GlobalController.class));
		module.controller("InvitationRequestController", function(InvitationRequestController.class));
		module.controller("InvitationEditController", function(InvitationEditController.class));
		module.controller("InvitationListController", function(InvitationListController.class));
		module.controller("InvitationViewController", function(InvitationViewController.class));

		module.config(new Object[] { "$routeProvider", function((IRouteProvider $routeProvider) -> {
			$routeProvider.when("/list", new IRoute() {
				{
					templateUrl = union("list.html");
					controller = union("InvitationListController");
				}
			}).when("/request", new IRoute() {
				{
					templateUrl = union("edit.html");
					controller = union("InvitationRequestController");
				}
			}).when("/view/:id", new IRoute() {
				{
					templateUrl = union("view.html");
					controller = union("InvitationViewController");
				}
			}).when("/edit/:id", new IRoute() {
				{
					templateUrl = union("edit.html");
					controller = union("InvitationEditController");
				}
			}).otherwise(new IRoute() {
				{
					redirectTo = union("/list");
				}
			});
		}) });
	}

	public static String toTitleCase(String str) {
		return string(string(str).toLowerCase().replace(new RegExp("\\w\\S*", "g"), (tok, i) -> {
			return tok.charAt(0).toUpperCase().concat(tok.substr(1).toLowerCase());
		}));
	}
}