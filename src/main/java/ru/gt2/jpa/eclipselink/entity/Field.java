package ru.gt2.jpa.eclipselink.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@NamedQuery(name = Field.UNSET_POSITION_ALL,
        query = "UPDATE Field f SET f.position = NULL WHERE f.position IS NOT NULL")
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "UK_FIELD_POSITION", columnNames = { "position" })
})
public class Field {
    public static final String UNSET_POSITION_ALL = "Field.UNSET_POSITION_ALL";

    @Id
    private Integer id;

    private Integer position;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
