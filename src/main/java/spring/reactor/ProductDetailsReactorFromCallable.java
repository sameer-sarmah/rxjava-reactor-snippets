package spring.reactor;

import java.util.Collections;

import httpclient.HttpClient;
import httpclient.HttpMethod;
import reactor.core.publisher.Mono;

public class ProductDetailsReactorFromCallable implements IProductDetails {
	private static final String serviceBaseURL = "https://services.odata.org/Northwind/Northwind.svc/Products";
	final static HttpClient httpClient = new HttpClient();

	public Mono<String> getProducts() {
		String productURL = serviceBaseURL + "?$format=json";
		return Mono.fromCallable(() -> {
			String productsJSON = "[]";
			try {
				productsJSON = httpClient.request(productURL, HttpMethod.GET, Collections.<String, String>emptyMap(),
						Collections.<String, String>emptyMap(), null);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return productsJSON;
		});
	}

	public Mono<String> getProductCount() {
		String countURL = serviceBaseURL + "/$count";
		return Mono.fromCallable(() -> {
			String count = "";
			try {
				count = httpClient.request(countURL, HttpMethod.GET, Collections.<String, String>emptyMap(),
						Collections.<String, String>emptyMap(), null);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return count;
		});
	}
}
