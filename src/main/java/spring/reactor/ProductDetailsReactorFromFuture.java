package spring.reactor;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import httpclient.HttpClient;
import httpclient.HttpMethod;
import reactor.core.publisher.Mono;

public class ProductDetailsReactorFromFuture implements IProductDetails{
	private static final String serviceBaseURL = "https://services.odata.org/Northwind/Northwind.svc/Products";
	final static HttpClient httpClient = new HttpClient();

	public  Mono<String> getProducts() {
		String productURL = serviceBaseURL + "?$format=json";
		CompletableFuture<String> future = CompletableFuture.supplyAsync(()->{
			String productsJSON = "[]";
			try {
				productsJSON = httpClient.request(productURL, HttpMethod.GET, Collections.<String, String>emptyMap(),
						Collections.<String, String>emptyMap(), null);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return productsJSON;
		});
		return Mono.fromFuture(future);
	}

	public  Mono<String> getProductCount() {
		String countURL = serviceBaseURL + "/$count";
		CompletableFuture<String> future = CompletableFuture.supplyAsync(()->{
			String count = "";
			try {
				count = httpClient.request(countURL, HttpMethod.GET, Collections.<String, String>emptyMap(),
						Collections.<String, String>emptyMap(), null);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return count;
		});
		return Mono.fromFuture(future);
	}
}
