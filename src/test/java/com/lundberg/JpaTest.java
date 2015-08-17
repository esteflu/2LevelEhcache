package com.lundberg;

import com.lundberg.config.AppConfig;
import com.lundberg.domain.User;
import com.lundberg.repositories.UserRepository;
import com.lundberg.services.UserService;
import com.sun.org.glassfish.external.statistics.Statistic;
import net.sf.ehcache.CacheManager;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.stat.Statistics;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class JpaTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private FactoryBean<SessionFactory> sessionFactory;

    private int counter;

    private Statistics statistics;

    @Before
    public void setup() throws Exception {
        CacheManager.getInstance().clearAll();
        statistics = getEnabledStatistics();
        userService.saveUser(createUser("Stefan", "Lundberg"));
        counter = 0;
    }

    @After
    public void teardown() {
        userRepository.deleteAll();
    }

    @Test
    public void find_user_by_lastname_in_second_level_cache_twice() throws Exception {

        findOneUserByLastName("Lundberg");
        printStatistics(statistics);

        findOneUserByLastName("Lundberg");
        printStatistics(statistics);

        findOneUserByLastName("Lundberg");
        printStatistics(statistics);

        assertEquals(2, statistics.getSecondLevelCacheHitCount());
    }

    @Ignore
    @Test
    public void find_user_by_firstname_in_second_level_cache() throws Exception {

        findOneUserByFirstName("Stefan");
        printStatistics(statistics);

        findOneUserByFirstName("Stefan");
        printStatistics(statistics);

        assertEquals(1, statistics.getSecondLevelCacheHitCount());
    }

    private User loadUserAndPrint(Statistics statistics, Session session, Long id) {
        User user = (User) session.load(User.class, id);
        printEntity(user);
        printStatistics(statistics);
        return user;
    }

    private User getUser(Session session, Long id) {
        return (User) session.get(User.class, id);
    }

    private User findOneUserByLastName(String lastname) {
        return userRepository.findByLastname(lastname).get(0);
    }

    private User findOneUserByFirstName(String firstname) {
        return userRepository.findByFirstname(firstname).get(0);
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
        System.out.println("Fetch nr: " + ++counter);
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
