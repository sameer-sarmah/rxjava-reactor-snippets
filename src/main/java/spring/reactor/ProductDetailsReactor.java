package spring.reactor;
import java.util.Collections;

import httpclient.HttpClient;
import httpclient.HttpMethod;
import reactor.core.publisher.Mono;

public class ProductDetailsReactor implements IProductDetails{
	private static final String serviceBaseURL = "https://services.odata.org/Northwind/Northwind.svc/Products";
	final static HttpClient httpClient = new HttpClient();

	public  Mono<String> getProducts() {
		String productURL = serviceBaseURL + "?$format=json";
		return Mono.create((emitter)->{
			try {
			String productsJSON = httpClient.request(productURL, HttpMethod.GET, Collections.<String, String>emptyMap(),
						Collections.<String, String>emptyMap(), null);
			emitter.success(productsJSON);
			}
			catch(Exception e) {
				emitter.error(e);
			}
		});
	}

	public  Mono<String> getProductCount() {
		String countURL = serviceBaseURL + "/$count";
		return Mono.create((emitter)->{
			try {
			String count = httpClient.request(countURL, HttpMethod.GET, Collections.<String, String>emptyMap(),
					Collections.<String, String>emptyMap(), null);
			emitter.success(count);
			}
			catch(Exception e) {
				emitter.error(e);
			}
		});
	}
}
