package com.tl.tutor_link.config;

import com.tl.tutor_link.tutor.model.Tutor;
import com.tl.tutor_link.tutor.repository.TutorRepository;
import com.tl.tutor_link.user.model.Role;
import com.tl.tutor_link.user.model.User;
import com.tl.tutor_link.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TutorRepository tutorRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(
            UserRepository userRepository,
            TutorRepository tutorRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.tutorRepository = tutorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!tutorRepository.findAll().isEmpty()) {
            return;
        }

        createDemoTutor("Bill", "Nye", "bill.nye.demo@tutorlink.com", "billnye",
                "Physics, Chemistry, Science Communication",
                "St Lucia QLD, Australia", -27.4975, 153.0137,
                true, 75,
                "Makes science feel less like homework and more like a controlled explosion. Strong believer that every problem can be solved with curiosity, evidence, and a bow tie.",
                "profile-images/bill-nye.jpg");

        createDemoTutor("Neil", "deGrasse Tyson", "neil.tyson.demo@tutorlink.com", "neiltyson",
                "Astronomy, Physics, Space Science",
                "South Brisbane QLD, Australia", -27.4810, 153.0220,
                true, 90,
                "Will help you understand the cosmos and also remind you your assignment deadline is still very real.",
                "profile-images/neil-tyson.jpg");

        createDemoTutor("Gordon", "Ramsay", "gordon.ramsay.demo@tutorlink.com", "gordonramsay",
                "Hospitality, Cooking, Food Technology",
                "Fortitude Valley QLD, Australia", -27.4570, 153.0340,
                false, 85,
                "Your work is RAW until proven otherwise. Expect brutally honest feedback and significantly improved results.",
                "profile-images/gordon-ramsay.png");

        createDemoTutor("David", "Attenborough", "david.attenborough.demo@tutorlink.com", "davidattenborough",
                "Biology, Environmental Science, Geography",
                "New Farm QLD, Australia", -27.4670, 153.0450,
                true, 70,
                "Calmly explains biology so well you might accidentally enjoy studying. Narration voice not guaranteed but highly likely.",
                "profile-images/david-att.jpg");

        createDemoTutor("Marie", "Curie", "marie.curie.demo@tutorlink.com", "mariecurie",
                "Chemistry, Physics, Research Methods",
                "Toowong QLD, Australia", -27.4850, 152.9920,
                false, 80,
                "Serious about chemistry, precision, and not guessing answers. Helps students shine without the radiation.",
                "profile-images/marie-curie.jpg");

        createDemoTutor("Albert", "Einstein", "albert.einstein.demo@tutorlink.com", "alberteinstein",
                "Maths, Physics, Problem Solving",
                "Indooroopilly QLD, Australia", -27.4990, 152.9730,
                true, 95,
                "Breaks down complex problems simply. Hair may be chaotic, explanations are not.",
                "profile-images/albert-einstein.jpg");

        createDemoTutor("Taylor", "Swift", "taylor.swift.demo@tutorlink.com", "taylorswift",
                "English, Writing, Creative Expression",
                "West End QLD, Australia", -27.4815, 153.0090,
                true, 88,
                "Turns essays into compelling narratives. Known for helping students rewrite their worst drafts into something impressive.",
                "profile-images/taylor-swift.png");

        createDemoTutor("Elon", "Musk", "elon.musk.demo@tutorlink.com", "elonmusk",
                "Engineering, Startups, Programming",
                "Brisbane City QLD, Australia", -27.4705, 153.0260,
                false, 100,
                "Will question your entire project scope and then help you rebuild it faster. May suggest adding rockets.",
                "profile-images/elon-musk.webp");

        createDemoTutor("Steve", "Irwin", "steve.irwin.demo@tutorlink.com", "steveirwin",
                "Biology, Wildlife Conservation, Outdoor Education",
                "Woolloongabba QLD, Australia", -27.4930, 153.0360,
                false, 65,
                "Crikey! Makes biology exciting and easy to understand. High energy, great explanations.",
                "profile-images/steve-irwin.jpg");

        createDemoTutor("Ada", "Lovelace", "ada.lovelace.demo@tutorlink.com", "adalovelace",
                "Computer Science, Algorithms, Programming",
                "Kelvin Grove QLD, Australia", -27.4480, 153.0130,
                true, 78,
                "Explains algorithms clearly and elegantly. Great for students struggling with programming logic.",
                "profile-images/ada-lovelace.png");
    }

    private void createDemoTutor(
            String firstname, String lastname, String email, String username,
            String subjects, String location, Double latitude, Double longitude,
            boolean remote, Integer hourlyRate, String bio, String profileImageKey
    ) {
        User user = new User();
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("Password123!"));
        user.setEnabled(true);
        user.setRoles(Set.of(Role.STUDENT, Role.TUTOR));

        User savedUser = userRepository.save(user);

        Tutor tutor = new Tutor();
        tutor.setUser(savedUser);
        tutor.setSubjects(subjects);
        tutor.setLocation(location);
        tutor.setLatitude(latitude);
        tutor.setLongitude(longitude);
        tutor.setRemote(remote);
        tutor.setHourlyRate(hourlyRate);
        tutor.setBio(bio);
        tutor.setProfileImageKey(profileImageKey);

        tutorRepository.save(tutor);
    }
}