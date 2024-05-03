package ori.config;

import org.springframework.context.annotation.Configuration;

import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@Configuration
public class PaypalConfig {
	public static String SUCCESS_URL ="http://localhost:8888/payment/paypal/success";
	public static String CANCEL_URL ="http://localhost:8888/payment/paypal/cancel";
	@Value("${paypal.client.id}")
	private String clientId;
	@Value("${paypal.client.secret}")
	private String clientSecret;
	@Value("${paypal.mode}")
	private String mode;

	@Bean
	public Map<String, String> paypalSdkConfig() {
		Map<String, String> configMap = new HashMap<>();
		configMap.put("mode", mode);
		return configMap;
	}

	@Bean
	public OAuthTokenCredential oAuthTokenCredential() {
		return new OAuthTokenCredential(clientId, clientSecret, paypalSdkConfig());
	}

	@Bean
	public APIContext apiContext() throws PayPalRESTException {
		APIContext context = new APIContext(oAuthTokenCredential().getAccessToken());
		context.setConfigurationMap(paypalSdkConfig());
		return context;
	}

}
