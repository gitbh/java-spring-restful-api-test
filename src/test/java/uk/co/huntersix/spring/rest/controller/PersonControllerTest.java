package uk.co.huntersix.spring.rest.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import uk.co.huntersix.spring.rest.model.Person;
import uk.co.huntersix.spring.rest.referencedata.PersonDataService;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@WebMvcTest(PersonController.class)
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonDataService personDataService;

    @Test
    public void shouldReturnPersonFromService() throws Exception {
        when(personDataService.findPerson(any(), any())).thenReturn(new Person("Mary", "Smith"));
        this.mockMvc.perform(get("/person/smith/mary"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("id").exists())
            .andExpect(jsonPath("firstName").value("Mary"))
            .andExpect(jsonPath("lastName").value("Smith"));
    }

    @Test
    public void shouldReturnHttpNotFoundFromService() throws Exception {
        when(personDataService.findPerson(any(), any())).thenThrow(new IndexOutOfBoundsException());
        this.mockMvc.perform(get("/person/sdfsdf22/sdf22"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("id").doesNotExist())
                .andExpect(jsonPath("firstName").doesNotExist())
                .andExpect(jsonPath("lastName").doesNotExist());
    }

    @Test
    public void shouldReturnSinglePersonFromSearchByLastNameService() throws Exception {
        List<Person> persons = Arrays.asList(new Person("Mary", "Smith"));
        when(personDataService.findByLastName(any())).thenReturn(persons);

        this.mockMvc.perform(get("/person-by-lastname/Smith"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].lastName", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is("Mary")))
                .andExpect(jsonPath("$[0].lastName", is("Smith")));
    }

    @Test
    public void shouldReturnMultiPersonFromSearchByLastNameService() throws Exception {
        List<Person> persons = Arrays.asList(new Person("Ada", "Sir"), new Person("Name", "Sir"));
        when(personDataService.findByLastName(any())).thenReturn(persons);

        this.mockMvc.perform(get("/person-by-lastname/Sir"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].lastName", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName", is("Ada")))
                .andExpect(jsonPath("$[1].firstName", is("Name")))
                .andExpect(jsonPath("$[0].lastName", is("Sir")))
                .andExpect(jsonPath("$[1].lastName", is("Sir")));
    }

    @Test
    public void shouldReturnEmptyFromSearchByLastNameService() throws Exception {
        List<Person> persons = Arrays.asList();
        when(personDataService.findByLastName(any())).thenReturn(persons);

        this.mockMvc.perform(get("/person-by-lastname/asdfsdf"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].lastName", hasSize(0)));
    }


    @Test
    public void shouldAddNewPerson() throws Exception {
        when(personDataService.isPersonPresent(any())).thenReturn(false);
        when(personDataService.addPerson(any())).thenReturn(new Person("Adam", "Sir"));

        mockMvc.perform(post("/person/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"firstName\": \"Adam\", \"lastName\": \"Sir\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.firstName").value("Adam"))
                .andExpect(jsonPath("$.lastName").value("Sir"));
    }

    @Test
    public void shouldNotAddDuplicatePerson() throws Exception {
        when(personDataService.isPersonPresent(any())).thenReturn(true);

        mockMvc.perform(post("/person/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"firstName\": \"Adam\", \"lastName\": \"Sir\" }")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(containsString("Person exists.")));
    }

}