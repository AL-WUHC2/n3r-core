package org.n3r.core.jmockit;

import mockit.*;

import org.junit.*;

public class VerifyInternalMethodsTest {

    static class User {

        String name;
        int age;

        public void setName(String name) {
            this.name = name;
        }

        public void setAge(int age) {
            this.age = age;
        }

    }

    static class UserService {

        void populateUser() {
            User user = new User();
            user.setName("fred");
            user.setAge(31);
        }
    }

    @Mocked
    User user;

    @Test
    public void verifyInternalMethods() {
        new UserService().populateUser();
        new FullVerificationsInOrder() {
            {
                User user = new User();
                user.setName("fred");
                user.setAge(withAny(1));
            }
        };

    }

}