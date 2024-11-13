package grpctesting;

import org.junit.runner.JUnitCore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TestApplication implements CommandLineRunner {

    public static void main(String[] args) {
        JUnitCore.runClasses(RunCucumberTest.class);
    }

    @Override
    public void run(String... args) throws Exception {
        // TODO Auto-generated method stub
    }

}
