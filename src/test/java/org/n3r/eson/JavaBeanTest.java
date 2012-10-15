package org.n3r.eson;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import static org.junit.Assert.*;

public class JavaBeanTest {

    @Test
    public void f_test_toJSON() throws Exception {
        User user = new User();
        user.setName("校长");
        user.setAge(3);
        user.setSalary(new BigDecimal("123456789.0123"));

        String jsonString = new Eson().toString(user);

        User user1 = new Eson().parse(jsonString, User.class);

        assertEquals(user.getAge(), user1.getAge());
        assertEquals(user.getName(), user1.getName());
        assertEquals(user.getSalary(), user1.getSalary());
    }

    @Test
    public void test_toJSON_List() throws Exception {
        User user = new User();
        user.setName("校长");
        user.setAge(3);
        user.setSalary(new BigDecimal("123456789.0123"));
        user.setBirthdate(new Date());
        user.setOld(true);

        List<User> userList = new ArrayList<User>();
        userList.add(user);

        String jsonString = new Eson().toString(userList);

        List<User> userList1 = new Eson().parse(jsonString, User.class);

        User user1 = userList1.get(0);

        assertEquals(user.getAge(), user1.getAge());
        assertEquals(user.getName(), user1.getName());
        assertEquals(user.getSalary(), user1.getSalary());
        assertEquals(user.getBirthdate(), user1.getBirthdate());
        assertEquals(user.isOld(), user1.isOld());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void f_testComposite() throws Exception {
        Group group = new Group();
        group.setName("神棍");

        User user = new User();
        user.setName("校长");
        user.setAge(3);
        user.setSalary(new BigDecimal("123456789.0123"));

        group.getUsers().add(user);
        ((List<User>) group.getUsers2()).add(user);

        String jsonString = new Eson().toString(group);

        Group group1 = new Eson().parse(jsonString, Group.class);
        Assert.assertEquals(group.getName(), group1.getName());

        User user1 = group1.getUsers().get(0);
        Assert.assertEquals(user.getAge(), user1.getAge());
        Assert.assertEquals(user.getName(), user1.getName());
        Assert.assertEquals(user.getSalary(), user1.getSalary());
    }

    public static class User {

        private String name;
        private int age;
        private BigDecimal salary;
        private Date birthdate;
        private boolean old;

        public boolean isOld() {
            return old;
        }

        public void setOld(boolean old) {
            this.old = old;
        }

        public Date getBirthdate() {
            return birthdate;
        }

        public void setBirthdate(Date birthdate) {
            this.birthdate = birthdate;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public BigDecimal getSalary() {
            return salary;
        }

        public void setSalary(BigDecimal salary) {
            this.salary = salary;
        }

    }

    public static class Group {

        private List<User> users = new ArrayList<User>();
        private List<? extends User> users2 = new ArrayList<User>();

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<User> getUsers() {
            return users;
        }

        public void setUsers(List<User> users) {
            this.users = users;
        }

        public List<? extends User> getUsers2() {
            return users2;
        }

        public void setUsers2(List<? extends User> users2) {
            this.users2 = users2;
        }

    }
}
