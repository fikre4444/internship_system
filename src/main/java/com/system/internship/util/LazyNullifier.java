package com.system.internship.util;

import jakarta.persistence.*;
import org.hibernate.Hibernate;
import java.lang.reflect.Field;

public class LazyNullifier {

  @PersistenceContext
  private EntityManager entityManager;

  // This method nullifies uninitialized lazy-loaded fields
  public static <T> T nullifyLazyFields(T entity) throws IllegalAccessException {
    Field[] fields = entity.getClass().getDeclaredFields();

    for (Field field : fields) {
      // Check if the field is a JPA relationship
      if (field.isAnnotationPresent(ManyToOne.class) ||
          field.isAnnotationPresent(OneToOne.class) ||
          field.isAnnotationPresent(OneToMany.class) ||
          field.isAnnotationPresent(ManyToMany.class)) {

        field.setAccessible(true); // Make the private field accessible

        Object fieldValue = field.get(entity); // Get the field value
        if (!Hibernate.isInitialized(fieldValue)) {
          field.set(entity, null); // Set it to null if not initialized
        }
      }
    }

    return entity;
  }
}
