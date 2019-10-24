import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import io.reactivex.Observable;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.subjects.PublishSubject;

public class RxjavaSnippets {
	public static void main(String... strings) throws InterruptedException {

		InputStream inputStreamOne = RxjavaSnippets.class.getClassLoader().getResourceAsStream("bundleOne.properties");
		BufferedReader readerOne = new BufferedReader(new InputStreamReader(inputStreamOne));
		List<String> linesFromOne = readerOne.lines().collect(Collectors.toList());

		InputStream inputStreamTwo = RxjavaSnippets.class.getClassLoader().getResourceAsStream("bundleTwo.txt");
		BufferedReader readerTwo = new BufferedReader(new InputStreamReader(inputStreamTwo));
		List<String> linesFromTwo = readerTwo.lines().collect(Collectors.toList());

		//generate(linesFromOne);
		
		//IProductDetails productDetails = new ProductDetailsRxJava();
		//IProductDetails productDetails =new ProductDetailsRxJavaFromCallable();
		IProductDetails productDetails = new ProductDetailsRxJavaFromFuture();
		//sequentialHttpCall();
		parallelHttpCall(productDetails);
		
		Thread.sleep(10000);
	}
	
	private static void parallelHttpCall(IProductDetails productDetails) {
		Observable<String> productsObs =  productDetails.getProducts();
		Observable<String> productCountObs =  productDetails.getProductCount();
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
		Observable<String> productsObs =  productDetails.getProducts();
		Observable<String> productCountObs =  productDetails.getProductCount();
		System.out.println("Non blocking");
		productCountObs.subscribe((count)->{
			System.out.println("Number of products are "+count);
			productsObs.subscribe((productsJSON)-> {
				double price = Util.extractUnitPriceOfChai(productsJSON);
				System.out.println("price of chai is "+price);
			});
			
		});
	}
	
	private static void create(List<String> lines){
		Observable<String> observable = Observable.create((emitter)->{
			try {
			Thread.sleep(3000);	
			emitter.onNext(lines.get(0));
			emitter.onComplete();
			}
			catch(Exception e) {
				emitter.onError(e);
			}
		});
		System.out.println("Non blocking create");
		observable.subscribe((line)-> {System.out.println(line);});
		System.out.println("Blocking subscribe");
	}
	
	private static void generate(List<String> lines)
	{
		Observable<String> observable = Observable.generate((emitter)->{
			try {
				Thread.sleep(3000);	
			emitter.onNext(lines.get(0));
			emitter.onComplete();
			}
			catch(Exception e) {
				emitter.onError(e);
			}
		});
		System.out.println("Non blocking generate");
		observable.subscribe((line)-> {System.out.println(line);});
		System.out.println("Blocking subscribe");
	}
	
	private static void subject(List<String> lines)
	{
		PublishSubject<String> subject = PublishSubject.create();
		subject.subscribe((line)->{
			if(line.contains("=")) {
				String[] parts = line.split("=");
				System.out.println("The key is '"+parts[0]+"',and the value is '"+parts[1]+"'");
			}
		});
		lines.stream().forEach((line)->subject.onNext(line));
	}
	
	private static void connectableObservable(Observable<String> stream)
	{
		ConnectableObservable<String> connectableObservable = stream.publish();
		connectableObservable.subscribe((line)-> {System.out.println(line);});
		connectableObservable.doOnError(error -> error.printStackTrace());
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		connectableObservable.connect();
	}
	
	private static void map(List<String> lines) {
		Observable<String> observable = Observable.fromIterable(()->lines.iterator())
			.delay(500, TimeUnit.MILLISECONDS)
			.map((line) ->line.split("=")[0])
			.filter((line) -> line.length() >= 4)
			.doOnEach(System.out::println);
		observable.subscribe((line)-> {System.out.println(line);});
	}
	
	
	private static void concatWith(List<String> linesFromOne,List<String> linesFromTwo) {
		Observable<String> streamOne = getObservableOne(linesFromOne);	
		Observable<String> streamTwo = getObservableTwo(linesFromTwo);
		Observable<String> streamConcatenated = streamOne.concatWith(streamTwo);
		streamConcatenated.subscribe((line)->{System.out.println("The key is "+line);});
	}
	
	private static void mergeWith(List<String> linesFromOne,List<String> linesFromTwo) {
		Observable<String> streamOne = getObservableOne(linesFromOne);
		Observable<String> streamTwo = getObservableTwo(linesFromTwo);
		Observable<String> streamMerged = streamOne.mergeWith(streamTwo);
		streamMerged.subscribe((line)->{System.out.println("The key is "+line);});
	}
	
	private static void zipWith(List<String> linesFromOne,List<String> linesFromTwo)
	{	Observable<String> streamOne = getObservableOne(linesFromOne);
		Observable<String> streamTwo = getObservableTwo(linesFromTwo);
		Observable<String> streamZipped = streamOne.zipWith(streamTwo,(key,text)->{
			return key+" "+text;
		});
		streamZipped.subscribe((line)->{System.out.println(line);});
	}
	
	private static Observable<String> getObservableOne(List<String> linesFromOne){
		return Observable.fromIterable(()->linesFromOne.iterator())
				.map((line) ->line.split("=")[0])
				.concatMap((line) ->Observable.just(line).delay(200, TimeUnit.MILLISECONDS))
				.filter((line) -> line.length() >= 4);
	} 
	
	private static Observable<String> getObservableTwo(List<String> linesFromTwo){
		return Observable.fromIterable(()->linesFromTwo.iterator())
				.flatMap((line) ->Observable.just(line).delay(500, TimeUnit.MILLISECONDS))
				.filter((line) -> line.length() > 0);
	} 

}
