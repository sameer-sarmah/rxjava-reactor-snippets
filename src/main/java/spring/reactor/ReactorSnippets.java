package spring.reactor;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.reactivestreams.Subscription;

import io.reactivex.Observable;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import util.Util;

public class ReactorSnippets {

	public static void main(String[] args) {
		InputStream inputStreamOne = ReactorSnippets.class.getClassLoader().getResourceAsStream("bundleOne.properties");
		BufferedReader readerOne = new BufferedReader(new InputStreamReader(inputStreamOne));
		List<String> linesFromOne = readerOne.lines().collect(Collectors.toList());
		
		InputStream inputStreamTwo = ReactorSnippets.class.getClassLoader().getResourceAsStream("bundleTwo.txt");
		BufferedReader readerTwo = new BufferedReader(new InputStreamReader(inputStreamTwo));
		List<String> linesFromTwo = readerTwo.lines().collect(Collectors.toList());

		//IProductDetails productDetails = new ProductDetailsReactorFromCallable();
		//IProductDetails productDetails = new ProductDetailsReactor();
		IProductDetails productDetails = new ProductDetailsReactorFromFuture();
		//sequentialHttpCall();
		//parallelHttpCall(productDetails);
		//requestRequiredItems(linesFromOne);
		//requestRequiredItemsOnNext(linesFromOne);
		//buffer(linesFromOne);
		//groupBy();
		//window(linesFromOne);
		
		Flux<String> deferredFlux = deferred();
		deferredFlux.subscribe((id)->{ System.out.println(id); });
		deferredFlux.subscribe((id)->{ System.out.println(id); });
		deferredFlux.subscribe((id)->{ System.out.println(id); });
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static Flux<String> deferred() {
		return Flux.defer(() -> {
		    return Flux.just(UUID.randomUUID().toString());
		});
	}
	
	private static void groupBy() {
		  Flux.range(0, 100)
								.groupBy((number)-> number % 2 == 0 ? "Even": "Odd")
								.subscribe((entry)->{
									String key = entry.key();
									entry.collectList().subscribe((list)->{
										int sum = list.stream().reduce(0, (accumulator,curent) -> accumulator+curent);
										System.out.println("The sum of "+key+" numbers is "+sum);
									});
								});

	}
	
	private static void buffer(List<String> lines) {
		Flux<List<String>> stream = getStreamOne(lines)
									.buffer(5);
		stream.subscribe((list)->{
			System.out.println(list);
		});
	}
	
	private static void window(List<String> lines) {
		Flux<Flux<String>> stream = getStreamOne(lines)
									.window(5);
		stream.subscribe((fluxList)->{
			fluxList.collectList().subscribe(System.out::println);
		});
	}
	
	private static void requestRequiredItemsOnNext(List<String> lines) {
		Flux<String> stream = getStreamOne(lines);
		stream.subscribe(new RequestOnNext());
	}
	
	private static void requestRequiredItems(List<String> lines) {
		List<Subscription> subscriptionContainer = new ArrayList<>();
		Flux<String> stream = getStreamOne(lines);
		Consumer<String> onNext = (String line) ->{
			System.out.println("The item is "+line); 
		};
		Consumer<Throwable> onError = (Throwable error) ->{
			error.printStackTrace();
		};
		Consumer<Subscription> onSubscribe = (Subscription subscription) ->{
			subscription.request(5);
			subscriptionContainer.add(subscription);
		};
		ScheduledExecutorService scheduledExecutorService =
		        Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
		scheduledExecutorService.scheduleAtFixedRate(()->{ 
			System.out.println("In scheduled executor callable");
			subscriptionContainer.get(0).request(5);
		}, 3, 3, TimeUnit.SECONDS);
		stream.subscribe(onNext,onError,()->{},onSubscribe);
	}
	
	private static void parallelHttpCall(IProductDetails productDetails) {
		Mono<String> productsObs =  productDetails.getProducts();
		Mono<String> productCountObs =  productDetails.getProductCount();
		CountDownLatch latch=new CountDownLatch(2);
		System.out.println("Non blocking");
		productCountObs.subscribe((count)->{
			System.out.println("Number of products are "+count);
			latch.countDown();
		});
		productsObs.subscribe((productsJSON)-> {
			double price = Util.extractUnitPriceOfChai(productsJSON);
			System.out.println("price of chai is "+price);
			latch.countDown();
		});
		System.out.println("all tasks are submitted");
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("all tasks are completed");
	}
	
	private static void sequentialHttpCall(IProductDetails productDetails) {
		Mono<String> productsObs =  productDetails.getProducts();
		Mono<String> productCountObs =  productDetails.getProductCount();
		System.out.println("Non blocking");
		productCountObs.subscribe((count)->{
			System.out.println("Number of products are "+count);
			productsObs.subscribe((productsJSON)-> {
				double price = Util.extractUnitPriceOfChai(productsJSON);
				System.out.println("price of chai is "+price);
				
			});
			
		});
	}
	

	
	private static void create(List<String> lines)
	{
		Flux<String> textContent = Flux.create(emitter -> {
			lines.forEach((text)->emitter.next(text));
			emitter.complete();
		});
		textContent.subscribe((line)-> {System.out.println(line);});
	}
	
	private static void generate(List<String> lines)
	{
		Flux<String> textContent = Flux.generate(emitter -> {
			lines.forEach((text)->emitter.next(text));
			emitter.complete();
		});
		textContent.subscribe((line)-> {System.out.println(line);});
	}
	
	private static void subject(List<String> lines)
	{
		EmitterProcessor<String> subject = EmitterProcessor.create();
		subject.subscribe((line)->{
			if(line.contains("=")) {
				String[] parts = line.split("=");
				System.out.println("The key is '"+parts[0]+"',and the value is '"+parts[1]+"'");
			}
		});
		lines.stream().forEach((line)->{
			subject.onNext(line);
		});
		subject.onComplete();
	}
	
	private static void connectableObservable(Flux<String> stream)
	{
		ConnectableFlux<String>  connectableFlux = stream.publish();
		connectableFlux.subscribe((line)-> System.out.println(line));
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		connectableFlux.connect();
	}
	
	
	
	private static void concatWith(List<String> linesFromOne,List<String> linesFromTwo) {
		Flux<String> streamOne = getStreamOne(linesFromOne);
		Flux<String> streamTwo = getStreamTwo(linesFromTwo);
		Flux<String>  streamConcatenated = streamOne.concatWith(streamTwo);
		streamConcatenated.subscribe((line)->{System.out.println("The key is "+line);});
	}
	
	private static void mergeWith(List<String> linesFromOne,List<String> linesFromTwo) {
		Flux<String> streamOne = getStreamOne(linesFromOne);
		Flux<String> streamTwo = getStreamTwo(linesFromTwo);
		Flux<String> streamMerged = streamOne.mergeWith(streamTwo);
		streamMerged.subscribe((line)-> {System.out.println(line);});
	}
	
	private static void zipWith(List<String> linesFromOne,List<String> linesFromTwo){	
		Flux<String> streamOne = getStreamOne(linesFromOne);
		Flux<String> streamTwo = getStreamTwo(linesFromTwo);
		Flux<Tuple2<String,String>> streamZipped =streamOne.zipWith(streamTwo);
		streamZipped.subscribe((tuple)-> {System.out.println("key is "+tuple.getT1()+" ,text is "+tuple.getT2());});
	}
	
	private static Flux<String> getStreamOne(List<String> linesFromOne){
		Duration duration = Duration.ofMillis(300);
		return Flux.fromIterable(()->linesFromOne.iterator())	
				.delayElements(duration)
				.map((line) ->line.split("=")[0])
				.filter((line) -> line.length() > 0);
	} 
	
	private static Flux<String> getStreamTwo(List<String> linesFromTwo){
		return Flux.fromIterable(()->linesFromTwo.iterator())	
				.filter((line) -> line.length() > 0);
	} 

}
