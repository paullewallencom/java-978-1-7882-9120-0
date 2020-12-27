package org.jee8ng.issues.boundary;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.Valid;
import org.jee8ng.issues.entity.Issue;
import org.jee8ng.issues.entity.User;

/**
 *
 * @author prashantp.org
 */
@Stateless
public class IssuesService {

    @PersistenceContext
    private EntityManager em;

    public Set<Issue> getAll() {
        List<Issue> list = em.createQuery("FROM Issue i", Issue.class)
                .getResultList();
        return new LinkedHashSet(list);
    }

    public void add(@Valid Issue newIssue) {
        newIssue.setCreated(LocalDateTime.now());
        User owner = em.find(User.class, newIssue.getAssignedTo().getId());
        
        owner.getIssues().add(newIssue);
        newIssue.setAssignedTo(owner);
        
        em.persist(newIssue);
    }

    public boolean update(Issue updated) {
        Issue found = em.find(Issue.class, updated.getId());
        if (found != null) {
            found.setModified(LocalDateTime.now());
            em.merge(updated);            
            return true;
        }
        return false;
    }

    public void remove(Long id) {
        Query query = em.createQuery("DELETE FROM Issue i WHERE i.id = :id");
        query.setParameter("id", id)
                .executeUpdate();
    }

    public Optional<Issue> get(Long id) {
        Issue found = em.find(Issue.class, id);
        return found != null ? Optional.of(found) : Optional.empty();
    }

}
