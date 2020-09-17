package uk.co.huntersix.spring.rest.controller;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import uk.co.huntersix.spring.rest.model.Person;

import java.util.Collection;


@RunWith(SpringRunner.class)
@SpringBootTest
public class IntegrationTests {

    @Autowired
    private PersonController personController;

    @Test
    public void testAddAndNotAddDuplicateAndSearchByFirstNameLastNameAndSearchByLastName() {
        Person person = new Person();
        person.setFirstName("Bah");
        person.setLastName("Iz");

        personController.addPerson(person);

        Person p1 = personController.findByLastNameAndFirstName("Iz", "Bah");
        Assert.assertEquals("Bah", p1.getFirstName());
        Assert.assertEquals("Iz", p1.getLastName());

        try {
            personController.addPerson(person);
        } catch (Exception e) {

        }

        Iterable<Person> persons = personController.findByLastName("Iz");
        Assert.assertEquals(1, ((Collection<?>)persons).size());

        Person person2 = new Person();
        person2.setFirstName("Name");
        person2.setLastName("Iz");
        personController.addPerson(person2);

        Iterable<Person> persons2 = personController.findByLastName("Iz");
        Assert.assertEquals(2, ((Collection<?>)persons2).size());
    }


}
