package com.lundberg;

import com.lundberg.config.AppConfig;
import com.lundberg.domain.User;
import com.lundberg.repositories.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class JpaTest {

    @Autowired
    UserRepository userRepository;

    @Test
    public void find_user_by_lastname() {
        //Given
        userRepository.save(createUser("Stefan", "Lundberg"));
        userRepository.save(createUser("Theo", "Lundberg"));

        //When
        List<User> users = userRepository.findByLastname("Lundberg");

        //Then
        for (User user : users) {
            assertEquals("Lundberg", user.getLastname());
        }
        assertEquals(2, users.size());
    }

    private User createUser(String firstname, String lastname) {
        return new User(firstname, lastname);
    }

}
