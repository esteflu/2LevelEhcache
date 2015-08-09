package com.lundberg;

import com.lundberg.config.AppConfig;
import com.lundberg.domain.User;
import com.lundberg.repositories.UserRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.stat.Statistics;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class JpaTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FactoryBean<SessionFactory> sessionFactory;

    @Before
    public void populateDB() {
        userRepository.save(createUser("Stefan", "Lundberg"));
        userRepository.save(createUser("Theo", "Lundberg"));
    }

    @After
    public void cleanDB() {
        userRepository.deleteAll();
    }

    @Test
    public void find_user_in_second_level_cache_twice() throws Exception {
        Statistics statistics = getEnabledStatistics();
        Session session = sessionFactory.getObject().openSession();
        Transaction transaction = session.beginTransaction();

        User user = loadUserAndPrint(statistics, session, 1L);

        session.evict(user); //evicts from first level cache

        user = loadUserAndPrint(statistics, session, 1L);

        session.evict(user); //evicts from first level cache

        loadUserAndPrint(statistics, session, 1L);

        assertEquals(1, statistics.getEntityFetchCount());
        assertEquals(1, statistics.getSecondLevelCacheMissCount());
        assertEquals(2, statistics.getSecondLevelCacheHitCount());

        transaction.commit();

    }

    private User loadUserAndPrint(Statistics statistics, Session session, Long id) {
        User user = (User) session.load(User.class, id);
        printEntity(user);
        printStatistics(statistics);
        return user;
    }

    private User loadUser(Session session, Long id) {
        return (User) session.load(User.class, id);
    }

    private User createUser(String firstname, String lastname) {
        return new User(firstname, lastname);
    }

    private Statistics getEnabledStatistics() throws Exception {
        Statistics statistics = sessionFactory.getObject().getStatistics();
        statistics.setStatisticsEnabled(true);
        return statistics;
    }

    private void printStatistics(Statistics statistics) {
        System.out.println("Fetch count from db: " + statistics.getEntityFetchCount());
        System.out.println("Fetch count from second level cache: " + statistics.getSecondLevelCacheHitCount());
        System.out.println("Miss hit from second level cache: " + statistics.getSecondLevelCacheMissCount()+ "\n");

    }

    private void printEntity(User user) {
        System.out.println("User id: " + user.getId() +
                ", User firstname: " + user.getFirstname() +
                ", User lastname: " + user.getLastname());
    }
}
