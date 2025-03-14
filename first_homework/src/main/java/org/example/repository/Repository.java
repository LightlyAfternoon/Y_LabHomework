package org.example.repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface Repository<E> {
    /**
     * Method adds a given entity in database
     * @param entity entity to add in database
     */
    E add(E entity);

    /**
     * Method searches and returns an entity with given UUID from database
     * @param uuid UUID with which the method should find the entity from database
     */
    E findById(UUID uuid);

    /**
     * Method searches and returns a List with all entities from database
     */
    List<E> findAll();

    /**
     * Method update a given entity in database
     * @param entity entity to update in database
     */
    void update(E entity);

    /**
     * Method deleted a given entity from database
     * @param entity entity to delete from database
     */
    boolean delete(E entity);
}