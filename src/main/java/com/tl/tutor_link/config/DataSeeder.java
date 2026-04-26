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

        createDemoTutor(
                "Emily",
                "Chen",
                "emily.chen.demo@tutorlink.com",
                "emilychen",
                "Math Methods, Specialist Maths, Physics",
                "St Lucia QLD, Australia",
                -27.4975,
                153.0137,
                true,
                55,
                "UQ engineering student helping high school and first-year university students build confidence in maths and physics."
        );

        createDemoTutor(
                "James",
                "Wilson",
                "james.wilson.demo@tutorlink.com",
                "jameswilson",
                "Computer Science, Java, React",
                "Brisbane City QLD, Australia",
                -27.4705,
                153.0260,
                true,
                65,
                "Software engineering graduate tutoring programming, web development, and computer science fundamentals."
        );

        createDemoTutor(
                "Sophie",
                "Taylor",
                "sophie.taylor.demo@tutorlink.com",
                "sophietaylor",
                "English, Essay Writing, Literature",
                "Indooroopilly QLD, Australia",
                -27.4990,
                152.9730,
                false,
                45,
                "Experienced English tutor focused on essay structure, exam preparation, and clear academic writing."
        );

        createDemoTutor(
                "Daniel",
                "Nguyen",
                "daniel.nguyen.demo@tutorlink.com",
                "danielnguyen",
                "Chemistry, Biology, Science",
                "Toowong QLD, Australia",
                -27.4850,
                152.9920,
                false,
                50,
                "Science tutor helping students understand difficult concepts through simple explanations and practice questions."
        );

        createDemoTutor(
                "Olivia",
                "Brown",
                "olivia.brown.demo@tutorlink.com",
                "oliviabrown",
                "Primary Maths, English, NAPLAN",
                "South Brisbane QLD, Australia",
                -27.4810,
                153.0220,
                true,
                40,
                "Friendly tutor for primary school students, specialising in foundational maths, reading, and NAPLAN preparation."
        );

        createDemoTutor(
                "Aiden",
                "Patel",
                "aiden.patel.demo@tutorlink.com",
                "aidenpatel",
                "Accounting, Economics, Business",
                "Springfield QLD, Australia",
                -27.6810,
                152.9030,
                true,
                60,
                "Business and economics tutor helping senior students and university students prepare for assessments."
        );

        createDemoTutor(
                "Mia",
                "Roberts",
                "mia.roberts.demo@tutorlink.com",
                "miaroberts",
                "Spanish, French, ESL",
                "West End QLD, Australia",
                -27.4815,
                153.0090,
                true,
                50,
                "Language tutor focused on conversation practice, grammar, pronunciation, and exam preparation."
        );

        createDemoTutor(
                "Noah",
                "Kim",
                "noah.kim.demo@tutorlink.com",
                "noahkim",
                "Guitar, Music Theory",
                "New Farm QLD, Australia",
                -27.4670,
                153.0450,
                false,
                45,
                "Relaxed guitar tutor teaching beginners and intermediate players practical songs, technique, and music theory."
        );

        createDemoTutor(
                "Grace",
                "Martin",
                "grace.martin.demo@tutorlink.com",
                "gracemartin",
                "Psychology, Statistics, Research Methods",
                "Kelvin Grove QLD, Australia",
                -27.4480,
                153.0130,
                true,
                58,
                "Psychology tutor helping students with statistics, report writing, and research design."
        );

        createDemoTutor(
                "Liam",
                "Anderson",
                "liam.anderson.demo@tutorlink.com",
                "liamanderson",
                "Maths, Physics, Engineering",
                "Sunnybank QLD, Australia",
                -27.5790,
                153.0600,
                false,
                52,
                "Engineering student tutoring maths and physics with a focus on problem-solving and exam technique."
        );
    }

    private void createDemoTutor(
            String firstname,
            String lastname,
            String email,
            String username,
            String subjects,
            String location,
            Double latitude,
            Double longitude,
            boolean remote,
            Integer hourlyRate,
            String bio
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
        tutor.setProfileImageKey(null);

        tutorRepository.save(tutor);
    }
}