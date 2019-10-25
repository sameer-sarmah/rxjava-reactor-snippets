package spring.reactor;

import java.util.concurrent.atomic.AtomicReference;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class RequestOnNext implements Subscriber<String>{

	AtomicReference<Subscription> subscriptionReference = new AtomicReference<>();
	
	@Override
	public void onSubscribe(Subscription subscription) {
		subscriptionReference.set(subscription);
		subscription.request(1);
	}

	@Override
	public void onNext(String text) {
		System.out.println("The item is "+text); 
		Subscription subscription = subscriptionReference.get();
		subscription.request(1);
	}

	@Override
	public void onError(Throwable error) {
		error.printStackTrace();
	}

	@Override
	public void onComplete() {
		Subscription subscription = subscriptionReference.get();
		subscription.cancel();
		System.out.println("Completed");
	}

}
