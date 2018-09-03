/*
 * WCT³ (WIAI Course Timetabling Tool) is a software that strives to automate
 * the timetabling process at the WIAI faculty of the University of Bamberg.
 *
 * WCT³-GUI comprises functionality to view generated timetables, edit semester
 * data and to generate new timetables.
 *
 * Copyright (C) 2018 Nicolas Gross
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package wcttt.gui.controller;

import java.util.concurrent.Flow;

/**
 * An abstract controller class for controllers that register on a subscription.
 *
 * @param <T> the type of objects received through the subscription.
 */
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
