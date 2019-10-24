import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import io.reactivex.Observable;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.subjects.PublishSubject;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

public class ReactorSnippets {

	public static void main(String[] args) {
		InputStream inputStreamOne = RxjavaSnippets.class.getClassLoader().getResourceAsStream("bundleOne.properties");
		BufferedReader readerOne = new BufferedReader(new InputStreamReader(inputStreamOne));
		List<String> linesFromOne = readerOne.lines().collect(Collectors.toList());
		
		InputStream inputStreamTwo = RxjavaSnippets.class.getClassLoader().getResourceAsStream("bundleTwo.txt");
		BufferedReader readerTwo = new BufferedReader(new InputStreamReader(inputStreamTwo));
		List<String> linesFromTwo = readerTwo.lines().collect(Collectors.toList());


		
		//sequentialHttpCall();
		parallelHttpCall();

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static void parallelHttpCall() {
		Mono<String> productsObs =  ProductDetailsReactor.getProducts();
		Mono<String> productCountObs =  ProductDetailsReactor.getProductCount();
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
	
	private static void sequentialHttpCall() {
		Mono<String> productsObs =  ProductDetailsReactor.getProducts();
		Mono<String> productCountObs =  ProductDetailsReactor.getProductCount();
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
		Flux<String> streamOne = getObservableOne(linesFromOne);
		Flux<String> streamTwo = getObservableTwo(linesFromTwo);
		Flux<String>  streamConcatenated = streamOne.concatWith(streamTwo);
		streamConcatenated.subscribe((line)->{System.out.println("The key is "+line);});
	}
	
	private static void mergeWith(List<String> linesFromOne,List<String> linesFromTwo) {
		Flux<String> streamOne = getObservableOne(linesFromOne);
		Flux<String> streamTwo = getObservableTwo(linesFromTwo);
		Flux<String> streamMerged = streamOne.mergeWith(streamTwo);
		streamMerged.subscribe((line)-> {System.out.println(line);});
	}
	
	private static void zipWith(List<String> linesFromOne,List<String> linesFromTwo){	
		Flux<String> streamOne = getObservableOne(linesFromOne);
		Flux<String> streamTwo = getObservableTwo(linesFromTwo);
		Flux<Tuple2<String,String>> streamZipped =streamOne.zipWith(streamTwo);
		streamZipped.subscribe((tuple)-> {System.out.println("key is "+tuple.getT1()+" ,text is "+tuple.getT2());});
	}
	
	private static Flux<String> getObservableOne(List<String> linesFromOne){
		Duration duration = Duration.ofMillis(300);
		return Flux.fromIterable(()->linesFromOne.iterator())	
				.delayElements(duration)
				.map((line) ->line.split("=")[0])
				.filter((line) -> line.length() > 0);
	} 
	
	private static Flux<String> getObservableTwo(List<String> linesFromTwo){
		return Flux.fromIterable(()->linesFromTwo.iterator())	
				.filter((line) -> line.length() > 0);
	} 

}
