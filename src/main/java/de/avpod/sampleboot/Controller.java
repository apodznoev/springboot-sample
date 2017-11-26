package de.avpod.sampleboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by apodznoev
 * date 21.11.2017.
 */
@RestController
@org.springframework.stereotype.Controller
public class Controller {

    @Autowired
    CustomRepository repository;

    @RequestMapping("/hello")
    public String helloWorld(@RequestParam(value = "name", required = false) String name) {
        return "Hello " + name + "!";
    }

    @RequestMapping("/findCustomerCard")
    public int findCardNumber(@RequestParam(value = "fullName") String name) {
        Customer customer = repository.findByFullName(name);
        if (customer != null)
            return customer.cardNumber;
        return -1;
    }

    @PreAuthorize("hasRole('ROLE_DEVELOPERS')")
    @RequestMapping(value = "/persistCustomer", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public CustomerResponse postSomething(Customer entity) {
        Customer savedEntity = repository.save(entity);
        return new CustomerResponse(savedEntity.fullName + "_" + savedEntity.id);
    }


    public class CustomerResponse {
        private final String responseText;

        CustomerResponse(String response) {
            this.responseText = response;
        }

        public String getResponseText() {
            return responseText;
        }

    }
}
