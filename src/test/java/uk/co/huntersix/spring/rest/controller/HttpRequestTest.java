package uk.co.huntersix.spring.rest.controller;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import uk.co.huntersix.spring.rest.model.Person;
import uk.co.huntersix.spring.rest.referencedata.PersonDataService;

import javax.validation.ValidationException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HttpRequestTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldReturnPersonDetails() throws Exception {
        assertThat(
            this.restTemplate.getForObject(
                "http://localhost:" + port + "/person/smith/mary",
                String.class
            )
        ).contains("Mary");
    }

    @Test
    public void shouldReturnPersonNotFound() {
        ResponseEntity<String> responseEntity = this.restTemplate.getForEntity("http://localhost:" + port + "/person/asd23/fddsfdsf22",
                String.class);
        Assert.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void shouldReturnSinglePersonSearchBySurname() {
        String personUrl
                = "http://localhost:" + port + "/person-by-lastname/Smith";
        Person[] persons
                = restTemplate.getForObject(personUrl, Person[].class);
        Assertions.assertThat(persons).extracting(Person::getLastName).containsOnly("Smith");
        Assertions.assertThat(persons).extracting(Person::getFirstName).containsOnly("Mary");
        Assert.assertEquals(1, persons.length);
    }

    @Test
    public void shouldReturnMultiPersonSearchBySurname() {
        String personUrl
                = "http://localhost:" + port + "/person-by-lastname/Archer";
        Person[] persons
                = restTemplate.getForObject(personUrl, Person[].class);
        Assertions.assertThat(persons).extracting(Person::getLastName).containsOnly("Archer");
        Assertions.assertThat(persons).extracting(Person::getFirstName).contains("Brian", "Adam");
        Assert.assertEquals(2, persons.length);
    }


    @Test
    public void shouldReturnEmptySearchBySurname() {
        String personUrl
                = "http://localhost:" + port + "/person-by-lastname/asdfsdf";
        Person[] persons
                = restTemplate.getForObject(personUrl, Person[].class);
        Assert.assertEquals(0, persons.length);
    }

    @Test
    public void shouldAddNewPerson() {
        String personUrl
                = "http://localhost:" + port + "/person/";
        Person person = new Person();
        person.setFirstName("Ad22");
        person.setLastName("Sur33");
        ResponseEntity<Person> entity = restTemplate.postForEntity(personUrl, person, Person.class);

        Assert.assertEquals("Ad22", entity.getBody().getFirstName());
        Assert.assertEquals("Sur33", entity.getBody().getLastName());
    }

    @Test
    public void shouldAddNewPersonAndNotAddDuplicatePerson() {
        String personUrl
                = "http://localhost:" + port + "/person/";
        Person person = new Person();
        person.setFirstName("Ad33");
        person.setLastName("Sur44");
        ResponseEntity<Person> entity = restTemplate.postForEntity(personUrl, person, Person.class);

        Assert.assertEquals("Ad33", entity.getBody().getFirstName());
        Assert.assertEquals("Sur44", entity.getBody().getLastName());

        ResponseEntity<Person> entity2 = restTemplate.postForEntity(personUrl, person, Person.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, entity2.getStatusCode());
    }
}