package rxjava;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import httpclient.HttpClient;
import httpclient.HttpMethod;
import io.reactivex.Observable;

public class ProductDetailsRxJavaFromFuture implements IProductDetails {
	private static final String serviceBaseURL = "https://services.odata.org/Northwind/Northwind.svc/Products";
	private final static HttpClient httpClient = new HttpClient();
	private final ExecutorService executor=Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	public Observable<String> getProducts() {
		String productURL = serviceBaseURL + "?$format=json";
		//CompletableFuture.supplyAsync(supplier) could also be used
		CompletableFuture<String> completableFuture = new CompletableFuture<>();
		executor.submit(()->{
			try {
			String productsJSON = httpClient.request(productURL, HttpMethod.GET, Collections.<String, String>emptyMap(),
						Collections.<String, String>emptyMap(), null);
			 completableFuture.complete(productsJSON);
			}
			catch(Exception e) {
				e.printStackTrace();
				completableFuture.completeExceptionally(e);
			}
		});
		return Observable.fromFuture(completableFuture);
	}



	public Observable<String> getProductCount() {
		String countURL = serviceBaseURL + "/$count";
		//CompletableFuture.supplyAsync(supplier) could also be used
		CompletableFuture<String> completableFuture = new CompletableFuture<>();
		executor.submit(()->{
			try {
			String count = httpClient.request(countURL, HttpMethod.GET, Collections.<String, String>emptyMap(),
					Collections.<String, String>emptyMap(), null);
			completableFuture.complete(count);
			}
			catch(Exception e) {
				e.printStackTrace();
				completableFuture.completeExceptionally(e);
			}
		});
		return Observable.fromFuture(completableFuture);
	}
}
