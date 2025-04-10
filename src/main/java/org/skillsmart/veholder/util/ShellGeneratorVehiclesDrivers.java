package org.skillsmart.veholder.util;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class ShellGeneratorVehiclesDrivers {

    @ShellMethod(key = "hello", value = "Say hello")
    public String hello() {
        return "Hello, world!";
    }

    @ShellMethod(key = "add", value = "Add two integers together")
    public int add(int a, int b) {
        return a + b;
    }
}
