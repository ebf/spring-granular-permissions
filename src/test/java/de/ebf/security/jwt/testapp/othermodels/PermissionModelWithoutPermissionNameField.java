package de.ebf.security.jwt.testapp.othermodels;

import javax.persistence.Entity;
import javax.persistence.Id;

import de.ebf.security.annotations.PermissionModel;

@PermissionModel
@Entity
public class PermissionModelWithoutPermissionNameField {

    @Id
    private String name;
}
