package app.daos;

import jakarta.persistence.*;

import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractDAO<T> implements IDAO<T> {

    protected final EntityManagerFactory emf;
    private final Class<T> entityClass;

    protected AbstractDAO(Class<T> entityClass, EntityManagerFactory emf) {
        this.entityClass = entityClass;
        this.emf = emf;
    }

    @Override
    public T create(T t) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(t);
            em.getTransaction().commit();
            return t;
        } catch (RollbackException e) {
            throw new RollbackException(String.format("Failed to create %s: %s", entityClass.getSimpleName(), e.getMessage()), e);
        }
    }

    @Override
    public T getById(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            T t = em.find(entityClass, id);

            if (t == null) {
                throw new EntityNotFoundException(String.format("%s with id %s could not be found.", entityClass.getSimpleName(), id));
            }

            return t;
        } catch (RollbackException e) {
            throw new RollbackException(String.format("Failed to get %s: %s", entityClass.getSimpleName(), e.getMessage()), e);
        }
    }

    @Override
    public Set<T> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<T> query = em.createQuery(String.format("SELECT e FROM %s e", entityClass.getSimpleName()), entityClass);
            return query.getResultStream().collect(Collectors.toSet());
        } catch (RollbackException e) {
            throw new RollbackException(String.format("Failed to get all %s(s): %s", entityClass.getSimpleName(), e.getMessage()), e);
        }
    }

    @Override
    public T update(T t) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.merge(t);
            em.getTransaction().commit();
            return t;
        } catch (RollbackException e) {
            throw new RollbackException(String.format("Failed to update %s: %s", entityClass.getSimpleName(), e.getMessage()), e);
        }
    }

    @Override
    public void delete(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            T t = em.find(entityClass, id);

            if (t == null) {
                throw new EntityNotFoundException(String.format("%s with id %s could not be found.", entityClass.getSimpleName(), id));
            }

            em.getTransaction().begin();
            em.remove(t);
            em.getTransaction().commit();
        } catch (RollbackException e) {
            throw new RollbackException(String.format("Failed to delete %s: %s", entityClass.getSimpleName(), e.getMessage()), e);
        }
    }
}
