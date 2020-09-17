package uk.co.huntersix.spring.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import uk.co.huntersix.spring.rest.model.Person;
import uk.co.huntersix.spring.rest.referencedata.PersonDataService;

import javax.validation.Valid;

@RestController
public class PersonController {
    private PersonDataService personDataService;

    public PersonController(@Autowired PersonDataService personDataService) {
        this.personDataService = personDataService;
    }

    @GetMapping("/person/{lastName}/{firstName}")
    public Person findByLastNameAndFirstName(@PathVariable(value="lastName") String lastName,
                         @PathVariable(value="firstName") String firstName) {
        try {
            return personDataService.findPerson(lastName, firstName);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Person Not Found"
            );
        }
    }

    @GetMapping("/person-by-lastname/{lastName}")
    Iterable<Person> findByLastName(@PathVariable(value="lastName") String lastName) {
        return personDataService.findByLastName(lastName);
    }

    @PostMapping("/person")
    ResponseEntity<Person> addPerson(@RequestBody Person person) {
        if (!personDataService.isPersonPresent(person)) {
            return new ResponseEntity(personDataService.addPerson(person), HttpStatus.OK);
        }
        else {
            //return new ResponseEntity(person, HttpStatus.BAD_REQUEST);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Person exists."
            );
        }
    }
}