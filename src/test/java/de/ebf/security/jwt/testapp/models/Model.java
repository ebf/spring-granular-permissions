package de.ebf.security.jwt.testapp.models;

import javax.persistence.Entity;
import javax.persistence.Id;

import de.ebf.security.annotations.PermissionModel;
import de.ebf.security.annotations.PermissionNameField;

@Entity
@PermissionModel
public class Model {

    @Id
    @PermissionNameField
    private String name;

    private int wrongTypeField;

}
