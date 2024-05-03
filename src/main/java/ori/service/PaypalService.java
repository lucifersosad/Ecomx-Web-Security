package ori.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Details;
import com.paypal.api.payments.Item;
import com.paypal.api.payments.ItemList;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

import ori.entity.Cart;



@Service
public class PaypalService {
	@Autowired
	private APIContext apiContext;
	@Autowired
	ICartService cartService;
	@Autowired
	IUserService userService;
	public static final double unit = (double) 0.000041;
	public Payment createPayment(
			Integer userId,
			Double discount,
			String currency, 
			String method,
			String intent,
			String description, 
			String cancelUrl, 
			String successUrl) throws PayPalRESTException{

		List<Cart> carts = cartService.findByUserId(userId);
		List<Item> items = new ArrayList<Item>();
		Double total = 0.0;
		for (Cart cart : carts) {		
			Item item = new Item();
			float sale = cart.getProduct().getSale();
			Double price = (Double) (unit * cart.getProduct().getPrice() * 1000 * (100 - sale) / 100.0) * (1-discount);
			price = new BigDecimal(price).setScale(2, RoundingMode.HALF_UP).doubleValue();
		    int quantity = cart.getQuantity();
		    total += quantity * price;
			item.setName(cart.getProduct().getName()).setQuantity(String.valueOf(quantity)).setCurrency("USD").setPrice(price.toString());
			items.add(item);
		}
		
		total = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP).doubleValue();

		// Adding items to itemList
		ItemList itemList = new ItemList();
		itemList.setItems(items);
	
		Details details = new Details();
		details.setSubtotal(total.toString());
		
		// Payment amount
		Amount amount = new Amount();
		amount.setCurrency(currency);		
		amount.setTotal(total.toString());
		
		// Transaction information
		Transaction transaction = new Transaction();
		transaction.setDescription(description);
		transaction.setAmount(amount);
		transaction.setItemList(itemList);
		List<Transaction> transactions = new ArrayList<>();
		transactions.add(transaction);
		
		Payer payer = new Payer();
		payer.setPaymentMethod(method.toString());

		Payment payment = new Payment();
		payment.setIntent(intent.toString());
		payment.setPayer(payer);  
		payment.setTransactions(transactions);
		RedirectUrls redirectUrls = new RedirectUrls();
		redirectUrls.setCancelUrl(cancelUrl);
		redirectUrls.setReturnUrl(successUrl);
		payment.setRedirectUrls(redirectUrls);

		return payment.create(apiContext);
	}
	
	public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException{
		Payment payment = new Payment();
		payment.setId(paymentId);
		PaymentExecution paymentExecute = new PaymentExecution();
		paymentExecute.setPayerId(payerId);
		return payment.execute(apiContext, paymentExecute);
	}

}