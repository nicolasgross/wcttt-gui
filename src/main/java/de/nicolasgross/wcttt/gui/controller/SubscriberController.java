package de.nicolasgross.wcttt.gui.controller;

import java.util.concurrent.Flow;

public abstract class SubscriberController<T> extends Controller
		implements Flow.Subscriber<T> {

	private Flow.Subscription subscription;


	Flow.Subscription getSubscription() {
		return subscription;
	}

	@Override
	public void onSubscribe(Flow.Subscription subscription) {
		this.subscription = subscription;
		this.subscription.request(1);
	}

	@Override
	public void onError(Throwable throwable) {
		Util.exceptionAlert(throwable);
	}

	@Override
	public void onComplete() {
		// won't happen
	}

}
