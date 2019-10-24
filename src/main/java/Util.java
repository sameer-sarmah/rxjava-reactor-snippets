import java.io.IOException;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class Util {
	public static double extractUnitPriceOfChai(String jsonString)  {
		double licensedLimit = 0;
		JsonNode parent;
		try {
			parent = new ObjectMapper().readTree(jsonString);
			JsonNode productsNode = parent.get("value");
			if (productsNode instanceof ArrayNode) {
				ArrayNode products = (ArrayNode) productsNode;
				JsonNode chai = StreamSupport.stream(products.spliterator(), false).filter((node) -> {
					return node.get("ProductName").asText().equalsIgnoreCase("Chai");
				}).findFirst().orElseThrow(() -> new IOException());
				if (chai != null) {
					return chai.get("UnitPrice").asDouble();
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return licensedLimit;
	}
}
