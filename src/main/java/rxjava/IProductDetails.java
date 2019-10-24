package rxjava;
import io.reactivex.Observable;

public interface IProductDetails {
	public Observable<String> getProducts();
	public Observable<String> getProductCount(); 
}
