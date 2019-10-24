package rxjava;
import java.util.Collections;

import httpclient.HttpClient;
import httpclient.HttpMethod;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class ProductDetailsRxJavaFromCallable implements IProductDetails {

	private static final String serviceBaseURL = "https://services.odata.org/Northwind/Northwind.svc/Products";
	private final static HttpClient httpClient = new HttpClient();

	public Observable<String> getProducts() {
		String productURL = serviceBaseURL + "?$format=json";
		return Observable.fromCallable(()->{
			String productsJSON ="[]";
			try {
			productsJSON = httpClient.request(productURL, HttpMethod.GET, Collections.<String, String>emptyMap(),
						Collections.<String, String>emptyMap(), null);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			return productsJSON;
		}).subscribeOn(Schedulers.io());
	}



	public Observable<String> getProductCount() {
		String countURL = serviceBaseURL + "/$count";
		return Observable.fromCallable(()->{
			String count = "0";
			try {
			 count = httpClient.request(countURL, HttpMethod.GET, Collections.<String, String>emptyMap(),
					Collections.<String, String>emptyMap(), null);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			return count;
		});
	}
}
