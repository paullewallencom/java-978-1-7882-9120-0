package org.jee8ng.users.boundary;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.validation.Valid;
import org.jee8ng.users.entity.User;

/**
 *
 * @author prashantp.org
 */
@Stateless
public class UsersService {

    @PersistenceContext
    private EntityManager em;
    
    public User isValid(String username, String password) {
        TypedQuery<User> query = em.createQuery("SELECT u from User u WHERE u.credential.username = :username and u.credential.password = :password",
                User.class);
        query.setParameter("username", username);
        query.setParameter("password", password);
        try {
            return query.getSingleResult();
        }catch(NoResultException nre) { }
        
        return null;
    }

    public Set<User> getAll() {
        List<User> list = em.createQuery("FROM User u", User.class)
                .getResultList();
        return new LinkedHashSet(list);
    }

    public void add(@Valid User newUser) {
        em.persist(newUser);
    }

    public boolean update(User updated) {
        User found = em.find(User.class, updated.getId());
        if (found != null) {
            em.merge(updated);
            return true;
        }
        return false;
    }

    public void remove(Long id) {
        Query query = em.createQuery("DELETE FROM User u WHERE u.id = :id");
        query.setParameter("id", id)
                .executeUpdate();
    }

    public Optional<User> get(Long id) {
        User found = em.find(User.class, id);
        return found != null ? Optional.of(found) : Optional.empty();
    }

    public List<String> getNames() {
        TypedQuery<User> query = em.createQuery("SELECT u from User u",
                User.class);
        List<String> names = query.getResultStream().map(User::getName)
                .collect(Collectors.toList());
        return names;
    }

}
