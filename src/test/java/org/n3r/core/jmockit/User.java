package org.n3r.core.jmockit;

public class User {
    String name;

    private User() { this.name = "joe"; }
    
    public static User getInstance() {
        return new User();
    }

    String name() { return name; }
}
