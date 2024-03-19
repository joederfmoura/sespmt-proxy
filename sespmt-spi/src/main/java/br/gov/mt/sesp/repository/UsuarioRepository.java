package br.gov.mt.sesp.repository;

import br.gov.mt.sesp.model.User;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioRepository extends BaseRepository<User> {

    private static final String EMAIL = "email";
    private static final String NAME = "name";
    private static final String PASSWORD = "password";

    public UsuarioRepository(EntityManager em) {
        super(em);
    }

    public List<User> findAll(String filter) {
        CriteriaBuilder cb = getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> root = query.from(User.class);

        Predicate predicate = cb.and();

        predicate = cb.or(predicate, cb.like(cb.upper(root.<String>get(EMAIL)), "%" + filter.toUpperCase() + "%"));

        query.where(predicate);

        return Optional.ofNullable(em.createQuery(query).getResultList()).orElse(new ArrayList<User>());
    }

    public List<User> findAll(String filter, int firstResult, int maxResults) {
        CriteriaBuilder cb = getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = cb.createQuery(User.class);
        Root<User> root = criteriaQuery.from(User.class);

        Predicate predicate = cb.like(cb.upper(root.<String>get(EMAIL)), "%" + filter.toUpperCase() + "%");
        predicate = cb.or(predicate, cb.like(cb.upper(root.<String>get(NAME)), "%" + filter.toUpperCase() + "%"));

        criteriaQuery.where(predicate);

        TypedQuery<User> query = em.createQuery(criteriaQuery);

        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);

        return Optional.ofNullable(query.getResultList()).orElse(new ArrayList<User>());
    }

    public User findByEmail(String email) {
        CriteriaBuilder cb = getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> root = query.from(User.class);

        query.where(cb.equal(root.get(EMAIL), email));

        List<User> users = em.createQuery(query).getResultList();

        if (users == null || users.isEmpty()) {
            return null;
        }

        return users.get(0);
    }

    public User findByUsername(String username) {
       return findByEmail(username);
    }    

    public int count() {
        Criteria criteria = getSession().createCriteria(User.class);

        return ((Number) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
    }

    public boolean updatePassword(String username, String password) {
        CriteriaBuilder cb = getCriteriaBuilder();
        CriteriaUpdate<User> criteriaQuery = cb.createCriteriaUpdate(User.class);
        Root<User> root = criteriaQuery.from(User.class);

        criteriaQuery.set(root.get(PASSWORD), password);
        criteriaQuery.where(cb.equal(root.get(EMAIL), username));

        Query query = em.createQuery(criteriaQuery);

        return query.executeUpdate() > 0;
    }
}
