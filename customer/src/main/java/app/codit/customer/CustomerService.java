package app.codit.customer;

import app.codit.amqp.RabbitMQMessageProducer;
import app.codit.clients.fraud.FraudCheckResponse;
import app.codit.clients.fraud.FraudClient;
import app.codit.clients.notification.NotificationClient;
import app.codit.clients.notification.NotificationRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
public class CustomerService {
        private final CustomerRepository customerRepository;
        private final RestTemplate restTemplate;
        private final FraudClient fraudClient;
        private final RabbitMQMessageProducer producer;

    public void registerCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        Customer customer = Customer.builder()
                .firstName(customerRegistrationRequest.firstName())
                .lastName(customerRegistrationRequest.lastName())
                .email(customerRegistrationRequest.email())
                .build();

        customerRepository.saveAndFlush(customer);

        FraudCheckResponse fraudCheckResponse = fraudClient.isFraudster(customer.getId());

        if(fraudCheckResponse.isFraudster()) {
            throw new IllegalStateException("Fraudster");
        }

        NotificationRequest notificationRequest = new NotificationRequest(customer.getId(), customer.getEmail(),
                "Welcome "+customer.getFirstName()+" "+customer.getLastName());

        producer.publish(
                notificationRequest,
                "internal.exchange",
                "internal.notification.routing-key"
        );
    }
}
