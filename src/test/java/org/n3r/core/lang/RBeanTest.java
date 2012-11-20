package org.n3r.core.lang;

import org.junit.Test;

public class RBeanTest {

    public static class Person extends RBaseBean {
        private String name;

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class PersonWithAge extends Person {
        private int age;

        public void setAge(int age) {
            this.age = age;
        }

        public int getAge() {
            return age;
        }
    }

    public static class PersonWithId extends Person {
        private String id;

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    @Test
    public void testBean() {
        PersonWithAge personWithAge = new PersonWithAge();
        personWithAge.setName("John");
        personWithAge.setAge(12);

        PersonWithId personWithId = RBean.beanToAnotherBean(personWithAge, PersonWithId.class);
        System.out.println(personWithId.getName());
        System.out.println(personWithId.getId());
    }

}
