package rxjava;
import java.util.Collections;

import httpclient.HttpClient;
import httpclient.HttpMethod;
import io.reactivex.Observable;

public class ProductDetailsRxJava implements IProductDetails{
	private static final String serviceBaseURL = "https://services.odata.org/Northwind/Northwind.svc/Products";
	private final static HttpClient httpClient = new HttpClient();

	public Observable<String> getProducts() {
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



	public Observable<String> getProductCount() {
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
