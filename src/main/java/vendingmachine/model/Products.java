package vendingmachine.model;

import static vendingmachine.constant.Constant.*;
import static vendingmachine.constant.ErrorMessage.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

public class Products {

	public List<Product> products = new ArrayList<>();

	public Products(String requestProduct) {
		String[] productsSplitBySemeColon = requestProduct.split(SEME_COLON);
		for (String product : productsSplitBySemeColon) {
			String[] productSplitByFormat = product.replace(LEFT_PARENTHESIS, BLANK)
				.replace(RIGHT_PARENTHESIS, BLANK)
				.split(COMMA);
			products.add(new Product(productSplitByFormat));
		}
	}

	public int getMinimumPrice() {
		Comparator<Product> comparatorByPrice = Comparator.comparingInt(Product::getPrice);
		return products.stream()
			.min(comparatorByPrice)
			.orElseThrow(NoSuchElementException::new).getPrice();
	}

	public boolean isOverZeroAllProductCount() {
		return 0 < products.stream().mapToInt(Product::getQuantity).sum();
	}

	public void purchase(String productName, Money inputMoney) {
		Product product = findByName(productName);
		product.purchase();
		inputMoney.subtractMoney(product.getPrice());
	}

	private Product findByName(String productName) {
		return products.stream()
			.filter(product -> product.getName().equals(productName))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException(PRODUCT_NOT_EXIST_MSG));
	}

	public boolean canSell(String productName, Money inputMoney) {
		Product product = findByName(productName);
		return product.isOverZeroQuantity() && product.isPurchase(inputMoney.getMoney());

	}
}
