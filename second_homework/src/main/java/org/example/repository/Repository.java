package org.example.repository;

import liquibase.exception.LiquibaseException;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public interface Repository<E> {
    /**
     * Method adds a given entity in database
     * @param entity entity to add in database
     */
    E add(E entity) throws SQLException, LiquibaseException;

    /**
     * Method searches and returns an entity with given UUID from database
     * @param id UUID with which the method should find the entity from database
     */
    E findById(int id) throws SQLException, LiquibaseException;

    /**
     * Method searches and returns a List with all entities from database
     */
    List<E> findAll() throws SQLException, LiquibaseException;

    /**
     * Method update a given entity in database
     * @param entity entity to update in database
     */
    void update(E entity) throws SQLException, LiquibaseException;

    /**
     * Method deleted a given entity from database
     * @param entity entity to delete from database
     */
    boolean delete(E entity) throws SQLException, LiquibaseException;
}