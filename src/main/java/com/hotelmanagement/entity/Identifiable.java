package com.hotelmanagement.entity;

/** Every entity has an Integer primary key. This lets BaseDAO work with all of them. */
public interface Identifiable {
    Integer getId();
    void setId(Integer id);
}
