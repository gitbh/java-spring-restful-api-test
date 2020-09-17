package uk.co.huntersix.spring.rest.referencedata;

import org.springframework.stereotype.Service;
import uk.co.huntersix.spring.rest.model.Person;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonDataService {

    public static List<Person> PERSON_DATA = new ArrayList<Person>(Arrays.asList(
        new Person("Mary", "Smith"),
        new Person("Brian", "Archer"),
        new Person("Adam", "Archer"),
        new Person("Collin", "Brown")
    ));

    public Person findPerson(String lastName, String firstName) {
        return PERSON_DATA.stream()
            .filter(p -> p.getFirstName().equalsIgnoreCase(firstName)
                && p.getLastName().equalsIgnoreCase(lastName))
            .collect(Collectors.toList()).get(0);
    }

    public Iterable<Person> findByLastName(String lastName) {
        return PERSON_DATA.stream()
                .filter(p -> p.getLastName().equalsIgnoreCase(lastName))
                .collect(Collectors.toList());
    }

    public boolean isPersonPresent(Person person) {
        int size = PERSON_DATA.stream()
                .filter(p -> p.getFirstName().equalsIgnoreCase(person.getFirstName())
                        && p.getLastName().equalsIgnoreCase(person.getLastName()))
                .collect(Collectors.toList()).size();
        return size != 0;
    }

    public Person addPerson(Person person) {
        Person p = new Person(person.getFirstName(), person.getLastName());
        PERSON_DATA.add(p);
        return p;
    }
}
