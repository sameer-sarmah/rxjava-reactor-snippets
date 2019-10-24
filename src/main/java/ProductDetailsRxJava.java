import java.io.IOException;
import java.util.Collections;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import northwind.exception.CoreException;
import northwind.httpclient.HttpClient;
import northwind.httpclient.HttpMethod;

public class ProductDetailsRxJava {
	private static final String serviceBaseURL = "https://services.odata.org/Northwind/Northwind.svc/Products";
	final static HttpClient httpClient = new HttpClient();

	public static Observable<String> getProducts() {
		String productURL = serviceBaseURL + "?$format=json";
		return Observable.create((emitter)->{
			try {
			String productsJSON = httpClient.request(productURL, HttpMethod.GET, Collections.<String, String>emptyMap(),
						Collections.<String, String>emptyMap(), null);		
			emitter.onNext(productsJSON);
			emitter.onComplete();
			}
			catch(Exception e) {
				emitter.onError(e);
			}
		});
	}



	public static Observable<String> getProductCount() {
		String countURL = serviceBaseURL + "/$count";
		return Observable.create((emitter)->{
			try {
			String count = httpClient.request(countURL, HttpMethod.GET, Collections.<String, String>emptyMap(),
					Collections.<String, String>emptyMap(), null);
			emitter.onNext(count);
			emitter.onComplete();
			}
			catch(Exception e) {
				emitter.onError(e);
			}
		});
	}
}
