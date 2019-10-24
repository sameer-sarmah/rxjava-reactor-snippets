package spring.reactor;
import reactor.core.publisher.Mono;

public interface IProductDetails {
	public Mono<String> getProducts();
	public Mono<String> getProductCount(); 
}
