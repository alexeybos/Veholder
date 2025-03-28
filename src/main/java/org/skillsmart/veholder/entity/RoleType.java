package org.skillsmart.veholder.entity;

public enum RoleType implements Role {
    USER, ADMIN;

    @Override
    public boolean includes(Role role) {
        return true;
    }


}
