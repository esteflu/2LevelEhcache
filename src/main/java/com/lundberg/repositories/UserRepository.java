package com.lundberg.repositories;


import com.lundberg.domain.User;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    List<User> findByLastname(String lastname);

    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    List<User> findByFirstname(String firstname);
}
